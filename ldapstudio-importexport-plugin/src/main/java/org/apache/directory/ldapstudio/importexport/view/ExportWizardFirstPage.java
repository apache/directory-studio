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

package org.apache.directory.ldapstudio.importexport.view;

import javax.naming.directory.SearchControls;

import org.apache.directory.ldapstudio.importexport.Messages;
import org.apache.directory.ldapstudio.importexport.Plugin;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the first page of the Export Wizard
 * On this page, the user provides all the information about the export, 
 * such as: Base DN, Scope and destination file
 */
public class ExportWizardFirstPage extends WizardPage
{  
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ExportWizardFirstPage.class );
    
    private Text destionationFile_text;
    private Text exportPoint_text;
    private Button scopeObject_button;
    private Button scopeOneLevel_button;
    private Button scopeSubTree;
    private Button destionationFile_button;

    /**
     * Default constructor
     */
    public ExportWizardFirstPage() {
        super("ExportAsLDIF"); //$NON-NLS-1$
        setTitle(Messages.getString("ExportWizardFirstPage.Export_as_LDIF")); //$NON-NLS-1$
        setDescription(Messages.getString("ExportWizardFirstPage.Wizard_Page_Description")); //$NON-NLS-1$
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Plugin.ID, ImageKeys.WIZARD_EXPORT ) );
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        
        Label exportPoint_label = new Label(container, SWT.NONE);
        exportPoint_label.setText( Messages.getString("ExportWizardFirstPage.Export_Point") ); //$NON-NLS-1$
        
        exportPoint_text = new Text(container, SWT.BORDER);
        exportPoint_text.setLayoutData(new GridData(GridData.FILL, SWT.NONE, true, false, 2, 1));
        
        Group group = new Group( container, SWT.NONE );
        group.setText( Messages.getString("ExportWizardFirstPage.Define_Scope") ); //$NON-NLS-1$
        
        group.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false, 3, 1 ) );
        group.setLayout( new GridLayout() );
        
        
        scopeObject_button = new Button( group, SWT.RADIO );
        scopeObject_button.setText( Messages.getString("ExportWizardFirstPage.Object") ); //$NON-NLS-1$
        
        scopeOneLevel_button = new Button( group, SWT.RADIO );
        scopeOneLevel_button.setText( Messages.getString("ExportWizardFirstPage.One_level") ); //$NON-NLS-1$
        
        scopeSubTree = new Button( group, SWT.RADIO );
        scopeSubTree.setText( Messages.getString("ExportWizardFirstPage.Subtree") ); //$NON-NLS-1$
        
        Label destionationFile_label = new Label( container, SWT.NONE );
        destionationFile_label.setText( Messages.getString("ExportWizardFirstPage.Destination_file") ); //$NON-NLS-1$
        
        destionationFile_text = new Text( container, SWT.BORDER );
        destionationFile_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        
        destionationFile_button = new Button( container, SWT.BORDER );
        destionationFile_button.setText( Messages.getString("ExportWizardFirstPage.Browse") ); //$NON-NLS-1$
        destionationFile_button.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
                
                FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE );
                fd.setText( Messages.getString("ExportWizardFirstPage.Export_to_LDIF_file") ); //$NON-NLS-1$
                fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
                fd.setFilterExtensions( new String[] { "*.ldif;*.LDIF", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
                fd.setFilterNames( new String[] { Messages.getString("ExportWizardFirstPage.LDIF_files"), Messages.getString("ExportWizardFirstPage.All_files") } ); //$NON-NLS-1$ //$NON-NLS-2$
                destionationFile_text.setText( fd.open() );
                logger.info( "Destination file has changed : " + destionationFile_text.getText() ); //$NON-NLS-1$
            }
        });
        
        setControl(container);
        
        initListeners();
        initFields();
        updatePageComplete();
    }

    /**
     * Initializes SWT widgets listeners
     */
    private void initListeners()
    {
        // Export Point
        exportPoint_text.addModifyListener( new ModifyListener(){
            public void modifyText( ModifyEvent e )
            {
                resetEntries();
                updatePageComplete();
            }
        });
        
        //SCOPE
        scopeObject_button.addSelectionListener( new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e )
            {
                resetEntries();
                updatePageComplete();
            }
        });
        
        scopeOneLevel_button.addSelectionListener( new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e )
            {
                resetEntries();
                updatePageComplete();
            }
        });
        
        scopeSubTree.addSelectionListener( new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e )
            {
                resetEntries();
                updatePageComplete();
            }
        });
        
        // Destination file
        destionationFile_text.addModifyListener( new ModifyListener(){
            public void modifyText( ModifyEvent e )
            {
                updatePageComplete();
            }
        });
        
        destionationFile_button.addSelectionListener( new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e )
            {
                updatePageComplete();
            }
        });
    }

    /**
     * Initializes the fields with default value
     */
    private void initFields()
    {
        // SCOPE
        scopeSubTree.setSelection( true );
    }
    
    /**
     * Checks if the page is complete and the user allowed to access the next page
     */
    private void updatePageComplete()
    {
        setPageComplete( false );
        setErrorMessage( null );
        
        // SCOPE
        if ( ( !scopeObject_button.getSelection() ) && ( !scopeOneLevel_button.getSelection() ) && ( !scopeSubTree.getSelection() ) )
        {
            return;
        }
        
        // DESTINATION FILE
        if ( ( destionationFile_text.getText() == null ) || ( "".equals( destionationFile_text.getText() ) ) )  //$NON-NLS-1$
        {
            return;
        }
        
        setPageComplete( true );
    }

    /**
     * Resets already fetched entries
     */
    private void resetEntries()
    {
        ExportWizard wizard = ( ExportWizard ) getWizard();
        wizard.resetEntries();        
    }

    /**
     * Returns the Export Point
     * @return the Export Point
     */
    public String getExportPoint()
    {
        return this.exportPoint_text.getText();
    }
    
    /**
     * Returns the Scope of the export.
     * Values can be : SearchControls.OBJECT_SCOPE, SearchControls.ONELEVEL_SCOPE,
     * SearchControls.SUBTREE_SCOPE or -1 (if no scope has been choosen - should never occur)
     * @return the Scope of the export
     */
    public int getScope()
    {
        if ( scopeObject_button.getSelection() )
        {
            return SearchControls.OBJECT_SCOPE;
        }
        else if ( scopeOneLevel_button.getSelection() )
        {
            return SearchControls.ONELEVEL_SCOPE;
        }
        else if ( scopeSubTree.getSelection() )
        {
            return SearchControls.SUBTREE_SCOPE;
        }
        
        // Should never go there
        return -1;
    }
    
    /**
     * Returns the destination file path
     * @return the destination file path
     */
    public String getDestinationFile()
    {
        return destionationFile_text.getText();
    }
}
