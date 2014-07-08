package com.owteam.engUtils;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.classic.log4j.XMLLayout;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;

/**
 *
 * @author bwadleigh
 */
public class EngLog {

	public static void clearAppenders() {
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.detachAndStopAllAppenders();
	}

	public static void addFileAppender() {

		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		root.setLevel(Level.TRACE);

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		String logName = "englog";
		String logSize = "10MB";
		int logHistory = 10;
		String logDir = System.getProperty("java.io.tmpdir");

		if (System.getProperty("script.name") != null) {
			logName = System.getProperty("script.name");
		}
		if (System.getProperty("englog.name") != null) {
			logName = System.getProperty("englog.name");
		}
		if (System.getProperty("englog.dir") != null) {
			logDir = System.getProperty("englog.dir");
		}
		if (System.getProperty("englog.maxFileSize") != null) {
			logDir = System.getProperty("englog.maxFileSize");
		}
		if (System.getProperty("englog.maxFiles") != null) {
			logHistory = Integer.parseInt(System.getProperty("englog.maxFiles"));
		}

		encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		encoder.start();

		TimeBasedRollingPolicy rollPolicy = new TimeBasedRollingPolicy();
		SizeAndTimeBasedFNATP sizePolicy = new SizeAndTimeBasedFNATP();
		sizePolicy.setMaxFileSize("10MB");
		rollPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizePolicy);

		rollPolicy.setFileNamePattern("/tmp/" + logName + "-%d{yyyy-MM-dd}.%i.log");
		rollPolicy.setMaxHistory(3);

		RollingFileAppender rollingFileAppenderTrace = new RollingFileAppender();
		rollingFileAppenderTrace.setName(logName + "-Log");
		rollingFileAppenderTrace.setFile("/tmp/" + logName + ".log");
		rollingFileAppenderTrace.setEncoder(encoder);
		rollingFileAppenderTrace.setRollingPolicy(rollPolicy);
		rollPolicy.setParent(rollingFileAppenderTrace);
		rollPolicy.setContext(loggerContext);
		rollPolicy.start();
		rollingFileAppenderTrace.start();
		root.addAppender(rollingFileAppenderTrace);

	}

	public static void addConsoleAppender() {

		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.TRACE);
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		encoder.setContext(loggerContext);
		encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		encoder.start();
		ConsoleAppender stderrAppender = new ConsoleAppender();
		stderrAppender.setWithJansi(true);
		stderrAppender.setEncoder(encoder);
		stderrAppender.start();
		root.addAppender(stderrAppender);

	}
}
