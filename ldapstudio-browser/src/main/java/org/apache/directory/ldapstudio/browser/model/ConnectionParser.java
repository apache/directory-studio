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

package org.apache.directory.ldapstudio.browser.model;


import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * This class is a Connection Parser
 */
public class ConnectionParser
{

    private ConnectionParserContainer container;


    /**
     * Default constructor
     * @throws XmlPullParserException
     */
    public ConnectionParser() throws XmlPullParserException
    {
        this.container = new ConnectionParserContainer();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlPullParser xpp = factory.newPullParser();

        container.setParser( xpp );
    }


    public void parse( String str ) throws XmlPullParserException
    {
        XmlPullParser xpp = container.getParser();

        xpp.setInput( new StringReader( str ) );

        container.setState( ConnectionGrammar.GRAMMAR_START );

        int eventType = xpp.getEventType();
        do
        {
            if ( eventType == XmlPullParser.START_DOCUMENT )
            {
                container.setState( ConnectionGrammar.GRAMMAR_START );
            }
            else if ( eventType == XmlPullParser.END_DOCUMENT )
            {
                container.setState( ConnectionGrammar.GRAMMAR_END );
            }
            else if ( eventType == XmlPullParser.START_TAG )
            {
                processTag( Tag.START );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                processTag( Tag.END );
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
        while ( container.getState() != ConnectionGrammar.GRAMMAR_END );
    }


    private void processTag( int tagType ) throws XmlPullParserException
    {
        XmlPullParser xpp = container.getParser();

        String tagName = xpp.getName().toLowerCase();

        GrammarTransition transition = container.getTransition( container.getState(), new Tag( tagName, tagType ) );

        if ( transition != null )
        {
            container.setState( transition.getNextState() );

            if ( transition.hasAction() )
            {
                transition.getAction().action( container );
            }
        }
        else
        {
            throw new XmlPullParserException( "The tag " + new Tag( tagName, tagType )
                + " can't be found at this position", xpp, null );
        }
    }


    /**
     * Gets a List of the parsed Connections
     * @return a List of the parsed Connections
     */
    public List<Connection> getConnections()
    {
        return container.getConnections();
    }
}
