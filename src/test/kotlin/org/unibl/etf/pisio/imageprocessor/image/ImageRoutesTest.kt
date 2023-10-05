package org.unibl.etf.pisio.imageprocessor.image

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class ImageRoutesTest {

    @MockkBean
    private lateinit var cloudinary: Cloudinary

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var imageRepository: ImageRepository

    @Test
    fun `should upload image to cloudinary, save data to db and return status created with uploaded url in body`(): Unit =
        runBlocking {
            // given
            every {
                cloudinary.uploader().upload(any<String>(), any<Map<*, *>>())
            } returns ObjectUtils.asMap(
                "secure_url",
                "https://res.cloudinary.com/abcdefghi/image/upload/v1693944072/incidents/nq8wkgz9k1yf4pjbgvy7.jpg",
                "public_id",
                "publicId"
            )
            val formData: MultiValueMap<String, Any> =
                LinkedMultiValueMap(mapOf("image" to listOf("image".toByteArray())))

            // when
            // then
            webTestClient.post()
                .uri("/api/v1/images")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(formData)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().valueMatches(
                    "Location",
                    "/api/v1/images/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                )
                .expectBody()
                .jsonPath("$.id").isNotEmpty
                .jsonPath("$.url").isEqualTo("v1693944072/incidents/nq8wkgz9k1yf4pjbgvy7.jpg")
        }

    @Test
    fun `should delete image from cludinary and from db`(): Unit =
        runBlocking {
            // given
            val image = Image(null, "url", "publicId")
            val savedImage = imageRepository.save(image)
            every { cloudinary.uploader().destroy(any<String>(), any<Map<*, *>>()) } returns ObjectUtils.emptyMap()

            // when
            // then
            webTestClient.delete()
                .uri("/api/v1/images/${savedImage.id}")
                .exchange()
                .expectStatus().isNoContent

            verify { cloudinary.uploader().destroy("publicId", ObjectUtils.emptyMap()) }
            confirmVerified(cloudinary)
        }
}