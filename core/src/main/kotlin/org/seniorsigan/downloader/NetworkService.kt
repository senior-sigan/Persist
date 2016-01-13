package org.seniorsigan.downloader

import com.google.gson.*
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.InputStream
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

class NetworkService {
    private val client = OkHttpClient()
    private val gsonBuilder = GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer())
    private val gson = gsonBuilder.create()

    init {
        client.setConnectTimeout(15, TimeUnit.SECONDS)
        client.setReadTimeout(15, TimeUnit.SECONDS)
    }

    fun getInputStream(url: String): InputStream {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (response != null && response.isSuccessful) {
            return response.body().byteStream()
        } else {
            throw Exception("Can't load $url: ${response?.message()}")
        }
    }

    fun <T> get(url: String, clazz: Class<T>): T {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (response != null && response.isSuccessful) {
            return gson.fromJson(response.body().byteStream().reader(), clazz)
        } else {
            throw Exception("Can't load $url: ${response?.message()}")
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
}
