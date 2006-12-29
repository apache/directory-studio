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

package org.apache.directory.ldapstudio.browser.ui.valueeditors.internal;


import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.dialogs.PasswordDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogBinaryValueEditor;
import org.eclipse.swt.widgets.Shell;


public class PasswordValueEditor extends AbstractDialogBinaryValueEditor
{

    public PasswordValueEditor()
    {
        super();
    }


    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof PasswordValueEditorRawValueWrapper )
        {
            PasswordValueEditorRawValueWrapper wrapper = ( PasswordValueEditorRawValueWrapper ) value;
            if ( wrapper.password != null && wrapper.password instanceof byte[] )
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
                return "NULL";
            }

            String password = value.getStringValue();;
            if ( password == null )
            {
                return "NULL";
            }
            else
            {
                String text;
                if ( "".equals( password ) )
                {
                    text = "Empty password";
                }
                else if ( password.indexOf( '{' ) == 0 && password.indexOf( '}' ) > 0 )
                {
                    String encryptionMethod = password.substring( password.indexOf( '{' ) + 1, password.indexOf( '}' ) );
                    text = encryptionMethod + " encrypted password";
                }
                else
                {
                    text = "Plain text password";
                }
                return text;
            }
        }
    }
 
    
    protected Object getEmptyRawValue( IAttribute attribute )
    {
        return new PasswordValueEditorRawValueWrapper( new byte[0], attribute.getEntry() );
    }


    public Object getRawValue( AttributeHierarchy ah )
    {
        Object wrapper = super.getRawValue( ah );
        return wrapper;
    }


    public Object getRawValue( IValue value )
    {
        Object password = super.getRawValue( value );
        return new PasswordValueEditorRawValueWrapper( password, value.getAttribute().getEntry() );
    }


    public Object getRawValue( IConnection connection, Object value )
    {
        Object password = super.getRawValue( connection, value );
        return new PasswordValueEditorRawValueWrapper( password, null );
    }

    class PasswordValueEditorRawValueWrapper
    {
        Object password;
        IEntry entry;


        public PasswordValueEditorRawValueWrapper( Object password, IEntry entry )
        {
            this.password = password;
            this.entry = entry;
        }
    }

}
