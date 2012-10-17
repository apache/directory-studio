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


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;

import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldifeditor.editor.LdifEditor;
import org.apache.directory.studio.ldifeditor.editor.NonExistingLdifEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.texteditor.IDocumentProvider;


public class BatchOperationWizard extends Wizard implements INewWizard
{
    /** The connection */
    private IBrowserConnection connection;

    // Wizard pages
    private BatchOperationApplyOnWizardPage applyOnPage;
    private BatchOperationTypeWizardPage typePage;
    private BatchOperationLdifWizardPage ldifPage;
    private BatchOperationModifyWizardPage modifyPage;
    private BatchOperationFinishWizardPage finishPage;


    /**
     * Creates a new instance of BatchOperationWizard.
     */
    public BatchOperationWizard()
    {
        super.setWindowTitle( Messages.getString( "BatchOperationWizard.BatchOperation" ) ); //$NON-NLS-1$
        super.setNeedsProgressMonitor( true );
    }


    /**
     * Gets the id.
     *
     * @return the id
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_BATCH_OPERATION;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {

        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        Connection[] connections = BrowserSelectionUtils.getConnections( selection );
        ISearch[] searches = BrowserSelectionUtils.getSearches( selection );
        IEntry[] entries = BrowserSelectionUtils.getEntries( selection );
        ISearchResult[] searchResults = BrowserSelectionUtils.getSearchResults( selection );
        IBookmark[] bookmarks = BrowserSelectionUtils.getBookmarks( selection );
        IAttribute[] attributes = BrowserSelectionUtils.getAttributes( selection );
        IValue[] values = BrowserSelectionUtils.getValues( selection );

        // if(searches.length + entries.length + searchResults.length +
        // bookmarks.length > 0) {
        if ( connections.length > 0
            && connections[0].getConnectionWrapper().isConnected()
            || searches.length + entries.length + searchResults.length + bookmarks.length + attributes.length
                + values.length > 0 )
        {

            ISearch search = BrowserSelectionUtils.getExampleSearch( selection );
            search.setName( null );
            this.connection = search.getBrowserConnection();

            applyOnPage = new BatchOperationApplyOnWizardPage( BatchOperationApplyOnWizardPage.class.getName(), this );
            addPage( applyOnPage );

            typePage = new BatchOperationTypeWizardPage( BatchOperationTypeWizardPage.class.getName(), this );
            addPage( typePage );

            ldifPage = new BatchOperationLdifWizardPage( BatchOperationLdifWizardPage.class.getName(), this );
            addPage( ldifPage );

            modifyPage = new BatchOperationModifyWizardPage( BatchOperationModifyWizardPage.class.getName(), this );
            addPage( modifyPage );

            finishPage = new BatchOperationFinishWizardPage( BatchOperationFinishWizardPage.class.getName() );
            addPage( finishPage );
        }
        else
        {
            IWizardPage page = new DummyWizardPage();
            addPage( page );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp( applyOnPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_batchoperation_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem().setHelp( typePage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_batchoperation_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem().setHelp( ldifPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_batchoperation_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem().setHelp( modifyPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_batchoperation_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem().setHelp( finishPage.getControl(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_batchoperation_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This private class implements a dummy wizard page that is displayed when no connection is selected.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    class DummyWizardPage extends WizardPage
    {
        /**
         * Creates a new instance of DummyWizardPage.
         */
        protected DummyWizardPage()
        {
            super( "" ); //$NON-NLS-1$
            super.setTitle( Messages.getString( "BatchOperationWizard.NoConnectionSelected" ) ); //$NON-NLS-1$
            super.setDescription( Messages.getString( "BatchOperationWizard.SelectOpenConnection" ) ); //$NON-NLS-1$
            // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ENTRY_WIZARD));
            super.setPageComplete( true );
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

            setControl( composite );
        }
    }


    /**
     * {@inheritDoc}
     */
    public IWizardPage getNextPage( IWizardPage page )
    {
        if ( this.applyOnPage != null )
        {

            if ( page == this.applyOnPage )
            {
                return this.typePage;
            }

            else if ( page == this.typePage
                && this.typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_CREATE_LDIF )
            {
                return this.ldifPage;
            }
            else if ( page == this.typePage
                && this.typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_MODIFY )
            {
                return this.modifyPage;
            }
            else if ( page == this.typePage
                && this.typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_DELETE )
            {
                return this.finishPage;
            }

            else if ( page == this.modifyPage )
            {
                return this.finishPage;
            }
            else if ( page == this.ldifPage )
            {
                return this.finishPage;
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean canFinish()
    {
        if ( this.applyOnPage != null )
        {
            if ( !this.applyOnPage.isPageComplete() )
            {
                return false;
            }
            if ( !this.typePage.isPageComplete() )
            {
                return false;
            }

            if ( this.typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_CREATE_LDIF
                && !this.ldifPage.isPageComplete() )
            {
                return false;
            }
            if ( this.typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_MODIFY
                && !this.modifyPage.isPageComplete() )
            {
                return false;
            }

            if ( !this.finishPage.isPageComplete() )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performCancel()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        if ( this.applyOnPage != null )
        {
            this.applyOnPage.saveDialogSettings();
            this.finishPage.saveDialogSettings();

            // get LDIF
            String ldifFragment = ""; //$NON-NLS-1$
            if ( typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_CREATE_LDIF )
            {
                ldifFragment = this.ldifPage.getLdifFragment();
            }
            else if ( typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_MODIFY )
            {
                ldifFragment = this.modifyPage.getLdifFragment();
            }
            if ( typePage.getOperationType() == BatchOperationTypeWizardPage.OPERATION_TYPE_DELETE )
            {
                ldifFragment = "changetype: delete" + BrowserCoreConstants.LINE_SEPARATOR; //$NON-NLS-1$
            }

            // get DNs
            Dn[] dns = applyOnPage.getApplyOnDns();
            if ( dns == null )
            {
                if ( applyOnPage.getApplyOnSearch() != null )
                {
                    ISearch search = applyOnPage.getApplyOnSearch();
                    if ( search.getBrowserConnection() != null )
                    {
                        search.setSearchResults( null );
                        SearchRunnable runnable = new SearchRunnable( new ISearch[]
                            { search } );
                        IStatus status = RunnableContextRunner.execute( runnable, getContainer(), true );
                        if ( status.isOK() )
                        {
                            ISearchResult[] srs = search.getSearchResults();
                            dns = new Dn[srs.length];
                            for ( int i = 0; i < srs.length; i++ )
                            {
                                dns[i] = srs[i].getDn();
                            }
                        }
                    }
                }
            }

            if ( dns != null )
            {
                StringBuffer ldif = new StringBuffer();
                for ( int i = 0; i < dns.length; i++ )
                {
                    ldif.append( "dn: " ); //$NON-NLS-1$
                    ldif.append( dns[i].getName() );
                    ldif.append( BrowserCoreConstants.LINE_SEPARATOR );
                    ldif.append( ldifFragment );
                    ldif.append( BrowserCoreConstants.LINE_SEPARATOR );
                }

                if ( finishPage.getExecutionMethod() == BatchOperationFinishWizardPage.EXECUTION_METHOD_LDIF_EDITOR )
                {
                    // Opening an LDIF Editor with the LDIF content
                    try
                    {
                        IEditorInput input = new NonExistingLdifEditorInput();
                        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .openEditor( input, LdifEditor.getId() );
                        IDocumentProvider documentProvider = ( ( LdifEditor ) editor ).getDocumentProvider();
                        if ( documentProvider != null )
                        {
                            IDocument document = documentProvider.getDocument( input );
                            if ( document != null )
                            {
                                document.set( ldif.toString() );
                            }
                        }
                    }
                    catch ( PartInitException e )
                    {
                        return false;
                    }

                    return true;
                }
                else if ( finishPage.getExecutionMethod() == BatchOperationFinishWizardPage.EXECUTION_METHOD_LDIF_FILE ) // TODO
                {
                    // Saving the LDIF to a file

                    // Getting the shell
                    Shell shell = Display.getDefault().getActiveShell();

                    // detect IDE or RCP:
                    // check if perspective org.eclipse.ui.resourcePerspective is available
                    boolean isIDE = CommonUIUtils.isIDEEnvironment();

                    if ( isIDE )
                    {
                        // Asking the user for the location where to 'save as' the file
                        SaveAsDialog dialog = new SaveAsDialog( shell );

                        if ( dialog.open() != Dialog.OK )
                        {
                            return false;
                        }

                        // Getting if the resulting file
                        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( dialog.getResult() );

                        try
                        {
                            // Creating the file if it does not exist
                            if ( !file.exists() )
                            {
                                file.create( new ByteArrayInputStream( "".getBytes() ), true, null ); //$NON-NLS-1$
                            }

                            // Saving the LDIF to the file in the workspace
                            file.setContents( new ByteArrayInputStream( ldif.toString().getBytes() ), true, true,
                                new NullProgressMonitor() );
                        }
                        catch ( Exception e )
                        {
                            return false;
                        }
                    }
                    else
                    {
                        boolean canOverwrite = false;
                        String path = null;

                        while ( !canOverwrite )
                        {
                            // Open FileDialog
                            FileDialog dialog = new FileDialog( shell, SWT.SAVE );
                            path = dialog.open();
                            if ( path == null )
                            {
                                return false;
                            }

                            // Check whether file exists and if so, confirm overwrite
                            final File externalFile = new File( path );
                            if ( externalFile.exists() )
                            {
                                String question = NLS.bind( Messages
                                    .getString( "BatchOperationWizard.TheFileAlreadyExistsReplace" ), path ); //$NON-NLS-1$
                                MessageDialog overwriteDialog = new MessageDialog( shell, Messages
                                    .getString( "BatchOperationWizard.Question" ), null, question, //$NON-NLS-1$
                                    MessageDialog.QUESTION, new String[]
                                        {
                                            IDialogConstants.YES_LABEL,
                                            IDialogConstants.NO_LABEL,
                                            IDialogConstants.CANCEL_LABEL }, 0 );
                                int overwrite = overwriteDialog.open();
                                switch ( overwrite )
                                {
                                    case 0: // Yes
                                        canOverwrite = true;
                                        break;
                                    case 1: // No
                                        break;
                                    case 2: // Cancel
                                    default:
                                        return false;
                                }
                            }
                            else
                            {
                                canOverwrite = true;
                            }
                        }

                        // Saving the LDIF to the file on disk
                        try
                        {
                            BufferedWriter outFile = new BufferedWriter( new FileWriter( path ) );
                            outFile.write( ldif.toString() );
                            outFile.close();
                        }
                        catch ( Exception e )
                        {
                            return false;
                        }
                    }

                    return true;
                }
                else if ( finishPage.getExecutionMethod() == BatchOperationFinishWizardPage.EXECUTION_METHOD_LDIF_CLIPBOARD )
                {
                    // Copying the LDIF to the clipboard
                    CopyAction.copyToClipboard( new Object[]
                        { ldif.toString() }, new Transfer[]
                        { TextTransfer.getInstance() } );

                    return true;
                }
                else if ( finishPage.getExecutionMethod() == BatchOperationFinishWizardPage.EXECUTION_METHOD_ON_CONNECTION )
                {
                    // Executing the LDIF on the connection
                    ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( getConnection(), ldif.toString(), true,
                        finishPage.getContinueOnError() );
                    StudioBrowserJob job = new StudioBrowserJob( runnable );
                    job.execute();

                    return true;
                }
            }

            return false;
        }

        return true;
    }


    /**
     * Gets the type of the page.
     *
     * @return the type of the page
     */
    public BatchOperationTypeWizardPage getTypePage()
    {
        return typePage;
    }


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return this.connection;
    }
}
