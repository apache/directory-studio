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
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.ui.dialogs.DnDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


public class DnValueEditor extends AbstractDialogStringValueEditor
{

    public DnValueEditor()
    {
        super();
    }


    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof DnValueEditorRawValueWrapper )
        {
            DnValueEditorRawValueWrapper wrapper = ( DnValueEditorRawValueWrapper ) value;
            DnDialog dialog = new DnDialog( shell, wrapper.connection, wrapper.dn );
            if ( dialog.open() == TextDialog.OK && dialog.getDn() != null )
            {
                setValue( dialog.getDn().toString() );
                return true;
            }
        }
        return false;
    }


    public Object getRawValue( AttributeHierarchy ah )
    {
        if ( ah == null )
        {
            return null;
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 0 )
        {
            IConnection connection = ah.getAttribute().getEntry().getConnection();
            DN dn = null;
            return new DnValueEditorRawValueWrapper( connection, dn );
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
            return new DnValueEditorRawValueWrapper( connection, dn );
        }
        else
        {
            return null;
        }
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

        return new DnValueEditorRawValueWrapper( connection, dn );
    }


    public Object getRawValue( IConnection connection, Object value )
    {
        Object o = super.getRawValue( connection, value );
        if ( o != null && o instanceof String )
        {
            try
            {
                DN dn = new DN( ( String ) o );
                return new DnValueEditorRawValueWrapper( connection, dn );
            }
            catch ( NameException e )
            {
            }
        }

        return null;
    }


    public String getDisplayValue( IValue value )
    {
        if ( value == null )
        {
            return "NULL";
        }

        String displayValue = value.getStringValue();
        return displayValue;
    }

    class DnValueEditorRawValueWrapper
    {
        IConnection connection;

        DN dn;


        public DnValueEditorRawValueWrapper( IConnection connection, DN dn )
        {
            this.connection = connection;
            this.dn = dn;
        }
    }

}
