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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.ldif.LdifReader;
import org.apache.directory.shared.util.Strings;
import org.apache.directory.studio.apacheds.configuration.StudioEntityResolver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.osgi.util.NLS;


/**
 * This abstract class implements the {@link ServerXmlIO} class and adds 
 * useful methods for reading and creating XML.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractServerXmlIO implements ServerXmlIO
{
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
    public Element getBeanElementById( Document document, String id )
    {
        for ( Iterator<?> i = document.getRootElement().elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
        {
            Element element = ( Element ) i.next();
            org.dom4j.Attribute idAttribute = element.attribute( "id" ); //$NON-NLS-1$
            if ( idAttribute != null && ( idAttribute.getValue().equals( id ) ) )
            {
                return element;
            }
        }

        return null;
    }


    /**
     * Gets the given property Element in the the bean
     *
     * @param property
     *      the propery
     * @param element
     *      the bean Element
     * @return
     *      the associated property, or null if the property has not been found
     */
    public Element getBeanPropertyElement( String property, Element element )
    {
        for ( Iterator<?> i = element.elementIterator( "property" ); i.hasNext(); ) //$NON-NLS-1$
        {
            Element propertyElement = ( Element ) i.next();
            org.dom4j.Attribute nameAttribute = propertyElement.attribute( "name" ); //$NON-NLS-1$
            if ( nameAttribute != null && ( nameAttribute.getValue().equals( property ) ) )
            {
                return propertyElement;
            }
        }

        return null;
    }


    /**
     * Reads the given property in the Bean and returns its value.
     *
     * @param property
     *      the property
     * @param element
     *      the Bean Element
     * @return
     *      the value of the property, or null if the property has not been found
     */
    public String readBeanProperty( String property, Element element )
    {
        Element propertyElement = getBeanPropertyElement( property, element );
        if ( propertyElement != null )
        {
            org.dom4j.Attribute valueAttribute = propertyElement.attribute( "value" ); //$NON-NLS-1$
            if ( valueAttribute != null )
            {
                return valueAttribute.getValue();
            }

            org.dom4j.Attribute refAttribute = propertyElement.attribute( "ref" ); //$NON-NLS-1$
            if ( refAttribute != null )
            {
                return refAttribute.getValue();
            }
        }

        return null;
    }


    /**
     * Parses the string argument as a boolean.
     *
     * @param s
     *      a String containing the boolean representation to be parsed
     * @return
     *      the boolean value represented by the argument.
     * @throws BooleanFormatException
     *      if the string does not contain a parsable boolean.
     */
    public boolean parseBoolean( String s ) throws BooleanFormatException
    {
        if ( "true".equals( s ) ) //$NON-NLS-1$
        {
            return true;
        }
        else if ( "false".equals( s ) ) //$NON-NLS-1$
        {
            return false;
        }
        else
        {
            throw new BooleanFormatException( NLS.bind(
                Messages.getString( "AbstractServerXmlIO.ErrorNotBoolean" ), new String[] { s } ) ); //$NON-NLS-1$
        }
    }

    /**
     * Thrown to indicate that the application has attempted to convert a string to a boolean, 
     * but that the string does not have the appropriate format.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public class BooleanFormatException extends Exception
    {
        /** The Serial Version UID */
        private static final long serialVersionUID = -6426955193802317452L;


        /**
         * Creates a new instance of BooleanFormatException.
         *
         * @param message
         * @param cause
         */
        public BooleanFormatException( String message )
        {
            super( message );
        }
    }


    /**
     * Reads an entry (without Dn)
     * 
     * @param text
     *            The ldif format text
     * @return An Attributes.
     */
    public Entry readContextEntry( String text )
    {
        StringReader strIn = new StringReader( text );
        BufferedReader in = new BufferedReader( strIn );

        String line = null;
        Entry entry = new DefaultEntry();

        try
        {
            while ( ( line = ( ( BufferedReader ) in ).readLine() ) != null )
            {
                if ( line.length() == 0 )
                {
                    continue;
                }

                String addedLine = line.trim();

                if ( Strings.isEmpty(addedLine) )
                {
                    continue;
                }

                EntryAttribute attribute = LdifReader.parseAttributeValue( addedLine );
                EntryAttribute oldAttribute = entry.get( attribute.getId() );

                if ( oldAttribute != null )
                {
                    oldAttribute.add( attribute.get() );
                    entry.put( oldAttribute );
                }
                else
                {
                    entry.put( attribute );
                }
            }
        }
        catch ( Exception e )
        {
            // Do nothing : we can't reach this point !
        }

        return entry;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#isValid(java.io.InputStream)
     */
    public final boolean isValid( InputStream is )
    {
        try
        {
            SAXReader saxReader = new SAXReader();
            saxReader.setEntityResolver( new StudioEntityResolver() );

            return isValid( saxReader.read( is ) );
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#isValid(java.io.Reader)
     */
    public final boolean isValid( Reader reader )
    {
        try
        {
            SAXReader saxReader = new SAXReader();
            saxReader.setEntityResolver( new StudioEntityResolver() );

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
    protected abstract boolean isValid( Document document );
}
