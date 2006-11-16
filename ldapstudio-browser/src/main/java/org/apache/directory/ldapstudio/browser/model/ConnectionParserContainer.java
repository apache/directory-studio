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


import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;


/**
 * This container is used for parsing connections
 */
/**
 * @author pajbam
 *
 */
public class ConnectionParserContainer
{

    /** The current state of the decoding */
    private int state;

    /** The current transition */
    private int transition;

    /** The pool parser */
    private XmlPullParser parser;

    /** The connections */
    private List<Connection> connections;

    private ConnectionGrammar grammar;


    public ConnectionParserContainer()
    {
        grammar = new ConnectionGrammar();
        connections = new ArrayList<Connection>();
    }


    /**
     * Get the parser
     * 
     * @return Returns the parser
     */
    public XmlPullParser getParser()
    {
        return parser;
    }


    /**
     * Set the parser
     * 
     * @param state
     *            The parser
     */
    public void setParser( XmlPullParser parser )
    {
        this.parser = parser;
    }


    /**
     * Get the current grammar state
     * 
     * @return Returns the current grammar state
     */
    public int getState()
    {
        return state;
    }


    /**
     * Set the new current state
     * 
     * @param state
     *            The new state
     */
    public void setState( int state )
    {
        this.state = state;
    }


    /**
     * Get the transition
     * 
     * @return Returns the transition from the previous state to the new state
     */
    public int getTransition()
    {
        return transition;
    }


    /**
     * Update the transition from a state to another
     * 
     * @param transition
     *            The transition to set
     */
    public void setTransition( int transition )
    {
        this.transition = transition;
    }


    /**
     * Get the connections
     * 
     * @return Returns the parsed connections
     */
    public List<Connection> getConnections()
    {
        return this.connections;
    }


    /**
     * Get the transition associated with the state and tag
     * 
     * @param state
     *            The current state
     * @param tag
     *            The current tag
     * @return A valid transition if any, or null.
     */
    public GrammarTransition getTransition( int state, Tag tag )
    {
        return grammar.getTransition( state, tag );
    }


    /**
     * Returns the current Connection
     * @return A Connection
     */
    public Connection getCurrentConnection()
    {
        return connections.get( connections.size() - 1 );
    }


    /**
     * Adds a connection to the Connection List
     * @param connection The Connection to add
     * @return true (as per the general contract of the Collection.add method).
     */
    public boolean addConnection( Connection connection )
    {
        return connections.add( connection );
    }
}
