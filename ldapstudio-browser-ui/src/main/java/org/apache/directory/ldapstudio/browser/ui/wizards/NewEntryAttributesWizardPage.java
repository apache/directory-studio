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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.EntryEditorWidgetActionGroup;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.EntryEditorWidgetActionGroupWithAttribute;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.EntryEditorWidgetUniversalListener;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.OpenDefaultEditorAction;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * The NewEntryAttributesWizardPage is used to fill the attributes of
 * the new entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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


    /**
     * Creates a new instance of NewEntryAttributesWizardPage.
     *
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewEntryAttributesWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        setTitle( "Attributes" );
        setDescription( "Please enter the attributes for the entry. Enter at least the MUST attributes." );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;
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
        }
        super.dispose();
    }


    /**
     * {@inheritDoc}
     *
     * This implementation initializes the must attributes of the
     * protoype entry and initializes the entry widget when this
     * page becomes visible.
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            DummyEntry newEntry = wizard.getPrototypeEntry();
            IValue editValue = null;

            try
            {
                EventRegistry.suspendEventFireingInCurrentThread();

                // remove empty must attributes
                // necessary when navigating back, modifying object classes
                // and DN and navigation forward again.
                String[] oldMust = newEntry.getSubschema().getMustAttributeNames();
                for ( int i = 0; i < oldMust.length; i++ )
                {
                    IAttribute attribute = newEntry.getAttribute( oldMust[i] );
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
                String[] newMust = newEntry.getSubschema().getMustAttributeNames();
                for ( int i = 0; i < newMust.length; i++ )
                {
                    if ( newEntry.getAttribute( newMust[i] ) == null )
                    {
                        IAttribute att = new Attribute( newEntry, newMust[i] );
                        newEntry.addAttribute( att );
                        att.addEmptyValue();

                        if ( editValue == null )
                        {
                            editValue = att.getValues()[0];
                        }
                    }
                }
            }
            catch ( ModelModificationException e )
            {
                e.printStackTrace();
            }
            finally
            {
                EventRegistry.resumeEventFireingInCurrentThread();
            }

            // set the input
            mainWidget.getViewer().setInput( newEntry );
            mainWidget.getViewer().refresh();
            validate();

            // set focus to the viewer
            mainWidget.getViewer().getControl().setFocus();

            // start editing if there is an empty value
            if ( editValue != null )
            {
                mainWidget.getViewer().setSelection( new StructuredSelection( editValue ), true );
                OpenDefaultEditorAction openDefaultEditorAction = actionGroup.getOpenDefaultEditorAction();
                if ( openDefaultEditorAction.isEnabled() )
                {
                    openDefaultEditorAction.run();
                }
            }
        }
        else
        {
            mainWidget.getViewer().setInput( "" );
            mainWidget.getViewer().refresh();
            setPageComplete( false );
        }
    }


    /**
     * Checks if the prototype entry is completed.
     */
    private void validate()
    {
        if ( wizard.getPrototypeEntry() != null && wizard.getPrototypeEntry().isConsistent() )
        {
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
        mainWidget = new EntryEditorWidget( this.configuration );
        mainWidget.createWidget( composite );
        mainWidget.getViewer().getTree().setFocus();

        // create actions
        actionGroup = new EntryEditorWidgetActionGroupWithAttribute( mainWidget, configuration );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.fillContextMenu( mainWidget.getContextMenuManager() );

        // create the listener
        universalListener = new EntryEditorWidgetUniversalListener( mainWidget.getViewer(), actionGroup
            .getOpenDefaultEditorAction() );
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
