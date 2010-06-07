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
package org.apache.directory.studio.ldapbrowser.ui.views.modificationlogs;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * Action to clear the log files.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ClearAction extends BrowserAction
{

    /** The modification logs view. */
    private ModificationLogsView view;


    /**
     * Creates a new instance of ClearAction.
     * 
     * @param view
     *            the modification logs view
     */
    public ClearAction( ModificationLogsView view )
    {
        this.view = view;
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
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_CLEAR );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "ClearAction.Clear" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getInput() != null && ( getInput() instanceof ModificationLogsViewInput );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( MessageDialog.openConfirm( this.getShell(),
            Messages.getString( "ClearAction.Delete" ), Messages.getString( "ClearAction.DeleteAllLogFiles" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            view.getUniversalListener().clearInput();
        }

    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();
    }

}
