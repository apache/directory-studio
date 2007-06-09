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

package org.apache.directory.ldapstudio.browser.ui.views.modificationlogs;


import java.io.File;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * Action to switch to an older logfile.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OlderAction extends BrowserAction
{

    /** The modification logs view. */
    private ModificationLogsView view;


    /**
     * Creates a new instance of OlderAction.
     *
     * @param view the modification logs view
     */
    public OlderAction( ModificationLogsView view )
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
        ModificationLogsViewInput oldInput = ( ModificationLogsViewInput ) getInput();
        ModificationLogsViewInput newInput = new ModificationLogsViewInput( oldInput.getConnection(), oldInput
            .getIndex() + 1 );
        view.getUniversalListener().setInput( newInput );
        view.getUniversalListener().scrollToNewest();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Older";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_PREVIOUS );
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
        if ( getInput() != null && ( getInput() instanceof ModificationLogsViewInput ) )
        {
            ModificationLogsViewInput input = ( ModificationLogsViewInput ) getInput();
            File[] files = input.getConnection().getModificationLogger().getFiles();
            int i = input.getIndex() + 1;
            if ( 0 <= i && i < files.length && files[i] != null && files[i].exists() && files[i].canRead() )
            {
                return true;
            }
        }

        return false;
    }

}
