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
import org.apache.directory.studio.apacheds.configuration.editor.v152.dialogs.SaslRealmDialog;
import org.apache.directory.studio.apacheds.configuration.model.v152.SaslQualityOfProtectionEnum;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerConfigurationV152;
import org.apache.directory.studio.apacheds.configuration.model.v152.SupportedMechanismEnum;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

    private List<String> saslRealms;

    // UI Fields
    private CheckboxTableViewer supportedMechanismsTableViewer;
    private Button selectAllSupportedMechanismsButton;
    private Button deselectAllSupportedMechanismsButton;
    private Text saslHostText;
    private Text saslPrincipalText;
    private Text searchBaseDnText;
    private CheckboxTableViewer saslQualityOfProtectionTableViewer;
    private Button selectAllQualityOfProtectionButton;
    private Button deselectAllQualityOfProtectionButton;
    private CheckboxTableViewer saslRealmsTableViewer;
    private Button addSaslRealmButton;
    private Button editSaslRealmsButton;
    private Button deleteSaslRealmButton;


    /**
     * Creates a new instance of AuthenticationPage.
     *
     * @param editor
     *      the associated editor
     */
    public AuthenticationPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
        saslRealms = new ArrayList<String>();
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
        supportedMechanismsTableViewer.setInput( new SupportedMechanismEnum[]
            { SupportedMechanismEnum.SIMPLE, SupportedMechanismEnum.CRAM_MD5, SupportedMechanismEnum.DIGEST_MD5,
                SupportedMechanismEnum.GSSAPI } );

        // Select All Button
        selectAllSupportedMechanismsButton = toolkit.createButton( client, "Select All", SWT.PUSH );
        selectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Deselect All Button
        deselectAllSupportedMechanismsButton = toolkit.createButton( client, "Deselect All", SWT.PUSH );
        deselectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
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
        saslHostText = toolkit.createText( client, "" );
        saslHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Principal
        toolkit.createLabel( client, "SASL Principal:" );
        saslPrincipalText = toolkit.createText( client, "" );
        saslPrincipalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Base DN
        toolkit.createLabel( client, "Search Base DN:" );
        searchBaseDnText = toolkit.createText( client, "" );
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
        saslQualityOfProtectionTableViewer = new CheckboxTableViewer( saslQualityOfProtectionTable );
        saslQualityOfProtectionTableViewer.setContentProvider( new ArrayContentProvider() );
        saslQualityOfProtectionTableViewer.setInput( new SaslQualityOfProtectionEnum[]
            { SaslQualityOfProtectionEnum.AUTH, SaslQualityOfProtectionEnum.AUTH_INT,
                SaslQualityOfProtectionEnum.AUTH_CONF } );

        // Select All Button
        selectAllQualityOfProtectionButton = toolkit.createButton( client, "Select All", SWT.PUSH );
        selectAllQualityOfProtectionButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Deselect All Button
        deselectAllQualityOfProtectionButton = toolkit.createButton( client, "Deselect All", SWT.PUSH );
        deselectAllQualityOfProtectionButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
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
        saslRealmsTableViewer = new CheckboxTableViewer( saslRealmsTable );
        saslRealmsTableViewer.setContentProvider( new ArrayContentProvider() );

        // Add Button
        addSaslRealmButton = toolkit.createButton( client, "Add...", SWT.PUSH );
        addSaslRealmButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Edit Button
        editSaslRealmsButton = toolkit.createButton( client, "Edit...", SWT.PUSH );
        editSaslRealmsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editSaslRealmsButton.setEnabled( false );

        // Delete Button
        deleteSaslRealmButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        deleteSaslRealmButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteSaslRealmButton.setEnabled( false );
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

        // SASL Host
        saslHostText.setText( configuration.getSaslHost() );

        // SASL Principal
        saslPrincipalText.setText( configuration.getSaslPrincipal() );

        // Search Base DN
        searchBaseDnText.setText( configuration.getSearchBaseDn() );

        // SASL Quality Of Protection
        saslQualityOfProtectionTableViewer.setCheckedElements( configuration.getSaslQops().toArray() );

        // SASL Realms
        saslRealms.addAll( configuration.getSaslRealms() );
        saslRealmsTableViewer.setInput( saslRealms );
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        //  The Modify Listener
        ModifyListener modifyListener = new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        };

        // Supported Authentication Mechanisms
        selectAllSupportedMechanismsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                supportedMechanismsTableViewer.setAllChecked( true );
                setEditorDirty();
            }
        } );
        deselectAllSupportedMechanismsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                supportedMechanismsTableViewer.setAllChecked( false );
                setEditorDirty();
            }
        } );
        supportedMechanismsTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                setEditorDirty();
            }
        } );

        // SASL Host
        saslHostText.addModifyListener( modifyListener );

        // SASL Principal
        saslPrincipalText.addModifyListener( modifyListener );

        // Search Base DN
        searchBaseDnText.addModifyListener( modifyListener );

        // SASL Quality Of Protection
        selectAllQualityOfProtectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                saslQualityOfProtectionTableViewer.setAllChecked( true );
                setEditorDirty();
            }
        } );
        deselectAllQualityOfProtectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                saslQualityOfProtectionTableViewer.setAllChecked( false );
                setEditorDirty();
            }
        } );
        saslQualityOfProtectionTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                setEditorDirty();
            }
        } );

        // SASL Realms
        saslRealmsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                editSaslRealmsButton.setEnabled( !event.getSelection().isEmpty() );
                deleteSaslRealmButton.setEnabled( !event.getSelection().isEmpty() );
            }
        } );
        saslRealmsTableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editSelectedSaslRealm();
            }
        } );
        addSaslRealmButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                SaslRealmDialog dialog = new SaslRealmDialog( "" );
                if ( Dialog.OK == dialog.open() && dialog.isDirty() )
                {
                    String newSaslRealm = dialog.getSaslRealm();
                    if ( newSaslRealm != null && !"".equals( newSaslRealm ) && !saslRealms.contains( newSaslRealm ) )
                    {
                        saslRealms.add( newSaslRealm );

                        saslRealmsTableViewer.refresh();
                        setEditorDirty();
                    }
                }
            }
        } );
        editSaslRealmsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editSelectedSaslRealm();
            }
        } );

        // The SelectionListener for the Binary Attributes Delete Button
        deleteSaslRealmButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) saslRealmsTableViewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    String saslRealm = ( String ) selection.getFirstElement();
                    saslRealms.remove( saslRealm );

                    saslRealmsTableViewer.refresh();
                    setEditorDirty();
                }
            }
        } );
    }


    /**
     * Opens a SASL Realm Dialog with the selected SASL Realm in the SASL 
     * Realms Table Viewer.
     */
    private void editSelectedSaslRealm()
    {
        StructuredSelection selection = ( StructuredSelection ) saslRealmsTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            String oldSaslRealm = ( String ) selection.getFirstElement();

            SaslRealmDialog dialog = new SaslRealmDialog( oldSaslRealm );
            if ( Dialog.OK == dialog.open() && dialog.isDirty() )
            {
                saslRealms.remove( oldSaslRealm );

                String newSaslRealm = dialog.getSaslRealm();
                if ( newSaslRealm != null && !"".equals( newSaslRealm ) && !saslRealms.contains( newSaslRealm ) )
                {
                    saslRealms.add( newSaslRealm );
                }

                saslRealmsTableViewer.refresh();
                setEditorDirty();
            }
        }
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

        // Supported Authentication Mechanisms
        List<SupportedMechanismEnum> supportedMechanismsList = new ArrayList<SupportedMechanismEnum>();
        for ( Object supportedMechanism : supportedMechanismsTableViewer.getCheckedElements() )
        {
            supportedMechanismsList.add( ( SupportedMechanismEnum ) supportedMechanism );
        }
        configuration.setSupportedMechanisms( supportedMechanismsList );

        // SASL Host
        configuration.setSaslHost( saslHostText.getText() );

        // SASL Principal
        configuration.setSaslPrincipal( saslPrincipalText.getText() );

        // Search Base DN
        configuration.setSearchBaseDn( searchBaseDnText.getText() );

        // SASL Quality Of Protection
        List<SaslQualityOfProtectionEnum> saslQoPList = new ArrayList<SaslQualityOfProtectionEnum>();
        for ( Object qop : supportedMechanismsTableViewer.getCheckedElements() )
        {
            saslQoPList.add( ( SaslQualityOfProtectionEnum ) qop );
        }
        configuration.setSaslQops( saslQoPList );

        // SASL Realms
        configuration.setSaslRealms( saslRealms );
    }
}
