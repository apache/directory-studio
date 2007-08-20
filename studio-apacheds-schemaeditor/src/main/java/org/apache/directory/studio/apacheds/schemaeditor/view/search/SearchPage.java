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

package org.apache.directory.studio.apacheds.schemaeditor.view.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SearchView;
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
 * @version $Rev$, $Date$
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
    private Button mandatoryAttributes;
    private Button optionalAttributes;

    /**
     * This enums represents the different possible scopes for a Schema Search.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum SearchScopeEnum
    {
        ALIASES, OID, DESCRIPTION, SUPERIOR, SYNTAX, MATCHING_RULES, SUPERIORS, MANDATORY_ATTRIBUTES, OPTIONAL_ATTRIBUTES
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        parent.setLayout( new GridLayout() );

        // Search String Label
        Label searchStringLabel = new Label( parent, SWT.NONE );
        searchStringLabel.setText( "Search string (*=any string, ?=any character):" );
        searchStringLabel.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Search Combo
        searchCombo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        searchCombo.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        searchCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent arg0 )
            {
                validate();
            }
        } );

        // Seach In Group
        Group searchIn = new Group( parent, SWT.NONE );
        searchIn.setLayout( new GridLayout() );
        searchIn.setText( "Search in" );
        searchIn.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Aliases Button
        aliasesButton = new Button( searchIn, SWT.CHECK );
        aliasesButton.setText( "Aliases" );

        // OID Button
        oidButton = new Button( searchIn, SWT.CHECK );
        oidButton.setText( "OID" );

        // Description Button
        descriptionButon = new Button( searchIn, SWT.CHECK );
        descriptionButon.setText( "Description" );

        // Specific Scope Composite
        Composite specificScopeComposite = new Composite( parent, SWT.NONE );
        GridLayout specificScopeLayout = new GridLayout( 2, true );
        specificScopeLayout.marginBottom = 0;
        specificScopeLayout.marginHeight = 0;
        specificScopeLayout.marginLeft = 0;
        specificScopeLayout.marginRight = 0;
        specificScopeLayout.marginTop = 0;
        specificScopeLayout.marginWidth = 0;
        specificScopeComposite.setLayout( specificScopeLayout );
        specificScopeComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Attribute Types Group
        Group attributeTypesGroup = new Group( specificScopeComposite, SWT.NONE );
        attributeTypesGroup.setText( "Seach in (for attribute types)" );
        attributeTypesGroup.setLayout( new GridLayout() );
        attributeTypesGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Superior Button
        superiorButton = new Button( attributeTypesGroup, SWT.CHECK );
        superiorButton.setText( "Superior" );

        // Syntax Button
        syntaxButton = new Button( attributeTypesGroup, SWT.CHECK );
        syntaxButton.setText( "Syntax" );

        // Matching Rules Button
        matchingRulesButton = new Button( attributeTypesGroup, SWT.CHECK );
        matchingRulesButton.setText( "Matching Rules" );

        // Object Classes Group
        Group objectClassesGroup = new Group( specificScopeComposite, SWT.NONE );
        objectClassesGroup.setText( "Search in (for object classes)" );
        objectClassesGroup.setLayout( new GridLayout() );
        objectClassesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Superiors Button
        superiorsButton = new Button( objectClassesGroup, SWT.CHECK );
        superiorsButton.setText( "Superiors" );

        // Mandatory Attributes Button
        mandatoryAttributes = new Button( objectClassesGroup, SWT.CHECK );
        mandatoryAttributes.setText( "Mandatory Attributes" );

        // Optional Attributes Button
        optionalAttributes = new Button( objectClassesGroup, SWT.CHECK );
        optionalAttributes.setText( "Optional Attributes" );

        initSearchStringHistory();

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
     * Initializes the Search Scope
     */
    private void initSearchScope()
    {
        IDialogSettings settings = Activator.getDefault().getDialogSettings();

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES ) == null )
        {
            aliasesButton.setSelection( true );
        }
        else
        {
            aliasesButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID ) == null )
        {
            oidButton.setSelection( true );
        }
        else
        {
            oidButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION ) == null )
        {
            descriptionButon.setSelection( true );
        }
        else
        {
            descriptionButon.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION ) );
        }
        superiorButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIOR ) );
        syntaxButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SYNTAX ) );
        matchingRulesButton
            .setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MATCHING_RULES ) );
        superiorsButton.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIORS ) );
        mandatoryAttributes.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MANDATORY_ATTRIBUTES ) );
        optionalAttributes.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OPTIONAL_ATTRIBUTES ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchPage#performAction()
     */
    public boolean performAction()
    {
        List<SearchScopeEnum> searchScope = new ArrayList<SearchScopeEnum>();
        if ( aliasesButton.getSelection() )
        {
            searchScope.add( SearchScopeEnum.ALIASES );
        }
        if ( oidButton.getSelection() )
        {
            searchScope.add( SearchScopeEnum.OID );
        }
        if ( descriptionButon.getSelection() )
        {
            searchScope.add( SearchScopeEnum.DESCRIPTION );
        }
        if ( superiorButton.getSelection() )
        {
            searchScope.add( SearchScopeEnum.SUPERIOR );
        }
        if ( syntaxButton.getSelection() )
        {
            searchScope.add( SearchScopeEnum.SYNTAX );
        }
        if ( matchingRulesButton.getSelection() )
        {
            searchScope.add( SearchScopeEnum.MATCHING_RULES );
        }
        if ( superiorsButton.getSelection() )
        {
            searchScope.add( SearchScopeEnum.SUPERIORS );
        }
        if ( mandatoryAttributes.getSelection() )
        {
            searchScope.add( SearchScopeEnum.MANDATORY_ATTRIBUTES );
        }
        if ( optionalAttributes.getSelection() )
        {
            searchScope.add( SearchScopeEnum.OPTIONAL_ATTRIBUTES );
        }

        // Opening the SearchView and displaying the results
        try
        {
            SearchView searchView = ( SearchView ) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().showView( SearchView.ID );
            searchView.setSearchInput( searchCombo.getText(), searchScope.toArray( new SearchScopeEnum[0] ) );
        }
        catch ( PartInitException e )
        {
            // TODO ADD Logger
            e.printStackTrace();
        }

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
     * Loads the Search scope.
     *
     * @return
     *      the search scope
     */
    public static List<SearchScopeEnum> loadSearchScope()
    {
        List<SearchScopeEnum> searchScope = new ArrayList<SearchScopeEnum>();
        IDialogSettings settings = Activator.getDefault().getDialogSettings();

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES ) == null )
        {
            searchScope.add( SearchScopeEnum.ALIASES );
        }
        else
        {
            if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES ) )
            {
                searchScope.add( SearchScopeEnum.ALIASES );
            }
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID ) == null )
        {
            searchScope.add( SearchScopeEnum.OID );
        }
        else
        {
            if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID ) )
            {
                searchScope.add( SearchScopeEnum.OID );
            }
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION ) == null )
        {
            searchScope.add( SearchScopeEnum.DESCRIPTION );
        }
        else
        {
            if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION ) )
            {
                searchScope.add( SearchScopeEnum.DESCRIPTION );
            }
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIOR ) )
        {
            searchScope.add( SearchScopeEnum.SUPERIOR );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SYNTAX ) )
        {
            searchScope.add( SearchScopeEnum.SYNTAX );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MATCHING_RULES ) )
        {
            searchScope.add( SearchScopeEnum.MATCHING_RULES );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIORS ) )
        {
            searchScope.add( SearchScopeEnum.SUPERIORS );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MANDATORY_ATTRIBUTES ) )
        {
            searchScope.add( SearchScopeEnum.MANDATORY_ATTRIBUTES );
        }
        if ( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OPTIONAL_ATTRIBUTES ) )
        {
            searchScope.add( SearchScopeEnum.OPTIONAL_ATTRIBUTES );
        }

        return searchScope;
    }


    /**
     * Saves the Search scope.
     *
     * @param scope
     *      the Search scope
     */
    public static void saveSearchScope( List<SearchScopeEnum> scope )
    {
        if ( ( scope != null ) && ( scope.size() > 0 ) )
        {
            IDialogSettings settings = Activator.getDefault().getDialogSettings();

            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES, scope.contains( SearchScopeEnum.ALIASES ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID, scope.contains( SearchScopeEnum.OID ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION, scope
                .contains( SearchScopeEnum.DESCRIPTION ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIOR, scope.contains( SearchScopeEnum.SUPERIOR ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SYNTAX, scope.contains( SearchScopeEnum.SYNTAX ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MATCHING_RULES, scope
                .contains( SearchScopeEnum.MATCHING_RULES ) );
            settings
                .put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIORS, scope.contains( SearchScopeEnum.SUPERIORS ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MANDATORY_ATTRIBUTES, scope
                .contains( SearchScopeEnum.MANDATORY_ATTRIBUTES ) );
            settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OPTIONAL_ATTRIBUTES, scope
                .contains( SearchScopeEnum.OPTIONAL_ATTRIBUTES ) );
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
