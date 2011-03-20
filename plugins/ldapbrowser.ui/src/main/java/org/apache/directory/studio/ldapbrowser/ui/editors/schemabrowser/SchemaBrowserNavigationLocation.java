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


import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.MatchingRuleUse;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
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
        SchemaObject schemaElement = getSchemaElement();
        if ( schemaElement != null )
        {
            if ( schemaElement instanceof ObjectClass )
            {

                return Messages.getString( "SchemaBrowserNavigationLocation.ObjectClass" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof AttributeType )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.AttributeType" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof LdapSyntax )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.Syntax" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof MatchingRule )
            {
                return Messages.getString( "SchemaBrowserNavigationLocation.MatchingRule" ) + SchemaUtils.toString( schemaElement ); //$NON-NLS-1$
            }
            else if ( schemaElement instanceof MatchingRuleUse )
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
        SchemaObject schemaElement = getSchemaElement();
        memento.putString( "CONNECTION", connection.getConnection().getId() ); //$NON-NLS-1$
        memento.putString( "SCHEMAELEMENTYPE", schemaElement.getClass().getName() ); //$NON-NLS-1$
        memento.putString( "SCHEMAELEMENTOID", schemaElement.getOid() ); //$NON-NLS-1$
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
        SchemaObject schemaElement = null;
        
        if ( schemaElementType.contains( ObjectClass.class.getName() ) )
        {
            schemaElement = connection.getSchema().getObjectClassDescription( schemaElementOid );
        }
        else if ( schemaElementType.contains( AttributeType.class.getName() ) )
        {
            schemaElement = connection.getSchema().getAttributeTypeDescription( schemaElementOid );
        }
        else if ( schemaElementType.contains( LdapSyntax.class.getName() ) )
        {
            schemaElement = connection.getSchema().getLdapSyntaxDescription( schemaElementOid );
        }
        else if ( schemaElementType.contains( MatchingRule.class.getName() ) )
        {
            schemaElement = connection.getSchema().getMatchingRuleDescription( schemaElementOid );
        }
        else if ( schemaElementType.contains( MatchingRuleUse.class.getName() ) )
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
        SchemaObject other = location.getSchemaElement();
        SchemaObject element = getSchemaElement();

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
    private SchemaObject getSchemaElement()
    {

        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof SchemaBrowserInput )
        {
            SchemaBrowserInput schemaBrowserInput = ( SchemaBrowserInput ) editorInput;
            SchemaObject schemaElement = schemaBrowserInput.getSchemaElement();
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
        return "" + getSchemaElement(); //$NON-NLS-1$
    }

}
