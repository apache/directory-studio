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


/**
 * This is the main widget of the OpenLDAP ACL visual editor.
 * <p>
 * It extends ScrolledComposite.
 * 
 * <pre>
 * .----------------(##Visual Editor##|  Source  )----------------.
 * |                                                              |
 * |  Access to "What"                                            |
 * | .----------------------------------------------------------. |
 * | |                                                          | |
 * ...
 * | |                                                          | |
 * | `----------------------------------------------------------' |
 * |  Access to "Who"                                             |
 * | .----------------------------------------------------------. |
 * | |                                                          | |
 * ...
 * | |                                                          | |
 * | `----------------------------------------------------------' |
 * |                                                              |
 * |                                                              |
 * `--------------------------------------------------------------'
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclVisualEditorComposite extends ScrolledComposite
{
    /** The ACL context */
    private OpenLdapAclValueWithContext context;

    // UI widgets
    /** The WHAT clause Widget */
    private OpenLdapAclWhatClauseWidget whatClauseWidget;
    
    /** The WHO clause widget */
    private OpenLdapAclWhoClausesBuilderWidget whoClausesBuilderWidget;


    /**
     * Creates a new instance of OpenLdapAclVisualEditorComposite. It contains
     * the WhatClause and WhoClause widgets :
     *
     * <pre>
     *  Access to "What"
     * .----------------------------------------------------------.
     * |                                                          |
     * ...
     * |                                                          |
     * `----------------------------------------------------------'
     *  Access to "Who"
     * .----------------------------------------------------------.
     * |                                                          |
     * ...
     * |                                                          |
     * `----------------------------------------------------------'
     * </pre>
     * @param parent a widget which will be the parent of the new instance (cannot be null)
     * @param style the style of widget to construct
     */
    public OpenLdapAclVisualEditorComposite( Composite parent, OpenLdapAclValueWithContext context, int style )
    {
        super( parent, style | SWT.H_SCROLL | SWT.V_SCROLL );
        
        this.context = context;

        // Creating the composite
        Composite visualEditorComposite = new Composite( this, SWT.NONE );
        visualEditorComposite.setLayout( new GridLayout() );
        visualEditorComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the WhatClause widget
        whatClauseWidget = new OpenLdapAclWhatClauseWidget( this, visualEditorComposite, context );

        // Creating the WhoClause widget
        whoClausesBuilderWidget = new OpenLdapAclWhoClausesBuilderWidget( this, context );
        whoClausesBuilderWidget.create( visualEditorComposite );

        // Configuring the composite
        setContent( visualEditorComposite );
        setExpandHorizontal( true );
        setExpandVertical( true );
        setMinSize( visualEditorComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    }


    /**
     * Populate the GUI elements with the Context content. 
     */
    public void refresh()
    {
        // Setting the input ACL to the widgets
        whatClauseWidget.refresh();
        whoClausesBuilderWidget.refresh();
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
        AclItem aclItem = new AclItem( context.getAclItem().getWhatClause(), context.getAclItem().getWhoClauses() );
        
        return aclItem.toString();
    }


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings()
    {
        whatClauseWidget.saveWidgetSettings();
    }
}
