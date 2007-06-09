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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MatchingRuleDescription extends SchemaPart2
{

    private static final long serialVersionUID = -8497098446034593329L;

    private String syntaxDescriptionNumericOID;


    public MatchingRuleDescription()
    {
        super();
        this.syntaxDescriptionNumericOID = null;
    }


    public int compareTo( Object o )
    {
        if ( o instanceof MatchingRuleDescription )
        {
            return this.toString().compareTo( o.toString() );
        }
        else
        {
            throw new ClassCastException( "Object of type " + this.getClass().getName() + " required." );
        }
    }


    /**
     * 
     * @return the syntax description OID, may be null
     */
    public String getSyntaxDescriptionNumericOID()
    {
        return syntaxDescriptionNumericOID;
    }


    public void setSyntaxDescriptionNumericOID( String syntaxDescriptionNumericOID )
    {
        this.syntaxDescriptionNumericOID = syntaxDescriptionNumericOID;
    }


    /**
     * 
     * @return all attribute type descriptions using this matching rule for
     *         equality, substring or ordering matching
     */
    public AttributeTypeDescription[] getUsedFromAttributeTypeDescriptions()
    {
        Set usedFromSet = new HashSet();
        for ( Iterator it = this.getSchema().getAtdMapByName().values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            if ( atd.getEqualityMatchingRuleDescriptionOIDTransitive() != null
                && this.getLowerCaseIdentifierSet().contains(
                    atd.getEqualityMatchingRuleDescriptionOIDTransitive().toLowerCase() ) )
            {
                usedFromSet.add( atd );
            }
            if ( atd.getSubstringMatchingRuleDescriptionOIDTransitive() != null
                && this.getLowerCaseIdentifierSet().contains(
                    atd.getSubstringMatchingRuleDescriptionOIDTransitive().toLowerCase() ) )
            {
                usedFromSet.add( atd );
            }
            if ( atd.getOrderingMatchingRuleDescriptionOIDTransitive() != null
                && this.getLowerCaseIdentifierSet().contains(
                    atd.getOrderingMatchingRuleDescriptionOIDTransitive().toLowerCase() ) )
            {
                usedFromSet.add( atd );
            }
        }
        AttributeTypeDescription[] atds = ( AttributeTypeDescription[] ) usedFromSet
            .toArray( new AttributeTypeDescription[0] );
        Arrays.sort( atds );
        return atds;
    }

}
