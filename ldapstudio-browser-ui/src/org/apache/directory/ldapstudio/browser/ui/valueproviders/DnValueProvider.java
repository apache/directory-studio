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
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.DnDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class DnValueProvider extends AbstractDialogCellEditor implements ValueProvider, ModelModifier
{

    private TextValueProvider delegate;


    public DnValueProvider( Composite parent )
    {
        super( parent );
        this.delegate = new TextValueProvider( parent );
    }


    public CellEditor getCellEditor()
    {
        return this;
    }


    public String getCellEditorName()
    {
        return "DN Editor";
    }


    public ImageDescriptor getCellEditorImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_DNEDITOR );
    }


    protected Object openDialogBox( Control cellEditorWindow )
    {
        Object value = getValue();
        if ( value != null && value instanceof DnValueProviderRawValue )
        {

            DnValueProviderRawValue v = ( DnValueProviderRawValue ) value;

            DnDialog dialog = new DnDialog( cellEditorWindow.getShell(), v.connection, v.dn );
            if ( dialog.open() == TextDialog.OK && dialog.getDn() != null )
            {
                return dialog.getDn();
            }
        }
        return null;
    }


    public String getDisplayValue( AttributeHierachie ah )
    {
        return delegate.getDisplayValue( ah );
    }


    public String getDisplayValue( IValue value )
    {
        return delegate.getDisplayValue( value );
    }


    public void create( IEntry entry, String attributeDescription, Object newRawValue )
        throws ModelModificationException
    {
        if ( newRawValue != null && newRawValue instanceof DN )
        {
            newRawValue = ( ( DN ) newRawValue ).toString();
            delegate.create( entry, attributeDescription, newRawValue );
        }
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
        return delegate.getEmptyRawValue( entry, attributeDescription );
    }


    public Object getRawValue( AttributeHierachie ah )
    {

        if ( ah == null )
        {
            return null;
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 0 )
        {
            IConnection connection = ah.getAttribute().getEntry().getConnection();
            DN dn = null;
            return new DnValueProviderRawValue( connection, dn );
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {
            IConnection connection = ah.getAttribute().getEntry().getConnection();
            DN dn = null;
            try
            {
                dn = new DN( getDisplayValue( ah ) );
            }
            catch ( NameException e )
            {
            }
            return new DnValueProviderRawValue( connection, dn );
        }
        else
        {
            return null;
        }

        // IConnection connection = attribute.getEntry().getConnection();
        //
        // DN dn = null;
        // if (attribute.getValueSize() == 0) {
        // dn = null;
        // }
        // else {
        // try {
        // dn = new DN(getDisplayValue(attributes));
        // }
        // catch (NameException e) {
        // }
        // }
        //
        // return new DnValueProviderRawValue(connection, dn);
    }


    public Object getRawValue( IValue value )
    {

        IConnection connection = value.getAttribute().getEntry().getConnection();

        DN dn = null;
        if ( value.isEmpty() )
        {
            dn = null;
        }
        else
        {
            try
            {
                dn = new DN( getDisplayValue( value ) );
            }
            catch ( NameException e )
            {
            }
        }

        return new DnValueProviderRawValue( connection, dn );
    }


    public Object getRawValue( IConnection connection, Schema schema, Object value )
    {
        return null;
    }


    public void modify( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
        if ( newRawValue != null && newRawValue instanceof DN )
        {
            newRawValue = ( ( DN ) newRawValue ).toString();
            delegate.modify( oldValue, newRawValue );
        }
    }

    class DnValueProviderRawValue
    {
        IConnection connection;

        DN dn;


        public DnValueProviderRawValue( IConnection connection, DN dn )
        {
            this.connection = connection;
            this.dn = dn;
        }
    }

}
