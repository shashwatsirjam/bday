using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

public class Worker : BackgroundService
{
    private readonly ILogger<Worker> _logger;
    private readonly string javaExecutable = "java";  // Path to java executable, ensure it's in PATH
    private readonly string jarFilePath = @"C:\Users\Shashwat\app\your-application.jar";
    private readonly string configDir = @"C:\Users\Shashwat\app\config";
    private readonly string certsDir = @"C:\Users\Shashwat\app\certs";

    public Worker(ILogger<Worker> logger)
    {
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        // Run the Java application
        var startInfo = new ProcessStartInfo
        {
            FileName = javaExecutable,
            Arguments = $"-jar \"{jarFilePath}\"", // Arguments to run the .jar file
            WorkingDirectory = Path.GetDirectoryName(jarFilePath),  // Set working directory to the location of the .jar file
            RedirectStandardOutput = true,
            RedirectStandardError = true,
            UseShellExecute = false,
            CreateNoWindow = true
        };

        // Add environment variables for your Java app (e.g., for config, certs)
        startInfo.EnvironmentVariables["CONFIG_DIR"] = configDir;
        startInfo.EnvironmentVariables["CERTS_DIR"] = certsDir;

        try
        {
            _logger.LogInformation("Starting Java application...");

            using (var process = Process.Start(startInfo))
            {
                if (process != null)
                {
                    process.OutputDataReceived += (sender, e) => _logger.LogInformation(e.Data);
                    process.ErrorDataReceived += (sender, e) => _logger.LogError(e.Data);

                    process.BeginOutputReadLine();
                    process.BeginErrorReadLine();

                    await process.WaitForExitAsync(stoppingToken);
                }
            }
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error starting Java application: {ex.Message}");
        }
    }
}
