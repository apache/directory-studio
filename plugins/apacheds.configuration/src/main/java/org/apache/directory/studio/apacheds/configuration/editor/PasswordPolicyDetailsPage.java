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
package org.apache.directory.studio.apacheds.configuration.editor;


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
 * <pre>
 * .-------------------------------------------.
 * | Password Policy Details                   |
 * +-------------------------------------------+
 * | Set the properties of the password Policy |
 * |  [X] Enabled                              |
 * |  ID :          [//////////]               |
 * |  Description : [////////////////////////] |
 * |  Attribute   : [////////////////////////] |
 * .-------------------------------------------.
 * | Quality                                   |
 * +-------------------------------------------+
 * | Check quality : [=======================] |
 * | Validator :     [///////////////////////] |
 * | [X] Enable Minimum Length                 |
 * |   Number of chars : [NNN]                 |
 * | [X] Enable Maximum Length                 |
 * |   Number of chars : [NNN]                 |
 * .-------------------------------------------.
 * | Expiration                                |
 * +-------------------------------------------+
 * | Minimum age (seconds): [NNN]              |
 * | Maximum age (seconds): [NNN]              |
 * | [X] Enable Expire Warning                 |
 * |   Number of seconds  : [NNN]              |
 * | [X] Enable Grace Authentication Limit     |
 * |   Number of times    : [NNN]              |
 * | [X] Enable Grace Expire                   |
 * |   Interval (seconds) : [NNN]              |
 * .-------------------------------------------.
 * | Options                                   |
 * +-------------------------------------------+
 * | [X] Enable Must Change                    |
 * | [X] Enable Allow User Change              |
 * | [X] Enable Safe Modify                    |
 * .-------------------------------------------.
 * | Lockout                                   |
 * +-------------------------------------------+
 * | [X] Enable Lockout                        |
 * |   Lockout duration (seconds)   : [NNN]    |
 * |   Maximum Consecutive Failures : [NNN]    |
 * |   Failure Count Interval       : [NNN]    |
 * | [X] Enable Maximum Idle                   |
 * |   Intervals                    : [NNN]    |
 * | [X] Enable In History                     |
 * |   Used passwords stored in Hist: [NNN]    |
 * | [X] Delay                                 |
 * |   Minimum delay (seconds)      : [NNN]    |
 * |   Maximum delay (seconds)      : [NNN]    |
 * +-------------------------------------------+
 * </pre>
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
    private Text validatorText;
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
    private Button maxIdleCheckbox;
    private Text maxIdleText;
    private Text minimumDelayText;
    private Text maximumDelayText;

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

    private ISelectionChangedListener checkQualityComboViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) checkQualityComboViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                CheckQuality checkQuality = ( CheckQuality ) selection.getFirstElement();

                if ( checkQuality == CheckQuality.DISABLED )
                {
                    minimumLengthCheckbox.setEnabled( false );
                    minimumLengthText.setEnabled( false );
                    maximumLengthCheckbox.setEnabled( false );
                    maximumLengthText.setEnabled( false );
                }
                else
                {
                    int minimumLength = 0;
                    int maximumLength = 0;

                    try
                    {
                        minimumLength = Integer.parseInt( minimumLengthText.getText() );
                    }
                    catch ( NumberFormatException e )
                    {
                        // Nothing to do.
                    }

                    try
                    {
                        maximumLength = Integer.parseInt( maximumLengthText.getText() );
                    }
                    catch ( NumberFormatException e )
                    {
                        // Nothing to do.
                    }

                    minimumLengthCheckbox.setEnabled( true );
                    minimumLengthText.setEnabled( minimumLength != 0 );
                    maximumLengthCheckbox.setEnabled( true );
                    maximumLengthText.setEnabled( maximumLength != 0 );
                }
            }
        }
    };

    private SelectionListener minimumLengthCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            minimumLengthText.setEnabled( minimumLengthCheckbox.getSelection() );
        }
    };

    private SelectionListener maximumLengthCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            maximumLengthText.setEnabled( maximumLengthCheckbox.getSelection() );
        }
    };

    private SelectionListener expireWarningCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            expireWarningText.setEnabled( expireWarningCheckbox.getSelection() );
        }
    };

    private SelectionListener graceAuthenticationLimitCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            graceAuthenticationLimitText.setEnabled( graceAuthenticationLimitCheckbox.getSelection() );
        }
    };

    private SelectionListener graceExpireCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            graceExpireText.setEnabled( graceExpireCheckbox.getSelection() );
        }
    };

    private SelectionListener maxIdleCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            maxIdleText.setEnabled( maxIdleCheckbox.getSelection() );
        }
    };

    private SelectionListener inHistoryCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            inHistoryText.setEnabled( inHistoryCheckbox.getSelection() );
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

        // Depending on if the PP is enabled or disabled, we will
        // expose the configuration
        
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
        section.setText( "Password Policy Details" );
        section.setDescription( "Set the properties of the password policy." );
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
        GridLayout gridLayout = new GridLayout( 2, false );
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
            new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Validator
        toolkit.createLabel( composite, "Validator:" );
        validatorText = toolkit.createText( composite, "" );
        validatorText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Minimum Length (pwdMinLength)
        minimumLengthCheckbox = toolkit.createButton( composite, "Enable Minimum Length", SWT.CHECK );
        minimumLengthCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );
        Composite minimumLengthRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Number of characters:" );
        minimumLengthText = toolkit.createText( minimumLengthRadioIndentComposite, "" );
        minimumLengthText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Maximum Length (pwdMaxLength)
        maximumLengthCheckbox = toolkit.createButton( composite, "Enable Maximum Length", SWT.CHECK );
        maximumLengthCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );
        Composite maximumLengthRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Number of characters:" );
        maximumLengthText = toolkit.createText( maximumLengthRadioIndentComposite, "" );
        maximumLengthText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
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
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Minimum Age (pwdMinAge)
        toolkit.createLabel( composite, "Minimum Age (seconds):" );
        minimumAgeText = toolkit.createText( composite, "" );
        minimumAgeText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Maximum Age (pwdMaxAge)
        toolkit.createLabel( composite, "Maximum Age (seconds):" );
        maximumAgeText = toolkit.createText( composite, "" );
        maximumAgeText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Expire Warning (pwdExpireWarning)
        expireWarningCheckbox = toolkit.createButton( composite, "Enable Expire Warning", SWT.CHECK );
        expireWarningCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );
        Composite expireWarningRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Number of seconds:" );
        expireWarningText = toolkit.createText( expireWarningRadioIndentComposite, "" );
        expireWarningText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Grace Authentication Limit (pwdGraceAuthNLimit)
        graceAuthenticationLimitCheckbox = toolkit.createButton( composite, "Enable Grace Authentication Limit",
            SWT.CHECK );
        graceAuthenticationLimitCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );
        Composite graceAuthenticationLimitRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Number of times:" );
        graceAuthenticationLimitText = toolkit.createText( graceAuthenticationLimitRadioIndentComposite, "" );
        graceAuthenticationLimitText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Grace Expire (pwdGraceExpire)
        graceExpireCheckbox = toolkit.createButton( composite, "Enable Grace Expire", SWT.CHECK );
        graceExpireCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );
        Composite graceExpireRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Interval (seconds):" );
        graceExpireText = toolkit.createText( graceExpireRadioIndentComposite, "" );
        graceExpireText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
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
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        // Lockout (pwdLockout)
        lockoutCheckbox = toolkit.createButton( composite, "Enable Lockout", SWT.CHECK );
        lockoutCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Lockout Duration (pwdLockoutDuration)
        toolkit.createLabel( composite, "Lockout Duration (seconds):" );
        lockoutDurationText = toolkit.createText( composite, "" );
        lockoutDurationText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Max Failure (pwdMaxFailure)
        toolkit.createLabel( composite, "Maximum Consecutive Failures (count):" );
        maxFailureText = toolkit.createText( composite, "" );
        maxFailureText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Failure Count Interval (pwdFailureCountInterval)
        toolkit.createLabel( composite, "Failure Count Interval (seconds):" );
        failureCountIntervalText = toolkit.createText( composite, "" );
        failureCountIntervalText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Max Idle (pwdMaxIdle)
        maxIdleCheckbox = toolkit.createButton( composite, "Enable Maximum Idle", SWT.CHECK );
        maxIdleCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 3, 1 ) );
        Composite maxIdleCheckboxRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Interval (seconds):" );
        maxIdleText = toolkit.createText( maxIdleCheckboxRadioIndentComposite, "" );
        maxIdleText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // In History (pwdInHistory)
        inHistoryCheckbox = toolkit.createButton( composite, "Enable In History", SWT.CHECK );
        inHistoryCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );
        Composite inHistoryRadioIndentComposite = createRadioIndentComposite( toolkit, composite,
            "Used passwords stored in history:" );
        inHistoryText = toolkit.createText( inHistoryRadioIndentComposite, "" );
        inHistoryText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        
        // Minimum delay (pwdMinDelay)
        toolkit.createLabel( composite, "Minimum Delay (seconds):" );
        minimumDelayText = toolkit.createText( composite, "" );
        minimumDelayText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Maximum Delay (pwdMaxDelay)
        toolkit.createLabel( composite, "Maximum Delay (seconds):" );
        maximumDelayText = toolkit.createText( composite, "" );
        maximumDelayText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    }


    /**
     * Creates a radio indented composite.
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     * @return a radio indented composite
     */
    private Composite createRadioIndentComposite( FormToolkit toolkit, Composite parent, String text )
    {
        Composite composite = toolkit.createComposite( parent );
        GridLayout gridLayout = new GridLayout( 3, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );

        toolkit.createLabel( composite, "   " );
        toolkit.createLabel( composite, text );

        return composite;
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
        checkQualityComboViewer.addSelectionChangedListener( checkQualityComboViewerSelectionChangedListener );
        validatorText.addModifyListener( textModifyListener );
        minimumLengthCheckbox.addSelectionListener( buttonSelectionListener );
        minimumLengthCheckbox.addSelectionListener( minimumLengthCheckboxSelectionListener );
        minimumLengthText.addModifyListener( textModifyListener );
        minimumLengthText.addVerifyListener( integerVerifyListener );
        maximumLengthCheckbox.addSelectionListener( buttonSelectionListener );
        maximumLengthCheckbox.addSelectionListener( maximumLengthCheckboxSelectionListener );
        maximumLengthText.addModifyListener( textModifyListener );
        maximumLengthText.addVerifyListener( integerVerifyListener );
        minimumAgeText.addModifyListener( textModifyListener );
        minimumAgeText.addVerifyListener( integerVerifyListener );
        maximumAgeText.addModifyListener( textModifyListener );
        maximumAgeText.addVerifyListener( integerVerifyListener );
        expireWarningCheckbox.addSelectionListener( buttonSelectionListener );
        expireWarningCheckbox.addSelectionListener( expireWarningCheckboxSelectionListener );
        expireWarningText.addModifyListener( textModifyListener );
        expireWarningText.addVerifyListener( integerVerifyListener );
        graceAuthenticationLimitCheckbox.addSelectionListener( buttonSelectionListener );
        graceAuthenticationLimitCheckbox.addSelectionListener( graceAuthenticationLimitCheckboxSelectionListener );
        graceAuthenticationLimitText.addModifyListener( textModifyListener );
        graceAuthenticationLimitText.addVerifyListener( integerVerifyListener );
        graceExpireCheckbox.addSelectionListener( buttonSelectionListener );
        graceExpireCheckbox.addSelectionListener( graceExpireCheckboxSelectionListener );
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
        maxIdleCheckbox.addSelectionListener( buttonSelectionListener );
        maxIdleCheckbox.addSelectionListener( maxIdleCheckboxSelectionListener );
        maxIdleText.addModifyListener( textModifyListener );
        maxIdleText.addVerifyListener( integerVerifyListener );
        inHistoryCheckbox.addSelectionListener( buttonSelectionListener );
        inHistoryCheckbox.addSelectionListener( inHistoryCheckboxSelectionListener );
        inHistoryText.addModifyListener( textModifyListener );
        inHistoryText.addVerifyListener( integerVerifyListener );
        minimumDelayText.addModifyListener( textModifyListener );
        minimumDelayText.addVerifyListener( integerVerifyListener );
        maximumDelayText.addModifyListener( textModifyListener );
        maximumDelayText.addVerifyListener( integerVerifyListener );
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
        checkQualityComboViewer.removeSelectionChangedListener( checkQualityComboViewerSelectionChangedListener );
        validatorText.removeModifyListener( textModifyListener );
        minimumLengthCheckbox.removeSelectionListener( buttonSelectionListener );
        minimumLengthCheckbox.removeSelectionListener( minimumLengthCheckboxSelectionListener );
        minimumLengthText.removeModifyListener( textModifyListener );
        minimumLengthText.removeVerifyListener( integerVerifyListener );
        maximumLengthCheckbox.removeSelectionListener( buttonSelectionListener );
        maximumLengthCheckbox.removeSelectionListener( maximumLengthCheckboxSelectionListener );
        maximumLengthText.removeModifyListener( textModifyListener );
        maximumLengthText.removeVerifyListener( integerVerifyListener );
        minimumAgeText.removeModifyListener( textModifyListener );
        minimumAgeText.removeVerifyListener( integerVerifyListener );
        maximumAgeText.removeModifyListener( textModifyListener );
        maximumAgeText.removeVerifyListener( integerVerifyListener );
        expireWarningCheckbox.removeSelectionListener( buttonSelectionListener );
        expireWarningCheckbox.removeSelectionListener( expireWarningCheckboxSelectionListener );
        expireWarningText.removeModifyListener( textModifyListener );
        expireWarningText.removeVerifyListener( integerVerifyListener );
        graceAuthenticationLimitCheckbox.removeSelectionListener( buttonSelectionListener );
        graceAuthenticationLimitCheckbox.removeSelectionListener( graceAuthenticationLimitCheckboxSelectionListener );
        graceAuthenticationLimitText.removeModifyListener( textModifyListener );
        graceAuthenticationLimitText.removeVerifyListener( integerVerifyListener );
        graceExpireCheckbox.removeSelectionListener( buttonSelectionListener );
        graceExpireCheckbox.removeSelectionListener( graceExpireCheckboxSelectionListener );
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
        maxIdleCheckbox.removeSelectionListener( buttonSelectionListener );
        maxIdleCheckbox.removeSelectionListener( maxIdleCheckboxSelectionListener );
        maxIdleText.removeModifyListener( textModifyListener );
        maxIdleText.removeVerifyListener( integerVerifyListener );
        inHistoryCheckbox.removeSelectionListener( buttonSelectionListener );
        inHistoryCheckbox.removeSelectionListener( inHistoryCheckboxSelectionListener );
        inHistoryText.removeModifyListener( textModifyListener );
        inHistoryText.removeVerifyListener( integerVerifyListener );
        minimumDelayText.removeModifyListener( textModifyListener );
        minimumDelayText.removeVerifyListener( integerVerifyListener );
        maximumDelayText.removeModifyListener( textModifyListener );
        maximumDelayText.removeVerifyListener( integerVerifyListener );
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

            // Validator
            passwordPolicy
                .setPwdValidator( ServerConfigurationEditorUtils.checkEmptyString( validatorText.getText() ) );

            // Mininum Length
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

            // Max Idle
            if ( maxIdleCheckbox.getSelection() )
            {
                try
                {
                    passwordPolicy.setPwdMaxIdle( Integer.parseInt( maxIdleText.getText() ) );
                }
                catch ( NumberFormatException e )
                {
                    passwordPolicy.setPwdMaxIdle( 0 );
                }
            }
            else
            {
                passwordPolicy.setPwdMaxIdle( 0 );
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

            // Minimum Delay
            try
            {
                passwordPolicy.setPwdMinDelay( Integer.parseInt( minimumDelayText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdMinDelay( 0 );
            }

            // Maximum Delay
            try
            {
                passwordPolicy.setPwdMaxDelay( Integer.parseInt( maximumDelayText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                passwordPolicy.setPwdMaxDelay( 0 );
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

            // Validator
            validatorText.setText( ServerConfigurationEditorUtils.checkNull( passwordPolicy.getPwdValidator() ) );

            // Mininum Length
            int minimumLength = passwordPolicy.getPwdMinLength();
            minimumLengthCheckbox.setSelection( minimumLength != 0 );
            minimumLengthText.setText( "" + minimumLength );

            // Maximum Length
            int maximumLength = passwordPolicy.getPwdMaxLength();
            maximumLengthCheckbox.setSelection( maximumLength != 0 );
            maximumLengthText.setText( "" + maximumLength );

            if ( getPwdCheckQuality() == 0 )
            {
                minimumLengthCheckbox.setEnabled( false );
                minimumLengthText.setEnabled( false );
                maximumLengthCheckbox.setEnabled( false );
                maximumLengthText.setEnabled( false );
            }
            else
            {
                minimumLengthCheckbox.setEnabled( true );
                minimumLengthText.setEnabled( minimumLength != 0 );
                maximumLengthCheckbox.setEnabled( true );
                maximumLengthText.setEnabled( maximumLength != 0 );
            }

            // Minimum Age
            minimumAgeText.setText( "" + passwordPolicy.getPwdMinAge() );

            // Maximum Age
            maximumAgeText.setText( "" + passwordPolicy.getPwdMaxAge() );

            // Expire Warning
            int expireWarning = passwordPolicy.getPwdExpireWarning();
            expireWarningCheckbox.setSelection( expireWarning != 0 );
            expireWarningText.setText( "" + expireWarning );
            expireWarningText.setEnabled( expireWarning != 0 );

            // Grace Authentication Limit
            int graceAuthenticationLimit = passwordPolicy.getPwdGraceAuthNLimit();
            graceAuthenticationLimitCheckbox.setSelection( graceAuthenticationLimit != 0 );
            graceAuthenticationLimitText.setText( "" + graceAuthenticationLimit );
            graceAuthenticationLimitText.setEnabled( graceAuthenticationLimit != 0 );

            // Grace Expire
            int graceExpire = passwordPolicy.getPwdGraceExpire();
            graceExpireCheckbox.setSelection( graceExpire != 0 );
            graceExpireText.setText( "" + graceExpire );
            graceExpireText.setEnabled( graceExpire != 0 );

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

            // Max Idle
            int maxIdle = passwordPolicy.getPwdMaxIdle();
            maxIdleCheckbox.setSelection( maxIdle != 0 );
            maxIdleText.setText( "" + maxIdle );
            maxIdleText.setEnabled( maxIdle != 0 );

            // In History
            int inHistory = passwordPolicy.getPwdInHistory();
            inHistoryCheckbox.setSelection( inHistory != 0 );
            inHistoryText.setText( "" + inHistory );
            inHistoryText.setEnabled( inHistory != 0 );

            // Minimum Delay
            minimumDelayText.setText( "" + passwordPolicy.getPwdMinDelay() );

            // Maximum Delay
            maximumDelayText.setText( "" + passwordPolicy.getPwdMaxDelay() );
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
