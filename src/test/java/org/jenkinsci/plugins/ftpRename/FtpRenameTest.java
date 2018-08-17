package org.jenkinsci.plugins.ftpRename;
import static org.junit.Assert.*;

import java.io.File;
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
		assertTrue(ftpHandler.openConnection("ftp.dlptest.com", 21, "dlpuser@dlptest.com", "3D6XZV9MKdhM5fF", true));
		assertTrue(ftpHandler.changeDirectory("/"));
		assertTrue(ftpHandler.closeConnection());
	}
	@Test
	/**
	 * Public FTP test site info is below and can be used to upload test DLP files.
     * The files will only be stored for 5 minutes before being deleted.
     * https://dlptest.com/ftp-test/
	 */
	public void testRenameFile() {
		assertTrue(ftpHandler.openConnection("ftp.dlptest.com", 21, "dlpuser@dlptest.com", "3D6XZV9MKdhM5fF", true));
		assertTrue(ftpHandler.changeDirectory("/"));
		FileHandler tempFile = new FileHandler();
		File file1 = tempFile.createFile("./teste1.txt", "testeteste");
		File file2 = tempFile.createFile("./teste2.txt", "opa");
		assertTrue(ftpHandler.uploadFile(file1, "teste1.txt"));
		assertTrue(ftpHandler.uploadFile(file2, "teste2.txt"));
		//renaming...
        assertTrue(ftpHandler.renameFtpFile("teste1.txt", "teste2.txt", "/"));

        //check sizes
        assertEquals(3, ftpHandler.getFileSize("teste1.txt"));
        assertEquals(10, ftpHandler.getFileSize("teste2.txt"));

        //removing local files
        assertTrue(file1.delete());
        assertTrue(file2.delete());

        assertTrue(ftpHandler.closeConnection());
    }
}
