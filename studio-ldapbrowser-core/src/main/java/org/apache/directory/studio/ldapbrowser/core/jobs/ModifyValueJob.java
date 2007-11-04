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
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;


/**
 * Job to modify an existing value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModifyValueJob extends AbstractAttributeModificationJob
{

    /** The attribute to modify. */
    private IAttribute attribute;

    /** The old value. */
    private IValue oldValue;

    /** The new raw value. */
    private Object newRawValue;

    /** The created new value. */
    private IValue createdNewValue;


    /**
     * Creates a new instance of ModifyValueJob.
     * 
     * @param oldValue the old value
     * @param newRawValue the new raw value
     */
    public ModifyValueJob( IValue oldValue, Object newRawValue )
    {
        this.attribute = oldValue.getAttribute();
        this.oldValue = oldValue;
        this.newRawValue = newRawValue;
        setName( BrowserCoreMessages.jobs__modify_value_name );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#executeAttributeModificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeAttributeModificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__modify_value_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        IValue newValue = new Value( attribute, newRawValue );

        modifyValue( attribute.getEntry().getBrowserConnection(), attribute.getEntry(), oldValue, newValue, monitor );
        if ( !monitor.errorsReported() )
        {
            createdNewValue = newValue;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#getModifiedEntry()
     */
    protected IEntry getModifiedEntry()
    {
        return attribute.getEntry();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAttributeModificationJob#getAffectedAttributeDescriptions()
     */
    protected String[] getAffectedAttributeDescriptions()
    {
        return new String[]
            { attribute.getDescription() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        EntryModificationEvent event;

        if ( createdNewValue != null )
        {
            event = new ValueModifiedEvent( attribute.getEntry().getBrowserConnection(), attribute.getEntry(),
                attribute, oldValue, createdNewValue );
        }
        else
        {
            event = new AttributesInitializedEvent( attribute.getEntry() );
        }

        EventRegistry.fireEntryUpdated( event, this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__modify_value_error;
    }


    /**
     * Modifies the value.
     * 
     * @param browserConnection the browser connection
     * @param entry the entry
     * @param oldValue the old value
     * @param newValue the new value
     * @param monitor the progress monitor
     */
    private void modifyValue( IBrowserConnection browserConnection, IEntry entry, IValue oldValue, IValue newValue,
        StudioProgressMonitor monitor )
    {
        if ( browserConnection.getConnection() != null )
        {
            // dn
            String dn = entry.getDn().getUpName();

            // modification items
            // perform a replace if the current attribute is single-valued
            // perform an add and a remove operation if the current attribute is multi-valued
            ModificationItem[] modificationItems;
            if ( oldValue.getAttribute().getValueSize() == 1 )
            {
                modificationItems = new ModificationItem[1];
                BasicAttribute attribute = new BasicAttribute( newValue.getAttribute().getDescription(), newValue
                    .getRawValue() );
                modificationItems[0] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attribute );
            }
            else
            {
                modificationItems = new ModificationItem[2];
                BasicAttribute newAttribute = new BasicAttribute( newValue.getAttribute().getDescription(), newValue
                    .getRawValue() );
                modificationItems[0] = new ModificationItem( DirContext.ADD_ATTRIBUTE, newAttribute );
                BasicAttribute oldAttribute = new BasicAttribute( oldValue.getAttribute().getDescription(), oldValue
                    .getRawValue() );
                modificationItems[1] = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, oldAttribute );
            }

            // controls
            Control[] controls = null;
            if ( entry.isReferral() )
            {
                controls = new Control[]
                    { new ManageReferralControl() };
            }

            browserConnection.getConnection().getJNDIConnectionWrapper().modifyAttributes( dn, modificationItems,
                controls, monitor );
        }
        else
        {
            oldValue.getAttribute().modifyValue( oldValue, newValue );
        }
    }

}
