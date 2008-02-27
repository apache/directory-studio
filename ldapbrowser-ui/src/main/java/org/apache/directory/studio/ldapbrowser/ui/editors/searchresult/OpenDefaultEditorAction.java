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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.ui.actions.proxy.SearchResultEditorActionProxy;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;


public class OpenDefaultEditorAction extends AbstractOpenEditorAction
{

    private SearchResultEditorActionProxy bestValueEditorProxy;


    public OpenDefaultEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        ValueEditorManager valueEditorManager, SearchResultEditorActionProxy bestValueEditorProxy,
        SearchResultEditorActionGroup actionGroup )
    {
        super( viewer, cursor, valueEditorManager, actionGroup );
        this.bestValueEditorProxy = bestValueEditorProxy;
    }


    public void run()
    {
        this.bestValueEditorProxy.run();
    }


    public void dispose()
    {
        this.bestValueEditorProxy = null;

        super.dispose();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#getCommandId()
     */
    public String getCommandId()
    {
        return BrowserCommonConstants.ACTION_ID_EDIT_VALUE;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        if ( bestValueEditorProxy != null )
        {
            return bestValueEditorProxy.getImageDescriptor();
        }
        else
        {
            return null;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#getText()
     */
    public String getText()
    {
        return "Edit Value";
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#isEnabled()
     */
    public boolean isEnabled()
    {
        if ( bestValueEditorProxy != null )
        {
            return bestValueEditorProxy.isEnabled();
        }
        else
        {
            return false;
        }
    }

}
