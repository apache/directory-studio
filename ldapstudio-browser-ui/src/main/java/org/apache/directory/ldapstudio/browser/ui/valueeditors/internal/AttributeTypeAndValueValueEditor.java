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
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.dialogs.AttributeTypeAndValueDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for attribute type and value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeAndValueValueEditor extends AbstractDialogStringValueEditor
{

    /**
     * {@inheritDoc}
     * 
     * This implementation opens the AttributeTypeDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof AttributeTypeAndValueValueEditorRawValueWrapper )
        {
            AttributeTypeAndValueValueEditorRawValueWrapper wrapper = ( AttributeTypeAndValueValueEditorRawValueWrapper ) value;
            AttributeTypeAndValueDialog dialog = new AttributeTypeAndValueDialog( shell, wrapper.schema, wrapper.attributeType, wrapper.value );
            if ( dialog.open() == TextDialog.OK && !"".equals( dialog.getAttributeType() ) && !"".equals( dialog.getValue() ) )
            {
                setValue( dialog.getAttributeType() + '=' + dialog.getValue() );
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns always the string value.
     * 
     * Reimplementation, because getRawValue() returns an 
     * AttributeTypeValueEditorRawValueWrapper.
     */
    public String getDisplayValue( IValue value )
    {
        if ( value == null )
        {
            return "NULL";
        }

        String displayValue = value.getStringValue();
        return displayValue;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns null.
     * Modification in search result editor not supported.
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns an AttributeTypeAndValueValueEditorRawValueWrapper.
     */
    public Object getRawValue( IValue value )
    {
        if ( value == null || !value.isString() )
        {
            return null;
        }
        else
        {
            return getRawValue( value.getAttribute().getEntry().getConnection(), value.getStringValue() );
        }
    }


    /**
     * {@inheritDoc}
     * 
     * Returns an AttributeTypeAndValueValueEditorRawValueWrapper.
     */
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

        String atavValue = ( String ) value;
        String[] atav = atavValue.split( "=", 2 );
        String at = atav.length > 0 ? atav[0] : "";
        String v = atav.length > 1 ? atav[1] : "";
        AttributeTypeAndValueValueEditorRawValueWrapper wrapper = new AttributeTypeAndValueValueEditorRawValueWrapper( schema, at, v );
        return wrapper;
    }

    /**
     * The AttributeTypeAndValueValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened AttributeTypeAndValueDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class AttributeTypeAndValueValueEditorRawValueWrapper
    {
        /** 
         * The schema, used in AttributeTypeDialog to build the list
         * with possible attribute types.
         */
        private Schema schema;

        /** The attribute type, used as initial attribute type. */
        private String attributeType;
        
        /** The value, used as initial value. */
        private String value;


        /**
         * Creates a new instance of AttributeTypeAndValueValueEditorRawValueWrapper.
         * 
         * @param schema the schema
         * @param attributeType the attribute type
         * @param value the value
         */
        private AttributeTypeAndValueValueEditorRawValueWrapper( Schema schema, String attributeType, String value )
        {
            super();
            this.schema = schema;
            this.attributeType = attributeType;
            this.value = value;
        }
    }

}
