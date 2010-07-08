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

package org.apache.directory.studio.ldapbrowser.common.filtereditor;


import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleDescription;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterExtensibleComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;


/**
 * The FilterTextHover is used to display error messages in a tooltip.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FilterTextHover implements ITextHover
{

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The schema, used to retrieve attributeType and objectClass information. */
    private Schema schema;


    /**
     * Creates a new instance of FilterTextHover.
     *
     * @param parser filter parser
     */
    public FilterTextHover( LdapFilterParser parser )
    {
        this.parser = parser;
    }


    /**
     * Sets the schema, used to retrieve attributeType and objectClass information.
     * 
     * @param schema the schema
     */
    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    /**
     * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    public String getHoverInfo( ITextViewer textViewer, IRegion hoverRegion )
    {
        // check attribute type, object class or matching rule values
        if ( schema != null )
        {
            LdapFilter filter = parser.getModel().getFilter( hoverRegion.getOffset() );

            if ( filter.getFilterComponent() instanceof LdapFilterItemComponent )
            {
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();
                if ( fc.getAttributeToken() != null
                    && fc.getAttributeToken().getOffset() <= hoverRegion.getOffset()
                    && hoverRegion.getOffset() <= fc.getAttributeToken().getOffset()
                        + fc.getAttributeToken().getLength() )
                {
                    String attributeType = fc.getAttributeToken().getValue();
                    AttributeTypeDescription attributeTypeDescription = schema
                        .getAttributeTypeDescription( attributeType );
                    String ldifLine = SchemaUtils.getLdifLine( attributeTypeDescription );
                    return ldifLine;
                }
                if ( fc.getAttributeToken() != null
                    && SchemaConstants.OBJECT_CLASS_AT.equalsIgnoreCase( fc.getAttributeToken().getValue() )
                    && fc.getValueToken() != null && fc.getValueToken().getOffset() <= hoverRegion.getOffset()
                    && hoverRegion.getOffset() <= fc.getValueToken().getOffset() + fc.getValueToken().getLength() )
                {
                    String objectClass = fc.getValueToken().getValue();
                    ObjectClassDescription objectClassDescription = schema.getObjectClassDescription( objectClass );
                    String ldifLine = SchemaUtils.getLdifLine( objectClassDescription );
                    return ldifLine;
                }
            }
            if ( filter.getFilterComponent() instanceof LdapFilterExtensibleComponent )
            {
                LdapFilterExtensibleComponent fc = ( LdapFilterExtensibleComponent ) filter.getFilterComponent();
                if ( fc.getAttributeToken() != null
                    && fc.getAttributeToken().getOffset() <= hoverRegion.getOffset()
                    && hoverRegion.getOffset() <= fc.getAttributeToken().getOffset()
                        + fc.getAttributeToken().getLength() )
                {
                    String attributeType = fc.getAttributeToken().getValue();
                    AttributeTypeDescription attributeTypeDescription = schema
                        .getAttributeTypeDescription( attributeType );
                    String ldifLine = SchemaUtils.getLdifLine( attributeTypeDescription );
                    return ldifLine;
                }
                if ( fc.getMatchingRuleToken() != null
                    && fc.getMatchingRuleToken().getOffset() <= hoverRegion.getOffset()
                    && hoverRegion.getOffset() <= fc.getMatchingRuleToken().getOffset()
                        + fc.getMatchingRuleToken().getLength() )
                {
                    String matchingRule = fc.getMatchingRuleToken().getValue();
                    MatchingRuleDescription matchingRuleDescription = schema.getMatchingRuleDescription( matchingRule );
                    String info = SchemaUtils.getLdifLine( matchingRuleDescription );
                    return info;
                }
            }
        }

        // check invalid tokens
        LdapFilter[] invalidFilters = parser.getModel().getInvalidFilters();
        for ( int i = 0; i < invalidFilters.length; i++ )
        {
            if ( invalidFilters[i].getStartToken() != null )
            {
                int start = invalidFilters[i].getStartToken().getOffset();
                int stop = invalidFilters[i].getStopToken() != null ? invalidFilters[i].getStopToken().getOffset()
                    + invalidFilters[i].getStopToken().getLength() : start
                    + invalidFilters[i].getStartToken().getLength();
                if ( start <= hoverRegion.getOffset() && hoverRegion.getOffset() < stop )
                {
                    return invalidFilters[i].getInvalidCause();
                }
            }
        }

        // check error tokens
        LdapFilterToken[] tokens = parser.getModel().getTokens();
        for ( int i = 0; i < tokens.length; i++ )
        {
            if ( tokens[i].getType() == LdapFilterToken.ERROR )
            {

                int start = tokens[i].getOffset();
                int stop = start + tokens[i].getLength();
                if ( start <= hoverRegion.getOffset() && hoverRegion.getOffset() < stop )
                {
                    return Messages.getString( "FilterTextHover.InvalidCharacters" ); //$NON-NLS-1$
                }
            }
        }
        return null;
    }


    /**
     * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
     */
    public IRegion getHoverRegion( ITextViewer textViewer, int offset )
    {
        return new Region( offset, 1 );
    }

}
