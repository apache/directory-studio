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

package org.apache.directory.studio.ldapbrowser.ui.views.searchlogs;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * Action to refresh the view from logfile.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RefreshAction extends BrowserAction
{

    /** The search logs view. */
    private SearchLogsView view;


    /**
     * Creates a new instance of RefreshAction.
     *
     * @param view the search logs view
     */
    public RefreshAction( SearchLogsView view )
    {
        this.view = view;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // int topIndex = view.getMainWidget().getSourceViewer().getTopIndex();
        view.getUniversalListener().refreshInput();
        view.getUniversalListener().scrollToNewest();
        // view.getMainWidget().getSourceViewer().setTopIndex(topIndex);
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "RefreshAction.Refresh" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_REFRESH );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getInput() instanceof SearchLogsViewInput;
    }
}
