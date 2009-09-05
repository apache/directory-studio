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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IShowEditorInput;


/**
 * An single-tab entry editor that uses LDIF format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SingleTabLdifEntryEditor extends MultiTabLdifEntryEditor implements IShowEditorInput
{

    private boolean inShowEditorInput = false;


    /**
     * Gets the ID of the SingleTabLdifEntryEditor.
     * 
     * @return the id of the SingleTabLdifEntryEditor
     */
    public static String getId()
    {
        return BrowserUIConstants.EDITOR_SINGLE_TAB_LDIF_ENTRY_EDITOR;
    }


    public void showEditorInput( IEditorInput input )
    {
        if ( inShowEditorInput )
        {
            /*
             *  This is to avoid recursion when using history navigation:
             *  - when selecting an entry this method is called
             *  - this method fires an input changed event
             *  - the LinkWithEditorAction gets the event and selects the entry in the browser tree
             *  - this selection fires an selection event
             *  - the selection event causes an openEditor() that calls this method again
             */
            return;
        }

        try
        {
            inShowEditorInput = true;

            if ( input instanceof EntryEditorInput )
            {
                /*
                 * Workaround to make link-with-editor working for the single-tab editor:
                 * The call of firePropertyChange is used to inform the link-with-editor action.
                 * However firePropertyChange also modifies the navigation history.
                 * Thus, a dummy input with the real entry but a null extension is set.
                 * This avoids to modification of the navigation history.
                 * Afterwards the real input is set.
                 */
                EntryEditorInput eei = ( EntryEditorInput ) input;
                IEntry entry = eei.getEntryInput();
                ISearchResult searchResult = eei.getSearchResultInput();
                IBookmark bookmark = eei.getBookmarkInput();
                EntryEditorInput dummyInput;
                if ( entry != null )
                {
                    dummyInput = new EntryEditorInput( entry, null );
                }
                else if ( searchResult != null )
                {
                    dummyInput = new EntryEditorInput( searchResult, null );
                }
                else
                {
                    dummyInput = new EntryEditorInput( bookmark, null );
                }
                doSetInput( dummyInput );
                firePropertyChange( IEditorPart.PROP_INPUT );

                // now set the real input and mark history location
                doSetInput( input );
                getSite().getPage().getNavigationHistory().markLocation( this );
            }
        }
        catch ( CoreException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            inShowEditorInput = false;
        }
    }

}
