import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ativa o monitor de falhas global ultraleve
        AppCrashHandler.init(this)
    }
}
