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

package org.apache.directory.studio.schemas.model;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.directory.studio.schemas.Activator;
import org.eclipse.core.runtime.Platform;


/**
 * This class allows to get the list of all matching rules
 * (which is initialized once parsing a XML file)
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MatchingRules
{
    /** The Equality Matching Rules */
    private static final ArrayList<MatchingRule> equalityMatchingRules;

    /** The Ordering Matching Rules */
    private static final ArrayList<MatchingRule> orderingMatchingRules;

    /** The Substring Matching Rules */
    private static final ArrayList<MatchingRule> substringMatchingRules;

    // Equality Matching Rules Initialization
    static
    {
        try
        {
            equalityMatchingRules = new ArrayList<MatchingRule>();
            URL url = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "resources/utils/matching_rules.xml" ); //$NON-NLS-1$
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

    // Ordering Matching Rules Initialization
    static
    {
        try
        {
            orderingMatchingRules = new ArrayList<MatchingRule>();

            URL url = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "resources/utils/matching_rules.xml" ); //$NON-NLS-1$
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

    // Substring Matching Rules Initialization
    static
    {
        try
        {
            substringMatchingRules = new ArrayList<MatchingRule>();
            URL url = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "resources/utils/matching_rules.xml" ); //$NON-NLS-1$
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
     * Returns the unique initialized ArrayList containing all Equality Matching Rules.
     * @return
     *      the Equality Matching Rules ArrayList
     */
    public static ArrayList<MatchingRule> getEqualityMatchingRules()
    {
        return equalityMatchingRules;
    }


    /**
     * Returns the unique initialized ArrayList containing all Ordering Matching Rules.
     * 
     * @return
     *      the Ordering Matching Rules ArrayList
     */
    public static ArrayList<MatchingRule> getOrderingMatchingRules()
    {
        return orderingMatchingRules;
    }


    /**
     * Returns the unique initialized ArrayList containing all Substring Matching Rules.
     * 
     * @return
     *      the Substring Matching Rules ArrayList
     */
    public static ArrayList<MatchingRule> getSubstringMatchingRules()
    {
        return substringMatchingRules;
    }


    /**
     * Gets a Matching Rule from a given name.
     *
     * @param name
     *      the name of the Matching Rule
     * @return
     *      the corresponding Matching Rule
     */
    public static MatchingRule getMatchingRule( String name )
    {
        if ( name == null )
        {
            return null;
        }

        for ( MatchingRule matchingRule : equalityMatchingRules )
        {
            if ( name.equalsIgnoreCase( matchingRule.getName() ) )
            {
                return matchingRule;
            }
        }

        for ( MatchingRule matchingRule : orderingMatchingRules )
        {
            if ( name.equalsIgnoreCase( matchingRule.getName() ) )
            {
                return matchingRule;
            }
        }

        for ( MatchingRule matchingRule : substringMatchingRules )
        {
            if ( name.equalsIgnoreCase( matchingRule.getName() ) )
            {
                return matchingRule;
            }
        }

        return null;
    }
}
