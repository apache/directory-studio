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


import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetContentProvider;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetFilter;


public class EntryEditorConfiguration extends EntryEditorWidgetConfiguration
{

    public EntryEditorConfiguration()
    {
        super();
    }


    public void dispose()
    {
        super.dispose();
    }


    public EntryEditorWidgetContentProvider getContentProvider( EntryEditorWidget mainWidget )
    {
        if ( this.contentProvider == null )
            this.contentProvider = new EntryEditorContentProvider( this.getPreferences(), mainWidget );

        return contentProvider;
    }


    public EntryEditorWidgetFilter getFilter()
    {
        if ( this.filter == null )
            this.filter = new EntryEditorFilter( getPreferences() );

        return filter;
    }

}
