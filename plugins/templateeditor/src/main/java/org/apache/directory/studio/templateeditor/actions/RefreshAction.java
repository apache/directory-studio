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
package org.apache.directory.studio.templateeditor.actions;


import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This action refreshes the entry in the given editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RefreshAction extends Action
{
    /** The associated editor */
    private IEntryEditor editor;


    /**
     * Creates a new instance of RefreshAction.
     *
     * @param editor
     *      the associated editor
     */
    public RefreshAction( IEntryEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_REFRESH );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return org.apache.directory.studio.ldapbrowser.common.actions.Messages
            .getString( "RefreshAction.RelaodAttributes" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( editor != null )
        {
            return ( editor.getEntryEditorInput().getResolvedEntry() != null );
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public String getActionDefinitionId()
    {
        return "org.eclipse.ui.file.refresh"; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( editor != null )
        {
            IEntry entry = editor.getEntryEditorInput().getResolvedEntry();
            new StudioConnectionJob( new InitializeAttributesRunnable( entry ) ).execute();
        }
    }
}
