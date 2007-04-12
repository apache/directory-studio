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
package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class GeneralPage extends FormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".BasicPage";

    /** The Page Title */
    private static final String TITLE = "General";

    // UI Fields
    private Text portText;
    private Combo authenticationCombo;
    private Text principalText;
    private Text passwordText;
    private Button showPasswordCheckbox;
    private Button allowAnonymousAccessCheckbox;
    private Text maxTimeLimitText;
    private Text maxSizeLimitText;
    private Text synchPeriodText;
    private Text maxThreadsText;
    private Button enableAccesControlCheckbox;
    private Button enableNTPCheckbox;
    private Button enableKerberosCheckbox;
    private Button enableChangePasswordCheckbox;


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public GeneralPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( "General" );

        Composite parent = form.getBody();
        parent.setLayout( new TableWrapLayout() );
        FormToolkit toolkit = managedForm.getToolkit();

        createSettingsSection( parent, toolkit );
        createLimitsSection( parent, toolkit );
        createOptionsSection( parent, toolkit );

        initFromInput();
        addListeners();
    }


    /**
     * Creates the Settings Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Settings" );
        section.setDescription( "Set the settings of the server." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Port
        toolkit.createLabel( client, "Port:" );
        portText = toolkit.createText( client, "" );
        portText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        portText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Authentication
        toolkit.createLabel( client, "Authentication:" );
        authenticationCombo = new Combo( client, SWT.SIMPLE );
        authenticationCombo.setItems( new String[]
            { "Simple" } );
        authenticationCombo.setText( "Simple" );
        authenticationCombo.setEnabled( false );
        authenticationCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Principal
        toolkit.createLabel( client, "Principal:" );
        principalText = toolkit.createText( client, "" );
        principalText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Password
        toolkit.createLabel( client, "Password:" );
        passwordText = toolkit.createText( client, "" );
        passwordText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        passwordText.setEchoChar( '\u2022' );

        // Show Password
        toolkit.createLabel( client, "" );
        showPasswordCheckbox = toolkit.createButton( client, "Show password", SWT.CHECK );
        showPasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        showPasswordCheckbox.setSelection( false );
        showPasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( showPasswordCheckbox.getSelection() )
                {
                    passwordText.setEchoChar( '\0' );
                }
                else
                {
                    passwordText.setEchoChar( '\u2022' );
                }
            }
        } );

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox = toolkit.createButton( client, "Allow Anonymous Access", SWT.CHECK );
        allowAnonymousAccessCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Creates the Limits Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createLimitsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Limits" );
        section.setDescription( "Set the limits of the server." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 4, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Max. Time Limit
        toolkit.createLabel( client, "Max. Time Limit:" );
        maxTimeLimitText = toolkit.createText( client, "" );
        maxTimeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        maxTimeLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Max. Size Limit
        toolkit.createLabel( client, "Max. Size Limit:" );
        maxSizeLimitText = toolkit.createText( client, "" );
        maxSizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        maxSizeLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Synchronization Period
        toolkit.createLabel( client, "Synchronization Period:" );
        synchPeriodText = toolkit.createText( client, "" );
        synchPeriodText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        synchPeriodText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Max. Threads
        toolkit.createLabel( client, "Max. Threads:" );
        maxThreadsText = toolkit.createText( client, "" );
        maxThreadsText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        maxThreadsText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
    }


    /**
     * Creates the Options Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createOptionsSection( Composite parent, FormToolkit toolkit )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 4;
        section.setText( "Options" );
        section.setDescription( "Set the options of the server." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, true );
        client.setLayout( glayout );
        section.setClient( client );

        // Enable Access Control
        enableAccesControlCheckbox = toolkit.createButton( client, "Enable Access Control", SWT.CHECK );
        enableAccesControlCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable NTP
        enableNTPCheckbox = toolkit.createButton( client, "Enable NTP", SWT.CHECK );
        enableNTPCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable Kerberos
        enableKerberosCheckbox = toolkit.createButton( client, "Enable Kerberos", SWT.CHECK );
        enableKerberosCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable Change Password
        enableChangePasswordCheckbox = toolkit.createButton( client, "Enable Change Password", SWT.CHECK );
        enableChangePasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        ServerConfiguration configuration = ( ( ServerConfigurationEditorInput ) getEditorInput() )
            .getServerConfiguration();

        // Port
        portText.setText( "" + configuration.getPort() );

        // Principal
        String principal = configuration.getPrincipal();
        if ( principal != null )
        {
            principalText.setText( principal );
        }

        // Password
        String password = configuration.getPassword();
        if ( password != null )
        {
            passwordText.setText( password );
        }

        // Allow Anonymous Access
        allowAnonymousAccessCheckbox.setSelection( configuration.isAllowAnonymousAccess() );

        // Max Time Limit
        maxTimeLimitText.setText( "" + configuration.getMaxTimeLimit() );

        // Max Size Limit
        maxSizeLimitText.setText( "" + configuration.getMaxSizeLimit() );

        // Synchronization Period
        synchPeriodText.setText( "" + configuration.getSynchronizationPeriod() );

        // Max Threads
        maxThreadsText.setText( "" + configuration.getMaxThreads() );

        // Enable Access Control
        enableAccesControlCheckbox.setSelection( configuration.isEnableAccessControl() );

        // Enable NTP
        enableNTPCheckbox.setSelection( configuration.isEnableNTP() );

        // Enable Kerberos
        enableKerberosCheckbox.setSelection( configuration.isEnableKerberos() );

        // Enable Change Password
        enableChangePasswordCheckbox.setSelection( configuration.isEnableChangePassword() );
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        portText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        authenticationCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        principalText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        passwordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        allowAnonymousAccessCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        } );

        maxTimeLimitText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        maxSizeLimitText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        synchPeriodText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        maxThreadsText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setEditorDirty();
            }
        } );

        enableAccesControlCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        } );

        enableNTPCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        } );

        enableKerberosCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        } );

        enableChangePasswordCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setEditorDirty();
            }
        } );
    }


    /**
     * Sets the Editor as dirty.
     */
    private void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) getEditor() ).setDirty( true );
    }
}
