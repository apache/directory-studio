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

package org.apache.directory.studio.ldapbrowser.ui.dialogs.properties;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.SearchPageWrapper;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * The SearchPropertyPage implements the property page for an {@link ISearch}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchPropertyPage extends PropertyPage implements IWorkbenchPropertyPage, WidgetModifyListener
{

    /** The search. */
    private ISearch search;

    /** The search page wrapper. */
    private SearchPageWrapper spw;


    /**
     * Creates a new instance of SearchPropertyPage.
     */
    public SearchPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        spw.removeWidgetModifyListener( this );
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent,
            BrowserUIConstants.PLUGIN_ID + "." + "tools_search_properties" ); //$NON-NLS-1$ //$NON-NLS-2$

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

        super.setMessage( Messages.getString( "SearchPropertyPage.Search" ) + Utils.shorten( search.getName(), 30 ) ); //$NON-NLS-1$

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        spw = new SearchPageWrapper( SearchPageWrapper.CONNECTION_READONLY );
        spw.createContents( composite );
        spw.loadFromSearch( search );
        spw.addWidgetModifyListener( this );

        widgetModified( new WidgetModifyEvent( this ) );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        boolean modified = spw.saveToSearch( search );
        if ( modified && search.getBrowserConnection() != null )
        {
            // send update event to force saving of new search parameters.
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search,
                SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED ), this );

            return spw.performSearch( search );
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        setValid( spw.isValid() );
        setErrorMessage( spw.getErrorMessage() );
    }

}
