import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.nio.file.Paths;

@Slf4j
public class WindowsServiceWrapper {
    private static final String SERVICE_NAME = "FSDTService";
    private static final String DISPLAY_NAME = "FSDT Application Service";
    private static final String DESCRIPTION = "Windows Service for FSDT Application";
    private static final String APP_PATH = "C:\\user\\shaswat";
    private static final String JAR_NAME = "FSDT.jar";
    
    private Process currentProcess;

    public static void main(String[] args) {
        WindowsServiceWrapper wrapper = new WindowsServiceWrapper();
        
        if (args.length > 0) {
            String command = args[0];
            switch (command.toLowerCase()) {
                case "install":
                    wrapper.installService();
                    break;
                case "uninstall":
                    wrapper.uninstallService();
                    break;
                case "start":
                    wrapper.startService();
                    break;
                case "stop":
                    wrapper.stopService();
                    break;
                default:
                    wrapper.runApplication();
            }
        } else {
            wrapper.runApplication();
        }
    }

    private void installService() {
        try {
            // Create service command with full path
            String servicePath = String.format(
                "java -Xmx512m -Dlog4j.configurationFile=config/log4j2.xml " +
                "-Djavax.net.ssl.trustStore=cert/176-solace.jks " +
                "-jar \"%s\\%s\" " +
                "--spring.config.location=%s\\application.properties",
                APP_PATH, JAR_NAME, APP_PATH
            );

            ProcessBuilder processBuilder = new ProcessBuilder(
                "sc",
                "create",
                SERVICE_NAME,
                "binPath=" + servicePath,
                "start=auto",
                "DisplayName=" + DISPLAY_NAME
            );
            
            Process process = processBuilder.start();
            int result = process.waitFor();

            if (result == 0) {
                // Set description
                processBuilder = new ProcessBuilder(
                    "sc",
                    "description",
                    SERVICE_NAME,
                    DESCRIPTION
                );
                
                process = processBuilder.start();
                process.waitFor();

                log.info("Service installed successfully");
            } else {
                log.error("Failed to install service. Error code: {}", result);
            }
        } catch (Exception e) {
            log.error("Installation error", e);
        }
    }

    private void runApplication() {
        try {
            // Build command to run your jar
            ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-Xmx512m",
                "-Dlog4j.configurationFile=config/log4j2.xml",
                "-Djavax.net.ssl.trustStore=cert/176-solace.jks",
                "-jar",
                Paths.get(APP_PATH, JAR_NAME).toString(),
                "--spring.config.location=" + Paths.get(APP_PATH, "application.properties")
            );
            
            // Set working directory
            processBuilder.directory(new File(APP_PATH));
            
            // Redirect error and output to files
            File logsDir = new File(APP_PATH, "logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
            
            processBuilder.redirectError(new File(logsDir, "error.log"));
            processBuilder.redirectOutput(new File(logsDir, "output.log"));
            
            // Start the process
            currentProcess = processBuilder.start();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (currentProcess != null && currentProcess.isAlive()) {
                    currentProcess.destroy();
                }
            }));
            
            // Wait for the process
            int exitCode = currentProcess.waitFor();
            if (exitCode != 0) {
                log.error("Application exited with code: {}", exitCode);
                throw new RuntimeException("Application exited with code: " + exitCode);
            }
        } catch (Exception e) {
            log.error("Failed to start application", e);
            throw new RuntimeException("Failed to start application", e);
        }
    }

    // [Previous uninstallService, startService, and stopService methods remain the same]
}
