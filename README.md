# GifCat

Powered by [The Cat Api](https://developers.thecatapi.com)

## Features
### List of Breeds

- displays an inifite list of cards diplaying information about each breed of cat
- expandable cards to show attributes for each breed
- on tap of the card the app navigates to the image gallery screen

![breeds](docs/breeds.png?token=GHSAT0AAAAAABYJTLRHZ4LJND3YMQ6E6CZIYZVTQNA)

![breeds expanded](docs/breeds-expanded.png?token=GHSAT0AAAAAABYJTLRGW7BUSIDDM6SKMKXEYZVTPFA)

### Image Gallery
- horizontal paged image gallery displaying up to 10 images of the selected breed
- uses the color pallette of the loaded image to set the background of the page, this background color is different in light and dark modes

![gallery](docs/gallery.png?token=GHSAT0AAAAAABYJTLRGLG5CVXFAFNXHSO4AYZVTQOQ)

## Libraries/Frameworks/Other

#### Conventional Commits [Website](https://www.conventionalcommits.org/en/v1.0.0/) | [Cheat-sheet](https://cheatography.com/albelop/cheat-sheets/conventional-commits/)
"A specification for adding human and machine readable meaning to commit messages" Helpful in automation of release version generation and can be used for internal release notes

#### Decompose [Github](https://github.com/arkivanov/Decompose) | [Docs](https://arkivanov.github.io/Decompose/)

Provides a means for the shared layer to share navigation and pluggable ui between multiple platforms.

#### detekt [Github](https://github.com/detekt/detekt) & ktlint [Github](https://github.com/pinterest/ktlint)
detekt and ktlint for static code analysis and linting.

#### kotest [Website](https://kotest.io/)
api tests tagged so they can be ran separately to other tests.

#### Kermit [Github](https://github.com/touchlab/Kermit)
kotlin multiplatform logging framework.

#### Landscapist [Github](https://github.com/skydoves/landscapist)
Jetpack Compose library used for images.

#### Accompanist [Github](https://github.com/google/accompanist)
Jetpack Compose library used for horizontal paging in image gallery.

## Further work:
#### iOS Support
The small number of integration tests in the project and using Kotlin Multiplatform should reduce the time it takes to add an iOS UI. Navigation, API calls and mapping API responses is already handled by the shared layer.

#### Translations
Moko resources could be used to localise the text in the shared layer.

#### UI/UX
- improve handling of no internet connection with observable expect actual implementations for iOS and Android. This would make it possible to introduce a no internet connection component instead or try again once internet is resumed
  - Root component could listen to internet connection changes -> push no internet to top of stack, pop when internet resumes
- improve UI for tablet and landscape mode on phones
- placeholders for all of the breed cards and image gallery images
- improve loading experience for images in the gallery
- change scaffold title for each page
- further improve card layout
- zoom support for each gallery image

#### Other
- dependency injection for the core layer instead of singletons e.g. CatsApi
- facade layer could be added between components and api to handle mapping of responses
- hook up ktlint and detekt to git commit pre hook
- reporting of unit/integration tests
- reporting of cyclomatic complexity via detekt
- documentation generation using dokka or similar

## Misc
- api key not hidden, deemed it to be low risk information
- app performs best when ran app without debugging, debug isn't that performant with Jetpack Compose
- could reduce app size by not using androidx.compose.material3:material3-icons-extended and instead pulling in the limited number of icons needed by the app.
