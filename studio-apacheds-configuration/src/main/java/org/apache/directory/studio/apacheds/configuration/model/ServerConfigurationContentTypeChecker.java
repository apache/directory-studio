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
package org.apache.directory.studio.apacheds.configuration.model;


import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * This class represents the Server Configuration Content Type Checker. 
 * It is used to check if a file correspond to a correct Apache DS 'server.xml' configuration.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationContentTypeChecker
{
    /**
     * Checks if the InputStream is valid.
     *
     * @param inputStream
     *      the InputStream
     * @return
     *      true if the InputStream is valid, false if not
     */
    public static boolean isValid( InputStream inputStream )
    {
        try
        {
            SAXReader saxReader = new SAXReader();

            return isValid( saxReader.read( inputStream ) );
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /**
     * Checks if the InputStream is valid.
     *
     * @param inputStream
     *      the InputStream
     * @return
     *      true if the InputStream is valid, false if not
     */
    public static boolean isValid( Reader reader )
    {
        try
        {
            SAXReader saxReader = new SAXReader();

            return isValid( saxReader.read( reader ) );
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /**
     * Checks if the Document is valid.
     *
     * @param document
     *      the Document
     * @return
     *      true if the Document is valid, false if not
     */
    private static boolean isValid( Document document )
    {
        Element configurationBean = getBeanElementById( document, "configuration" );
        if ( configurationBean == null )
        {
            return false;
        }
        else
        {
            Attribute classAttribute = configurationBean.attribute( "class" );
            if ( classAttribute == null )
            {
                return false;
            }
            else
            {
                return "org.apache.directory.server.configuration.MutableServerStartupConfiguration"
                    .equals( classAttribute.getValue() );
            }
        }
    }


    /**
     * Gets the Bean element corresponding to the given ID.
     *
     * @param document
     *      the document to use
     * @param id
     *      the id
     * @return
     *       the Bean element corresponding to the given ID or null if the bean was not found
     */
    private static Element getBeanElementById( Document document, String id )
    {
        for ( Iterator<?> i = document.getRootElement().elementIterator( "bean" ); i.hasNext(); )
        {
            Element element = ( Element ) i.next();
            Attribute idAttribute = element.attribute( "id" );
            if ( idAttribute != null && ( idAttribute.getValue().equals( id ) ) )
            {
                return element;
            }
        }

        return null;
    }
}
