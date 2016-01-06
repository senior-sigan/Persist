package org.seniorsigan.downloader

import com.google.common.io.ByteStreams
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipService {
    fun pack(files: List<RemoteFile>, os: OutputStream) {
        if (files.isEmpty()) throw Exception("Nothing to download and zip")
        val zos = ZipOutputStream(os)
        files.forEach {
            addToZip(it.name, it.stream, zos)
            it.stream.close()
        }
        zos.close()
        println("Zip created")
    }

    private fun addToZip(filename: String, input: InputStream, zos: ZipOutputStream) {
        println("Add $filename to zip")
        val zipEntry = ZipEntry(filename)
        zos.putNextEntry(zipEntry)
        try {
            ByteStreams.copy(input, zos)
        } catch (e: Exception) {
            println(e.message)
        }
        zos.closeEntry()
    }
}
