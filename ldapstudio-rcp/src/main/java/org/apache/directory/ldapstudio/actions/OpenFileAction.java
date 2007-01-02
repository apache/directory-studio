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

package org.apache.directory.ldapstudio.actions;


import java.io.File;
import java.text.MessageFormat;

import org.apache.directory.ldapstudio.Messages;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;


/**
 * The Action is used to open files from file system. 
 * It creates IPathEditorInput inputs.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenFileAction extends Action implements IWorkbenchWindowActionDelegate
{

    /** The workbench window */
    private IWorkbenchWindow workbenchWindow;


    /**
     * Creates a new instance of OpenFileAction.
     */
    public OpenFileAction()
    {
        setId( "org.apache.directory.ldapstudio.openFile" ); //$NON-NLS-1$
        setText( Messages.getString( "OpenFileAction.Open_File" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "OpenFileAction.Open_file_from_filesystem" ) ); //$NON-NLS-1$
        setEnabled( true );
    }


    /**
     * Creates a new instance of OpenFileAction.
     *
     * @param window the workbench window
     */
    public OpenFileAction( IWorkbenchWindow window )
    {
        this();
        init( window );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        workbenchWindow = null;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        workbenchWindow = window;
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // get path
        FileDialog dialog = new FileDialog( workbenchWindow.getShell(), SWT.OPEN );
        dialog.setText( Messages.getString( "OpenFileAction.Open_File" ) ); //$NON-NLS-1$
        String path = dialog.open();
        if ( path == null || path.length() == 0 )
        {
            // canceled
            return;
        }

        // check file 
        File file = new File( path );
        if ( !file.exists() )
        {
            String msg = MessageFormat.format(
                Messages.getString( "OpenFileAction.File_x_does_not_exist" ), new Object[] //$NON-NLS-1$
                    { file.getName() } );
            MessageDialog.openWarning( workbenchWindow.getShell(), Messages
                .getString( "OpenFileAction.Warning_message" ), msg ); //$NON-NLS-1$
            return;
        }
        if ( !file.canRead() )
        {
            String msg = MessageFormat.format(
                Messages.getString( "OpenFileAction.File_x_is_not_readable" ), new Object[] //$NON-NLS-1$
                    { file.getName() } );
            MessageDialog.openWarning( workbenchWindow.getShell(), Messages
                .getString( "OpenFileAction.Warning_message" ), msg ); //$NON-NLS-1$
            return;
        }

        // get editor for this file
        IWorkbench workbench = workbenchWindow.getWorkbench();
        IEditorRegistry editorRegistry = workbench.getEditorRegistry();
        IEditorDescriptor descriptor = editorRegistry.getDefaultEditor( file.getName() );

        if ( descriptor == null )
        {
            String msg = MessageFormat.format(
                Messages.getString( "OpenFileAction.No_appropriate_editor_found_for_x" ), new Object[] //$NON-NLS-1$
                { file.getName() } );
            MessageDialog.openWarning( workbenchWindow.getShell(), Messages
                .getString( "OpenFileAction.Warning_message" ), msg ); //$NON-NLS-1$
            return;
        }

        // create IEdiorInput
        IPath location = new Path( file.getAbsolutePath() );
        ImageDescriptor imageDescriptor = descriptor.getImageDescriptor();
        IPathEditorInput input = new PathEditorInput( location, imageDescriptor );

        // activate editor
        IWorkbenchPage page = workbenchWindow.getActivePage();
        String editorId = descriptor.getId();
        try
        {
            page.openEditor( input, editorId );
        }
        catch ( PartInitException e )
        {
            e.printStackTrace();
        }
    }

}