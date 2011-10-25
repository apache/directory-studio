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

package org.apache.directory.studio.schemaeditor.view.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.views.SearchView;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import org.eclipse.ui.PartInitException;


/**
 * This class implements the Search Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchPage extends DialogPage implements ISearchPage
{
    /** The SearchPageContainer */
    private ISearchPageContainer container;

    // UI Fields
    private Combo searchCombo;
    private Button aliasesButton;
    private Button oidButton;
    private Button descriptionButon;
    private Button superiorButton;
    private Button syntaxButton;
    private Button matchingRulesButton;
    private Button superiorsButton;
    private Button mandatoryAttributesButton;
    private Button optionalAttributesButton;
    private Button attributeTypesAndObjectClassesButton;
    private Button attributeTypesOnlyButton;
    private Button objectClassesOnly;

    /**
     * This enums represents the different possible search in for a Schema Search.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum SearchInEnum
    {
        ALIASES, OID, DESCRIPTION, SUPERIOR, SYNTAX, MATCHING_RULES, SUPERIORS, MANDATORY_ATTRIBUTES, OPTIONAL_ATTRIBUTES
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        parent.setLayout( new GridLayout() );

        // Search String Label
        Label searchStringLabel = new Label( parent, SWT.NONE );
        searchStringLabel.setText( Messages.getString( "SearchPage.SearchString" ) ); //$NON-NLS-1$
        searchStringLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Combo
        searchCombo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        searchCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        searchCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent arg0 )
            {
                validate();
            }
        } );

        // Specific Scope Composite
        Composite searchInComposite = new Composite( parent, SWT.NONE );
        GridLayout SearchInLayout = new GridLayout( 3, true );
        SearchInLayout.marginBottom = 0;
        SearchInLayout.marginHeight = 0;
        SearchInLayout.marginLeft = 0;
        SearchInLayout.marginRight = 0;
        SearchInLayout.marginTop = 0;
        SearchInLayout.marginWidth = 0;
        searchInComposite.setLayout( SearchInLayout );
        searchInComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Search In Group
        Group searchInGroup = new Group( searchInComposite, SWT.NONE );
        searchInGroup.setLayout( new GridLayout() );
        searchInGroup.setText( Messages.getString( "SearchPage.SearchIn" ) ); //$NON-NLS-1$
        searchInGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Aliases Button
        aliasesButton = new Button( searchInGroup, SWT.CHECK );
        aliasesButton.setText( Messages.getString( "SearchPage.Aliases" ) ); //$NON-NLS-1$

        // OID Button
        oidButton = new Button( searchInGroup, SWT.CHECK );
        oidButton.setText( Messages.getString( "SearchPage.OID" ) ); //$NON-NLS-1$

        // Description Button
        descriptionButon = new Button( searchInGroup, SWT.CHECK );
        descriptionButon.setText( Messages.getString( "SearchPage.Description" ) ); //$NON-NLS-1$

        // Attribute Types Group
        Group attributeTypesSearchInGroup = new Group( searchInComposite, SWT.NONE );
        attributeTypesSearchInGroup.setText( Messages.getString( "SearchPage.SearchInForAttribute" ) ); //$NON-NLS-1$
        attributeTypesSearchInGroup.setLayout( new GridLayout() );
        attributeTypesSearchInGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Superior Button
        superiorButton = new Button( attributeTypesSearchInGroup, SWT.CHECK );
        superiorButton.setText( Messages.getString( "SearchPage.Superior" ) ); //$NON-NLS-1$

        // Syntax Button
        syntaxButton = new Button( attributeTypesSearchInGroup, SWT.CHECK );
        syntaxButton.setText( Messages.getString( "SearchPage.Syntax" ) ); //$NON-NLS-1$

        // Matching Rules Button
        matchingRulesButton = new Button( attributeTypesSearchInGroup, SWT.CHECK );
        matchingRulesButton.setText( Messages.getString( "SearchPage.MatchingRules" ) ); //$NON-NLS-1$

        // Object Classes Group
        Group objectClassesSearchInGroup = new Group( searchInComposite, SWT.NONE );
        objectClassesSearchInGroup.setText( Messages.getString( "SearchPage.SearchInObject" ) ); //$NON-NLS-1$
        objectClassesSearchInGroup.setLayout( new GridLayout() );
        objectClassesSearchInGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Superiors Button
        superiorsButton = new Button( objectClassesSearchInGroup, SWT.CHECK );
        superiorsButton.setText( Messages.getString( "SearchPage.Superiors" ) ); //$NON-NLS-1$

        // Mandatory Attributes Button
        mandatoryAttributesButton = new Button( objectClassesSearchInGroup, SWT.CHECK );
        mandatoryAttributesButton.setText( Messages.getString( "SearchPage.MandatoryAttributes" ) ); //$NON-NLS-1$

        // Optional Attributes Button
        optionalAttributesButton = new Button( objectClassesSearchInGroup, SWT.CHECK );
        optionalAttributesButton.setText( Messages.getString( "SearchPage.OptionalAttributes" ) ); //$NON-NLS-1$

        // Scope Group
        Group scopeGroup = new Group( parent, SWT.NONE );
        scopeGroup.setText( Messages.getString( "SearchPage.Scope" ) ); //$NON-NLS-1$
        scopeGroup.setLayout( new GridLayout() );
        scopeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Attribute Types and Object Classes
        attributeTypesAndObjectClassesButton = new Button( scopeGroup, SWT.RADIO );
        attributeTypesAndObjectClassesButton.setText( Messages.getString( "SearchPage.TypesAndClasses" ) ); //$NON-NLS-1$

        // Attribute Types Only
        attributeTypesOnlyButton = new Button( scopeGroup, SWT.RADIO );
        attributeTypesOnlyButton.setText( Messages.getString( "SearchPage.TypesOnly" ) ); //$NON-NLS-1$

        // Object Classes Only
        objectClassesOnly = new Button( scopeGroup, SWT.RADIO );
        objectClassesOnly.setText( Messages.getString( "SearchPage.ClassesOnly" ) ); //$NON-NLS-1$

        initSearchStringHistory();

        initSearchIn();

        initSearchScope();

        searchCombo.setFocus();

        super.setControl( parent );
    }


    /**
     * Initializes the Search String History.
     */
    private void initSearchStringHistory()
    {
        searchCombo.setItems( loadSearchStringHistory() );
    }


    /**
     * Initializes the Search In.
     */
    private void initSearchIn()
    {
        IDialogSettings settings = Activator.getDefault().getDialogSettings();

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES ) == null )
        {
            aliasesButton.setSelection( true );
        }
        else
        {
            aliasesButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID ) == null )
        {
            oidButton.setSelection( true );
        }
        else
        {
            oidButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION ) == null )
        {
            descriptionButon.setSelection( true );
        }
        else
        {
            descriptionButon.setSelection( settings
                .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION ) );
        }
        superiorButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIOR ) );
        syntaxButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SYNTAX ) );
        matchingRulesButton.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MATCHING_RULES ) );
        superiorsButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIORS ) );
        mandatoryAttributesButton.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MANDATORY_ATTRIBUTES ) );
        optionalAttributesButton.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OPTIONAL_ATTRIBUTES ) );
    }


    /**
     * Initializes the Search Scope.
     */
    private void initSearchScope()
    {
        IDialogSettings settings = Activator.getDefault().getDialogSettings();

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE ) == null )
        {
            attributeTypesAndObjectClassesButton.setSelection( true );
        }
        else
        {
            switch ( settings.getInt( PluginConstants.PREFS_SEARCH_PAGE_SCOPE ) )
            {
                case PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC:
                    attributeTypesAndObjectClassesButton.setSelection( true );
                    break;
                case PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_ONLY:
                    attributeTypesOnlyButton.setSelection( true );
                    break;
                case PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OC_ONLY:
                    objectClassesOnly.setSelection( true );
                    break;
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean performAction()
    {
        // Search In
        List<SearchInEnum> searchIn = new ArrayList<SearchInEnum>();
        if ( aliasesButton.getSelection() )
        {
            searchIn.add( SearchInEnum.ALIASES );
        }
        if ( oidButton.getSelection() )
        {
            searchIn.add( SearchInEnum.OID );
        }
        if ( descriptionButon.getSelection() )
        {
            searchIn.add( SearchInEnum.DESCRIPTION );
        }
        if ( superiorButton.getSelection() )
        {
            searchIn.add( SearchInEnum.SUPERIOR );
        }
        if ( syntaxButton.getSelection() )
        {
            searchIn.add( SearchInEnum.SYNTAX );
        }
        if ( matchingRulesButton.getSelection() )
        {
            searchIn.add( SearchInEnum.MATCHING_RULES );
        }
        if ( superiorsButton.getSelection() )
        {
            searchIn.add( SearchInEnum.SUPERIORS );
        }
        if ( mandatoryAttributesButton.getSelection() )
        {
            searchIn.add( SearchInEnum.MANDATORY_ATTRIBUTES );
        }
        if ( optionalAttributesButton.getSelection() )
        {
            searchIn.add( SearchInEnum.OPTIONAL_ATTRIBUTES );
        }

        // Scope
        int scope = 0;
        if ( attributeTypesAndObjectClassesButton.getSelection() )
        {
            scope = PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC;
        }
        else if ( attributeTypesOnlyButton.getSelection() )
        {
            scope = PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_ONLY;
        }
        else if ( objectClassesOnly.getSelection() )
        {
            scope = PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OC_ONLY;
        }

        // Opening the SearchView and displaying the results
        try
        {
            SearchView searchView = ( SearchView ) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().showView( SearchView.ID );
            searchView.setSearchInput( searchCombo.getText(), searchIn.toArray( new SearchInEnum[0] ), scope );
        }
        catch ( PartInitException e )
        {
            PluginUtils.logError( Messages.getString( "SearchPage.ErrorOpeningView" ), e ); //$NON-NLS-1$
            ViewUtils.displayErrorMessageBox(
                Messages.getString( "SearchPage.Error" ), Messages.getString( "SearchPage.ErrorOpeningView" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return true;
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
        return ( ( searchCombo.getText() != null ) && ( !"".equals( searchCombo.getText() ) ) ); //$NON-NLS-1$
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        container.setPerformActionEnabled( isValid() );
    }


    /**
     * Adds a new Search String to the History.
     *
     * @param value
     *      the value to save
     */
    public static void addSearchStringHistory( String value )
    {
        // get current history
        String[] history = loadSearchStringHistory();
        List<String> list = new ArrayList<String>( Arrays.asList( history ) );

        // add new value or move to first position
        if ( list.contains( value ) )
        {
            list.remove( value );
        }
        list.add( 0, value );

        // check history size
        while ( list.size() > 10 )
        {
            list.remove( list.size() - 1 );
        }

        // save
        history = ( String[] ) list.toArray( new String[list.size()] );
        Activator.getDefault().getDialogSettings().put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_HISTORY, history );
    }


    /**
     * Removes the given value from the History.
     *
     * @param value
     *      the value to remove
     */
    public static void removeSearchStringHistory( String value )
    {
        // get current history
        String[] history = loadSearchStringHistory();
        List<String> list = new ArrayList<String>( Arrays.asList( history ) );

        // add new value or move to first position
        if ( list.contains( value ) )
        {
            list.remove( value );
        }

        // save
        history = ( String[] ) list.toArray( new String[list.size()] );
        Activator.getDefault().getDialogSettings().put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_HISTORY, history );
    }


    /**
     * Loads the Search History
     *
     * @return
     *      an array of String containing the Search History
     */
    public static String[] loadSearchStringHistory()
    {
        String[] history = Activator.getDefault().getDialogSettings().getArray(
            PluginConstants.PREFS_SEARCH_PAGE_SEARCH_HISTORY );
        if ( history == null )
        {
            history = new String[0];
        }
        return history;
    }


    /**
     * Loads the Search In.
     *
     * @return
     *      the search In
     */
    public static List<SearchInEnum> loadSearchIn()
    {
        List<SearchInEnum> searchScope = new ArrayList<SearchInEnum>();
        IDialogSettings settings = Activator.getDefault().getDialogSettings();

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES ) == null )
        {
            searchScope.add( SearchInEnum.ALIASES );
        }
        else
        {
            if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES ) )
            {
                searchScope.add( SearchInEnum.ALIASES );
            }
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID ) == null )
        {
            searchScope.add( SearchInEnum.OID );
        }
        else
        {
            if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID ) )
            {
                searchScope.add( SearchInEnum.OID );
            }
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION ) == null )
        {
            searchScope.add( SearchInEnum.DESCRIPTION );
        }
        else
        {
            if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION ) )
            {
                searchScope.add( SearchInEnum.DESCRIPTION );
            }
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIOR ) )
        {
            searchScope.add( SearchInEnum.SUPERIOR );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SYNTAX ) )
        {
            searchScope.add( SearchInEnum.SYNTAX );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MATCHING_RULES ) )
        {
            searchScope.add( SearchInEnum.MATCHING_RULES );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIORS ) )
        {
            searchScope.add( SearchInEnum.SUPERIORS );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MANDATORY_ATTRIBUTES ) )
        {
            searchScope.add( SearchInEnum.MANDATORY_ATTRIBUTES );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OPTIONAL_ATTRIBUTES ) )
        {
            searchScope.add( SearchInEnum.OPTIONAL_ATTRIBUTES );
        }

        return searchScope;
    }


    /**
     * Loads the scope.
     *
     * @return
     *      the scope
     */
    public static int loadScope()
    {
        IDialogSettings settings = Activator.getDefault().getDialogSettings();

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE ) == null )
        {
            return PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC;
        }
        else
        {
            return settings.getInt( PluginConstants.PREFS_SEARCH_PAGE_SCOPE );
        }
    }


    /**
     * Saves the Search scope.
     *
     * @param scope
     *      the Search scope
     */
    public static void saveSearchScope( List<SearchInEnum> scope )
    {
        if ( ( scope != null ) && ( scope.size() > 0 ) )
        {
            IDialogSettings settings = Activator.getDefault().getDialogSettings();

            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES, scope.contains( SearchInEnum.ALIASES ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID, scope.contains( SearchInEnum.OID ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION, scope
                .contains( SearchInEnum.DESCRIPTION ) );
            settings
                .put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIOR, scope.contains( SearchInEnum.SUPERIOR ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SYNTAX, scope.contains( SearchInEnum.SYNTAX ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MATCHING_RULES, scope
                .contains( SearchInEnum.MATCHING_RULES ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIORS, scope
                .contains( SearchInEnum.SUPERIORS ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MANDATORY_ATTRIBUTES, scope
                .contains( SearchInEnum.MANDATORY_ATTRIBUTES ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OPTIONAL_ATTRIBUTES, scope
                .contains( SearchInEnum.OPTIONAL_ATTRIBUTES ) );
        }
    }


    /**
     * Clears the Search History.
     */
    public static void clearSearchHistory()
    {
        Activator.getDefault().getDialogSettings()
            .put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_HISTORY, new String[0] );
    }
}
