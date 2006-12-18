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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.ui.actions.proxy.ModificationLogsViewActionProxy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IActionBars;


public class ModificationLogsViewActionGroup implements IMenuListener
{

    private ModificationLogsView view;

    private static final String olderAction = "olderAction";

    private static final String newerAction = "newerAction";

    private static final String refreshAction = "refreshAction";

    private Map modificationLogsViewActionMap;


    public ModificationLogsViewActionGroup( ModificationLogsView view )
    {
        this.view = view;
        SourceViewer viewer = this.view.getMainWidget().getSourceViewer();

        this.modificationLogsViewActionMap = new HashMap();
        this.modificationLogsViewActionMap.put( olderAction, new ModificationLogsViewActionProxy( viewer,
            new OlderAction( view ) ) );
        this.modificationLogsViewActionMap.put( newerAction, new ModificationLogsViewActionProxy( viewer,
            new NewerAction( view ) ) );
        this.modificationLogsViewActionMap.put( refreshAction, new ModificationLogsViewActionProxy( viewer,
            new RefreshAction( view ) ) );
    }


    public void dispose()
    {
        if ( this.view != null )
        {

            for ( Iterator it = this.modificationLogsViewActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = ( String ) it.next();
                ModificationLogsViewActionProxy action = ( ModificationLogsViewActionProxy ) this.modificationLogsViewActionMap
                    .get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            this.modificationLogsViewActionMap.clear();
            this.modificationLogsViewActionMap = null;

            this.view = null;
        }
    }


    public void fillActionBars( IActionBars actionBars )
    {
        // Tool Bar
        actionBars.getToolBarManager().add( ( IAction ) this.modificationLogsViewActionMap.get( refreshAction ) );
        actionBars.getToolBarManager().add( new Separator() );
        actionBars.getToolBarManager().add( ( IAction ) this.modificationLogsViewActionMap.get( olderAction ) );
        actionBars.getToolBarManager().add( ( IAction ) this.modificationLogsViewActionMap.get( newerAction ) );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

    }


    public void setInput( ModificationLogsViewInput input )
    {
        for ( Iterator it = this.modificationLogsViewActionMap.values().iterator(); it.hasNext(); )
        {
            ModificationLogsViewActionProxy action = ( ModificationLogsViewActionProxy ) it.next();
            action.inputChanged( input );
        }
    }

}
