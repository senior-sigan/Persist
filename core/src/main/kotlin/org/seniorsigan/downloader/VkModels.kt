package org.seniorsigan.downloader

import java.util.*

data class WallResponse(
    var response: List<Item> = emptyList()
)

data class Item(
    var id: Long = 0,
    var from_id: Long = 0,
    var owner_id: Long = 0,
    var date: Date = Date(),
    var post_type: String = "",
    var text: String = "",
    var attachments: List<Attachment> = emptyList()
)

data class Attachment(
    var type: String = "",
    var audio: AudioAttachment? = null,
    var link: LinkAttachment? = null,
    var photo: PhotoAttachment? = null
)

data class AudioAttachment(
    var id: Long = 0,
    var owner_id: Long = 0,
    var url: String = "",
    var artist: String = "",
    var title: String = "",
    var date: Date = Date(),
    var duration: Long = 0
)

data class PhotoAttachment(
    var id: Long = 0,
    var owner_id: Long = 0,
    var src_small: String = "",
    var src_big: String = "",
    var src_xbig: String = "",
    var src_xxbig: String = "",
    var src: String = "",
    var width: Int = 0,
    var height: Int = 0
) {
    fun photoUrl(): String? {
        return listOf(src, src_xxbig, src_xbig, src_big, src_small).firstOrNull { it.isNotBlank() }
    }
}

data class LinkAttachment(
    var url: String = ""
)
