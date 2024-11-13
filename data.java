package com.fsdt.service;

import com.fsdt.service.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
public class WindowsServiceWrapper {

    @Autowired
    private ServiceConfig serviceConfig;

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        try {
            context = SpringApplication.run(WindowsServiceWrapper.class, args);
            WindowsServiceWrapper wrapper = context.getBean(WindowsServiceWrapper.class);

            if (args.length > 0) {
                wrapper.handleCommand(args[0]);
            } else {
                wrapper.runApplication();
            }
        } catch (Exception e) {
            log.error("Service error: ", e);
            if (context != null) {
                context.close();
            }
            System.exit(1);
        }
    }

    private void handleCommand(String command) throws IOException, InterruptedException {
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
    }

    private void installService() throws IOException, InterruptedException {
        // Verify required files exist
        verifyRequiredFiles();

        String servicePath = String.format(
            "java %s -jar \"%s\\%s\" --spring.config.location=%s\\application.properties",
            serviceConfig.getJavaOpts(),
            serviceConfig.getAppPath(),
            serviceConfig.getJarName(),
            serviceConfig.getAppPath()
        );

        ProcessBuilder processBuilder = new ProcessBuilder(
            "sc",
            "create",
            serviceConfig.getName(),
            "binPath=" + servicePath,
            "start=auto",
            "DisplayName=" + serviceConfig.getDisplayName()
        );
        
        executeCommand(processBuilder, "Failed to create service");

        // Set description
        processBuilder = new ProcessBuilder(
            "sc",
            "description",
            serviceConfig.getName(),
            serviceConfig.getDescription()
        );
        
        executeCommand(processBuilder, "Failed to set service description");

        // Set recovery options
        processBuilder = new ProcessBuilder(
            "sc",
            "failure",
            serviceConfig.getName(),
            "reset=86400",
            "actions=restart/60000/restart/60000/restart/60000"
        );
        
        executeCommand(processBuilder, "Failed to set service recovery options");

        log.info("Service installed successfully");
    }

    private void verifyRequiredFiles() {
        String appPath = serviceConfig.getAppPath();
        String[] requiredFiles = {
            serviceConfig.getJarName(),
            "application.properties",
            "config/log4j2.xml",
            "cert/176-solace.jks"
        };

        for (String file : requiredFiles) {
            File f = new File(appPath, file);
            if (!f.exists()) {
                throw new RuntimeException("Required file not found: " + f.getAbsolutePath());
            }
        }
    }

    private void uninstallService() throws IOException, InterruptedException {
        // Stop service first
        ProcessBuilder stopProcess = new ProcessBuilder(
            "sc",
            "stop",
            serviceConfig.getName()
        );
        executeCommand(stopProcess, "Failed to stop service");

        // Delete service
        ProcessBuilder deleteProcess = new ProcessBuilder(
            "sc",
            "delete",
            serviceConfig.getName()
        );
        executeCommand(deleteProcess, "Failed to delete service");

        log.info("Service uninstalled successfully");
    }

    private void startService() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "sc",
            "start",
            serviceConfig.getName()
        );
        executeCommand(processBuilder, "Failed to start service");
        log.info("Service started successfully");
    }

    private void stopService() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "sc",
            "stop",
            serviceConfig.getName()
        );
        executeCommand(processBuilder, "Failed to stop service");
        log.info("Service stopped successfully");
    }

    private void executeCommand(ProcessBuilder processBuilder, String errorMessage) 
            throws IOException, InterruptedException {
        Process process = processBuilder.start();
        int result = process.waitFor();
        if (result != 0) {
            throw new RuntimeException(errorMessage + ". Error code: " + result);
        }
    }

    private void runApplication() {
        try {
            // Set system properties
            System.setProperty("log4j.configurationFile", "config/log4j2.xml");
            System.setProperty("javax.net.ssl.trustStore", "cert/176-solace.jks");
            
            // Set application properties location
            String propertiesPath = Paths.get(serviceConfig.getAppPath(), "application.properties").toString();
            System.setProperty("spring.config.location", propertiesPath);
            
            log.info("Starting FSDT application...");
            
            // Start the actual FSDT application
            SpringApplication.run(FSDTServiceApplication.class);
            
            log.info("FSDT application started successfully");
        } catch (Exception e) {
            log.error("Failed to start FSDT application", e);
            throw e;
        }
    }
}
