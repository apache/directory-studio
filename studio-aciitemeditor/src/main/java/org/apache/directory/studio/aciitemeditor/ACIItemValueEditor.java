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
package org.apache.directory.studio.aciitemeditor;


import org.apache.directory.studio.aciitemeditor.dialogs.ACIItemDialog;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * The IValueEditor implementation of this plugin.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemValueEditor extends AbstractDialogStringValueEditor
{

    /**
     * The Constructor.
     */
    public ACIItemValueEditor()
    {
        super();
    }


    /**
     * Opens the ACI item dialog.
     * 
     * @param shell the shell
     * 
     * @return true, if open dialog
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof ACIItemValueWithContext )
        {
            ACIItemValueWithContext context = (ACIItemValueWithContext)value;
            
            ACIItemDialog dialog = new ACIItemDialog( shell, context );
            if ( dialog.open() == ACIItemDialog.OK && !"".equals( dialog.getACIItemValue() ) ) //$NON-NLS-1$
            {
                setValue( dialog.getACIItemValue() );
                return true;
            }
        }
        return false;
    }

    
    /**
     * Returns a ACIItemValueContext with the connection
     * and entry of the attribute hierarchy and an empty value if there
     * are no values in attributeHierarchy.
     * 
     * Returns a ACIItemValueContext with the connection
     * and entry of the attribute hierarchy and a value if there is
     * one value in attributeHierarchy.
     * 
     * @param attributeHierarchy the attribute hierarchy
     * 
     * @return the raw value
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy == null )
        {
            return null;
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 0 )
        {
            IEntry entry = attributeHierarchy.getAttribute().getEntry();
            IConnection connection = entry.getConnection();
            return new ACIItemValueWithContext( connection, entry, "" ); //$NON-NLS-1$
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 1 )
        {
            IEntry entry = attributeHierarchy.getAttribute().getEntry();
            IConnection connection = entry.getConnection();
            String value = getDisplayValue( attributeHierarchy );
            return new ACIItemValueWithContext( connection, entry, value );
        }
        else
        {
            return null;
        }
    }


    /**
     * Returns a ACIItemValueContext with the connection,
     * entry and string value of the given value.
     * 
     * @param value the value
     * 
     * @return the raw value
     */
    public Object getRawValue( IValue value )
    {
        Object o = super.getRawValue( value );
        if ( o != null && o instanceof String )
        {
            IEntry entry = value.getAttribute().getEntry();
            IConnection connection = entry.getConnection();
            String v = (String) o;
            return new ACIItemValueWithContext( connection, entry, v );
        }

        return null;
    }

}
