/**
 * @author Bruno Cardoso Cantisano
 */
package org.jenkinsci.plugins.ftpRename;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
public class FtpHandler {
    private FTPClient ftp;
    
    public FtpHandler(){
		this.ftp = new FTPClient();
    }
	/**
     * Open Connection Method.
     *
     * @param ftpServer server
     * @param ftpPort ftp port
     * @param ftpUser user
     * @param ftpPassword password
     * @param passiveMode passive mode
     * @return connection was succeed or had an error
     */
    public boolean openConnection(String ftpServer, int ftpPort, String ftpUser, String ftpPassword, boolean passiveMode){       	
        try {
            ftp.connect(ftpServer,ftpPort);
            int replyCode = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return false;
            }
            boolean success = ftp.login(ftpUser,  ftpPassword);
            if (!success) {
                return false;
            }
            if (passiveMode) {
                ftp.enterLocalPassiveMode();
            } else {
                ftp.enterLocalActiveMode();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
		return true;
    }
	/**
     * Change directory Method.
     *
     * @param pathFtp path ftp server
     */
    public boolean changeDirectory(String pathFtp){
        try {
            // Changes working directory
            return ftp.changeWorkingDirectory(pathFtp);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		return false;
    }

	/**
     * Change directory Method.
     *
     * @param pathFile path file
     */
    public boolean fileExists(String pathFile){
        try {
            // Changes working directory
            return ftp.listFiles(pathFile).length != 0 ? true : false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		return false;
    }
    
    public boolean uploadFile(File localFile, String remoteFile) {
    	boolean completed = false;
    	InputStream inputStream = null;
    	OutputStream outputStream = null;
    	try {
        	ftp.setFileType(FTP.BINARY_FILE_TYPE);
        	inputStream = new FileInputStream(localFile);
        	outputStream = ftp.storeFileStream(remoteFile);
        	byte[] bytesIn = new byte[4096];
        	int read = 0;
        	while ((read = inputStream.read(bytesIn)) != -1) {
        	    outputStream.write(bytesIn, 0, read);
        	}
    	}
    	catch(FileNotFoundException e1) {
    		completed = false;
    	}
    	catch(IOException e1) {
    		completed = false;
    	}
    	finally {
    		try {
                if (inputStream != null) {
                	inputStream.close();
                }

                if (outputStream != null) {
                	outputStream.close();
                }                
            	//The file was uploaded successfully.
            	completed = ftp.completePendingCommand();
    		}
    		catch(Exception e2) {
    			completed=false;
    		}
        }
    	return completed;
    }
    public long getFileSize(String remoteFile) {
	   long fileSize = 0;
	   try {
		   FTPFile[] files = ftp.listFiles(); //ftp list
		   for ( int i = 0; i < files.length; i++)
		   { //ftp connect forder in files              
		      if (remoteFile.equals(files[i].getName()))
		      { 
		        fileSize = files[i].getSize();
		        return fileSize;
		      }
		   }
	   }
	   catch(IOException e) {
		   return fileSize;
	   }

	   return fileSize;
    }
    /**
     * Rename file Method.
     *
     * @param artifactName String - path old filename.
     * @param newArtifactName String - path new filename.
     * @param ftpDirectory String - default path in the ftp server.
     * @return upload was succeed or had an error
     */
	public boolean renameFtpFile(String artifactName, String newArtifactName, String ftpDirectory){
		String artifactNameTemp = "tempfile."+FilenameUtils.getExtension(artifactName);
		artifactName = ftpDirectory + artifactName;
		newArtifactName = ftpDirectory + newArtifactName;
		try{
			if(!fileExists(artifactName) && !fileExists(newArtifactName)) { 
				return false;
			}else{
				if(fileExists(artifactName) && !fileExists(newArtifactName)) { //nao existe
					if (!ftp.rename(artifactName, newArtifactName)){
						return false;
					}
				}else if(fileExists(newArtifactName) &&	!fileExists(artifactName)){
					if (!ftp.rename(newArtifactName, artifactName)){
						return false;
					}
				}else if(fileExists(artifactName) && fileExists(newArtifactName)) { //existem os dois
					if (!ftp.rename(newArtifactName, artifactNameTemp )){
						return false;
					}
					if (!ftp.rename(artifactName, newArtifactName)){
						return false;
					}
					if (!ftp.rename(artifactNameTemp, artifactName)){
						return false;
					}
				}
			}
		}
		catch(Exception e){
			return false;
		}
		return true;
	}

    /**
     * Close Connection Method.
     *
     * @return close connection was succeed or had an error
     */
    public boolean closeConnection(){
		try{
			ftp.logout();
			ftp.disconnect();
		}
		catch(Exception e){
			return false;
		}
		return true;
    }
}
