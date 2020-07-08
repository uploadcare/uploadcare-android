package com.uploadcare.android.library.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uploadcare.android.library.utils.AsStringAdapter

import com.uploadcare.android.library.utils.MoshiAdapter
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass

class ObjectMapper(val moshi: Moshi) {

    fun <T : Any> fromJson(json: String, classOfT: Class<T>): T? {
        val jsonAdapter: JsonAdapter<T> = moshi.adapter(classOfT)
        return jsonAdapter.fromJson(json)
    }

    fun <T : Any> fromJson(json: String, typeOfT: Type): T? {
        val jsonAdapter: JsonAdapter<T> = moshi.adapter(typeOfT)
        return jsonAdapter.fromJson(json)
    }

    fun <T : Any> toJson(src: T?, classOfT: KClass<T>): String {
        val jsonAdapter: JsonAdapter<T> = moshi.adapter(classOfT.java)
        return jsonAdapter.toJson(src)
    }

    fun toJson(src: Any?, typeOfSrc: Type): String {
        val jsonAdapter: JsonAdapter<Any> = moshi.adapter(typeOfSrc)
        return jsonAdapter.toJson(src)
    }

    companion object {
        fun build(): ObjectMapper {
            return ObjectMapper(Moshi.Builder()
                    .add(AsStringAdapter.FACTORY)
                    .add(KotlinJsonAdapterFactory())
                    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())//rfc2822
                    .add(MoshiAdapter())
                    .build())
        }
    }
}