using System;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;

namespace GlassBox.Desktop;

/// <summary>Dependency-free diagnostics for WPF applications. Call Install() at app startup.</summary>
public static class WindowsGlassBox
{
    private static int _installed;

    public static void Install(bool continueAfterUiException = false)
    {
        if (Interlocked.Exchange(ref _installed, 1) != 0) return;

        AppDomain.CurrentDomain.UnhandledException += (_, eventArgs) =>
            Record(eventArgs.ExceptionObject as Exception ??
                   new Exception("Unknown AppDomain exception."), "AppDomain.UnhandledException", true);

        TaskScheduler.UnobservedTaskException += (_, eventArgs) =>
        {
            Record(eventArgs.Exception, "TaskScheduler.UnobservedTaskException", false);
            eventArgs.SetObserved();
        };

        if (Application.Current != null)
        {
            Application.Current.DispatcherUnhandledException += (_, eventArgs) =>
            {
                Record(eventArgs.Exception, "DispatcherUnhandledException", !continueAfterUiException);
                ShowDialog();
                eventArgs.Handled = continueAfterUiException;
            };
        }
    }

    public static string Report(Exception exception, string source = "Caught exception") =>
        Record(exception, source, false);

    private static string Record(Exception exception, string source, bool fatal)
    {
        var report = $"""
            GlassBox diagnostic report
            time: {DateTimeOffset.Now:O}
            platform: Windows
            fatal: {fatal}
            source: {source}
            process: {Environment.ProcessPath}

            {exception}
            """;

        var directory = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
            "GlassBox", "crashes");
        Directory.CreateDirectory(directory);
        var path = Path.Combine(directory, $"crash-{DateTimeOffset.UtcNow:yyyyMMdd-HHmmss-fff}.log");
        File.WriteAllText(path, report);
        TrySetClipboard(report);
        return path;
    }

    private static void ShowDialog()
    {
        try
        {
            Application.Current?.Dispatcher.BeginInvoke(() =>
                MessageBox.Show(
                    "O aplicativo encontrou um erro. O diagnóstico foi salvo e copiado para a área de transferência.",
                    "GlassBox — diagnóstico de erro",
                    MessageBoxButton.OK,
                    MessageBoxImage.Error));
        }
        catch { /* Logging has already happened; never throw from the handler. */ }
    }

    private static void TrySetClipboard(string report)
    {
        try
        {
            if (Application.Current?.Dispatcher != null)
                Application.Current.Dispatcher.Invoke(() => Clipboard.SetText(report));
        }
        catch { /* Clipboard can be locked or unavailable in a service session. */ }
    }
}
