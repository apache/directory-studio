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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifOutlinePage;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetUniversalListener;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


public class EntryEditorUniversalListener extends EntryEditorWidgetUniversalListener implements IPartListener2
{

    private EntryEditor entryEditor;

    private IContextActivation contextActivation;


    public EntryEditorUniversalListener( EntryEditor entryEditor )
    {
        super( entryEditor.getMainWidget().getViewer(), entryEditor.getActionGroup().getOpenDefaultEditorAction() );
        this.entryEditor = entryEditor;
        entryEditor.getSite().getPage().addPartListener( this );
    }


    public void dispose()
    {
        if ( this.entryEditor != null )
        {
            entryEditor.getSite().getPage().removePartListener( this );
            this.entryEditor = null;
        }

        super.dispose();
    }


    void setInput( IEntry entry )
    {
        if ( entry != this.viewer.getInput() )
        {
            this.viewer.setInput( entry );
            this.entryEditor.getActionGroup().setInput( entry );
        }

    }


    public void partDeactivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this.entryEditor && contextActivation != null )
        {

            this.entryEditor.getActionGroup().deactivateGlobalActionHandlers();

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextService.deactivateContext( contextActivation );
            contextActivation = null;
        }
    }


    public void partActivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this.entryEditor )
        {

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextActivation = contextService
                .activateContext( "org.apache.directory.ldapstudio.browser.action.context" );
            // org.eclipse.ui.contexts.dialogAndWindow
            // org.eclipse.ui.contexts.window
            // org.eclipse.ui.text_editor_context

            this.entryEditor.getActionGroup().activateGlobalActionHandlers();
        }
    }


    public void partBroughtToTop( IWorkbenchPartReference partRef )
    {
    }


    public void partClosed( IWorkbenchPartReference partRef )
    {
    }


    public void partOpened( IWorkbenchPartReference partRef )
    {
    }


    public void partHidden( IWorkbenchPartReference partRef )
    {
    }


    public void partVisible( IWorkbenchPartReference partRef )
    {
    }


    public void partInputChanged( IWorkbenchPartReference partRef )
    {
    }


    public void entryUpdated( EntryModificationEvent event )
    {
        super.entryUpdated( event );

        LdifOutlinePage outlinePage = ( LdifOutlinePage ) this.entryEditor.getAdapter( IContentOutlinePage.class );
        if ( outlinePage != null )
        {
            outlinePage.refresh();
        }
    }

}
