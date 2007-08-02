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
package org.apache.directory.studio.apacheds.schemaeditor.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.CollapseAllAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ConnectAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.DeleteSchemaElementAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ExportSchemasAsOpenLdapAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ExportSchemasAsXmlAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ExportSchemasForADSAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ImportSchemasFromOpenLdapAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ImportSchemasFromXmlAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.LinkWithEditorSchemaViewAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.NewAttributeTypeAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.NewObjectClassAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.NewSchemaAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenElementAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenSchemaViewPreferenceAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenSchemaViewSortingDialogAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenTypeHierarchyAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ShowSearchFieldAction;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SchemaView;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SearchView;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder.FolderType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Controller for the SearchView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchViewController
{
    /** The associated view */
    private SearchView view;

    // The Actions
    private ShowSearchFieldAction showSearchField;


    /**
     * Creates a new instance of SearchViewController.
     *
     * @param view
     *      the associated view
     */
    public SearchViewController( SearchView view )
    {
        this.view = view;

        initActions();
        initToolbar();
        initMenu();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        showSearchField = new ShowSearchFieldAction( view );
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( showSearchField );
    }


    /**
     * Initializes the Menu.
     */
    private void initMenu()
    {
        IMenuManager menu = view.getViewSite().getActionBars().getMenuManager();
    }
}
