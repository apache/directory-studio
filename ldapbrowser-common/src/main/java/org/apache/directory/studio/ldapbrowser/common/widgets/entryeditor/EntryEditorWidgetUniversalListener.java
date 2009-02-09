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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;


/**
 * The EntryEditorWidgetUniversalListener manages all events for the entry editor widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetUniversalListener implements EntryUpdateListener
{

    /** The tree viewer */
    protected TreeViewer viewer;

    /** The configuration. */
    protected EntryEditorWidgetConfiguration configuration;

    /** The action group. */
    protected EntryEditorWidgetActionGroup actionGroup;

    /** The action used to start the default value editor */
    protected OpenDefaultEditorAction startEditAction;

    /** This listener starts the value editor when pressing enter */
    protected SelectionListener viewerSelectionListener = new SelectionAdapter()
    {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected( SelectionEvent e )
        {
        }


        /**
         * {@inheritDoc}
         *
         * This implementation starts the value editor.
         */
        public void widgetDefaultSelected( SelectionEvent e )
        {
            if ( startEditAction.isEnabled() )
            {
                startEditAction.run();
            }
        }
    };

    /** This listener starts the value editor or expands/collapses the selected attribute */
    protected MouseListener viewerMouseListener = new MouseAdapter()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation starts the value editor or expands/collapses the selected attribute.
         */
        public void mouseDoubleClick( MouseEvent e )
        {
            IAttribute[] attributes = BrowserSelectionUtils.getAttributes( viewer.getSelection() );
            IValue[] values = BrowserSelectionUtils.getValues( viewer.getSelection() );
            if ( attributes.length == 1 && values.length == 0 )
            {
                if ( viewer.getExpandedState( attributes[0] ) )
                {
                    viewer.collapseToLevel( attributes[0], 1 );
                }
                else
                {
                    viewer.expandToLevel( attributes[0], 1 );
                }
            }
        }


        /**
         * {@inheritDoc}
         */
        public void mouseDown( MouseEvent e )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void mouseUp( MouseEvent e )
        {
        }
    };


    /**
     * Creates a new instance of EntryEditorWidgetUniversalListener.
     * 
     * @param treeViewer the tree viewer
     * @param configuration the configuration
     * @param actionGroup the action group
     * @param startEditAction the action used to start the default value editor
     */
    public EntryEditorWidgetUniversalListener( TreeViewer treeViewer, EntryEditorWidgetConfiguration configuration,
        EntryEditorWidgetActionGroup actionGroup, OpenDefaultEditorAction startEditAction )
    {
        this.startEditAction = startEditAction;
        this.viewer = treeViewer;
        this.configuration = configuration;
        this.actionGroup = actionGroup;

        // register listeners
        viewer.getTree().addSelectionListener( viewerSelectionListener );
        viewer.getTree().addMouseListener( viewerMouseListener );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        // Don't invoke Finish' or 'OK' button when pressing 'Enter' in wizard or dialog
        viewer.getTree().addTraverseListener( new TraverseListener()
        {
            public void keyTraversed( TraverseEvent e )
            {
                if ( e.detail == SWT.TRAVERSE_RETURN )
                {
                    e.doit = false;
                }
            }
        } );
    }


    /**
     * Disposes this universal listener.
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            EventRegistry.removeEntryUpdateListener( this );

            startEditAction = null;
            viewer = null;
            configuration = null;
            actionGroup = null;
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the viewer and selects a value depending
     * on the event.
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        if ( viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed() || viewer.getInput() == null
            || ( event.getModifiedEntry() != viewer.getInput() && !( event instanceof BulkModificationEvent ) ) )
        {
            return;
        }

        // force closing of cell editors
        if ( viewer.isCellEditorActive() )
        {
            viewer.cancelEditing();
        }

        // refresh
        viewer.refresh();

        // selection value
        if ( event instanceof ValueAddedEvent )
        {
            // select the vadded value
            ValueAddedEvent vaEvent = ( ValueAddedEvent ) event;
            viewer.setSelection( new StructuredSelection( vaEvent.getAddedValue() ), true );
            viewer.refresh();
        }
        else if ( event instanceof ValueDeletedEvent )
        {
            // select another value of the deleted attribute
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

            // show operational attributes if an operational attribute was added
            if ( evaEvent.getAddedValue().getAttribute().isOperationalAttribute()
                && !BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
                    BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES ) )
            {
                BrowserCommonActivator.getDefault().getPreferenceStore().setValue(
                    BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES, true );
            }

            // select the added value and start editing
            viewer.setSelection( new StructuredSelection( evaEvent.getAddedValue() ), true );
            if ( startEditAction.isEnabled() )
            {
                startEditAction.run();
            }
        }
        else if ( event instanceof EmptyValueDeletedEvent )
        {
            // select another value of the deleted attribute
            EmptyValueDeletedEvent evdEvent = ( EmptyValueDeletedEvent ) event;
            if ( viewer.getSelection().isEmpty() && evdEvent.getDeletedValue().getAttribute().getValueSize() > 0 )
            {
                viewer.setSelection(
                    new StructuredSelection( evdEvent.getDeletedValue().getAttribute().getValues()[0] ), true );
            }
        }
        else if ( event instanceof ValueModifiedEvent )
        {
            // select the modified value
            ValueModifiedEvent vmEvent = ( ValueModifiedEvent ) event;
            viewer.setSelection( new StructuredSelection( vmEvent.getNewValue() ), true );
        }
        else if ( event instanceof ValueRenamedEvent )
        {
            // select the renamed value
            ValueRenamedEvent vrEvent = ( ValueRenamedEvent ) event;
            viewer.setSelection( new StructuredSelection( vrEvent.getNewValue() ), true );
        }
    }


    /**
     * Sets the input to the viewer.
     *
     * @param entry the entry input
     */
    public void setInput( IEntry entry )
    {
        if ( entry != viewer.getInput() )
        {
            viewer.setInput( entry );
            actionGroup.setInput( entry );
            expandFoldedAttributes();
        }

    }


    /**
     * Sets the input to the viewer.
     * 
     * @param attributeHierarchy the attribute hierarchy
     */
    public void setInput( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy != viewer.getInput() )
        {
            viewer.setInput( attributeHierarchy );
            actionGroup.setInput( attributeHierarchy );
            expandFoldedAttributes();
        }

    }


    /**
     * Expands folded attributes if the appropriate preference is set.
     */
    protected void expandFoldedAttributes()
    {
        if ( configuration.getPreferences().isAutoExpandFoldedAttributes() )
        {
            viewer.expandAll();
        }
    }

}
