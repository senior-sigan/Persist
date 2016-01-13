package org.seniorsigan.downloader.cli

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val service = StoreService()
        if (args.size == 0) throw Exception("post id can't be empty")
        service.saveAudios(args[0])
        //service.saveAudioToZip(args[0])
    }
}
