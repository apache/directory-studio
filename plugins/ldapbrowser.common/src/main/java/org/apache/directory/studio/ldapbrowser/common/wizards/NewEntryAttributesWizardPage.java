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

package org.apache.directory.studio.ldapbrowser.common.wizards;


import java.util.Collection;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetActionGroup;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetActionGroupWithAttribute;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetUniversalListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.OpenDefaultEditorAction;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


/**
 * The NewEntryAttributesWizardPage is used to fill the attributes of
 * the new entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewEntryAttributesWizardPage extends WizardPage implements EntryUpdateListener
{

    /** The wizard. */
    private NewEntryWizard wizard;

    /** The configuration. */
    private EntryEditorWidgetConfiguration configuration;

    /** The action group. */
    private EntryEditorWidgetActionGroup actionGroup;

    /** The main widget. */
    private EntryEditorWidget mainWidget;

    /** The universal listener. */
    private EntryEditorWidgetUniversalListener universalListener;

    /** Token used to activate and deactivate shortcuts in the editor */
    private IContextActivation contextActivation;


    /**
     * Creates a new instance of NewEntryAttributesWizardPage.
     *
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewEntryAttributesWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        setTitle( Messages.getString( "NewEntryAttributesWizardPage.Attributes" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewEntryAttributesWizardPage.PleaseEnterAttributesForEntry" ) ); //$NON-NLS-1$
        setImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_ENTRY_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;

        IWizardContainer container = wizard.getContainer();
        if ( container instanceof WizardDialog )
        {
            WizardDialog dialog = ( WizardDialog ) container;
            dialog.addPageChangedListener( new IPageChangedListener()
            {
                public void pageChanged( PageChangedEvent event )
                {
                    if ( getControl().isVisible() )
                    {
                        for ( IAttribute attribute : NewEntryAttributesWizardPage.this.wizard.getPrototypeEntry()
                            .getAttributes() )
                        {
                            for ( IValue value : attribute.getValues() )
                            {
                                if ( value.isEmpty() )
                                {
                                    mainWidget.getViewer().setSelection( new StructuredSelection( value ), true );
                                    OpenDefaultEditorAction openDefaultEditorAction = actionGroup
                                        .getOpenDefaultEditorAction();
                                    if ( openDefaultEditorAction.isEnabled() )
                                    {
                                        openDefaultEditorAction.run();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            } );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( configuration != null )
        {
            EventRegistry.removeEntryUpdateListener( this );
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
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
        super.dispose();
    }


    /**
     * {@inheritDoc}
     *
     * This implementation initializes the must attributes of the
     * prototype entry and initializes the entry widget when this
     * page becomes visible.
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            DummyEntry newEntry = wizard.getPrototypeEntry();
            try
            {
                EventRegistry.suspendEventFiringInCurrentThread();

                // remove empty must attributes
                // necessary when navigating back, modifying object classes
                // and Dn and navigation forward again.
                Collection<AttributeType> oldMusts = SchemaUtils.getMustAttributeTypeDescriptions( newEntry );
                for ( AttributeType oldMust : oldMusts )
                {
                    IAttribute attribute = newEntry.getAttribute( oldMust.getOid() );
                    if ( attribute != null )
                    {
                        IValue[] values = attribute.getValues();
                        for ( int v = 0; v < values.length; v++ )
                        {
                            if ( values[v].isEmpty() )
                            {
                                attribute.deleteValue( values[v] );
                            }
                        }
                        if ( attribute.getValueSize() == 0 )
                        {
                            newEntry.deleteAttribute( attribute );
                        }
                    }
                }

                // add must attributes
                Collection<AttributeType> newMusts = SchemaUtils.getMustAttributeTypeDescriptions( newEntry );
                for ( AttributeType newMust : newMusts )
                {
                    if ( newEntry.getAttributeWithSubtypes( newMust.getOid() ) == null )
                    {
                        String friendlyIdentifier = SchemaUtils.getFriendlyIdentifier( newMust );
                        IAttribute att = new Attribute( newEntry, friendlyIdentifier );
                        newEntry.addAttribute( att );
                        att.addEmptyValue();
                    }
                }
            }
            finally
            {
                EventRegistry.resumeEventFiringInCurrentThread();
            }

            // set the input
            universalListener.setInput( newEntry );
            mainWidget.getViewer().refresh();
            validate();

            // set focus to the viewer
            mainWidget.getViewer().getControl().setFocus();
        }
        else
        {
            mainWidget.getViewer().setInput( "" ); //$NON-NLS-1$
            mainWidget.getViewer().refresh();
            setPageComplete( false );
        }
    }


    /**
     * Checks if the prototype entry is completed.
     */
    private void validate()
    {
        if ( wizard.getPrototypeEntry() != null )
        {
            Collection<String> messages = SchemaUtils.getEntryIncompleteMessages( wizard.getPrototypeEntry() );
            if ( messages != null && !messages.isEmpty() )
            {
                StringBuffer sb = new StringBuffer();
                for ( String message : messages )
                {
                    sb.append( message );
                    sb.append( ' ' );
                }
                setMessage( sb.toString(), WizardPage.WARNING );
            }
            else
            {
                setMessage( null );
            }

            setPageComplete( true );
        }
        else
        {
            setPageComplete( false );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // create configuration
        configuration = new EntryEditorWidgetConfiguration();

        // create main widget
        mainWidget = new EntryEditorWidget( configuration );
        mainWidget.createWidget( composite );
        mainWidget.getViewer().getTree().setFocus();

        // create actions
        actionGroup = new EntryEditorWidgetActionGroupWithAttribute( mainWidget, configuration );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.fillContextMenu( mainWidget.getContextMenuManager() );
        IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
            IContextService.class );
        contextActivation = contextService.activateContext( BrowserCommonConstants.CONTEXT_DIALOGS );
        actionGroup.activateGlobalActionHandlers();

        // create the listener
        universalListener = new EntryEditorWidgetUniversalListener( mainWidget.getViewer(), configuration, actionGroup,
            actionGroup.getOpenDefaultEditorAction() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        setControl( composite );
    }


    /**
     * {@inheritDoc}
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        if ( event.getModifiedEntry() == wizard.getPrototypeEntry() && !isDisposed() && getControl().isVisible() )
        {
            validate();
        }
    }


    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    private boolean isDisposed()
    {
        return configuration == null;
    }

}
