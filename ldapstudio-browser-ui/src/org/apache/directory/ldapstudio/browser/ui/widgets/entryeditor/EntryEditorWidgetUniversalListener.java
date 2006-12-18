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


import org.apache.directory.ldapstudio.browser.core.events.EmptyValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.ValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ValueDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ValueModifiedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ValueRenamedEvent;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class EntryEditorWidgetUniversalListener implements EntryUpdateListener
{

    protected TreeViewer viewer;

    protected OpenDefaultEditorAction startEditAction;


    public EntryEditorWidgetUniversalListener( TreeViewer treeViewer, OpenDefaultEditorAction startEditAction )
    {
        this.startEditAction = startEditAction;
        this.viewer = treeViewer;

        this.initListeners();
    }


    protected void initListeners()
    {

        this.viewer.getTree().addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
                if ( startEditAction.isEnabled() )
                    startEditAction.run();
            }
        } );
        this.viewer.getTree().addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                if ( startEditAction.isEnabled() )
                    startEditAction.run();

                IAttribute[] attributes = SelectionUtils.getAttributes( viewer.getSelection() );
                IValue[] values = SelectionUtils.getValues( viewer.getSelection() );
                if ( attributes.length == 1 && values.length == 0 )
                {
                    if ( viewer.getExpandedState( attributes[0] ) )
                        viewer.collapseToLevel( attributes[0], 1 );
                    else
                        viewer.expandToLevel( attributes[0], 1 );
                }
            }


            public void mouseDown( MouseEvent e )
            {
            }


            public void mouseUp( MouseEvent e )
            {
            }
        } );

        EventRegistry.addEntryUpdateListener( this );
    }


    public void dispose()
    {
        if ( this.viewer != null )
        {
            EventRegistry.removeEntryUpdateListener( this );

            this.startEditAction = null;
            this.viewer = null;
        }
    }


    public void entryUpdated( EntryModificationEvent event )
    {

        if ( this.viewer == null || this.viewer.getTree() == null || this.viewer.getTree().isDisposed()
            || this.viewer.getInput() == null || event.getModifiedEntry() != this.viewer.getInput() )
        {
            return;
        }

        // force closing of cell editors
        if ( this.viewer.isCellEditorActive() )
        {
            this.viewer.cancelEditing();
        }

        // refresh
        this.viewer.refresh();

        // restore selection
        if ( event instanceof ValueAddedEvent )
        {
            ValueAddedEvent vaEvent = ( ValueAddedEvent ) event;
            viewer.setSelection( new StructuredSelection( vaEvent.getAddedValue() ), true );
            this.viewer.refresh();
        }
        else if ( event instanceof ValueDeletedEvent )
        {
            ValueDeletedEvent vdEvent = ( ValueDeletedEvent ) event;
            if ( viewer.getSelection().isEmpty() && vdEvent.getDeletedValue().getAttribute().getValueSize() > 0 )
            {
                viewer.setSelection(
                    new StructuredSelection( vdEvent.getDeletedValue().getAttribute().getValues()[0] ), true );
            }
        }
        else if ( event instanceof EmptyValueAddedEvent )
        {
            EmptyValueAddedEvent evaEvent = ( EmptyValueAddedEvent ) event;
            viewer.setSelection( new StructuredSelection( evaEvent.getAddedValue() ), true );
            if ( startEditAction.isEnabled() )
            {
                startEditAction.run();
            }
        }
        else if ( event instanceof EmptyValueDeletedEvent )
        {
            EmptyValueDeletedEvent evdEvent = ( EmptyValueDeletedEvent ) event;
            if ( viewer.getSelection().isEmpty() && evdEvent.getDeletedValue().getAttribute().getValueSize() > 0 )
            {
                viewer.setSelection(
                    new StructuredSelection( evdEvent.getDeletedValue().getAttribute().getValues()[0] ), true );
            }
        }
        else if ( event instanceof ValueModifiedEvent )
        {
            ValueModifiedEvent vmEvent = ( ValueModifiedEvent ) event;
            viewer.setSelection( new StructuredSelection( vmEvent.getNewValue() ), true );
        }
        else if ( event instanceof ValueRenamedEvent )
        {
            ValueRenamedEvent vrEvent = ( ValueRenamedEvent ) event;
            viewer.setSelection( new StructuredSelection( vrEvent.getNewValue() ), true );
        }
    }

}
