package com.uploadcare.android.widget.viewmodels

import android.app.Application
import android.os.Bundle
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

    val loading = MutableLiveData<Boolean>().apply { value = false }
    val loadingMore = MutableLiveData<Boolean>().apply { value = false }
    val isEmpty = MutableLiveData<Boolean>().apply { value = false }
    val isSearch = MutableLiveData<Boolean>().apply { value = false }

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

        isSearch.value = false
        currentChunk = position
        getChunkData()
    }

    private fun getChunkData(loadMore: Boolean = false, query: String? = null) {
        isEmpty.value = false
        allowLoadMore.postValue(false)

        if (!loadMore) {
            things.postValue(null)
            loading.value = true
        } else {
            loadingMore.value = true
        }

        val stringBuilder = StringBuilder()
        if (query != null) {
            stringBuilder.append(socialSource?.rootChunks?.get(currentChunk)?.pathChunk)
                    .append("/")
            stringBuilder.append("-").append("/").append(query)
        } else if (isSearch.value == true && loadMore) {
            stringBuilder.append(chunks[currentChunk].pathChunk)
        } else if (isRoot) {
            stringBuilder.append(chunks[currentChunk].pathChunk)
        } else {
            stringBuilder.append(socialSource?.rootChunks?.get(currentChunk)?.pathChunk)
                    .append("/")
            for (i in chunks.indices) {
                if (i != chunks.size - 1) {
                    stringBuilder.append(chunks[i].pathChunk).append("/")
                } else {
                    stringBuilder.append(chunks[i].pathChunk)
                }
            }
        }

        UploadcareWidget.getInstance().socialApi
                .getSourceChunk(socialSource?.getCookie(getApplication()) ?: "",
                        socialSource?.urls?.sourceBase ?: "",
                        stringBuilder.toString(),
                        if (loadMore) getNext() else "")
                .enqueue(object : Callback<ChunkResponse> {
                    override fun onFailure(call: Call<ChunkResponse>, t: Throwable) {
                        loadingMore.value = false
                        loading.value = false
                        isEmpty.value = (things.value?.isEmpty() == true)
                        errorCommand.postValue(UploadcareApiException(t))
                    }

                    override fun onResponse(call: Call<ChunkResponse>,
                                            response: Response<ChunkResponse>) {
                        loadingMore.value = false
                        loading.value = false
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

                        isSearch.value = response.body()?.searchPath != null
                        isEmpty.value = (things.value?.isEmpty() == true)
                    }
                })
    }

    private fun getNext(): String {
        val stringBuilder = StringBuilder()

        nextPath?.chunks?.let {
            for (i in it.indices) {
                if (i != it.size - 1) {
                    stringBuilder.append(it[i].pathChunk).append("/")
                } else {
                    stringBuilder.append(it[i].pathChunk)
                }
            }
        }

        return stringBuilder.toString()
    }
}
