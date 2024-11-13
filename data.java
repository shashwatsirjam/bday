import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinNT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class WindowsServiceWrapper {
    private static final String SERVICE_NAME = "FSDTService";
    private static final String DISPLAY_NAME = "FSDT Application Service";
    private static final String DESCRIPTION = "Windows Service for FSDT Application";
    private static final String APP_PATH = "C:\\user\\shaswat";
    private static final String JAR_NAME = "FSDT.jar";
    private static final String JAVA_OPTS = "-Xmx512m -Dlog4j.configurationFile=config/log4j2.xml -Djavax.net.ssl.trustStore=cert/176-solace.jks";

    public static void main(String[] args) {
        if (args.length > 0) {
            String command = args[0];
            switch (command.toLowerCase()) {
                case "install":
                    installService();
                    break;
                case "uninstall":
                    uninstallService();
                    break;
                case "start":
                    startService();
                    break;
                case "stop":
                    stopService();
                    break;
                default:
                    runApplication();
            }
        } else {
            runApplication();
        }
    }

    private static void installService() {
        try {
            // Create service with full path and Java options
            String servicePath = String.format(
                "java %s -jar \"%s\\%s\" --spring.config.location=%s\\application.properties",
                JAVA_OPTS,
                APP_PATH,
                JAR_NAME,
                APP_PATH
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

                // Set recovery options (restart on failure)
                processBuilder = new ProcessBuilder(
                    "sc",
                    "failure",
                    SERVICE_NAME,
                    "reset=86400",
                    "actions=restart/60000/restart/60000/restart/60000"
                );
                
                process = processBuilder.start();
                process.waitFor();

                System.out.println("Service installed successfully");
            } else {
                System.err.println("Failed to install service. Error code: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void uninstallService() {
        try {
            // Stop the service first
            ProcessBuilder stopProcess = new ProcessBuilder(
                "sc",
                "stop",
                SERVICE_NAME
            );
            stopProcess.start().waitFor();

            // Delete the service
            ProcessBuilder deleteProcess = new ProcessBuilder(
                "sc",
                "delete",
                SERVICE_NAME
            );
            
            Process process = deleteProcess.start();
            int result = process.waitFor();

            if (result == 0) {
                System.out.println("Service uninstalled successfully");
            } else {
                System.err.println("Failed to uninstall service. Error code: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startService() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "sc",
                "start",
                SERVICE_NAME
            );
            
            Process process = processBuilder.start();
            int result = process.waitFor();

            if (result == 0) {
                System.out.println("Service started successfully");
            } else {
                System.err.println("Failed to start service. Error code: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopService() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "sc",
                "stop",
                SERVICE_NAME
            );
            
            Process process = processBuilder.start();
            int result = process.waitFor();

            if (result == 0) {
                System.out.println("Service stopped successfully");
            } else {
                System.err.println("Failed to stop service. Error code: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            APP_PATH + "/" + "FSDT.jar",
            "--spring.config.location=" + APP_PATH + "/application.properties"
        );
        
        // Set working directory
        processBuilder.directory(new File(APP_PATH));
        
        // Redirect error and output to files
        processBuilder.redirectError(new File(APP_PATH + "/logs/error.log"));
        processBuilder.redirectOutput(new File(APP_PATH + "/logs/output.log"));
        
        // Start the process
        Process process = processBuilder.start();
        
        // Add shutdown hook to stop the process when service stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (process.isAlive()) {
                process.destroy();
            }
        }));
        
        // Wait for the process
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Application exited with code: " + exitCode);
        }
    } catch (Exception e) {
        throw new RuntimeException("Failed to start application", e);
    }
}
}
