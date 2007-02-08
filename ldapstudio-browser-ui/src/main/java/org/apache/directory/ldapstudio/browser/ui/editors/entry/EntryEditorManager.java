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


import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * The EntryEditorManager is used to set and get the the input
 * of the single entry editor instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorManager
{

    /** The dummy input, to find the single entry editor instance */
    private static EntryEditorInput DUMMY_INPUT = new EntryEditorInput( ( IEntry ) null );


    /**
     * Sets the entry input to the entry editor.
     *
     * @param entry the entry input, may be null to clear the editor
     */
    public static void setInput( IEntry entry )
    {
        EntryEditorInput input = new EntryEditorInput( entry );
        setInput( input );
    }


    /**
     * Sets the search result input to the entry editor.
     *
     * @param searchResult the search result input, may be null to clear the editor
     */
    public static void setInput( ISearchResult searchResult )
    {
        EntryEditorInput input = new EntryEditorInput( searchResult );
        setInput( input );
    }


    /**
     * Sets the bookmark input to the entry editor.
     *
     * @param bookmark the bookmark input, may be null to clear the editor
     */
    public static void setInput( IBookmark bookmark )
    {
        EntryEditorInput input = new EntryEditorInput( bookmark );
        setInput( input );
    }


    /**
     * Sets the input to the entry edtior. 
     *
     * @param input the input
     */
    private static void setInput( EntryEditorInput input )
    {
        EntryEditor editor = ( EntryEditor ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findEditor( DUMMY_INPUT );
        if ( editor == null && input.getResolvedEntry() != null )
        {
            // open new entry editor
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
        else
        {
            // set the input to already opened entry editor
            editor.setInput( input );

            // bring entry editor to top only if an entry is displayed in it. 
            if ( input.getResolvedEntry() != null )
            {
                if ( !PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPartVisible( editor ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( editor );
                }
            }
        }
    }


    /**
     * Get the input of the entry editor. 
     * May be null if the editor is not opended 
     * or it the editor has an invalid input.
     *
     * @return the editor input or null
     */
    public static Object getInput()
    {
        EntryEditor editor = ( EntryEditor ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findEditor( DUMMY_INPUT );

        if ( editor != null )
        {
            IEditorInput input = editor.getEditorInput();
            if ( input != null && input instanceof EntryEditorInput )
            {
                EntryEditorInput eei = ( EntryEditorInput ) input;
                return eei.getInput();
            }

        }

        return null;
    }

}
