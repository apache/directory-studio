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
package org.apache.directory.studio.apacheds.schemaeditor;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.apacheds.schemaeditor.controller.ProjectsHandlerListener;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.ProblemsView;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SchemaView;
import org.apache.directory.studio.apacheds.schemaeditor.view.widget.SchemaCodeScanner;
import org.apache.directory.studio.apacheds.schemaeditor.view.widget.SchemaTextAttributeProvider;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Activator extends AbstractUIPlugin
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.apache.directory.studio.apacheds.schemaeditor"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator plugin;

    /** the Schema Code Scanner */
    private ITokenScanner schemaCodeScanner;

    /** The Schema Text Attribute Provider */
    private SchemaTextAttributeProvider schemaTextAttributeProvider;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaCheker */
    private SchemaChecker schemaChecker;

    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;

    /** The SchemaView */
    private SchemaView schemaView;

    /** The ProblemsView */
    private ProblemsView problemsView;


    /**
     * Creates a new instance of Activator.
     */
    public Activator()
    {
        plugin = this;
        projectsHandler = ProjectsHandler.getInstance();
        projectsHandler.addListener( new ProjectsHandlerListener()
        {
            public void openProjectChanged( Project oldProject, Project newProject )
            {
                // Listing all the editors from the Schema Editor Plugin.
                List<IEditorReference> editors = new ArrayList<IEditorReference>();
                for ( IEditorReference editorReference : getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getEditorReferences() )
                {
                    if ( ( editorReference.getId().equals( AttributeTypeEditor.ID ) )
                        || ( editorReference.getId().equals( ObjectClassEditor.ID ) )
                        || ( editorReference.getId().equals( SchemaEditor.ID ) ) )
                    {
                        editors.add( editorReference );
                    }
                }

                // Closing the opened editors
                if ( !getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditors(
                    editors.toArray( new IEditorReference[0] ), true ) )
                {
                    // If all the editors have not been closed, we force them to be closed.
                    getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditors(
                        editors.toArray( new IEditorReference[0] ), false );
                }

                if ( newProject == null )
                {
                    schemaHandler = null;
                    schemaChecker = null;
                }
                else
                {
                    // Registering the SchemaHandler and SchemaChecker
                    schemaHandler = newProject.getSchemaHandler();
                    schemaChecker = newProject.getSchemaChecker();
                    schemaChecker.enableModificationsListening();

                    // Reloading views
                    //                    schemaView = ( SchemaView ) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                    //                        SchemaView.ID );
                    problemsView = ( ProblemsView ) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                        ProblemsView.ID );
                    //                    schemaView.reloadViewer();
                    problemsView.reloadViewer();
                }

                PluginUtils.saveProjects();
            }


            public void projectAdded( Project project )
            {
                PluginUtils.saveProjects();
            }


            public void projectRemoved( Project project )
            {
                PluginUtils.saveProjects();
            }
        } );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        // Loading the projects
        PluginUtils.loadProjects();

    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        // Saving the projects
        PluginUtils.saveProjects();

        super.stop( context );
        plugin = null;
    }


    /**
     * Returns the shared instance.
     *
     * @return
     *      the shared instance
     */
    public static Activator getDefault()
    {
        return plugin;
    }


    /**
     * Gets the SchemaHandler
     *
     * @return
     *      the SchemaHandler
     */
    public SchemaHandler getSchemaHandler()
    {
        return schemaHandler;
    }


    /**
     * Gets the SchemaChecker
     *
     * @return
     *      the SchemaChecker
     */
    public SchemaChecker getSchemaChecker()
    {
        return schemaChecker;
    }


    /**
     * Gets the ProjectsHandler
     *
     * @return
     *      the ProjectsHandler
     */
    public ProjectsHandler getProjectsHandler()
    {
        return projectsHandler;
    }


    /**
     * Returns the Schema Code Scanner.
     *
     * @return
     *      the Schema Code Scanner
     */
    public ITokenScanner getSchemaCodeScanner()
    {
        if ( schemaCodeScanner == null )
        {
            schemaCodeScanner = new SchemaCodeScanner( getSchemaTextAttributeProvider() );
        }

        return schemaCodeScanner;
    }


    /**
     * Returns the Schema Text Attribute Provider.
     *
     * @return
     *     the Schema Text Attribute Provider 
     */
    private SchemaTextAttributeProvider getSchemaTextAttributeProvider()
    {
        if ( schemaTextAttributeProvider == null )
        {
            schemaTextAttributeProvider = new SchemaTextAttributeProvider();
        }

        return schemaTextAttributeProvider;
    }
}
