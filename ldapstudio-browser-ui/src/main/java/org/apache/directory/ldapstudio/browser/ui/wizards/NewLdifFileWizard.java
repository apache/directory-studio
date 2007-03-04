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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifEditor;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.NonExistingLdifEditorInput;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;


/**
 * The NewLdifFileWizard is used to add a "New LDIF" action to the platforms
 * "New..." menu. It just opens an editor with a dummy LDIF editor input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */

public class NewLdifFileWizard extends Wizard implements INewWizard
{

    /** The window. */
    private IWorkbenchWindow window;


    /**
     * Creates a new instance of NewLdifFileWizard.
     */
    public NewLdifFileWizard()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        window = workbench.getActiveWorkbenchWindow();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        window = null;
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return NewLdifFileWizard.class.getName();
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        IEditorInput input = new NonExistingLdifEditorInput();
        String editorId = LdifEditor.getId();

        try
        {
            IWorkbenchPage page = window.getActivePage();
            page.openEditor( input, editorId );
        }
        catch ( PartInitException e )
        {
            return false;
        }
        return true;
    }

}
