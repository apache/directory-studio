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


import org.apache.directory.studio.ldapbrowser.common.dialogs.FilterWidgetDialog;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for LDAP filters.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FilterValueEditor extends AbstractDialogStringValueEditor
{

    private static final String EMPTY = ""; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     * 
     * This implementation opens the FilterWidgetDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value instanceof FilterValueEditorRawValueWrapper )
        {
            FilterValueEditorRawValueWrapper wrapper = ( FilterValueEditorRawValueWrapper ) value;
            FilterWidgetDialog dialog = new FilterWidgetDialog( shell, Messages
                .getString( "FilterValueEditor.dialog.title" ), wrapper.filter, //$NON-NLS-1$
                wrapper.connection );
            if ( dialog.open() == TextDialog.OK && !EMPTY.equals( dialog.getFilter() ) )
            {
                setValue( dialog.getFilter() );
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
        if ( value != null )
        {
            return getRawValue( value.getAttribute().getEntry().getBrowserConnection(), 
                                value.getStringValue() );
        }
        else
        {
            return null;
        }
    }


    private Object getRawValue( IBrowserConnection connection, Object value )
    {
        if ( connection == null || !( value instanceof String ) )
        {
            return null;
        }

        String filterValue = ( String ) value;
        FilterValueEditorRawValueWrapper wrapper = new FilterValueEditorRawValueWrapper( connection, filterValue );
        return wrapper;
    }

    /**
     * The FilterValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened FilterDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class FilterValueEditorRawValueWrapper
    {
        /** 
         * The connection, used in FilterDialog to build the list
         * with possible attribute types.
         */
        private IBrowserConnection connection;

        /** The filter, used as initial value in FilterDialog. */
        private String filter;


        /**
         * Creates a new instance of FilterValueEditorRawValueWrapper.
         *
         * @param schema the schema
         * @param attributeType the attribute type
         */
        private FilterValueEditorRawValueWrapper( IBrowserConnection connection, String filter )
        {
            this.connection = connection;
            this.filter = filter;
        }
    }
}
