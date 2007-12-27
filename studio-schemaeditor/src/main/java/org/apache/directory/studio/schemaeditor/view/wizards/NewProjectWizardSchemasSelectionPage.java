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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.util.Arrays;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Information Page of the NewProjectWizard.
 * <p>
 * It is used to let the user create a new Project
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewProjectWizardSchemasSelectionPage extends WizardPage
{
    // UI Fields
    private Button typeApacheDSButton;
    private Button typeOpenLDAPButton;
    private CheckboxTableViewer coreSchemasTableViewer;


    /**
     * Creates a new instance of NewProjectWizardSchemasSelectionPage.
     */
    protected NewProjectWizardSchemasSelectionPage()
    {
        super( "NewProjectWizardSchemasSelectionPage" );
        setTitle( "Create a Schema project." );
        setDescription( "Please select the core schemas to include in the project." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_PROJECT_NEW_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Server Type Group
        Group serverTypeGroup = new Group( composite, SWT.NONE );
        serverTypeGroup.setText( "Server Type" );
        serverTypeGroup.setLayout( new GridLayout( 2, false ) );
        serverTypeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Type Apache DS Button
        typeApacheDSButton = new Button( serverTypeGroup, SWT.RADIO );
        typeApacheDSButton.setText( "Apache DS" );
        typeApacheDSButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                initCoreSchemasTableViewerApacheDS();
            }
        } );

        // Type OpenLDAP Button
        typeOpenLDAPButton = new Button( serverTypeGroup, SWT.RADIO );
        typeOpenLDAPButton.setText( "OpenLDAP" );
        typeOpenLDAPButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                initCoreSchemasTableViewerOpenLDAP();
            }
        } );

        // Core Schemas Label
        Label coreSchemaslabel = new Label( composite, SWT.NONE );
        coreSchemaslabel.setText( "Choose the 'core' schemas to include:" );
        coreSchemaslabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Core Schemas TableViewer
        coreSchemasTableViewer = new CheckboxTableViewer( new Table( composite, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        gridData.heightHint = 127;
        coreSchemasTableViewer.getTable().setLayoutData( gridData );
        coreSchemasTableViewer.setContentProvider( new ArrayContentProvider() );
        coreSchemasTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA )
                    .createImage();
            }
        } );

        Button coreSchemasTableSelectAllButton = new Button( composite, SWT.PUSH );
        coreSchemasTableSelectAllButton.setText( "Select All" );
        coreSchemasTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        coreSchemasTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                coreSchemasTableViewer.setAllChecked( true );
            }
        } );

        Button coreSchemasTableDeselectAllButton = new Button( composite, SWT.PUSH );
        coreSchemasTableDeselectAllButton.setText( "Deselect All" );
        coreSchemasTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        coreSchemasTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                coreSchemasTableViewer.setAllChecked( false );
            }
        } );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        typeApacheDSButton.setSelection( true );

        initCoreSchemasTableViewerApacheDS();
    }


    /**
     * Initializes the Core Schemas Table with Apache DS Schemas
     */
    private void initCoreSchemasTableViewerApacheDS()
    {
        coreSchemasTableViewer.setAllChecked( false );
        coreSchemasTableViewer.setInput( new String[]
            { "apache", "apachedns", "apachemeta", "autofs", "collective", "corba", "core", "cosine", "dhcp",
                "inetorgperson", "java", "krb5kdc", "mozilla", "nis", "samba", "system" } );
    }


    /**
     * Initializes the Core Schemas Table with Apache DS Schemas
     */
    private void initCoreSchemasTableViewerOpenLDAP()
    {
        coreSchemasTableViewer.setAllChecked( false );
        coreSchemasTableViewer.setInput( new String[]
            { "corba", "core", "cosine", "dyngroup", "inetorgperson", "java", "misc", "nis", "openldap", "ppolicy",
                "system" } );
    }


    /**
     * Gets the schemas selected by the User.
     *
     * @return
     *      the selected schemas
     */
    public String[] getSelectedSchemas()
    {
        return Arrays.asList( coreSchemasTableViewer.getCheckedElements() ).toArray( new String[0] );
    }


    /**
     * Gets the Server Type
     *
     * @return
     *      the Server Type
     */
    public ServerTypeEnum getServerType()
    {
        if ( typeApacheDSButton.getSelection() )
        {
            return ServerTypeEnum.APACHE_DS;
        }
        else if ( typeOpenLDAPButton.getSelection() )
        {
            return ServerTypeEnum.OPENLDAP;
        }
        else
        {
            // Default
            return null;
        }
    }

    /**
     * This enum represents the different server types.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum ServerTypeEnum
    {
        APACHE_DS, OPENLDAP
    }
}
