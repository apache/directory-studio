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

package org.apache.directory.ldapstudio.browser.ui.views.modificationlogs;


import java.io.File;
import java.io.FileReader;

import org.apache.directory.ldapstudio.browser.core.events.AttributesInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ChildrenInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;


public class ModificationLogsViewUniversalListener implements EntryUpdateListener
{

    private ModificationLogsView view;

    ModificationLogsViewInput input;


    public ModificationLogsViewUniversalListener( ModificationLogsView view )
    {
        this.view = view;

        this.input = null;
        EventRegistry.addEntryUpdateListener( this );
    }


    public void dispose()
    {
        if ( this.view != null )
        {
            EventRegistry.removeEntryUpdateListener( this );
            this.view = null;
        }
    }


    void refreshInput()
    {
        ModificationLogsViewInput input = this.input;
        this.input = null;
        setInput( input );
    }


    void setInput( ModificationLogsViewInput input )
    {

        // only if another connection is selected
        if ( this.input != input )
        {

            this.input = input;

            // load file %u %g
            StringBuffer sb = new StringBuffer();
            File[] files = input.connection.getModificationLogger().getFiles();
            int i = input.index;
            if ( 0 <= i && i < files.length && files[i] != null && files[i].exists() && files[i].canRead() )
            {
                try
                {
                    FileReader fr = new FileReader( files[i] );
                    char[] cbuf = new char[4096];
                    for ( int length = fr.read( cbuf ); length > 0; length = fr.read( cbuf ) )
                    {
                        sb.append( cbuf, 0, length );
                    }
                }
                catch ( Exception e )
                {
                    sb.append( e.getMessage() );
                }
            }

            // change input
            this.view.getMainWidget().getSourceViewer().getDocument().set( sb.toString() );
            this.view.getActionGroup().setInput( input );

        }
    }


    public void entryUpdated( EntryModificationEvent event )
    {
        if ( !( event instanceof AttributesInitializedEvent ) && !( event instanceof ChildrenInitializedEvent ) )
        {
            refreshInput();
            scrollToNewest();
        }
    }


    public void scrollToOldest()
    {
        this.view.getMainWidget().getSourceViewer().setTopIndex( 0 );
    }


    public void scrollToNewest()
    {
        try
        {
            LdifContainer record = this.view.getMainWidget().getLdifModel().getLastContainer();
            int offset = record.getOffset();
            int line = this.view.getMainWidget().getSourceViewer().getDocument().getLineOfOffset( offset );
            if ( line > 3 )
                line -= 3;
            this.view.getMainWidget().getSourceViewer().setTopIndex( line );
        }
        catch ( Exception e )
        {
        }

    }

}
