/**
 * @author Bruno Cardoso Cantisano
 */
package org.jenkinsci.plugins.ftpRename;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
public class FileHandler {
	
	public FileHandler() {
		
	}
	public File createFile(String name, String text){
		OutputStreamWriter writer = null;
		File file = null;
	    try {
	        //create a temporary file
	        file = new File(name);
	        writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	        PrintWriter pw = new PrintWriter(writer);
	        pw.print(text);
	        pw.close();	       	       
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
