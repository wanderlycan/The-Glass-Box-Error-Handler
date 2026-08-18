import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Looper
import android.widget.Toast

class AppCrashHandler private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        val stackTraceString = android.util.Log.getStackTraceString(e)

        try {
            // Joga o erro direto para o copiar/colar antes de morrer
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Crash Log", stackTraceString)
            clipboard.setPrimaryClip(clip)
        } catch (_: Exception) {
            // Garante que se falhar o clipboard não trava o processo de saída
        }

        // Se quiser tentar evitar o fechamento em casos específicos do Firebase/Exceções tratáveis:
        // (Nota: Se for um erro fatal crítico do sistema, ele ainda fechará, mas o texto já estará copiado).
        if (isRecoverableException(e)) {
            // Mantém a UI viva se for uma exceção controlada
            return
        }

        // Repassa para o handler padrão do Android fechar o app de forma limpa
        defaultHandler?.uncaughtException(t, e)
    }

    private fun isRecoverableException(e: Throwable): Boolean {
        val message = e.message ?: ""
        // Exemplo: se for um erro comum de mapeamento ou conflito tipado do Firebase que não corrompe o estado global
        return message.contains("Firebase", ignoreCase = true) || 
               message.contains("com.google.firebase", ignoreCase = true)
    }

    companion object {
        fun init(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler(context.applicationContext))
        }
    }
}
