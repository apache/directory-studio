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
package org.apache.directory.studio.apacheds.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * This class implements an {@link EntityResolver} used for XML parsing. 
 * 
 * Currently a zero-length character stream is returned. This will prevent 
 * lookups to the internet for public entities (i.e. DTDs) in XML documents
 * that are passed to the {@link ApacheDSConfigurationContentDescriber}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioEntityResolver implements EntityResolver
{

    public InputSource resolveEntity( String publicId, String systemId ) throws SAXException, IOException
    {
        InputSource is;
        if ( publicId.equalsIgnoreCase( "-//SPRING//DTD BEAN//EN" ) ) //$NON-NLS-1$
        {
            // Assigning the Spring Beans DTD to an entity resolver
            // (This will prevent the parser to try to get it online)
            InputStream in = ApacheDSConfigurationPlugin.class.getResourceAsStream( "spring-beans.dtd" ); //$NON-NLS-1$
            is = new InputSource( in );
        }
        else
        {
            is = new InputSource( new StringReader( "" ) );
        }

        is.setSystemId( systemId );
        is.setPublicId( publicId );
        return is;
    }

}
