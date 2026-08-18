# 🛡️ Android GlassBox Error Handler

> Stop guessing why your app crashed. Turn every failure into a diagnostic window.

A lightweight, drop-in utility for Kotlin & Jetpack Compose that intercepts system crashes globally, automatically copying the raw stack trace to the clipboard and showing instant visual feedback before a clean app exit.

---

## 🌍 Why use this?

In many scenarios, developers and users face unexpected exceptions, environment constraints, or diverse hardware issues. Instead of a generic *"App has stopped"* message, **GlassBox** reveals the technical root cause (such as network drops, permission errors, or unhandled exceptions) directly, allowing users or support teams to capture and share diagnostic data instantly.

---

## 🚀 Key Features

* **Zero-Footprint:** No background services, minimal memory consumption, and zero battery drain.
* **Universal Interceptor:** Intercepts uncaught exceptions globally using `Thread.UncaughtExceptionHandler`.
* **Support-Ready:** Automatically copies the raw error log to the clipboard with one tap and shows an instant feedback `Toast`.
* **Thread-Safe & Reliable:** Fully compliant with modern Android best practices (API 33+), executing UI and clipboard interactions safely on the Main Thread.
* **Transparent Exit:** Doesn't hide fatal crashes behind unstable states—ensures a clean and secure exit while preserving the diagnostic log.

---

## 📦 Installation & Implementation

To ensure monitoring begins the exact second your application starts (even before any screen loads), initialize the handler inside your custom `Application` class (`MyApplication.kt`):

```kotlin
import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ativa o interceptador global de crashes
        AppCrashHandler.init(this)
    }
}
