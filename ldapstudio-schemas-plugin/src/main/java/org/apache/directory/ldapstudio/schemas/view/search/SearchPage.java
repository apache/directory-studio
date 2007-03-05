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

package org.apache.directory.ldapstudio.schemas.view.search;


import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.view.viewers.SearchView;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Search Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchPage extends DialogPage implements ISearchPage
{
    private ISearchPageContainer container;

    // UI Fields
    private Combo searchCombo;
    private Button allMetadataButton;
    private Button nameButton;
    private Button oidButton;
    private Button descriptionButon;


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        parent.setLayout( new GridLayout() );

        Label label = new Label( parent, SWT.NONE );
        label.setText( "Search string (* = any string, ? = any character):" );
        label.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        searchCombo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        searchCombo.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        searchCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent arg0 )
            {
                validate();
            }
        } );

        Group scopeGroup = new Group( parent, SWT.NONE );
        scopeGroup.setLayout( new GridLayout( 4, false ) );
        scopeGroup.setText( "Scope" );
        scopeGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        allMetadataButton = new Button( scopeGroup, SWT.RADIO );
        allMetadataButton.setText( "All metadata" );
        allMetadataButton.setSelection( true );

        nameButton = new Button( scopeGroup, SWT.RADIO );
        nameButton.setText( "Name" );

        oidButton = new Button( scopeGroup, SWT.RADIO );
        oidButton.setText( "OID" );

        descriptionButon = new Button( scopeGroup, SWT.RADIO );
        descriptionButon.setText( "Description" );

        initSearchHistory();

        super.setControl( parent );
    }


    /**
     * Initializes the Search History.
     */
    private void initSearchHistory()
    {
        searchCombo.setItems( SearchView.loadHistory( PluginConstants.PREFS_SEARCH_VIEW_SEARCH_HISTORY ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchPage#performAction()
     */
    public boolean performAction()
    {
        SearchView searchView = ( SearchView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( SearchView.ID );

        String scope = null;

        if ( allMetadataButton.getSelection() )
        {
            scope = SearchView.SEARCH_ALL;
        }
        else if ( nameButton.getSelection() )
        {
            scope = SearchView.SEARCH_NAME;
        }
        else if ( oidButton.getSelection() )
        {
            scope = SearchView.SEARCH_OID;
        }
        else if ( descriptionButon.getSelection() )
        {
            scope = SearchView.SEARCH_DESC;
        }

        searchView.setSearch( searchCombo.getText(), scope );

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
     */
    public void setContainer( ISearchPageContainer container )
    {
        this.container = container;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    public void setVisible( boolean visible )
    {
        validate();
        super.setVisible( visible );
    }


    /**
     * Verifies if the page is valid.
     *
     * @return
     *      true if the page is valid
     */
    private boolean isValid()
    {
        return ( ( searchCombo.getText() != null ) && ( !"".equals( searchCombo.getText() ) ) );
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        container.setPerformActionEnabled( isValid() );
    }

}
