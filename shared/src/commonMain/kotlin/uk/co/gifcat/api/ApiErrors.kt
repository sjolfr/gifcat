package uk.co.gifcat.api

import kotlinx.coroutines.flow.MutableSharedFlow

internal object ApiErrors {
    val errors = MutableSharedFlow<String>()
}
