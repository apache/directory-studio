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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.search.SearchPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.PlatformUI;


/**
 * This Action opens a new Search Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewSearchAction extends BrowserAction
{
    /**
     * Creates a new instance of NewSearchAction.
     */
    public NewSearchAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        NewSearchUI.openSearchDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow(), SearchPage.getId() );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "New Search...";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_SEARCH_NEW );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.eclipse.search.ui.openSearchDialog";
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }
}
