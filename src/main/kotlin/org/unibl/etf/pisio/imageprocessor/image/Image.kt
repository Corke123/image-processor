package org.unibl.etf.pisio.imageprocessor.image

import org.springframework.data.annotation.Id
import java.util.*

data class Image(@Id val id: UUID?, val url: String, val publicId: String)
