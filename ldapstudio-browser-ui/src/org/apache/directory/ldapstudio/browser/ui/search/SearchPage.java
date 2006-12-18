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

package org.apache.directory.ldapstudio.browser.ui.search;


import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.SearchPageWrapper;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


public class SearchPage extends DialogPage implements ISearchPage, WidgetModifyListener
{

    private ISearchPageContainer container;

    private ISearch search;

    private SearchPageWrapper spw;


    public static String getId()
    {
        return SearchPage.class.getName();
    }


    public void dispose()
    {
        this.spw.removeWidgetModifyListener( this );
        super.dispose();
    }


    public SearchPage()
    {
        super();
    }


    public SearchPage( String title )
    {
        super( title );
    }


    public SearchPage( String title, ImageDescriptor image )
    {
        super( title, image );
    }


    public boolean performAction()
    {
        this.spw.saveToSearch( this.search );
        if ( this.search.getConnection() != null )
        {
            this.search.getConnection().getSearchManager().addSearch( this.search );
            return this.spw.performSearch( this.search );
        }

        return false;
    }


    public void setContainer( ISearchPageContainer container )
    {
        this.container = container;
    }


    public void createControl( Composite parent )
    {

        // declare search
        this.search = SelectionUtils.getExampleSearch( this.container.getSelection() );

        // create search page content
        GridLayout gl = new GridLayout();
        parent.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        // gd.heightHint =
        // convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        parent.setLayoutData( gd );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_search_dialog" );

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        this.spw = new SearchPageWrapper( SearchPageWrapper.NONE );
        this.spw.createContents( composite );
        this.spw.loadFromSearch( this.search );
        this.spw.addWidgetModifyListener( this );

        super.setControl( parent );
    }


    public void setVisible( boolean visible )
    {
        this.container.setPerformActionEnabled( this.spw.isValid() );
        super.setVisible( visible );
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        this.container.setPerformActionEnabled( this.spw.isValid() );
    }

}
