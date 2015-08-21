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
import java.util.Collection;
import java.util.List;

/**
 * The Acl what-attrs clause.
 * 
 * <pre>
 * attrs ::= attrlist val-e
 * attrlist ::= attr attr-e
 * attr-e ::= ',' attr attr-e
 * attr :: attributeType | '!' objectClass | '@' objectClass | 'entry' | 'children'
 * val-e ::= 'val' matchingRule style '=' attrval | e
 * matchingRule ::= '/matchingRule' | e
 * style ::= '.exact' | '.base' | '.baseobject' | '.regex' | '.one' | '.onelevel' | '.sub' | '.subtree' | '.children'
 * attrval ::= STRING 
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclWhatClauseAttributes
{
    /** The attributeVal element */
    private AclAttributeVal aclAttributeVal;
    
    
    /**
     * Creates an instance of AclWhatClauseAttributes
     */
    public AclWhatClauseAttributes()
    {
        aclAttributeVal = new AclAttributeVal();
    }
    
    
    /**
     * Gets the attributes list.
     *
     * @return the attributes list
     */
    public List<AclAttribute> getAttributes()
    {
        List<AclAttribute> copyAttributes = new ArrayList<AclAttribute>( aclAttributeVal.getAclAttributes().size() );
        
        copyAttributes.addAll( aclAttributeVal.getAclAttributes() );
        
        return copyAttributes;
    }


    /**
     * Adds an attribute.
     * 
     * @param attribute the attribute to add
     */
    public void addAttribute( String attribute )
    {
        aclAttributeVal.getAclAttributes().add( new AclAttribute( attribute, null ) );
    }


    /**
     * Adds a {@link Collection} of attributes.
     * 
     * @param attributes the {@link Collection} of attributes
     */
    public void addAllAttributes( Collection<AclAttribute> attributes )
    {
        aclAttributeVal.getAclAttributes().addAll(  attributes );
    }


    /**
     * Clears attributes.
     */
    public void clearAttributes()
    {
        aclAttributeVal.getAclAttributes().clear();
    }
    
    
    /**
     * @return true if the AclWhatClauseAttributes has a val flag
     */
    public boolean hasVal()
    {
        return aclAttributeVal.hasVal();
    }
    
    
    /**
     * Set the val flag
     * 
     * @param val The val flag value
     */
    public void setVal( boolean val )
    {
        aclAttributeVal.setVal( val );
    }
    
    
    /**
     * @return true if the AclWhatClauseAttributes has a MatchingRule flag
     */
    public boolean hasMatchingRule()
    {
        return aclAttributeVal.hasMatchingRule();
    }
    
    
    /**
     * set the matchingRule flag
     */
    public void setMatchingRule( boolean matchingRule )
    {
        aclAttributeVal.setMatchingRule( matchingRule );
    }

    
    /**
     * @return The AclAttribute style
     */
    public AclAttributeStyleEnum getStyle()
    {
        return aclAttributeVal.getStyle();
    }

    
    /**
     * @param style The AttributeVal style
     */
    public void setStyle( AclAttributeStyleEnum style )
    {
        aclAttributeVal.setStyle( style );
    }

    
    /**
     * @return The AclAttribute value
     */
    public String getValue()
    {
        return aclAttributeVal.getValue();
    }

    
    /**
     * Sets the AclWhatClauseAttributes value
     * 
     * @param value The AttributeVal value
     */
    public void setValue( String value )
    {
        aclAttributeVal.setValue( value );
    }
    

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Attrs
        sb.append( "attrs" );

        // Attributes
        if ( ( aclAttributeVal.getAclAttributes() != null ) && ( aclAttributeVal.getAclAttributes().size() > 0 ) )
        {
            sb.append( '=' );
            boolean isFirst = true;
            
            for ( AclAttribute attribute : aclAttributeVal.getAclAttributes() )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( ',' );
                }
                
                sb.append( attribute );
            }
        }
        
        // The val
        if ( aclAttributeVal.hasVal() )
        {
            sb.append( " val" );
            
            if ( aclAttributeVal.hasMatchingRule() )
            {
                sb.append( "/matchingRule" );
            }
            
            if ( aclAttributeVal.getStyle() != AclAttributeStyleEnum.NONE )
            {
                sb.append( '.' );
                sb.append( aclAttributeVal.getStyle().getName() );
            }
            
            sb.append( "=\"" );
            sb.append( aclAttributeVal.getValue() );
            sb.append( '"' );
        }

        return sb.toString();
    }
}
