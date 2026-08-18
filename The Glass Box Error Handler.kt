import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

class AppCrashHandler private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun uncaughtException(t: Thread, e: Throwable) {
        val stackTraceString = Log.getStackTraceString(e)

        try {
            // 1. Joga o erro direto para o clipboard (copiar/colar)
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Crash Log", stackTraceString)
            clipboard.setPrimaryClip(clip)
        } catch (_: Exception) {
            // Garante que se falhar o clipboard não trava o processo
        }

        // 2. Exibe um Toast amigável na Thread Principal antes do app fechar
        try {
            mainHandler.post {
                Toast.makeText(
                    context,
                    "Ops! O app fechou, mas o erro já foi copiado para a área de transferência.",
                    Toast.LENGTH_LONG
                ).show()
            }
            
            // Dá um breve respiro (300ms) para o Toast aparecer na tela e o clipboard salvar
            Thread.sleep(300)
        } catch (_: Exception) {
            // Fallback caso a thread de UI falhe
        }

        // 3. Repassa para o handler padrão do Android fechar o app de forma limpa e segura
        defaultHandler?.uncaughtException(t, e)
    }

    companion object {
        fun init(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler(context.applicationContext))
        }
    }
}
