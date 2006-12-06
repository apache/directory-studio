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


import org.xmlpull.v1.XmlPullParser;


public class Dsmlv2Container implements Container
{
    /** The current state of the decoding */
    private int state;

    /** The current transition */
    private int transition;

    /** Store the different states for debug purpose */
    private IStates states;

    /** The pool parser */
    private XmlPullParser parser;

    /** The BatchRequest of the parsing */
    private BatchRequest batchRequest;

    /** The BatchResponse of the parsing */
    private BatchResponse batchResponse;

    private AbstractGrammar grammar;


    public Dsmlv2Container()
    {
        //grammar = Dsmlv2Grammar.getInstance();
    }


    /**
     * Get the DSML Batch Request
     * 
     * @return Returns the Batch Request
     */
    public BatchRequest getBatchRequest()
    {
        return batchRequest;
    }


    /**
     * Sets the DSML Batch Request
     * @param batchRequest
     */
    public void setBatchRequest( BatchRequest batchRequest )
    {
        this.batchRequest = batchRequest;
    }


    /**
     * Get the DSML Batch Response
     * 
     * @return Returns the Batch Response
     */
    public BatchResponse getBatchResponse()
    {
        return batchResponse;
    }


    /**
     * Sets the DSML Batch Request
     * @param batchRequest
     */
    public void setBatchResponse( BatchResponse batchResponse )
    {
        this.batchResponse = batchResponse;
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
     * Get the states for this container's grammars
     * 
     * @return Returns the states.
     */
    public IStates getStates()
    {
        return states;
    }


    public AbstractGrammar getGrammar()
    {
        return grammar;
    }


    public void setGrammar( AbstractGrammar grammar )
    {
        this.grammar = grammar;
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
}
