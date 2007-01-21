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
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetActionGroup;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetUniversalListener;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class NewEntryAttributesWizardPage extends WizardPage implements EntryUpdateListener
{

    private NewEntryWizard wizard;

    private EntryEditorWidgetConfiguration configuration;

    private EntryEditorWidgetActionGroup actionGroup;

    private EntryEditorWidget mainWidget;

    private EntryEditorWidgetUniversalListener universalListener;


    public NewEntryAttributesWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        super.setTitle( "Attributes" );
        super.setDescription( "Please enter the attributes for the entry. Enter at least the MUST attributes." );
        super
            .setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
        super.setPageComplete( false );
        this.wizard = wizard;
    }


    public void dispose()
    {
        if ( this.configuration != null )
        {
            EventRegistry.removeEntryUpdateListener( this );
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.configuration.dispose();
            this.configuration = null;
        }
        super.dispose();
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {

            DummyEntry newEntry = wizard.getNewEntry();

            try
            {
                EventRegistry.suspendEventFireingInCurrentThread();

                // remove empty must attributes
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

            this.mainWidget.getViewer().setInput( newEntry );
            this.mainWidget.getViewer().refresh();
            validate();
        }
        else
        {
            this.mainWidget.getViewer().setInput( "" );
            this.mainWidget.getViewer().refresh();
            setPageComplete( false );
        }
    }


    private void validate()
    {
        if ( this.wizard.getNewEntry() != null && this.wizard.getNewEntry().isConsistent() )
        {
            super.setPageComplete( true );
        }
        else
        {
            super.setPageComplete( false );
        }
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // create configuration
        this.configuration = new EntryEditorWidgetConfiguration();

        // create main widget
        this.mainWidget = new EntryEditorWidget( this.configuration );
        this.mainWidget.createWidget( composite );
        this.mainWidget.getViewer().getTree().setFocus();

        // create actions
        this.actionGroup = new NewEntryAttributesWizardPageActionGroup( this.mainWidget, this.configuration );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.fillContextMenu( this.mainWidget.getContextMenuManager() );

        // create the listener
        this.universalListener = new EntryEditorWidgetUniversalListener( this.mainWidget.getViewer(), this.actionGroup
            .getOpenDefaultEditorAction() );
        EventRegistry.addEntryUpdateListener( this );

        setControl( composite );
    }


    public void entryUpdated( EntryModificationEvent event )
    {
        if ( event.getModifiedEntry() == this.wizard.getNewEntry() && !this.isDisposed() && getControl().isVisible() )
        {
            this.validate();
        }
    }


    public boolean isDisposed()
    {
        return this.configuration == null;
    }

}
