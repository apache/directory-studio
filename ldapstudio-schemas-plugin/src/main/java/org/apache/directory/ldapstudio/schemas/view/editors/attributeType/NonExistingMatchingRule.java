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
package org.apache.directory.ldapstudio.schemas.view.editors.attributeType;


import org.apache.directory.ldapstudio.schemas.Messages;


/**
 * This class implements the Non Existing Attribute Type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NonExistingMatchingRule
{
    public static final String NONE = Messages.getString( "NonExistingMatchingRule.(None)" );

    /** The name */
    private String name;


    /**
     * Creates a new instance of NonExistingMatchingRule.
     *
     * @param name
     *      the name the NonExistingMatchingRule
     */
    public NonExistingMatchingRule( String name )
    {
        this.name = name;
    }


    /**
     * Gets the name of the NonExistingMatchingRule.
     *
     * @return
     *      the name of the NonExistingMatchingRule
     */
    public String getName()
    {
        return name;
    }


    /**
     * Gets the displayable name of the NonExistingMatchingRule.
     *
     * @return
     *      the displayable name of the NonExistingMatchingRule
     */
    public String getDisplayName()
    {
        if ( name.equals( NONE ) )
        {
            return NONE;
        }
        else
        {
            return name + "   " + Messages.getString( "NonExistingMatchingRule.(This_matching_rule_doesnt_exist)" );
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof NonExistingMatchingRule )
        {
            return name.equalsIgnoreCase( (( NonExistingMatchingRule ) obj).getName() );
        }

        return false;
    }
}
