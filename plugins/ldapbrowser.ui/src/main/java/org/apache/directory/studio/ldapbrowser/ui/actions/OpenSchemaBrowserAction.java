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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.MutableLdapSyntaxImpl;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser.SchemaBrowserManager;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action opens the Schema Browser 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
            return Messages.getString( "OpenSchemaBrowserAction.OpenSchemaBrowser" ); //$NON-NLS-1$
        }
        else if ( mode == MODE_OBJECTCLASS )
        {
            return Messages.getString( "OpenSchemaBrowserAction.ObjectDescription" ); //$NON-NLS-1$
        }
        else if ( mode == MODE_ATTRIBUTETYPE )
        {
            return Messages.getString( "OpenSchemaBrowserAction.AttributeDescription" ); //$NON-NLS-1$
        }
        else if ( mode == MODE_EQUALITYMATCHINGRULE )
        {
            return Messages.getString( "OpenSchemaBrowserAction.EqualityDescription" ); //$NON-NLS-1$
        }
        else if ( mode == MODE_SUBSTRINGMATCHINGRULE )
        {
            return Messages.getString( "OpenSchemaBrowserAction.SubstringDescription" ); //$NON-NLS-1$
        }
        else if ( mode == MODE_ORDERINGMATCHINGRULE )
        {
            return Messages.getString( "OpenSchemaBrowserAction.OrderingDescription" ); //$NON-NLS-1$
        }
        else if ( mode == MODE_SYNTAX )
        {
            return Messages.getString( "OpenSchemaBrowserAction.SyntaxDescription" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "OpenSchemaBrowserAction.OpenSchemaBrowser" ); //$NON-NLS-1$
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
            return getConnection() != null;
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
    private MutableLdapSyntaxImpl getLsd()
    {
        if ( getConnection() != null )
        {
            Schema schema = getConnection().getSchema();
            AttributeType atd = getAtd();

            if ( atd != null && SchemaUtils.getSyntaxNumericOidTransitive( atd, schema ) != null
                && schema.hasLdapSyntaxDescription( SchemaUtils.getSyntaxNumericOidTransitive( atd, schema ) ) )
            {
                return schema.getLdapSyntaxDescription( SchemaUtils.getSyntaxNumericOidTransitive( atd, schema ) );
            }
        }

        return null;
    }


    /**
     * Gets the Object Class Description.
     *
     * @return
     *      the Object Class Description
     */
    private ObjectClass getOcd()
    {
        if ( getSelectedAttributes().length == 0 && getSelectedValues().length == 1
            && getSelectedValues()[0].getAttribute().isObjectClassAttribute() )
        {
            String ocdName = getSelectedValues()[0].getStringValue();
            if ( ocdName != null
                && getSelectedValues()[0].getAttribute().getEntry().getBrowserConnection().getSchema()
                    .hasObjectClassDescription( ocdName ) )
            {
                return getSelectedValues()[0].getAttribute().getEntry().getBrowserConnection().getSchema()
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
    private AttributeType getAtd()
    {
        if ( ( getSelectedValues().length + getSelectedAttributes().length ) + getSelectedAttributeHierarchies().length == 1 )
        {
            AttributeType atd = null;
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
    private IBrowserConnection getConnection()
    {
        if ( ( getSelectedValues().length + getSelectedAttributes().length ) + getSelectedAttributeHierarchies().length == 1 )
        {
            IBrowserConnection connection = null;
            if ( getSelectedValues().length == 1 )
            {
                connection = getSelectedValues()[0].getAttribute().getEntry().getBrowserConnection();
            }
            else if ( getSelectedAttributes().length == 1 )
            {
                connection = getSelectedAttributes()[0].getEntry().getBrowserConnection();
            }
            else if ( getSelectedAttributeHierarchies().length == 1 && getSelectedAttributeHierarchies()[0].size() == 1 )
            {
                connection = getSelectedAttributeHierarchies()[0].getAttribute().getEntry().getBrowserConnection();
            }

            return connection;
        }
        else if ( getSelectedConnections().length == 1 )
        {
            Connection connection = getSelectedConnections()[0];
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( connection );
            return browserConnection;
        }
        else if ( getSelectedEntries().length == 1 )
        {
            return getSelectedEntries()[0].getBrowserConnection();
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            return getSelectedSearchResults()[0].getEntry().getBrowserConnection();
        }
        else if ( getSelectedBookmarks().length == 1 )
        {
            return getSelectedBookmarks()[0].getBrowserConnection();
        }
        else if ( getSelectedSearches().length == 1 )
        {
            return getSelectedSearches()[0].getBrowserConnection();
        }

        return null;
    }


    /**
     * Gets the Equality Matching Rule Description.
     *
     * @return
     *      the Equality Matching Rule Description
     */
    private MatchingRule getEmrd()
    {
        if ( getConnection() != null )
        {
            Schema schema = getConnection().getSchema();
            AttributeType atd = getAtd();
            if ( atd != null
                && SchemaUtils.getEqualityMatchingRuleNameOrNumericOidTransitive( atd, schema ) != null
                && schema.hasLdapSyntaxDescription( SchemaUtils.getEqualityMatchingRuleNameOrNumericOidTransitive( atd,
                    schema ) ) )
            {
                return schema.getMatchingRuleDescription( SchemaUtils
                    .getEqualityMatchingRuleNameOrNumericOidTransitive( atd, schema ) );
            }
        }
        return null;
    }


    /**
     * Gets the Substring Matching Rule Description.
     *
     * @return
     *      the Substring Matching Rule Description
     */
    private MatchingRule getSmrd()
    {
        if ( getConnection() != null )
        {
            Schema schema = getConnection().getSchema();
            AttributeType atd = getAtd();
            if ( atd != null
                && SchemaUtils.getSubstringMatchingRuleNameOrNumericOidTransitive( atd, schema ) != null
                && schema.hasLdapSyntaxDescription( SchemaUtils.getSubstringMatchingRuleNameOrNumericOidTransitive(
                    atd, schema ) ) )
            {
                return schema.getMatchingRuleDescription( SchemaUtils
                    .getSubstringMatchingRuleNameOrNumericOidTransitive( atd, schema ) );
            }
        }
        return null;
    }


    /**
     * Gets the Ordering Matching Rule Description.
     *
     * @return
     *      the Ordering Matching Rule Description
     */
    private MatchingRule getOmrd()
    {
        if ( getConnection() != null )
        {
            Schema schema = getConnection().getSchema();
            AttributeType atd = getAtd();
            if ( atd != null
                && SchemaUtils.getOrderingMatchingRuleNameOrNumericOidTransitive( atd, schema ) != null
                && schema.hasLdapSyntaxDescription( SchemaUtils.getOrderingMatchingRuleNameOrNumericOidTransitive( atd,
                    schema ) ) )
            {
                return schema.getMatchingRuleDescription( SchemaUtils
                    .getOrderingMatchingRuleNameOrNumericOidTransitive( atd, schema ) );
            }
        }
        return null;
    }
}
