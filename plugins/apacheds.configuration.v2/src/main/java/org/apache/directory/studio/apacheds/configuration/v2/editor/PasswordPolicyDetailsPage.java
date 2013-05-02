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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.apache.directory.server.config.beans.PasswordPolicyBean;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Password Policy type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private PasswordPoliciesMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input password policy */
    private PasswordPolicyBean passwordPolicy;

    // UI Widgets
    private Button enabledCheckbox;
    private Text idText;
    private Text descriptionText;
    private ComboViewer checkQualityComboViewer;
    private Button minimumLengthCheckbox;
    private Text minimumLengthText;
    private Button maximumLengthCheckbox;
    private Text maximumLengthText;
    private Text minimumAgeText;
    private Text maximumAgeText;
    private Button expireWarningCheckbox;
    private Text expireWarningText;
    private Button graceAuthenticationLimitCheckbox;
    private Text graceAuthenticationLimitText;
    private Button graceExpireCheckbox;
    private Text graceExpireText;
    private Button mustChangeCheckbox;
    private Button allowUserChangeCheckbox;
    private Button safeModifyCheckbox;
    private Button lockoutCheckbox;
    private Text lockoutDurationText;
    private Text maxFailureText;
    private Text failureCountIntervalText;
    private Button inHistoryCheckbox;
    private Text inHistoryText;

    // Listeners
    /** The Text Modify Listener */
    private ModifyListener textModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    /** The button Selection Listener */
    private SelectionListener buttonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    /** The viewer Selection Changed Listener */
    private ISelectionChangedListener viewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    private VerifyListener integerVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };


    /**
     * Creates a new instance of PartitionDetailsPage.
     *
     * @param pmdb
     *      the associated Master Details Block
     */
    public PasswordPolicyDetailsPage( PasswordPoliciesMasterDetailsBlock pmdb )
    {
        masterDetailsBlock = pmdb;
    }


    /**
     * {@inheritDoc}
     */
    public void createContents( Composite parent )
    {
        FormToolkit toolkit = mform.getToolkit();
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout( layout );

        createDetailsSection( toolkit, parent );
        createQualitySection( toolkit, parent );
        createExpirationSection( toolkit, parent );
        createOptionsSection( toolkit, parent );
        createLockoutSection( toolkit, parent );
    }


    /**
     * Creates the Details Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createDetailsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Replication Consumer Details" );
        section.setDescription( "Set the properties of the replication consumer." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Enabled Checkbox
        enabledCheckbox = toolkit.createButton( client, "Enabled", SWT.CHECK );
        enabledCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // ID Text
        toolkit.createLabel( client, "ID:" );
        idText = toolkit.createText( client, "" );
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Description Text
        toolkit.createLabel( client, "Description:" );
        descriptionText = toolkit.createText( client, "" );
        descriptionText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Quality section.
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createQualitySection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Quality" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 3, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Check Quality (pwdCheckQuality)
        toolkit.createLabel( composite, "Check Quality:" );
        checkQualityComboViewer = new ComboViewer( composite );
        checkQualityComboViewer.setContentProvider( new ArrayContentProvider() );
        checkQualityComboViewer.setInput( new CheckQuality[]
            { CheckQuality.DISABLED, CheckQuality.RELAXED, CheckQuality.STRICT } );
        checkQualityComboViewer.getControl().setLayoutData(
            new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Minimum Length (pwdMinLength)
        minimumLengthCheckbox = toolkit.createButton( composite, "Enable Mimimum Length", SWT.CHECK );
        minimumLengthCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        toolkit.createLabel( composite, "   " );
        minimumLengthText = toolkit.createText( composite, "" );

        // Maximum Length (pwdMaxLength)
        maximumLengthCheckbox = toolkit.createButton( composite, "Enable Maximum Length", SWT.CHECK );
        maximumLengthCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        toolkit.createLabel( composite, "   " );
        maximumLengthText = toolkit.createText( composite, "" );
    }


    /**
     * Creates the Expiration section.
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createExpirationSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Expiration" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 3, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Minimum Age (pwdMinAge)
        toolkit.createLabel( composite, "Mimimum Age:" );
        minimumAgeText = toolkit.createText( composite, "" );
        minimumAgeText.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Maximum Age (pwdMaxAge)
        toolkit.createLabel( composite, "Maximum Age:" );
        maximumAgeText = toolkit.createText( composite, "" );
        maximumAgeText.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Expire Warning (pwdExpireWarning)
        expireWarningCheckbox = toolkit.createButton( composite, "Enable Expire Warning", SWT.CHECK );
        expireWarningCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        toolkit.createLabel( composite, "   " );
        expireWarningText = toolkit.createText( composite, "" );

        // Grace Authentication Limit (pwdGraceAuthNLimit)
        graceAuthenticationLimitCheckbox = toolkit.createButton( composite, "Enable Grace Authentication Limit",
            SWT.CHECK );
        graceAuthenticationLimitCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        toolkit.createLabel( composite, "   " );
        graceAuthenticationLimitText = toolkit.createText( composite, "" );

        // Grace Expire (pwdGraceExpire)
        graceExpireCheckbox = toolkit.createButton( composite, "Enable Grace Expire", SWT.CHECK );
        graceExpireCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        toolkit.createLabel( composite, "   " );
        graceExpireText = toolkit.createText( composite, "" );
    }


    /**
     * Creates the Options section.
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createOptionsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Options" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Must Change (pwdMustChange)
        mustChangeCheckbox = toolkit.createButton( composite, "Enable Must Change", SWT.CHECK );
        mustChangeCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Allow User Change (pwdAllowUserChange)
        allowUserChangeCheckbox = toolkit.createButton( composite, "Enable Allow User Change", SWT.CHECK );
        allowUserChangeCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Safe Modify (pwdSafeModify)
        safeModifyCheckbox = toolkit.createButton( composite, "Enable Safe Modify", SWT.CHECK );
        safeModifyCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );
    }


    /**
     * Creates the Lockout section.
     *
     * @param toolkit the toolkit to use
     * @param parent the parent composite
     */
    private void createLockoutSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Lockout" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( 3, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Lockout (pwdLockout)
        lockoutCheckbox = toolkit.createButton( composite, "Enable Lockout", SWT.CHECK );
        lockoutCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        // Lockout Duration (pwdLockoutDuration)
        toolkit.createLabel( composite, "Lockout Duration:" );
        lockoutDurationText = toolkit.createText( composite, "" );
        lockoutDurationText.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Max Failure (pwdMaxFailure)
        toolkit.createLabel( composite, "Max Failure:" );
        maxFailureText = toolkit.createText( composite, "" );
        maxFailureText.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Failure Count Interval (pwdFailureCountInterval)
        toolkit.createLabel( composite, "Failure Count Interval:" );
        failureCountIntervalText = toolkit.createText( composite, "" );
        failureCountIntervalText.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // In History (pwdInHistory)
        inHistoryCheckbox = toolkit.createButton( composite, "Enable In History", SWT.CHECK );
        inHistoryCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );

        toolkit.createLabel( composite, "   " );
        inHistoryText = toolkit.createText( composite, "" );
    }


    /**
     * Adds listeners to UI fields.
     */
    private void addListeners()
    {
        enabledCheckbox.addSelectionListener( buttonSelectionListener );
        idText.addModifyListener( textModifyListener );
        descriptionText.addModifyListener( textModifyListener );
        checkQualityComboViewer.addSelectionChangedListener( viewerSelectionChangedListener );
        minimumLengthCheckbox.addSelectionListener( buttonSelectionListener );
        minimumLengthText.addModifyListener( textModifyListener );
        minimumLengthText.addVerifyListener( integerVerifyListener );
        maximumLengthCheckbox.addSelectionListener( buttonSelectionListener );
        maximumLengthText.addModifyListener( textModifyListener );
        maximumLengthText.addVerifyListener( integerVerifyListener );
        minimumAgeText.addModifyListener( textModifyListener );
        minimumAgeText.addVerifyListener( integerVerifyListener );
        maximumAgeText.addModifyListener( textModifyListener );
        maximumAgeText.addVerifyListener( integerVerifyListener );
        expireWarningCheckbox.addSelectionListener( buttonSelectionListener );
        expireWarningText.addModifyListener( textModifyListener );
        expireWarningText.addVerifyListener( integerVerifyListener );
        graceAuthenticationLimitCheckbox.addSelectionListener( buttonSelectionListener );
        graceAuthenticationLimitText.addModifyListener( textModifyListener );
        graceAuthenticationLimitText.addVerifyListener( integerVerifyListener );
        graceExpireCheckbox.addSelectionListener( buttonSelectionListener );
        graceExpireText.addModifyListener( textModifyListener );
        graceExpireText.addVerifyListener( integerVerifyListener );
        mustChangeCheckbox.addSelectionListener( buttonSelectionListener );
        allowUserChangeCheckbox.addSelectionListener( buttonSelectionListener );
        safeModifyCheckbox.addSelectionListener( buttonSelectionListener );
        lockoutCheckbox.addSelectionListener( buttonSelectionListener );
        lockoutDurationText.addModifyListener( textModifyListener );
        lockoutDurationText.addVerifyListener( integerVerifyListener );
        maxFailureText.addModifyListener( textModifyListener );
        maxFailureText.addVerifyListener( integerVerifyListener );
        failureCountIntervalText.addModifyListener( textModifyListener );
        failureCountIntervalText.addVerifyListener( integerVerifyListener );
        inHistoryText.addVerifyListener( integerVerifyListener );
        inHistoryCheckbox.addSelectionListener( buttonSelectionListener );
        inHistoryText.addModifyListener( textModifyListener );
        inHistoryText.addVerifyListener( integerVerifyListener );
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
        enabledCheckbox.removeSelectionListener( buttonSelectionListener );
        idText.removeModifyListener( textModifyListener );
        descriptionText.removeModifyListener( textModifyListener );
        checkQualityComboViewer.removeSelectionChangedListener( viewerSelectionChangedListener );
        minimumLengthCheckbox.removeSelectionListener( buttonSelectionListener );
        minimumLengthText.removeModifyListener( textModifyListener );
        minimumLengthText.removeVerifyListener( integerVerifyListener );
        maximumLengthCheckbox.removeSelectionListener( buttonSelectionListener );
        maximumLengthText.removeModifyListener( textModifyListener );
        maximumLengthText.removeVerifyListener( integerVerifyListener );
        minimumAgeText.removeModifyListener( textModifyListener );
        minimumAgeText.removeVerifyListener( integerVerifyListener );
        maximumAgeText.removeModifyListener( textModifyListener );
        maximumAgeText.removeVerifyListener( integerVerifyListener );
        expireWarningCheckbox.removeSelectionListener( buttonSelectionListener );
        expireWarningText.removeModifyListener( textModifyListener );
        expireWarningText.removeVerifyListener( integerVerifyListener );
        graceAuthenticationLimitCheckbox.removeSelectionListener( buttonSelectionListener );
        graceAuthenticationLimitText.removeModifyListener( textModifyListener );
        graceAuthenticationLimitText.removeVerifyListener( integerVerifyListener );
        graceExpireCheckbox.removeSelectionListener( buttonSelectionListener );
        graceExpireText.removeModifyListener( textModifyListener );
        graceExpireText.removeVerifyListener( integerVerifyListener );
        mustChangeCheckbox.removeSelectionListener( buttonSelectionListener );
        allowUserChangeCheckbox.removeSelectionListener( buttonSelectionListener );
        safeModifyCheckbox.removeSelectionListener( buttonSelectionListener );
        lockoutCheckbox.removeSelectionListener( buttonSelectionListener );
        lockoutDurationText.removeModifyListener( textModifyListener );
        lockoutDurationText.removeVerifyListener( integerVerifyListener );
        maxFailureText.removeModifyListener( textModifyListener );
        maxFailureText.removeVerifyListener( integerVerifyListener );
        failureCountIntervalText.removeModifyListener( textModifyListener );
        failureCountIntervalText.removeVerifyListener( integerVerifyListener );
        inHistoryText.removeVerifyListener( integerVerifyListener );
        inHistoryCheckbox.removeSelectionListener( buttonSelectionListener );
        inHistoryText.removeModifyListener( textModifyListener );
        inHistoryText.removeVerifyListener( integerVerifyListener );
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            passwordPolicy = ( PasswordPolicyBean ) ssel.getFirstElement();
        }
        else
        {
            passwordPolicy = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( passwordPolicy != null )
        {
            // Enabled
            passwordPolicy.setEnabled( enabledCheckbox.getSelection() );

            // ID
            passwordPolicy.setPwdId( ServerConfigurationEditorUtils.checkEmptyString( idText.getText() ) );

            // Description
            passwordPolicy
                .setDescription( ServerConfigurationEditorUtils.checkEmptyString( descriptionText.getText() ) );

            // Check Quality
            passwordPolicy.setPwdCheckQuality( getPwdCheckQuality() );

            // Miminum Length
            if ( minimumLengthCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdMinLength( Integer.parseInt( minimumLengthText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdMinLength( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdMinLength( 0 );
            }

            // Maximum Length
            if ( maximumLengthCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdMaxLength( Integer.parseInt( maximumLengthText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdMaxLength( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdMaxLength( 0 );
            }

            // Minimum Age
            try
            {
                passwordPolicy.setPwdMinAge( Integer.parseInt( minimumAgeText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdMinAge( 0 );
            }

            // Maximum Age
            try
            {
                passwordPolicy.setPwdMaxAge( Integer.parseInt( maximumAgeText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdMaxAge( 0 );
            }

            // Expire Warning
            if ( expireWarningCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdExpireWarning( Integer.parseInt( expireWarningText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdExpireWarning( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdExpireWarning( 0 );
            }

            // Grace Authentication Limit
            if ( graceAuthenticationLimitCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdGraceAuthNLimit( Integer.parseInt( graceAuthenticationLimitText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdGraceAuthNLimit( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdGraceAuthNLimit( 0 );
            }

            // Grace Expire
            if ( graceExpireCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdGraceExpire( Integer.parseInt( graceExpireText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdGraceExpire( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdGraceExpire( 0 );
            }

            // Must Change
            passwordPolicy.setPwdMustChange( mustChangeCheckbox.getSelection() );

            // Allow User Change
            passwordPolicy.setPwdAllowUserChange( allowUserChangeCheckbox.getSelection() );

            // Safe Modify
            passwordPolicy.setPwdSafeModify( safeModifyCheckbox.getSelection() );

            // Lockout
            passwordPolicy.setPwdLockout( lockoutCheckbox.getSelection() );

            // Lockout Duration
            try
            {
                passwordPolicy.setPwdLockoutDuration( Integer.parseInt( lockoutDurationText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdLockoutDuration( 0 );
            }

            // Max Failure
            try
            {
                passwordPolicy.setPwdMaxFailure( Integer.parseInt( maxFailureText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdMaxFailure( 0 );
            }

            // Failure Count Interval
            try
            {
                passwordPolicy.setPwdFailureCountInterval( Integer.parseInt( failureCountIntervalText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdFailureCountInterval( 0 );
            }

            // In History
            if ( inHistoryCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdInHistory( Integer.parseInt( inHistoryText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdInHistory( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdInHistory( 0 );
            }
        }
    }


    /**
     * Gets the password policy check quality.
     *
     * @return the password policy check quality
     */
    private int getPwdCheckQuality()
    {
        IStructuredSelection selection = ( StructuredSelection ) checkQualityComboViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            CheckQuality checkQuality = ( CheckQuality ) selection.getFirstElement();

            return checkQuality.getValue();
        }

        return CheckQuality.DISABLED.getValue();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStale()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( passwordPolicy != null )
        {
            // Checking if this is the default password policy
            boolean isDefaultPasswordPolicy = PasswordPoliciesPage.isDefaultPasswordPolicy( passwordPolicy );
            
            // Enabled
            enabledCheckbox.setSelection( passwordPolicy.isEnabled() );

            // ID
            idText.setText( ServerConfigurationEditorUtils.checkNull( passwordPolicy.getPwdId() ) );
            idText.setEnabled( !isDefaultPasswordPolicy );

            // Description
            descriptionText.setText( ServerConfigurationEditorUtils.checkNull( passwordPolicy.getDescription() ) );
            descriptionText.setEnabled( !isDefaultPasswordPolicy );

            // Check Quality
            checkQualityComboViewer.setSelection( new StructuredSelection( CheckQuality.valueOf( passwordPolicy
                .getPwdCheckQuality() ) ) );

            // Miminum Length
            int minimumLength = passwordPolicy.getPwdMinLength();
            minimumLengthCheckbox.setSelection( minimumLength != 0 );
            minimumLengthText.setText( "" + minimumLength );

            // Maximum Length
            int maximumLength = passwordPolicy.getPwdMaxLength();
            maximumLengthCheckbox.setSelection( maximumLength != 0 );
            maximumLengthText.setText( "" + maximumLength );

            // Minimum Age
            minimumAgeText.setText( "" + passwordPolicy.getPwdMinAge() );

            // Maximum Age
            maximumAgeText.setText( "" + passwordPolicy.getPwdMaxAge() );

            // Expire Warning
            int expireWarning = passwordPolicy.getPwdExpireWarning();
            expireWarningCheckbox.setSelection( expireWarning != 0 );
            expireWarningText.setText( "" + expireWarning );

            // Grace Authentication Limit
            int graceAuthenticationLimit = passwordPolicy.getPwdGraceAuthNLimit();
            graceAuthenticationLimitCheckbox.setSelection( graceAuthenticationLimit != 0 );
            graceAuthenticationLimitText.setText( "" + graceAuthenticationLimit );

            // Grace Expire
            int graceExpire = passwordPolicy.getPwdGraceExpire();
            graceExpireCheckbox.setSelection( graceExpire != 0 );
            graceExpireText.setText( "" + graceExpire );

            // Must Change
            mustChangeCheckbox.setSelection( passwordPolicy.isPwdMustChange() );

            // Allow User Change
            allowUserChangeCheckbox.setSelection( passwordPolicy.isPwdAllowUserChange() );

            // Safe Modify
            safeModifyCheckbox.setSelection( passwordPolicy.isPwdSafeModify() );

            // Lockout
            lockoutCheckbox.setSelection( passwordPolicy.isPwdLockout() );

            // Lockout Duration
            lockoutDurationText.setText( "" + passwordPolicy.getPwdLockoutDuration() );

            // Max Failure
            maxFailureText.setText( "" + passwordPolicy.getPwdMaxFailure() );

            // Failure Count Interval
            failureCountIntervalText.setText( "" + passwordPolicy.getPwdFailureCountInterval() );

            // In History
            inHistoryCheckbox.setSelection( passwordPolicy.getPwdInHistory() != 0 );
            inHistoryText.setText( "" + passwordPolicy.getPwdInHistory() );
        }

        addListeners();
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        //        idText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }

    /**
     * This enum is used for the check quality value.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private enum CheckQuality
    {
        DISABLED(0),
        RELAXED(1),
        STRICT(2);

        /** The value */
        private int value;


        /**
         * Creates a new instance of CheckQuality.
         *
         * @param value the value
         */
        private CheckQuality( int value )
        {
            this.value = value;
        }


        /**
         * Gets the value.
         *
         * @return the value
         */
        public int getValue()
        {
            return value;
        }


        public static CheckQuality valueOf( int value )
        {
            for ( CheckQuality checkQuality : CheckQuality.class.getEnumConstants() )
            {
                if ( checkQuality.getValue() == value )
                {
                    return checkQuality;
                }
            }

            throw new IllegalArgumentException( "There is no CheckQuality value for :" + value );
        }


        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            switch ( this )
            {
                case DISABLED:
                    return "Disabled";
                case RELAXED:
                    return "Relaxed";
                case STRICT:
                    return "Strict";
            }

            return super.toString();
        }
    }
}
