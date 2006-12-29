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

package org.apache.directory.ldapstudio.dsmlv2.view;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.directory.ldapstudio.dsmlv2.Activator;
import org.eclipse.core.runtime.Preferences;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.xmlpull.v1.XmlPullParserException;


/**
 * This class implements the Execute Wizard Page
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExecuteWizardPage extends WizardPage
{
    // Preferences identifiers (used to keep the last information entered in the fields)
    public static final String HOST = "HOST";
    public static final String PORT = "PORT";
    public static final String USER_DN = "USER_DN";
    public static final String INPUT_FILE_PATH = "INPUT_FILE_PATH";
    public static final String OUTPUT_CHOICE = "OUTPUT_CHOICE";
    public static final int OUTPUT_CHOICE_CONSOLE = 0;
    public static final int OUTPUT_CHOICE_FILE = 1;
    public static final String OUTPUT_FILE_PATH = "OUTPUT_FILE_PATH";

    // UI fields
    private Text host_text;
    private Text port_text;
    private Text userDN_text;
    private Text password_text;
    private Text inputFile_text;
    private Button inputFile_button;
    private Text outputFile_text;
    private Button outputFile_button;
    private Button outputFileConsoleChoice_radio;
    private Button outputFileFileChoice_radio;
    private Label outputFile_label;


    /**
     * Creates a new instance of ExecuteWizardPage.
     *
     */
    protected ExecuteWizardPage()
    {
        super( "ExecuteDSML" );
        setTitle( "Execute a DSML File" );
        setDescription( "Executes a DSML File" );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.WIZARD_DSML ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        container.setLayout( layout );
        layout.numColumns = 1;

        // Server Settings Group
        Group serverGroup = new Group( container, SWT.NULL );
        serverGroup.setText( "Server settings" );
        layout = new GridLayout();
        serverGroup.setLayout( layout );
        layout.numColumns = 3;
        serverGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 1, 1 ) );

        // Host
        Label host_label = new Label( serverGroup, SWT.NONE );
        host_label.setText( "Host:" );

        host_text = new Text( serverGroup, SWT.BORDER );
        host_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Port
        Label port_label = new Label( serverGroup, SWT.NONE );
        port_label.setText( "Port:" );

        port_text = new Text( serverGroup, SWT.BORDER );
        port_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ) );

        // User DN
        Label userDN_label = new Label( serverGroup, SWT.NONE );
        userDN_label.setText( "User DN:" );

        userDN_text = new Text( serverGroup, SWT.BORDER );
        userDN_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Password
        Label password_label = new Label( serverGroup, SWT.NONE );
        password_label.setText( "Password:" );

        password_text = new Text( serverGroup, SWT.BORDER | SWT.PASSWORD );
        password_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Input Group
        Group inputGroup = new Group( container, SWT.NULL );
        inputGroup.setText( "Input file" );
        layout = new GridLayout();
        inputGroup.setLayout( layout );
        layout.numColumns = 3;
        inputGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 1, 1 ) );

        // Input File
        Label inputFile_label = new Label( inputGroup, SWT.NONE );
        inputFile_label.setText( "Input File:" );

        inputFile_text = new Text( inputGroup, SWT.BORDER );
        inputFile_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        inputFile_button = new Button( inputGroup, SWT.BORDER );
        inputFile_button.setText( "Browse..." );

        // Output Group
        Group outputGroup = new Group( container, SWT.NULL );
        outputGroup.setText( "Output" );
        layout = new GridLayout();
        outputGroup.setLayout( layout );
        layout.numColumns = 3;
        outputGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 1, 1 ) );

        // Console
        outputFileConsoleChoice_radio = new Button( outputGroup, SWT.RADIO );
        outputFileConsoleChoice_radio.setText( "Show the response in a console." );
        outputFileConsoleChoice_radio.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Output File
        outputFileFileChoice_radio = new Button( outputGroup, SWT.RADIO );
        outputFileFileChoice_radio.setText( "Save the response to a file :" );
        outputFileFileChoice_radio.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 3, 1 ) );

        outputFile_label = new Label( outputGroup, SWT.NONE );
        outputFile_label.setText( "Output File:" );

        outputFile_text = new Text( outputGroup, SWT.BORDER );
        outputFile_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        outputFile_button = new Button( outputGroup, SWT.BORDER );
        outputFile_button.setText( "Browse..." );

        setControl( container );

        initListeners();
        initFields();

        updatePageComplete();
    }


    /**
     * Initializes SWT widgets listeners
     */
    private void initListeners()
    {
        // Host
        host_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        } );

        // Port
        port_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        } );

        // User DN
        userDN_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        } );

        // Password
        password_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        } );

        // Input File Browse Button
        inputFile_button.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    SWT.OPEN );
                fd.setText( "Choose an input file." );
                if ( ( inputFile_text.getText() == null ) || ( "".equals( inputFile_text.getText() ) ) )
                {
                    fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
                }
                else
                {
                    fd.setFilterPath( inputFile_text.getText() );
                }
                fd.setFilterExtensions( new String[]
                    { "*.dsml;*.DSML", "*.xml;*.XML", "*.*" } );
                fd.setFilterNames( new String[]
                    { "DSML files", "XML files", "All files" } );
                inputFile_text.setText( fd.open() );
                updatePageComplete();
            }
        } );

        // Input File Text
        inputFile_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        } );

        // Output Radio Buttons
        outputFileFileChoice_radio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                outputFileConsoleChoice_radio.setSelection( false );
                outputFile_label.setEnabled( true );
                outputFile_text.setEnabled( true );
                outputFile_button.setEnabled( true );
                outputFileFileChoice_radio.setSelection( true ); // We force selection
                updatePageComplete();
            }
        } );
        outputFileConsoleChoice_radio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                outputFileFileChoice_radio.setSelection( false );
                outputFile_label.setEnabled( false );
                outputFile_text.setEnabled( false );
                outputFile_button.setEnabled( false );
                outputFileConsoleChoice_radio.setSelection( true ); // We force selection
                updatePageComplete();
            }
        } );

        // Output File Browse Button
        outputFile_button.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    SWT.SAVE );
                fd.setText( "Specify an output file." );
                if ( ( outputFile_text.getText() == null ) || ( "".equals( outputFile_text.getText() ) ) )
                {
                    fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
                }
                else
                {
                    fd.setFilterPath( outputFile_text.getText() );
                }
                fd.setFilterExtensions( new String[]
                    { "*.dsml;*.DSML", "*.xml;*.XML", "*.*" } );
                fd.setFilterNames( new String[]
                    { "DSML files", "XML files", "All files" } );
                outputFile_text.setText( fd.open() );
                updatePageComplete();
            }
        } );

        // Output File Text
        outputFile_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        } );
    }


    /**
     * Initializes the fields with default value
     */
    private void initFields()
    {
        // Getting the preferences store
        Preferences store = Activator.getDefault().getPluginPreferences();

        host_text.setText( store.getString( HOST ) );
        port_text.setText( store.getString( PORT ) );
        userDN_text.setText( store.getString( USER_DN ) );

        inputFile_text.setText( store.getString( INPUT_FILE_PATH ) );

        int outputChoice = store.getInt( OUTPUT_CHOICE );
        if ( outputChoice == OUTPUT_CHOICE_CONSOLE )
        {
            outputFileConsoleChoice_radio.setSelection( true );
            outputFile_label.setEnabled( false );
            outputFile_text.setEnabled( false );
            outputFile_button.setEnabled( false );
            outputFileFileChoice_radio.setSelection( false );
        }
        else if ( outputChoice == OUTPUT_CHOICE_FILE )
        {
            outputFileFileChoice_radio.setSelection( true );
            outputFileConsoleChoice_radio.setSelection( false );
            outputFile_text.setText( store.getString( OUTPUT_FILE_PATH ) );
        }
    }


    /**
     * Checks if the page is complete and the user allowed to hit the 'Finish' button
     */
    private void updatePageComplete()
    {
        setPageComplete( false );
        setErrorMessage( null );

        // Host
        if ( ( host_text.getText() == null ) || ( "".equals( host_text.getText() ) ) )
        {
            setErrorMessage( "An host must be provided." );
            return;
        }

        // Port
        if ( ( port_text.getText() == null ) || ( "".equals( port_text.getText() ) ) )
        {
            setErrorMessage( "A port must be provided." );
            return;
        }
        else
        {
            try
            {
                int port = Integer.parseInt( port_text.getText() );

                if ( ( port < 0 ) || ( port > 65536 ) )
                {
                    setErrorMessage( "The port value must be between 1 to 65536." );
                    return;
                }
            }
            catch ( NumberFormatException e )
            {
                setErrorMessage( "The port value must be an integer." );
                return;
            }
        }

        // User DN
        if ( ( userDN_text.getText() == null ) || ( "".equals( userDN_text.getText() ) ) )
        {
            setErrorMessage( "A user DN must be provided." );
            return;
        }

        // Input
        if ( ( inputFile_text.getText() == null ) || ( "".equals( inputFile_text.getText() ) ) )
        {
            // TODO Add verification of presence of the file 
            setErrorMessage( "An input file must be provided." );
            return;
        }
        else
        {
            File checkFile = new File( inputFile_text.getText() );

            if ( !checkFile.exists() )
            {
                setErrorMessage( "The input file doesn't exit." );
                return;
            }
        }

        // Output
        if ( ( outputFileFileChoice_radio.getSelection() ) || ( outputFileConsoleChoice_radio.getSelection() ) )
        {
            if ( outputFileFileChoice_radio.getSelection()
                && ( ( outputFile_text.getText() == null ) || ( "".equals( outputFile_text.getText() ) ) ) )
            {
                setErrorMessage( "An output file must be provided." );
                return;
            }
        }

        setPageComplete( true );
    }


    /**
     * Performs any actions appropriate in response to the user having pressed the Finish 
     * button, or refuse if finishing now is not permitted.
     * @return true to indicate the finish request was accepted, and false to indicate that 
     *  the finish request was refused
     */
    public boolean performFinish()
    {
        // Getting the preferences store
        Preferences store = Activator.getDefault().getPluginPreferences();

        // Registering preferences
        store.setValue( HOST, host_text.getText() );
        store.setValue( PORT, port_text.getText() );
        store.setValue( USER_DN, userDN_text.getText() );
        store.setValue( INPUT_FILE_PATH, inputFile_text.getText() );
        if ( outputFileFileChoice_radio.getSelection() )
        {
            store.setValue( OUTPUT_CHOICE, OUTPUT_CHOICE_FILE );
            store.setValue( OUTPUT_FILE_PATH, outputFile_text.getText() );
        }
        else if ( outputFileConsoleChoice_radio.getSelection() )
        {
            store.setValue( OUTPUT_CHOICE, OUTPUT_CHOICE_CONSOLE );
        }

        int port = Integer.parseInt( port_text.getText() ); // There's no need to catch NumberFormatException Exception since the verification has already been done.

        Dsmlv2Engine engine = new Dsmlv2Engine( host_text.getText(), port, userDN_text.getText(), password_text
            .getText() );

        // Processing DSMLv2 input file on the server 
        String response = null;
        try
        {
            response = engine.processDSMLFile( inputFile_text.getText() );
        }
        catch ( FileNotFoundException e )
        {
            setErrorMessage( "The input file could not been found." );
            return false;
        }
        catch ( XmlPullParserException e )
        {
            setErrorMessage( "An error ocurred when parsing the input file.\n Error: " + e.getMessage() );
            return false;
        }

        // Managing Output
        if ( outputFileConsoleChoice_radio.getSelection() )
        {
            ConsoleDialog consoleDialog = new ConsoleDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell(), response );
            consoleDialog.open();
        }
        else if ( outputFileFileChoice_radio.getSelection() )
        {
            String outputFile = outputFile_text.getText();

            if ( outputFile == null )
            {
                setErrorMessage( "The output file can't be null" );
                return false;
            }
            File checkFile = new File( outputFile );

            if ( checkFile.exists() )
            {
                MessageBox messageBox = new MessageBox(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES
                        | SWT.NO );
                messageBox.setText( "Replace existing file?" ); //$NON-NLS-1$
                messageBox
                    .setMessage( "The file " + checkFile.getName() + "already exists. Do you want to replace it?" ); //$NON-NLS-1$ //$NON-NLS-2$
                if ( SWT.YES != messageBox.open() )
                {
                    return false;
                }
                else
                {
                    checkFile.delete();
                }
            }
            else
            {
                // File doesn't exists, but does parent directories exists ?
                String parentPathName = checkFile.getParent();
                if ( parentPathName == null )
                {
                    setErrorMessage( "The path to the destination file seems to be wrong" );
                    return false;
                }
                else
                {
                    File parent = new File( parentPathName );
                    if ( !parent.exists() )
                    {
                        MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO );
                        messageBox.setText( "Create directory?" );
                        messageBox.setMessage( "ExportWizard.Target_directory_doesnt_exist" );
                        if ( SWT.YES != messageBox.open() )
                        {
                            return false;
                        }
                        else
                        {
                            // Creating target directory (and its sub-directories if necessary)
                            parent.mkdirs();
                        }
                    }
                }
            }

            // Saving the file to disk
            try
            {
                // Open an output stream
                FileOutputStream fout = new FileOutputStream( checkFile );

                // Print a line of text
                new PrintStream( fout ).println( response );

                // Close our output stream
                fout.close();
            }
            // Catches any error conditions
            catch ( IOException e )
            {
                setErrorMessage( "An error ocurred when writine file to disk" );
                return false;
            }
        }

        return false;
    }
}
