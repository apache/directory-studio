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

package org.apache.directory.studio.ldapbrowser.common.dialogs;


import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserActionGroup;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserConfiguration;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserUniversalListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to select an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SelectEntryDialog extends Dialog
{

    /** The dialog title. */
    private String title;

    /** The root entry. */
    private IEntry rootEntry;

    /** The initial entry. */
    private IEntry initialEntry;

    /** The selected entry. */
    private IEntry selectedEntry;

    /** The browser configuration. */
    private BrowserConfiguration browserConfiguration;

    /** The browser universal listener. */
    private BrowserUniversalListener browserUniversalListener;

    /** The browser action group. */
    private BrowserActionGroup browserActionGroup;

    /** The browser widget. */
    private BrowserWidget browserWidget;


    /**
     * Creates a new instance of SelectEntryDialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param rootEntry the root entry
     * @param initialEntry the initial entry
     */
    public SelectEntryDialog( Shell parentShell, String title, IEntry rootEntry, IEntry initialEntry )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = title;
        this.rootEntry = rootEntry;
        this.initialEntry = initialEntry;
        this.selectedEntry = null;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        if ( browserWidget != null )
        {
            browserConfiguration.dispose();
            browserConfiguration = null;
            browserActionGroup.deactivateGlobalActionHandlers();
            browserActionGroup.dispose();
            browserActionGroup = null;
            browserUniversalListener.dispose();
            browserUniversalListener = null;
            browserWidget.dispose();
            browserWidget = null;
        }
        return super.close();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        selectedEntry = initialEntry;
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed()
    {
        selectedEntry = null;
        super.cancelPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // create configuration
        browserConfiguration = new BrowserConfiguration();

        // create main widget
        browserWidget = new BrowserWidget( browserConfiguration, null );
        browserWidget.createWidget( composite );
        browserWidget.setInput( new IEntry[]
            { rootEntry } );

        // create actions and context menu (and register global actions)
        browserActionGroup = new BrowserActionGroup( browserWidget, browserConfiguration );
        browserActionGroup.fillToolBar( browserWidget.getToolBarManager() );
        browserActionGroup.fillMenu( browserWidget.getMenuManager() );
        browserActionGroup.fillContextMenu( browserWidget.getContextMenuManager() );
        browserActionGroup.activateGlobalActionHandlers();

        // create the listener
        browserUniversalListener = new BrowserUniversalListener( browserWidget.getViewer() );

        browserWidget.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                if ( !event.getSelection().isEmpty() )
                {
                    Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( o instanceof IEntry )
                    {
                        initialEntry = ( IEntry ) o;
                    }
                }
            }
        } );

        browserWidget.getViewer().expandToLevel( 2 );
        if ( initialEntry != null )
        {
            IEntry entry = this.initialEntry;
            browserWidget.getViewer().reveal( entry );
            browserWidget.getViewer().refresh( entry, true );
            browserWidget.getViewer().setSelection( new StructuredSelection( entry ), true );
            browserWidget.getViewer().setSelection( new StructuredSelection( entry ), true );
        }

        applyDialogFont( composite );

        browserWidget.setFocus();

        return composite;
    }


    /**
     * Gets the selected entry.
     * 
     * @return the selected entry
     */
    public IEntry getSelectedEntry()
    {
        return selectedEntry;
    }

}
