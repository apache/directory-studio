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


import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;


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
        setWindowTitle( Messages.getString( "EditEntryWizard.EditEntry" ) ); //$NON-NLS-1$
        setNeedsProgressMonitor( true );

        selectedEntry = entry;
        selectedConnection = entry.getBrowserConnection();

        try
        {
            EventRegistry.suspendEventFiringInCurrentThread();
            LdifContentRecord record = ModelConverter.entryToLdifContentRecord( selectedEntry );
            prototypeEntry = ModelConverter.ldifContentRecordToEntry( record, selectedConnection );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            EventRegistry.resumeEventFiringInCurrentThread();
        }
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
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        new CompoundModification().replaceAttributes( prototypeEntry, selectedEntry );
        return true;
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
