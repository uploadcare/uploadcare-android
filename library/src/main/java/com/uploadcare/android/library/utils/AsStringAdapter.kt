package com.uploadcare.android.library.utils

import com.squareup.moshi.*
import com.squareup.moshi.JsonAdapter.Factory
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class AsString

class AsStringAdapter : JsonAdapter<String>() {

    companion object {

        var FACTORY: Factory = Factory { type, annotations, moshi ->
            val nextAnnotations = Types.nextAnnotations(annotations, AsString::class.java)
            if (nextAnnotations == null || nextAnnotations.isNotEmpty())
                null else {
                AsStringAdapter()
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: String?) {
        value?.let { writer.value(Buffer().writeUtf8(value.toString())) }
    }

    override fun fromJson(reader: JsonReader): String? {
        // Here we're expecting the JSON object, it is processed as Map<String, Any> by Moshi
        return (reader.readJsonValue() as? Map<*, *>)?.let { data ->
            try {
                JSONObject(data)
            } catch (e: JSONException) {
                // Handle error if arises
            }
        }?.toString()
    }
}
