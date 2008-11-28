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
package org.apache.directory.studio.apacheds.actions;


import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the open action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenConfigurationAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of OpenConfigurationAction.
     */
    public OpenConfigurationAction()
    {
        super( Messages.getString( "OpenConfigurationAction.OpenConfiguration" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of OpenConfigurationAction.
     * 
     * @param view
     *      the associated view
     */
    public OpenConfigurationAction( ServersView view )
    {
        super( Messages.getString( "OpenConfigurationAction.OpenConfiguration" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( ApacheDsPluginConstants.CMD_OPEN_CONFIGURATION );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_OPEN_CONFIGURATION );
        setToolTipText( Messages.getString( "OpenConfigurationAction.OpenConfigurationToolTip" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        if ( view != null )
        {
            // What we get from the TableViewer is a StructuredSelection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();

            // Here's the real object
            Server server = ( Server ) selection.getFirstElement();
            if ( server != null )
            {
                // Opening the editor
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                PathEditorInput input = new PathEditorInput( ApacheDsPluginUtils.getApacheDsServersFolder().append(
                    server.getId() ).append( "conf" ).append( "server.xml" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                try
                {
                    page.openEditor( input, ServerConfigurationEditor.ID );
                }
                catch ( PartInitException e )
                {
                    ApacheDsPluginUtils.reportError( Messages.getString( "OpenConfigurationAction.ErrorWhenOpening" ) //$NON-NLS-1$
                        + e.getMessage() );
                }
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }

    /**
     * This IEditorInput is used to open files that are located in the local file system.
     * 
     * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class PathEditorInput implements IPathEditorInput
    {
        /** The absolute path in local file system */
        private IPath path;


        /**
         * 
         * Creates a new instance of PathEditorInput.
         *
         * @param path the absolute path
         */
        public PathEditorInput( IPath path )
        {
            if ( path == null )
            {
                throw new IllegalArgumentException();
            }

            this.path = path;
        }


        /**
         * Returns hash code of the path.
         */
        public int hashCode()
        {
            return path.hashCode();
        }


        /** 
         * This implemention just compares the paths
         */
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }

            if ( o instanceof PathEditorInput )
            {
                PathEditorInput input = ( PathEditorInput ) o;
                return path.equals( input.path );
            }

            return false;
        }


        /**
         * {@inheritDoc}
         */
        public boolean exists()
        {
            return path.toFile().exists();
        }


        /**
         * {@inheritDoc}
         */
        public ImageDescriptor getImageDescriptor()
        {
            return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor( path.toString() );
        }


        /**
         * Returns the file name only.
         */
        public String getName()
        {
            return path.toFile().getName();
            //return path.toString();
        }


        /**
         * Returns the complete path. 
         */
        public String getToolTipText()
        {
            return path.makeRelative().toOSString();
        }


        /**
         * {@inheritDoc}
         */
        public IPath getPath()
        {
            return path;
        }


        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Object getAdapter( Class adapter )
        {
            return Platform.getAdapterManager().getAdapter( this, adapter );
        }


        /**
         * {@inheritDoc}
         */
        public IPersistableElement getPersistable()
        {
            return null;
        }


        /**
         * Returns the path.
         */
        public IPath getErrorMessage( Object element )
        {
            if ( element instanceof PathEditorInput )
            {
                PathEditorInput input = ( PathEditorInput ) element;
                return input.getPath();
            }

            return null;
        }
    }
}
