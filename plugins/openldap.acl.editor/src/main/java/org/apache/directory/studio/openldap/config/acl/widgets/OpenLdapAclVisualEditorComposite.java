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
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclItem;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseStar;
import org.apache.directory.studio.openldap.config.acl.model.OpenLdapAclParser;


/**
 * This is the main widget of the OpenLDAP ACL visual editor.
 * <p>
 * It extends ScrolledComposite.
 */
public class OpenLdapAclVisualEditorComposite extends ScrolledComposite
{
    private OpenLdapAclVisualEditorComposite instance;

    /** The ACL  */
    private AclItem acl;

    // UI widgets
    private OpenLdapAclWhatClauseWidget whatClauseWidget;
    private OpenLdapAclWhoClausesBuilderWidget whoClausesBuilderWidget;


    /**
     * Creates a new instance of OpenLdapAclVisualEditorComposite.
     *
     * @param parent a widget which will be the parent of the new instance (cannot be null)
     * @param style the style of widget to construct
     */
    public OpenLdapAclVisualEditorComposite( Composite parent, int style )
    {
        super( parent, style | SWT.H_SCROLL | SWT.V_SCROLL );

        // Creating the composite
        Composite composite = new Composite( this, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the widgets
        createWhatClauseWidget( composite );
        createWhoClausesWidget( composite );

        // Configuring the composite
        setContent( composite );
        setExpandHorizontal( true );
        setExpandVertical( true );
        setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

        instance = this;
    }


    /**
     * Creates the "What" clause widget.
     *
     * @param parent the parent composite
     */
    private void createWhatClauseWidget( Composite parent )
    {
        whatClauseWidget = new OpenLdapAclWhatClauseWidget( this );
        whatClauseWidget.create( parent );
    }


    /**
     * Creates the "Who" clauses widget.
     *
     * @param parent the parent composite
     */
    private void createWhoClausesWidget( Composite parent )
    {
        whoClausesBuilderWidget = new OpenLdapAclWhoClausesBuilderWidget( this );
        whoClausesBuilderWidget.create( parent );
    }


    /**
     * Sets the input. The given ACI Item string is parsed and
     * populated to the GUI elements.
     * 
     *
     * @param input The string representation of the ACI item
     * @throws ParseException if the syntax is invalid
     */
    public void setInput( String input ) throws ParseException
    {
        // Reseting the previous input
        acl = null;

        // Parsing the input string
        OpenLdapAclParser parser = new OpenLdapAclParser();
        acl = parser.parse( input );

        // Setting the input ACL to the widgets
        whatClauseWidget.setInput( acl.getWhatClause() );
        whoClausesBuilderWidget.setInput( acl.getWhoClauses() );
    }


    /**
     * Returns the string representation of the ACI item as defined in GUI.
     * 
     *
     * @return the string representation of the ACI item
     * @throws ParseException if the syntax is invalid
     */
    public String getInput() throws ParseException
    {
        AclItem aclItem = new AclItem( whatClauseWidget.getClause(), whoClausesBuilderWidget.getClauses() );
        return aclItem.toString();
    }


    /**
     * Sets the context.
     * 
     * @param context the context
     */
    public void setContext( OpenLdapAclValueWithContext context )
    {
        whatClauseWidget.setContext( context );
        whoClausesBuilderWidget.setContext( context );
    }


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings()
    {
        whatClauseWidget.saveWidgetSettings();
    }
}
