package org.apache.directory.studio;

import javax.naming.NamingException;

public class Main 
{

	
	public static void main(String[] args)
	{
		try
		{
			SchemaImporter schemaImporter = new SchemaImporter();
			schemaImporter.getServerSchema();
			
			
		}
		catch (NamingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	

}
