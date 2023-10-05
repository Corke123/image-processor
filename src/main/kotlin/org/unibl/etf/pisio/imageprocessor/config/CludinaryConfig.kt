package org.unibl.etf.pisio.imageprocessor.config

import com.cloudinary.Cloudinary
import com.cloudinary.Uploader
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CludinaryConfig {

    @Bean
    fun cludinaryProperties() = CloudinaryProperties()

    @Bean
    fun cloudinary(cloudinaryProperties: CloudinaryProperties) =
        Cloudinary("cloudinary://${cloudinaryProperties.apiKey}:${cloudinaryProperties.apiSecret}@${cloudinaryProperties.cloudName}?secure=true")
}


@ConfigurationProperties(prefix = "cloudinary")
data class CloudinaryProperties(
    var cloudName: String = "", var apiKey: String = "", var apiSecret: String = "", var folder: String = ""
)

suspend fun Uploader.uploadAsync(
    file: Any, options: Map<*, *>, dispatcher: CoroutineDispatcher = Dispatchers.IO
): MutableMap<Any?, Any?> = withContext(dispatcher) { upload(file, options) }

suspend fun Uploader.destroyAsync(
    publicId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
): MutableMap<Any?, Any?> = withContext(dispatcher) { destroy(publicId, ObjectUtils.emptyMap()) }