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

package org.apache.directory.studio.valueeditors.dn;


import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for syntax 1.3.6.1.4.1.1466.115.121.1.12
 * (Distinguished Name).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DnValueEditor extends AbstractDialogStringValueEditor
{

    /**
     * {@inheritDoc}
     *
     * This implementation opens the DnDialog.
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof DnValueEditorRawValueWrapper )
        {
            DnValueEditorRawValueWrapper wrapper = ( DnValueEditorRawValueWrapper ) value;
            DN dn;
            try
            {
                dn = wrapper.dn != null ? new DN( wrapper.dn ) : null;
            }
            catch ( NameException e )
            {
                dn = null;
            }
            DnDialog dialog = new DnDialog( shell, wrapper.connection, dn );
            if ( dialog.open() == TextDialog.OK && dialog.getDn() != null )
            {
                setValue( dialog.getDn().toString() );
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     *
     * Returns a DnValueEditorRawValueWrapper with the connection of
     * the attribute hierarchy and a null DN if there are no values
     * in attributeHierarchy.
     *
     * Returns a DnValueEditorRawValueWrapper with the connection of
     * the attribute hierarchy and a DN if there is one value
     * in attributeHierarchy.
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy == null )
        {
            return null;
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 0 )
        {
            IBrowserConnection connection = attributeHierarchy.getAttribute().getEntry().getBrowserConnection();
            return new DnValueEditorRawValueWrapper( connection, null );
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 1 )
        {
            IBrowserConnection connection = attributeHierarchy.getAttribute().getEntry().getBrowserConnection();
            return new DnValueEditorRawValueWrapper( connection, getDisplayValue( attributeHierarchy ) );
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     *
     * Returns a DnValueEditorRawValueWrapper with the connection of
     * the value and a DN build from the given value.
     */
    public Object getRawValue( IValue value )
    {
        Object o = super.getRawValue( value );
        if ( o != null && o instanceof String )
        {
            IBrowserConnection connection = value.getAttribute().getEntry().getBrowserConnection();
            return new DnValueEditorRawValueWrapper( connection, ( String ) o );
        }

        return null;
    }


    /**
     * The DnValueEditorRawValueWrapper is used to pass contextual
     * information to the opened DnDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class DnValueEditorRawValueWrapper
    {
        /** The connection, used in DnDialog to browse for an entry */
        private IBrowserConnection connection;

        /** The DN, used as initial value in DnDialog */
        private String dn;


        /**
         * Creates a new instance of DnValueEditorRawValueWrapper.
         *
         * @param connection the connection
         * @param dn the DN
         */
        private DnValueEditorRawValueWrapper( IBrowserConnection connection, String dn )
        {
            this.connection = connection;
            this.dn = dn;
        }


        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return dn == null ? "" : dn;
        }

    }

}
