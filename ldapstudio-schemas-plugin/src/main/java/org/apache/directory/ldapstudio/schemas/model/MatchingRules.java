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

package org.apache.directory.ldapstudio.schemas.model;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.eclipse.core.runtime.Platform;


/**
 * This class allows to get the list of all matching rules
 * (which is initialized once parsing a XML file)
 * 
 */
public class MatchingRules
{
    private static final ArrayList<MatchingRule> equalityMatchingRules;
    private static final ArrayList<MatchingRule> orderingMatchingRules;
    private static final ArrayList<MatchingRule> substringMatchingRules;

    static
    {
        try
        {
            equalityMatchingRules = new ArrayList<MatchingRule>();
            URL url = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/utils/matching_rules.xml" ); //$NON-NLS-1$
            XMLConfiguration config = new XMLConfiguration( url );

            // We get the number of matching rules to parse for EQUALITY
            Object matchingRules = config.getProperty( "equality.matchingRule.name" ); //$NON-NLS-1$
            if ( matchingRules instanceof Collection )
            {
                for ( int i = 0; i < ( ( Collection ) matchingRules ).size(); i++ )
                {
                    // We parse each syntax and get its properties
                    String name = config.getString( "equality.matchingRule(" + i + ").name" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String oid = config.getString( "equality.matchingRule(" + i + ").oid" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String syntax = config.getString( "equality.matchingRule(" + i + ").syntax" ); //$NON-NLS-1$ //$NON-NLS-2$

                    // We create the corresponding syntax object and add it to the ArrayList
                    MatchingRule matchingRule = new MatchingRule( name, oid, syntax );
                    equalityMatchingRules.add( matchingRule );
                }
            }
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }

    static
    {
        try
        {
            orderingMatchingRules = new ArrayList<MatchingRule>();

            URL url = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/utils/matching_rules.xml" ); //$NON-NLS-1$
            XMLConfiguration config = new XMLConfiguration( url );

            // We get the number of matching rules to parse for ORDERING
            Object matchingRules = config.getProperty( "ordering.matchingRule.name" ); //$NON-NLS-1$
            if ( matchingRules instanceof Collection )
            {
                for ( int i = 0; i < ( ( Collection ) matchingRules ).size(); i++ )
                {
                    // We parse each syntax and get its properties
                    String name = config.getString( "ordering.matchingRule(" + i + ").name" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String oid = config.getString( "ordering.matchingRule(" + i + ").oid" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String syntax = config.getString( "ordering.matchingRule(" + i + ").syntax" ); //$NON-NLS-1$ //$NON-NLS-2$

                    // We create the corresponding syntax object and add it to the ArrayList
                    MatchingRule matchingRule = new MatchingRule( name, oid, syntax );
                    orderingMatchingRules.add( matchingRule );
                }
            }
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }

    static
    {
        try
        {
            substringMatchingRules = new ArrayList<MatchingRule>();
            URL url = Platform.getBundle( Application.PLUGIN_ID ).getResource( "ressources/utils/matching_rules.xml" ); //$NON-NLS-1$
            XMLConfiguration config = new XMLConfiguration( url );

            // We get the number of matching rules to parse for SUBSTRING
            Object matchingRules = config.getProperty( "substring.matchingRule.name" ); //$NON-NLS-1$
            if ( matchingRules instanceof Collection )
            {
                for ( int i = 0; i < ( ( Collection ) matchingRules ).size(); i++ )
                {
                    // We parse each syntax and get its properties
                    String name = config.getString( "substring.matchingRule(" + i + ").name" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String oid = config.getString( "substring.matchingRule(" + i + ").oid" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String syntax = config.getString( "substring.matchingRule(" + i + ").syntax" ); //$NON-NLS-1$ //$NON-NLS-2$

                    // We create the corresponding syntax object and add it to the ArrayList
                    MatchingRule matchingRule = new MatchingRule( name, oid, syntax );
                    substringMatchingRules.add( matchingRule );
                }
            }
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }


    /**
     * Return the unique initialized ArrayList containing all Equality Matching Rules
     * @return the Equality Matching Rules ArrayList
     */
    public static ArrayList<MatchingRule> getEqualityMatchingRules()
    {
        return equalityMatchingRules;
    }


    /**
     * Return the unique initialized ArrayList containing all Ordering Matching Rules
     * @return the Ordering Matching Rules ArrayList
     */
    public static ArrayList<MatchingRule> getOrderingMatchingRules()
    {
        return orderingMatchingRules;
    }


    /**
     * Return the unique initialized ArrayList containing all Substring Matching Rules
     * @return the Substring Matching Rules ArrayList
     */
    public static ArrayList<MatchingRule> getSubstringMatchingRules()
    {
        return substringMatchingRules;
    }
}
