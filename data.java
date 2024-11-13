# Service Configuration
service.name=FSDTService
service.display-name=FSDT Application Service
service.description=Windows Service for FSDT Application
service.app-path=C:\\user\\shaswat
service.jar-name=FSDT.jar
service.java-opts=-Xmx512m -Dlog4j.configurationFile=config/log4j2.xml -Djavax.net.ssl.trustStore=cert/176-solace.jks

# Logging Configuration
logging.file.name=${service.app-path}/logs/service-wrapper.log
logging.level.root=INFO
logging.level.com.fsdt=DEBUG

# Spring Configuration
spring.main.banner-mode=console
spring.main.log-startup-info=true
