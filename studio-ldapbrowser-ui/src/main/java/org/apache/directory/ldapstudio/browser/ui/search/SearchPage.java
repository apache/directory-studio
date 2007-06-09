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


import org.apache.directory.ldapstudio.browser.common.actions.SelectionUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.common.widgets.search.SearchPageWrapper;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the {@link ISearchPage} to perform an LDAP search.
 * It uses the {@link SearchPageWrapper} to render all UI elements. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchPage extends DialogPage implements ISearchPage, WidgetModifyListener
{

    /** The search page container. */
    private ISearchPageContainer container;

    /** The search. */
    private ISearch search;

    /** The search page wrapper. */
    private SearchPageWrapper spw;

    /** The error message label. */
    private Label errorMessageLabel;


    /**
     * Gets the ID of the LDAP search page.
     * 
     * @return the ID of the LDAP search page
     */
    public static String getId()
    {
        return SearchPage.class.getName();
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
     * Creates a new instance of SearchPage.
     */
    public SearchPage()
    {
    }


    /**
     * Creates a new instance of SearchPage.
     * 
     * @param title the title
     */
    public SearchPage( String title )
    {
        super( title );
    }


    /**
     * Creates a new instance of SearchPage.
     * 
     * @param title the title
     * @param image the image
     */
    public SearchPage( String title, ImageDescriptor image )
    {
        super( title, image );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performAction()
    {
        spw.saveToSearch( search );
        if ( search.getConnection() != null )
        {
            search.getConnection().getSearchManager().addSearch( search );
            return spw.performSearch( search );
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void setContainer( ISearchPageContainer container )
    {
        this.container = container;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // declare search
        search = SelectionUtils.getExampleSearch( container.getSelection() );

        // create search page content
        GridLayout gl = new GridLayout();
        parent.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        // gd.heightHint =
        // convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        parent.setLayoutData( gd );

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        spw = new SearchPageWrapper( SearchPageWrapper.NONE );
        spw.createContents( composite );
        spw.loadFromSearch( search );
        spw.addWidgetModifyListener( this );

        errorMessageLabel = BaseWidgetUtils.createLabel( parent, "", 3 );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( composite,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_search_dialog" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_search_dialog" );

        super.setControl( parent );
    }


    /**
     * {@inheritDoc}
     */
    public void setVisible( boolean visible )
    {
        container.setPerformActionEnabled( spw.isValid() );
        super.setVisible( visible );
    }


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        container.setPerformActionEnabled( spw.isValid() );

        setErrorMessage( spw.getErrorMessage() );
        errorMessageLabel.setText( getErrorMessage() != null ? getErrorMessage() : "" );
    }

}
