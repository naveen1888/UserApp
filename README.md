# UserApp - User Management System

A modern Android application demonstrating the use of Jetpack Compose, Room Database, Hilt, and MVVM architecture.

## 🚀 Features

- **User List**: Displays all saved users in an alphabetical list. Handles empty states gracefully.
- **Add User**: Form with real-time validation for:
  - **Name**: Non-empty, letters and spaces only.
  - **Email**: Valid format and **uniqueness enforcement** (prevents duplicate emails).
  - **Age**: Numeric input between 1 and 100.
- **User Details**: View specific information for a selected user.
- **Robust Error Handling**: Specific user feedback for database conflicts, validation errors, and loading states.
- **Modern UI**: Built entirely with Jetpack Compose and Material 3 design.

## 🛠 Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Database**: [Room](https://developer.android.com/training/data-storage/room) (SQLite abstraction)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Asynchronous Programming**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Testing**:
  - **Unit Tests**: Mockito & Kotlinx Coroutines Test
  - **UI Tests**: Compose Test Rule & Espresso
  - **Coverage**: Jacoco for detailed reporting

## 📂 Project Structure

- `com.user.app.data`: Room entities, DAO, Database configuration, and Repository.
- `com.user.app.ui`: Compose screens, ViewModels, and Theme.
- `com.user.app.UserApplication`: Centralized dependency management and lazy database initialization.

## 🧪 Testing

The project has a comprehensive test suite covering all layers:

### Running Tests
- **Unit Tests**:
  ```bash
  ./gradlew :app:testDebugUnitTest
  ```
- **Instrumented Tests** (requires an emulator/device):
  ```bash
  ./gradlew :app:connectedDebugAndroidTest
  ```

### Code Coverage
The project is configured with **Jacoco**. To generate a detailed HTML coverage report, run:
```bash
./gradlew createDebugCoverageReport
```
Report location: `app/build/reports/coverage/androidTest/debug/connected/index.html`

## 🏗 Setup & Installation

1. Clone the repository.
2. Open in **Android Studio Ladybug** or newer.
3. Sync Gradle and run the `:app` module.

## 📝 Design Patterns

- **Stateless Composables**: UI components are decoupled from logic for better testability and preview support.
- **Sealed Class for Errors**: Specific error types (`DuplicateEmail`, `DatabaseError`) for clear UI communication.
- **Repository Pattern**: Abstracts data source details from the ViewModel.
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for managing dependencies.
