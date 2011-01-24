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


import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserCategory;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserEntryPage;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserSearchResultPage;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Bookmark;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * The NewConnectionWizard is used to create a new bookmark.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewBookmarkWizard extends Wizard implements INewWizard
{

    /** The main page. */
    private NewBookmarkMainWizardPage mainPage;

    /** The selected entry. */
    private IEntry selectedEntry;


    /**
     * Creates a new instance of NewBookmarkWizard.
     */
    public NewBookmarkWizard()
    {
        setWindowTitle( Messages.getString( "NewBookmarkWizard.NewBookmark" ) ); //$NON-NLS-1$
        setNeedsProgressMonitor( false );
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_NEW_BOOKMARK;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // determine the currently selected entry, used 
        // to preset the bookmark target Dn
        Object o = selection.getFirstElement();
        if ( o instanceof IEntry )
        {
            selectedEntry = ( ( IEntry ) o );
        }
        else if ( o instanceof ISearchResult )
        {
            selectedEntry = ( ( ISearchResult ) o ).getEntry();
        }
        else if ( o instanceof IBookmark )
        {
            selectedEntry = ( ( IBookmark ) o ).getEntry();
        }
        else if ( o instanceof IAttribute )
        {
            selectedEntry = ( ( IAttribute ) o ).getEntry();
        }
        else if ( o instanceof IValue )
        {
            selectedEntry = ( ( IValue ) o ).getAttribute().getEntry();
        }
        else if ( o instanceof IBrowserConnection )
        {
            selectedEntry = ( ( IBrowserConnection ) o ).getRootDSE();
        }
        else if ( o instanceof ISearch )
        {
            selectedEntry = ( ( ISearch ) o ).getBrowserConnection().getRootDSE();
        }
        else if ( o instanceof BrowserCategory )
        {
            selectedEntry = ( ( BrowserCategory ) o ).getParent().getRootDSE();
        }
        else if ( o instanceof BrowserSearchResultPage )
        {
            selectedEntry = ( ( BrowserSearchResultPage ) o ).getSearch().getBrowserConnection().getRootDSE();
        }
        else if ( o instanceof BrowserEntryPage )
        {
            selectedEntry = ( ( BrowserEntryPage ) o ).getEntry();
        }

        else
        {
            selectedEntry = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        if ( selectedEntry != null )
        {
            mainPage = new NewBookmarkMainWizardPage( NewBookmarkMainWizardPage.class.getName(), selectedEntry, this );
            addPage( mainPage );
        }
        else
        {
            IWizardPage page = new DummyWizardPage();
            addPage( page );
        }
    }

    /**
     * Just a dummy page.
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
            setTitle( Messages.getString( "NewBookmarkWizard.NoEntrySelected" ) ); //$NON-NLS-1$
            setDescription( Messages.getString( "NewBookmarkWizard.InOrderToUse" ) ); //$NON-NLS-1$
            // setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
            setPageComplete( true );
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
    public boolean performFinish()
    {
        if ( selectedEntry != null )
        {
            String name = mainPage.getBookmarkName();
            Dn dn = mainPage.getBookmarkDn();
            IBookmark bookmark = new Bookmark( selectedEntry.getBrowserConnection(), dn, name );
            selectedEntry.getBrowserConnection().getBookmarkManager().addBookmark( bookmark );
        }
        mainPage.saveDialogSettings();
        return true;
    }

}
