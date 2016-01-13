package org.seniorsigan.downloader

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gson.*
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.util.*

class NetworkService(
    private val transport: HttpTransport = NetHttpTransport()
) {
    private val requestFactory = transport.createRequestFactory()

    private val gsonBuilder = GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer())
    private val gson = gsonBuilder.create()

    private class DateDeserializer: JsonDeserializer<Date> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
            val timestamp = json?.asJsonPrimitive?.asLong
            if (timestamp != null) {
                return Date(timestamp * 1000)
            } else {
                throw JsonParseException("Can't parse date ${json?.asString}")
            }
        }
    }

    fun <T> getAs(url: String, clazz: Class<T>): T? {
        val request = requestFactory.buildGetRequest(GenericUrl(url))
        val response = request.execute()
        if (response != null && response.isSuccessStatusCode) {
            return gson.fromJson(response.content.reader(), clazz)
        } else {
            throw Exception("Can't load $url: ${response?.statusMessage}")
        }
    }

    fun getInputStream(url: String): InputStream {
        println("Load $url")
        val request = requestFactory.buildGetRequest(GenericUrl(url))
        val response = request.execute()
        if (response != null && response.isSuccessStatusCode) {
            return response.content
        } else {
            throw Exception("Can't load $url: ${response?.statusMessage}")
        }
    }

    fun download(url: String, out: OutputStream) {
        val request = requestFactory.buildGetRequest(GenericUrl(url))
        val response = request.execute()
        if (response != null && response.isSuccessStatusCode) {
            response.download(out)
        } else {
            throw Exception("Can't load $url: ${response?.statusMessage}")
        }
    }
}
