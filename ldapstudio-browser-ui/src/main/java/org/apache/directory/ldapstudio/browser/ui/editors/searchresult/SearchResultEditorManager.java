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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * The SearchResultEditorManager is used to set and get the the input
 * of the single search result editor instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEditorManager
{

    /** The dummy input, to find the single entry editor instance */
    private static SearchResultEditorInput DUMMY_INPUT = new SearchResultEditorInput( null );


    /**
     * Sets the input to the search result editor.
     *
     * @param entry the search input, may be null to clear the editor
     */
    public static void setInput( ISearch search )
    {
        SearchResultEditorInput input = new SearchResultEditorInput( search );
        setInput( input );
    }


    /**
     * Sets the input to the search result edtior. 
     *
     * @param input the input
     */
    private static void setInput( SearchResultEditorInput input )
    {
        SearchResultEditor editor = ( SearchResultEditor ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().findEditor( DUMMY_INPUT );
        if ( editor == null && input.getSearch() != null )
        {
            // open new search result editor
            try
            {
                editor = ( SearchResultEditor ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor( input, SearchResultEditor.getId(), false );
                editor.setInput( input );
            }
            catch ( PartInitException e )
            {
                e.printStackTrace();
            }
        }
        else if ( editor != null )
        {
            // set the input to already opened search result editor
            editor.setInput( input );

            // bring search result editor to top only if a search is displayed in it. 
            if ( input.getSearch() != null )
            {
                if ( !PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPartVisible( editor ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( editor );
                }
            }
        }
    }


    /**
     * Get the input of the search result editor. 
     * May be null if the editor is not opended 
     * or it the editor has an invalid input.
     *
     * @return the editor input or null
     */
    public static ISearch getInput()
    {
        SearchResultEditor editor = ( SearchResultEditor ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().findEditor( DUMMY_INPUT );

        if ( editor != null )
        {
            IEditorInput input = editor.getEditorInput();
            if ( input != null && input instanceof SearchResultEditorInput )
            {
                SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
                return srei.getSearch();
            }

        }

        return null;
    }

}
