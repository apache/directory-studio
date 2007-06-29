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

import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SchemaImpl;

public class FakeLoader
{
    public static void loadSchemas()
    {
        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        
        Schema schema1 = new SchemaImpl("schema1");
        AttributeTypeImpl at1 = new AttributeTypeImpl("1.2.3.4.1");
        at1.setNames( new String[] { "at1" } );
        at1.setSchema( schema1.getName() );
        schema1.addAttributeType( at1 );
        
        AttributeTypeImpl at2 = new AttributeTypeImpl("1.2.3.4.2");
        at2.setNames( new String[] { "at2", "attributeType2" } );
        at2.setSchema( schema1.getName() );
        schema1.addAttributeType( at2 );
        
        ObjectClassImpl oc1 = new ObjectClassImpl("1.2.3.4.3");
        oc1.setNames(new String[] { "oc1", "objectClass1" } );
        oc1.setSchema( schema1.getName() );
        schema1.addObjectClass( oc1 );
        
        schemaHandler.addSchema( schema1 );
        schemaHandler.addListener( new SchemaHandlerAdapter(){

            public void schemaAdded( Schema schema )
            {
                System.out.println("schemaAdded");
            }
            
        });
    }
}
