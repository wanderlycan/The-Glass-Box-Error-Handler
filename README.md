# 🛡️ GlassBox Error Handler

> Stop guessing why your app crashed. Turn every failure into a diagnostic window.

A lightweight, drop-in utility that intercepts system crashes globally, automatically copying the raw stack trace to the clipboard and showing instant visual feedback before a clean app exit. Available for both **Android** and **Windows**.

---

## 🌐 Supported Platforms

| Platform | Technology | Description |
| :--- | :--- | :--- |
| **Android** | Kotlin / Jetpack Compose | Intercepts unhandled app exceptions globally using `Thread.UncaughtExceptionHandler`. |
| **Windows** | C# / .NET (WPF / WinForms) | Intercepts unhandled desktop exceptions globally using `AppDomain.CurrentDomain.UnhandledException`. |

---

## 🌍 Why use this?

In many scenarios, developers and users face unexpected exceptions, environment constraints, or diverse hardware issues. Instead of a generic crash message, *GlassBox* reveals the technical root cause directly, allowing users or support teams to capture and share diagnostic data instantly via the system clipboard.

---

## 📦 Installation & Implementation

### 📱 Android Implementation
To ensure monitoring begins the exact second your application starts, initialize the handler inside your custom `Application` class (`MyApplication.kt`):

```kotlin
import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ativa o interceptador global de crashes no Android
        AppCrashHandler.init(this)
    }
}
