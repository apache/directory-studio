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

package org.apache.directory.studio.valueeditors.address;


import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for syntax 1.3.6.1.4.1.1466.115.121.1.41 
 * (Postal Address). In the displayed value the $ separators are replaced
 * by commas. In the opened AddressDialog the $ separators are replaced by 
 * line breaks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddressValueEditor extends AbstractDialogStringValueEditor
{
    /**
     * {@inheritDoc}
     * 
     * This implementation opens the AddressDialog.
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        
        if ( value instanceof String )
        {
            AddressDialog dialog = new AddressDialog( shell, ( String ) value );
            
            if ( ( dialog.open() == AddressDialog.OK ) && !EMPTY.equals( dialog.getAddress() ) ) //$NON-NLS-1$
            {
                setValue( dialog.getAddress() );
                
                return true;
            }
        }
        
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation replaces the $ separators by commas.
     */
    public String getDisplayValue( IValue value )
    {
        String displayValue = super.getDisplayValue( value );

        if ( !showRawValues() )
        {
            displayValue = displayValue.replaceAll( "\\$", ", " ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return displayValue;
    }
}
