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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.ldifeditor.LdifEditorActivator;
import org.apache.directory.ldapstudio.ldifeditor.LdifEditorConstants;
import org.apache.directory.ldapstudio.ldifeditor.editor.LdifEditor;
import org.apache.directory.ldapstudio.ldifeditor.editor.NonExistingLdifEditorInput;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This Action launches a new empty LDIF Editor. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewLdifFileAction extends BrowserAction
{
    /**
     * Creates a new instance of NewLdifFileAction.
     *
     */
    public NewLdifFileAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IEditorInput input = new NonExistingLdifEditorInput();
        String editorId = LdifEditor.getId();
        try
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            page.openEditor( input, editorId );
        }
        catch ( PartInitException e )
        {
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "New LDIF File";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return LdifEditorActivator.getDefault().getImageDescriptor( LdifEditorConstants.IMG_LDIFEDITOR_NEW );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }
}
