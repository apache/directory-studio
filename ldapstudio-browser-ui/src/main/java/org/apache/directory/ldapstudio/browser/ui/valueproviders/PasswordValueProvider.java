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

package org.apache.directory.ldapstudio.browser.ui.valueproviders;


import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierachie;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.PasswordDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class PasswordValueProvider extends AbstractDialogCellEditor implements ValueProvider, ModelModifier
{

    private IEntry entry;

    private HexValueProvider delegate;


    public PasswordValueProvider( Composite parent )
    {
        super( parent );
        this.delegate = new HexValueProvider( parent );
    }


    public CellEditor getCellEditor()
    {
        return this;
    }


    public String getCellEditorName()
    {
        return "Password Editor";
    }


    public ImageDescriptor getCellEditorImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_PASSWORDEDITOR );
    }


    protected Object openDialogBox( Control cellEditorWindow )
    {
        Object value = getValue();
        if ( value != null && value instanceof byte[] )
        {
            byte[] pw = ( byte[] ) value;
            PasswordDialog dialog = new PasswordDialog( cellEditorWindow.getShell(), pw, entry );
            if ( dialog.open() == TextDialog.OK )
            {
                return dialog.getNewPassword();
            }
        }
        return null;
    }


    public String getDisplayValue( AttributeHierachie ah )
    {
        if ( ah == null )
        {
            return "NULL";
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {
            return getDisplayValue( ah.getAttribute().getValues()[0] );
        }
        else
        {
            return "not displayable";
        }
    }


    public String getDisplayValue( IValue value )
    {

        if ( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES ) )
        {
            return delegate.getPlainTextValue( value );
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


    public void create( IEntry entry, String attributeDescription, Object newRawValue )
        throws ModelModificationException
    {
        delegate.create( entry, attributeDescription, newRawValue );
    }


    public void delete( AttributeHierachie ah ) throws ModelModificationException
    {
        delegate.delete( ah );
    }


    public void delete( IValue oldValue ) throws ModelModificationException
    {
        delegate.delete( oldValue );
    }


    public Object getEmptyRawValue( IEntry entry, String attributeDescription )
    {
        this.entry = entry;
        return delegate.getEmptyRawValue( entry, attributeDescription );
    }


    public Object getRawValue( AttributeHierachie ah )
    {
        if ( ah != null )
        {
            this.entry = ah.getAttribute().getEntry();
        }
        return delegate.getRawValue( ah );
    }


    public Object getRawValue( IValue value )
    {
        this.entry = value.getAttribute().getEntry();
        return delegate.getRawValue( value );
    }


    public Object getRawValue( IConnection connection, Schema schema, Object value )
    {
        this.entry = null;
        return delegate.getRawValue( connection, schema, value );
    }


    public void modify( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
        delegate.modify( oldValue, newRawValue );
    }

}
