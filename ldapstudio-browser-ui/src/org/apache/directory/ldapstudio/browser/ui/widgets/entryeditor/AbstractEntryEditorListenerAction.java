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


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;


public abstract class AbstractEntryEditorListenerAction extends Action implements ISelectionChangedListener,
    EntryUpdateListener
{

    protected ISelectionProvider selectionProvider;

    protected SelectionChangedEvent currentSelectionChangedEvent;

    protected IEntry selectedEntry;

    protected IAttribute[] selectedAttributes;

    protected IValue[] selectedValues;


    public AbstractEntryEditorListenerAction( ISelectionProvider selectionProvider, String title,
        ImageDescriptor image, String command, int style )
    {
        super( title, style );
        super.setText( title );
        super.setToolTipText( title );
        super.setImageDescriptor( image );
        super.setActionDefinitionId( command );
        super.setEnabled( false );

        this.selectionProvider = selectionProvider;

        this.init();
    }


    public AbstractEntryEditorListenerAction( ISelectionProvider selectionProvider, String title,
        ImageDescriptor image, String command )
    {
        this( selectionProvider, title, image, command, Action.AS_PUSH_BUTTON );
    }


    private void init()
    {
        this.currentSelectionChangedEvent = null;
        this.selectedEntry = null;
        this.selectedValues = new IValue[0];
        this.selectedAttributes = new IAttribute[0];

        this.selectionProvider.addSelectionChangedListener( this );
        EventRegistry.addEntryUpdateListener( this );
    }


    public void selectionChanged( SelectionChangedEvent event )
    {
        this.currentSelectionChangedEvent = event;

        ISelection selection = event.getSelection();

        this.selectedAttributes = SelectionUtils.getAttributes( selection );
        this.selectedValues = SelectionUtils.getValues( selection );

        if ( this.selectedAttributes.length > 0 )
        {
            this.selectedEntry = this.selectedAttributes[0].getEntry();
        }
        else if ( this.selectedValues.length > 0 )
        {
            this.selectedEntry = this.selectedValues[0].getAttribute().getEntry();
        }
        else
        {
            this.selectedEntry = null;
        }

        this.callUpdateEnabledState();

        this.currentSelectionChangedEvent = null;
    }


    public final void entryUpdated( EntryModificationEvent event )
    {
        this.callUpdateEnabledState();
    }


    private void callUpdateEnabledState()
    {
        if ( this.selectionProvider != null )
        {
            this.updateEnabledState();
        }
    }


    protected abstract void updateEnabledState();


    public void dispose()
    {
        EventRegistry.removeEntryUpdateListener( this );
        this.selectionProvider.removeSelectionChangedListener( this );

        this.currentSelectionChangedEvent = null;
        this.selectedEntry = null;
        this.selectedValues = new IValue[0];
        this.selectedAttributes = new IAttribute[0];

        this.selectionProvider = null;
    }


    protected Set getValueSet()
    {
        Set valueSet = new HashSet();
        for ( int i = 0; i < this.selectedAttributes.length; i++ )
        {
            valueSet.addAll( Arrays.asList( this.selectedAttributes[i].getValues() ) );
        }
        valueSet.addAll( Arrays.asList( this.selectedValues ) );
        return valueSet;
    }

}
