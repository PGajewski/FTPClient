package com.gajos.ftp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FTPProject implements Serializable{

	/**
	 * Static variable of version for serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Version of object.
	 */
	private long versionUID;

	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();
	
	/**
	 * List of clients in project.
	 */
	private List<FTPClient> clientsList = new ArrayList<>();

	/**
	 * Actual project filePath.
	 */
	private String filePath;
	
	/**
	 * Public contructor.
	 */
	public FTPProject()
	{
		versionUID = serialVersionUID;
	}
	
	/**
	 * Method to deserialize project from file.
	 * @param path Path to serialized object
	 */
	public void deserialize(String path) throws FTPException
	{
		FTPProject temp = null;
        // Deserialization 
        try
        {    
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(path); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            temp = (FTPProject)in.readObject(); 
            
            //Close streams.
            in.close(); 
            file.close();
            
            //Check object compatibility.
            if (temp.versionUID != FTPProject.serialVersionUID)
            	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().incompatibleFile());
            
            //Copy variables to new object.
            this.clientsList = new ArrayList<>(temp.clientsList);
            this.filePath = path;
            			
        } 
          
        catch(IOException ex) 
        { 
        	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().deserializationError());
        }
        catch(ClassNotFoundException ex) 
        { 
            //Never happend.
        	throw new FTPException(FTPStatus.ERROR, null);
        } 
	}
	
	/**
	 * Static method to serialize project to file.
	 * @param project Project to serialize.
	 * @param path Path to file for save project.
	 */
	public void serialize(String path)  throws FTPException
	{
		// Serialization  
        try
        {    
            //Saving of object in a file 
            FileOutputStream file = new FileOutputStream(path); 
            ObjectOutputStream out = new ObjectOutputStream(file); 
              
            // Method for serialization of object 
            out.writeObject(this); 
              
            out.close(); 
            file.close(); 
        }   
        catch(IOException ex) 
        { 
            throw new FTPException(FTPStatus.ERROR, lc.getLanguage().serializationError());
        } 
	}
	
	/**
	 * Method to add new FTPClient to project.
	 * @param client 
	 */
	public void addServerEndpoint(FTPClient client)
	{
		this.clientsList.add(client);
	}
	
	public void setFilePath(String path)
	{
		this.filePath = path;
	}
	
	public String getFilePath()
	{
		return this.filePath;
	}
}
