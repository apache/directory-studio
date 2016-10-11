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
package org.apache.directory.studio.combinededitor.editor;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import org.apache.directory.studio.combinededitor.CombinedEditorPlugin;
import org.apache.directory.studio.combinededitor.CombinedEditorPluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.actions.SwitchTemplateListener;
import org.apache.directory.studio.templateeditor.editor.TemplateEditorWidget;
import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This class implements the Template Entry Editor.
 * <p>
 * This editor is composed of a three tabs TabFolder object:
 * <ul>
 *  <li>the Template Editor itself</li>
 *  <li>the Table Editor</li>
 *  <li>the LDIF Editor</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class CombinedEntryEditor extends EditorPart implements INavigationLocationProvider, IEntryEditor,
    IReusableEditor, IShowEditorInput, SwitchTemplateListener
{
    /** The Template Editor page */
    private TemplateEditorPage templateEditorPage;
    
    /** The Table Editor page */
    private TableEditorPage tableEditorPage;
    
    /** The LDIF Editor page */
    private LdifEditorPage ldifEditorPage;

    /** The Tab Folder */
    private CTabFolder tabFolder;

    /** The tab associated with the Template Editor */
    private CTabItem templateEditorTab;
    
    /** The tab associated with the Table Editor */
    private CTabItem tableEditorTab;
    
    /** The tab associated with the LDIF Editor */
    private CTabItem ldifEditorTab;


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );
        setInput( input );
    }


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        // Creating the TabFolder
        tabFolder = new CTabFolder( parent, SWT.BOTTOM );

        // Creating the editor pages and tab items
        // The Template editor item
        templateEditorPage = new TemplateEditorPage( this );
        templateEditorTab = templateEditorPage.getTabItem();
        
        // The Table editor item
        tableEditorPage = new TableEditorPage( this );
        tableEditorTab = tableEditorPage.getTabItem();
        
        // The LDIF editor item
        ldifEditorPage = new LdifEditorPage( this );
        ldifEditorTab = ldifEditorPage.getTabItem();

        // Getting the preference store
        IPreferenceStore store = CombinedEditorPlugin.getDefault().getPreferenceStore();

        // Getting the default editor
        int defaultEditor = store.getInt( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR );
        
        switch ( defaultEditor )
        {
            case CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE :
                // Getting the boolean indicating if the user wants to auto-switch the template editor
                boolean autoSwitchToAnotherEditor = store
                    .getBoolean( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR );
                
                if ( autoSwitchToAnotherEditor && !canBeHandledWithATemplate() )
                {
                    switch ( store.getInt( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR ) )
                    {
                        case CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_TABLE :
                            // Selecting the Table Editor
                            tabFolder.setSelection( tableEditorTab );
                            // Forcing the initialization of the first tab item, 
                            // because the listener is not triggered when selecting a tab item programmatically
                            tableEditorPage.init();
                            break;
                            
                        case  CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_LDIF :
                            // Selecting the LDIF Editor
                            tabFolder.setSelection( ldifEditorTab );
                            // Forcing the initialization of the first tab item, 
                            // because the listener is not triggered when selecting a tab item programmatically
                            ldifEditorPage.init();
                    }
                }
                else
                {
                    // Selecting the Template Editor
                    tabFolder.setSelection( templateEditorTab );
                    // Forcing the initialization of the first tab item, 
                    // because the listener is not triggered when selecting a tab item programmatically
                    templateEditorPage.init();
                }
                
                break;
            
            case CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TABLE :
                // Selecting the Table Editor
                tabFolder.setSelection( tableEditorTab );
                // Forcing the initialization of the first tab item, 
                // because the listener is not triggered when selecting a tab item programmatically
                tableEditorPage.init();
                
                break;
        
            case CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_LDIF :
                // Selecting the LDIF Editor
                tabFolder.setSelection( ldifEditorTab );
                // Forcing the initialization of the first tab item, 
                // because the listener is not triggered when selecting a tab item programmatically
                ldifEditorPage.init(); 
        }
    }


    /**
     * {@inheritDoc}
     */
    public void workingCopyModified( Object source )
    {
        update();

        if ( !isAutoSave() )
        {
            // mark as dirty
            firePropertyChange( PROP_DIRTY );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        //
        // Disposing the TabFolder, its tabs and Editor Pages
        //

        // Tab Folder
        if ( ( tabFolder != null ) && ( !tabFolder.isDisposed() ) )
        {
            tabFolder.dispose();
        }

        // Template Editor Tab
        if ( ( templateEditorTab != null ) && ( !templateEditorTab.isDisposed() ) )
        {
            templateEditorTab.dispose();
        }

        // Table Editor Tab
        if ( ( tableEditorTab != null ) && ( !tableEditorTab.isDisposed() ) )
        {
            tableEditorTab.dispose();
        }

        // LDIF Editor Tab
        if ( ( ldifEditorTab != null ) && ( !ldifEditorTab.isDisposed() ) )
        {
            ldifEditorTab.dispose();
        }

        // Template Editor Page
        if ( templateEditorPage != null )
        {
            templateEditorPage.dispose();
        }

        // Table Editor Page
        if ( tableEditorPage != null )
        {
            tableEditorPage.dispose();
        }

        // LDIF Editor Page
        if ( ldifEditorPage != null )
        {
            ldifEditorPage.dispose();
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public boolean canHandle( IEntry entry )
    {
    	return true;
    }


    /**
     * Indicates whether or not the entry can be handled with a (at least) template.
     *
     * @param entry the entry
     * @return <code>true</code> if the entry can be handled with a template,
     * <code>false</code> if not.
     */
    private boolean canBeHandledWithATemplate( IEntry entry )
    {
        return ( EntryTemplatePluginUtils.getMatchingTemplates( entry ).size() > 0 );
    }


    /**
     * Indicates whether or not the input entry can be handled with a (at least) template.
     *
     * @return <code>true</code> if the input entry can be handled with a template,
     *      <code>false</code> if not.
     */
    private boolean canBeHandledWithATemplate()
    {
        IEditorInput editorInput = getEditorInput();
        
        if ( editorInput instanceof EntryEditorInput )
        {
            IEntry entry = ( ( EntryEditorInput ) editorInput ).getResolvedEntry();
            
            if ( entry != null )
            {
                return canBeHandledWithATemplate( entry );
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        if ( !isAutoSave() )
        {
            EntryEditorInput eei = getEntryEditorInput();
            eei.saveSharedWorkingCopy( true, this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return getEntryEditorInput().isSharedWorkingCopyDirty( this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
        // Nothing to do, will never occur as "Save As..." is not allowed
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        if ( ( tabFolder != null ) && ( !tabFolder.isDisposed() ) )
        {
            tabFolder.setFocus();
        }
    }


    /**
     * {@inheritDoc}
     */
    public EntryEditorInput getEntryEditorInput()
    {
        Object editorInput = getEditorInput();
        
        if ( editorInput instanceof EntryEditorInput )
        {
            return ( EntryEditorInput ) editorInput;
        }

        return null;
    }


    /**
     * Updates the selected AbstractTemplateEntryEditorPage.
     */
    private void update()
    {
        ICombinedEntryEditorPage selectedPage = getEditorPageFromSelectedTab();
        
        if ( selectedPage != null )
        {
            selectedPage.update();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        setPartName( input.getName() );
    }


    /**
     * {@inheritDoc}
     */
    public INavigationLocation createEmptyNavigationLocation()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public INavigationLocation createNavigationLocation()
    {
        return new CombinedEntryEditorNavigationLocation( this );
    }


    /**
     * {@inheritDoc}
     */
    public void showEditorInput( IEditorInput input )
    {
        if ( input instanceof EntryEditorInput )
        {
            /*
             * Optimization: no need to set the input again if the same input is already set
             */
            if ( getEntryEditorInput() != null
                && getEntryEditorInput().getResolvedEntry() == ( ( EntryEditorInput ) input ).getResolvedEntry() )
            {
                return;
            }

            // If the editor is dirty, let's ask for a save before changing the input
            if ( isDirty() )
            {
                if ( !EntryEditorUtils.askSaveSharedWorkingCopyBeforeInputChange( this ) )
                {
                    return;
                }
            }

            // now set the real input and mark history location
            setInput( input );
            getSite().getPage().getNavigationHistory().markLocation( this );
            firePropertyChange( BrowserUIConstants.INPUT_CHANGED );

            // Getting the preference store
            IPreferenceStore store = CombinedEditorPlugin.getDefault().getPreferenceStore();

            // Getting the default editor
            switch ( store.getInt( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR ) )
            {
                case CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE :
                    // Getting the boolean indicating if the user wants to auto-switch the template editor
                    boolean autoSwitchToAnotherEditor = store
                        .getBoolean( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR );
                    
                    if ( autoSwitchToAnotherEditor && !canBeHandledWithATemplate() )
                    {
                        switch ( store.getInt( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR ) )
                        {
                            case  CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_TABLE :
                                // Selecting the Table Editor
                                tabFolder.setSelection( tableEditorTab );
                                // Forcing the initialization of the first tab item, 
                                // because the listener is not triggered when selecting a tab item programmatically
                                tableEditorPage.init();
                                break;
                                
                            case CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_LDIF :
                                // Selecting the LDIF Editor
                                tabFolder.setSelection( ldifEditorTab );
                                // Forcing the initialization of the first tab item, 
                                // because the listener is not triggered when selecting a tab item programmatically
                                ldifEditorPage.init();
                        }
                    }
                    else
                    {
                        // Selecting the Template Editor
                        tabFolder.setSelection( templateEditorTab );
                        // Forcing the initialization of the first tab item, 
                        // because the listener is not triggered when selecting a tab item programmatically
                        templateEditorPage.init();
                    }
                    
                    break;

                case CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TABLE :
                    // Selecting the Table Editor
                    tabFolder.setSelection( tableEditorTab );
                    // Forcing the initialization of the first tab item, 
                    // because the listener is not triggered when selecting a tab item programmatically
                    tableEditorPage.init();
                    break;
                    
                case  CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_LDIF :
                    // Selecting the LDIF Editor
                    tabFolder.setSelection( ldifEditorTab );
                    // Forcing the initialization of the first tab item, 
                    // because the listener is not triggered when selecting a tab item programmatically
                    ldifEditorPage.init(); 
                    break;
            }

            // Noticing all pages that the editor input has changed
            templateEditorPage.editorInputChanged();
            tableEditorPage.editorInputChanged();
            ldifEditorPage.editorInputChanged();
        }
    }


    /**
     * Gets the {@link ICombinedEntryEditorPage} associated with the selected tab.
     *
     * @return the {@link ICombinedEntryEditorPage} associated with the selected tab
     */
    private ICombinedEntryEditorPage getEditorPageFromSelectedTab()
    {
        CTabItem selectedTabItem = getSelectedTabItem();
        
        if ( selectedTabItem != null )
        {
            // Template Editor Tab
            if ( selectedTabItem.equals( templateEditorTab ) )
            {
                return templateEditorPage;
            }
            // Table Editor Tab
            else if ( selectedTabItem.equals( tableEditorTab ) )
            {
                return tableEditorPage;
            }
            // LDIF Editor Tab
            else if ( selectedTabItem.equals( ldifEditorTab ) )
            {
                return ldifEditorPage;
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void templateSwitched( TemplateEditorWidget templateEditorWidget, Template template )
    {
        if ( templateEditorPage != null )
        {
            templateEditorPage.templateSwitched( templateEditorWidget, template );
        }
    }


    /**
     * Returns the {@link CTabFolder} associated with the editor.
     *
     * @return the {@link CTabFolder} associated with the editor
     */
    public CTabFolder getTabFolder()
    {
        return tabFolder;
    }


    /**
     * Returns the currently selected {@link CTabItem}.
     *
     * @return the currently selected {@link CTabItem}
     */
    public CTabItem getSelectedTabItem()
    {
        return tabFolder.getSelection();
    }


    /**
     * Get the {@link TemplateEditorPage} page.
     *
     * @return the {@link TemplateEditorPage} page
     *
    public TemplateEditorPage getTemplateEditorPage()
    {
        return templateEditorPage;
    }


    /**
     * Get the {@link TableEditorPage} page.
     *
     * @return the {@link TableEditorPage} page
     *
    public TableEditorPage getTableEditorPage()
    {
        return tableEditorPage;
    }


    /**
     * Get the {@link LdifEditorPage} page.
     *
     * @return the {@link LdifEditorPage} page
     *
    public LdifEditorPage getLdifEditorPage()
    {
        return ldifEditorPage;
    }*/
}
