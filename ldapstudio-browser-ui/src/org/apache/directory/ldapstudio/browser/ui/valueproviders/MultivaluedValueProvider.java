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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierachie;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.MultivaluedDialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class MultivaluedValueProvider extends CellEditor implements ValueProvider, ModelModifier
{

    protected Object value;

    protected Composite parent;

    protected ValueProviderManager valueProviderManager;


    public MultivaluedValueProvider( Composite parent, ValueProviderManager valueProviderManager )
    {
        super( parent );
        this.parent = parent;
        this.valueProviderManager = valueProviderManager;
    }


    protected Control createControl( Composite parent )
    {
        return null;
    }


    protected Object doGetValue()
    {
        return this.value;
    }


    protected void doSetFocus()
    {
    }


    protected void doSetValue( Object value )
    {
        this.value = value;
    }


    public void activate()
    {
        if ( this.getValue() != null && this.getValue() instanceof AttributeHierachie )
        {
            AttributeHierachie ah = ( AttributeHierachie ) this.getValue();
            if ( ah != null )
            {
                MultivaluedDialog dialog = new MultivaluedDialog( this.parent.getShell(), ah );
                dialog.open();
            }
        }

        fireCancelEditor();
    }


    public CellEditor getCellEditor()
    {
        return this;
    }


    public String getCellEditorName()
    {
        return "Multivalued Editor";
    }


    public ImageDescriptor getCellEditorImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_MULTIVALUEDEDITOR );
    }


    public String getDisplayValue( AttributeHierachie ah )
    {

        List valueList = new ArrayList();
        for ( Iterator it = ah.iterator(); it.hasNext(); )
        {
            IAttribute attribute = ( IAttribute ) it.next();
            valueList.addAll( Arrays.asList( attribute.getValues() ) );
        }

        StringBuffer sb = new StringBuffer();
        if ( valueList.size() > 1 )
            sb.append( valueList.size() + " values: " );
        for ( Iterator it = valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            ValueProvider vp = this.valueProviderManager.getCurrentValueProvider( value );
            sb.append( vp.getDisplayValue( value ) );
            if ( it.hasNext() )
                sb.append( ", " );
        }
        return sb.toString();
    }


    public String getDisplayValue( IValue value )
    {
        return "";
    }


    public Object getRawValue( AttributeHierachie ah )
    {
        return ah;
    }


    public Object getRawValue( IValue value )
    {
        return null;
    }


    public Object getRawValue( IConnection connection, Schema schema, Object value )
    {
        return null;
    }


    public void modify( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
    }


    public void create( IEntry entry, String attributeName, Object newRawValue ) throws ModelModificationException
    {
    }


    public void delete( AttributeHierachie ah ) throws ModelModificationException
    {
    }


    public void delete( IValue oldValue ) throws ModelModificationException
    {
    }

}
