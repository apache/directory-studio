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


import java.util.Map;

import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager;
import org.apache.directory.studio.ldapbrowser.ui.actions.proxy.ModificationLogsViewActionProxy;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;


/**
 * The ModificationLogsViewActionGroup manages all the actions of the modification logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModificationLogsViewActionGroup implements ActionHandlerManager, IMenuListener
{

    /** The view. */
    private ModificationLogsView view;

    /** The Constant olderAction. */
    private static final String olderAction = "olderAction"; //$NON-NLS-1$

    /** The Constant newerAction. */
    private static final String newerAction = "newerAction"; //$NON-NLS-1$

    /** The Constant refreshAction. */
    private static final String refreshAction = "refreshAction"; //$NON-NLS-1$

    /** The Constant clearAction. */
    private static final String clearAction = "clearAction"; //$NON-NLS-1$

    /** The Constant exportAction. */
    private static final String exportAction = "exportAction"; //$NON-NLS-1$

    /** The enable modification logs action. */
    private EnableModificationLogsAction enableModificationLogsAction;

    /** The open modification logs preference page action. */
    private OpenModificationLogsPreferencePageAction openModificationLogsPreferencePageAction;

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
//        SourceViewer viewer = this.view.getMainWidget().getSourceViewer();

//        modificationLogsViewActionMap = new HashMap<String, ModificationLogsViewActionProxy>();
//        modificationLogsViewActionMap.put( olderAction, new ModificationLogsViewActionProxy( viewer, new OlderAction(
//            view ) ) );
//        modificationLogsViewActionMap.put( newerAction, new ModificationLogsViewActionProxy( viewer, new NewerAction(
//            view ) ) );
//        modificationLogsViewActionMap.put( refreshAction, new ModificationLogsViewActionProxy( viewer,
//            new RefreshAction( view ) ) );
//        modificationLogsViewActionMap.put( clearAction, new ModificationLogsViewActionProxy( viewer, new ClearAction(
//            view ) ) );
//        modificationLogsViewActionMap.put( exportAction, new ModificationLogsViewActionProxy( viewer,
//            new ExportAction() ) );
//        enableModificationLogsAction = new EnableModificationLogsAction();
//        openModificationLogsPreferencePageAction = new OpenModificationLogsPreferencePageAction();
    }


    /**
     * Disposes this action group.
     */
    public void dispose()
    {
//        if ( view != null )
//        {
//            for ( ModificationLogsViewActionProxy action : modificationLogsViewActionMap.values() )
//            {
//                action.dispose();
//                action = null;
//            }
//            modificationLogsViewActionMap.clear();
//            modificationLogsViewActionMap = null;
//
//            enableModificationLogsAction = null;
//            openModificationLogsPreferencePageAction = null;
//
//            view = null;
//        }
    }


    /**
     * Fill the action bars.
     * 
     * @param actionBars the action bars
     */
    public void fillActionBars( IActionBars actionBars )
    {
//        // Tool Bar
//        actionBars.getToolBarManager().add( modificationLogsViewActionMap.get( clearAction ) );
//        actionBars.getToolBarManager().add( modificationLogsViewActionMap.get( refreshAction ) );
//        actionBars.getToolBarManager().add( new Separator() );
//        actionBars.getToolBarManager().add( modificationLogsViewActionMap.get( olderAction ) );
//        actionBars.getToolBarManager().add( modificationLogsViewActionMap.get( newerAction ) );
//        actionBars.getToolBarManager().add( new Separator() );
//        actionBars.getToolBarManager().add( modificationLogsViewActionMap.get( exportAction ) );
//
//        // Menu Bar
//        actionBars.getMenuManager().add( enableModificationLogsAction );
//        actionBars.getMenuManager().add( new Separator() );
//        actionBars.getMenuManager().add( openModificationLogsPreferencePageAction );
//        actionBars.getMenuManager().addMenuListener( new IMenuListener()
//        {
//            public void menuAboutToShow( IMenuManager manager )
//            {
//                enableModificationLogsAction.setChecked( ConnectionCorePlugin.getDefault().getPluginPreferences()
//                    .getBoolean( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE ) );
//            }
//        } );
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
//        for ( ModificationLogsViewActionProxy action : modificationLogsViewActionMap.values() )
//        {
//            action.inputChanged( input );
//        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager#activateGlobalActionHandlers()
     */
    public void activateGlobalActionHandlers()
    {
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager#deactivateGlobalActionHandlers()
     */
    public void deactivateGlobalActionHandlers()
    {
    }

}
