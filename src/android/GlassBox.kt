package io.glassbox

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Small, dependency-free crash reporter for Android.
 *
 * Call [install] once from Application.onCreate(). Fatal exceptions are still delegated to
 * Android's original handler; GlassBox only records and presents diagnostics beforehand.
 */
object GlassBox {
    private const val TAG = "GlassBox"
    private const val CRASH_DIRECTORY = "glassbox-crashes"
    private val installed = AtomicBoolean(false)
    private lateinit var appContext: Context
    private var previousHandler: Thread.UncaughtExceptionHandler? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    fun install(context: Context) {
        if (!installed.compareAndSet(false, true)) return

        appContext = context.applicationContext
        previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleFatal(thread, throwable)
        }
    }

    /** Records an exception that was caught by the app and would otherwise be invisible. */
    fun report(throwable: Throwable, source: String = "caught exception") {
        if (!installed.get()) {
            Log.w(TAG, "GlassBox.install(context) was not called; report was ignored.")
            return
        }
        writeReport(throwable, source, fatal = false)
    }

    fun latestReportFile(): File? =
        crashDirectory().listFiles()?.maxByOrNull { it.lastModified() }

    private fun handleFatal(thread: Thread, throwable: Throwable) {
        try {
            val report = writeReport(throwable, "uncaught exception on \${thread.name}", fatal = true)
            showFatalNotice(report)
        } catch (handlerFailure: Throwable) {
            // The error handler must never prevent Android's normal crash path.
            Log.e(TAG, "GlassBox failed while processing a fatal exception", handlerFailure)
        } finally {
            val fallback = previousHandler
            if (fallback != null && fallback !== Thread.getDefaultUncaughtExceptionHandler()) {
                fallback.uncaughtException(thread, throwable)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
                kotlin.system.exitProcess(10)
            }
        }
    }

    private fun writeReport(throwable: Throwable, source: String, fatal: Boolean): File {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US)
        val stackTrace = StringWriter().also { writer ->
            throwable.printStackTrace(PrintWriter(writer))
        }.toString()
        val report = buildString {
            appendLine("GlassBox diagnostic report")
            appendLine("time: \${formatter.format(Date())}")
            appendLine("fatal: \$fatal")
            appendLine("source: \$source")
            appendLine("thread: \${Thread.currentThread().name}")
            appendLine()
            append(stackTrace)
        }

        Log.e(TAG, report)
        val file = File(crashDirectory(), "crash-\${System.currentTimeMillis()}.log")
        file.writeText(report, Charsets.UTF_8)
        return file
    }

    private fun crashDirectory(): File =
        File(appContext.filesDir, CRASH_DIRECTORY).apply { mkdirs() }

    private fun showFatalNotice(report: File) {
        // A fatal exception can occur off the UI thread. Give the main loop a brief, bounded
        // chance to show/copy the message, then always let the original handler terminate.
        val completed = CountDownLatch(1)
        mainHandler.post {
            try {
                val text = report.readText()
                val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("GlassBox crash report", text))
                Toast.makeText(
                    appContext,
                    "O app encontrou um erro. O diagnóstico foi salvo e copiado.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (clipboardFailure: Throwable) {
                Log.w(TAG, "Could not copy fatal report to clipboard", clipboardFailure)
            } finally {
                completed.countDown()
            }
        }
        completed.await(250, TimeUnit.MILLISECONDS)
    }
}
