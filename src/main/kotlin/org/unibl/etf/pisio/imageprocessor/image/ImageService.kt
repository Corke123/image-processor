package org.unibl.etf.pisio.imageprocessor.image

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils.asMap
import org.springframework.stereotype.Service
import org.unibl.etf.pisio.imageprocessor.config.CloudinaryProperties
import org.unibl.etf.pisio.imageprocessor.config.destroyAsync
import org.unibl.etf.pisio.imageprocessor.config.uploadAsync
import java.util.*

@Service
class ImageService(
    val cloudinaryProperties: CloudinaryProperties, val cloudinary: Cloudinary, val imageRepository: ImageRepository
) {

    suspend fun findById(id: UUID) = imageRepository.findById(id)

    suspend fun save(data: ByteArray): Image {
        val uploadResult = cloudinary.uploader()
            .uploadAsync(
                data,
                asMap("folder", cloudinaryProperties.folder, "transformation", "c_fit,,w_620,h_400,ar_1.55")
            )
        val url = uploadResult["secure_url"] as String
        val publicId = uploadResult["public_id"] as String
        val image = Image(null, url, publicId)
        return imageRepository.save(image)
    }

    suspend fun delete(image: Image) {
        cloudinary.uploader().destroyAsync(image.publicId)
        imageRepository.delete(image)
    }
}