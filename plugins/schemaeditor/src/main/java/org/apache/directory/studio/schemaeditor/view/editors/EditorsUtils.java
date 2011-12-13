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
/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.apache.directory.studio.schemaeditor.view.editors;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
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
        IEditorPart[] dirtyEditors = getDirtyEditors();

        if ( dirtyEditors.length == 0 )
        {
            return true;
        }

        if ( !askSaveAllDirtyEditors( dirtyEditors ) )
        {
            return false;
        }

        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench().saveAllEditors( false );
    }


    /**
     * Asks the user if all the dirty editors must be saved.
     *
     * @param dirtyEditors the dirty editors
     * @return <code>true</code> if the dirty editors must be saved,
     *         <code>false</code> if not.
     */
    private static boolean askSaveAllDirtyEditors( IEditorPart[] dirtyEditors )
    {
        ListDialog dialog = new ListDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        dialog.setTitle( "Save All Modified Resources" );
        dialog.setAddCancelButton( true );
        dialog.setLabelProvider( createDialogLabelProvider() );
        dialog.setMessage( "All modified resources must be saved before this operation." );
        dialog.setContentProvider( new ArrayContentProvider() );
        dialog.setInput( dirtyEditors );
        return dialog.open() == Window.OK;
    }


    /**
     * Create the dialog label provider.
     *
     * @return the dialog label provider
     */
    private static ILabelProvider createDialogLabelProvider()
    {
        return new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return ( ( IEditorPart ) element ).getTitleImage();
            }


            public String getText( Object element )
            {
                return ( ( IEditorPart ) element ).getTitle();
            }
        };
    }


    /**
     * Returns an array of all editors that have an unsaved content. If the identical content is 
     * presented in more than one editor, only one of those editor parts is part of the result.
     * 
     * @return an array of all dirty editor parts.
     */
    public static IEditorPart[] getDirtyEditors()
    {
        Set<IEditorInput> inputs = new HashSet<IEditorInput>();
        List<IEditorPart> result = new ArrayList<IEditorPart>( 0 );
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
        for ( int i = 0; i < windows.length; i++ )
        {
            IWorkbenchPage[] pages = windows[i].getPages();
            for ( int x = 0; x < pages.length; x++ )
            {
                IEditorPart[] editors = pages[x].getDirtyEditors();
                for ( int z = 0; z < editors.length; z++ )
                {
                    IEditorPart ep = editors[z];
                    IEditorInput input = ep.getEditorInput();
                    if ( !inputs.contains( input ) )
                    {
                        inputs.add( input );
                        result.add( ep );
                    }
                }
            }
        }
        return result.toArray( new IEditorPart[result.size()] );
    }
}
