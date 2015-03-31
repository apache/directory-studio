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
package org.apache.directory.studio.combinededitor.actions;


import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.action.Action;


/**
 * This action fetches the operational attributes of the entry in the given editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FetchOperationalAttributesAction extends Action
{
    /** The associated editor */
    private IEntryEditor editor;


    /**
     * Creates a new instance of FetchOperationalAttributesAction.
     *
     * @param editor
     *      The associated editor 
     */
    public FetchOperationalAttributesAction( IEntryEditor editor )
    {
        this.editor = editor;
    }


    @Override
    public int getStyle()
    {
        return Action.AS_CHECK_BOX;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return org.apache.directory.studio.ldapbrowser.common.actions.Messages
            .getString( "FetchOperationalAttributesAction.FetchOperationalAttributes" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( editor != null )
        {
            IEntry entry = editor.getEntryEditorInput().getResolvedEntry();
            if ( entry != null )
            {
                entry = entry.getBrowserConnection().getEntryFromCache( entry.getDn() );

                return !entry.getBrowserConnection().isFetchOperationalAttributes();
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( editor != null )
        {
            IEntry entry = editor.getEntryEditorInput().getResolvedEntry();
            entry = entry.getBrowserConnection().getEntryFromCache( entry.getDn() );

            boolean init = !entry.isInitOperationalAttributes();
            entry.setInitOperationalAttributes( init );
            new StudioBrowserJob( new InitializeAttributesRunnable( entry ) ).execute();
        }
    }
}
