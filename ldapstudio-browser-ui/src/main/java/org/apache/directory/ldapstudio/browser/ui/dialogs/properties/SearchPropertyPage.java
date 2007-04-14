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

package org.apache.directory.ldapstudio.browser.ui.dialogs.properties;


import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.common.widgets.search.SearchPageWrapper;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.internal.model.Search;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class SearchPropertyPage extends PropertyPage implements IWorkbenchPropertyPage, WidgetModifyListener
{

    private ISearch search;

    private SearchPageWrapper spw;


    public SearchPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    public void dispose()
    {
        this.spw.removeWidgetModifyListener( this );
        super.dispose();
    }


    protected Control createContents( Composite parent )
    {

        // declare search
        ISearch search = ( ISearch ) getElement();
        if ( search != null )
        {
            this.search = search;
        }
        else
        {
            this.search = new Search();
        }

        super.setMessage( "Search " + Utils.shorten( search.getName(), 30 ) );

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        this.spw = new SearchPageWrapper( SearchPageWrapper.CONNECTION_READONLY );
        this.spw.createContents( composite );
        this.spw.loadFromSearch( this.search );
        this.spw.addWidgetModifyListener( this );

        return composite;
    }


    public boolean performOk()
    {
        boolean modified = this.spw.saveToSearch( this.search );
        if ( modified && this.search.getConnection() != null && this.search.getConnection().isOpened() )
        {
            // send update event to force saving of new search parameters.
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( this.search,
                SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED ), this );

            return this.spw.performSearch( this.search );
        }

        return true;
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        setValid( this.spw.isValid() );
    }

}
