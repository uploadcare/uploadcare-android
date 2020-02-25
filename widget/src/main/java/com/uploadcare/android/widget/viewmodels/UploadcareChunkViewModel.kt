package com.uploadcare.android.widget.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.data.*
import com.uploadcare.android.widget.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadcareChunkViewModel(application: Application) : AndroidViewModel(application) {

    val things = MutableLiveData<List<Thing>>()
    val allowLoadMore = MutableLiveData<Boolean>().apply { value = false }
    val errorCommand = SingleLiveEvent<UploadcareApiException?>()
    val needAuthCommand = SingleLiveEvent<String>()

    val loading = ObservableBoolean(false)
    val loadingMore = ObservableBoolean(false)
    val isEmpty = ObservableBoolean(false)
    val isSearch = ObservableBoolean(false)

    private var socialSource: SocialSource? = null
    private var chunks = listOf<Chunk>()
    var title: String? = null
    private var isRoot = false
    private var currentChunk = 0

    private var nextPath: Path? = null

    fun start(arguments: Bundle) {
        currentChunk = arguments.getInt("currentChunk", 0)
        socialSource = arguments.getParcelable("socialSource")
        chunks = arguments.getParcelableArrayList<Chunk>("chunks")?.toList() ?: listOf()
        title = arguments.getString("title")
        isRoot = arguments.getBoolean("isRoot", false)

        getChunkData()
    }

    fun search(query: String?) {
        getChunkData(query = query)
    }

    fun loadMore() {
        if (nextPath != null) {
            getChunkData(true)
        }
    }

    fun changeChunk(position: Int) {
        if (position == currentChunk) {
            return
        }

        isSearch.set(false)
        currentChunk = position
        getChunkData()
    }

    private fun getChunkData(loadMore: Boolean = false, query: String? = null) {
        isEmpty.set(false)
        allowLoadMore.postValue(false)

        if (!loadMore) {
            things.postValue(null)
            loading.set(true)
        } else {
            loadingMore.set(true)
        }

        val stringBuilder = StringBuilder()
        if (query != null) {
            stringBuilder.append(socialSource?.rootChunks?.get(currentChunk)?.pathChunk)
                    .append("/")
            stringBuilder.append("-").append("/").append(query)
        } else if (isSearch.get() && loadMore) {
            stringBuilder.append(chunks.get(currentChunk).pathChunk)
        } else if (isRoot) {
            stringBuilder.append(chunks.get(currentChunk).pathChunk)
        } else {
            stringBuilder.append(socialSource?.rootChunks?.get(currentChunk)?.pathChunk)
                    .append("/")
            for (i in chunks.indices) {
                if (i != chunks.size - 1) {
                    stringBuilder.append(chunks.get(i).pathChunk).append("/")
                } else {
                    stringBuilder.append(chunks.get(i).pathChunk)
                }
            }
        }

        UploadcareWidget.getInstance(getApplication()).socialApi
                .getSourceChunk(socialSource?.getCookie(getApplication()) ?: "",
                        socialSource?.urls?.sourceBase ?: "",
                        stringBuilder.toString(),
                        if (loadMore) getNext() else "")
                .enqueue(object : Callback<ChunkResponse> {
                    override fun onFailure(call: Call<ChunkResponse>, t: Throwable) {
                        loadingMore.set(false)
                        loading.set(false)
                        isEmpty.set(things.value?.isEmpty() == true)
                        errorCommand.postValue(UploadcareApiException(t))
                    }

                    override fun onResponse(call: Call<ChunkResponse>,
                                            response: Response<ChunkResponse>) {
                        loadingMore.set(false)
                        loading.set(false)
                        if (response.body()?.error != null) {
                            response.body()?.loginLink?.let {
                                needAuthCommand.postValue(it)
                            }
                        } else {
                            nextPath = response.body()?.nextPage
                            if (!loadMore) {
                                things.value = response.body()?.things
                            } else {
                                response.body()?.things?.let { new ->
                                    if (things.value != null) {
                                        things.value = things.value?.plus(new)
                                    } else {
                                        things.value = new
                                    }
                                }
                            }
                            nextPath?.let { allowLoadMore.postValue(true) }
                        }

                        if (response.body()?.searchPath != null) {
                            isSearch.set(true)
                        } else {
                            isSearch.set(false)
                        }

                        isEmpty.set(things.value?.isEmpty() == true)
                    }
                })
    }

    private fun getNext(): String {
        val stringBuilder = StringBuilder()

        nextPath?.chunks?.let {
            for (i in it.indices) {
                if (i != it.size - 1) {
                    stringBuilder.append(it.get(i).pathChunk).append("/")
                } else {
                    stringBuilder.append(it.get(i).pathChunk)
                }
            }
        }

        return stringBuilder.toString()
    }
}
