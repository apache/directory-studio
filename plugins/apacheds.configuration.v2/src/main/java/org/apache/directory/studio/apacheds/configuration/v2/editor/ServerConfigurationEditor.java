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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;


/**
 * This class implements the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationEditor extends FormEditor
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.class.getName();


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        setPartName( input.getName() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages()
    {
        try
        {
            addPage( new OverviewPage( this ) );
            addPage( new LdapServerPage( this ) );
            addPage( new KerberosServerPage( this ) );
            addPage( new AuthenticationPage( this ) );
            addPage( new PartitionsPage( this ) );
            addPage( new ReplicationPage( this ) );
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
    {
    }


    //    /**
    //     * Performs the "Save as..." action.
    //     *
    //     * @param monitor
    //     *      the monitor to use
    //     * @throws Exception
    //     */
    //    private boolean doSaveAs( IProgressMonitor monitor ) throws Exception
    //    {
    //        // detect IDE or RCP:
    //        // check if perspective org.eclipse.ui.resourcePerspective is available
    //        boolean isIDE = ApacheDSConfigurationPluginUtils.isIDEEnvironment();
    //
    //        if ( isIDE )
    //        {
    //            // Asking the user for the location where to 'save as' the file
    //            SaveAsDialog dialog = new SaveAsDialog( getSite().getShell() );
    //            if ( !( getEditorInput() instanceof NonExistingServerConfigurationInput ) )
    //            {
    //                dialog.setOriginalFile( ResourcesPlugin.getWorkspace().getRoot().getFile(
    //                    new Path( getEditorInput().getToolTipText() ) ) );
    //            }
    //            if ( dialog.open() != Dialog.OK )
    //            {
    //                return false;
    //            }
    //
    //            // Getting if the resulting file
    //            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( dialog.getResult() );
    //
    //            // Creating the file if it does not exist
    //            if ( !file.exists() )
    //            {
    //                file.create( new ByteArrayInputStream( "".getBytes() ), true, null ); //$NON-NLS-1$
    //            }
    //
    //            // Creating the new input for the editor
    //            FileEditorInput fei = new FileEditorInput( file );
    //
    //            // Saving the file to disk
    //            saveEditorPages();
    //            saveConfiguration( fei, monitor );
    //
    //            // Setting the new input to the editor
    //            setInput( fei );
    //        }
    //        else
    //        {
    //            Shell shell = getSite().getShell();
    //            boolean canOverwrite = false;
    //            String path = null;
    //
    //            while ( !canOverwrite )
    //            {
    //                // Open FileDialog
    //                FileDialog dialog = new FileDialog( shell, SWT.SAVE );
    //                path = dialog.open();
    //                if ( path == null )
    //                {
    //                    return false;
    //                }
    //
    //                // Check whether file exists and if so, confirm overwrite
    //                final File externalFile = new File( path );
    //                if ( externalFile.exists() )
    //                {
    //                    String question = NLS.bind( Messages
    //                        .getString( "ServerConfigurationEditor.TheFileAlreadyExistsReplace" ), path ); //$NON-NLS-1$
    //                    MessageDialog overwriteDialog = new MessageDialog( shell, Messages
    //                        .getString( "ServerConfigurationEditor.Question" ), null, question, //$NON-NLS-1$
    //                        MessageDialog.QUESTION, new String[]
    //                            { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0 );
    //                    int overwrite = overwriteDialog.open();
    //                    switch ( overwrite )
    //                    {
    //                        case 0: // Yes
    //                            canOverwrite = true;
    //                            break;
    //                        case 1: // No
    //                            break;
    //                        case 2: // Cancel
    //                        default:
    //                            return false;
    //                    }
    //                }
    //                else
    //                {
    //                    canOverwrite = true;
    //                }
    //            }
    //
    //            // Saving the file to disk
    //            saveEditorPages();
    //            saveConfiguration( path );
    //
    //            // Creating the new input for the editor
    //            PathEditorInput newInput = new PathEditorInput( new Path( path ) );
    //
    //            // Setting the new input to the editor
    //            setInput( newInput );
    //        }
    //
    //        // Updating the title and tooltip texts
    //        setPartName( getEditorInput().getName() );
    //
    //        return true;
    //    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }

}

/**
 * This IEditorInput is used to open files that are located in the local file system.
 * 
 * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
