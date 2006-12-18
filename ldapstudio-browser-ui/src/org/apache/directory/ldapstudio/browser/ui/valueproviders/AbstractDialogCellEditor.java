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


import org.apache.directory.ldapstudio.browser.core.model.IValue;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public abstract class AbstractDialogCellEditor extends CellEditor
{

    protected Object value;

    protected Composite parent;


    public AbstractDialogCellEditor( Composite parent )
    {
        super( parent );
        this.parent = parent;
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
        if ( value != null && value instanceof IValue.EmptyValue )
        {
            IValue.EmptyValue emptyValue = ( IValue.EmptyValue ) value;
            if ( emptyValue.isBinary() )
                value = emptyValue.getBinaryValue();
            else
                value = emptyValue.getStringValue();
        }
        this.value = value;
    }


    public void activate()
    {
        Object newValue = this.openDialogBox( parent );
        doSetValue( newValue );
        if ( this.value == null )
        {
            fireCancelEditor();
        }
        else
        {
            fireApplyEditorValue();
            deactivate();
        }
    }


    protected abstract Object openDialogBox( Control control );

}
