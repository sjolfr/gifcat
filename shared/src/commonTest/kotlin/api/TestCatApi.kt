package api

import io.kotest.core.Tag
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldNotBeEmpty
import uk.co.gifcat.api.CatsApi
import uk.co.gifcat.api.models.Breed

class TestCatApi : BehaviorSpec() {
    override fun tags(): Set<Tag> {
        return setOf(Tag("CatApi"))
    }

    init {
        given("a cat api") {
            `when`("when a request is made to get breeds") {
                val response: List<Breed> = CatsApi.getBreeds(10, 0)
                then("the request should return 10 results") {
                    response.shouldHaveSize(10)
                }
                then("the request should return a list of breeds") {
                    response.forEach {
                        it.id.shouldNotBeEmpty()
                    }
                }
            }
        }
    }
}
