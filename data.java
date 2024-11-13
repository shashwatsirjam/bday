Create a Windows Service Wrapper:

Download the Java Service Wrapper (also known as "prunsrv") from the official website: https://wrapper.tanukisoftware.com/doc/english/download.jsp
Extract the downloaded ZIP file to a directory of your choice.


Configure the Service Wrapper:

Open the conf/wrapper.conf file in the extracted directory.
Modify the following properties to match your SpringBoot application:

wrapper.app.path: Specify the path to your SpringBoot application JAR file.
wrapper.java.command: Specify the path to your Java executable (e.g., %JAVA_HOME%\bin\java.exe).
wrapper.java.mainclass: Set this to org.springframework.boot.loader.JarLauncher.
wrapper.console.loglevel: Set the log level for the service (e.g., INFO).




Install the Windows Service:

Open an administrative command prompt or PowerShell window.
Navigate to the extracted Java Service Wrapper directory.
Run the following command to install the service:
Copybin\prunsrv.exe //IS//MySpringBootService
Replace MySpringBootService with a name that identifies your service.


Start the Windows Service:

In the same command prompt or PowerShell window, run the following command to start the service:
Copybin\prunsrv.exe //ES//MySpringBootService



Verify the Service:

Open the Windows Services management console (search for "Services" in the Start menu).
You should see your "MySpringBootService" listed and running.
