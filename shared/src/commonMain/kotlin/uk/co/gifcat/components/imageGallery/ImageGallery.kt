package uk.co.gifcat.components.imageGallery

import com.arkivanov.decompose.value.Value

interface ImageGallery {
    val model: Value<ImageGalleryModel>

    fun onBackPressed()
}

data class ImageGalleryModel(
    private val breedId: String,
    val isLoading: Boolean,
    val breedName: String,
    val images: List<ImageModel>,
    val page: Int,
    val limit: Int,
) {
    internal constructor(
        breedId: String,
        breedName: String
    ) : this(breedId, false, breedName, listOf(), DefaultPage, DefaultLimit)
    constructor() : this("", false, "", listOf(), DefaultPage, DefaultLimit)

    private companion object {
        const val DefaultPage = 0
        const val DefaultLimit = 3
    }
}

data class ImageModel(val imageUrl: String, val height: Long, val width: Long) {
    constructor() : this("", 0, 0)
}
