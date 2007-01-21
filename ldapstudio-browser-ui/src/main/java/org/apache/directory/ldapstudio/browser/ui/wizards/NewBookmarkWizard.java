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


import org.apache.directory.ldapstudio.browser.core.internal.model.Bookmark;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
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


public class NewBookmarkWizard extends Wizard implements INewWizard
{

    private NewBookmarkMainWizardPage mainPage;

    private IEntry selectedEntry;


    public NewBookmarkWizard()
    {
        super.setWindowTitle( "New Bookmark" );
        super.setNeedsProgressMonitor( false );
    }


    public static String getId()
    {
        return NewBookmarkWizard.class.getName();
    }


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        Object o = selection.getFirstElement();
        if ( o instanceof IEntry )
        {
            this.selectedEntry = ( ( IEntry ) o );
        }
        else if ( o instanceof ISearchResult )
        {
            this.selectedEntry = ( ( ISearchResult ) o ).getEntry();
        }
        else if ( o instanceof Bookmark )
        {
            this.selectedEntry = ( ( Bookmark ) o ).getEntry();
        }
        else if ( o instanceof IAttribute )
        {
            this.selectedEntry = ( ( IAttribute ) o ).getEntry();
        }
        else if ( o instanceof IValue )
        {
            this.selectedEntry = ( ( IValue ) o ).getAttribute().getEntry();
        }
        else
        {
            this.selectedEntry = null;
        }
    }


    public void addPages()
    {
        if ( this.selectedEntry != null )
        {
            mainPage = new NewBookmarkMainWizardPage( NewBookmarkMainWizardPage.class.getName(), this.selectedEntry,
                this );
            addPage( mainPage );
        }
        else
        {
            IWizardPage page = new DummyWizardPage();
            addPage( page );
        }
    }

    class DummyWizardPage extends WizardPage
    {

        protected DummyWizardPage()
        {
            super( "" );
            super.setTitle( "No entry selected" );
            super.setDescription( "In order to use the bookmark creation wizard please select an entry." );
            // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
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


    public boolean performCancel()
    {
        return true;
    }


    public boolean performFinish()
    {
        if ( selectedEntry != null )
        {
            String name = mainPage.getBookmarkName();
            DN dn = mainPage.getBookmarkDn();
            IBookmark bookmark = new Bookmark( selectedEntry.getConnection(), dn, name );
            selectedEntry.getConnection().getBookmarkManager().addBookmark( bookmark );
        }
        mainPage.saveDialogSettings();
        return true;
    }

}
