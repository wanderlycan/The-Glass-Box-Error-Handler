override fun uncaughtException(t: Thread, e: Throwable) {
    val stackTraceString = Log.getStackTraceString(e)

    try {
        // Executa tanto o Clipboard quanto o Toast na Thread Principal por segurança
        mainHandler.post {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Crash Log", stackTraceString)
                clipboard.setPrimaryClip(clip)
            } catch (_: Exception) {}

            Toast.makeText(
                context,
                "Ops! O app fechou, mas o erro já foi copiado para a área de transferência.",
                Toast.LENGTH_LONG
            ).show()
        }
        
        // Dá o respiro necessário para as tarefas assíncronas rodarem na main thread
        Thread.sleep(400)
    } catch (_: Exception) {
        // Fallback global de segurança
    }

    defaultHandler?.uncaughtException(t, e)
}
