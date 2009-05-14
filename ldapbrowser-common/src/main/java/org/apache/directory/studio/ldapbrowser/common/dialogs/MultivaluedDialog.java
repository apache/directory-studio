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
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


/**
 * Dialog to view and edit multi-valued attributes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MultivaluedDialog extends Dialog
{

    /** The dialog title. */
    private static final String DIALOG_TITLE = Messages.getString("MultivaluedDialog.MultivaluedEditor"); //$NON-NLS-1$

    /** The attribute hierarchie to edit. */
    private AttributeHierarchy attributeHierarchie;

    /** The entry editor widget configuration. */
    private EntryEditorWidgetConfiguration configuration;

    /** The entry edtior widget action group. */
    private EntryEditorWidgetActionGroup actionGroup;

    /** The entry editor widget. */
    private EntryEditorWidget mainWidget;

    /** The universal listener. */
    private MultiValuedEntryEditorUniversalListener universalListener;

    /** Token used to activate and deactivate shortcuts in the editor */
    private IContextActivation contextActivation;


    /**
     * Creates a new instance of MultivaluedDialog.
     * 
     * @param parentShell the parent shell
     * @param attributeHierarchie the attribute hierarchie
     */
    public MultivaluedDialog( Shell parentShell, AttributeHierarchy attributeHierarchie )
    {
        super( parentShell );
        setShellStyle( getShellStyle() | SWT.RESIZE );
        this.attributeHierarchie = attributeHierarchie;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_MULTIVALUEDEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false );
        getShell().update();
        getShell().layout( true, true );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
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


    /**
     * @see org.eclipse.jface.window.Window#open()
     */
    public int open()
    {
        if ( attributeHierarchie.getAttribute().getValueSize() == 0 )
        {
            attributeHierarchie.getAttribute().addEmptyValue();
        }

        return super.open();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        boolean returnValue = super.close();
        if ( returnValue )
        {
            dispose();

            // cleanup attribute hierarchy after editing
            for ( Iterator<IAttribute> it = attributeHierarchie.iterator(); it.hasNext(); )
            {
                IAttribute attribute = it.next();
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

                    // delete attribute from entry if all values were deleted
                    if ( attribute.getValueSize() == 0 )
                    {
                        attribute.getEntry().deleteAttribute( attribute );
                    }
                }
            }
        }
        return returnValue;
    }


    /**
     * Disposes all widgets.
     */
    public void dispose()
    {
        if ( configuration != null )
        {
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
            actionGroup.deactivateGlobalActionHandlers();
            actionGroup.dispose();
            actionGroup = null;
            configuration.dispose();
            configuration = null;

            if ( contextActivation != null )
            {
                IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                    IContextService.class );
                contextService.deactivateContext( contextActivation );
                contextActivation = null;
            }
        }
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        // create configuration
        configuration = new EntryEditorWidgetConfiguration();

        // create main widget
        mainWidget = new EntryEditorWidget( configuration );
        mainWidget.createWidget( composite );
        mainWidget.getViewer().getTree().setFocus();

        // create actions
        actionGroup = new EntryEditorWidgetActionGroup( mainWidget, configuration );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.fillContextMenu( mainWidget.getContextMenuManager() );
        IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
            IContextService.class );
        contextActivation = contextService.activateContext( BrowserCommonConstants.CONTEXT_DIALOGS );
        actionGroup.activateGlobalActionHandlers();

        // create the listener
        universalListener = new MultiValuedEntryEditorUniversalListener( mainWidget.getViewer(), configuration,
            actionGroup, actionGroup.getOpenDefaultEditorAction() );
        universalListener.setInput( attributeHierarchie );

        // start edit mode if an empty value exists
        for ( Iterator<IAttribute> it = attributeHierarchie.iterator(); it.hasNext(); )
        {
            IAttribute attribute = it.next();
            IValue[] values = attribute.getValues();
            for ( int i = 0; i < values.length; i++ )
            {
                IValue value = values[i];
                if ( value.isEmpty() )
                {
                    mainWidget.getViewer().setSelection( new StructuredSelection( value ), true );
                    if ( actionGroup.getOpenDefaultEditorAction().isEnabled() )
                    {
                        actionGroup.getOpenDefaultEditorAction().run();
                        break;
                    }
                }
            }
        }

        applyDialogFont( composite );
        return composite;
    }

    /**
     * A special listener for the {@link MultivaluedDialog}.
     */
    class MultiValuedEntryEditorUniversalListener extends EntryEditorWidgetUniversalListener
    {

        /**
         * Creates a new instance of MultiValuedEntryEditorUniversalListener.
         * 
         * @param treeViewer the tree viewer
         * @param configuration the configuration
         * @param actionGroup the action group
         * @param startEditAction the start edit action
         */
        public MultiValuedEntryEditorUniversalListener( TreeViewer treeViewer,
            EntryEditorWidgetConfiguration configuration, EntryEditorWidgetActionGroup actionGroup,
            OpenDefaultEditorAction startEditAction )
        {
            super( treeViewer, configuration, actionGroup, startEditAction );
        }


        /**
         * @see org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetUniversalListener#entryUpdated(org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent)
         */
        public void entryUpdated( EntryModificationEvent event )
        {
            if ( viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed() )
            {
                return;
            }

            if ( viewer.isCellEditorActive() )
            {
                viewer.cancelEditing();
            }

            // set new input because attributes are newly created after a
            // modification
            IEntry entry = attributeHierarchie.getEntry();
            String attributeDescription = attributeHierarchie.getAttributeDescription();
            attributeHierarchie = entry.getAttributeWithSubtypes( attributeDescription );
            if ( attributeHierarchie == null )
            {
                EventRegistry.suspendEventFiringInCurrentThread();
                IAttribute attribute = new Attribute( entry, attributeDescription );
                entry.addAttribute( attribute );
                attribute.addEmptyValue();
                EventRegistry.resumeEventFiringInCurrentThread();
                attributeHierarchie = entry.getAttributeWithSubtypes( attributeDescription );
            }
            viewer.setInput( attributeHierarchie );
            viewer.refresh();

            // select added/modified value
            if ( event instanceof ValueAddedEvent )
            {
                ValueAddedEvent vaEvent = ( ValueAddedEvent ) event;
                viewer.setSelection( new StructuredSelection( vaEvent.getAddedValue() ), true );
                viewer.refresh();
            }
            else if ( event instanceof ValueModifiedEvent )
            {
                ValueModifiedEvent vmEvent = ( ValueModifiedEvent ) event;
                viewer.setSelection( new StructuredSelection( vmEvent.getNewValue() ), true );
            }
            else if ( event instanceof ValueDeletedEvent )
            {
                ValueDeletedEvent vdEvent = ( ValueDeletedEvent ) event;
                if ( vdEvent.getDeletedValue().getAttribute().getValueSize() > 0 )
                {
                    viewer.setSelection( new StructuredSelection(
                        vdEvent.getDeletedValue().getAttribute().getValues()[0] ), true );
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
