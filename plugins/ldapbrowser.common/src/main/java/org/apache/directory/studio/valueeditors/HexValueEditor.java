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

package org.apache.directory.studio.valueeditors;


import org.apache.directory.studio.ldapbrowser.common.dialogs.HexDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * The default editor for binary values. Uses the HexDialog.
 * 
 * The HexDialog is currently only able to save and load binary data
 * to and from file. It is not possible to edit the data in the dialog
 * directly.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HexValueEditor extends AbstractDialogBinaryValueEditor
{

    /**
     * {@inheritDoc}
     * 
     * This implementation opens the HexDialog.
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        
        if ( value instanceof byte[] )
        {
            byte[] initialData = ( byte[] ) value;
            HexDialog dialog = new HexDialog( shell, initialData );
            
            if ( ( dialog.open() == HexDialog.OK ) && dialog.getData() != null )
            {
                setValue( dialog.getData() );
                
                return true;
            }
        }
        
        return false;
    }

}
