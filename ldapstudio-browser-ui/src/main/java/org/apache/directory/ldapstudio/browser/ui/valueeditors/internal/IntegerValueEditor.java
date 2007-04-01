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


import org.apache.directory.ldapstudio.browser.ui.dialogs.IntegerDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for integer values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class IntegerValueEditor extends AbstractDialogStringValueEditor
{

    /**
     * {@inheritDoc}
     * 
     * This implementation opens the IntegerDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof String )
        {
            String stringValue = ( String ) value;
            int intValue;
            try
            {
                intValue = Integer.valueOf( stringValue );
            }
            catch ( NumberFormatException e )
            {
                intValue = 0;
            }
            IntegerDialog dialog = new IntegerDialog( shell, intValue );
            if ( dialog.open() == IntegerDialog.OK && dialog.getInteger() != -1 )
            {
                setValue( Integer.toString( dialog.getInteger() ) );
                return true;
            }
        }
        return false;
    }

}
