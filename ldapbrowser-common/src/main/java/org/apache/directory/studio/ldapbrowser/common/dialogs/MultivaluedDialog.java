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

import org.apache.directory.studio.connection.ui.RunnableContextRunner;
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
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.jobs.UpdateEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
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
    private static final String DIALOG_TITLE = Messages.getString( "MultivaluedDialog.MultivaluedEditor" ); //$NON-NLS-1$

    /** The attribute hierarchy to edit. */
    private AttributeHierarchy attributeHierarchy;

    /** The entry editor widget configuration. */
    private MultiValuedEntryEditorConfiguration configuration;

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
     * @param attributeHierarchy the attribute hierarchy
     */
    public MultivaluedDialog( Shell parentShell, AttributeHierarchy attributeHierarchy )
    {
        super( parentShell );
        setShellStyle( getShellStyle() | SWT.RESIZE );

        // clone the entry and attribute hierarchy
        IEntry entry = attributeHierarchy.getEntry();
        String attributeDescription = attributeHierarchy.getAttributeDescription();
        IEntry clone = new CompoundModification().cloneEntry( entry );
        this.attributeHierarchy = clone.getAttributeWithSubtypes( attributeDescription );
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


    @Override
    protected void okPressed()
    {
        IEntry modifiedEntry = attributeHierarchy.getEntry();
        IEntry originalEntry = modifiedEntry.getBrowserConnection().getEntryFromCache( modifiedEntry.getDn() );
        LdifFile diff = Utils.computeDiff( originalEntry, modifiedEntry );
        if ( diff != null )
        {
            // save
            UpdateEntryRunnable runnable = new UpdateEntryRunnable( originalEntry, diff
                .toFormattedString( LdifFormatParameters.DEFAULT ) );
            IStatus status = RunnableContextRunner.execute( runnable, null, true );
            if ( status.isOK() )
            {
                super.okPressed();
            }
        }
        else
        {
            super.okPressed();
        }
    }


    /**
     * @see org.eclipse.jface.window.Window#open()
     */
    public int open()
    {
        if ( attributeHierarchy.getAttribute().getValueSize() == 0 )
        {
            attributeHierarchy.getAttribute().addEmptyValue();
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
            for ( Iterator<IAttribute> it = attributeHierarchy.iterator(); it.hasNext(); )
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
        configuration = new MultiValuedEntryEditorConfiguration();

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
        universalListener.setInput( attributeHierarchy );

        // start edit mode if an empty value exists
        for ( Iterator<IAttribute> it = attributeHierarchy.iterator(); it.hasNext(); )
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

    /**
     * A special configuration for the {@link MultivaluedDialog}.
     */
    class MultiValuedEntryEditorConfiguration extends EntryEditorWidgetConfiguration
    {
        @Override
        public ValueEditorManager getValueEditorManager( TreeViewer viewer )
        {
            if ( valueEditorManager == null )
            {
                valueEditorManager = new ValueEditorManager( viewer.getTree(), false, false );
            }

            return valueEditorManager;
        }
    }
}
