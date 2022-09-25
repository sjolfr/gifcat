package api

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.ktor.http.URLBuilder
import io.ktor.http.URLParserException

object MatchUrl : Matcher<String> {
    override fun test(value: String): MatcherResult {
        val isValidUrl: Boolean = try {
            URLBuilder(value)
                .build()
            true
        } catch (uRLParserException: URLParserException) {
            false
        }

        return MatcherResult(
            passed = isValidUrl,
            failureMessageFn = { "string had value $value but we expected it to be a url" },
            negatedFailureMessageFn = { "string should be a valid url" },
        )
    }
}
