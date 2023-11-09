package com.uploadcare.android.library.urls

import com.uploadcare.android.library.api.RequestHelper
import java.util.*

interface UrlParameter {
    fun getParam(): String

    fun getValue(): String?
}

@Suppress("unused")
class FilesFromParameter(private val fromDate: Date? = null, private val fromSize: Long? = null) : UrlParameter {

    constructor(fromDate: Date) : this(fromDate, null)

    constructor(fromSize: Long) : this(null, fromSize)

    override fun getParam() = "from"

    override fun getValue(): String = if (fromDate != null) {
        RequestHelper.iso8601(fromDate)
    } else {
        fromSize?.toString() ?: "0"
    }
}

@Suppress("unused")
class FilesLimitParameter(private val limit: Int) : UrlParameter {
    override fun getParam() = "limit"

    override fun getValue() = limit.toString()
}

@Suppress("unused")
class FilesOrderParameter(private val order: Order) : UrlParameter {
    override fun getParam() = "ordering"

    override fun getValue() = order.rawValue
}

@Suppress("unused")
class FilesRemovedParameter(private val removed: Boolean) : UrlParameter {
    override fun getParam() = "removed"

    override fun getValue() = if (removed) "true" else "false"
}

@Suppress("unused")
class FilesStoredParameter(private val stored: Boolean) : UrlParameter {
    override fun getParam() = "stored"

    override fun getValue() = if (stored) "true" else "false"
}

@Suppress("unused")
class PublicKeyParameter(private val publicKey: String) : UrlParameter {
    override fun getParam() = "pub_key"

    override fun getValue() = publicKey
}

@Suppress("unused")
class FileIdParameter(private val fileId: String) : UrlParameter {
    override fun getParam() = "file_id"

    override fun getValue() = fileId
}

@Suppress("unused")
class IncludeParameter(private val fields: String) : UrlParameter {
    override fun getParam() = "include"

    override fun getValue() = fields
}

@Suppress("unused")
enum class Order constructor(val rawValue: String) {
    UPLOAD_TIME_ASC("datetime_uploaded"),
    UPLOAD_TIME_DESC("-datetime_uploaded")
}

class RequestIdParameter(private val requestId: String) : UrlParameter {

    override fun getParam() = "request_id"

    override fun getValue() = requestId
}
