package com.fsdt.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "service")
public class ServiceConfig {
    private String name = "FSDTService";
    private String displayName = "FSDT Application Service";
    private String description = "Windows Service for FSDT Application";
    private String appPath = "C:\\user\\shaswat";
    private String jarName = "FSDT.jar";
    private String javaOpts = "-Xmx512m -Dlog4j.configurationFile=config/log4j2.xml -Djavax.net.ssl.trustStore=cert/176-solace.jks";
}
