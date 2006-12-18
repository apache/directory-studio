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

package org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SchemaElementSelectionListener;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleUseDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;

import org.eclipse.ui.IWorkbenchPart;


public class HistoryManager implements SchemaElementSelectionListener
{

    private static final int HISTORY_LENGTH = 20;

    private final List history;

    private int currentPointer;

    private List historyListenerList;


    public HistoryManager( IWorkbenchPart part )
    {

        this.history = new ArrayList( HISTORY_LENGTH );
        for ( int i = 0; i < HISTORY_LENGTH; i++ )
        {
            history.add( i, null );
        }
        this.currentPointer = -1;
        this.historyListenerList = new ArrayList( 2 );

        EventRegistry.addSchemaElementSelectionListener( this );
    }


    public void addHistoryListener( HistoryListener l )
    {
        this.historyListenerList.add( l );
    }


    public void removeHistoryListener( HistoryListener l )
    {
        this.historyListenerList.remove( l );
    }


    public void back()
    {
        this.currentPointer--;
        SchemaBrowser.select( this.history.get( this.currentPointer ) );
    }


    public void forward()
    {
        this.currentPointer++;
        SchemaBrowser.select( this.history.get( this.currentPointer ) );
    }


    public boolean isBackPossible()
    {
        return this.currentPointer > 0 && this.history.get( this.currentPointer - 1 ) != null;
    }


    public boolean isForwardPossible()
    {
        return this.currentPointer > -1 && this.currentPointer + 1 < HISTORY_LENGTH
            && this.history.get( this.currentPointer + 1 ) != null;
    }


    public void attributeTypeDescriptionSelected( AttributeTypeDescription atd )
    {
        this.update( atd );
    }


    public void objectClassDescriptionSelected( ObjectClassDescription ocd )
    {
        this.update( ocd );
    }


    public void matchingRuleDescriptionSelected( MatchingRuleDescription mrd )
    {
        this.update( mrd );
    }


    public void ldapSyntacDescriptionSelected( LdapSyntaxDescription lsd )
    {
        this.update( lsd );
    }


    public void matchingRuleUseDescriptionSelected( MatchingRuleUseDescription mrud )
    {
        this.update( mrud );
    }


    private void update( Object obj )
    {
        if ( this.currentPointer < 0 || this.history.get( this.currentPointer ) != obj )
        {

            // clear previous backs
            if ( currentPointer > -1 && this.currentPointer + 1 < HISTORY_LENGTH )
            {
                for ( int i = this.currentPointer + 1; i < HISTORY_LENGTH; i++ )
                {
                    history.set( i, null );
                }
            }

            // check full history
            if ( this.currentPointer + 1 >= HISTORY_LENGTH )
            {
                history.remove( 0 );
                history.add( null );
                this.currentPointer--;
            }

            // update history
            this.currentPointer++;
            history.set( this.currentPointer, obj );

        }

        // call listeners
        for ( Iterator it = this.historyListenerList.iterator(); it.hasNext(); )
        {
            ( ( HistoryListener ) it.next() ).historyModified();
        }
    }


    public void dispose()
    {
        EventRegistry.removeSchemaElementSelectionListener( this );
    }

}
