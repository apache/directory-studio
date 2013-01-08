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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.SearchPageWrapper;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


public class BatchOperationApplyOnWizardPage extends WizardPage
{

    private String[] initCurrentSelectionTexts;

    private Dn[][] initCurrentSelectionDns;

    private ISearch initSearch;

    private Button currentSelectionButton;

    private Combo currentSelectionCombo;

    private Button searchButton;

    private SearchPageWrapper spw;


    public BatchOperationApplyOnWizardPage( String pageName, BatchOperationWizard wizard )
    {
        super( pageName );
        super.setTitle( Messages.getString( "BatchOperationApplyOnWizardPage.SelectApplicationEntries" ) ); //$NON-NLS-1$
        super.setDescription( Messages.getString( "BatchOperationApplyOnWizardPage.PleaseSelectEntries" ) ); //$NON-NLS-1$
        super.setPageComplete( false );

        this.prepareCurrentSelection();
        this.prepareSearch();
    }


    private void validate()
    {
        setPageComplete( getApplyOnDns() != null || spw.isValid() );
        setErrorMessage( searchButton.getSelection() ? spw.getErrorMessage() : null );
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Composite applyOnGroup = composite;

        this.currentSelectionButton = BaseWidgetUtils.createRadiobutton( applyOnGroup, Messages
            .getString( "BatchOperationApplyOnWizardPage.CurrentSelection" ), 1 ); //$NON-NLS-1$
        this.currentSelectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                enableCurrentSelectionWidgets( currentSelectionButton.getSelection() );
                validate();
            }
        } );

        Composite currentSelectionComposite = BaseWidgetUtils.createColumnContainer( applyOnGroup, 2, 1 );
        BaseWidgetUtils.createRadioIndent( currentSelectionComposite, 1 );
        this.currentSelectionCombo = BaseWidgetUtils.createReadonlyCombo( currentSelectionComposite,
            this.initCurrentSelectionTexts, 0, 1 );
        this.currentSelectionCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createSpacer( applyOnGroup, 1 );
        BaseWidgetUtils.createSpacer( applyOnGroup, 1 );

        this.searchButton = BaseWidgetUtils.createRadiobutton( applyOnGroup, Messages
            .getString( "BatchOperationApplyOnWizardPage.ResultsOfSearch" ), 1 ); //$NON-NLS-1$
        this.searchButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                enableSearchWidgets( searchButton.getSelection() );
                validate();
            }
        } );

        Composite searchComposite = BaseWidgetUtils.createColumnContainer( applyOnGroup, 2, 1 );
        BaseWidgetUtils.createRadioIndent( searchComposite, 1 );
        Composite innerSearchComposite = BaseWidgetUtils.createColumnContainer( searchComposite, 3, 1 );
        this.spw = new SearchPageWrapper( SearchPageWrapper.NAME_INVISIBLE
            | SearchPageWrapper.REFERRALOPTIONS_FOLLOW_MANUAL_INVISIBLE
            | SearchPageWrapper.RETURNINGATTRIBUTES_INVISIBLE | SearchPageWrapper.REFERRALOPTIONS_READONLY );
        this.spw.createContents( innerSearchComposite );
        this.spw.loadFromSearch( this.initSearch );
        this.spw.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        this.currentSelectionButton.setSelection( this.currentSelectionCombo.getItemCount() > 0 );
        this.currentSelectionButton.setEnabled( this.currentSelectionCombo.getItemCount() > 0 );
        this.searchButton.setSelection( this.currentSelectionCombo.getItemCount() == 0 );
        this.enableCurrentSelectionWidgets( this.currentSelectionButton.getSelection() );
        this.enableSearchWidgets( this.searchButton.getSelection() );

        validate();

        setControl( composite );
    }


    public Dn[] getApplyOnDns()
    {
        if ( currentSelectionButton.getSelection() )
        {
            int index = currentSelectionCombo.getSelectionIndex();
            return initCurrentSelectionDns[index];
        }
        else
        {
            return null;
        }
    }


    public ISearch getApplyOnSearch()
    {
        if ( searchButton.getSelection() )
        {
            return this.initSearch;
        }
        else
        {
            return null;
        }
    }


    private void enableCurrentSelectionWidgets( boolean b )
    {
        currentSelectionCombo.setEnabled( b );
    }


    private void enableSearchWidgets( boolean b )
    {
        spw.setEnabled( b );
    }


    private void prepareSearch()
    {
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        this.initSearch = BrowserSelectionUtils.getExampleSearch( selection );
        this.initSearch.setName( null );

        // never follow referrals for a batch operation!
        this.initSearch.setReferralsHandlingMethod( Connection.ReferralHandlingMethod.IGNORE );
    }


    private void prepareCurrentSelection()
    {

        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        ISearch[] searches = BrowserSelectionUtils.getSearches( selection );
        IEntry[] entries = BrowserSelectionUtils.getEntries( selection );
        ISearchResult[] searchResults = BrowserSelectionUtils.getSearchResults( selection );
        IBookmark[] bookmarks = BrowserSelectionUtils.getBookmarks( selection );
        IAttribute[] attributes = BrowserSelectionUtils.getAttributes( selection );
        IValue[] values = BrowserSelectionUtils.getValues( selection );

        List<String> textList = new ArrayList<String>();
        List<Dn[]> dnsList = new ArrayList<Dn[]>();

        if ( attributes.length + values.length > 0 )
        {
            Set<Dn> internalDnSet = new LinkedHashSet<Dn>();
            for ( int v = 0; v < values.length; v++ )
            {
                if ( values[v].isString() )
                {
                    try
                    {
                        Dn dn = new Dn( values[v].getStringValue() );
                        internalDnSet.add( dn );
                    }
                    catch ( LdapInvalidDnException e )
                    {
                    }
                }
            }

            for ( int a = 0; a < attributes.length; a++ )
            {
                IValue[] vals = attributes[a].getValues();
                for ( int v = 0; v < vals.length; v++ )
                {
                    if ( vals[v].isString() )
                    {
                        try
                        {
                            Dn dn = new Dn( vals[v].getStringValue() );
                            internalDnSet.add( dn );
                        }
                        catch ( LdapInvalidDnException e )
                        {
                        }
                    }
                }
            }

            if ( !internalDnSet.isEmpty() )
            {
                dnsList.add( internalDnSet.toArray( new Dn[internalDnSet.size()] ) );
                textList
                    .add( NLS
                        .bind(
                            Messages.getString( "BatchOperationApplyOnWizardPage.DNsOfSelectedAttributes" ), new Object[] { internalDnSet.size() } ) ); //$NON-NLS-1$
            }
        }
        if ( searches.length == 1 && searches[0].getSearchResults() != null )
        {
            Set<Dn> internalDnSet = new LinkedHashSet<Dn>();
            ISearchResult[] srs = searches[0].getSearchResults();
            for ( int i = 0; i < srs.length; i++ )
            {
                internalDnSet.add( srs[i].getDn() );
            }

            dnsList.add( internalDnSet.toArray( new Dn[internalDnSet.size()] ) );
            textList
                .add( NLS
                    .bind(
                        Messages.getString( "BatchOperationApplyOnWizardPage.SearchResultOf" ), new Object[] { searches[0].getName(), searches[0].getSearchResults().length } ) ); //$NON-NLS-1$
        }
        if ( entries.length + searchResults.length + bookmarks.length > 0 )
        {
            Set<Dn> internalDnSet = new LinkedHashSet<Dn>();
            for ( int i = 0; i < entries.length; i++ )
            {
                internalDnSet.add( entries[i].getDn() );
            }
            for ( int i = 0; i < searchResults.length; i++ )
            {
                internalDnSet.add( searchResults[i].getDn() );
            }
            for ( int i = 0; i < bookmarks.length; i++ )
            {
                internalDnSet.add( bookmarks[i].getDn() );
            }

            dnsList.add( internalDnSet.toArray( new Dn[internalDnSet.size()] ) );
            textList
                .add( NLS
                    .bind(
                        Messages.getString( "BatchOperationApplyOnWizardPage.SelectedEntries" ), new Object[] { internalDnSet.size() } ) ); //$NON-NLS-1$
        }

        this.initCurrentSelectionTexts = textList.toArray( new String[textList.size()] );
        this.initCurrentSelectionDns = dnsList.toArray( new Dn[0][0] );

    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        this.spw.saveToSearch( initSearch );
    }

}