package org.seniorsigan.downloader

import java.io.OutputStream

class Service(
    private val network: NetworkService = NetworkService()
) {
    fun getWall(id: String): List<Item> {
        val url = "https://api.vk.com/method/wall.getById?posts=$id&extended=0&copy_history_depth=1"
        val json = network.getAs(url, WallResponse::class.java)
        println(json)
        return json?.response ?: emptyList()
    }

    fun selectAudio(items: List<Item>): List<Audio> {
        return items.map {
            it.attachments.filter {
                it.type == "audio" && it.audio != null
            }.map {
                with(it.audio) {
                    if (this != null) {
                        Audio(url, title, artist)
                    } else {
                        null
                    }
                }
            }
        }.flatten().requireNoNulls()
    }

    fun saveAudioToZip(id: String, os: OutputStream): List<Audio> {
        if (id.isBlank()) throw Exception("post id can't be empty")
        val zip = ZipService()
        println("Start loading audios")
        val posts = getWall(id)
        val audios = selectAudio(posts)
        zip.pack(audios.map { RemoteFile(it.url, it.name(), network.getInputStream(it.url)) }, os)
        return audios
    }
}
