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


import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetUniversalListener;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;


/**
 * This class implements an editor page for the Table Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TableEditorPage extends AbstractCombinedEntryEditorPage
{
    /** The entry editor widget */
    private EntryEditorWidget entryEditorWidget;

    /** The listener associated with the entry editor widget */
    private EntryEditorWidgetUniversalListener listener;


    /**
     * Creates a new instance of TableEditorPage.
     *
     * @param editor the associated editor
     */
    public TableEditorPage( CombinedEntryEditor editor )
    {
        super( editor );

        // Creating and assigning the tab item
        CTabItem tabItem = new CTabItem( editor.getTabFolder(), SWT.NONE );
        tabItem.setText( Messages.getString( "TableEditorPage.TableEditor" ) ); //$NON-NLS-1$
        tabItem
            .setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_BROWSER_SINGLETAB_ENTRYEDITOR ) );
        setTabItem( tabItem );
    }


    /**
     * {@inheritDoc}
     */
    public void init()
    {
        super.init();

        EditorConfiguration configuration = new EditorConfiguration();

        entryEditorWidget = new EntryEditorWidget( configuration );
        entryEditorWidget.createWidget( getEditor().getTabFolder() );

        TableEditorPageActionGroup entryEditorActionGroup = new TableEditorPageActionGroup( getEditor(),
            entryEditorWidget, configuration );

        entryEditorActionGroup.fillToolBar( entryEditorWidget.getToolBarManager() );
        entryEditorActionGroup.fillMenu( entryEditorWidget.getMenuManager() );
        entryEditorActionGroup.fillContextMenu( entryEditorWidget.getContextMenuManager() );

        setInput();

        getEditor().getSite().setSelectionProvider( entryEditorWidget.getViewer() );
        listener = new EntryEditorWidgetUniversalListener( entryEditorWidget.getViewer(), configuration,
            entryEditorActionGroup, entryEditorActionGroup.getOpenDefaultEditorAction() );

        entryEditorActionGroup.setInput( getEditor().getEntryEditorInput().getSharedWorkingCopy( getEditor() ) );

        getEditor().getSite().setSelectionProvider( entryEditorWidget.getViewer() );

        getTabItem().setControl( entryEditorWidget.getControl() );
    }


    /**
     * Sets the input to the Entry Editor Widget.
     */
    private void setInput()
    {
        if ( entryEditorWidget != null )
        {
            entryEditorWidget.getViewer().setInput(
                getEditor().getEntryEditorInput().getSharedWorkingCopy( getEditor() ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        if ( entryEditorWidget != null )
        {
            entryEditorWidget.getViewer().refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        if ( entryEditorWidget != null )
        {
            entryEditorWidget.setFocus();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( entryEditorWidget != null )
        {
            entryEditorWidget.dispose();
        }

        if ( listener != null )
        {
            listener.dispose();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void editorInputChanged()
    {
        if ( isInitialized() )
        {
            setInput();
        }
    }

    /**
     * A special configuration for the {@link TableEditorPage}.
     */
    class EditorConfiguration extends EntryEditorWidgetConfiguration
    {
        /**
        * {@inheritDoc}
        */
        public ValueEditorManager getValueEditorManager( TreeViewer viewer )
        {
            if ( valueEditorManager == null )
            {
                valueEditorManager = new ValueEditorManager( viewer.getTree(), true, false );
            }

            return valueEditorManager;
        }
    }
}
