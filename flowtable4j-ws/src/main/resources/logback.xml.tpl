<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoding>UTF-8</encoding>
        <encoder><!-- 必须指定，否则不会往文件输出内容 -->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="fileAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoding>UTF-8</encoding>
        <file>/opt/logs/tomcat/flowtable4j-ws.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/logs/tomcat/flowtable4j-ws.log.%d{yyyy-MM-dd}</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder><!-- 必须指定，否则不会往文件输出内容 -->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Output to central logging -->
    <appender name="CLoggingAppender" class="com.ctrip.framework.clogging.agent.appender.CLoggingAppender">
        <appId>100000807</appId>
        <serverIp>{$CLogging.serverIp}</serverIp>
        <serverPort>{$CLogging.serverPort}</serverPort>
    </appender>

    <logger name="com.ctrip.infosec.flowtable4j" additivity="false">
        <level value="INFO" />
        <appender-ref ref="STDOUT" />
        <appender-ref ref="fileAppender" />
        <appender-ref ref="CLoggingAppender" />
    </logger>
    <logger name="com.ctrip.infosec.sars.monitor" additivity="false">
        <level value="ERROR" />
        <appender-ref ref="STDOUT" />
        <appender-ref ref="fileAppender" />
        <appender-ref ref="CLoggingAppender" />
    </logger>

    <logger name="org.springframework">
        <level value="ERROR" />
    </logger>

    <logger name="org.mybatis">
        <level value="WARN" />
    </logger>

    <logger name="java.sql">
        <level value="WARN" />
    </logger>

    <logger name="org.apache.commons">
        <level value="ERROR" />
    </logger>

    <logger name="org.eclipse.jetty">
        <level value="INFO" />
    </logger>

    <root level="WARN">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="fileAppender" />
        <appender-ref ref="CLoggingAppender" />
    </root>

</configuration>

