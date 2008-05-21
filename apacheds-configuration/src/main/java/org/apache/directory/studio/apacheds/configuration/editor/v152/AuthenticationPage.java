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
package org.apache.directory.studio.apacheds.configuration.editor.v152;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.editor.SaveableFormPage;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerConfigurationV152;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Authentication Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AuthenticationPage extends FormPage implements SaveableFormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".V152.AuthenticationPage";

    /** The Page Title */
    private static final String TITLE = "Authentication";

    // UI Fields
    private CheckboxTableViewer supportedMechanismsTableViewer;
    private Button selectAllSupportedMechanismsButton;
    private Button deselectAllSupportedMechanismsButton;


    /**
     * Creates a new instance of AuthenticationPage.
     *
     * @param editor
     *      the associated editor
     */
    public AuthenticationPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( "Authentication" );

        Composite parent = form.getBody();
        parent.setLayout( new TableWrapLayout() );
        FormToolkit toolkit = managedForm.getToolkit();

        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout() );
        TableWrapData compositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        compositeTableWrapData.grabHorizontal = true;
        composite.setLayoutData( compositeTableWrapData );

        createSupportedAuthenticationMechanismsSection( composite, toolkit );
        createSaslSettingsSection( composite, toolkit );
        createSaslQualityOfProtectionSection( composite, toolkit );
        createSaslRealmsSection( composite, toolkit );

        initFromInput();
        addListeners();
    }


    /**
     * Creates the Supported Authentication Mechanisms Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSupportedAuthenticationMechanismsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "Supported Authentication Mechanisms" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Supported Authentication Mechanisms Table
        Table supportedMechanismsTable = toolkit.createTable( client, SWT.CHECK );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 76;
        supportedMechanismsTable.setLayoutData( gd );
        supportedMechanismsTableViewer = new CheckboxTableViewer( supportedMechanismsTable );
        supportedMechanismsTableViewer.setContentProvider( new ArrayContentProvider() );
        supportedMechanismsTableViewer.setInput( new String[]
            { "SIMPLE", "CRAM-MD5 (SASL)", "DIGEST-MD5 (SASL)", "GSSAPI (SASL)" } );

        // Select All Button
        selectAllSupportedMechanismsButton = toolkit.createButton( client, "Select All", SWT.PUSH );
        selectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Deselect All Button
        deselectAllSupportedMechanismsButton = toolkit.createButton( client, "Deselect All", SWT.PUSH );
        deselectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * Creates the SASL Realms Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSaslRealmsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "SASL Realms" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // SASL Realms Table
        Table saslRealmsTable = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 82;
        saslRealmsTable.setLayoutData( gd );
        TableViewer saslRealmsTableViewer = new CheckboxTableViewer( saslRealmsTable );
        saslRealmsTableViewer.setContentProvider( new ArrayContentProvider() );

        // Add Button
        Button addSaslRealmButton = toolkit.createButton( client, "Add...", SWT.PUSH );
        addSaslRealmButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Edit Button
        Button editSaslRealmsButton = toolkit.createButton( client, "Edit...", SWT.PUSH );
        editSaslRealmsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Delete Button
        Button deleteSaslRealmButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        deleteSaslRealmButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * Creates the SASL Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSaslSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "SASL Settings" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // SASL Host
        toolkit.createLabel( client, "SASL Host:" );
        Text saslHostText = toolkit.createText( client, "" );
        saslHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Principal
        toolkit.createLabel( client, "SASL Principal:" );
        Text saslPrincipalText = toolkit.createText( client, "" );
        saslPrincipalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Base DN
        toolkit.createLabel( client, "Search Base DN:" );
        Text searchBaseDnText = toolkit.createText( client, "" );
        searchBaseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the SASL Quality Of Protection Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSaslQualityOfProtectionSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "SASL Quality Of Protection" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // SASL Quality Of Protection Table
        Table saslQualityOfProtectionTable = toolkit.createTable( client, SWT.CHECK );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 76;
        saslQualityOfProtectionTable.setLayoutData( gd );
        TableViewer saslQualityOfProtectionTableViewer = new CheckboxTableViewer( saslQualityOfProtectionTable );
        saslQualityOfProtectionTableViewer.setContentProvider( new ArrayContentProvider() );
        saslQualityOfProtectionTableViewer.setInput( new String[]
            { "auth (Authentication only)", "auth-int (Authentication with integrity protection)",
                "auth-conf (Authentication with integrity and privacy protection)" } );

        // Select All Button
        Button selectAllQualityOfProtectionButton = toolkit.createButton( client, "Select All", SWT.PUSH );
        selectAllQualityOfProtectionButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Deselect All Button
        Button deselectAllQualityOfProtectionButton = toolkit.createButton( client, "Deselect All", SWT.PUSH );
        deselectAllQualityOfProtectionButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        ServerConfigurationV152 configuration = ( ServerConfigurationV152 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        // Supported Authentication Mechanisms
        supportedMechanismsTableViewer.setCheckedElements( configuration.getSupportedMechanisms().toArray() );

    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        //  The Selection Listener
        SelectionListener selectionListener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        };

        selectAllSupportedMechanismsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                supportedMechanismsTableViewer.setAllChecked( true );
            }
        } );

        deselectAllSupportedMechanismsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                supportedMechanismsTableViewer.setAllChecked( false );
            }
        } );

        supportedMechanismsTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                setEditorDirty();
            }
        } );

        selectAllSupportedMechanismsButton.addSelectionListener( selectionListener );
        deselectAllSupportedMechanismsButton.addSelectionListener( selectionListener );
    }


    /**
     * Sets the Editor as dirty.
     */
    private void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) getEditor() ).setDirty( true );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.editor.SavableWizardPage#save()
     */
    public void save()
    {
        ServerConfigurationV152 configuration = ( ServerConfigurationV152 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        List<String> supportedMechanismsList = new ArrayList<String>();
        for ( Object supportedMechanism : supportedMechanismsTableViewer.getCheckedElements() )
        {
            supportedMechanismsList.add( ( String ) supportedMechanism );
        }
        configuration.setSupportedMechanisms( supportedMechanismsList );
    }
}
