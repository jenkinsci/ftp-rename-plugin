/**
 * @author Bruno Cardoso Cantisano
 */
package org.jenkinsci.plugins.ftpRename;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
public class FileHandler {
	
	public FileHandler() {
		
	}
	public File createFile(String name, String text){
		BufferedWriter writer = null;
		File file = null;
	    try {
	        //create a temporary file
	        file = new File(name);
	        writer = new BufferedWriter(new FileWriter(file));
	        writer.write(text);
	    } catch (Exception e) {
	        e.printStackTrace();	        
	    } finally {
	        try {
	            writer.close();
	        } catch (Exception e) {
	        }	        
	    }
	    return file;
	}
}
