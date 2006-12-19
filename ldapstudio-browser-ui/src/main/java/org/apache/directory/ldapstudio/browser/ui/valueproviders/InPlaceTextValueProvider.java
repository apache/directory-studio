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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;


public class InPlaceTextValueProvider extends TextCellEditor implements ValueProvider, ModelModifier
{

    private TextValueProvider delegate;


    public InPlaceTextValueProvider( Composite parent )
    {
        super( parent );
        this.delegate = new TextValueProvider( parent );
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


    public String getDisplayValue( AttributeHierachie ah )
    {
        return delegate.getDisplayValue( ah );
    }


    public String getDisplayValue( IValue value )
    {
        return delegate.getDisplayValue( value );
    }


    public Object getEmptyRawValue( IEntry entry, String attributeDescription )
    {
        return delegate.getEmptyRawValue( entry, attributeDescription );
    }


    public Object getRawValue( AttributeHierachie ah )
    {
        return delegate.getRawValue( ah );
    }


    public Object getRawValue( IValue value )
    {
        return delegate.getRawValue( value );
    }


    public Object getRawValue( IConnection connection, Schema schema, Object value )
    {
        return delegate.getRawValue( connection, schema, value );
    }


    public void modify( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
        delegate.modify( oldValue, newRawValue );
    }


    public CellEditor getCellEditor()
    {
        return this;
    }


    public String getCellEditorName()
    {
        return "In-Place Text Editor";
    }


    public ImageDescriptor getCellEditorImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_INPLACE_TEXTEDITOR );
    }


    protected Object doGetValue()
    {
        return "".equals( text.getText() ) ? null : text.getText();
    }


    protected void doSetValue( Object value )
    {
        if ( value != null && value instanceof IValue.EmptyValue )
        {
            value = ( ( IValue.EmptyValue ) value ).getStringValue();
        }
        super.doSetValue( value );
    }

}
