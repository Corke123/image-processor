package org.unibl.etf.pisio.imageprocessor.extension

import org.unibl.etf.pisio.imageprocessor.image.Image
import java.util.*

fun Image.toImageResponse() = ImageResponse(id!!, url.substring(50))

data class ImageResponse(val id: UUID, val url: String)