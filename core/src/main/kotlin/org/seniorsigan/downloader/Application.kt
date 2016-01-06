package org.seniorsigan.downloader

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val service = Service()
        var id: String
        if (args.size == 0) {
            //throw Exception("post id can't be empty")
            id = "-58267631_18185"
        } else {
            id = args[0]
        }
        //service.saveAudios(id)
        service.saveAudioToZip(id)
    }
}
