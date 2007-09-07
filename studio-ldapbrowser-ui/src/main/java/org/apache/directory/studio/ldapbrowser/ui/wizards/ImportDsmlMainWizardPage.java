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


import java.io.File;

import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.FileBrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.ConnectionWidget;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * This class implements the Main Page of the DSML Import Wizard
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportDsmlMainWizardPage extends WizardPage
{
    /** The wizard the page is attached to */
    private ImportDsmlWizard wizard;

    /** The extensions used by DSML files*/
    private static final String[] EXTENSIONS = new String[]
        { "*.xml", "*.*" };

    /** The dsml file browser widget. */
    private FileBrowserWidget dsmlFileBrowserWidget;

    /** The connection widget. */
    private ConnectionWidget connectionWidget;

    /** The save response button. */
    private Button saveResponseButton;

    /** The use default response file button. */
    private Button useDefaultResponseFileButton;

    /** The use custom response file button. */
    private Button useCustomResponseFileButton;

    /** The response file browser widget. */
    private FileBrowserWidget responseFileBrowserWidget;

    /** The overwrite response file button. */
    private Button overwriteResponseFileButton;

    /** The custom response file name. */
    private String customResponseFileName;


    /**
     * Creates a new instance of ImportDsmlMainWizardPage.
     *
     * @param pageName
     *          the name of the page
     * @param wizard
     *          the wizard the page is attached to
     */
    public ImportDsmlMainWizardPage( String pageName, ImportDsmlWizard wizard )
    {
        super( pageName );
        setTitle( ImportDsmlWizard.WIZARD_TITLE );
        setDescription( "Please select a connection and the DSML file to import" );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_IMPORT_DSML_WIZARD ) );
        setPageComplete( false );
        this.wizard = wizard;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // DSML file
        BaseWidgetUtils.createLabel( composite, "DSML file:", 1 );
        dsmlFileBrowserWidget = new FileBrowserWidget( "Select a DSML File", EXTENSIONS, FileBrowserWidget.TYPE_OPEN );
        dsmlFileBrowserWidget.createWidget( composite );
        dsmlFileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                wizard.setDsmlFilename( dsmlFileBrowserWidget.getFilename() );
                if ( useDefaultResponseFileButton.getSelection() )
                {
                    responseFileBrowserWidget.setFilename( dsmlFileBrowserWidget.getFilename() + ".response.xml" );
                }
                validate();
            }
        } );

        // Connection
        BaseWidgetUtils.createLabel( composite, "Import into:", 1 );
        connectionWidget = new ConnectionWidget( wizard.getImportConnection() );
        connectionWidget.createWidget( composite );
        connectionWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                wizard.setImportConnection( connectionWidget.getBrowserConnection() );
                validate();
            }
        } );

        // Save Response
        Composite responseOuterComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 3 );
        Group responseGroup = BaseWidgetUtils.createGroup( responseOuterComposite, "Response", 1 );
        Composite responseContainer = BaseWidgetUtils.createColumnContainer( responseGroup, 3, 1 );

        saveResponseButton = BaseWidgetUtils.createCheckbox( responseContainer, "Save response", 3 );
        saveResponseButton.setSelection( true );
        wizard.setSaveResponse( saveResponseButton.getSelection() );
        saveResponseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                wizard.setSaveResponse( saveResponseButton.getSelection() );
                useDefaultResponseFileButton.setEnabled( saveResponseButton.getSelection() );
                useCustomResponseFileButton.setEnabled( saveResponseButton.getSelection() );
                responseFileBrowserWidget.setEnabled( saveResponseButton.getSelection()
                    && useCustomResponseFileButton.getSelection() );
                overwriteResponseFileButton.setEnabled( saveResponseButton.getSelection() );
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( responseContainer, 1 );
        useDefaultResponseFileButton = BaseWidgetUtils.createRadiobutton( responseContainer,
            "Use default response file", 2 );
        useDefaultResponseFileButton.setSelection( true );
        useDefaultResponseFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                String temp = customResponseFileName;
                responseFileBrowserWidget.setFilename( dsmlFileBrowserWidget.getFilename() + ".response.xml" );
                responseFileBrowserWidget.setEnabled( false );
                customResponseFileName = temp;
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( responseContainer, 1 );
        useCustomResponseFileButton = BaseWidgetUtils.createRadiobutton( responseContainer, "Use custom response file",
            2 );
        useCustomResponseFileButton.setSelection( false );
        useCustomResponseFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                responseFileBrowserWidget.setFilename( customResponseFileName != null ? customResponseFileName : "" );
                responseFileBrowserWidget.setEnabled( true );
                validate();
            }
        } );

        BaseWidgetUtils.createRadioIndent( responseContainer, 1 );
        responseFileBrowserWidget = new FileBrowserWidget( "Select save file", null, FileBrowserWidget.TYPE_SAVE );
        responseFileBrowserWidget.createWidget( responseContainer );
        responseFileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                customResponseFileName = responseFileBrowserWidget.getFilename();
                wizard.setResponseFilename( customResponseFileName );
                validate();
            }
        } );
        responseFileBrowserWidget.setEnabled( false );

        BaseWidgetUtils.createRadioIndent( responseContainer, 1 );
        overwriteResponseFileButton = BaseWidgetUtils.createCheckbox( responseContainer,
            "Overwrite existing response file", 2 );
        overwriteResponseFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                validate();
            }
        } );

        setControl( composite );
    }


    /**
     * Validates the page. This method is responsible for displaying errors, as well as enabling/disabling the "Finish" button
     */
    private void validate()
    {
        boolean ok = true;

        File dsmlFile = new File( dsmlFileBrowserWidget.getFilename() );
        if ( "".equals( dsmlFileBrowserWidget.getFilename() ) )
        {
            setErrorMessage( null );
            ok = false;
        }
        else if ( !dsmlFile.isFile() || !dsmlFile.exists() )
        {
            setErrorMessage( "Selected DSML file doesn't exist." );
            ok = false;
        }
        else if ( !dsmlFile.canRead() )
        {
            setErrorMessage( "Selected DSML file is not readable." );
            ok = false;
        }
        else if ( saveResponseButton.getSelection() )
        {
            File responseFile = new File( responseFileBrowserWidget.getFilename() );
            File responseFileDirectory = responseFile.getParentFile();

            if ( responseFile.equals( dsmlFile ) )
            {
                setErrorMessage( "DSML file and response file must not be equal." );
                ok = false;
            }
            else if ( responseFile.isDirectory() )
            {
                setErrorMessage( "Selected response file is not a file." );
                ok = false;
            }
            else if ( responseFile.exists() && !overwriteResponseFileButton.getSelection() )
            {
                setErrorMessage( "Selected response file already exists. Select option 'Overwrite existing response file' if you want to overwrite the response file." );
                ok = false;
            }
            else if ( responseFile.exists() && !responseFile.canWrite() )
            {
                setErrorMessage( "Selected response file is not writeable." );
                ok = false;
            }
            else if ( responseFile.getParentFile() == null )
            {
                setErrorMessage( "Selected response file directory is not writeable." );
                ok = false;
            }
            else if ( !responseFile.exists() && ( responseFileDirectory == null || !responseFileDirectory.canWrite() ) )
            {
                setErrorMessage( "Selected response file directory is not writeable." );
                ok = false;
            }
        }

        if ( ( wizard.getImportConnection() == null ) || ( connectionWidget.getBrowserConnection() == null ) )
        {
            setErrorMessage( "Please select a Connection." );
            ok = false;
        }

        if ( ok )
        {
            setErrorMessage( null );
        }
        setPageComplete( ok );
        getContainer().updateButtons();
    }


    /**
     * Saves the Dialog Settings of the Page
     */
    public void saveDialogSettings()
    {
        dsmlFileBrowserWidget.saveDialogSettings();
    }
}
