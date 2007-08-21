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

package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.PluginUtils;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SearchViewController;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.search.SearchPage;
import org.apache.directory.studio.apacheds.schemaeditor.view.search.SearchPage.SearchScopeEnum;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Search View.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchView extends ViewPart
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.SearchView"; //$NON-NLS-1$

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
        searchFieldLabel.setText( "Search:" );
        searchFieldLabel.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // Search Text Field
        searchField = new Text( searchFieldInnerComposite, SWT.BORDER );
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
                else if ( ( e.keyCode == Action.findKeyCode( "RETURN" ) ) || ( e.keyCode == 16777296 /* The "Enter" Key at the bottom right of the keyboard */) ) //$NON-NLS-1$ 
                {
                    search();
                }
            }
        } );

        // Search Scope Toolbar
        final ToolBar scopeToolBar = new ToolBar( searchFieldInnerComposite, SWT.HORIZONTAL | SWT.FLAT );
        // Creating the Search Scope ToolItem
        final ToolItem scopeToolItem = new ToolItem( scopeToolBar, SWT.DROP_DOWN );
        scopeToolItem.setText( "Scope" );
        // Adding the action to display the Menu when the item is clicked
        scopeToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = scopeToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = scopeToolBar.toDisplay( pt );

                Menu menu = createMenu();
                menu.setLocation( pt.x, pt.y );
                menu.setVisible( true );
            }
        } );
        scopeToolBar.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // Search Button
        searchButton = new Button( searchFieldInnerComposite, SWT.PUSH | SWT.DOWN );
        searchButton.setEnabled( false );
        searchButton.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SEARCH ).createImage() );
        searchButton.setToolTipText( "Search" );
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
     * Creates the menu
     *
     * @return
     *      the menu
     */
    public Menu createMenu()
    {
        final IDialogSettings settings = Activator.getDefault().getDialogSettings();

        // Creating the associated Menu
        Menu scopeMenu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );

        // Filling the menu
        // Aliases
        final MenuItem aliasesMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        aliasesMenuItem.setText( "Aliases" );
        aliasesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES, aliasesMenuItem.getSelection() );
            }
        } );
        // OID
        final MenuItem oidMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        oidMenuItem.setText( "OID" );
        oidMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID, oidMenuItem.getSelection() );
            }
        } );
        // Description
        final MenuItem descriptionMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        descriptionMenuItem.setText( "Description" );
        descriptionMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION, descriptionMenuItem.getSelection() );
            }
        } );
        // Separator
        new MenuItem( scopeMenu, SWT.SEPARATOR );
        // Superior
        final MenuItem superiorMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        superiorMenuItem.setText( "Superior" );
        superiorMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIOR, superiorMenuItem.getSelection() );
            }
        } );
        // Syntax
        final MenuItem syntaxMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        syntaxMenuItem.setText( "Syntax" );
        syntaxMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SYNTAX, syntaxMenuItem.getSelection() );
            }
        } );
        // Matching Rules
        final MenuItem matchingRulesMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        matchingRulesMenuItem.setText( "Matching Rules" );
        matchingRulesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MATCHING_RULES, matchingRulesMenuItem
                    .getSelection() );
            }
        } );
        // Separator
        new MenuItem( scopeMenu, SWT.SEPARATOR );
        // Superiors
        final MenuItem superiorsMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        superiorsMenuItem.setText( "Superiors" );
        superiorsMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIORS, superiorsMenuItem.getSelection() );
            }
        } );
        // Mandatory Attributes
        final MenuItem mandatoryAttributesMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        mandatoryAttributesMenuItem.setText( "Mandatory Attributes" );
        mandatoryAttributesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MANDATORY_ATTRIBUTES, mandatoryAttributesMenuItem
                    .getSelection() );
            }
        } );
        // Optional Attributes
        final MenuItem optionalAttributesMenuItem = new MenuItem( scopeMenu, SWT.CHECK );
        optionalAttributesMenuItem.setText( "Optional Attributes" );
        optionalAttributesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                settings.put( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OPTIONAL_ATTRIBUTES, optionalAttributesMenuItem
                    .getSelection() );
            }
        } );

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES ) == null )
        {
            aliasesMenuItem.setSelection( true );
        }
        else
        {
            aliasesMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_ALIASES ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID ) == null )
        {
            oidMenuItem.setSelection( true );
        }
        else
        {

            oidMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OID ) );
        }

        if ( settings.get( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION ) == null )
        {
            descriptionMenuItem.setSelection( true );
        }
        else
        {
            descriptionMenuItem
                .setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_DESCRIPTION ) );
        }

        superiorMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIOR ) );
        syntaxMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SYNTAX ) );
        matchingRulesMenuItem.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MATCHING_RULES ) );
        superiorsMenuItem.setSelection( settings.getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_SUPERIORS ) );
        mandatoryAttributesMenuItem.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_MANDATORY_ATTRIBUTES ) );
        optionalAttributesMenuItem.setSelection( settings
            .getBoolean( PluginConstants.PREFS_SEARCH_PAGE_SCOPE_OPTIONAL_ATTRIBUTES ) );

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
                if ( ( e.keyCode == Action.findKeyCode( "RETURN" ) )
                    || ( e.keyCode == 16777296 /* The "Enter" Key at the bottom right of the keyboard */) ) // return key
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
                        PluginUtils.logError( "An error occured when opening the editor.", exception );
                        ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
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
     * @param scope
     *      the search Scope
     */
    public void setSearchInput( String searchString, SearchScopeEnum[] scope )
    {
        this.searchString = searchString;

        // Saving search String and Search Scope to dialog settings
        SearchPage.addSearchStringHistory( searchString );
        SearchPage.saveSearchScope( Arrays.asList( scope ) );

        if ( ( searchField != null ) && ( !searchField.isDisposed() ) )
        {
            searchField.setText( searchString );
            validateSearchField();
        }

        List<SchemaObject> results = search( searchString, scope );
        setSearchResultsLabel( searchString, results.size() );
        resultsTableViewer.setInput( results );
    }


    /**
     * Searches the objects corresponding to the search parameters.
     *
     * @param searchString
     *      the search String
     * @param scope
     *      the search Scope
     */
    private List<SchemaObject> search( String searchString, SearchScopeEnum[] scope )
    {
        List<SchemaObject> searchResults = new ArrayList<SchemaObject>();

        if ( searchString != null )
        {
            Pattern pattern = Pattern.compile( ".*" + searchString + ".*", Pattern.CASE_INSENSITIVE );

            SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
            if ( schemaHandler != null )
            {
                List<SearchScopeEnum> searchScope = new ArrayList<SearchScopeEnum>( Arrays.asList( scope ) );

                // Looping on attribute types
                List<AttributeTypeImpl> attributeTypes = schemaHandler.getAttributeTypes();
                for ( AttributeTypeImpl at : attributeTypes )
                {
                    // Aliases
                    if ( searchScope.contains( SearchScopeEnum.ALIASES ) )
                    {
                        if ( checkArray( pattern, at.getNames() ) )
                        {
                            searchResults.add( at );
                            continue;
                        }
                    }

                    // OID
                    if ( searchScope.contains( SearchScopeEnum.OID ) )
                    {
                        if ( checkString( pattern, at.getOid() ) )
                        {
                            searchResults.add( at );
                            continue;
                        }
                    }

                    // Description
                    if ( searchScope.contains( SearchScopeEnum.DESCRIPTION ) )
                    {
                        if ( checkString( pattern, at.getDescription() ) )
                        {
                            searchResults.add( at );
                            continue;
                        }
                    }

                    // Superior
                    if ( searchScope.contains( SearchScopeEnum.SUPERIOR ) )
                    {
                        if ( checkString( pattern, at.getSuperiorName() ) )
                        {
                            searchResults.add( at );
                            continue;
                        }
                    }

                    // Syntax
                    if ( searchScope.contains( SearchScopeEnum.SYNTAX ) )
                    {
                        if ( checkString( pattern, at.getSyntaxOid() ) )
                        {
                            searchResults.add( at );
                            continue;
                        }
                    }

                    // Matching Rules
                    if ( searchScope.contains( SearchScopeEnum.MATCHING_RULES ) )
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

                // Looping on object classes
                List<ObjectClassImpl> objectClasses = schemaHandler.getObjectClasses();
                for ( ObjectClassImpl oc : objectClasses )
                {
                    // Aliases
                    if ( searchScope.contains( SearchScopeEnum.ALIASES ) )
                    {
                        if ( checkArray( pattern, oc.getNames() ) )
                        {
                            searchResults.add( oc );
                            continue;
                        }
                    }

                    // OID
                    if ( searchScope.contains( SearchScopeEnum.OID ) )
                    {
                        if ( checkString( pattern, oc.getOid() ) )
                        {
                            searchResults.add( oc );
                            continue;
                        }
                    }

                    // Description
                    if ( searchScope.contains( SearchScopeEnum.DESCRIPTION ) )
                    {
                        if ( checkString( pattern, oc.getDescription() ) )
                        {
                            searchResults.add( oc );
                            continue;
                        }
                    }

                    // Superiors
                    if ( searchScope.contains( SearchScopeEnum.SUPERIORS ) )
                    {
                        if ( checkArray( pattern, oc.getSuperClassesNames() ) )
                        {
                            searchResults.add( oc );
                            continue;
                        }
                    }

                    // Mandatory Attributes
                    if ( searchScope.contains( SearchScopeEnum.MANDATORY_ATTRIBUTES ) )
                    {
                        if ( checkArray( pattern, oc.getMustNamesList() ) )
                        {
                            searchResults.add( oc );
                            continue;
                        }
                    }

                    // Optional Attributes
                    if ( searchScope.contains( SearchScopeEnum.OPTIONAL_ATTRIBUTES ) )
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
        List<SearchScopeEnum> searchScope = SearchPage.loadSearchScope();

        setSearchInput( searchString, searchScope.toArray( new SearchScopeEnum[0] ) );
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
            sb.append( "No search" );
        }
        else
        {
            // Search String
            sb.append( "'" + searchString + "'" );
            sb.append( " - " );

            // Search results count
            sb.append( resultsCount );
            sb.append( " " );
            if ( resultsCount > 1 )
            {
                sb.append( "matches" );
            }
            else
            {
                sb.append( "match" );
            }

            sb.append( " in workspace" );
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
            setSearchInput( searchString, SearchPage.loadSearchScope().toArray( new SearchScopeEnum[0] ) );
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
