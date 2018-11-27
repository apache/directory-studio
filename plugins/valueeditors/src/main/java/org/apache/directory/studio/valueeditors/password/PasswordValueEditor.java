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

package org.apache.directory.studio.valueeditors.password;


import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogBinaryValueEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for attribute userPassword.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordValueEditor extends AbstractDialogBinaryValueEditor
{
    /**
     * {@inheritDoc}
     * 
     * This implementation opens the PasswordDialog.
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        
        if ( value instanceof PasswordValueEditorRawValueWrapper )
        {
            PasswordValueEditorRawValueWrapper wrapper = ( PasswordValueEditorRawValueWrapper ) value;
            
            if ( wrapper.password instanceof byte[] )
            {
                byte[] pw = ( byte[] ) wrapper.password;
                PasswordDialog dialog = new PasswordDialog( shell, pw, wrapper.entry );
                
                if ( dialog.open() == TextDialog.OK )
                {
                    setValue( dialog.getNewPassword() );
                    
                    return true;
                }
            }
        }
        
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns information about the 
     * used hash algorithm. The value stored in directory
     * is only display when the showRawValues option is 
     * active.
     */
    public String getDisplayValue( IValue value )
    {
        if ( showRawValues() )
        {
            return getPrintableString( value );
        }
        else
        {
            if ( value == null )
            {
                return NULL; //$NON-NLS-1$
            }

            String password = value.getStringValue();
            
            if ( password == null )
            {
                return NULL; //$NON-NLS-1$
            }
            else
            {
                String text;
                
                if ( EMPTY.equals( password ) ) //$NON-NLS-1$
                {
                    text = Messages.getString( "PasswordValueEditor.EmptyPassword" ); //$NON-NLS-1$
                }
                else if ( ( password.indexOf( '{' ) == 0  )&& ( password.indexOf( '}' ) > 0 ) )
                {
                    text = NLS.bind(
                        Messages.getString( "PasswordValueEditor.HashedPassword" ), getHashMethodName( password ) ); //$NON-NLS-1$
                }
                else
                {
                    text = Messages.getString( "PasswordValueEditor.PlainTextPassword" ); //$NON-NLS-1$
                }
                return text;
            }
        }
    }


    /**
     * Gets the name of the hash method.
     *
     * @param s the hash method string
     * @return the name of the associated hash method or the given string
     */
    private String getHashMethodName( String s )
    {
        LdapSecurityConstants hashMethod = PasswordUtil.findAlgorithm( Strings.getBytesUtf8( s ) );

        if ( hashMethod != null )
        {
            return hashMethod.getName();
        }

        return s;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns a PasswordValueEditorRawValueWrapper with empty 
     * password.
     */
    protected Object getEmptyRawValue( IAttribute attribute )
    {
        return new PasswordValueEditorRawValueWrapper( new byte[0], attribute.getEntry() );
    }


    /**
     * {@inheritDoc}
     * 
     * Returns a PasswordValueEditorRawValueWrapper.
     */
    public Object getRawValue( IValue value )
    {
        Object password = super.getRawValue( value );
        
        return new PasswordValueEditorRawValueWrapper( password, value.getAttribute().getEntry() );
    }
    

    /**
     * The PasswordValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened PasswordDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class PasswordValueEditorRawValueWrapper
    {
        /** The password, used as initial value in PasswordDialog */
        private Object password;

        /** The entry, used for the bind operation in PasswordDialog */
        private IEntry entry;


        /**
         * Creates a new instance of PasswordValueEditorRawValueWrapper.
         *
         * @param password the password
         * @param entry the entry
         */
        private PasswordValueEditorRawValueWrapper( Object password, IEntry entry )
        {
            this.password = password;
            this.entry = entry;
        }
    }
}
