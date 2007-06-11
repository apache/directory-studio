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

package org.apache.directory.studio.schemas.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.controller.actions.LinkWithEditorHierarchyView;
import org.apache.directory.studio.schemas.controller.actions.OpenHierarchyViewPreferencesAction;
import org.apache.directory.studio.schemas.controller.actions.ShowSubtypeHierarchyAction;
import org.apache.directory.studio.schemas.controller.actions.ShowSupertypeHierarchyAction;
import org.apache.directory.studio.schemas.model.LDAPModelEvent;
import org.apache.directory.studio.schemas.model.PoolListener;
import org.apache.directory.studio.schemas.model.SchemaPool;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditorInput;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditor;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditorInput;
import org.apache.directory.studio.schemas.view.views.HierarchyView;
import org.apache.directory.studio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Controller for the Hierarchy View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchyViewController implements PoolListener
{
    /** The associated view */
    private HierarchyView view;

    /** The authorized Preferences keys*/
    List<String> authorizedPrefs;

    // The Actions
    private Action showSupertypeHierarchy;
    private Action showSubtypeHierarchy;
    private Action linkWithEditor;
    private Action openPreferencePage;


    /**
     * Creates a new instance of SchemasViewController.
     *
     * @param view
     *      the associated view
     */
    public HierarchyViewController( HierarchyView view )
    {
        this.view = view;

        SchemaPool.getInstance().addListener( this );

        initAuthorizedPrefs();
        initActions();
        initToolbar();
        initMenu();
        initDoubleClickListener();
        initPreferencesListener();
    }


    /**
     * Initializes the values for the authorized preferences.
     */
    private void initAuthorizedPrefs()
    {
        authorizedPrefs = new ArrayList<String>();
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_DISPLAY );
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( PluginConstants.PREFS_HIERARCHY_VIEW_MODE ) == null )
        {
            Activator.getDefault().getDialogSettings().put( PluginConstants.PREFS_HIERARCHY_VIEW_MODE,
                PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE );
        }
        showSupertypeHierarchy = new ShowSupertypeHierarchyAction( view );
        showSubtypeHierarchy = new ShowSubtypeHierarchyAction( view );
        linkWithEditor = new LinkWithEditorHierarchyView( view );
        openPreferencePage = new OpenHierarchyViewPreferencesAction();
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( showSupertypeHierarchy );
        toolbar.add( showSubtypeHierarchy );
    }


    /**
     * Initializes the Menu.
     */
    private void initMenu()
    {
        IMenuManager menu = view.getViewSite().getActionBars().getMenuManager();
        menu.add( showSupertypeHierarchy );
        menu.add( showSubtypeHierarchy );
        menu.add( new Separator() );
        menu.add( linkWithEditor );
        menu.add( new Separator() );
        menu.add( openPreferencePage );
    }


    /**
     * Initializes the DoubleClickListener.
     */
    private void initDoubleClickListener()
    {
        view.getViewer().addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                // What we get from the treeViewer is a StructuredSelection
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                // Here's the real object (an AttributeTypeWrapper, ObjectClassWrapper or IntermediateNode)
                Object objectSelection = selection.getFirstElement();
                IEditorInput input = null;
                String editorId = null;

                // Selecting the right editor and input
                if ( objectSelection instanceof AttributeTypeWrapper )
                {
                    input = new AttributeTypeEditorInput( ( ( AttributeTypeWrapper ) objectSelection )
                        .getMyAttributeType() );
                    editorId = AttributeTypeEditor.ID;
                }
                else if ( objectSelection instanceof ObjectClassWrapper )
                {
                    input = new ObjectClassEditorInput( ( ( ObjectClassWrapper ) objectSelection ).getMyObjectClass() );
                    editorId = ObjectClassEditor.ID;
                }

                // Let's open the editor
                if ( input != null )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( input, editorId );
                    }
                    catch ( PartInitException e )
                    {
                        // TODO ADD A LOGGER
                    }
                }
            }
        } );
    }


    /**
     * Initializes the listener on the preferences store.
     */
    private void initPreferencesListener()
    {
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener( new IPropertyChangeListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
             */
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( authorizedPrefs.contains( event.getProperty() ) )
                {
                    view.refresh();
                }
            }
        } );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.model.PoolListener#poolChanged(org.apache.directory.studio.schemas.model.SchemaPool, org.apache.directory.studio.schemas.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        view.refresh();
    }
}
