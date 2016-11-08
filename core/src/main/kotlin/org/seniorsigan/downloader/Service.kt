package org.seniorsigan.downloader

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Service(
    private val network: NetworkService = NetworkService()
) {
    fun getWall(id: String): List<Item> {
        val url = "https://api.vk.com/method/wall.getById?posts=$id&extended=0&copy_history_depth=1"
        val rawResponse = network.get(url, WallResponse::class.java)
        println(rawResponse)
        return rawResponse.response
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
        }.flatten().requireNoNulls().filter { it.url.isNotBlank() }
    }

    fun selectPhoto(items: List<Item>): String? {
        return items.map {
            it.attachments.filter {
                it.type == "photo" && it.photo != null
            }.map {
                it.photo?.photoUrl()
            }.firstOrNull()
        }.firstOrNull()
    }

    fun saveAudioToZip(id: String, os: OutputStream): Collection {
        if (id.isBlank()) throw Exception("post id can't be empty")
        val zip = ZipService()
        println("Start loading audios")
        val posts = getWall(id)
        val audios = selectAudio(posts)
        val coverUrl = selectPhoto(posts)
        zip.pack(audios.map { RemoteFile(it.url, it.name(), network.getInputStream(it.url)) }, os)
        return Collection(audios, coverUrl, "https://vk.com/wall$id")
    }
}
