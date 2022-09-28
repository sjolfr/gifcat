package uk.co.gifcat.api.errors

import kotlinx.coroutines.flow.MutableSharedFlow

internal object ApiErrors {
    val errors = MutableSharedFlow<ApiError>()
}
