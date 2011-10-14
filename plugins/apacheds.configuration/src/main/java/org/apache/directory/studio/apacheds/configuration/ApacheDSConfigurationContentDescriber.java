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
import java.io.Reader;

import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;


/**
 * This class implements a ContentDescriber for Apache DS Configuration file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDSConfigurationContentDescriber implements ITextContentDescriber
{
    /** The supported Options */
    private static final QualifiedName[] SUPPORTED_OPTIONS = new QualifiedName[]
        { IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK };


    /**
     * {@inheritDoc}
     */
    public int describe( Reader contents, IContentDescription description ) throws IOException
    {
        if ( isValid( contents ) )
        {
            return ITextContentDescriber.VALID;
        }
        else
        {
            return ITextContentDescriber.INVALID;
        }
    }


    /**
     * {@inheritDoc}
     */
    public int describe( InputStream contents, IContentDescription description ) throws IOException
    {
        if ( isValid( contents ) )
        {
            return ITextContentDescriber.VALID;
        }
        else
        {
            return ITextContentDescriber.INVALID;
        }
    }


    /**
     * {@inheritDoc}
     */
    public QualifiedName[] getSupportedOptions()
    {
        return SUPPORTED_OPTIONS;
    }


    /**
     * Indicates if the given {@link Reader} is a valid server configuration.
     *
     * @param contents
     *      the contents reader
     * @return
     *      <code>true</code> if the given reader is a valid server 
     *      configuration, <code>false</code> if not
     */
    private boolean isValid( Reader contents )
    {
        // Looping on the ServerXmlIO classes to find a corresponding one
        ServerXmlIO[] serverXmlIOs = ApacheDSConfigurationPlugin.getDefault().getServerXmlIOs();
        for ( ServerXmlIO validationServerXmlIO : serverXmlIOs )
        {
            // Marking the reader
            try
            {
                contents.mark( -1 );
            }
            catch ( IOException e1 )
            {
                return false;
            }

            // Checking if the ServerXmlIO is valid
            if ( validationServerXmlIO.isValid( contents ) )
            {
                return true;
            }

            // Reseting the reader to the mark
            try
            {
                contents.reset();
            }
            catch ( IOException e )
            {
                return false;
            }
        }

        return false;
    }


    /**
     * Indicates if the given {@link InputStream} is a valid server configuration.
     *
     * @param contents
     *      the contents input stream
     * @return
     *      <code>true</code> if the given input stream is a valid server 
     *      configuration, <code>false</code> if not
     */
    private boolean isValid( InputStream contents )
    {
        // Looping on the ServerXmlIO classes to find a corresponding one
        ServerXmlIO[] serverXmlIOs = ApacheDSConfigurationPlugin.getDefault().getServerXmlIOs();
        for ( ServerXmlIO validationServerXmlIO : serverXmlIOs )
        {
            // Marking the input stream
            contents.mark( -1 );

            // Checking if the ServerXmlIO is valid
            if ( validationServerXmlIO.isValid( contents ) )
            {
                return true;
            }

            // Reseting the input stream to the mark
            try
            {
                contents.reset();
            }
            catch ( IOException e )
            {
                return false;
            }
        }

        return false;
    }
}
