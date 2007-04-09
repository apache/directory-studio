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

package org.apache.directory.ldapstudio.valueeditors.dn;


import org.apache.directory.ldapstudio.browser.common.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.valueeditors.AbstractDialogStringValueEditor;
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
            DnDialog dialog = new DnDialog( shell, wrapper.connection, wrapper.dn );
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
            IConnection connection = attributeHierarchy.getAttribute().getEntry().getConnection();
            DN dn = null;
            return new DnValueEditorRawValueWrapper( connection, dn );
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 1 )
        {
            IConnection connection = attributeHierarchy.getAttribute().getEntry().getConnection();
            try
            {
                DN dn = new DN( getDisplayValue( attributeHierarchy ) );
                return new DnValueEditorRawValueWrapper( connection, dn );
            }
            catch ( NameException e )
            {
                return new DnValueEditorRawValueWrapper( connection, null );
            }
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
     * 
     * If the value doesn't contain a valid DN a DnValueEditorRawValueWrapper
     * with a null DN is returned.
     */
    public Object getRawValue( IValue value )
    {
        Object o = super.getRawValue( value );
        if ( o != null && o instanceof String )
        {
            IConnection connection = value.getAttribute().getEntry().getConnection();
            try
            {
                DN dn = new DN( ( String ) o );
                return new DnValueEditorRawValueWrapper( connection, dn );
            }
            catch ( NameException e )
            {
                return new DnValueEditorRawValueWrapper( connection, null );
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns a DnValueEditorRawValueWrapper with the given 
     * connection and a DN build from the given value.
     * 
     * If the value doesn't contain a valid DN a DnValueEditorRawValueWrapper
     * with a null DN is returned.
     */
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
                return new DnValueEditorRawValueWrapper( connection, null );
            }
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
        private IConnection connection;

        /** The DN, used as initial value in DnDialog */
        private DN dn;


        /**
         * Creates a new instance of DnValueEditorRawValueWrapper.
         *
         * @param connection the connection
         * @param dn the DN
         */
        private DnValueEditorRawValueWrapper( IConnection connection, DN dn )
        {
            this.connection = connection;
            this.dn = dn;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return dn == null ? "" : dn.toString();
        }
        
    }

}
