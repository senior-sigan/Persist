package org.seniorsigan.downloader

import com.google.gson.*
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Service {
    private val client = OkHttpClient()
    private val gsonBuilder = GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer())
    private val gson = gsonBuilder.create()
    private val path = "/tmp/downloader/"

    init {
        client.setConnectTimeout(15, TimeUnit.SECONDS)
        client.setReadTimeout(15, TimeUnit.SECONDS)
    }

    fun getWall(id: String): List<Item> {
        val url = "https://api.vk.com/method/wall.getById?posts=$id&extended=0&copy_history_depth=1"
        val rawResponse = get(url)
        val json = parseJson(rawResponse, WallResponse::class.java)
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

    fun saveAudio(audios: List<Audio>) {
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

    private fun get(url: String): String {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (response != null && response.isSuccessful) {
            return response.body().string()
        } else {
            throw Exception("Can't load $url: ${response?.message()}")
        }
    }

    private fun getAndSave(url: String, name: String) {
        val path = buildPath(name)
        println("Saving $url to $path")
        val inputStream = getInputStream(url)
        Files.copy(inputStream, Paths.get(path))
        println("Saved $url to $path")
    }

    private fun getInputStream(url: String): InputStream {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (response != null && response.isSuccessful) {
            return response.body().byteStream()
        } else {
            throw Exception("Can't load $url: ${response?.message()}")
        }
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

    private fun <T> parseJson(rawJson: String, classOf: Class<T>): T? {
        try {
            return gson.fromJson(rawJson, classOf)
        } catch (e: Exception) {
            return null
        }
    }


    class DateDeserializer: JsonDeserializer<Date> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
            val timestamp = json?.asJsonPrimitive?.asLong
            if (timestamp != null) {
                return Date(timestamp * 1000)
            } else {
                throw JsonParseException("Can't parse date ${json?.asString}")
            }
        }
    }

    fun saveAudios(id: String) {
        if (id.isBlank()) throw Exception("post id can't be empty")
        println("Start loading audios")
        val posts = getWall(id)
        val audios = selectAudio(posts)
        saveAudio(audios)
        println("All audios saved to $path")
    }

    fun saveAudioToZip(id: String, os: OutputStream): List<Audio> {
        if (id.isBlank()) throw Exception("post id can't be empty")
        val zip = ZipService()
        println("Start loading audios")
        val posts = getWall(id)
        val audios = selectAudio(posts)
        zip.pack(audios.map { RemoteFile(it.url, it.name(), getInputStream(it.url)) }, os)
        return audios
    }

    fun saveAudioToZip(id: String) {
        val file = File.createTempFile("downloader", ".zip")
        val os = FileOutputStream(file)
        saveAudioToZip(id, os)
        println("All audios saved to ${file.absoluteFile}")
    }
}
