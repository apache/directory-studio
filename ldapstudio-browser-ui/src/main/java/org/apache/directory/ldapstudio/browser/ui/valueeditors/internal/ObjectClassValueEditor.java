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
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.dialogs.ObjectClassDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


public class ObjectClassValueEditor extends AbstractDialogStringValueEditor 
{

    public ObjectClassValueEditor()
    {
        super();
    }


    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof ObjectClassValueEditorRawValueWrapper )
        {
            ObjectClassValueEditorRawValueWrapper wrapper = ( ObjectClassValueEditorRawValueWrapper ) value;
            ObjectClassDialog dialog = new ObjectClassDialog( shell, wrapper.schema,
                wrapper.value );
            if ( dialog.open() == TextDialog.OK && !"".equals( dialog.getObjectClass() ) )
            {
                setValue( dialog.getObjectClass() );
                return true;
            }
        }
        return false;
    }


    public String getDisplayValue( IValue value )
    {
        if(getRawValue( value ) == null)
        {
            return "NULL";
        }
        
        String displayValue = value.getStringValue();
        
        if(!showRawValues())
        {
            Schema schema = value.getAttribute().getEntry().getConnection().getSchema();
            ObjectClassDescription ocd = schema.getObjectClassDescription( displayValue );
            if ( ocd.isStructural() )
            {
                displayValue = displayValue + " (structural)";
            }
            else if ( ocd.isAbstract() )
            {
                displayValue = displayValue + " (abstract)";
            }
            else if ( ocd.isAuxiliary() )
            {
                displayValue = displayValue + " (auxiliary)";
            }
            else if ( ocd.isObsolete() )
            {
                displayValue = displayValue + " (obsolete)";
            }
        }
        
        return displayValue;
    }


    public Object getRawValue( AttributeHierarchy ah )
    {
        return null;
    }


    public Object getRawValue( IValue value )
    {
        if ( value == null || !value.isString() || !value.getAttribute().isObjectClassAttribute() )
        {
            return null;
        }
        else
        {
            return getRawValue( value.getAttribute().getEntry().getConnection(), value.getStringValue() );
        }
    }


    public Object getRawValue( IConnection connection, Object value )
    {
        Schema schema = null;
        if ( connection != null )
        {
            schema = connection.getSchema();
        }
        if ( schema == null || value == null || !( value instanceof String ) )
        {
            return null;
        }

        String ocValue = ( String ) value;
        ObjectClassValueEditorRawValueWrapper wrapper = new ObjectClassValueEditorRawValueWrapper( schema, ocValue );
        return wrapper;
    }


    class ObjectClassValueEditorRawValueWrapper
    {
        Schema schema;

        String value;


        public ObjectClassValueEditorRawValueWrapper( Schema schema, String value )
        {
            super();
            this.schema = schema;
            this.value = value;
        }
    }

}
