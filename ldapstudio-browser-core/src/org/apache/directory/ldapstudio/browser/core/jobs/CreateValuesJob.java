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

package org.apache.directory.ldapstudio.browser.core.jobs;


import java.util.HashSet;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.ValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.Value;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;


public class CreateValuesJob extends AbstractModificationJob
{

    private IEntry entry;

    private String[] attributeDescriptions;

    private Object[] rawValues;

    private ValueAddedEvent event;


    public CreateValuesJob( IEntry entry, String[] attributeDescriptions, Object[] rawValues )
    {
        this.entry = entry;
        this.attributeDescriptions = attributeDescriptions;
        this.rawValues = rawValues;

        setName( rawValues.length == 1 ? BrowserCoreMessages.jobs__create_values_name_1
            : BrowserCoreMessages.jobs__create_values_name_n );
    }


    public CreateValuesJob( IAttribute attribute, Object newValue )
    {
        this( attribute.getEntry(), new String[]
            { attribute.getDescription() }, new Object[]
            { newValue } );
    }


    protected void executeAsyncModificationJob( ExtendedProgressMonitor monitor ) throws ModelModificationException
    {

        monitor.beginTask( rawValues.length == 1 ? BrowserCoreMessages.jobs__create_values_task_1
            : BrowserCoreMessages.jobs__create_values_task_n, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        IValue[] newValues = new IValue[rawValues.length];
        for ( int i = 0; i < newValues.length; i++ )
        {
            IAttribute attribute = entry.getAttribute( attributeDescriptions[i] );
            if ( attribute == null )
            {
                // String[] possibleAttributeNames =
                // entry.getSubschema().getAllAttributeNames();
                // if(!Arrays.asList(possibleAttributeNames).contains(attributeNames[i]))
                // {
                // throw new ModelModificationException("Attribute
                // "+attributeNames[i]+" is not in subschema");
                // }
                attribute = new Attribute( entry, attributeDescriptions[i] );
                entry.addAttribute( attribute, this );
            }

            newValues[i] = new Value( attribute, rawValues[i] );
            attribute.addValue( newValues[i], this );

            if ( this.event == null )
            {
                event = new ValueAddedEvent( entry.getConnection(), entry, attribute, newValues[i], this );
            }
        }

        entry.getConnection().create( newValues, monitor );
    }


    protected IEntry getModifiedEntry()
    {
        return entry;
    }


    protected String[] getAffectedAttributeNames()
    {
        Set affectedAttributeNameSet = new HashSet();
        for ( int i = 0; i < attributeDescriptions.length; i++ )
        {
            affectedAttributeNameSet.add( attributeDescriptions[i] );
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
        return attributeDescriptions.length == 1 ? BrowserCoreMessages.jobs__create_values_error_1
            : BrowserCoreMessages.jobs__create_values_error_n;
    }

}
