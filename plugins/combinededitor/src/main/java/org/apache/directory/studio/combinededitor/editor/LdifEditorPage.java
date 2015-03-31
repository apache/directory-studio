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


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifeditor.widgets.LdifEditorWidget;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifInvalidContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import org.apache.directory.studio.combinededitor.actions.FetchOperationalAttributesAction;
import org.apache.directory.studio.templateeditor.actions.EditorPagePropertiesAction;
import org.apache.directory.studio.templateeditor.actions.RefreshAction;
import org.apache.directory.studio.templateeditor.actions.SimpleActionProxy;


/**
 * This class implements an editor page for the LDIF Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifEditorPage extends AbstractCombinedEntryEditorPage
{
    /** The LDIF editor widget */
    private LdifEditorWidget ldifEditorWidget;

    /** A count to know if the editor page has updated the shared working copy */
    private int hasUpdatedSharedWorkingCopyCount = 0;

    /** The modify listener for the widget */
    private WidgetModifyListener listener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            updateSharedWorkingCopy();
        }
    };

    /** The context menu */
    private Menu contextMenu;


    /**
     * Creates a new instance of LdifEditorPage.
     *
     * @param editor
     *      the associated editor
     */
    public LdifEditorPage( CombinedEntryEditor editor )
    {
        super( editor );

        // Creating and assigning the tab item
        CTabItem tabItem = new CTabItem( editor.getTabFolder(), SWT.NONE );
        tabItem.setText( Messages.getString( "LdifEditorPage.LDIFEditor" ) ); //$NON-NLS-1$
        tabItem.setImage( LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_BROWSER_LDIFEDITOR ) );
        setTabItem( tabItem );
    }


    /**
     * {@inheritDoc}
     */
    public void init()
    {
        super.init();

        ldifEditorWidget = new LdifEditorWidget( null, "", true ); //$NON-NLS-1$
        ldifEditorWidget.createWidget( getEditor().getTabFolder() );

        // Creating a new menu manager
        Control sourceViewerControl = ldifEditorWidget.getSourceViewer().getControl();

        MenuManager menuManager = new MenuManager();
        contextMenu = menuManager.createContextMenu( sourceViewerControl );
        sourceViewerControl.setMenu( contextMenu );

        IEditorSite site = getEditor().getEditorSite();
        IActionBars bars = site.getActionBars();

        Action cutAction = new Action( "Cut" )
        {
            public void run()
            {
                ldifEditorWidget.getSourceViewer().doOperation( SourceViewer.CUT );
            }
        };

        Action copyAction = new Action( "Copy" )
        {
            public void run()
            {
                ldifEditorWidget.getSourceViewer().doOperation( SourceViewer.COPY );
            }
        };

        Action pasteAction = new Action( "Paste" )
        {
            public void run()
            {
                ldifEditorWidget.getSourceViewer().doOperation( SourceViewer.PASTE );
            }
        };

        bars.setGlobalActionHandler( ActionFactory.CUT.getId(), cutAction );
        bars.setGlobalActionHandler( ActionFactory.COPY.getId(), copyAction );
        bars.setGlobalActionHandler( ActionFactory.PASTE.getId(), pasteAction );

        // TODO remove this
        menuManager.add( ActionFactory.CUT.create( PlatformUI.getWorkbench().getActiveWorkbenchWindow() ) );
        menuManager.add( ActionFactory.COPY.create( PlatformUI.getWorkbench().getActiveWorkbenchWindow() ) );
        menuManager.add( ActionFactory.PASTE.create( PlatformUI.getWorkbench().getActiveWorkbenchWindow() ) );

        menuManager.add( new Separator() );
        menuManager.add( new RefreshAction( getEditor() ) );
        menuManager.add( new FetchOperationalAttributesAction( getEditor() ) );
        menuManager.add( new Separator() );
        menuManager.add( new SimpleActionProxy( new EditorPagePropertiesAction( getEditor() ) ) );

        setInput();

        getTabItem().setControl( ldifEditorWidget.getControl() );
    }


    /**
     * Adds the listener.
     */
    private void addListener()
    {
        ldifEditorWidget.addWidgetModifyListener( listener );
    }


    /**
     * Removes the listener.
     */
    private void removeListener()
    {
        ldifEditorWidget.removeWidgetModifyListener( listener );
    }


    /**
     * Updates the shared working copy entry.
     */
    private void updateSharedWorkingCopy()
    {
        LdifFile ldifModel = ldifEditorWidget.getLdifModel();

        // only continue if the LDIF model is valid
        LdifRecord[] records = ldifModel.getRecords();
        if ( records.length != 1 || !( records[0] instanceof LdifContentRecord ) || !records[0].isValid()
            || !records[0].getDnLine().isValid() )
        {
            return;
        }
        for ( LdifContainer ldifContainer : ldifModel.getContainers() )
        {
            if ( ldifContainer instanceof LdifInvalidContainer )
            {
                return;
            }
        }

        // update shared working copy
        try
        {
            LdifContentRecord modifiedRecord = ( LdifContentRecord ) records[0];
            EntryEditorInput input = getEditor().getEntryEditorInput();
            IEntry sharedWorkingCopyEntry = input.getSharedWorkingCopy( getEditor() );

            IBrowserConnection browserConnection = input.getSharedWorkingCopy( getEditor() ).getBrowserConnection();
            DummyEntry modifiedEntry = ModelConverter.ldifContentRecordToEntry( modifiedRecord, browserConnection );
            ( ( DummyEntry ) sharedWorkingCopyEntry ).setDn( modifiedEntry.getDn() );
            new CompoundModification().replaceAttributes( modifiedEntry, sharedWorkingCopyEntry, this );

            // Increasing the update count
            hasUpdatedSharedWorkingCopyCount++;
        }
        catch ( LdapInvalidDnException e )
        {
            throw new RuntimeException( e );
        }
    }


    /**
     * Sets the input to the LDIF Editor widget.
     */
    private void setInput()
    {
        removeListener();

        if ( ldifEditorWidget != null )
        {
            SourceViewer sourceViewer = ldifEditorWidget.getSourceViewer();
            IEntry entry = getEditor().getEntryEditorInput().getSharedWorkingCopy( getEditor() );
            if ( entry != null )
            {
                // Making the source viewer editable
                sourceViewer.setEditable( true );

                // Showing the context menu
                sourceViewer.getControl().setMenu( contextMenu );

                // Assigning the content to the source viewer
                sourceViewer.getDocument().set(
                    ModelConverter.entryToLdifContentRecord( entry )
                        .toFormattedString( Utils.getLdifFormatParameters() ) );
            }
            else
            {
                // Making the source viewer non editable
                sourceViewer.setEditable( false );

                // Hiding the context menu
                sourceViewer.getControl().setMenu( null );

                // Assigning a blank content to the source viewer
                sourceViewer.getDocument().set( "" ); //$NON-NLS-1$
            }
        }

        addListener();
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        // Checking if the editor page is the source of this update
        if ( hasUpdatedSharedWorkingCopyCount != 0 )
        {
            // Decreasing the number of updates to be discarded
            hasUpdatedSharedWorkingCopyCount--;
        }
        else
        {
            // Reseting the input
            setInput();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        // Nothing to do.
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
}
