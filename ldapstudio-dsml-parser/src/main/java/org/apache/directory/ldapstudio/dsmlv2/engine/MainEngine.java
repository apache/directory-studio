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

package org.apache.directory.ldapstudio.dsmlv2.engine;

import java.io.FileNotFoundException;

import org.xmlpull.v1.XmlPullParserException;


/**
 * This class is the Main Engine for DSMLv2.
 */
public class MainEngine
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        Dsmlv2Engine engine = new Dsmlv2Engine( "localhost", 10389, "uid=admin, ou=system", "secret" );
        
        try
        {
            System.out.println( engine.processDSMLFile( "CompleteBatchRequest.xml" ) );
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( XmlPullParserException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
