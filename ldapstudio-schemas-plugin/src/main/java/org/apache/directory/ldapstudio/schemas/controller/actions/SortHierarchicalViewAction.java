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

package org.apache.directory.ldapstudio.schemas.controller.actions;


import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.controller.ICommandIds;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.viewers.HierarchicalViewer;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AlphabeticalOrderComparator;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.UnAlphabeticalOrderComparator;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for sorting the Hierarchy View
 */
@SuppressWarnings("unused")//$NON-NLS-1$
public class SortHierarchicalViewAction extends Action
{
    private final IWorkbenchWindow window;

    public enum SortType
    {
        alphabetical, unalphabetical
    };

    private SortType type;


    /**
     * Default constructor
     * @param window the attached window
     * @param type the type of sorting
     * @param label the label to be displayed
     */
    public SortHierarchicalViewAction( IWorkbenchWindow window, SortType type, String label )
    {
        this.window = window;
        setText( label );
        this.type = type;

        // The id is used to refer to the action in a menu or toolbar
        setId( ICommandIds.CMD_SORT_HIERACHICAL_VIEWER );
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId( ICommandIds.CMD_SORT_HIERACHICAL_VIEWER );

        if ( type.equals( SortType.alphabetical ) )
            setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
                IImageKeys.SORT_ALPHABETICAL ) );
        else if ( type.equals( SortType.unalphabetical ) )
            setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
                IImageKeys.SORT_UNALPHABETICAL ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        HierarchicalViewer viewer = ( HierarchicalViewer ) window.getActivePage().findView(
            Application.PLUGIN_ID + ".view.HierarchicalViewer" ); //$NON-NLS-1$

        if ( type.equals( SortType.alphabetical ) )
        {
            viewer.setOrder( new AlphabeticalOrderComparator() );
        }
        else if ( type.equals( SortType.unalphabetical ) )
        {
            viewer.setOrder( new UnAlphabeticalOrderComparator() );
        }
    }
}
