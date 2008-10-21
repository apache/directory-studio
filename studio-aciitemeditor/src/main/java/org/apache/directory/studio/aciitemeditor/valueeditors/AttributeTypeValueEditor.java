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

package org.apache.directory.studio.aciitemeditor.valueeditors;


import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for attribute types.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeValueEditor extends AbstractDialogStringValueEditor
{

    private static final String EMPTY = ""; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     * 
     * This implementation opens the AttributeTypeDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof AttributeTypeValueEditorRawValueWrapper )
        {
            AttributeTypeValueEditorRawValueWrapper wrapper = ( AttributeTypeValueEditorRawValueWrapper ) value;
            AttributeTypeDialog dialog = new AttributeTypeDialog( shell, wrapper.schema, wrapper.attributeType );
            if ( dialog.open() == TextDialog.OK && !EMPTY.equals( dialog.getAttributeType() ) )
            {
                setValue( dialog.getAttributeType() );
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns an AttributeTypeAndValueValueEditorRawValueWrapper.
     */
    public Object getRawValue( IValue value )
    {
        return value != null ? getRawValue( value.getAttribute().getEntry().getBrowserConnection(), value.getStringValue() )
            : null;
    }


    private Object getRawValue( IBrowserConnection connection, Object value )
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

        String atValue = ( String ) value;
        AttributeTypeValueEditorRawValueWrapper wrapper = new AttributeTypeValueEditorRawValueWrapper( schema, atValue );
        return wrapper;
    }

    /**
     * The AttributeTypeValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened AttributeTypeDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class AttributeTypeValueEditorRawValueWrapper
    {
        /** 
         * The schema, used in AttributeTypeDialog to build the list
         * with possible attribute types.
         */
        private Schema schema;

        /** The attribute type, used as initial value in AttributeTypeDialog. */
        private String attributeType;


        /**
         * Creates a new instance of AttributeTypeValueEditorRawValueWrapper.
         *
         * @param schema the schema
         * @param attributeType the attribute type
         */
        private AttributeTypeValueEditorRawValueWrapper( Schema schema, String attributeType )
        {
            super();
            this.schema = schema;
            this.attributeType = attributeType;
        }
    }

}
