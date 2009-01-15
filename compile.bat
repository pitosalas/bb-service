@set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_06\
@set PATH=%JAVA_HOME%\bin;%PATH%
@maven clean war
rem copy target/bbservice.war C:\Development\Tools\apache-tomcat-5.5.16\webapps\