package org.seniorsigan.downloader

import java.io.InputStream

data class Collection(
    val audios: List<Audio>,
    val coverUrl: String?,
    val url: String
)

data class Audio(
    val url: String,
    val title: String,
    val artist: String
) {
    fun name() = "$artist - $title.mp3"
}

data class RemoteFile(
    val url: String,
    val name: String,
    val stream: InputStream
)
