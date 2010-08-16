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

package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import org.apache.directory.shared.ldap.schema.parsers.AbstractSchemaDescription;
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.parsers.LdapSyntaxDescription;
import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleDescription;
import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleUseDescription;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;


/**
 * This class is used to mark the schema browser input to the navigation history.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaBrowserNavigationLocation extends NavigationLocation
{

    /**
     * Creates a new instance of SchemaBrowserNavigationLocation.
     *
     * @param schemaBrowser the schema browser
     */
    SchemaBrowserNavigationLocation( SchemaBrowser schemaBrowser )
    {
        super( schemaBrowser );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        AbstractSchemaDescription schemaElement = getSchemElement();
        if ( schemaElement != null )
        {
            if ( schemaElement instanceof ObjectClassDescription )
            {

                return Messages.getString( "SchemaBrowserNavigationLocation.ObjectClass" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof AttributeTypeDescription )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.AttributeType" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof LdapSyntaxDescription )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.Syntax" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof MatchingRuleDescription )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.MatchingRule" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof MatchingRuleUseDescription )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.MatchingRuleUse" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else
            {
                return SchemaUtils.toString( schemaElement );
            }
        }
        else
        {
            return super.getText();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void saveState( IMemento memento )
    {
        IBrowserConnection connection = getConnection();
        AbstractSchemaDescription schemaElement = getSchemElement();
        memento.putString( "CONNECTION", connection.getConnection().getId() ); //$NON-NLS-1$
        memento.putString( "SCHEMAELEMENTYPE", schemaElement.getClass().getName() ); //$NON-NLS-1$
        memento.putString( "SCHEMAELEMENTOID", schemaElement.getNumericOid() ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void restoreState( IMemento memento )
    {
        IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager().getBrowserConnectionById(
            memento.getString( "CONNECTION" ) ); //$NON-NLS-1$
        String schemaElementType = memento.getString( "SCHEMAELEMENTYPE" ); //$NON-NLS-1$
        String schemaElementOid = memento.getString( "SCHEMAELEMENTOID" ); //$NON-NLS-1$
        AbstractSchemaDescription schemaElement = null;
        if ( ObjectClassDescription.class.getName().equals( schemaElementType ) )
        {
            schemaElement = connection.getSchema().getObjectClassDescription( schemaElementOid );
        }
        else if ( AttributeTypeDescription.class.getName().equals( schemaElementType ) )
        {
            schemaElement = connection.getSchema().getAttributeTypeDescription( schemaElementOid );
        }
        else if ( LdapSyntaxDescription.class.getName().equals( schemaElementType ) )
        {
            schemaElement = connection.getSchema().getLdapSyntaxDescription( schemaElementOid );
        }
        else if ( MatchingRuleDescription.class.getName().equals( schemaElementType ) )
        {
            schemaElement = connection.getSchema().getMatchingRuleDescription( schemaElementOid );
        }
        else if ( MatchingRuleUseDescription.class.getName().equals( schemaElementType ) )
        {
            schemaElement = connection.getSchema().getMatchingRuleUseDescription( schemaElementOid );
        }

        super.setInput( new SchemaBrowserInput( connection, schemaElement ) );
    }


    /**
     * {@inheritDoc}
     */
    public void restoreLocation()
    {
        IEditorPart editorPart = getEditorPart();
        if ( editorPart != null && editorPart instanceof SchemaBrowser )
        {
            SchemaBrowser schemaBrowser = ( SchemaBrowser ) editorPart;
            Object input = getInput();
            if ( input != null && input instanceof SchemaBrowserInput )
            {
                SchemaBrowserInput sbi = ( SchemaBrowserInput ) input;
                if ( sbi.getConnection() != null && sbi.getSchemaElement() != null )
                {
                    schemaBrowser.setInput( sbi );
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean mergeInto( INavigationLocation currentLocation )
    {
        if ( currentLocation == null )
        {
            return false;
        }

        if ( getClass() != currentLocation.getClass() )
        {
            return false;
        }

        SchemaBrowserNavigationLocation location = ( SchemaBrowserNavigationLocation ) currentLocation;
        AbstractSchemaDescription other = location.getSchemElement();
        AbstractSchemaDescription element = getSchemElement();

        if ( other == null && element == null )
        {
            return true;
        }
        else if ( other == null || element == null )
        {
            return false;
        }
        else
        {
            return element.equals( other );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
    }


    /**
     * Gets the schema element.
     *
     * @return the schema element
     */
    private AbstractSchemaDescription getSchemElement()
    {

        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof SchemaBrowserInput )
        {
            SchemaBrowserInput schemaBrowserInput = ( SchemaBrowserInput ) editorInput;
            AbstractSchemaDescription schemaElement = schemaBrowserInput.getSchemaElement();
            if ( schemaElement != null )
            {
                return schemaElement;
            }
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

        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof SchemaBrowserInput )
        {
            SchemaBrowserInput schemaBrowserInput = ( SchemaBrowserInput ) editorInput;
            return schemaBrowserInput.getConnection();
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "" + getSchemElement(); //$NON-NLS-1$
    }

}
