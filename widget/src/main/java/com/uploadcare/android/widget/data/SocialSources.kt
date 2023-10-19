package com.uploadcare.android.widget.data

fun getSocialSources(): List<SocialSource> = listOf(
    getFacebook(),
    getGoogleDrive(),
    getGooglePhotos(),
    getDropbox(),
    getInstagram(),
    getOneDrive()
)

private fun getFacebook(): SocialSource = SocialSource(
    rootChunks = listOf(
        Chunk(objectType = "", pathChunk = "me", title = "My Albums")
    ),
    name = "facebook",
    urls = Urls(
        sourceBase = "facebook/source",
        session = "facebook/session",
        done = "facebook/done"
    )
)

private fun getGoogleDrive(): SocialSource = SocialSource(
    rootChunks = listOf(
        Chunk(objectType = "", title = "My Files", pathChunk = "root"),
        Chunk(objectType = "", title = "Shared with Me", pathChunk = "shared"),
        Chunk(objectType = "", title = "Starred", pathChunk = "starred"),
        Chunk(objectType = "", title = "Team drives", pathChunk = "team_drives")
    ),
    name = "gdrive",
    urls = Urls(
        sourceBase = "gdrive/source",
        session = "gdrive/session",
        done = "gdrive/done"
    )
)

private fun getGooglePhotos(): SocialSource = SocialSource(
    rootChunks = listOf(
        Chunk(objectType = "", title = "Photos", pathChunk = "root"),
        Chunk(objectType = "", title = "Albums", pathChunk = "albums")
    ),
    name = "gphotos",
    urls = Urls(
        sourceBase = "gphotos/source",
        session = "gphotos/session",
        done = "gphotos/done"
    )
)

private fun getDropbox(): SocialSource = SocialSource(
    rootChunks = listOf(
        Chunk(objectType = "", title = "Files", pathChunk = "root"),
        Chunk(objectType = "", title = "Team files", pathChunk = "team")
    ),
    name = "dropbox",
    urls = Urls(
        sourceBase = "dropbox/source",
        session = "dropbox/session",
        done = "dropbox/done"
    )
)

private fun getInstagram(): SocialSource = SocialSource(
    rootChunks = listOf(
        Chunk(objectType = "", title = "My Photos", pathChunk = "my")
    ),
    name = "instagram",
    urls = Urls(
        sourceBase = "instagram/source",
        session = "instagram/session",
        done = "instagram/done"
    )
)

private fun getOneDrive(): SocialSource = SocialSource(
    rootChunks = listOf(
        Chunk(objectType = "", title = "My drives", pathChunk = "root_v2"),
        Chunk(objectType = "", title = "Shared with me", pathChunk = "shared_v2"),
        Chunk(objectType = "", title = "SharePoint", pathChunk = "sharepoint"),
        Chunk(objectType = "", title = "My groups", pathChunk = "groups")
    ),
    name = "onedrive",
    urls = Urls(
        sourceBase = "onedrive/source",
        session = "onedrive/session",
        done = "onedrive/done"
    )
)
