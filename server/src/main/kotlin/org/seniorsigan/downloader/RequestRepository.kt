package org.seniorsigan.downloader

import java.util.logging.Logger

class RequestRepository {
    companion object {
        val LOG = Logger.getLogger("RequestRepository")
    }

    fun save(data: Any) {
        LOG.info("Save to db: $data")
        //TODO: save to db
    }
}
