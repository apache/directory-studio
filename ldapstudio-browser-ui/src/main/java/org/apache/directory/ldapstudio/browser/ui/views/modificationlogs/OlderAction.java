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
import org.apache.directory.ldapstudio.browser.ui.actions.BrowserAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class OlderAction extends BrowserAction
{

    private ModificationLogsView view;


    public OlderAction( ModificationLogsView view )
    {
        super();
        this.view = view;
    }


    public void dispose()
    {
        super.dispose();
    }


    public void run()
    {
        ModificationLogsViewInput oldInput = ( ModificationLogsViewInput ) getInput();
        ModificationLogsViewInput newInput = new ModificationLogsViewInput( oldInput.connection, oldInput.index + 1 );
        view.getUniversalListener().setInput( newInput );
        view.getUniversalListener().scrollToNewest();

        // go to bottom
        // view.getMainWidget().getSourceViewer().setTopIndex(Integer.MAX_VALUE);
    }


    public String getText()
    {
        return "Older";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_PREVIOUS );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        if ( getInput() != null && ( getInput() instanceof ModificationLogsViewInput ) )
        {
            ModificationLogsViewInput input = ( ModificationLogsViewInput ) getInput();
            File[] files = input.connection.getModificationLogger().getFiles();
            int i = input.index + 1;
            if ( 0 <= i && i < files.length && files[i] != null && files[i].exists() && files[i].canRead() )
            {
                return true;
            }
        }

        return false;
    }

}
