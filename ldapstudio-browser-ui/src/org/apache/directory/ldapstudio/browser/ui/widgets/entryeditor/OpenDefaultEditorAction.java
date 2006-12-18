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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.actions.RenameAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.EntryEditorActionProxy;
import org.eclipse.jface.viewers.TreeViewer;


public class OpenDefaultEditorAction extends AbstractEntryEditorListenerAction
{

    private OpenBestEditorAction valueEditorProxy;

    private EntryEditorActionProxy renameProxy;


    public OpenDefaultEditorAction( TreeViewer viewer, OpenBestEditorAction proxy )
    {
        super( viewer, "Edit Value", null, BrowserUIConstants.ACTION_ID_EDIT_VALUE );

        this.valueEditorProxy = proxy;
        this.renameProxy = null;
    }


    public void dispose()
    {
        this.valueEditorProxy = null;
        this.renameProxy = null;

        super.dispose();
    }


    public void run()
    {
        if ( this.valueEditorProxy != null && this.valueEditorProxy.isEnabled() )
        {
            this.valueEditorProxy.run();
        }
        else if ( this.renameProxy != null && this.renameProxy.isEnabled() )
        {
            this.renameProxy.run();
        }
    }


    protected void updateEnabledState()
    {

        // update proxy selections
        if ( this.valueEditorProxy != null )
        {
            if ( this.currentSelectionChangedEvent != null )
            {
                this.valueEditorProxy.selectionChanged( currentSelectionChangedEvent );
            }
            this.valueEditorProxy.updateEnabledState();
        }
        if ( this.renameProxy != null )
        {
            if ( this.currentSelectionChangedEvent != null )
            {
                this.renameProxy.selectionChanged( currentSelectionChangedEvent );
            }
            this.renameProxy.updateAction();
        }

        // update my state
        if ( this.valueEditorProxy != null && this.renameProxy != null )
        {
            this.setEnabled( this.valueEditorProxy.isEnabled() || this.renameProxy.isEnabled() );
        }
        else if ( this.renameProxy != null )
        {
            this.setEnabled( this.renameProxy.isEnabled() );
        }
        else if ( this.valueEditorProxy != null )
        {
            this.setEnabled( this.valueEditorProxy.isEnabled() );
        }
        else
        {
            this.setEnabled( false );
        }

        if ( this.valueEditorProxy != null )
        {
            this.setImageDescriptor( this.valueEditorProxy.getImageDescriptor() );
        }
        else if ( this.renameProxy != null )
        {
            this.setImageDescriptor( this.renameProxy.getImageDescriptor() );
        }

    }


    public void enableRenameEntryAction()
    {
        this.renameProxy = new EntryEditorActionProxy( valueEditorProxy.viewer, new RenameAction() );
    }

}
