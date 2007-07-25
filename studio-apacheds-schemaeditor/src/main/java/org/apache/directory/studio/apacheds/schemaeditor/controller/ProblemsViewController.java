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


import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaCheckerListener;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.ProblemsView;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaErrorWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWarningWrapper;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Controller for the ProblemsView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProblemsViewController
{
    /** The associated view */
    private ProblemsView view;

    /** The SchemaCheckerListener */
    private SchemaCheckerListener schemaCheckerListener = new SchemaCheckerListener()
    {
        public void schemaCheckerUpdated()
        {
            Display.getDefault().asyncExec( new Runnable()
            {
                public void run()
                {
                    view.reloadViewer();
                }
            } );
        }
    };


    // The Actions

    /**
     * Creates a new instance of SchemasViewController.
     *
     * @param view
     *      the associated view
     */
    public ProblemsViewController( ProblemsView view )
    {
        this.view = view;

        initActions();
        initToolbar();
        initProjectsHandlerListener();
        initDoubleClickListener();
        initSchemaCheckerListener();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        //        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
    }


    /**
     * Initializes the ProjectsHandlerListener.
     */
    private void initProjectsHandlerListener()
    {
        Activator.getDefault().getProjectsHandler().addListener( new ProjectsHandlerAdapter()
        {
            public void openProjectChanged( Project oldProject, Project newProject )
            {
                if ( oldProject != null )
                {
                    removeSchemaCheckerListener( oldProject );

                }

                if ( newProject != null )
                {
                    addSchemaCheckerListener( newProject );
                    view.reloadViewer();
                }
                else
                {
                	view.setErrorsAndWarningsCount(0, 0);
                    view.getViewer().setInput( null );
                }
            }
        } );
    }


    /**
     * Adds the SchemaCheckerListener.
     *
     * @param project
     *      the project
     */
    private void addSchemaCheckerListener( Project project )
    {
        SchemaChecker schemaChecker = project.getSchemaChecker();
        if ( schemaChecker != null )
        {
            schemaChecker.addListener( schemaCheckerListener );
            schemaChecker.enableModificationsListening();
        }
    }


    /**
     * Removes the SchemaCheckerListener.
     *
     * @param project
     *      the project
     */
    private void removeSchemaCheckerListener( Project project )
    {
        SchemaChecker schemaChecker = project.getSchemaChecker();
        if ( schemaChecker != null )
        {
            schemaChecker.removeListener( schemaCheckerListener );
            schemaChecker.disableModificationsListening();
        }
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
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                TreeViewer viewer = view.getViewer();

                // What we get from the treeViewer is a StructuredSelection
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                // Here's the real object (an AttributeTypeWrapper, ObjectClassWrapper or IntermediateNode)
                Object objectSelection = selection.getFirstElement();
                IEditorInput input = null;
                String editorId = null;

                // Selecting the right editor and input
                if ( objectSelection instanceof SchemaErrorWrapper )
                {
                    SchemaObject object = ( ( SchemaErrorWrapper ) objectSelection ).getSchemaError().getSource();

                    if ( object instanceof AttributeTypeImpl )
                    {
                        input = new AttributeTypeEditorInput( ( AttributeTypeImpl ) object );
                        editorId = AttributeTypeEditor.ID;
                    }
                    else if ( object instanceof ObjectClassImpl )
                    {
                        input = new ObjectClassEditorInput( ( ObjectClassImpl ) object );
                        editorId = ObjectClassEditor.ID;
                    }
                }
                else if ( objectSelection instanceof SchemaWarningWrapper )
                {
                    SchemaObject object = ( ( SchemaWarningWrapper ) objectSelection ).getSchemaWarning().getSource();

                    if ( object instanceof AttributeTypeImpl )
                    {
                        input = new AttributeTypeEditorInput( ( AttributeTypeImpl ) object );
                        editorId = AttributeTypeEditor.ID;
                    }
                    else if ( object instanceof ObjectClassImpl )
                    {
                        input = new ObjectClassEditorInput( ( ObjectClassImpl ) object );
                        editorId = ObjectClassEditor.ID;
                    }
                }
                else if ( ( objectSelection instanceof Folder ) )
                {
                    // Here we don't open an editor, we just expand the node.
                    viewer.setExpandedState( objectSelection, !viewer.getExpandedState( objectSelection ) );
                }

                // Let's open the editor
                if ( input != null )
                {
                    try
                    {
                        page.openEditor( input, editorId );
                    }
                    catch ( PartInitException e )
                    {
                        // TODO
                    }
                }
            }
        } );
    }


    /**
     * Initializes the SchemaCheckerListener.
     */
    private void initSchemaCheckerListener()
    {
        SchemaChecker schemaChecker = Activator.getDefault().getSchemaChecker();

        if ( schemaChecker != null )
        {
            schemaChecker.addListener( new SchemaCheckerListener()
            {
                /* (non-Javadoc)
                 * @see org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaCheckerListener#schemaCheckerUpdated()
                 */
                public void schemaCheckerUpdated()
                {
                    Display.getDefault().asyncExec( new Runnable()
                    {
                        public void run()
                        {
                            view.reloadViewer();
                        }
                    } );
                }
            } );
        }
    }
}
