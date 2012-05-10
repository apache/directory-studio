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
package org.apache.directory.studio.schemaeditor.view.editors;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;


/**
 * This class implements a utility class for the editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorsUtils
{
    /**
     * Saves all the dirty editors (asking the user if they need to be saved or not).
     *
     * @return <code>true</code> if all the dirty editors were saved successfully,
     *          <code>false</code> if not (if the user decided to not save them, or 
     *          if an error occurred when saving).
     */
    public static boolean saveAllDirtyEditors()
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        List<IEditorPart> dirtyEditorsList = getDirtyEditorsList( workbench );

        if ( dirtyEditorsList.size() > 0 )
        {
            // Creating the dialog to ask the user if the dirty editors must be saved
            ListDialog dialog = new ListDialog( workbenchWindow.getShell() );
            dialog.setTitle( Messages.getString( "EditorsUtils.SaveDialogTitle" ) ); //$NON-NLS-1$
            dialog.setMessage( Messages.getString( "EditorsUtils.SaveDialogMessage" ) ); //$NON-NLS-1$
            dialog.setLabelProvider( new LabelProvider()
            {
                public Image getImage( Object element )
                {
                    return ( ( IEditorPart ) element ).getTitleImage();
                }


                public String getText( Object element )
                {
                    IEditorPart editorPart = ( IEditorPart ) element;

                    StringBuilder sb = new StringBuilder();
                    sb.append( editorPart.getTitle() );

                    String tooltip = editorPart.getTitleToolTip();

                    if ( ( tooltip != null ) && ( !"".equals( tooltip ) ) )
                    {
                        sb.append( " [" );
                        sb.append( tooltip );
                        sb.append( "]" );
                    }

                    return sb.toString();
                }
            } );
            dialog.setContentProvider( new ArrayContentProvider() );
            dialog.setHelpAvailable( false );
            dialog.setAddCancelButton( true );
            dialog.setInput( dirtyEditorsList );

            // Opening the dialog
            if ( dialog.open() != Dialog.OK )
            {
                // Cancel
                return false;
            }

            // Forcing the save of all dirty editors
            return workbenchWindow.getWorkbench().saveAllEditors( false );
        }

        return true;
    }


    /**
     * Returns a list of all editors having unsaved content.
     * 
     * @return a list of all dirty editors.
     */
    public static List<IEditorPart> getDirtyEditorsList( IWorkbench workbench )
    {
        List<IEditorPart> dirtyEditorsList = new ArrayList<IEditorPart>();

        if ( workbench != null )
        {
            List<IEditorInput> processedInputs = new ArrayList<IEditorInput>();

            for ( IWorkbenchWindow workbenchWindow : workbench.getWorkbenchWindows() )
            {
                for ( IWorkbenchPage workbenchPage : workbenchWindow.getPages() )
                {
                    for ( IEditorPart editor : workbenchPage.getDirtyEditors() )
                    {
                        IEditorInput input = editor.getEditorInput();

                        if ( !processedInputs.contains( input ) )
                        {
                            processedInputs.add( input );
                            dirtyEditorsList.add( editor );
                        }
                    }
                }
            }
        }

        return dirtyEditorsList;
    }
}
