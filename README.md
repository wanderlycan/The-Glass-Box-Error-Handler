# The-Glass-Box-Error-Handler
Stop guessing why your app crashed. Turn every failure into a diagnostic window.

# 🛡️ Android GlassBox Error Handler

A lightweight, drop-in utility for Kotlin & Jetpack Compose that intercepts system crashes and Firestore errors, displaying them in a user-friendly, copy-pasteable diagnostic window.

## 🌍 Why use this?
In many regions, developers and users face unstable connections and diverse hardware. Instead of a generic "App has stopped" message, **GlassBox** reveals the technical root cause (like `PERMISSION_DENIED` or `CredentialException`) directly to the user or support team.

## 🚀 Key Features
- **Zero-Footprint**: No background services or battery drain (consumo irrisório de RAM).
- **Universal Interceptor**: Intercepts uncaught exceptions globally using `Thread.UncaughtExceptionHandler`.
- **Support-Ready**: Automatically copies the raw error log to the clipboard with one tap and shows an instant feedback Toast.
- **Transparency**: Fully compliant with Android best practices—it doesn't hide fatal crashes behind risky states, ensuring a clean exit while preserving the diagnostic log.

## 📦 Installation & Implementation

To ensure that monitoring begins the exact second the application is opened (even before any screen loads), initialize the handler in your project's custom `Application` class (`MyApplication.kt`):

```kotlin
import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Ativa o interceptador global de crashes
        AppCrashHandler.init(this)
    }
}
