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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
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


/**
 * The NewEntryTypeWizardPage is used to choose the entry creation method.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewEntryTypeWizardPage extends WizardPage implements WidgetModifyListener, SelectionListener
{

    /** The Constant PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY. */
    public static final String PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY = NewEntryTypeWizardPage.class
        .getName()
        + ".preferredEntryCreationMethod";

    /** The wizard. */
    private NewEntryWizard wizard;

    /** The schema button. */
    private Button schemaButton;

    /** The template button. */
    private Button templateButton;

    /** The entry widget to select the template entry. */
    private EntryWidget entryWidget;


    /**
     * Creates a new instance of NewEntryTypeWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewEntryTypeWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        setTitle( "Entry Creation Method" );
        setDescription( "Please select the entry creation method." );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;
    }


    /**
     * Validates the input fields.
     */
    private void validate()
    {
        if ( schemaButton.getSelection() )
        {
            setPageComplete( true );
        }
        else if ( templateButton.getSelection() )
        {
            setPageComplete( entryWidget.getBrowserConnection() != null && entryWidget.getDn() != null );
        }
        else
        {
            setPageComplete( false );
        }
    }

    
    /**
     * {@inheritDoc}
     * 
     * This implementation just checks if this page is complete. IIt 
     * doesn't call {@link #getNextPage()} to avoid unneeded 
     * creations of new prototype entries.
     */
    public boolean canFlipToNextPage()
    {
        return isPageComplete();
    }
    

    /**
     * {@inheritDoc}
     * 
     * This implementation creates the prototype entry depending on the 
     * selected entry creation method before flipping to the next page.
     */
    public IWizardPage getNextPage()
    {
        if ( templateButton.getSelection() )
        {
            final IBrowserConnection browserConnection = entryWidget.getBrowserConnection();
            final LdapDN dn = entryWidget.getDn();
            final IEntry[] templateEntries = new IEntry[1];

            if ( browserConnection == null )
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
            ReadEntryRunnable readEntryRunnable = new ReadEntryRunnable( browserConnection, dn );
            RunnableContextRunner.execute( readEntryRunnable, getContainer(), false );
            templateEntries[0] = readEntryRunnable.getReadEntry();
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
                InitializeAttributesRunnable initializeAttributesRunnable = new InitializeAttributesRunnable( templateEntries, false );
                RunnableContextRunner.execute( initializeAttributesRunnable, getContainer(), true );
            }

            // clone entry and remove non-modifyable attributes
            try
            {
                EventRegistry.suspendEventFireingInCurrentThread();

                LdifContentRecord record = ModelConverter.entryToLdifContentRecord( templateEntries[0] );
                DummyEntry prototypeEntry = ModelConverter.ldifContentRecordToEntry( record, browserConnection );
                IAttribute[] attributes = prototypeEntry.getAttributes();
                for ( int i = 0; i < attributes.length; i++ )
                {
                    if ( !SchemaUtils.isModifyable( attributes[i].getAttributeTypeDescription() ) )
                    {
                        prototypeEntry.deleteAttribute( attributes[i] );
                    }
                }
                wizard.setPrototypeEntry( prototypeEntry );
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
            wizard.setPrototypeEntry( new DummyEntry( new LdapDN(), wizard.getSelectedConnection() ) );
        }

        return super.getNextPage();
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

        schemaButton = BaseWidgetUtils.createRadiobutton( composite, "Create entry from scratch", 1 );
        schemaButton.addSelectionListener( this );
        templateButton = BaseWidgetUtils.createRadiobutton( composite, "Use existing entry as template", 1 );
        templateButton.addSelectionListener( this );

        Composite entryComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );
        BaseWidgetUtils.createRadioIndent( entryComposite, 1 );
        entryWidget = new EntryWidget( wizard.getSelectedConnection(), wizard.getSelectedEntry() != null ? wizard
            .getSelectedEntry().getDn() : null );
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


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected( SelectionEvent e )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void widgetSelected( SelectionEvent e )
    {
        entryWidget.setEnabled( templateButton.getSelection() );
        validate();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        BrowserUIPlugin.getDefault().getDialogSettings().put( PREFERRED_ENTRY_CREATION_METHOD_DIALOGSETTING_KEY,
            schemaButton.getSelection() );
    }

}
