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


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser.SchemaBrowserManager;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action opens the Schema Browser 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenSchemaBrowserAction extends BrowserAction
{
    /**
     * None Mode
     */
    public static final int MODE_NONE = 0;

    /**
     * Object Class Mode
     */
    public static final int MODE_OBJECTCLASS = 10;

    /**
     * Attribute Type Mode
     */
    public static final int MODE_ATTRIBUTETYPE = 20;

    /**
     * Equality Matching Rule Mode
     */
    public static final int MODE_EQUALITYMATCHINGRULE = 30;

    /**
     * Substring Matching Rule Mode
     */
    public static final int MODE_SUBSTRINGMATCHINGRULE = 31;

    /**
     * Ordering Matching Rule Mode
     */
    public static final int MODE_ORDERINGMATCHINGRULE = 32;

    /**
     * Syntax Mode
     */
    public static final int MODE_SYNTAX = 40;

    protected int mode;


    /**
     * Creates a new instance of OpenSchemaBrowserAction.
     */
    public OpenSchemaBrowserAction()
    {
        super();
        this.mode = MODE_NONE;
    }


    /**
     * Creates a new instance of OpenSchemaBrowserAction.
     *
     * @param mode
     *      the display mode
     */
    public OpenSchemaBrowserAction( int mode )
    {
        super();
        this.mode = mode;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( mode == MODE_NONE )
        {
            SchemaBrowserManager.setInput( getConnection(), null );
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            SchemaBrowserManager.setInput( getConnection(), getOcd() );
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            SchemaBrowserManager.setInput( getConnection(), getAtd() );
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            SchemaBrowserManager.setInput( getConnection(), getEmrd() );
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            SchemaBrowserManager.setInput( getConnection(), getSmrd() );
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            SchemaBrowserManager.setInput( getConnection(), getOmrd() );
        }
        else if ( mode == MODE_SYNTAX )
        {
            SchemaBrowserManager.setInput( getConnection(), getLsd() );
        }
        else
        {
            SchemaBrowserManager.setInput( getConnection(), null );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( mode == MODE_NONE )
        {
            return "Open Schema Browser";
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            return "Object Class Description";
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            return "Attribute Type Description";
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            return "Equality Matching Rule Description";
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            return "Substring Matching Rule Description";
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            return "Ordering Matching Rule Description";
        }
        else if ( mode == MODE_SYNTAX )
        {
            return "Syntax Description";
        }
        else
        {
            return "Open Schema Browser";
        }
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * Gets the LDAP Syntax Description.
     *
     * @return
     *      the LDAP Syntax Description
     */
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


    /**
     * Gets the Object Class Description.
     *
     * @return
     *      the Object Class Drescription
     */
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


    /**
     * Gets the Attribute Type Description.
     *
     * @return
     *      the Attribute Type Description
     */
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


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    private IConnection getConnection()
    {
        if ( ( getSelectedValues().length + getSelectedAttributes().length ) + getSelectedAttributeHierarchies().length == 1 )
        {
            IConnection connection = null;
            if ( getSelectedValues().length == 1 )
            {
                connection = getSelectedValues()[0].getAttribute().getEntry().getConnection();
            }
            else if ( getSelectedAttributes().length == 1 )
            {
                connection = getSelectedAttributes()[0].getEntry().getConnection();
            }
            else if ( getSelectedAttributeHierarchies().length == 1 && getSelectedAttributeHierarchies()[0].size() == 1 )
            {
                connection = getSelectedAttributeHierarchies()[0].getAttribute().getEntry().getConnection();
            }

            return connection;
        }
        else if ( getSelectedConnections().length == 1 )
        {
            return getSelectedConnections()[0];
        }
        else if ( getSelectedEntries().length == 1 )
        {
            return getSelectedEntries()[0].getConnection();
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            return getSelectedSearchResults()[0].getEntry().getConnection();
        }
        else if ( getSelectedBookmarks().length == 1 )
        {
            return getSelectedBookmarks()[0].getConnection();
        }
        else if ( getSelectedSearches().length == 1 )
        {
            return getSelectedSearches()[0].getConnection();
        }

        return null;
    }


    /**
     * Gets the Equality Matching Rule Description.
     *
     * @return
     *      the Equality Matching Rule Description
     */
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


    /**
     * Gets the Substring Matching Rule Description.
     *
     * @return
     *      the Substring Matching Rule Description
     */
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


    /**
     * Gets the Ordering Matching Rule Description.
     *
     * @return
     *      the Ordering Matching Rule Description
     */
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
