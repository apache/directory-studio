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
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Attribute;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Value;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;


public class RenameValuesJob extends AbstractModificationJob
{

    private IEntry entry;

    private IValue[] oldValues;

    private String newAttributeName;

    private ValueRenamedEvent event;


    public RenameValuesJob( IEntry entry, IValue[] oldValues, String newAttributeName )
    {
        this.entry = entry;
        this.oldValues = oldValues;
        this.newAttributeName = newAttributeName;

        setName( oldValues.length == 1 ? BrowserCoreMessages.jobs__rename_value_name_1
            : BrowserCoreMessages.jobs__rename_value_name_n );
    }


    protected void executeAsyncModificationJob( StudioProgressMonitor monitor ) throws ModelModificationException
    {

        monitor.beginTask( oldValues.length == 1 ? BrowserCoreMessages.jobs__rename_value_task_1
            : BrowserCoreMessages.jobs__rename_value_task_n, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        for ( int i = 0; i < oldValues.length; i++ )
        {
            if ( oldValues[i].getAttribute().getEntry() != this.entry )
            {
                return;
            }
        }

        IValue[] newValues = new IValue[oldValues.length];
        for ( int i = 0; i < oldValues.length; i++ )
        {

            IAttribute newAttribute = entry.getAttribute( newAttributeName );
            if ( newAttribute == null )
            {
                newAttribute = new Attribute( entry, newAttributeName );
                entry.addAttribute( newAttribute );
            }

            newValues[i] = new Value( newAttribute, oldValues[i].getRawValue() );
            newAttribute.addValue( newValues[i] );

            oldValues[i].getAttribute().deleteValue( oldValues[i] );

            if ( this.event == null )
            {
                this.event = new ValueRenamedEvent( entry.getBrowserConnection(), entry, oldValues[0], newValues[0] );
            }
        }

        if ( !monitor.errorsReported() )
        {
            entry.getBrowserConnection().create( newValues, monitor );
        }
        if ( !monitor.errorsReported() )
        {
            entry.getBrowserConnection().delete( oldValues, monitor );
        }
    }


    protected IEntry getModifiedEntry()
    {
        return this.entry;
    }


    protected String[] getAffectedAttributeNames()
    {
        Set affectedAttributeNameSet = new HashSet();
        affectedAttributeNameSet.add( newAttributeName );
        for ( int i = 0; i < oldValues.length; i++ )
        {
            affectedAttributeNameSet.add( oldValues[i].getAttribute().getDescription() );
        }
        return ( String[] ) affectedAttributeNameSet.toArray( new String[affectedAttributeNameSet.size()] );
    }


    protected void runNotification()
    {
        if ( this.event != null )
        {
            EventRegistry.fireEntryUpdated( this.event, this );
        }
    }


    protected String getErrorMessage()
    {
        return oldValues.length == 1 ? BrowserCoreMessages.jobs__rename_value_error_1
            : BrowserCoreMessages.jobs__rename_value_error_n;
    }

}
