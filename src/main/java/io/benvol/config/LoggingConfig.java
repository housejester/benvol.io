package io.benvol.config;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class LoggingConfig {

    public static void configure(String environment) {
        Properties p = new Properties();
        if (environment.equals("local")) {
            p.put("log4j.rootLogger", "INFO, Console");
            p.put("log4j.appender.Console", "org.apache.log4j.ConsoleAppender");
            p.put("log4j.appender.Console.layout", "org.apache.log4j.PatternLayout");
            p.put("log4j.appender.Console.layout.conversionPattern", "%d{MM/dd/yyyy HH:mm:ss,SSS} [%t] %p %c %x - %m%n");
        } else {
            p.put("log4j.rootLogger", "INFO, file");
            p.put("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
            p.put("log4j.appender.file.File", "/var/log/belvolio/belvolio.log");
            p.put("log4j.appender.file.MaxBackupIndex", "60");
            p.put("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
            p.put("log4j.appender.file.layout.conversionPattern", "%d{MM/dd/yyyy HH:mm:ss,SSS} [%t] %p %c %x - %m%n");
        }
        PropertyConfigurator.configure(p);
    }
}
