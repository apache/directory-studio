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
package org.apache.directory.studio.apacheds.configuration.editor.v155;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.editor.v155.dialogs.NtlmProviderDialog;
import org.apache.directory.studio.apacheds.configuration.editor.v155.dialogs.SaslRealmDialog;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerConfigurationV155;
import org.apache.directory.studio.apacheds.configuration.model.v155.SupportedMechanismEnum;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.PlatformUI;
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
 */
public class AuthenticationPage extends FormPage
{
    /** The Page ID */
    public static final String ID = ServerConfigurationEditor.ID + ".V155.AuthenticationPage"; //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "AuthenticationPage.Authentication" ); //$NON-NLS-1$

    private List<String> saslRealms;

    // UI Fields
    private CheckboxTableViewer supportedMechanismsTableViewer;
    private Button selectAllSupportedMechanismsButton;
    private Button deselectAllSupportedMechanismsButton;
    private Button editSupportedMechanismButton;
    private Text saslHostText;
    private Text saslPrincipalText;
    private Text searchBaseDnText;
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


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        PlatformUI.getWorkbench().getHelpSystem().setHelp( getPartControl(),
            ApacheDSConfigurationPluginConstants.PLUGIN_ID + "." + "configuration_editor_155" ); //$NON-NLS-1$ //$NON-NLS-2$

