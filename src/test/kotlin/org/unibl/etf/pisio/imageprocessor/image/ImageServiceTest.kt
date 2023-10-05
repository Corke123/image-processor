package org.unibl.etf.pisio.imageprocessor.image

import com.cloudinary.Cloudinary
import com.cloudinary.Uploader
import com.cloudinary.utils.ObjectUtils.asMap
import com.cloudinary.utils.ObjectUtils.emptyMap
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.unibl.etf.pisio.imageprocessor.config.CloudinaryProperties
import java.util.*

@ExtendWith(MockKExtension::class)
class ImageServiceTest {

    @MockK(relaxed = true)
    lateinit var imageRepository: ImageRepository

    @MockK(relaxed = true)
    lateinit var cloudinary: Cloudinary

    @MockK
    lateinit var cloudinaryProperties: CloudinaryProperties

    @InjectMockKs
    lateinit var underTest: ImageService

    @Test
    fun `findById should return Image from database if exists`(): Unit = runBlocking {
        // given
        val id = UUID.randomUUID()
        coEvery { imageRepository.findById(id) } returns Image(id, "url", "publicId")

        // when
        val result = underTest.findById(id)

        // then
        assertThat(result).isEqualTo(Image(id, "url", "publicId"))
    }

    @Test
    fun `findById should return null if Image does not exist`(): Unit = runBlocking {
        // given
        val id = UUID.randomUUID()
        coEvery { imageRepository.findById(id) } returns null

        // when
        val result = underTest.findById(id)

        // then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `save should upload data to cloudinary into specific folder and save to database`(): Unit = runBlocking {
        // given
        val id = UUID.randomUUID()
        val data = "test".encodeToByteArray()
        val uploader = mockk<Uploader>()
        every { cloudinaryProperties.folder } returns "folder"
        every { cloudinary.uploader() } returns uploader
        every {
            uploader.upload(data, asMap("folder", "folder", "transformation", "c_fit,,w_620,h_400,ar_1.55"))
        } returns mutableMapOf("secure_url" to "url", "public_id" to "publicId")
        coEvery { imageRepository.save(Image(null, "url", "publicId")) } returns Image(id, "url", "publicId")

        // when
        val result = underTest.save(data)

        // then
        assertThat(result).isEqualTo(Image(id, "url", "publicId"))
    }

    @Test
    fun `delete should delete image from cloudinary and from database`() = runBlocking {
        // given
        val image = Image(UUID.randomUUID(), "url", "publicId")
        val uploader = mockk<Uploader>(relaxed = true)
        every { cloudinary.uploader() } returns uploader

        // when
        underTest.delete(image)

        // then
        verify { uploader.destroy("publicId", emptyMap()) }
        coVerify { imageRepository.delete(image) }
    }
}