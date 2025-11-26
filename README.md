# ArrMatey

An all-in-one mobile too for your Arr stack

This is a Kotlin Multiplatform project targeting Android, iOS.

- [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

- [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

- [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Adding Localized String

Strings are generated for each platform from a shared source of truth. Strings can be added to [strings/strings.txt](./strings/strings.txt). Entries in [strings/strings.txt](./strings/strings.txt) follow these rules:

`[[Category]]` - purely organizational, will be displayed as a comment in Android strings.xml and ignored on iOS

`[key]` - the value to be used as the key for this string

Each `[key]` can have the following properties:

- `iosKey = {value}` - Allows you to specific a different key for the iOS `Localizeable.xcstrings` file. Because of how SwiftUI handles localization, you may want to use a key specifically for iOS, particularily if using plurals or variables.

- `comment = {value}` - {value} will be added as a comment to the entry is `Localizeable.xcstrings`, will also be show as an XML comment in Android `strings.xml` eg. `<string name="distance">%1$d-%2$d m</strimg> <!-- Represent a distance range -->`

- `{lang} = {value}` - the actual string and any translations, eg.

```
en = Hello
fr = Bonjour
es = Hola
```

- `{lang}_plural = {value}` is also supported for plurals. A `_plural` must be specified all languages or generation will fail. You will likely also want to set an `isoKey` for any plural entries, eg.

```
[items]
iosKey = %lld items
en = %d item
en_plural = %d items
fr = %d article
fr_plural = %d articles
```

- `variables` - The follow variables are supported

| Variable | Type   | iOS  | Android | Example   |
| -------- | ------ | ---- | ------- | --------- |
| %@       | String | %@   | %s      | Hello %@! |
| %d       | Int    | %lld | %d      | %d elk    |
| %f       | Float  | %f   | %f      | %.2f Kb   |

You can also specify the order of variables using the same format as native Android and iOS strings. eg. `en = Today is %1$d %2$@`

To generate platform specfic files, run

```
node generate-strings.js
```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
