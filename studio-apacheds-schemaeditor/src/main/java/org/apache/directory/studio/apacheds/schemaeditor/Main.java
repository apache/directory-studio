/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.studio.apacheds.schemaeditor;


import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;


public class Main
{

    public static void main( String[] args )
    {
        try
        {
            SchemaImporter schemaImporter = new SchemaImporter();
            List<Schema> schemas = schemaImporter.getServerSchema();

            for ( Schema schema : schemas )
            {
                FileOutputStream os = new FileOutputStream("/Users/pajbam/Desktop/test/" + schema.getName() +".xml");
                XMLEncoder encoder = new XMLEncoder(os);
                encoder.writeObject( schema.getAttributeTypes() );
                encoder.close();
            }

        }
        catch ( NamingException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
