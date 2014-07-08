/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.owteam.engUtils;

import ch.qos.logback.classic.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bwadleigh
 */
public class EngLogTest {

	public EngLogTest() {
	}

	/**
	 * Test of addFileAppender method, of class EngLog.
	 */
	@Test
	public void testAddFileAppender() {
		System.out.println("addFileAppender");
		EngLog.addFileAppender();
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.error("testing");
	}

	/**
	 * Test of addConsoleAppender method, of class EngLog.
	 */
	@Test
	public void testAddConsoleAppender() {
		System.out.println("addConsoleAppender");
		EngLog.addConsoleAppender();
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.error("testing");
	}

}
