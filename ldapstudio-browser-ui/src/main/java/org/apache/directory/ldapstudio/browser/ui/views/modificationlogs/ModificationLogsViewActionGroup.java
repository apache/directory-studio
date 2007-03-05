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


/**
 * The ModificationLogsViewActionGroup manages all the actions of the modification logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModificationLogsViewActionGroup implements IMenuListener
{

    /** The view. */
    private ModificationLogsView view;

    /** The Constant olderAction. */
    private static final String olderAction = "olderAction";

    /** The Constant newerAction. */
    private static final String newerAction = "newerAction";

    /** The Constant refreshAction. */
    private static final String refreshAction = "refreshAction";

    /** The modification logs view action map. */
    private Map<String, ModificationLogsViewActionProxy> modificationLogsViewActionMap;


    /**
     * Creates a new instance of ModificationLogsViewActionGroup.
     *
     * @param view the modification logs view
     */
    public ModificationLogsViewActionGroup( ModificationLogsView view )
    {
        this.view = view;
        SourceViewer viewer = this.view.getMainWidget().getSourceViewer();

        modificationLogsViewActionMap = new HashMap<String, ModificationLogsViewActionProxy>();
        modificationLogsViewActionMap.put( olderAction, new ModificationLogsViewActionProxy( viewer, new OlderAction(
            view ) ) );
        modificationLogsViewActionMap.put( newerAction, new ModificationLogsViewActionProxy( viewer, new NewerAction(
            view ) ) );
        modificationLogsViewActionMap.put( refreshAction, new ModificationLogsViewActionProxy( viewer,
            new RefreshAction( view ) ) );
    }


    /**
     * Disposes thes action group.
     */
    public void dispose()
    {
        if ( view != null )
        {
            for ( Iterator it = modificationLogsViewActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = ( String ) it.next();
                ModificationLogsViewActionProxy action = ( ModificationLogsViewActionProxy ) modificationLogsViewActionMap
                    .get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            modificationLogsViewActionMap.clear();
            modificationLogsViewActionMap = null;

            view = null;
        }
    }


    /**
     * Fill the action bars.
     * 
     * @param actionBars the action bars
     */
    public void fillActionBars( IActionBars actionBars )
    {
        // Tool Bar
        actionBars.getToolBarManager().add( ( IAction ) modificationLogsViewActionMap.get( refreshAction ) );
        actionBars.getToolBarManager().add( new Separator() );
        actionBars.getToolBarManager().add( ( IAction ) modificationLogsViewActionMap.get( olderAction ) );
        actionBars.getToolBarManager().add( ( IAction ) modificationLogsViewActionMap.get( newerAction ) );
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {
    }


    /**
     * Propagates the input to all actions.
     * 
     * @param input the input
     */
    public void setInput( ModificationLogsViewInput input )
    {
        for ( Iterator it = modificationLogsViewActionMap.values().iterator(); it.hasNext(); )
        {
            ModificationLogsViewActionProxy action = ( ModificationLogsViewActionProxy ) it.next();
            action.inputChanged( input );
        }
    }

}
