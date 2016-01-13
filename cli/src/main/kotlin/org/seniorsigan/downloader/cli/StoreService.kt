package org.seniorsigan.downloader.cli

import org.seniorsigan.downloader.Audio
import org.seniorsigan.downloader.NetworkService
import org.seniorsigan.downloader.Service
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StoreService {
    private val network = NetworkService()
    private val service = Service(network)
    private val path = "/tmp/downloader/"

    fun saveAudios(audios: List<Audio>) {
        val executor = Executors.newFixedThreadPool(4)
        val doneSignal = CountDownLatch(audios.size)
        audios.forEach {
            executor.submit {
                try {
                    getAndSave(it.url, it.name())
                } catch(e: Exception) {
                    println("Error while downloading ${it.url}: ${e.message}")
                }
                doneSignal.countDown()
            }
        }
        doneSignal.await(audios.size * 3L, TimeUnit.MINUTES)
        executor.shutdown()
    }

    private fun getAndSave(url: String, name: String) {
        val path = buildPath(name)
        println("Saving $url to $path")
        val inputStream = network.getInputStream(url)
        Files.copy(inputStream, Paths.get(path))
        println("Saved $url to $path")
    }

    private fun buildPath(name: String): String {
        try {
            File(path).mkdirs()
            return path + name
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Can't create dir $path", e)
        }
    }

    fun saveAudios(id: String) {
        if (id.isBlank()) throw Exception("post id can't be empty")
        println("Start loading audios")
        val posts = service.getWall(id)
        val audios = service.selectAudio(posts)
        saveAudios(audios)
        println("All audios saved to $path")
    }


    fun saveAudioToZip(id: String) {
        val file = File.createTempFile("downloader", ".zip")
        val os = FileOutputStream(file)
        service.saveAudioToZip(id, os)
        println("All audios saved to ${file.absoluteFile}")
    }
}
