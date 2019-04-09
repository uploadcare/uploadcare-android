package com.uploadcare.android.library.api

/**
 * The resource for project, associated with the connecting account.
 */
data class Project(val name: String,
                   val pubKey: String,
                   val collaborators: List<Collaborator>) {

    fun getOwner(): Collaborator? = if (collaborators.isNotEmpty()) {
        collaborators[0]
    } else {
        null
    }
}

data class Collaborator(val name: String,
                        val email: String)