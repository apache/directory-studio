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


import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.jobs.InitializeAttributesJob;
import org.apache.directory.ldapstudio.browser.core.jobs.ReadEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.apache.directory.ldapstudio.browser.core.utils.ModelConverter;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.EntryWidget;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class NewEntryTypeWizardPage extends WizardPage implements WidgetModifyListener, SelectionListener
{

    public static final String PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY = NewEntryTypeWizardPage.class
        .getName()
        + ".preferredEntryCreationMethod";

    private NewEntryWizard wizard;

    private Button schemaButton;

    private Button templateButton;

    private EntryWidget entryWidget;


    public NewEntryTypeWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        super.setTitle( "Entry Creation Method" );
        super.setDescription( "Please select the entry creation method." );
        super
            .setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
        super.setPageComplete( false );

        this.wizard = wizard;
    }


    private void validate()
    {
        if ( schemaButton.getSelection() )
        {
            super.setPageComplete( true );
        }
        else if ( templateButton.getSelection() )
        {
            super.setPageComplete( entryWidget.getConnection() != null && entryWidget.getDn() != null );
        }
        else
        {
            super.setPageComplete( false );
        }
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
    }


    public boolean canFlipToNextPage()
    {
        return isPageComplete();
    }


    public IWizardPage getNextPage()
    {

        if ( templateButton.getSelection() )
        {
            final IConnection connection = entryWidget.getConnection();
            final DN dn = entryWidget.getDn();
            final IEntry[] templateEntries = new IEntry[1];

            if ( connection == null )
            {
                getShell().getDisplay().syncExec( new Runnable()
                {
                    public void run()
                    {
                        MessageDialog.openError( getShell(), "Error", "No connection" );
                    }
                } );
                return null;
            }
            if ( dn == null )
            {
                getShell().getDisplay().syncExec( new Runnable()
                {
                    public void run()
                    {
                        MessageDialog.openError( getShell(), "Error", "No dn" );
                    }
                } );
                return null;
            }

            // check if selected DN exists
            ReadEntryJob readEntryJob = new ReadEntryJob( connection, dn );
            RunnableContextJobAdapter.execute( readEntryJob, getContainer(), false );
            templateEntries[0] = readEntryJob.getReadEntry();
            if ( templateEntries[0] == null )
            {
                getShell().getDisplay().syncExec( new Runnable()
                {
                    public void run()
                    {
                        MessageDialog.openError( getShell(), "Error", "Entry " + dn.toString() + " doesn't exists" );
                    }
                } );
                return null;
            }

            // init attributes
            if ( !templateEntries[0].isAttributesInitialized() )
            {
                InitializeAttributesJob job = new InitializeAttributesJob( templateEntries, false );
                RunnableContextJobAdapter.execute( job, getContainer() );
            }

            // clone entry and remove non-modifyable attributes
            try
            {
                EventRegistry.suspendEventFireingInCurrentThread();

                LdifContentRecord record = ModelConverter.entryToLdifContentRecord( templateEntries[0] );
                DummyEntry newEntry = ModelConverter.ldifContentRecordToEntry( record, connection );
                IAttribute[] attributes = newEntry.getAttributes();
                for ( int i = 0; i < attributes.length; i++ )
                {
                    if ( !SchemaUtils.isModifyable( attributes[i].getAttributeTypeDescription() ) )
                    {
                        newEntry.deleteAttribute( attributes[i] );
                    }
                }
                wizard.setNewEntry( newEntry );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
            finally
            {
                EventRegistry.resumeEventFireingInCurrentThread();
            }
        }
        else
        {
            wizard.setNewEntry( new DummyEntry( new DN(), wizard.getSelectedConnection() ) );
        }

        return super.getNextPage();
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        schemaButton = BaseWidgetUtils.createRadiobutton( composite, "Create entry from scratch", 1 );
        schemaButton.addSelectionListener( this );
        templateButton = BaseWidgetUtils.createRadiobutton( composite, "Use existing entry as template", 1 );
        templateButton.addSelectionListener( this );

        Composite entryComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );
        BaseWidgetUtils.createRadioIndent( entryComposite, 1 );
        entryWidget = new EntryWidget( this.wizard.getSelectedConnection(),
            this.wizard.getSelectedEntry() != null ? this.wizard.getSelectedEntry().getDn() : null );
        entryWidget.createWidget( entryComposite );
        entryWidget.addWidgetModifyListener( this );

        if ( BrowserUIPlugin.getDefault().getDialogSettings().get( PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY ) == null )
            BrowserUIPlugin.getDefault().getDialogSettings().put( PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY,
                true );
        schemaButton.setSelection( BrowserUIPlugin.getDefault().getDialogSettings().getBoolean(
            PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY ) );
        templateButton.setSelection( !BrowserUIPlugin.getDefault().getDialogSettings().getBoolean(
            PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY ) );
        widgetSelected( null );

        setControl( composite );
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    public void widgetDefaultSelected( SelectionEvent e )
    {
    }


    public void widgetSelected( SelectionEvent e )
    {
        entryWidget.setEnabled( templateButton.getSelection() );
        validate();
    }


    public void saveDialogSettings()
    {
        BrowserUIPlugin.getDefault().getDialogSettings().put( PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY,
            this.schemaButton.getSelection() );
    }

}
