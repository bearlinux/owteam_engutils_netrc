package com.owteam.engUtils.netrc;

import com.owteam.engUtils.netrc.Netrc;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.auth.Credentials;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bwadleigh
 */
public class NetrcTest {

	String tmpFile_oneLine_Path;
	File tmpFile_oneLine_File;
	String tmpFile_multiLine_Path;
	File tmpFile_multiLine_File;

	public NetrcTest() {

	}

	@BeforeClass
	public static void setUpClass() {

	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		try {
			tmpFile_oneLine_File = File.createTempFile("com.owteam.engutils.NetrcTest", ".netrc");
			tmpFile_multiLine_File = File.createTempFile("com.owteam.engutils.NetrcTest", ".netrc");
			tmpFile_oneLine_Path = tmpFile_oneLine_File.getPath();
			tmpFile_multiLine_Path = tmpFile_multiLine_File.getPath();
			FileWriter fw = new FileWriter(tmpFile_oneLine_File);
			fw.append("machine foo1 login log1 password pass1!@#$%^&*()-_=[]{};:'\",.<>/?|\\~` account acct1\n");
			fw.append("machine foo2 login log2 password pass2!@#$%^&*()-_=[]{};:'\",.<>/?|\\~` account acct2\n");
			fw.append("machine foo3 login log3 password pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~` account acct3\n");
			fw.append("default login logdefault password passdefault\n");
			fw.flush();
			fw.close();
			fw = new FileWriter(tmpFile_multiLine_File);
			fw.append("machine foo1 ");
			fw.append(" \t login log1");
			fw.append("   \t\t\t password pass1!@#$%^&*()-_=[]{};:'\",.<>/?|\\~` \t \t\t   ");
			fw.append("\t \taccount acct1\n");
			fw.append("\n");
			fw.append("machine foo2 ");
			fw.append(" \t login log2");
			fw.append("   \t\t\t password pass2!@#$%^&*()-_=[]{};:'\",.<>/?|\\~` \t \t\t   ");
			fw.append("\t \taccount acct2\n");
			fw.append("\n");
			fw.append("machine foo3 login log3 password pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~` account acct3\n");
			fw.append("\n");
			fw.append("macdef macro1\n");
			fw.append("\tline 1");
			fw.append("    line 2");
			fw.append("");
			fw.append("macdef macro2");
			fw.append("    \t line3");
			fw.append("\t    line 4");
			fw.append("\n");
			fw.append("\n");
			fw.append("default login logdefault password passdefault\n");
			fw.flush();
			fw.close();

		} catch (IOException ex) {
			Logger.getLogger(NetrcTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@After
	public void tearDown() {
		tmpFile_oneLine_File.delete();
		tmpFile_multiLine_File.delete();
	}

	/**
	 * Test of getInstance method, of class Netrc.
	 */
	@Test
	public void testGetInstance_String() {
		System.out.println("getInstance");
		Netrc result = Netrc.getInstance(tmpFile_oneLine_Path);
		assertEquals("pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", result.getCredentials("foo3").getPassword());
	}

	/**
	 * Test of getInstance method, of class Netrc.
	 */
	@Test
	public void testGetInstance_File() {
		System.out.println("getInstance");
		Netrc result = Netrc.getInstance(tmpFile_oneLine_File);
		assertEquals("pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", result.getCredentials("foo3").getPassword());
		Netrc result2 = Netrc.getInstance(tmpFile_multiLine_File);
		assertEquals("pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", result2.getCredentials("foo3").getPassword());
	}

	/**
	 * Test of getCredentials method, of class Netrc.
	 */
	@Test
	public void testGetCredentials() {
		System.out.println("getCredentials");
		Netrc netrc1 = Netrc.getInstance(tmpFile_oneLine_File);
		Netrc netrc2 = Netrc.getInstance(tmpFile_multiLine_File);
		assertNotNull(netrc1.getCredentials(""));
		assertNotNull(netrc2.getCredentials(""));
		assertEquals("logdefault", netrc1.getCredentials("").getUserPrincipal().getName());
		assertEquals("logdefault", netrc2.getCredentials("").getUserPrincipal().getName());
		assertEquals("log1", netrc1.getCredentials("foo1").getUserPrincipal().getName());
		assertEquals("pass1!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", netrc1.getCredentials("foo1").getPassword());
		assertEquals("pass2!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", netrc1.getCredentials("foo2").getPassword());
		assertEquals("pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", netrc1.getCredentials("foo3").getPassword());
		assertEquals("pass1!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", netrc2.getCredentials("foo1").getPassword());
		assertEquals("pass2!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", netrc2.getCredentials("foo2").getPassword());
		assertEquals("pass3!@#$%^&*()-_=[]{};:'\",.<>/?|\\~`", netrc2.getCredentials("foo3").getPassword());
	}

}
