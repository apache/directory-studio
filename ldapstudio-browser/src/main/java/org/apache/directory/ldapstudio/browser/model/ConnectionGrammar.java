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
import java.lang.reflect.Array;
import java.util.HashMap;

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * This class represent the Connection Grammar used to parsed the XML 
 * representation of a connection.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionGrammar extends AbstractGrammar
{
    /** The initial state */
    public static int GRAMMAR_START = 0;

    /** The ending state */
    public static int GRAMMAR_END = -1;

    // States for Connections tag
    public static int CONNECTIONS_LOOP = 1;

    // States for Connection tag
    public static int CONNECTION_START = 2;
    public static int CONNECTION_END = 3;

    // States for Name tag
    public static int NAME_START = 4;
    public static int NAME_END = 5;

    // States for Host tag
    public static int HOST_START = 6;
    public static int HOST_END = 7;

    // States for Port tag
    public static int PORT_START = 8;
    public static int PORT_END = 9;

    // States for BaseDN tag
    public static int BASEDN_START = 10;
    public static int BASEDN_END = 11;

    // States for AnonymousBind tag
    public static int ANONYMOUSBIND_START = 12;
    public static int ANONYMOUSBIND_END = 13;

    // States for UserDN tag
    public static int USERDN_START = 14;
    public static int USERDN_END = 15;

    // States for appendBaseDNtoUserDNWithBaseDN tag
    public static int APPENDBASEDNTOUSERDN_START = 18;
    public static int APPENDBASEDNTOUSERDN_END = 19;

    // States for Password tag
    public static int PASSWORD_START = 16;
    public static int PASSWORD_END = 17;


    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked")
    public ConnectionGrammar()
    {
        // Create the transitions table
        super.transitions = ( HashMap<Tag, GrammarTransition>[] ) Array.newInstance( HashMap.class, 20 );

        // Initilization of the HashMaps
        super.transitions[GRAMMAR_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[CONNECTIONS_LOOP] = new HashMap<Tag, GrammarTransition>();
        super.transitions[CONNECTION_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[CONNECTION_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[NAME_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[NAME_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[HOST_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[HOST_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[PORT_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[PORT_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[BASEDN_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[BASEDN_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[ANONYMOUSBIND_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[ANONYMOUSBIND_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[USERDN_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[USERDN_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[APPENDBASEDNTOUSERDN_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[APPENDBASEDNTOUSERDN_END] = new HashMap<Tag, GrammarTransition>();
        super.transitions[PASSWORD_START] = new HashMap<Tag, GrammarTransition>();
        super.transitions[PASSWORD_END] = new HashMap<Tag, GrammarTransition>();

        // State: [GRAMMAR_START] - Tag: <connections>
        super.transitions[GRAMMAR_START].put( new Tag( "connections", Tag.START ), new GrammarTransition(
            GRAMMAR_START, CONNECTIONS_LOOP, null ) );

        // State: [CONNECTIONS_LOOP] - Tag: <connection>
        super.transitions[CONNECTIONS_LOOP].put( new Tag( "connection", Tag.START ), new GrammarTransition(
            CONNECTIONS_LOOP, CONNECTION_START, createConnection ) );

        // State: [CONNECTION_START] - Tag: <name>
        super.transitions[CONNECTION_START].put( new Tag( "name", Tag.START ), new GrammarTransition( CONNECTION_START,
            NAME_START, addName ) );

        // State: [NAME_START] - Tag: </name>
        super.transitions[NAME_START].put( new Tag( "name", Tag.END ), new GrammarTransition( NAME_START, NAME_END,
            null ) );

        // State: [NAME_END] - Tag: <host>
        super.transitions[NAME_END].put( new Tag( "host", Tag.START ), new GrammarTransition( NAME_END, HOST_START,
            addHost ) );

        // State: [HOST_START] - Tag: </host>
        super.transitions[HOST_START].put( new Tag( "host", Tag.END ), new GrammarTransition( HOST_START, HOST_END,
            null ) );

        // State: [HOST_END] - Tag: <port>
        super.transitions[HOST_END].put( new Tag( "port", Tag.START ), new GrammarTransition( HOST_END, PORT_START,
            addPort ) );

        // State: [PORT_START] - Tag: </port>
        super.transitions[PORT_START].put( new Tag( "port", Tag.END ), new GrammarTransition( PORT_START, PORT_END,
            null ) );

        // State: [PORT_END] - Tag: <baseDN>
        super.transitions[PORT_END].put( new Tag( "baseDN", Tag.START ), new GrammarTransition( PORT_END, BASEDN_START,
            addBaseDN ) );

        // State: [BASEDN_START] - Tag: </baseDN>
        super.transitions[BASEDN_START].put( new Tag( "baseDN", Tag.END ), new GrammarTransition( BASEDN_START,
            BASEDN_END, null ) );

        // State: [BASEDN_END] - Tag: <anonymousBind>
        super.transitions[BASEDN_END].put( new Tag( "anonymousBind", Tag.START ), new GrammarTransition( BASEDN_END,
            ANONYMOUSBIND_START, addAnonymousBind ) );

        // State: [ANONYMOUSBIND_START] - Tag: </anonymousBind>
        super.transitions[ANONYMOUSBIND_START].put( new Tag( "anonymousBind", Tag.END ), new GrammarTransition(
            ANONYMOUSBIND_START, ANONYMOUSBIND_END, null ) );

        // State: [ANONYMOUSBIND_END] - Tag: <userDN>
        super.transitions[ANONYMOUSBIND_END].put( new Tag( "userDN", Tag.START ), new GrammarTransition(
            ANONYMOUSBIND_END, USERDN_START, addUserDN ) );

        // State: [USERDN_START] - Tag: </userDN>
        super.transitions[USERDN_START].put( new Tag( "userDN", Tag.END ), new GrammarTransition( USERDN_START,
            USERDN_END, null ) );

        // State: [USERDN_END] - Tag: <appendBaseDNtoUserDNWithBaseDN>
        super.transitions[USERDN_END].put( new Tag( "appendBaseDNtoUserDNWithBaseDN", Tag.START ),
            new GrammarTransition( USERDN_END, APPENDBASEDNTOUSERDN_START, appendBaseDNtoUserDNWithBaseDN ) );

        // State: [PREFIXUSERDNWITHBASEDN_START] - Tag: </appendBaseDNtoUserDNWithBaseDN>
        super.transitions[APPENDBASEDNTOUSERDN_START].put( new Tag( "appendBaseDNtoUserDNWithBaseDN", Tag.END ),
            new GrammarTransition( APPENDBASEDNTOUSERDN_START, APPENDBASEDNTOUSERDN_END, null ) );

        // State: [PREFIXUSERDNWITHBASEDN_END] - Tag: <password>
        super.transitions[APPENDBASEDNTOUSERDN_END].put( new Tag( "password", Tag.START ), new GrammarTransition(
            APPENDBASEDNTOUSERDN_END, PASSWORD_START, addPassword ) );

        // State: [PASSWORD_START] - Tag: </password>
        super.transitions[PASSWORD_START].put( new Tag( "password", Tag.END ), new GrammarTransition( PASSWORD_START,
            PASSWORD_END, null ) );

        // State: [PASSWORD_END] - Tag: </connection>
        super.transitions[PASSWORD_END].put( new Tag( "connection", Tag.END ), new GrammarTransition( PASSWORD_END,
            CONNECTIONS_LOOP, null ) );

        // State: [CONNECTIONS_LOOP] - Tag: </connections>
        super.transitions[CONNECTIONS_LOOP].put( new Tag( "connections", Tag.END ), new GrammarTransition(
            CONNECTIONS_LOOP, GRAMMAR_END, null ) );
    }

    /**
     * GrammarAction that create a Connection
     */
    private final GrammarAction createConnection = new GrammarAction( "Create Connection" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            container.addConnection( new Connection() );
        }
    };

    /**
     * GrammarAction that adds a Name to a Connection
     */
    private final GrammarAction addName = new GrammarAction( "Add Name" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "null" ) )
                {
                    connection.setName( null );
                }
                else
                {
                    connection.setName( xpp.getText() );
                }
            }
        }
    };

    /**
     * GrammarAction that adds a Host to a Connection
     */
    private final GrammarAction addHost = new GrammarAction( "Add Host" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "null" ) )
                {
                    connection.setHost( null );
                }
                else
                {
                    connection.setHost( xpp.getText() );
                }
            }
        }
    };

    /**
     * GrammarAction that adds a Port to a Connection
     */
    private final GrammarAction addPort = new GrammarAction( "Add Port" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "0" ) )
                {
                    connection.setPort( 0 );
                }
                else
                {
                    connection.setPort( Integer.parseInt( xpp.getText() ) );
                }
            }
        }
    };

    /**
     * GrammarAction that adds a BaseDN to a Connection
     */
    private final GrammarAction addBaseDN = new GrammarAction( "Add BaseDN" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "null" ) )
                {
                    connection.setBaseDN( LdapDN.EMPTY_LDAPDN );
                }
                else
                {
                    try
                    {
                        connection.setBaseDN( new LdapDN( xpp.getText() ) );
                    }
                    catch ( InvalidNameException e )
                    {
                        throw new XmlPullParserException( "An error has ocurred. " + e.getMessage(), xpp, null );
                    }
                }
            }
        }
    };

    /**
     * GrammarAction that adds a AnonymousBind to a Connection
     */
    private final GrammarAction addAnonymousBind = new GrammarAction( "Add AnonymousBind" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "false" ) )
                {
                    connection.setAnonymousBind( false );
                }
                else if ( xpp.getText().equals( "true" ) )
                {
                    connection.setAnonymousBind( true );
                }
                else
                {
                    throw new XmlPullParserException( "An error has ocurred.", xpp, null );
                }
            }
        }
    };

    /**
     * GrammarAction that adds a UserDN to a Connection
     */
    private final GrammarAction addUserDN = new GrammarAction( "Add UserDN" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "null" ) )
                {
                    connection.setUserDN( LdapDN.EMPTY_LDAPDN );
                }
                else
                {
                    try
                    {
                        connection.setUserDN( new LdapDN( xpp.getText() ) );
                    }
                    catch ( InvalidNameException e )
                    {
                        throw new XmlPullParserException( "An error has ocurred. " + e.getMessage(), xpp, null );
                    }
                }
            }
        }
    };

    /**
     * GrammarAction that adds a appendBaseDNtoUserDNWithBaseDN to a Connection
     */
    private final GrammarAction appendBaseDNtoUserDNWithBaseDN = new GrammarAction(
        "Add appendBaseDNtoUserDNWithBaseDN" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "false" ) )
                {
                    connection.setAppendBaseDNtoUserDNWithBaseDN( false );
                }
                else if ( xpp.getText().equals( "true" ) )
                {
                    connection.setAppendBaseDNtoUserDNWithBaseDN( true );
                }
                else
                {
                    throw new XmlPullParserException( "An error has ocurred.", xpp, null );
                }
            }
        }
    };

    /**
     * GrammarAction that adds a Password to a Connection
     */
    private final GrammarAction addPassword = new GrammarAction( "Add Password" )
    {
        public void action( ConnectionParserContainer container ) throws XmlPullParserException
        {
            Connection connection = container.getCurrentConnection();

            XmlPullParser xpp = container.getParser();

            int eventType = 0;
            try
            {
                eventType = xpp.next();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( e.getMessage(), xpp, null );
            }

            if ( eventType != XmlPullParser.TEXT )
            {
                throw new XmlPullParserException( "An error has ocurred.", xpp, null );
            }
            else
            {
                if ( xpp.getText().equals( "null" ) )
                {
                    connection.setPassword( null );
                }
                else
                {
                    connection.setPassword( xpp.getText() );
                }
            }
        }
    };
}
