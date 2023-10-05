package org.unibl.etf.pisio.imageprocessor.image

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.unibl.etf.pisio.imageprocessor.extension.toImageResponse
import java.net.URI
import java.util.*

@Component
class ImageRoutes {
    @Bean
    fun http(imageService: ImageService) = coRouter {
        POST("/api/v1/images") {
            val parts = it.awaitMultipartData()
            val filePart = parts.toSingleValueMap()["image"] ?: throw IllegalArgumentException("Missing 'image' part")
            val byteArray = filePart.content().awaitSingle().asInputStream().readAllBytes()
            val createdImage = imageService.save(byteArray)
            ServerResponse.created(URI.create("/api/v1/images/${createdImage.id}"))
                .bodyValueAndAwait(createdImage.toImageResponse())
        }
        DELETE("/api/v1/images/{id}") {
            try {
                val id = UUID.fromString(it.pathVariable("id"))
                val image = imageService.findById(id)
                if (image != null) {
                    imageService.delete(image)
                    ServerResponse.noContent().buildAndAwait()
                } else {
                    ServerResponse.notFound().buildAndAwait()
                }

            } catch (_: IllegalArgumentException) {
                ServerResponse.notFound().buildAndAwait()
            }
        }
    }
}
