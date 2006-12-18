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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser.SchemaBrowser;

import org.eclipse.jface.resource.ImageDescriptor;


public class OpenSchemaBrowserAction extends BrowserAction
{

    public static final int MODE_NONE = 0;

    public static final int MODE_OBJECTCLASS = 10;

    public static final int MODE_ATTRIBUTETYPE = 20;

    public static final int MODE_EQUALITYMATCHINGRULE = 30;

    public static final int MODE_SUBSTRINGMATCHINGRULE = 31;

    public static final int MODE_ORDERINGMATCHINGRULE = 32;

    public static final int MODE_SYNTAX = 40;

    protected int mode;


    public OpenSchemaBrowserAction()
    {
        super();
        this.mode = MODE_NONE;
    }


    public OpenSchemaBrowserAction( int mode )
    {
        super();
        this.mode = mode;
    }


    public void run()
    {
        if ( mode == MODE_NONE )
        {
            SchemaBrowser.select( null );
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            SchemaBrowser.select( getOcd() );
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            SchemaBrowser.select( getAtd() );
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            SchemaBrowser.select( getEmrd() );
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            SchemaBrowser.select( getSmrd() );
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            SchemaBrowser.select( getOmrd() );
        }
        else if ( mode == MODE_SYNTAX )
        {
            SchemaBrowser.select( getLsd() );
        }
        else
        {
            SchemaBrowser.select( null );
        }
    }


    public String getText()
    {
        if ( mode == MODE_NONE )
        {
            return "Open Schema Browser";
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            return "Object Class Definition";
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            return "Attribute Type Definiton";
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            return "Equality Matching Rule Definiton";
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            return "Substring Matching Rule Definiton";
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            return "Ordering Matching Rule Definiton";
        }
        else if ( mode == MODE_SYNTAX )
        {
            return "Syntax Definiton";
        }
        else
        {
            return "Open Schema Browser";
        }
    }


    public ImageDescriptor getImageDescriptor()
    {
        if ( mode == MODE_NONE )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_OCD );
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ATD );
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_MRD_EQUALITY );
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_MRD_SUBSTRING );
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_MRD_ORDERING );
        }
        else if ( mode == MODE_SYNTAX )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LSD );
        }
        else
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {

        if ( mode == MODE_NONE )
        {
            return true;
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            return getOcd() != null;
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            return getAtd() != null;
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            return getEmrd() != null;
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            return getSmrd() != null;
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            return getOmrd() != null;
        }
        else if ( mode == MODE_SYNTAX )
        {
            return getLsd() != null;
        }
        else
        {
            return false;
        }
    }


    private LdapSyntaxDescription getLsd()
    {
        AttributeTypeDescription atd = getAtd();

        if ( atd != null && atd.getSyntaxDescriptionNumericOIDTransitive() != null
            && atd.getSchema().hasLdapSyntaxDescription( atd.getSyntaxDescriptionNumericOIDTransitive() ) )
        {
            return atd.getSchema().getLdapSyntaxDescription( atd.getSyntaxDescriptionNumericOIDTransitive() );
        }

        return null;
    }


    private ObjectClassDescription getOcd()
    {
        if ( getSelectedAttributes().length == 0 && getSelectedValues().length == 1
            && getSelectedValues()[0].getAttribute().isObjectClassAttribute() )
        {
            String ocdName = getSelectedValues()[0].getStringValue();
            if ( ocdName != null
                && getSelectedValues()[0].getAttribute().getEntry().getConnection().getSchema()
                    .hasObjectClassDescription( ocdName ) )
            {
                return getSelectedValues()[0].getAttribute().getEntry().getConnection().getSchema()
                    .getObjectClassDescription( ocdName );
            }
        }

        return null;
    }


    private AttributeTypeDescription getAtd()
    {
        if ( ( getSelectedValues().length + getSelectedAttributes().length ) + getSelectedAttributeHierarchies().length == 1 )
        {
            AttributeTypeDescription atd = null;
            if ( getSelectedValues().length == 1 )
            {
                atd = getSelectedValues()[0].getAttribute().getAttributeTypeDescription();
            }
            else if ( getSelectedAttributes().length == 1 )
            {
                atd = getSelectedAttributes()[0].getAttributeTypeDescription();
            }
            else if ( getSelectedAttributeHierarchies().length == 1 && getSelectedAttributeHierarchies()[0].size() == 1 )
            {
                atd = getSelectedAttributeHierarchies()[0].getAttribute().getAttributeTypeDescription();
            }

            return atd;
        }

        return null;
    }


    private MatchingRuleDescription getEmrd()
    {
        AttributeTypeDescription atd = getAtd();

        if ( atd != null && atd.getEqualityMatchingRuleDescriptionOIDTransitive() != null
            && atd.getSchema().hasMatchingRuleDescription( atd.getEqualityMatchingRuleDescriptionOIDTransitive() ) )
        {
            return atd.getSchema().getMatchingRuleDescription( atd.getEqualityMatchingRuleDescriptionOIDTransitive() );
        }

        return null;
    }


    private MatchingRuleDescription getSmrd()
    {
        AttributeTypeDescription atd = getAtd();

        if ( atd != null && atd.getSubstringMatchingRuleDescriptionOIDTransitive() != null
            && atd.getSchema().hasMatchingRuleDescription( atd.getSubstringMatchingRuleDescriptionOIDTransitive() ) )
        {
            return atd.getSchema().getMatchingRuleDescription( atd.getSubstringMatchingRuleDescriptionOIDTransitive() );
        }

        return null;
    }


    private MatchingRuleDescription getOmrd()
    {
        AttributeTypeDescription atd = getAtd();

        if ( atd != null && atd.getOrderingMatchingRuleDescriptionOIDTransitive() != null
            && atd.getSchema().hasMatchingRuleDescription( atd.getOrderingMatchingRuleDescriptionOIDTransitive() ) )
        {
            return atd.getSchema().getMatchingRuleDescription( atd.getOrderingMatchingRuleDescriptionOIDTransitive() );
        }

        return null;
    }
}
