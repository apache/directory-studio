/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to store the WhatClause attributes with a qualifier (either '!' or '@').
 * We store either an AttributeType, or an ObjectClass (prefixed with '@' or '¬'), or one
 * of the two special values : 'entry' and 'children'
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclAttributeVal
{
    /** The list of AclAttributes */
    List<AclAttribute> aclAttributes = new ArrayList<AclAttribute>();
    
    /** The attribute style*/
    private AclAttributeStyleEnum style;
    
    /** The val flag */
    private boolean val;
    
    /** The MatchingRule flag */
    private boolean matchingRule;
    
    /** The regex */
    private String regex;

    /**
     * @return the aclAttributes
     */
    public List<AclAttribute> getAclAttributes()
    {
        return aclAttributes;
    }

    /**
     * @param aclAttributes the aclAttributes to set
     */
    public void setAclAttributes( List<AclAttribute> aclAttributes )
    {
        this.aclAttributes = aclAttributes;
    }

    /**
     * @return the style
     */
    public AclAttributeStyleEnum getStyle()
    {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle( AclAttributeStyleEnum style )
    {
        this.style = style;
    }

    /**
     * @return the val
     */
    public boolean hasVal()
    {
        return val;
    }

    /**
     * @param val the val to set
     */
    public void setVal( boolean val )
    {
        this.val = val;
    }

    /**
     * @return the matchingRule
     */
    public boolean hasMatchingRule()
    {
        return matchingRule;
    }

    /**
     * @param matchingRule the matchingRule to set
     */
    public void setMatchingRule( boolean matchingRule )
    {
        this.matchingRule = matchingRule;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue( String value )
    {
        this.value = value;
    }
}
