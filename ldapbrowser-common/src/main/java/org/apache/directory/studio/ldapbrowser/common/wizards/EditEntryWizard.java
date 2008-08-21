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

package org.apache.directory.studio.ldapbrowser.common.wizards;


import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.eclipse.core.runtime.IStatus;


/**
 * The EditEntryWizard is used to edit an existing entry offline, on finish 
 * it computes the difference between the orignal entry and the changed entry 
 * and sends these changes to the server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EditEntryWizard extends NewEntryWizard
{

    /**
     * Creates a new instance of EditEntryWizard.
     * 
     * @param entry the entry to edit
     */
    public EditEntryWizard( IEntry entry )
    {
        setWindowTitle( "Edit Entry" );
        setNeedsProgressMonitor( true );

        selectedEntry = entry;
        selectedConnection = entry.getBrowserConnection();
        originalReadOnlyFlag = selectedConnection.getConnection().isReadOnly();
        selectedConnection.getConnection().setReadOnly( true );

        // ensure the attributes of the entry are initialized
        if ( !selectedEntry.isAttributesInitialized() )
        {
            initAttributes();
        }

        try
        {
            EventRegistry.suspendEventFireingInCurrentThread();
            LdifContentRecord record = ModelConverter.entryToLdifContentRecord( selectedEntry );
            prototypeEntry = ModelConverter.ldifContentRecordToEntry( record, selectedConnection );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            EventRegistry.resumeEventFireingInCurrentThread();
        }
    }


    private void initAttributes()
    {
        boolean soa = BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );
        InitializeAttributesRunnable iar = new InitializeAttributesRunnable( new IEntry[]
            { selectedEntry }, soa );
        RunnableContextRunner.execute( iar, getContainer(), true );
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        ocPage = new NewEntryObjectclassWizardPage( NewEntryObjectclassWizardPage.class.getName(), this );
        addPage( ocPage );

        attributePage = new NewEntryAttributesWizardPage( NewEntryAttributesWizardPage.class.getName(), this );
        addPage( attributePage );
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

                LdifChangeModifyRecord record = Utils.computeDiff( selectedEntry, prototypeEntry );
                if ( record != null )
                {
                    ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( selectedConnection, record
                        .toFormattedString( LdifFormatParameters.DEFAULT ), false );
                    IStatus status = RunnableContextRunner.execute( runnable, getContainer(), true );
                    if ( !status.isOK() )
                    {
                        selectedConnection.getConnection().setReadOnly( true );
                        return false;
                    }
                    else
                    {
                        initAttributes();
                        return true;
                    }
                }
                else
                {
                    // no changes
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
