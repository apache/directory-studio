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
import org.apache.directory.studio.ldapbrowser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserCategory;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserEntryPage;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserSearchResultPage;
import org.apache.directory.studio.ldapbrowser.core.jobs.CreateEntryJob;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
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


/**
 * The NewEntryWizard is used to create a new entry from scratch or by 
 * using another entry as template.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewEntryWizard extends Wizard implements INewWizard
{

    /** The type page. */
    private NewEntryTypeWizardPage typePage;

    /** The object class page. */
    private NewEntryObjectclassWizardPage ocPage;

    /** The dn page. */
    private NewEntryDnWizardPage dnPage;

    /** The attributes page. */
    private NewEntryAttributesWizardPage attributePage;

    /** The selected entry. */
    private IEntry selectedEntry;

    /** The selected connection. */
    private IBrowserConnection selectedConnection;

    /** The read only flag of the selected connection. */
    private boolean originalReadOnlyFlag;
    
    /** The prototype entry. */
    private DummyEntry prototypeEntry;


    /**
     * Creates a new instance of NewEntryWizard.
     */
    public NewEntryWizard()
    {
        setWindowTitle( "New Entry" );
        setNeedsProgressMonitor( true );
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return NewEntryWizard.class.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // determine the currently selected entry
        Object o = selection.getFirstElement();
        if ( o instanceof IEntry )
        {
            selectedEntry = ( ( IEntry ) o );
            selectedConnection = selectedEntry.getBrowserConnection();
        }
        else if ( o instanceof ISearchResult )
        {
            selectedEntry = ( ( ISearchResult ) o ).getEntry();
            selectedConnection = selectedEntry.getBrowserConnection();
        }
        else if ( o instanceof IBookmark )
        {
            selectedEntry = ( ( IBookmark ) o ).getEntry();
            selectedConnection = selectedEntry.getBrowserConnection();
        }
        else if ( o instanceof IAttribute )
        {
            selectedEntry = ( ( IAttribute ) o ).getEntry();
            selectedConnection = selectedEntry.getBrowserConnection();
        }
        else if ( o instanceof IValue )
        {
            selectedEntry = ( ( IValue ) o ).getAttribute().getEntry();
            selectedConnection = selectedEntry.getBrowserConnection();
        }
        else if ( o instanceof ISearch )
        {
            selectedEntry = null;
            selectedConnection = ( ( ISearch ) o ).getBrowserConnection();
        }
        else if ( o instanceof IBrowserConnection )
        {
            selectedEntry = null;
            selectedConnection = ( IBrowserConnection ) o;
        }
        else if ( o instanceof BrowserCategory )
        {
            selectedEntry = null;
            selectedConnection = ( ( BrowserCategory ) o ).getParent();
        }
        else if ( o instanceof BrowserSearchResultPage )
        {
            selectedEntry = null;
            selectedConnection = ( ( BrowserSearchResultPage ) o ).getSearch().getBrowserConnection();
        }
        else if ( o instanceof BrowserEntryPage )
        {
            selectedEntry = null;
            selectedConnection = ( ( BrowserEntryPage ) o ).getEntry().getBrowserConnection();
        }
        else
        {
            selectedEntry = null;
            selectedConnection = null;
        }

        if ( selectedConnection != null )
        {
            originalReadOnlyFlag = selectedConnection.getConnection().isReadOnly();
            selectedConnection.getConnection().setReadOnly( true );
            prototypeEntry = new DummyEntry( new LdapDN(), selectedConnection );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        if ( selectedConnection != null )
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
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        if ( selectedConnection != null )
        {
            PlatformUI.getWorkbench().getHelpSystem().setHelp( typePage.getControl(),
                BrowserUIPlugin.PLUGIN_ID + "." + "tools_newentry_wizard" );
            PlatformUI.getWorkbench().getHelpSystem().setHelp( ocPage.getControl(),
                BrowserUIPlugin.PLUGIN_ID + "." + "tools_newentry_wizard" );
            PlatformUI.getWorkbench().getHelpSystem().setHelp( dnPage.getControl(),
                BrowserUIPlugin.PLUGIN_ID + "." + "tools_newentry_wizard" );
            PlatformUI.getWorkbench().getHelpSystem().setHelp( attributePage.getControl(),
                BrowserUIPlugin.PLUGIN_ID + "." + "tools_newentry_wizard" );
        }
    }

    /**
     * Just a dummy page.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class DummyWizardPage extends WizardPage
    {

        /**
         * Creates a new instance of DummyWizardPage.
         */
        protected DummyWizardPage()
        {
            super( "" );
            setTitle( "No connection selected or connection is closed" );
            setDescription( "In order to use the entry creation wizard please select an entry or connection." );
            setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
                BrowserUIConstants.IMG_ENTRY_WIZARD ) );
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
    public boolean performCancel()
    {
        if ( selectedConnection != null && selectedConnection.getConnection() != null )
        {
            selectedConnection.getConnection().setReadOnly( originalReadOnlyFlag );
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        try
        {
            if ( selectedConnection != null && selectedConnection.getConnection() != null )
            {
                selectedConnection.getConnection().setReadOnly( originalReadOnlyFlag );
                
                typePage.saveDialogSettings();
                ocPage.saveDialogSettings();
                dnPage.saveDialogSettings();

                CreateEntryJob job = new CreateEntryJob( prototypeEntry, selectedConnection );
                RunnableContextJobAdapter.execute( job, getContainer() );

                if ( !job.getExternalResult().isOK() )
                {
                    selectedConnection.getConnection().setReadOnly( true );
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
        catch ( Throwable t )
        {
            t.printStackTrace();
            return false;
        }
    }


    /**
     * Gets the selected entry.
     * 
     * @return the selected entry
     */
    public IEntry getSelectedEntry()
    {
        return selectedEntry;
    }


    /**
     * Gets the selected connection.
     * 
     * @return the selected connection
     */
    public IBrowserConnection getSelectedConnection()
    {
        return selectedConnection;
    }


    /**
     * Gets the prototype entry.
     * 
     * @return the prototype entry
     */
    public DummyEntry getPrototypeEntry()
    {
        return prototypeEntry;
    }


    /**
     * Sets the prototype entry.
     * 
     * @param getPrototypeEntry the prototype entry
     */
    public void setPrototypeEntry( DummyEntry getPrototypeEntry )
    {
        this.prototypeEntry = getPrototypeEntry;
    }

}
