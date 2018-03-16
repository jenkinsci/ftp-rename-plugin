package org.jenkinsci.plugins.ftpRename;
import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import hudson.EnvVars;
import hudson.slaves.*;

public class FtpRenameTest {
	FtpHandler ftpHandler = new FtpHandler();	
	GlobalEnvironment gblEnv = new GlobalEnvironment();
	EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
	
	private void addVariable(String key, String value) throws IOException {        		
		EnvVars envVars = prop.getEnvVars();
		envVars.put(key, value);        
	}

	@Test
	public void testSimpleReplacement() throws IOException {
		addVariable("globalKey", "globalValue");
		String value = "$globalKey";
		assertNotEquals(value, gblEnv.replaceGlobalVars(value, prop.getEnvVars()));
	}
	
	@Test
	public void testSimpleBracesReplacement() throws IOException {
		addVariable("globalKey", "globalValue");
		String value = "${globalKey}";
		assertNotEquals(value, gblEnv.replaceGlobalVars(value, prop.getEnvVars()));
	}

	@Test
	public void testSpacesBracesReplacement() throws IOException {
		addVariable("global Key", "global Value");
		String value = "${global Key}";
		assertNotEquals(value, gblEnv.replaceGlobalVars(value, prop.getEnvVars()));
	}

	@Test
	public void testMissingVariable() throws IOException {
		addVariable("globalKey", "globalValue");
		String value = "${wrong Global Key}";
		assertEquals(value, gblEnv.replaceGlobalVars(value, prop.getEnvVars()));
	}

	@Test
	public void testBlankVariable() throws IOException {
		addVariable("globalKey", "globalValue");
		String value = "${}";
		assertEquals(value, gblEnv.replaceGlobalVars(value, prop.getEnvVars()));
	}
	
	@Test
	public void testChangeRemoteDirectory() {
		assertTrue(ftpHandler.openConnection("ftp.dlptest.com", 21, "dlpuser@dlptest.com", "eiTqR7EMZD5zy7M", true));
		assertTrue(ftpHandler.changeDirectory("/control"));
        assertTrue(ftpHandler.closeConnection());		    
	}
	@Test
	public void testRenameFile() {
		assertTrue(ftpHandler.openConnection("ftp.dlptest.com", 21, "dlpuser@dlptest.com", "eiTqR7EMZD5zy7M", true));
		assertTrue(ftpHandler.changeDirectory("/control/"));
        assertTrue(ftpHandler.renameFtpFile("control.ocx", "control_New.ocx", "/control/"));
        assertTrue(ftpHandler.closeConnection());
	}
}