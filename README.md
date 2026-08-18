# 🛡️ GlassBox Error Handler

> Stop guessing why your app crashed. Turn every failure into a diagnostic window.

A lightweight, drop-in utility that intercepts system crashes globally, automatically copying the raw stack trace to the clipboard and showing instant visual feedback before a clean app exit. Available for both **Android** and **Windows**.

---

## 🌐 Supported Platforms

| Platform | Technology | Description |
| :--- | :--- | :--- |
| **Android** | Kotlin / Jetpack Compose | Intercepts unhandled app exceptions globally using `Thread.UncaughtExceptionHandler`. |
| **Windows** | C# / .NET (WPF / WinForms) | Intercepts unhandled desktop exceptions globally using `AppDomain.CurrentDomain.UnhandledException`. |
| **Linux** | C# / .NET (.NET Core / Mono) | Intercepts unhandled system exceptions globally, dumping diagnostics to local logs and clipboard. |
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

## 🐧 Linux Implementation

To initialize the global error handler in your Linux .NET application, call `LinuxGlassBox.Init()` at the very entry point of your program (`Program.cs`):

```csharp
using GlassBox.Linux;

class Program
{
    static void Main(string[] args)
    {
        // Ativa o interceptador global de crashes no Linux
        LinuxGlassBox.Init();

        // Seu código principal aqui...
    }
}

using GlassBox.Desktop;

public partial class App : Application
{
    protected override void OnStartup(StartupEventArgs e)
    {
        base.OnStartup(e);
        
        // Ativa o GlassBox no início da execução
        WindowsGlassBox.Init();
    }
}
