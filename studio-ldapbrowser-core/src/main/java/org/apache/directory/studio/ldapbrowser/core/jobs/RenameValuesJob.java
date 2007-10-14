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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import java.util.HashSet;
import java.util.Set;

import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Attribute;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Value;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;


/**
 * Job to rename the attribute description of values asynchronously.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RenameValuesJob extends AbstractModificationJob
{

    /** The entry to modify. */
    private IEntry entry;

    /** The old values. */
    private IValue[] oldValues;

    /** The new attribute description. */
    private String newAttributeDescription;

    /** The created values. */
    private IValue[] createdValues;


    /**
     * Creates a new instance of RenameValuesJob.
     * 
     * @param entry the entry to modify
     * @param oldValues the old values
     * @param newAttributeDescription the new attribute description
     */
    public RenameValuesJob( IEntry entry, IValue[] oldValues, String newAttributeDescription )
    {
        this.entry = entry;
        this.oldValues = oldValues;
        this.newAttributeDescription = newAttributeDescription;

        setName( oldValues.length == 1 ? BrowserCoreMessages.jobs__rename_value_name_1
            : BrowserCoreMessages.jobs__rename_value_name_n );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractModificationJob#executeAsyncModificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeAsyncModificationJob( StudioProgressMonitor monitor ) throws ModelModificationException
    {
        monitor.beginTask( oldValues.length == 1 ? BrowserCoreMessages.jobs__rename_value_task_1
            : BrowserCoreMessages.jobs__rename_value_task_n, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        for ( IValue oldValue : oldValues )
        {
            if ( oldValue.getAttribute().getEntry() != entry )
            {
                return;
            }
        }

        IValue[] newValues = new IValue[oldValues.length];
        for ( int i = 0; i < oldValues.length; i++ )
        {
            IAttribute attribute = entry.getAttribute( newAttributeDescription );
            if ( attribute == null )
            {
                attribute = new Attribute( entry, newAttributeDescription );
            }

            newValues[i] = new Value( attribute, oldValues[i].getRawValue() );
        }

        CreateValuesJob.createValues( entry.getBrowserConnection(), entry, newValues, monitor );
        if ( !monitor.errorsReported() )
        {
            entry.getBrowserConnection().delete( oldValues, monitor );
        }
        if ( !monitor.errorsReported() )
        {
            createdValues = newValues;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractModificationJob#getModifiedEntry()
     */
    protected IEntry getModifiedEntry()
    {
        return entry;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractModificationJob#getAffectedAttributeNames()
     */
    protected String[] getAffectedAttributeNames()
    {
        Set<String> affectedAttributeNameSet = new HashSet<String>();
        affectedAttributeNameSet.add( newAttributeDescription );
        for ( IValue oldValue : oldValues )
        {
            affectedAttributeNameSet.add( oldValue.getAttribute().getDescription() );
        }
        return affectedAttributeNameSet.toArray( new String[affectedAttributeNameSet.size()] );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAsyncBulkJob#runNotification()
     */
    protected void runNotification()
    {
        EntryModificationEvent event;

        if ( createdValues != null && createdValues.length > 0 )
        {
            event = new ValueRenamedEvent( entry.getBrowserConnection(), entry, oldValues[0], createdValues[0] );
        }
        else
        {
            event = new AttributesInitializedEvent( entry );
        }

        EventRegistry.fireEntryUpdated( event, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return oldValues.length == 1 ? BrowserCoreMessages.jobs__rename_value_error_1
            : BrowserCoreMessages.jobs__rename_value_error_n;
    }

}
