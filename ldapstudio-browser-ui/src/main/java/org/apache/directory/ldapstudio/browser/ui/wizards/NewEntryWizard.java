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
import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.internal.model.Bookmark;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.jobs.CreateEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;

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
import org.eclipse.ui.PlatformUI;


public class NewEntryWizard extends Wizard implements INewWizard, ModelModifier
{

    private NewEntryTypeWizardPage typePage;

    private NewEntryObjectclassWizardPage ocPage;

    private NewEntryDnWizardPage dnPage;

    private NewEntryAttributesWizardPage attributePage;

    private IEntry selectedEntry;

    private IConnection selectedConnection;

    private DummyEntry newEntry;


    public NewEntryWizard()
    {
        super.setWindowTitle( "New Entry" );
        super.setNeedsProgressMonitor( true );
    }


    public static String getId()
    {
        return NewEntryWizard.class.getName();
    }


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        Object o = selection.getFirstElement();
        if ( o instanceof IEntry )
        {
            this.selectedEntry = ( ( IEntry ) o );
            this.selectedConnection = this.selectedEntry.getConnection();
        }
        else if ( o instanceof ISearchResult )
        {
            this.selectedEntry = ( ( ISearchResult ) o ).getEntry();
            this.selectedConnection = this.selectedEntry.getConnection();
        }
        else if ( o instanceof Bookmark )
        {
            this.selectedEntry = ( ( Bookmark ) o ).getEntry();
            this.selectedConnection = this.selectedEntry.getConnection();
        }
        else if ( o instanceof IAttribute )
        {
            this.selectedEntry = ( ( IAttribute ) o ).getEntry();
            this.selectedConnection = this.selectedEntry.getConnection();
        }
        else if ( o instanceof IValue )
        {
            this.selectedEntry = ( ( IValue ) o ).getAttribute().getEntry();
            this.selectedConnection = this.selectedEntry.getConnection();
        }
        else if ( o instanceof ISearch )
        {
            this.selectedEntry = null;
            this.selectedConnection = ( ( ISearch ) o ).getConnection();
        }
        else if ( o instanceof IConnection )
        {
            this.selectedEntry = null;
            this.selectedConnection = ( IConnection ) o;
        }
        else
        {
            this.selectedEntry = null;
            this.selectedConnection = null;
        }

        if ( this.selectedConnection != null )
        {
            this.selectedConnection.suspend();
            this.newEntry = new DummyEntry( new DN(), this.selectedConnection );
        }
    }


    public void addPages()
    {
        if ( this.selectedConnection != null && this.selectedConnection.isOpened() )
        {
            typePage = new NewEntryTypeWizardPage( NewEntryTypeWizardPage.class.getName(), this );
            addPage( typePage );

            ocPage = new NewEntryObjectclassWizardPage( NewEntryObjectclassWizardPage.class.getName(), this );
            addPage( ocPage );

            dnPage = new NewEntryDnWizardPage( NewEntryDnWizardPage.class.getName(), this );
            addPage( dnPage );

            attributePage = new NewEntryAttributesWizardPage( NewEntryAttributesWizardPage.class.getName(), this );
            addPage( attributePage );
        }
        else
        {
            IWizardPage page = new DummyWizardPage();
            addPage( page );
        }

        PlatformUI.getWorkbench().getHelpSystem().setHelp( getContainer().getShell(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_newentry_wizard" );
    }

    class DummyWizardPage extends WizardPage
    {

        protected DummyWizardPage()
        {
            super( "" );
            super.setTitle( "No connection selected or connection is closed" );
            super.setDescription( "In order to use the entry creation wizard please select a opened connection." );
            super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
                BrowserUIConstants.IMG_ENTRY_WIZARD ) );
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
        if ( this.selectedConnection != null && this.selectedConnection.isOpened() )
        {
            EventRegistry.suspendEventFireingInCurrentThread();
            this.selectedConnection.reset();
            EventRegistry.resumeEventFireingInCurrentThread();
            this.selectedConnection.reset();
        }

        return true;
    }


    public boolean performFinish()
    {
        if ( this.selectedConnection != null && this.selectedConnection.isOpened() )
        {
            this.typePage.saveDialogSettings();
            this.ocPage.saveDialogSettings();
            this.dnPage.saveDialogSettings();

            this.getSelectedConnection().reset();

            CreateEntryJob job = new CreateEntryJob( new IEntry[]
                { this.getNewEntry() } );
            RunnableContextJobAdapter.execute( job, getContainer() );

            if ( !job.getExternalResult().isOK() )
            {
                this.getSelectedConnection().suspend();
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }


    public IEntry getSelectedEntry()
    {
        return selectedEntry;
    }


    public IConnection getSelectedConnection()
    {
        return selectedConnection;
    }


    public DummyEntry getNewEntry()
    {
        return newEntry;
    }


    public void setNewEntry( DummyEntry newEntry )
    {
        this.newEntry = newEntry;
    }

}
