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

package org.apache.directory.studio.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SearchViewController;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.schemaeditor.view.search.SearchPage;
import org.apache.directory.studio.schemaeditor.view.search.SearchPage.SearchInEnum;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class represents the Search View.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchView extends ViewPart
{
    /** The view's ID */
    public static final String ID = PluginConstants.VIEW_SEARCH_VIEW_ID;

    /** The current Search String */
    private String searchString;

    // UI fields
    private Text searchField;
    private Button searchButton;
    private Label searchResultsLabel;
    private Table resultsTable;
    private TableViewer resultsTableViewer;
    private Composite searchFieldComposite;
    private Composite searchFieldInnerComposite;
    private Label separatorLabel;

    /** The parent composite */
    private Composite parent;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent )
    {
        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, PluginConstants.PLUGIN_ID + "." + "search_view" ); //$NON-NLS-1$ //$NON-NLS-2$

        this.parent = parent;
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        parent.setLayout( gridLayout );

        // Search Field
        searchFieldComposite = new Composite( parent, SWT.NONE );
        gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        searchFieldComposite.setLayout( gridLayout );
        searchFieldComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        // This searchFieldCompositeSeparator is used to display correctly the searchFieldComposite,
        // since an empty composite does not display well.
        Label searchFieldCompositeSeparator = new Label( searchFieldComposite, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.heightHint = 1;
        searchFieldCompositeSeparator.setLayoutData( gridData );
        searchFieldCompositeSeparator.setVisible( false );

        // Search Results Label
        searchResultsLabel = new Label( parent, SWT.NONE );
        searchResultsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Separator Label
        Label separatorLabel2 = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel2.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Create the table 
        createTableViewer();

        setSearchResultsLabel( null, 0 );

        new SearchViewController( this );
    }


    /**
     * Create the Search Field Sections.
     */
    private void createSearchField()
    {
        // Search Inner Composite
        searchFieldInnerComposite = new Composite( searchFieldComposite, SWT.NONE );
        GridLayout searchFieldInnerCompositeGridLayout = new GridLayout( 4, false );
        searchFieldInnerCompositeGridLayout.horizontalSpacing = 1;
        searchFieldInnerCompositeGridLayout.verticalSpacing = 1;
        searchFieldInnerCompositeGridLayout.marginHeight = 1;
        searchFieldInnerCompositeGridLayout.marginWidth = 2;
        searchFieldInnerComposite.setLayout( searchFieldInnerCompositeGridLayout );
        searchFieldInnerComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Label
        Label searchFieldLabel = new Label( searchFieldInnerComposite, SWT.NONE );
        searchFieldLabel.setText( Messages.getString( "SearchView.SearchColon" ) ); //$NON-NLS-1$
        searchFieldLabel.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // Search Text Field
        searchField = new Text( searchFieldInnerComposite, SWT.BORDER | SWT.SEARCH | SWT.CANCEL );
        if ( searchString != null )
        {
            searchField.setText( searchString );
        }
        searchField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        searchField.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validateSearchField();
            }
        } );
        searchField.addKeyListener( new KeyAdapter()
        {
            public void keyReleased( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_DOWN )
                {
                    resultsTable.setFocus();
                }
                else if ( ( e.keyCode == Action.findKeyCode( "RETURN" ) ) || ( e.keyCode == SWT.KEYPAD_CR ) ) //$NON-NLS-1$ 
                {
                    search();
                }
            }
        } );

        // Search Toolbar
        final ToolBar searchToolBar = new ToolBar( searchFieldInnerComposite, SWT.HORIZONTAL | SWT.FLAT );
        // Creating the Search In ToolItem
        final ToolItem searchInToolItem = new ToolItem( searchToolBar, SWT.DROP_DOWN );
        searchInToolItem.setText( Messages.getString( "SearchView.SearchIn" ) ); //$NON-NLS-1$
        // Adding the action to display the Menu when the item is clicked
        searchInToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = searchInToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = searchToolBar.toDisplay( pt );

                Menu menu = createSearchInMenu();
                menu.setLocation( pt.x, pt.y );
                menu.setVisible( true );
            }
        } );

        new ToolItem( searchToolBar, SWT.SEPARATOR );

        final ToolItem scopeToolItem = new ToolItem( searchToolBar, SWT.DROP_DOWN );
        scopeToolItem.setText( Messages.getString( "SearchView.Scope" ) ); //$NON-NLS-1$
        // Adding the action to display the Menu when the item is clicked
        scopeToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = scopeToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = searchToolBar.toDisplay( pt );

                Menu menu = createScopeMenu();
                menu.setLocation( pt.x, pt.y );
                menu.setVisible( true );
            }
        } );
        searchToolBar.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // Search Button
        searchButton = new Button( searchFieldInnerComposite, SWT.PUSH | SWT.DOWN );
        searchButton.setEnabled( false );
        searchButton.setImage( Activator.getDefault().getImage( PluginConstants.IMG_SEARCH ) );
        searchButton.setToolTipText( Messages.getString( "SearchView.Search" ) ); //$NON-NLS-1$
        searchButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                search();
            }
        } );
        searchButton.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // Separator Label
        separatorLabel = new Label( searchFieldComposite, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Search In Menu
     *
     * @return
     *      the Search In menu
     */
    public Menu createSearchInMenu()
    {
        final IDialogSettings settings = Activator.getDefault().getDialogSettings();

        // Creating the associated Menu
        Menu searchInMenu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );

        // Filling the menu
        // Aliases
        final MenuItem aliasesMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        aliasesMenuItem.setText( Messages.getString( "SearchView.Aliases" ) ); //$NON-NLS-1$
        aliasesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES, aliasesMenuItem.getSelection() );
            }
        } );
        // OID
        final MenuItem oidMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        oidMenuItem.setText( Messages.getString( "SearchView.OID" ) ); //$NON-NLS-1$
        oidMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID, oidMenuItem.getSelection() );
            }
        } );
        // Description
        final MenuItem descriptionMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        descriptionMenuItem.setText( Messages.getString( "SearchView.Description" ) ); //$NON-NLS-1$
        descriptionMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION, descriptionMenuItem
                    .getSelection() );
            }
        } );
        // Separator
        new MenuItem( searchInMenu, SWT.SEPARATOR );
        // Superior
        final MenuItem superiorMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        superiorMenuItem.setText( Messages.getString( "SearchView.Superior" ) ); //$NON-NLS-1$
        superiorMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIOR, superiorMenuItem.getSelection() );
            }
        } );
        // Syntax
        final MenuItem syntaxMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        syntaxMenuItem.setText( Messages.getString( "SearchView.Syntax" ) ); //$NON-NLS-1$
        syntaxMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SYNTAX, syntaxMenuItem.getSelection() );
            }
        } );
        // Matching Rules
        final MenuItem matchingRulesMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        matchingRulesMenuItem.setText( Messages.getString( "SearchView.MatchingRules" ) ); //$NON-NLS-1$
        matchingRulesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MATCHING_RULES, matchingRulesMenuItem
                    .getSelection() );
            }
        } );
        // Separator
        new MenuItem( searchInMenu, SWT.SEPARATOR );
        // Superiors
        final MenuItem superiorsMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        superiorsMenuItem.setText( Messages.getString( "SearchView.Superiors" ) ); //$NON-NLS-1$
        superiorsMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIORS, superiorsMenuItem.getSelection() );
            }
        } );
        // Mandatory Attributes
        final MenuItem mandatoryAttributesMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        mandatoryAttributesMenuItem.setText( Messages.getString( "SearchView.MandatoryAttributes" ) ); //$NON-NLS-1$
        mandatoryAttributesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MANDATORY_ATTRIBUTES,
                    mandatoryAttributesMenuItem.getSelection() );
            }
        } );
        // Optional Attributes
        final MenuItem optionalAttributesMenuItem = new MenuItem( searchInMenu, SWT.CHECK );
        optionalAttributesMenuItem.setText( Messages.getString( "SearchView.OptionalAttributes" ) ); //$NON-NLS-1$
        optionalAttributesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OPTIONAL_ATTRIBUTES,
                    optionalAttributesMenuItem.getSelection() );
            }
        } );

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES ) == null )
        {
            aliasesMenuItem.setSelection( true );
        }
        else
        {
            aliasesMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID ) == null )
        {
            oidMenuItem.setSelection( true );
        }
        else
        {

            oidMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OID ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION ) == null )
        {
            descriptionMenuItem.setSelection( true );
        }
        else
        {
            descriptionMenuItem.setSelection( settings
                .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION ) );
        }

        superiorMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIOR ) );
        syntaxMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SYNTAX ) );
        matchingRulesMenuItem.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MATCHING_RULES ) );
        superiorsMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIORS ) );
        mandatoryAttributesMenuItem.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_MANDATORY_ATTRIBUTES ) );
        optionalAttributesMenuItem.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SEARCH_IN_OPTIONAL_ATTRIBUTES ) );

        return searchInMenu;
    }


    /**
     * Creates the Scope Menu
     *
     * @return
     *      the Scope menu
     */
    public Menu createScopeMenu()
    {
        final IDialogSettings settings = Activator.getDefault().getDialogSettings();

        // Creating the associated Menu
        Menu scopeMenu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );

        // Filling the menu
        // Attribute Types And Object Classes
        final MenuItem attributeTypesAndObjectClassesMenuItem = new MenuItem( scopeMenu, SWT.RADIO );
        attributeTypesAndObjectClassesMenuItem.setText( Messages.getString( "SearchView.TypesAndClasses" ) ); //$NON-NLS-1$
        attributeTypesAndObjectClassesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE,
                    PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC );
            }
        } );
        // Attributes Type Only
        final MenuItem attributesTypesOnlyMenuItem = new MenuItem( scopeMenu, SWT.RADIO );
        attributesTypesOnlyMenuItem.setText( Messages.getString( "SearchView.TypesOnly" ) ); //$NON-NLS-1$
        attributesTypesOnlyMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE, PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_ONLY );
            }
        } );
        // Object Classes Only
        final MenuItem objectClassesMenuItem = new MenuItem( scopeMenu, SWT.RADIO );
        objectClassesMenuItem.setText( Messages.getString( "SearchView.ClassesOnly" ) ); //$NON-NLS-1$
        objectClassesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE, PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OC_ONLY );
            }
        } );

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE ) == null )
        {
            attributeTypesAndObjectClassesMenuItem.setSelection( true );
        }
        else
        {
            switch ( settings.getInt( PluginConstants.PREFS_SEARCH_PAGE_SCOPE ) )
            {
                case PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC:
                    attributeTypesAndObjectClassesMenuItem.setSelection( true );
                    break;
                case PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_ONLY:
                    attributesTypesOnlyMenuItem.setSelection( true );
                    break;
                case PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OC_ONLY:
                    objectClassesMenuItem.setSelection( true );
                    break;
            }
        }

        return scopeMenu;
    }


    /**
     * Creates the TableViewer.
     */
    private void createTableViewer()
    {
        // Creating the TableViewer
        resultsTable = new Table( parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        resultsTable.setLayoutData( gridData );
        resultsTable.setLinesVisible( true );

        // Creating the TableViewer
        resultsTableViewer = new TableViewer( resultsTable );
        resultsTableViewer.setLabelProvider( new DecoratingLabelProvider( new SearchViewLabelProvider(), Activator
            .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        resultsTableViewer.setContentProvider( new SearchViewContentProvider() );

        // Adding listeners
        resultsTable.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( ( e.keyCode == Action.findKeyCode( "RETURN" ) ) || ( e.keyCode == SWT.KEYPAD_CR ) ) // return key //$NON-NLS-1$
                {
                    openEditor();
                }
            }
        } );

        resultsTableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                openEditor();
            }
        } );
    }


    /**
     * Open the editor associated with the current selection in the table.
     */
    private void openEditor()
    {
        if ( Activator.getDefault().getSchemaHandler() != null )
        {
            StructuredSelection selection = ( StructuredSelection ) resultsTableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                Object item = selection.getFirstElement();

                IEditorInput input = null;
                String editorId = null;

                // Here is the double clicked item
                if ( item instanceof AttributeTypeImpl )
                {
                    input = new AttributeTypeEditorInput( ( AttributeTypeImpl ) item );
                    editorId = AttributeTypeEditor.ID;
                }
                else if ( item instanceof ObjectClassImpl )
                {
                    input = new ObjectClassEditorInput( ( ObjectClassImpl ) item );
                    editorId = ObjectClassEditor.ID;
                }

                // Let's open the editor
                if ( input != null )
                {
                    try
                    {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( input,
                            editorId );
                    }
                    catch ( PartInitException exception )
                    {
                        PluginUtils.logError( Messages.getString( "SearchView.ErrorOpeningEditor" ), exception ); //$NON-NLS-1$
                        ViewUtils
                            .displayErrorMessageBox(
                                Messages.getString( "SearchView.Error" ), Messages.getString( "SearchView.ErrorOpeningEditor" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        if ( searchField != null && !searchField.isDisposed() )
        {
            searchField.setFocus();
        }
        else
        {
            resultsTable.setFocus();
        }
    }


    /**
     * Shows the Search Field Section.
     */
    public void showSearchFieldSection()
    {
        createSearchField();
        parent.layout( true, true );
        searchField.setFocus();
        validateSearchField();
    }


    /**
     * Hides the Search Field Section.
     */
    public void hideSearchFieldSection()
    {
        if ( searchFieldInnerComposite != null )
        {
            searchFieldInnerComposite.dispose();
            searchFieldInnerComposite = null;
        }
        if ( separatorLabel != null )
        {
            separatorLabel.dispose();
            separatorLabel = null;
        }
        parent.layout( true, true );
        resultsTable.setFocus();
    }


    private void validateSearchField()
    {
        searchButton.setEnabled( searchField.getText().length() > 0 );
    }


    /**
     * Sets the Search Input.
     *
     * @param searchString
     *      the search String
     * @param searchIn
     *      the search In
     * @param scope
     *      the scope
     */
    public void setSearchInput( String searchString, SearchInEnum[] searchIn, int scope )
    {
        this.searchString = searchString;

        // Saving search String and Search Scope to dialog settings
        SearchPage.addSearchStringHistory( searchString );
        SearchPage.saveSearchScope( Arrays.asList( searchIn ) );

        if ( ( searchField != null ) && ( !searchField.isDisposed() ) )
        {
            searchField.setText( searchString );
            validateSearchField();
        }

        List<SchemaObject> results = search( searchString, searchIn, scope );
        setSearchResultsLabel( searchString, results.size() );
        resultsTableViewer.setInput( results );
    }


    /**
     * Searches the objects corresponding to the search parameters.
     *
     * @param searchString
     *      the search String
     * @param searchIn
     *      the search In
     * @param scope
     *      the scope
     */
    private List<SchemaObject> search( String searchString, SearchInEnum[] searchIn, int scope )
    {
        List<SchemaObject> searchResults = new ArrayList<SchemaObject>();

        if ( searchString != null )
        {
            String computedSearchString = searchString.replaceAll( "\\*", "\\\\S*" ); //$NON-NLS-1$ //$NON-NLS-2$
            computedSearchString = computedSearchString.replaceAll( "\\?", ".*" ); //$NON-NLS-1$ //$NON-NLS-2$

            Pattern pattern = Pattern.compile( computedSearchString, Pattern.CASE_INSENSITIVE );

            SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
            if ( schemaHandler != null )
            {
                List<SearchInEnum> searchScope = new ArrayList<SearchInEnum>( Arrays.asList( searchIn ) );

                if ( ( scope == PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC )
                    || ( scope == PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_ONLY ) )
                {
                    // Looping on attribute types
                    List<AttributeTypeImpl> attributeTypes = schemaHandler.getAttributeTypes();
                    for ( AttributeTypeImpl at : attributeTypes )
                    {
                        // Aliases
                        if ( searchScope.contains( SearchInEnum.ALIASES ) )
                        {
                            if ( checkArray( pattern, at.getNamesRef() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }
                        }

                        // OID
                        if ( searchScope.contains( SearchInEnum.OID ) )
                        {
                            if ( checkString( pattern, at.getOid() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }
                        }

                        // Description
                        if ( searchScope.contains( SearchInEnum.DESCRIPTION ) )
                        {
                            if ( checkString( pattern, at.getDescription() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }
                        }

                        // Superior
                        if ( searchScope.contains( SearchInEnum.SUPERIOR ) )
                        {
                            if ( checkString( pattern, at.getSuperiorName() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }
                        }

                        // Syntax
                        if ( searchScope.contains( SearchInEnum.SYNTAX ) )
                        {
                            if ( checkString( pattern, at.getSyntaxOid() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }
                        }

                        // Matching Rules
                        if ( searchScope.contains( SearchInEnum.MATCHING_RULES ) )
                        {
                            // Equality
                            if ( checkString( pattern, at.getEqualityName() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }

                            // Ordering
                            if ( checkString( pattern, at.getOrderingName() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }

                            // Substring
                            if ( checkString( pattern, at.getSubstrName() ) )
                            {
                                searchResults.add( at );
                                continue;
                            }
                        }
                    }
                }

                if ( ( scope == PluginConstants.PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC )
                    || ( scope == PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OC_ONLY ) )
                {
                    // Looping on object classes
                    List<ObjectClassImpl> objectClasses = schemaHandler.getObjectClasses();
                    for ( ObjectClassImpl oc : objectClasses )
                    {
                        // Aliases
                        if ( searchScope.contains( SearchInEnum.ALIASES ) )
                        {
                            if ( checkArray( pattern, oc.getNamesRef() ) )
                            {
                                searchResults.add( oc );
                                continue;
                            }
                        }

                        // OID
                        if ( searchScope.contains( SearchInEnum.OID ) )
                        {
                            if ( checkString( pattern, oc.getOid() ) )
                            {
                                searchResults.add( oc );
                                continue;
                            }
                        }

                        // Description
                        if ( searchScope.contains( SearchInEnum.DESCRIPTION ) )
                        {
                            if ( checkString( pattern, oc.getDescription() ) )
                            {
                                searchResults.add( oc );
                                continue;
                            }
                        }

                        // Superiors
                        if ( searchScope.contains( SearchInEnum.SUPERIORS ) )
                        {
                            if ( checkArray( pattern, oc.getSuperClassesNames() ) )
                            {
                                searchResults.add( oc );
                                continue;
                            }
                        }

                        // Mandatory Attributes
                        if ( searchScope.contains( SearchInEnum.MANDATORY_ATTRIBUTES ) )
                        {
                            if ( checkArray( pattern, oc.getMustNamesList() ) )
                            {
                                searchResults.add( oc );
                                continue;
                            }
                        }

                        // Optional Attributes
                        if ( searchScope.contains( SearchInEnum.OPTIONAL_ATTRIBUTES ) )
                        {
                            if ( checkArray( pattern, oc.getMayNamesList() ) )
                            {
                                searchResults.add( oc );
                                continue;
                            }
                        }
                    }
                }
            }
        }

        return searchResults;
    }


    /**
     * Check an array with the given pattern.
     *
     * @param pattern
     *      the Regex pattern
     * @param array
     *      the array
     * @return
     *      true if the pattern matches one of the aliases, false, if not.
     */
    private boolean checkArray( Pattern pattern, String[] array )
    {
        if ( array != null )
        {
            for ( String string : array )
            {
                return pattern.matcher( string ).matches();

            }
        }

        return false;
    }


    private boolean checkString( Pattern pattern, String string )
    {
        if ( string != null )
        {
            return pattern.matcher( string ).matches();
        }

        return false;
    }


    /**
     * Launches the search from the search fields views.
     */
    private void search()
    {
        String searchString = searchField.getText();
        List<SearchInEnum> searchScope = SearchPage.loadSearchIn();

        setSearchInput( searchString, searchScope.toArray( new SearchInEnum[0] ), SearchPage.loadScope() );
    }


    /**
     * Refresh the overview label with the number of results.
     *
     * @param searchString
     *      the search String
     * @param resultsCount
     *      the number of results
     */
    public void setSearchResultsLabel( String searchString, int resultsCount )
    {
        StringBuffer sb = new StringBuffer();

        if ( searchString == null )
        {
            sb.append( Messages.getString( "SearchView.NoSearch" ) ); //$NON-NLS-1$
        }
        else
        {
            // Search String
            sb.append( "'" + searchString + "'" ); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append( " - " ); //$NON-NLS-1$

            // Search results count
            sb.append( resultsCount );
            sb.append( " " ); //$NON-NLS-1$
            if ( resultsCount > 1 )
            {
                sb.append( Messages.getString( "SearchView.Matches" ) ); //$NON-NLS-1$
            }
            else
            {
                sb.append( Messages.getString( "SearchView.Match" ) ); //$NON-NLS-1$
            }

            sb.append( Messages.getString( "SearchView.InWorkspace" ) ); //$NON-NLS-1$
        }

        searchResultsLabel.setText( sb.toString() );
    }


    /**
     * Runs the current search again.
     */
    public void runCurrentSearchAgain()
    {
        if ( searchString != null )
        {
            setSearchInput( searchString, SearchPage.loadSearchIn().toArray( new SearchInEnum[0] ), SearchPage
                .loadScope() );
        }
    }


    /**
     * Gets the Search String.
     *
     * @return
     *      the Search String or null if no Search String is set.
     */
    public String getSearchString()
    {
        return searchString;
    }


    /**
     * Refreshes the view.
     */
    public void refresh()
    {
        resultsTableViewer.refresh();
    }
}
