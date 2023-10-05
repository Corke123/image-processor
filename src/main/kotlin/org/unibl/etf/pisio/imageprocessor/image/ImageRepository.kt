package org.unibl.etf.pisio.imageprocessor.image

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface ImageRepository : CoroutineCrudRepository<Image, UUID>