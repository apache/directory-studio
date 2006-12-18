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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.NameException;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;


public class EntryEditorNavigationLocation extends NavigationLocation
{

    protected EntryEditorNavigationLocation( EntryEditor editor )
    {
        super( editor );
    }


    public String getText()
    {
        IEntry entry = getEntry();
        if ( entry != null )
        {
            return entry.getDn().toString();
        }
        else
        {
            return super.getText();
        }
    }


    public void saveState( IMemento memento )
    {
        IEntry entry = getEntry();
        memento.putString( "DN", entry.getDn().toString() );
        memento.putString( "CONNECTION", entry.getConnection().getName() );
    }


    public void restoreState( IMemento memento )
    {
        try
        {
            IConnection connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection(
                memento.getString( "CONNECTION" ) );
            DN dn = new DN( memento.getString( "DN" ) );
            IEntry entry = connection.getEntryFromCache( dn );
            super.setInput( new EntryEditorInput( entry ) );
        }
        catch ( NameException e )
        {
            e.printStackTrace();
        }

    }


    public void restoreLocation()
    {
        IEditorPart editorPart = getEditorPart();
        if ( editorPart != null && editorPart instanceof EntryEditor )
        {
            EntryEditor entryEditor = ( EntryEditor ) editorPart;
            entryEditor.setInput( ( EntryEditorInput ) getInput() );
        }
    }


    public boolean mergeInto( INavigationLocation currentLocation )
    {
        return false;
    }


    public void update()
    {

    }


    private IEntry getEntry()
    {

        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof EntryEditorInput )
        {
            EntryEditorInput entryEditorInput = ( EntryEditorInput ) editorInput;
            IEntry entry = entryEditorInput.getEntry();
            if ( entry != null )
            {
                return entry;
            }
        }

        return null;
    }

}
