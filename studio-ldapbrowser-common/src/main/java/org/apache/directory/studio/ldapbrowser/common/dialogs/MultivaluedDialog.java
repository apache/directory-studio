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

package org.apache.directory.studio.ldapbrowser.common.dialogs;


import java.util.Iterator;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetActionGroup;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetUniversalListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.OpenDefaultEditorAction;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class MultivaluedDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Multivalued Editor";

    public static final int MAX_WIDTH = 450;

    public static final int MAX_HEIGHT = 250;

    private AttributeHierarchy attributeHierarchie;

    private EntryEditorWidgetConfiguration configuration;

    private EntryEditorWidgetActionGroup actionGroup;

    private EntryEditorWidget mainWidget;

    private MultiValuedEntryEditorUniversalListener universalListener;


    public MultivaluedDialog( Shell parentShell, AttributeHierarchy attributeHierarchie )
    {
        super( parentShell );
        setShellStyle( getShellStyle() | SWT.RESIZE );
        this.attributeHierarchie = attributeHierarchie;
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_MULTIVALUEDEDITOR ) );
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false );

        getShell().update();
        getShell().layout( true, true );
    }


    protected void buttonPressed( int buttonId )
    {
        if ( IDialogConstants.CLOSE_ID == buttonId )
        {
            close();
        }
        else
        {
            super.buttonPressed( buttonId );
        }
    }


    public int open()
    {
        this.dialogOpened();
        return super.open();
    }


    public boolean close()
    {
        boolean returnValue = super.close();
        if ( returnValue )
        {
            this.dispose();
            this.dialogClosed();
        }
        return returnValue;
    }


    public void dispose()
    {
        if ( this.configuration != null )
        {
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
            this.actionGroup.deactivateGlobalActionHandlers();
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.configuration.dispose();
            this.configuration = null;
        }
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );

        // create configuration
        this.configuration = new EntryEditorWidgetConfiguration();

        // create main widget
        this.mainWidget = new EntryEditorWidget( this.configuration );
        this.mainWidget.createWidget( composite );
        this.mainWidget.getViewer().setInput( attributeHierarchie );
        this.mainWidget.getViewer().getTree().setFocus();

        // create actions
        this.actionGroup = new EntryEditorWidgetActionGroup( this.mainWidget, this.configuration );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.fillContextMenu( this.mainWidget.getContextMenuManager() );
        this.actionGroup.activateGlobalActionHandlers();

        // create the listener
        this.universalListener = new MultiValuedEntryEditorUniversalListener( this.mainWidget.getViewer(),
            this.actionGroup.getOpenDefaultEditorAction() );

        // start edit mode if an empty value exists
        for ( Iterator it = attributeHierarchie.iterator(); it.hasNext(); )
        {
            IAttribute attribute = ( IAttribute ) it.next();
            IValue[] values = attribute.getValues();
            for ( int i = 0; i < values.length; i++ )
            {
                IValue value = values[i];
                if ( value.isEmpty() )
                {
                    this.mainWidget.getViewer().setSelection( new StructuredSelection( value ), true );
                    if ( this.actionGroup.getOpenDefaultEditorAction().isEnabled() )
                    {
                        this.actionGroup.getOpenDefaultEditorAction().run();
                        break;
                    }
                }
            }
        }

        applyDialogFont( composite );
        return composite;
    }


    private void dialogOpened()
    {
        if ( this.attributeHierarchie.getAttribute().getValueSize() == 0 )
        {
            this.attributeHierarchie.getAttribute().addEmptyValue();
        }
    }


    private void dialogClosed()
    {

        for ( Iterator it = attributeHierarchie.iterator(); it.hasNext(); )
        {
            IAttribute attribute = ( IAttribute ) it.next();
            if ( attribute != null )
            {

                // remove empty values
                IValue[] values = attribute.getValues();
                for ( int i = 0; i < values.length; i++ )
                {
                    if ( values[i].isEmpty() )
                    {
                        attribute.deleteEmptyValue();
                    }
                }

                // are all values deleted?
                if ( attribute.getValueSize() == 0 )
                {
                    try
                    {
                        attribute.getEntry().deleteAttribute( attribute );
                    }
                    catch ( ModelModificationException e )
                    {
                    }
                    // new DeleteAttributeValueCommand(attribute).execute();
                }
            }
        }

    }

    class MultiValuedEntryEditorUniversalListener extends EntryEditorWidgetUniversalListener
    {

        public MultiValuedEntryEditorUniversalListener( TreeViewer treeViewer, OpenDefaultEditorAction startEditAction )
        {
            super( treeViewer, startEditAction );
        }


        public void entryUpdated( EntryModificationEvent event )
        {

            if ( this.viewer == null || this.viewer.getTree() == null || this.viewer.getTree().isDisposed() )
            {
                return;
            }

            if ( this.viewer.isCellEditorActive() )
                this.viewer.cancelEditing();

            // set new input because attributes are newly created after a
            // modification
            IEntry entry = attributeHierarchie.getEntry();
            String attributeDescription = attributeHierarchie.getAttributeDescription();
            attributeHierarchie = entry.getAttributeWithSubtypes( attributeDescription );
            if ( attributeHierarchie == null )
            {
                EventRegistry.suspendEventFireingInCurrentThread();
                try
                {
                    IAttribute attribute = new Attribute( entry, attributeDescription );
                    entry.addAttribute( attribute );
                    attribute.addEmptyValue();
                }
                catch ( ModelModificationException e )
                {
                }
                EventRegistry.resumeEventFireingInCurrentThread();
                attributeHierarchie = entry.getAttributeWithSubtypes( attributeDescription );
            }
            this.viewer.setInput( attributeHierarchie );
            this.viewer.refresh();

            // select added/modified value
            if ( event instanceof ValueAddedEvent )
            {
                ValueAddedEvent vaEvent = ( ValueAddedEvent ) event;
                this.viewer.setSelection( new StructuredSelection( vaEvent.getAddedValue() ), true );
                this.viewer.refresh();
            }
            else if ( event instanceof ValueModifiedEvent )
            {
                ValueModifiedEvent vmEvent = ( ValueModifiedEvent ) event;
                this.viewer.setSelection( new StructuredSelection( vmEvent.getNewValue() ), true );
            }
            else if ( event instanceof ValueDeletedEvent )
            {
                ValueDeletedEvent vdEvent = ( ValueDeletedEvent ) event;
                if ( vdEvent.getDeletedValue().getAttribute().getValueSize() > 0 )
                {
                    this.viewer.setSelection( new StructuredSelection( vdEvent.getDeletedValue().getAttribute()
                        .getValues()[0] ), true );
                }
            }
            else if ( event instanceof EmptyValueAddedEvent )
            {
                viewer.refresh();
                EmptyValueAddedEvent evaEvent = ( EmptyValueAddedEvent ) event;
                viewer.setSelection( new StructuredSelection( evaEvent.getAddedValue() ), true );
                if ( startEditAction.isEnabled() )
                    startEditAction.run();
            }
            else if ( event instanceof EmptyValueDeletedEvent )
            {
                EmptyValueDeletedEvent evdEvent = ( EmptyValueDeletedEvent ) event;
                if ( viewer.getSelection().isEmpty() && evdEvent.getDeletedValue().getAttribute().getValueSize() > 0 )
                    viewer.setSelection( new StructuredSelection(
                        evdEvent.getDeletedValue().getAttribute().getValues()[0] ), true );
            }
            else if ( event instanceof AttributeDeletedEvent )
            {

            }
        }
    }

}
