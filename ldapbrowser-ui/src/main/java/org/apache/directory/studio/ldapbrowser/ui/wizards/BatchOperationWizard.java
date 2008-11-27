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
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;


public class BatchOperationWizard extends Wizard implements INewWizard
{

    private IBrowserConnection connection;

    private BatchOperationApplyOnWizardPage applyOnPage;

    private BatchOperationTypeWizardPage typePage;

    private BatchOperationLdifWizardPage ldifPage;

    private BatchOperationModifyWizardPage modifyPage;

    private BatchOperationFinishWizardPage finishPage;


    public BatchOperationWizard()
    {
        super.setWindowTitle( Messages.getString( "BatchOperationWizard.BatchOperation" ) ); //$NON-NLS-1$
        super.setNeedsProgressMonitor( true );
    }


    public static String getId()
    {
        return BrowserUIConstants.WIZARD_BATCH_OPERATION;
    }


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()
    }


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
            && connections[0].getJNDIConnectionWrapper().isConnected()
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

            finishPage = new BatchOperationFinishWizardPage( BatchOperationFinishWizardPage.class.getName(), this );
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

    class DummyWizardPage extends WizardPage
    {

        protected DummyWizardPage()
        {
            super( "" ); //$NON-NLS-1$
            super.setTitle( Messages.getString( "BatchOperationWizard.NoConnectionSelected" ) ); //$NON-NLS-1$
            super.setDescription( Messages.getString( "BatchOperationWizard.SelectOPenCollection" ) ); //$NON-NLS-1$
            // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ENTRY_WIZARD));
            super.setPageComplete( true );
        }


        public void createControl( Composite parent )
        {
            Composite composite = new Composite( parent, SWT.NONE );
            GridLayout gl = new GridLayout( 1, false );
            composite.setLayout( gl );
            composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

            setControl( composite );
        }
    }


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


    public boolean performCancel()
    {
        return true;
    }


    public boolean performFinish()
    {

        if ( this.applyOnPage != null )
        {

            this.applyOnPage.saveDialogSettings();

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
            LdapDN[] dns = applyOnPage.getApplyOnDns();
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
                            dns = new LdapDN[srs.length];
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
                    ldif.append( dns[i].getUpName() );
                    ldif.append( BrowserCoreConstants.LINE_SEPARATOR );
                    ldif.append( ldifFragment );
                    ldif.append( BrowserCoreConstants.LINE_SEPARATOR );
                }

                if ( finishPage.getExecutionMethod() == BatchOperationFinishWizardPage.EXECUTION_METHOD_LDIF )
                {

                    IEditorInput input = new NonExistingLdifEditorInput();
                    String editorId = LdifEditor.getId();

                    try
                    {
                        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        IWorkbenchPage page = window.getActivePage();
                        IEditorPart editor = page.openEditor( input, editorId );
                        IDocumentProvider documentProvider = ( ( LdifEditor ) editor ).getDocumentProvider();
                        if ( documentProvider != null && input != null )
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
                else if ( finishPage.getExecutionMethod() == BatchOperationFinishWizardPage.EXECUTION_METHOD_ONLINE )
                {
                    // TODO
                }
            }

            return false;
        }

        return true;
    }


    public BatchOperationTypeWizardPage getTypePage()
    {
        return typePage;
    }


    public IBrowserConnection getConnection()
    {
        return this.connection;
    }

}
