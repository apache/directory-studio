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


import org.apache.directory.server.config.beans.AuthenticationInterceptorBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.InterceptorBean;
import org.apache.directory.server.config.beans.PasswordPolicyBean;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPoliciesPage extends ServerConfigurationEditorPage
{
    private static final String AUTHENTICATION_INTERCEPTOR_ID = "authenticationInterceptor";
    private static final String PASSWORD_POLICY_ID_DEFAULT = "default";

    /** The Page ID*/
    public static final String ID = PasswordPoliciesPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "PasswordPoliciesPage.PasswordPolicies" ); //$NON-NLS-1$

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


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public PasswordPoliciesPage( ServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        PasswordPoliciesMasterDetailsBlock masterDetailsBlock = new PasswordPoliciesMasterDetailsBlock( this );
        masterDetailsBlock.createContent( getManagedForm() );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent2( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        parent.setLayout( twl );

        // Left Composite
        Composite leftComposite = toolkit.createComposite( parent );
        leftComposite.setLayout( new GridLayout() );
        TableWrapData leftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        leftCompositeTableWrapData.grabHorizontal = true;
        leftComposite.setLayoutData( leftCompositeTableWrapData );

        // Right Composite
        Composite rightComposite = toolkit.createComposite( parent );
        rightComposite.setLayout( new GridLayout() );
        TableWrapData rightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        rightCompositeTableWrapData.grabHorizontal = true;
        rightComposite.setLayoutData( rightCompositeTableWrapData );

        // Creating the sections
        createQualitySection( toolkit, leftComposite );
        createExpirationSection( toolkit, leftComposite );
        createOptionsSection( toolkit, rightComposite );
        createLockoutSection( toolkit, rightComposite );

        //        // Attribute (pwdAttribute)
        //        Text attributeText = toolkit.createText( composite, "" );
        //        attributeText.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

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
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        PasswordPolicyBean passwordPolicy = getPasswordPolicy();

//        // Check Quality
//        checkQualityComboViewer.setSelection( new StructuredSelection( CheckQuality.valueOf( passwordPolicy
//            .getPwdCheckQuality() ) ) );
//
//        // Miminum Length
//        int minimumLength = passwordPolicy.getPwdMinLength();
//        minimumLengthCheckbox.setSelection( minimumLength != 0 );
//        minimumLengthText.setText( "" + minimumLength );
//
//        // Maximum Length
//        int maximumLength = passwordPolicy.getPwdMaxLength();
//        maximumLengthCheckbox.setSelection( maximumLength != 0 );
//        maximumLengthText.setText( "" + maximumLength );
//
//        // Minimum Age
//        minimumAgeText.setText( "" + passwordPolicy.getPwdMinAge() );
//
//        // Maximum Age
//        maximumAgeText.setText( "" + passwordPolicy.getPwdMaxAge() );
//
//        // Expire Warning
//        expireWarningCheckbox.setSelection( passwordPolicy.getPwdExpireWarning() == 0 );
//        expireWarningText.setText( "" + passwordPolicy.getPwdExpireWarning() );
//
//        // Grace Authentication Limit
//        int graceAuthenticationLimit = passwordPolicy.getPwdGraceAuthNLimit();
//        graceAuthenticationLimitCheckbox.setSelection( graceAuthenticationLimit != 0 );
//        graceAuthenticationLimitText.setText( "" + graceAuthenticationLimit );
//
//        // Grace Expire
//        int graceExpire = passwordPolicy.getPwdGraceExpire();
//        graceExpireCheckbox.setSelection( graceExpire != 0 );
//        graceExpireText.setText( "" + graceExpire );
//
//        // Must Change
//        mustChangeCheckbox.setSelection( passwordPolicy.isPwdMustChange() );
//
//        // Allow User Change
//        allowUserChangeCheckbox.setSelection( passwordPolicy.isPwdAllowUserChange() );
//
//        // Safe Modify
//        safeModifyCheckbox.setSelection( passwordPolicy.isPwdSafeModify() );
//
//        // Lockout
//        lockoutCheckbox.setSelection( passwordPolicy.isPwdLockout() );
//
//        // Lockout Duration
//        lockoutDurationText.setText( "" + passwordPolicy.getPwdLockoutDuration() );
//
//        // Max Failure
//        maxFailureText.setText( "" + passwordPolicy.getPwdMaxFailure() );
//
//        // Failure Count Interval
//        failureCountIntervalText.setText( "" + passwordPolicy.getPwdFailureCountInterval() );
//
//        // In History
//        inHistoryCheckbox.setSelection( passwordPolicy.getPwdInHistory() != 0 );
//        inHistoryText.setText( "" + passwordPolicy.getPwdInHistory() );
    }


    /**
     * Gets the Password Policy bean.
     *
     * @return the Password Policy bean
     */
    private PasswordPolicyBean getPasswordPolicy()
    {
        return getPasswordPolicyBean( getDirectoryServiceBean() );
    }


    /**
     * Gets the Password Policy bean.
     *
     * @param directoryServiceBean the directory service bean
     * @return the Password Policy bean
     */
    public static PasswordPolicyBean getPasswordPolicyBean( DirectoryServiceBean directoryServiceBean )
    {
        // Finding the password policy
        PasswordPolicyBean passwordPolicyBean = findPasswordPolicyBean( directoryServiceBean );

        if ( passwordPolicyBean == null )
        {
            addPasswordPolicyBean( directoryServiceBean );
        }

        return passwordPolicyBean;
    }


    /**
     * Gets the Password Policy bean.
     *
     * @param directoryServiceBean the directory service bean
     * @return the Password Policy bean
     */
    private static PasswordPolicyBean findPasswordPolicyBean( DirectoryServiceBean directoryServiceBean )
    {
        return getPasswordPolicyBean( getAuthenticationInterceptorBean( directoryServiceBean ) );
    }


    /**
     * Gets the authentication interceptor.
     *
     * @param directoryServiceBean the directory service bean
     * @return the authentication interceptor
     */
    private static AuthenticationInterceptorBean getAuthenticationInterceptorBean(
        DirectoryServiceBean directoryServiceBean )
    {
        // Looking for the authentication interceptor
        for ( InterceptorBean interceptor : directoryServiceBean.getInterceptors() )
        {
            if ( AUTHENTICATION_INTERCEPTOR_ID.equalsIgnoreCase( interceptor.getInterceptorId() )
                && ( interceptor instanceof AuthenticationInterceptorBean ) )
            {
                return ( AuthenticationInterceptorBean ) interceptor;
            }
        }

        return null;
    }


    /**
     * Gets the Password Policy bean.
     *
     * @param authenticationInterceptor the authentication interceptor
     * @return the Password Policy bean
     */
    private static PasswordPolicyBean getPasswordPolicyBean( AuthenticationInterceptorBean authenticationInterceptor )
    {
        // Looking for the default password policy
        if ( authenticationInterceptor != null )
        {
            for ( PasswordPolicyBean passwordPolicy : authenticationInterceptor.getPasswordPolicies() )
            {
                if ( PASSWORD_POLICY_ID_DEFAULT.equalsIgnoreCase( passwordPolicy.getPwdId() ) )
                {
                    return passwordPolicy;
                }
            }
        }

        return null;
    }


    /**
     * Adds the password policy to the directory service.
     *
     * @param directoryServiceBean the directory service bean
     */
    private static void addPasswordPolicyBean( DirectoryServiceBean directoryServiceBean )
    {
        AuthenticationInterceptorBean authenticationInterceptor = getAuthenticationInterceptorBean( directoryServiceBean );

        if ( authenticationInterceptor != null )
        {
            // Creating the password policy
            PasswordPolicyBean passwordPolicy = new PasswordPolicyBean();

            // Configuring the password policy
            passwordPolicy.setPwdId( PASSWORD_POLICY_ID_DEFAULT );
            // TODO add other parameters

            // Adding the password policy to the authentication interceptor
            authenticationInterceptor.addPasswordPolicies( passwordPolicy );
        }
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


    /**
     * Indicates if the given password policy is the default one.
     *
     * @param passwordPolicy the password policy
     * @return <code>true</code> if the given password policy is the default one,
     *         <code>false</code> if not.
     */
    public static boolean isDefaultPasswordPolicy( PasswordPolicyBean passwordPolicy )
    {
        if ( passwordPolicy != null )
        {
            return PASSWORD_POLICY_ID_DEFAULT.equalsIgnoreCase( passwordPolicy.getPwdId() );
        }

        return false;
    }
}