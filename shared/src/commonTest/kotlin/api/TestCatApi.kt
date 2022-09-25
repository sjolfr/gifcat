package api

import io.kotest.core.Tag
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotBeEmpty
import uk.co.gifcat.api.CatsApi
import uk.co.gifcat.api.models.Breed
import uk.co.gifcat.api.models.Image

class TestCatApi : BehaviorSpec() {
    override fun tags(): Set<Tag> {
        return setOf(Tag("CatApi"))
    }

    init {
        given("a cat api") {
            `when`("when a request is made to get breeds") {
                val response: List<Breed> = CatsApi.getBreeds(10, 0)
                then("the request should return ten results") {
                    response.shouldHaveSize(10)
                }
                then("the request should return a list of breeds") {
                    response.forEach {
                        it.id.shouldNotBeEmpty()
                    }
                }
            }

            `when`("a request is made to get images for abys") {
                val response: List<Image> = CatsApi.getBreedImages("abys", 3, 0)
                then("the request should return three results") {
                    response.shouldHaveSize(3)
                }
                response.forEach { image ->
                    then("the ids should be nine characters long") {
                        image.id.shouldHaveLength(9)
                    }
                    then("the image should have a valid url") {
                        image.url should MatchUrl
                    }
                    then("the image height should be greater than zero") {
                        image.height.shouldBeGreaterThan(0)
                    }
                    then("the image width should be greater than zero") {
                        image.width.shouldBeGreaterThan(0)
                    }
                    then("the image type should not be gif") {
                        image.url.shouldNotBe("gif")
                    }
                }
            }
        }
    }
}
