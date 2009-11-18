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

package org.apache.directory.studio.entryeditors;


import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;


public class EntryEditorUtils
{

    /**
     * Checks if the attributes of the given entry are initialized and 
     * initializes them in necessary.
     * 
     * @param entry the entry
     */
    public static void ensureAttributesInitialized( IEntry entry )
    {
        if ( !entry.isAttributesInitialized() )
        {
            InitializeAttributesRunnable runnable = new InitializeAttributesRunnable( entry );
            StudioBrowserJob job = new StudioBrowserJob( runnable );
            job.execute();
        }
    }


    /**
     * Gets the entry editor input from the editor input.
     * 
     * @param input the input
     * 
     * @return the entry editor input
     */
    public static EntryEditorInput getEntryEditorInput( IEditorInput input )
    {
        if ( input instanceof EntryEditorInput )
        {
            EntryEditorInput eei = ( EntryEditorInput ) input;
            return eei;
        }
        else
        {
            throw new IllegalArgumentException( "Expected an EntryEditorInput" ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the text used in the history navigation list.
     * 
     * @param input the input
     * 
     * @return the text
     */
    public static String getHistoryNavigationText( EntryEditorInput input )
    {
        if ( input != null )
        {
            if ( input.getEntryInput() != null )
            {
                String connectionName = input.getEntryInput().getBrowserConnection().getConnection() == null ? "" //$NON-NLS-1$
                    : " - " + input.getEntryInput().getBrowserConnection().getConnection().getName(); //$NON-NLS-1$
                if ( input.getEntryInput() instanceof IRootDSE )
                {
                    return Messages.getString( "EntryEditorNavigationLocation.RootDSE" ) + connectionName; //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    return NLS.bind( Messages.getString( "EntryEditorNavigationLocation.Entry" ), //$NON-NLS-1$
                        input.getEntryInput().getDn().getUpName() ) + connectionName;
                }
            }
            else if ( input.getSearchResultInput() != null )
            {
                String connectionName = input.getSearchResultInput().getEntry().getBrowserConnection().getConnection() == null ? "" //$NON-NLS-1$
                    : " - " + input.getSearchResultInput().getEntry().getBrowserConnection().getConnection().getName(); //$NON-NLS-1$
                if ( input.getSearchResultInput() instanceof IRootDSE )
                {
                    return Messages.getString( "EntryEditorNavigationLocation.RootDSE" ) + connectionName; //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    return NLS.bind( Messages.getString( "EntryEditorNavigationLocation.SearchResult" ), //$NON-NLS-1$
                        input.getSearchResultInput().getDn().getUpName() ) + connectionName; //$NON-NLS-1$
                }
            }
            else if ( input.getBookmarkInput() != null )
            {
                String connectionName = input.getBookmarkInput().getBrowserConnection().getConnection() == null ? "" //$NON-NLS-1$
                    : " - " + input.getBookmarkInput().getBrowserConnection().getConnection().getName(); //$NON-NLS-1$
                if ( input.getBookmarkInput() instanceof IRootDSE )
                {
                    return Messages.getString( "EntryEditorNavigationLocation.RootDSE" ) + connectionName; //$NON-NLS-1$
                }
                else
                {
                    return NLS.bind( Messages.getString( "EntryEditorNavigationLocation.Bookmark" ), //$NON-NLS-1$
                        input.getBookmarkInput().getDn().getUpName() ) + connectionName;
                }
            }
            else
            {
                return Messages.getString( "EntryEditorUtils.NoEntrySelected" ); //$NON-NLS-1$
            }
        }

        return null;
    }
}
