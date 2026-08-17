📝 Conteúdo para o README.md (Em Inglês para alcançar o mundo)
# 🛡️ Android GlassBox Error Handler

A lightweight, drop-in utility for Kotlin & Jetpack Compose that intercepts system crashes and Firestore errors, displaying them in a user-friendly, copy-pasteable diagnostic window.

## 🌍 Why use this?
In many regions, developers and users face unstable connections and diverse hardware. Instead of a generic "App has stopped" message, **GlassBox** reveals the technical root cause (like `PERMISSION_DENIED` or `CredentialException`) directly to the user or support team.

## 🚀 Key Features
- **Zero-Footprint**: No background services or battery drain.
- **Universal Interceptor**: Can be integrated into any ViewModel or Repository.
- **Support-Ready**: Allows users to copy the raw error log with one tap.
- **Transparency**: Fully compliant with Android best practices—it doesn't hide errors, it explains them.

## 🛠️ Implementation

### 1. The Error Interceptor (Universal Logic)
Add this to your `BaseViewModel` or any logic handler:

```kotlin
fun handleFailure(exception: Exception) {
    val technicalLog = exception.stackTraceToString()
    // Trigger your UI to show the GlassBox Dialog
    _uiErrorState.value = technicalLog
}
