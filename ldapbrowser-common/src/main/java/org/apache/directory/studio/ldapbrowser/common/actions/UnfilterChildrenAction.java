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

package org.apache.directory.studio.ldapbrowser.common.actions;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenJob;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This action removes the children filter from the currently selected entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UnfilterChildrenAction extends BrowserAction
{
    /**
     * Creates a new instance of UnfilterChildrenAction.
     */
    public UnfilterChildrenAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( getSelectedEntries().length == 1 )
        {
            getSelectedEntries()[0].setChildrenFilter( null );
            new InitializeChildrenJob( new IEntry[]
                { getSelectedEntries()[0] } ).execute();
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Remove Children Filter";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_UNFILTER_DIT );
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
        return getSelectedSearches().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedEntries().length == 1 && getSelectedEntries()[0].getChildrenFilter() != null;
    }
}
