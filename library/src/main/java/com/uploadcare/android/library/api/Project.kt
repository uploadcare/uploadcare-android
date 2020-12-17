package com.uploadcare.android.library.api

import com.squareup.moshi.Json

/**
 * The resource for project, associated with the connecting account.
 */
@Suppress("unused")
data class Project(val name: String,
                   @Json(name = "pub_key") val pubKey: String,
                   val collaborators: List<Collaborator>,
                   @Json(name = "autostore_enabled") val autostoreEnabled: Boolean = false) {

    fun getOwner(): Collaborator? = if (collaborators.isNotEmpty()) {
        collaborators[0]
    } else {
        null
    }
}

data class Collaborator(val name: String,
                        val email: String)