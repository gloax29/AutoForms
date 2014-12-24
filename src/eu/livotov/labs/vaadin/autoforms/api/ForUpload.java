/**
 * 
 */
package eu.livotov.labs.vaadin.autoforms.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;

/**
 * @author Gloax29 02/10/2014
 *
 */
public class ForUpload implements Receiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File objectUpload;
		
	public ForUpload(File objectUpload ) {
		
		this.objectUpload = objectUpload ;
	}
	

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		if(filename != null && filename != ""){
		 FileOutputStream fos = null; // Stream to write to
	        try {
	            // Open the file for writing.
	        	objectUpload = new File(filename);
	            fos = new FileOutputStream(objectUpload);
	        } catch (final java.io.FileNotFoundException e) {
	            new Notification("Could not open file<br/>",
	                             e.getMessage(),
	                             Notification.Type.ERROR_MESSAGE)
	                .show(Page.getCurrent());
	            return null;
	        }
	        return fos;
		}
		  return null;
	
	}

	
	/**
	 * @return the file
	 */
	public File getFile() {
		return objectUpload;
	}



	


	

}
