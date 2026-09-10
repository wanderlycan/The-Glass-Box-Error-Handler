using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Threading.Tasks;

namespace GlassBox.Linux;

/// <summary>Global diagnostics for .NET apps on Linux (desktop or headless).</summary>
public static class LinuxGlassBox
{
    private static int _installed;

    public static void Install()
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
    }

    public static string Report(Exception exception, string source = "Caught exception") =>
        Record(exception, source, false);

    private static string Record(Exception exception, string source, bool fatal)
    {
        var report = $"""
            GlassBox diagnostic report
            time: {DateTimeOffset.Now:O}
            platform: Linux
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

        TryCopyToClipboard(report);
        Console.Error.WriteLine($"[GlassBox] Diagnostic saved to {path}");
        return path;
    }

    private static void TryCopyToClipboard(string report)
    {
        var command = Environment.GetEnvironmentVariable("WAYLAND_DISPLAY") != null ? "wl-copy" : "xclip";
        var arguments = command == "xclip" ? "-selection clipboard" : string.Empty;

        try
        {
            using var process = Process.Start(new ProcessStartInfo
            {
                FileName = command,
                Arguments = arguments,
                RedirectStandardInput = true,
                UseShellExecute = false,
                CreateNoWindow = true
            });
            if (process == null) return;
            process.StandardInput.Write(report);
            process.StandardInput.Close();
            process.WaitForExit(500);
        }
        catch
        {
            // Headless servers and minimal containers usually have no clipboard utility.
        }
    }
}