        ScrolledForm form = managedForm.getForm();
        form.setText( Messages.getString( "AuthenticationPage.Authentication" ) ); //$NON-NLS-1$

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
        section.setText( Messages.getString( "AuthenticationPage.SupportedAuthenticationMechanisms" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Supported Authentication Mechanisms Table
        Table supportedMechanismsTable = toolkit.createTable( client, SWT.CHECK );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 110;
        supportedMechanismsTable.setLayoutData( gd );
        supportedMechanismsTableViewer = new CheckboxTableViewer( supportedMechanismsTable );
        supportedMechanismsTableViewer.setContentProvider( new ArrayContentProvider() );
        supportedMechanismsTableViewer.setInput( new SupportedMechanismEnum[]
            { SupportedMechanismEnum.SIMPLE, SupportedMechanismEnum.CRAM_MD5, SupportedMechanismEnum.DIGEST_MD5,
                SupportedMechanismEnum.GSSAPI, SupportedMechanismEnum.NTLM, SupportedMechanismEnum.GSS_SPNEGO } );

        // Edit Button
        editSupportedMechanismButton = toolkit.createButton( client,
            Messages.getString( "AuthenticationPage.Edit" ), SWT.PUSH ); //$NON-NLS-1$
        editSupportedMechanismButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editSupportedMechanismButton.setEnabled( false );

        // Select All Button
        selectAllSupportedMechanismsButton = toolkit.createButton( client, Messages
            .getString( "AuthenticationPage.SelectAll" ), SWT.PUSH ); //$NON-NLS-1$
        selectAllSupportedMechanismsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Deselect All Button
        deselectAllSupportedMechanismsButton = toolkit.createButton( client, Messages
            .getString( "AuthenticationPage.DeselectAll" ), SWT.PUSH ); //$NON-NLS-1$
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
        section.setText( Messages.getString( "AuthenticationPage.SaslSettings" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // SASL Host
        toolkit.createLabel( client, Messages.getString( "AuthenticationPage.SaslHost" ) ); //$NON-NLS-1$
        saslHostText = toolkit.createText( client, "" ); //$NON-NLS-1$
        saslHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Principal
        toolkit.createLabel( client, Messages.getString( "AuthenticationPage.SaslPrincipal" ) ); //$NON-NLS-1$
        saslPrincipalText = toolkit.createText( client, "" ); //$NON-NLS-1$
        saslPrincipalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Base Dn
        toolkit.createLabel( client, Messages.getString( "AuthenticationPage.SearchBaseDN" ) ); //$NON-NLS-1$
        searchBaseDnText = toolkit.createText( client, "" ); //$NON-NLS-1$
        searchBaseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
        section.setText( Messages.getString( "AuthenticationPage.SaslRealms" ) ); //$NON-NLS-1$
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
        addSaslRealmButton = toolkit.createButton( client, Messages.getString( "AuthenticationPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        addSaslRealmButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Edit Button
        editSaslRealmsButton = toolkit.createButton( client, Messages.getString( "AuthenticationPage.Edit" ), SWT.PUSH ); //$NON-NLS-1$
        editSaslRealmsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editSaslRealmsButton.setEnabled( false );

        // Delete Button
        deleteSaslRealmButton = toolkit.createButton( client,
            Messages.getString( "AuthenticationPage.Delete" ), SWT.PUSH ); //$NON-NLS-1$
        deleteSaslRealmButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteSaslRealmButton.setEnabled( false );
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        ServerConfigurationV155 configuration = ( ServerConfigurationV155 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        // Supported Authentication Mechanisms
        supportedMechanismsTableViewer.setCheckedElements( configuration.getSupportedMechanisms().toArray() );

        // SASL Host
        saslHostText.setText( configuration.getSaslHost() );

        // SASL Principal
        saslPrincipalText.setText( configuration.getSaslPrincipal() );

        // Search Base Dn
        searchBaseDnText.setText( configuration.getSearchBaseDn() );

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
        supportedMechanismsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) supportedMechanismsTableViewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    SupportedMechanismEnum selected = ( SupportedMechanismEnum ) selection.getFirstElement();
                    editSupportedMechanismButton
                        .setEnabled( ( SupportedMechanismEnum.NTLM.equals( selected ) || SupportedMechanismEnum.GSS_SPNEGO
                            .equals( selected ) ) );
                }
                else
                {
                    editSupportedMechanismButton.setEnabled( false );
                }
            }
        } );
        supportedMechanismsTableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editSelectedSupportedMechanism();
            }
        } );
        editSupportedMechanismButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editSelectedSupportedMechanism();
            }
        } );
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

        // Search Base Dn
        searchBaseDnText.addModifyListener( modifyListener );

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
                SaslRealmDialog dialog = new SaslRealmDialog( "" ); //$NON-NLS-1$
                if ( Dialog.OK == dialog.open() && dialog.isDirty() )
                {
                    String newSaslRealm = dialog.getSaslRealm();
                    if ( newSaslRealm != null && !"".equals( newSaslRealm ) && !saslRealms.contains( newSaslRealm ) ) //$NON-NLS-1$
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
     * Opens a NTLM Provider with the selected Supported Mechanism the Supported Mechanisms Table.
     */
    private void editSelectedSupportedMechanism()
    {
        StructuredSelection selection = ( StructuredSelection ) supportedMechanismsTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            SupportedMechanismEnum selected = ( SupportedMechanismEnum ) selection.getFirstElement();

            if ( SupportedMechanismEnum.NTLM.equals( selected ) || SupportedMechanismEnum.GSS_SPNEGO.equals( selected ) )
            {
                String oldNtlmProvider = selected.getNtlmProviderFqcn();

                NtlmProviderDialog dialog = new NtlmProviderDialog( oldNtlmProvider );
                if ( Dialog.OK == dialog.open() && dialog.isDirty() )
                {
                    selected.setNtlmProviderFqcn( dialog.getNtlmProvider() );
                    supportedMechanismsTableViewer.refresh();
                    setEditorDirty();
                }
            }
        }
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
                if ( newSaslRealm != null && !"".equals( newSaslRealm ) && !saslRealms.contains( newSaslRealm ) ) //$NON-NLS-1$
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


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        ServerConfigurationV155 configuration = ( ServerConfigurationV155 ) ( ( ServerConfigurationEditor ) getEditor() )
            .getServerConfiguration();

        // Supported Authentication Mechanisms
        if ( ( supportedMechanismsTableViewer != null ) && ( supportedMechanismsTableViewer.getTable() != null )
            && ( !supportedMechanismsTableViewer.getTable().isDisposed() ) )
        {
            List<SupportedMechanismEnum> supportedMechanismsList = new ArrayList<SupportedMechanismEnum>();
            for ( Object supportedMechanism : supportedMechanismsTableViewer.getCheckedElements() )
            {
                supportedMechanismsList.add( ( SupportedMechanismEnum ) supportedMechanism );
            }
            configuration.setSupportedMechanisms( supportedMechanismsList );
        }

        // SASL Host
        if ( ( saslHostText != null ) && ( !saslHostText.isDisposed() ) )
        {
            configuration.setSaslHost( saslHostText.getText() );
        }

        // SASL Principal
        if ( ( saslPrincipalText != null ) && ( !saslPrincipalText.isDisposed() ) )
        {
            configuration.setSaslPrincipal( saslPrincipalText.getText() );
        }

        // Search Base Dn
        if ( ( searchBaseDnText != null ) && ( !searchBaseDnText.isDisposed() ) )
        {
            configuration.setSearchBaseDn( searchBaseDnText.getText() );
        }

        // SASL Realms
        if ( ( saslRealmsTableViewer != null ) && ( saslRealmsTableViewer.getTable() != null )
            && ( !saslRealmsTableViewer.getTable().isDisposed() ) )
        {
            configuration.setSaslRealms( saslRealms );
        }
    }
}
