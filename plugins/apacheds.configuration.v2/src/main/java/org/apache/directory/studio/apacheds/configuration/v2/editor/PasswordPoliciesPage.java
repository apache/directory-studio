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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


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
    protected void refreshUI()
    {
        // Nothing to do.
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
            passwordPolicy.setPwdMaxAge( 0 );
            passwordPolicy.setPwdFailureCountInterval( 30 );
            passwordPolicy.setPwdAttribute( "userPassword" );
            passwordPolicy.setPwdMaxFailure( 5 );
            passwordPolicy.setPwdLockout( true );
            passwordPolicy.setPwdMustChange( false );
            passwordPolicy.setPwdLockoutDuration( 0 );
            passwordPolicy.setPwdMinLength( 5 );
            passwordPolicy.setPwdInHistory( 5 );
            passwordPolicy.setPwdExpireWarning( 600 );
            passwordPolicy.setPwdMinAge( 0 );
            passwordPolicy.setPwdAllowUserChange( true );
            passwordPolicy.setPwdGraceAuthNLimit( 5 );
            passwordPolicy.setPwdCheckQuality( 1 );
            passwordPolicy.setPwdMaxLength( 0 );
            passwordPolicy.setPwdGraceExpire( 0 );
            passwordPolicy.setPwdMinDelay( 0 );
            passwordPolicy.setPwdMaxDelay( 0 );
            passwordPolicy.setPwdMaxIdle( 0 );
            passwordPolicy
                .setPwdValidator( "org.apache.directory.server.core.api.authn.ppolicy.DefaultPasswordValidator" );

            // Adding the password policy to the authentication interceptor
            authenticationInterceptor.addPasswordPolicies( passwordPolicy );
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