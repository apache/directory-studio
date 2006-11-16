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


import java.util.HashMap;


/**
 * The abstract IGrammar which is the Mother of all the grammars. It contains
 * the transitions table.
 */
public abstract class AbstractGrammar implements IGrammar
{

    /**
     * Table of transitions. It's a two dimension array, the first dimension
     * indice the states, the second dimension indices the Tag value, so it is
     * 256 wide.
     */
    protected HashMap<Tag, GrammarTransition>[] transitions;

    /** The grammar name */
    protected String name;


    public AbstractGrammar()
    {

    }


    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Return the grammar's name
     * 
     * @return The grammar name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Set the grammar's name
     * 
     * @param name
     *            DOCUMENT ME!
     */
    public void setName( String name )
    {
        this.name = name;
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
        return transitions[state].get( tag );
    }
}
