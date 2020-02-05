# SocialApp

## About
This is single module android application made as a part of my Engineer's Thesis titled 'Application with social media features that uses cloud database'. It follows MVVM architecture and is based on collaboration with Firebase Platform. It uses email and password authentication for gaining access to the content of the app, Cloud Storage to store images and Firestore NoSQL database for storing key-value pairs inside documents. Access to the database is restricted through built-in Security Rules Tool which also provides additional data validation. Due to data duplication, some data written by client app needs to be populated among other collections in the database. For this purpose Cloud Functions were introduced to the project, which get triggered in respond to write events and then perform additional operations on a server-side.

## Libraries used in the project
- [Data binding](https://developer.android.com/topic/libraries/data-binding) - Introduces declarative programming inside layout files and allows referencing view components directly with null and type safety
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Supports performing asynchronous operations in a main-thread safe way and getting rid of callbacks through suspended functions and converting code behaviour to a sequential
- [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) - Library that can manage complex navigation, transition animation, deep linking, and compile-time checked argument passing between the screens in the app
- [Glide](https://github.com/bumptech/glide) - Tool used for loading images from network
- [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Used to introduce ViewModel and LiveData to the project, along with set of extensions
- [Timber](https://github.com/JakeWharton/timber) - Small, extensible API for printing logs
- [Paging](https://developer.android.com/topic/libraries/architecture/paging) - Allows loading and displaying elements in chunks
- [Algolia](https://github.com/algolia/algoliasearch-client-android) - Used to extend Firestore functionality by searching for substrings within the text
- [Material Components](https://developer.android.com/guide/topics/ui/look-and-feel) - UI Components following Google Material Design Guidelines
- [ViewPager2](https://developer.android.com/training/animation/screen-slide-2) - Used for handling fragments inside tablayout

## TODO
- [ ] Introduce Koin as a service locator
- [ ] Split FirestoreRepository class into smaller pieces for better readability
