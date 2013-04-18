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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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
    private PasswordPolicyBean input;

    // UI Widgets
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

        // Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout() );
        TableWrapData compositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        compositeTableWrapData.grabHorizontal = true;
        composite.setLayoutData( compositeTableWrapData );

        createQualitySection( toolkit, composite );
        createExpirationSection( toolkit, composite );
        createOptionsSection( toolkit, composite );
        createLockoutSection( toolkit, composite );
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
        section.setText( "Quality" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
        section.setText( "Expiration" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
        section.setText( "Options" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
        section.setText( "Lockout" );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( PasswordPolicyBean ) ssel.getFirstElement();
        }
        else
        {
            input = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( input != null )
        {
            // TODO
        }
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

        // TODO

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
