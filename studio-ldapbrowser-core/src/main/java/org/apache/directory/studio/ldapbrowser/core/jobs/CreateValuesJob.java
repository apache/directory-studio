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

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Attribute;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Value;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;


/**
 * Job to create values asynchronously.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CreateValuesJob extends AbstractAttributeModificationJob
{

    /** The entry to modify. */
    private IEntry entry;

    /** The values to create. */
    private IValue[] valuesToCreate;

    /** The created values. */
    private IValue[] createdValues;


    /**
     * Creates a new instance of CreateValuesJob.
     * 
     * @param entry the entry to modify
     * @param valuesToCreate the values to create
     */
    public CreateValuesJob( IEntry entry, IValue[] valuesToCreate )
    {
        this.entry = entry;
        this.valuesToCreate = valuesToCreate;

        setName( valuesToCreate.length == 1 ? BrowserCoreMessages.jobs__create_values_name_1
            : BrowserCoreMessages.jobs__create_values_name_n );
    }


    /**
     * Creates a new instance of CreateValuesJob.
     *
     * @param attribute the attribute to modify
     * @param newValue the new value
     */
    public CreateValuesJob( IAttribute attribute, Object newValue )
    {
        this( attribute.getEntry(), new IValue[]
            { new Value( attribute, newValue ) } );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#executeAttributeModificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeAttributeModificationJob( StudioProgressMonitor monitor ) throws ModelModificationException
    {
        monitor.beginTask( valuesToCreate.length == 1 ? BrowserCoreMessages.jobs__create_values_task_1
            : BrowserCoreMessages.jobs__create_values_task_n, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        IValue[] newValues = new IValue[valuesToCreate.length];
        for ( int i = 0; i < newValues.length; i++ )
        {
            IAttribute attribute = entry.getAttribute( valuesToCreate[i].getAttribute().getDescription() );
            if ( attribute == null )
            {
                attribute = new Attribute( entry, valuesToCreate[i].getAttribute().getDescription() );
            }

            newValues[i] = new Value( attribute, valuesToCreate[i].getRawValue() );
        }

        createValues( entry.getBrowserConnection(), entry, newValues, monitor );
        if ( !monitor.errorsReported() )
        {
            createdValues = newValues;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#getModifiedEntry()
     */
    protected IEntry getModifiedEntry()
    {
        return entry;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#getAffectedAttributeDescriptions()
     */
    protected String[] getAffectedAttributeDescriptions()
    {
        Set<String> attributeDescriptionSet = new HashSet<String>();
        for ( IValue value : valuesToCreate )
        {
            attributeDescriptionSet.add( value.getAttribute().getDescription() );
        }
        return attributeDescriptionSet.toArray( new String[attributeDescriptionSet.size()] );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        EntryModificationEvent event;

        if ( createdValues != null && createdValues.length > 0 )
        {
            event = new ValueAddedEvent( entry.getBrowserConnection(), entry, createdValues[0].getAttribute(),
                createdValues[0] );
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
        return valuesToCreate.length == 1 ? BrowserCoreMessages.jobs__create_values_error_1
            : BrowserCoreMessages.jobs__create_values_error_n;
    }


    /**
     * Creates the values using the underlying JNDI connection wrapper.
     * 
     * @param browserConnection the browser connection
     * @param entryToModify the entry to modify
     * @param valuesToCreate the values to create
     * @param monitor the progress monitor
     * 
     * @throws ModelModificationException
     */
    static void createValues( IBrowserConnection browserConnection, IEntry entryToModify, IValue[] valuesToCreate,
        StudioProgressMonitor monitor ) throws ModelModificationException
    {
        if ( browserConnection.getConnection() != null )
        {
            // dn
            String dn = entryToModify.getDn().toString();

            // modification items
            ModificationItem[] modificationItems = new ModificationItem[valuesToCreate.length];
            for ( int i = 0; i < modificationItems.length; i++ )
            {
                BasicAttribute attribute = new BasicAttribute( valuesToCreate[i].getAttribute().getDescription(),
                    valuesToCreate[i].getRawValue() );
                modificationItems[i] = new ModificationItem( DirContext.ADD_ATTRIBUTE, attribute );
            }

            // controls
            Control[] controls = null;
            if ( entryToModify.isReferral() )
            {
                controls = new Control[]
                    { new ManageReferralControl() };
            }

            browserConnection.getConnection().getJNDIConnectionWrapper().modifyAttributes( dn, modificationItems,
                controls, monitor );
        }
        else
        {
            for ( IValue value : valuesToCreate )
            {
                IAttribute attribute = entryToModify.getAttribute( value.getAttribute().getDescription() );
                if ( attribute == null )
                {
                    attribute = new Attribute( entryToModify, value.getAttribute().getDescription() );
                    entryToModify.addAttribute( attribute );
                }
                attribute.addValue( value );
            }
        }
    }

}
