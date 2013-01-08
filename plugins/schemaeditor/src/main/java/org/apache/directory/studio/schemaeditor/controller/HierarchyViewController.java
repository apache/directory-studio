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

package org.apache.directory.studio.schemaeditor.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.actions.LinkWithEditorHierarchyViewAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenHierarchyViewPreferencesAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ShowSubtypeHierarchyAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ShowSupertypeHierarchyAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ShowTypeHierarchyAction;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.schemaeditor.view.views.HierarchyView;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
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
 */
public class HierarchyViewController
{
    /** The associated view */
    private HierarchyView view;

    /** The authorized Preferences keys*/
    List<String> authorizedPrefs;

    /** The ProjectsHandlerListener */
    private ProjectsHandlerListener projectsHandlerListener = new ProjectsHandlerAdapter()
    {
        public void openProjectChanged( Project oldProject, Project newProject )
        {
            // Since we're changing of project, let's set the input as null
            view.setInput( null );

            if ( newProject != null )
            {
                view.getViewer().getTree().setEnabled( true );
                showTypeHierarchy.setEnabled( true );
                showSupertypeHierarchy.setEnabled( true );
                showSubtypeHierarchy.setEnabled( true );
                linkWithEditor.setEnabled( true );
                openPreferencePage.setEnabled( true );
            }
            else
            {
                view.getViewer().getTree().setEnabled( false );
                showTypeHierarchy.setEnabled( false );
                showSupertypeHierarchy.setEnabled( false );
                showSubtypeHierarchy.setEnabled( false );
                linkWithEditor.setEnabled( false );
                openPreferencePage.setEnabled( false );
            }
        }
    };

    /** The IDoubleClickListener */
    private IDoubleClickListener doubleClickListener = new IDoubleClickListener()
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
                input = new AttributeTypeEditorInput( ( ( AttributeTypeWrapper ) objectSelection ).getAttributeType() );
                editorId = AttributeTypeEditor.ID;
            }
            else if ( objectSelection instanceof ObjectClassWrapper )
            {
                input = new ObjectClassEditorInput( ( ( ObjectClassWrapper ) objectSelection ).getObjectClass() );
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
                    PluginUtils.logError( Messages.getString( "HierarchyViewController.ErrorOpeningEditor" ), e ); //$NON-NLS-1$
                    ViewUtils.displayErrorMessageDialog(
                        Messages.getString( "HierarchyViewController.Error" ), Messages //$NON-NLS-1$
                            .getString( "HierarchyViewController.ErrorOpeningEditor" ) ); //$NON-NLS-1$
                }
            }
        }
    };

    /** The IPropertyChangeListener */
    private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
    {
        public void propertyChange( PropertyChangeEvent event )
        {
            if ( authorizedPrefs.contains( event.getProperty() ) )
            {
                view.refresh();
            }
        }
    };

    // The Actions
    private Action showTypeHierarchy;
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

        initAuthorizedPrefs();
        initActions();
        initToolbar();
        initMenu();
        initProjectsHandlerListener();
        initDoubleClickListener();
        initPreferencesListener();
        initState();
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
                PluginConstants.PREFS_HIERARCHY_VIEW_MODE_TYPE );
        }
        showTypeHierarchy = new ShowTypeHierarchyAction( view );
        showSupertypeHierarchy = new ShowSupertypeHierarchyAction( view );
        showSubtypeHierarchy = new ShowSubtypeHierarchyAction( view );
        linkWithEditor = new LinkWithEditorHierarchyViewAction( view );
        openPreferencePage = new OpenHierarchyViewPreferencesAction();
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( showTypeHierarchy );
        toolbar.add( showSupertypeHierarchy );
        toolbar.add( showSubtypeHierarchy );
        toolbar.add( new Separator() );
        toolbar.add( linkWithEditor );
    }


    /**
     * Initializes the Menu.
     */
    private void initMenu()
    {
        IMenuManager menu = view.getViewSite().getActionBars().getMenuManager();
        menu.add( showTypeHierarchy );
        menu.add( showSupertypeHierarchy );
        menu.add( showSubtypeHierarchy );
        menu.add( new Separator() );
        menu.add( linkWithEditor );
        menu.add( new Separator() );
        menu.add( openPreferencePage );
    }


    /**
     * Initializes the ProjectsHandlerListener.
     */
    private void initProjectsHandlerListener()
    {
        Activator.getDefault().getProjectsHandler().addListener( projectsHandlerListener );
    }


    /**
     * Initializes the DoubleClickListener.
     */
    private void initDoubleClickListener()
    {
        view.getViewer().addDoubleClickListener( doubleClickListener );
    }


    /**
     * Initializes the listener on the preferences store.
     */
    private void initPreferencesListener()
    {
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener( propertyChangeListener );
    }


    /**
     * Initializes the state of the View.
     */
    private void initState()
    {
        if ( Activator.getDefault().getProjectsHandler().getOpenProject() != null )
        {
            view.getViewer().getTree().setEnabled( true );
            showTypeHierarchy.setEnabled( true );
            showSupertypeHierarchy.setEnabled( true );
            showSubtypeHierarchy.setEnabled( true );
            linkWithEditor.setEnabled( true );
            openPreferencePage.setEnabled( true );
        }
        else
        {
            view.getViewer().getTree().setEnabled( false );
            showTypeHierarchy.setEnabled( false );
            showSupertypeHierarchy.setEnabled( false );
            showSubtypeHierarchy.setEnabled( false );
            linkWithEditor.setEnabled( false );
            openPreferencePage.setEnabled( false );
        }
    }


    /**
     * This method is called when the view is disposed.
     */
    public void dispose()
    {
        Activator.getDefault().getProjectsHandler().removeListener( projectsHandlerListener );
        view.getViewer().removeDoubleClickListener( doubleClickListener );
        Activator.getDefault().getPreferenceStore().removePropertyChangeListener( propertyChangeListener );
    }
}
