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

package org.apache.directory.ldapstudio.browser.core.model.schema;


public class MatchingRuleUseDescription extends SchemaPart2
{

    private static final long serialVersionUID = 2768563387519504369L;

    private String[] appliesAttributeTypeDescriptionOIDs;


    public MatchingRuleUseDescription()
    {
        super();
        this.appliesAttributeTypeDescriptionOIDs = new String[0];
    }


    public int compareTo( Object o )
    {
        if ( o instanceof MatchingRuleUseDescription )
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
     * @return the names, may be an empty array
     */
    public String[] getNames()
    {
        return names;
    }


    public void setNames( String[] names )
    {
        this.names = names;
    }


    /**
     * 
     * @return the obsolete flag
     */
    public boolean isObsolete()
    {
        return isObsolete;
    }


    public void setObsolete( boolean isObsolete )
    {
        this.isObsolete = isObsolete;
    }


    /**
     * 
     * @return the applied attribute type description oids
     */
    public String[] getAppliesAttributeTypeDescriptionOIDs()
    {
        return appliesAttributeTypeDescriptionOIDs;
    }


    public void setAppliesAttributeTypeDescriptionOIDs( String[] appliesAttributeTypeDescriptionOIDs )
    {
        this.appliesAttributeTypeDescriptionOIDs = appliesAttributeTypeDescriptionOIDs;
    }

}
