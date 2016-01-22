package org.seniorsigan.downloader.cli

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size == 0) throw Exception("post id can't be empty")
        val path: String = if (args.size == 2) {
            args[1]
        } else {
            "/tmp/downloader/"
        }
        val service = FileDownloadService(path)
        service.saveAudios(args[0])
        //service.saveAudioToZip(args[0])
    }
}
