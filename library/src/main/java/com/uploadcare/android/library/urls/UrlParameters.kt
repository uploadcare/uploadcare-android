package com.uploadcare.android.library.urls

import com.uploadcare.android.library.api.RequestHelper
import java.util.*

interface UrlParameter {
    fun getParam(): String

    fun getValue(): String?
}

class FilesFromParameter(private val from: Date) : UrlParameter {
    override fun getParam() = "from"

    override fun getValue() = RequestHelper.iso8601(from)
}

class FilesLimitParameter(private val limit: Int) : UrlParameter {
    override fun getParam() = "limit"

    override fun getValue() = Integer.toString(limit)
}

class FilesOrderParameter(private val order: Order) : UrlParameter {
    override fun getParam() = "ordering"

    override fun getValue() = order.rawValue
}

class FilesRemovedParameter(private val removed: Boolean) : UrlParameter {
    override fun getParam() = "removed"

    override fun getValue() = if (removed) "true" else "false"
}

class FilesStoredParameter(private val stored: Boolean) : UrlParameter {
    override fun getParam() = "stored"

    override fun getValue() = if (stored) "true" else "false"
}

class PublicKeyParameter(private val publicKey: String) : UrlParameter {
    override fun getParam() = "pub_key"

    override fun getValue() = publicKey
}

class FileIdParameter(private val fileId: String) : UrlParameter {
    override fun getParam() = "file_id"

    override fun getValue() = fileId
}

class AddFieldsParameter(private val fields: String):UrlParameter{
    override fun getParam() = "add_fields"

    override fun getValue() = fields
}

enum class Order constructor(val rawValue: String) {
    UPLOAD_TIME_ASC("datetime_uploaded"),
    UPLOAD_TIME_DESC("-datetime_uploaded"),
    SIZE_ASC("size"),
    SIZE_DESC("-size")
}