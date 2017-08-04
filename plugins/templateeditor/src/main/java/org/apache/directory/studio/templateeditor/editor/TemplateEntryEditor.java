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
package org.apache.directory.studio.templateeditor.editor;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;


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
public abstract class TemplateEntryEditor extends EditorPart implements INavigationLocationProvider, IEntryEditor,
    IReusableEditor, IShowEditorInput
{
    /** The Template Editor page */
    private TemplateEditorWidget templateEditorWidget;


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
        templateEditorWidget = new TemplateEditorWidget( this );
        templateEditorWidget.init( parent );
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
        // Template Editor Widget
        if ( templateEditorWidget != null )
        {
            templateEditorWidget.dispose();
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public boolean canHandle( IEntry entry )
    {
        int useTemplateEditorFor = EntryTemplatePlugin.getDefault().getPreferenceStore().getInt(
            EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR );
        if ( useTemplateEditorFor == EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ANY_ENTRY )
        {
            return true;
        }
        else if ( useTemplateEditorFor == EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ENTRIES_WITH_TEMPLATE )
        {
            if ( entry == null )
            {
                return true;
            }

            return canBeHandledWithATemplate( entry );
        }

        return false;
    }


    /**
     * Indicates whether or not the entry can be handled with a (at least) template.
     *
     * @param entry
     *      the entry
     * @return
     *      <code>true</code> if the entry can be handled with a template,
     *      <code>false</code> if not.
     */
    private boolean canBeHandledWithATemplate( IEntry entry )
    {
        return ( EntryTemplatePluginUtils.getMatchingTemplates( entry ).size() > 0 );
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
        if ( templateEditorWidget != null )
        {
            templateEditorWidget.setFocus();
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
        if ( templateEditorWidget != null )
        {
            templateEditorWidget.update();
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
        return new TemplateEntryEditorNavigationLocation( this );
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

            // Updating the input on the template editor widget
            templateEditorWidget.editorInputChanged();
        }
    }

}
