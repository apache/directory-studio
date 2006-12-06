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

package org.apache.directory.ldapstudio.dsmlv2;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class Dsmlv2ResponseParser
{
    private Dsmlv2Container container;


    public Dsmlv2ResponseParser() throws XmlPullParserException
    {
        this.container = new Dsmlv2Container();

        this.container.setGrammar( Dsmlv2ResponseGrammar.getInstance() );

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        container.setParser( xpp );
    }


    public void setInput( String str ) throws FileNotFoundException, XmlPullParserException
    {
        container.getParser().setInput( new StringReader( str ) );
    }


    public void setInputFile( String fileName ) throws FileNotFoundException, XmlPullParserException
    {
        Reader reader = new FileReader( fileName );
        container.getParser().setInput( reader );
    }


    public void setInput( InputStream inputStream, String inputEncoding ) throws XmlPullParserException
    {
        container.getParser().setInput( inputStream, inputEncoding );
    }


    public void parse() throws Exception
    {
        Dsmlv2ResponseGrammar grammar = Dsmlv2ResponseGrammar.getInstance();

        grammar.executeAction( container );
    }


    public void parseBatchResponse() throws XmlPullParserException
    {
        XmlPullParser xpp = container.getParser();

        int eventType = xpp.getEventType();
        do
        {
            if ( eventType == XmlPullParser.START_DOCUMENT )
            {
                container.setState( Dsmlv2StatesEnum.INIT_GRAMMAR_STATE );
            }
            else if ( eventType == XmlPullParser.END_DOCUMENT )
            {
                container.setState( Dsmlv2StatesEnum.END_STATE );
            }
            else if ( eventType == XmlPullParser.START_TAG )
            {
                processTag( container, Tag.START );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                processTag( container, Tag.END );
            }
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( "An IOException ocurred during parsing : " + e.getMessage(), xpp,
                    null );
            }
        }
        while ( container.getState() != Dsmlv2StatesEnum.BATCH_RESPONSE_LOOP );
    }


    private void processTag( Dsmlv2Container container, int tagType ) throws XmlPullParserException
    {
        XmlPullParser xpp = container.getParser();

        String tagName = xpp.getName().toLowerCase();

        GrammarTransition transition = container.getTransition( container.getState(), new Tag( tagName, tagType ) );

        if ( transition != null )
        {
            container.setState( transition.getNextState() );

            if ( transition.hasAction() )
            {
                //                    System.out.println( transition.getAction().toString() );// TODO Suppress
                transition.getAction().action( container );
            }
        }
        else
        {
            throw new XmlPullParserException( "The tag " + new Tag( tagName, tagType )
                + " can't be found at this position", xpp, null );
        }
    }


    public BatchResponse getBatchResponse()
    {
        return container.getBatchResponse();
    }


    /**
     * Returns the next Request or null if there's no more request
     * @return the next Request or null if there's no more request
     * @throws XmlPullParserException 
     * @throws Exception
     */
    public LdapResponse getNextResponse() throws XmlPullParserException
    {
        if ( container.getBatchResponse() == null )
        {
            throw new XmlPullParserException( "The batch response needs to be parsed before parsing a response",
                container.getParser(), null );
        }

        XmlPullParser xpp = container.getParser();

        int eventType = xpp.getEventType();
        do
        {
            while ( eventType == XmlPullParser.TEXT )
            {
                try
                {
                    xpp.next();
                }
                catch ( IOException e )
                {
                    throw new XmlPullParserException( "An IOException ocurred during parsing : " + e.getMessage(), xpp,
                        null );
                }
                eventType = xpp.getEventType();
            }

            if ( eventType == XmlPullParser.START_DOCUMENT )
            {
                container.setState( Dsmlv2StatesEnum.INIT_GRAMMAR_STATE );
            }
            else if ( eventType == XmlPullParser.END_DOCUMENT )
            {
                container.setState( Dsmlv2StatesEnum.END_STATE );
                return null;
            }
            else if ( eventType == XmlPullParser.START_TAG )
            {
                processTag( container, Tag.START );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                processTag( container, Tag.END );
            }
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( "An IOException ocurred during parsing : " + e.getMessage(), xpp,
                    null );
            }
        }
        while ( container.getState() != Dsmlv2StatesEnum.BATCH_RESPONSE_LOOP );

        return container.getBatchResponse().getCurrentResponse();
    }


    public void parseAllResponses() throws Exception
    {
        while ( getNextResponse() != null )
        {
            continue;
        }
    }
}
