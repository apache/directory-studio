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

package org.apache.directory.studio.valueeditors.objectclass;


import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for attribute objectClass.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassValueEditor extends AbstractDialogStringValueEditor
{

    /**
     * {@inheritDoc}
     * 
     * This implementation opens the ObjectClassDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value instanceof ObjectClassValueEditorRawValueWrapper )
        {
            ObjectClassValueEditorRawValueWrapper wrapper = ( ObjectClassValueEditorRawValueWrapper ) value;
            ObjectClassDialog dialog = new ObjectClassDialog( shell, wrapper.schema, wrapper.objectClass );
            if ( dialog.open() == TextDialog.OK && !"".equals( dialog.getObjectClass() ) ) //$NON-NLS-1$
            {
                setValue( dialog.getObjectClass() );
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation appends the kind of object class,
     * on of structural, abstract, auxiliary or obsolete. 
     */
    public String getDisplayValue( IValue value )
    {
        if ( getRawValue( value ) == null )
        {
            return NULL;
        }

        String displayValue = value.getStringValue();

        if ( !showRawValues() && !"".equals( displayValue ) ) //$NON-NLS-1$
        {
            Schema schema = value.getAttribute().getEntry().getBrowserConnection().getSchema();
            ObjectClass ocd = schema.getObjectClassDescription( displayValue );
            switch ( ocd.getType() )
            {
                case STRUCTURAL:
                    displayValue = displayValue + Messages.getString( "ObjectClassValueEditor.Structural" ); //$NON-NLS-1$
                    break;
                case ABSTRACT:
                    displayValue = displayValue + Messages.getString( "ObjectClassValueEditor.Abstract" ); //$NON-NLS-1$
                    break;
                case AUXILIARY:
                    displayValue = displayValue + Messages.getString( "ObjectClassValueEditor.Auxiliary" ); //$NON-NLS-1$
                    break;
            }
            if ( ocd.isObsolete() )
            {
                displayValue = displayValue + Messages.getString( "ObjectClassValueEditor.Obsolete" ); //$NON-NLS-1$
            }
        }

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
     * Returns a ObjectClassValueEditorRawValueWrapper.
     */
    public Object getRawValue( IValue value )
    {
        if ( value == null || !value.isString() || !value.getAttribute().isObjectClassAttribute() )
        {
            return null;
        }
        else
        {
            return getRawValue( value.getAttribute().getEntry().getBrowserConnection(), value.getStringValue() );
        }
    }


    private Object getRawValue( IBrowserConnection connection, Object value )
    {
        Schema schema = null;
        
        if ( connection != null )
        {
            schema = connection.getSchema();
        }
        
        if ( !( value instanceof String ) )
        {
            return null;
        }

        String ocValue = ( String ) value;
        ObjectClassValueEditorRawValueWrapper wrapper = new ObjectClassValueEditorRawValueWrapper( schema, ocValue );
        
        return wrapper;
    }

    /**
     * The ObjectClassValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened ObjectClassDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class ObjectClassValueEditorRawValueWrapper
    {
        /** 
         * The schema, used in ObjectClassDialog to build the list
         * with possible object classes.
         */
        private Schema schema;

        /** The object class, used as initial value in ObjectClassDialog. */
        private String objectClass;


        /**
         * Creates a new instance of ObjectClassValueEditorRawValueWrapper.
         *
         * @param schema the schema
         * @param objectClass the object class
         */
        private ObjectClassValueEditorRawValueWrapper( Schema schema, String objectClass )
        {
            super();
            this.schema = schema;
            this.objectClass = objectClass;
        }
    }

}
