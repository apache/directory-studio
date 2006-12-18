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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.ldapstudio.browser.core.model.IEntry;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class EntryEditorManager
{

    private static EntryEditor editor;


    public static void setInput( IEntry entry )
    {

        IEditorInput input = new EntryEditorInput( entry );

        if ( editor == null && entry != null )
        {
            try
            {
                editor = ( EntryEditor ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor( input, EntryEditor.getId(), false );
            }
            catch ( PartInitException e )
            {
                e.printStackTrace();
            }
        }
        else if ( editor != null )
        {
            editor.setInput( input );
            if ( entry != null )
            {
                if ( !PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPartVisible( editor ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( editor );
                }
            }
        }
    }


    static void editorClosed()
    {
        editor = null;
    }

}
