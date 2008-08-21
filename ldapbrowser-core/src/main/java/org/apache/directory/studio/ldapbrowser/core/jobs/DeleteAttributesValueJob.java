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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * Job to delete attributes and values from an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteAttributesValueJob extends AbstractAttributeModificationJob
{

    /** The entry. */
    private IEntry entry;

    /** The attributes to delete. */
    private IAttribute[] attributesToDelete;

    /** The values to delete. */
    private IValue[] valuesToDelete;

    /** The deleted attributes. */
    private IAttribute[] deletedAttributes;

    /** The deleted values. */
    private IValue[] deletedValues;


    /**
     * Creates a new instance of DeleteAttributesValueJob.
     * 
     * @param attributesToDelete the attributes to delete
     * @param valuesToDelete the values to delete
     */
    public DeleteAttributesValueJob( IAttribute attributesToDelete[], IValue[] valuesToDelete )
    {
        this.attributesToDelete = attributesToDelete;
        this.valuesToDelete = valuesToDelete;
        for ( int i = 0; attributesToDelete != null && i < attributesToDelete.length; i++ )
        {
            if ( this.entry == null )
            {
                this.entry = attributesToDelete[i].getEntry();
            }
        }
        for ( int i = 0; valuesToDelete != null && i < valuesToDelete.length; i++ )
        {
            if ( this.entry == null )
            {
                this.entry = valuesToDelete[i].getAttribute().getEntry();
            }
        }

        setName( attributesToDelete.length + valuesToDelete.length == 1 ? BrowserCoreMessages.jobs__delete_attributes_name_1
            : BrowserCoreMessages.jobs__delete_attributes_name_n );
    }


    /**
     * Creates a new instance of DeleteAttributesValueJob.
     * 
     * @param attributeHierarchyToDelete the attribute hierarchy to delete
     */
    public DeleteAttributesValueJob( AttributeHierarchy attributeHierarchyToDelete )
    {
        this( attributeHierarchyToDelete.getAttributes(), new IValue[0] );
    }


    /**
     * Creates a new instance of DeleteAttributesValueJob.
     * 
     * @param valueToDelete the value to delete
     */
    public DeleteAttributesValueJob( IValue valueToDelete )
    {
        this( new IAttribute[0], new IValue[]
            { valueToDelete } );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#executeAttributeModificationJob(org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor)
     */
    protected void executeAttributeModificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask(
            attributesToDelete.length + valuesToDelete.length == 1 ? BrowserCoreMessages.jobs__delete_attributes_task_1
                : BrowserCoreMessages.jobs__delete_attributes_task_n, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        deleteAttributesAndValues( entry.getBrowserConnection(), entry, attributesToDelete, valuesToDelete, monitor );

        if ( !monitor.errorsReported() )
        {
            deletedValues = valuesToDelete;
            deletedAttributes = attributesToDelete;
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
        Set<String> affectedAttributeNameSet = new HashSet<String>();
        for ( int i = 0; i < attributesToDelete.length; i++ )
        {
            affectedAttributeNameSet.add( attributesToDelete[i].getDescription() );
        }
        for ( int i = 0; i < valuesToDelete.length; i++ )
        {
            affectedAttributeNameSet.add( valuesToDelete[i].getAttribute().getDescription() );
        }
        return affectedAttributeNameSet.toArray( new String[affectedAttributeNameSet.size()] );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        EntryModificationEvent event;

        if ( deletedValues != null && deletedValues.length > 0 )
        {
            event = new ValueDeletedEvent( entry.getBrowserConnection(), entry, deletedValues[0].getAttribute(),
                deletedValues[0] );
        }
        else if ( deletedAttributes != null && deletedAttributes.length > 0 )
        {
            event = new AttributeDeletedEvent( entry.getBrowserConnection(), entry, deletedAttributes[0] );
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
        return attributesToDelete.length + valuesToDelete.length == 1 ? BrowserCoreMessages.jobs__delete_attributes_error_1
            : BrowserCoreMessages.jobs__delete_attributes_error_n;
    }


    /**
     * Delete attributes and values.
     * 
     * @param browserConnection the browser connection
     * @param entry the entry
     * @param attributesToDelete the attributes to delete
     * @param valuesToDelete the values to delete
     * @param monitor the progress monitor
     */
    static void deleteAttributesAndValues( IBrowserConnection browserConnection, IEntry entry,
        IAttribute[] attributesToDelete, IValue[] valuesToDelete, StudioProgressMonitor monitor )
    {
        if ( browserConnection.getConnection() != null && !browserConnection.getConnection().isReadOnly() )
        {
            // dn
            String dn = entry.getDn().getUpName();

            // modification items
            List<ModificationItem> modificationItems = new ArrayList<ModificationItem>();
            if ( attributesToDelete != null )
            {
                for ( IAttribute attribute : attributesToDelete )
                {
                    BasicAttribute ba = new BasicAttribute( attribute.getDescription() );
                    ModificationItem modificationItem = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, ba );
                    modificationItems.add( modificationItem );
                }
            }
            if ( valuesToDelete != null )
            {
                for ( IValue value : valuesToDelete )
                {
                    BasicAttribute ba = new BasicAttribute( value.getAttribute().getDescription(), value.getRawValue() );
                    ModificationItem modificationItem = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, ba );
                    modificationItems.add( modificationItem );
                }
            }

            // determine referrals handling method
            ReferralHandlingMethod referralsHandlingMethod = entry.isReferral() ? ReferralHandlingMethod.MANAGE
                : ReferralHandlingMethod.FOLLOW;

            browserConnection.getConnection().getJNDIConnectionWrapper().modifyEntry( dn,
                modificationItems.toArray( new ModificationItem[modificationItems.size()] ), referralsHandlingMethod,
                null, monitor, null );
        }
        else
        {
            if ( attributesToDelete != null )
            {
                for ( IAttribute attribute : attributesToDelete )
                {
                    attribute.getEntry().deleteAttribute( attribute );
                }
            }
            if ( valuesToDelete != null )
            {
                for ( IValue value : valuesToDelete )
                {
                    value.getAttribute().deleteValue( value );
                }
            }
        }
    }

}
