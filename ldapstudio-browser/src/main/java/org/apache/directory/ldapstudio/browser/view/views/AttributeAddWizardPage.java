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

package org.apache.directory.ldapstudio.browser.view.views;


import java.io.File;

import javax.naming.directory.Attributes;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.modify.ModifyResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Attribute Add Wizard Page of the Attribute Add Wizard
 * TODO Add support for Binary File...
 */
public class AttributeAddWizardPage extends WizardPage
{

    private Label nameLabel;
    private Text nameText;
    private Group valueGroup;
    private Button textChoice;
    private Text textText;
    private Button binaryFileChoice;
    private Text binaryFileText;
    private Button binaryFileBrowseButton;


    /**
     * Default constructor
     */
    protected AttributeAddWizardPage()
    {
        super( "AttributeAddWizardPage" );
        setTitle( "Add a new attribute" );
        setDescription( "Specify information for the new attribute" );
        setImageDescriptor( AbstractUIPlugin
            .imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.WIZARD_ATTRIBUTE ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        container.setLayout( layout );
        layout.numColumns = 2;

        // Name
        nameLabel = new Label( container, SWT.NONE );
        nameLabel.setText( "Name:" );
        nameText = new Text( container, SWT.BORDER );
        nameText.setText( "" );
        nameText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Value Group
        valueGroup = new Group( container, SWT.NONE );
        valueGroup.setText( "Value" );
        valueGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ) );
        valueGroup.setLayout( new GridLayout( 3, false ) );

        // Text Choice
        textChoice = new Button( valueGroup, SWT.RADIO );
        textChoice.setText( "Text:" );
        textChoice.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );

        // Text 
        textText = new Text( valueGroup, SWT.BORDER );
        textText.setText( "" );
        textText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Binary File Choice
        binaryFileChoice = new Button( valueGroup, SWT.RADIO );
        binaryFileChoice.setText( "Binary file:" );
        binaryFileChoice.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );

        // Binary File 
        binaryFileText = new Text( valueGroup, SWT.BORDER );
        binaryFileText.setText( "" );
        binaryFileText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Binary File Browse
        binaryFileBrowseButton = new Button( valueGroup, SWT.NONE );
        binaryFileBrowseButton.setText( "Browse..." );
        binaryFileBrowseButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );

        setControl( nameText );

        initFields();

        initListeners();

        verifyPage();
    }


    /**
     * Initializes the fields of the UI with default values
     */
    private void initFields()
    {
        // The default choice is Text, so Binary File Widgets are disabled
        textChoice.setSelection( true );
        textText.setEnabled( true );
        binaryFileChoice.setSelection( false );
        binaryFileText.setEnabled( false );
        binaryFileBrowseButton.setEnabled( false );
    }


    /**
     * Initializes the listeners on SWT widgets
     */
    private void initListeners()
    {
        // Name
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyPage();
            }
        } );

        // Text Choice
        textChoice.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                textChoice.setSelection( true );
                binaryFileChoice.setSelection( false );
                binaryFileText.setEnabled( false );
                binaryFileBrowseButton.setEnabled( false );
                textText.setEnabled( true );
                verifyPage();
            }
        } );

        // Text
        textText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyPage();
            }
        } );

        // Binary File Choice
        binaryFileChoice.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                binaryFileChoice.setSelection( true );
                textChoice.setSelection( false );
                textText.setEnabled( false );
                binaryFileText.setEnabled( true );
                binaryFileBrowseButton.setEnabled( true );
                verifyPage();
            }
        } );

        // Binary File
        binaryFileText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyPage();
            }
        } );

        // Binary File Button
        // Input File Browse Button
        binaryFileBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    SWT.OPEN );
                fd.setText( "Choose a file." );
                if ( ( binaryFileText.getText() == null ) || ( "".equals( binaryFileText.getText() ) ) )
                {
                    fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
                }
                else
                {
                    fd.setFilterPath( binaryFileText.getText() );
                }
                binaryFileText.setText( fd.open() );
                verifyPage();
            }
        } );
    }


    /**
     * Verifies the status of the page and updates the complete state
     */
    private void verifyPage()
    {
        // Reseting previous message
        setErrorMessage( null );
        setPageComplete( true );

        if ( ( nameText.getText() == null ) || ( "".equals( nameText.getText() ) ) )
        {
            setErrorMessage( "Name can't be empty." );
            setPageComplete( false );
            return;
        }

        if ( binaryFileChoice.getSelection() )
        {
            if ( ( binaryFileText.getText() == null ) || ( "".equals( binaryFileText.getText() ) ) )
            {
                setErrorMessage( "A binary file must be provided." );
                setPageComplete( false );
                return;
            }
            else
            {
                File checkFile = new File( binaryFileText.getText() );

                if ( !checkFile.exists() )
                {
                    setErrorMessage( "The binary file doesn't exit." );
                    setPageComplete( false );
                    return;
                }
            }
        }
    }


    /**
     * Indicates if the Wizard is able to finish
     * @return true if the Wizard is able to finish
     */
    public boolean canFinish()
    {
        return isPageComplete();
    }


    /**
     * Adds the new attribute and its value to the entry
     */
    public boolean performFinish()
    {
        try
        {
            // Getting the Browser View
            BrowserView browserView = ( BrowserView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().findView( BrowserView.ID );

            EntryWrapper entryWrapper = ( EntryWrapper ) ( ( TreeSelection ) browserView.getViewer().getSelection() )
                .getFirstElement();
            SearchResultEntry entry = entryWrapper.getEntry();

            // Initialization of the DSML Engine and the DSML Response Parser
            Dsmlv2Engine engine = entryWrapper.getDsmlv2Engine();
            Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

            String request = "<batchRequest>" + "	<modifyRequest dn=\""
                + entry.getObjectName().getNormName().toString() + "\">" + "		<modification name=\""
                + nameText.getText() + "\" operation=\"add\">" + "       	<value>" + textText.getText() + "</value>"
                + "       </modification>" + "	</modifyRequest>" + "</batchRequest>";

            parser.setInput( engine.processDSML( request ) );
            parser.parse();

            LdapResponse ldapResponse = parser.getBatchResponse().getCurrentResponse();

            if ( ldapResponse instanceof ModifyResponse )
            {
                ModifyResponse modifyResponse = ( ModifyResponse ) ldapResponse;

                if ( modifyResponse.getLdapResult().getResultCode() == 0 )
                {
                    // Adding the provided attribute value
                    Attributes attributes = entry.getPartialAttributeList();

                    attributes.put( nameText.getText(), textText.getText() );

                    // refreshing the UI
                    AttributesView attributesView = ( AttributesView ) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage().findView( AttributesView.ID );
                    attributesView.setInput( entryWrapper );
                    attributesView.resizeColumsToFit();
                }
                else
                {
                    // Displaying an error
                    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Error !", "An error has ocurred.\n" + modifyResponse.getLdapResult().getErrorMessage() );
                }
            }
            else if ( ldapResponse instanceof ErrorResponse )
            {
                ErrorResponse errorResponse = ( ErrorResponse ) ldapResponse;

                // Displaying an error
                MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                    "An error has ocurred.\n" + errorResponse.getMessage() );
            }
        }
        catch ( Exception e )
        {
            // Displaying an error
            MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                "An error has ocurred.\n" + e.getMessage() );
        }

        return true;
    }
}
