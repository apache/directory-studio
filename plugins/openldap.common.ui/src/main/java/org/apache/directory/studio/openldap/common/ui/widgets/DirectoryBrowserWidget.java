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
package org.apache.directory.studio.openldap.common.ui.widgets;


import java.io.File;

import org.apache.directory.studio.common.ui.HistoryUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.Messages;
import org.apache.directory.studio.openldap.common.ui.OpenLdapCommonUiConstants;
import org.apache.directory.studio.openldap.common.ui.OpenLdapCommonUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * The DirectoryBrowserWidget provides a combo with a history of recently
 * used directory and a browse button to open the directory browser.
 */
public class DirectoryBrowserWidget extends BrowserWidget
{
    /** The combo with the history of recently used directories */
    protected Combo directoryCombo;

    /** The button to launch the file browser */
    protected Button browseButton;

    /** The title */
    protected String title;


    /**
     * Creates a new instance of DirectoryBrowserWidget.
     *
     * @param title The title
     */
    public DirectoryBrowserWidget( String title )
    {
        this.title = title;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        createWidget( parent, null );
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidget( Composite parent, FormToolkit toolkit )
    {
        // Combo
        directoryCombo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        if ( toolkit != null )
        {
            toolkit.adapt( directoryCombo );
        }
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.widthHint = 50;
        directoryCombo.setLayoutData( gd );
        directoryCombo.setVisibleItemCount( 20 );
        directoryCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );

        // Button
        if ( toolkit != null )
        {
            browseButton = toolkit.createButton( parent,
                Messages.getString( "FileBrowserWidget.BrowseButton" ), SWT.PUSH ); //$NON-NLS-1$
        }
        else
        {
            browseButton = BaseWidgetUtils.createButton( parent,
                Messages.getString( "FileBrowserWidget.BrowseButton" ), 1 ); //$NON-NLS-1$
        }
        browseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                DirectoryDialog directoryDialog = new DirectoryDialog( browseButton.getShell() );
                directoryDialog.setText( title );

                File file = new File( directoryCombo.getText() );
                if ( file.isFile() )
                {
                    directoryDialog.setFilterPath( file.getParent() );
                }
                else if ( file.isDirectory() )
                {
                    directoryDialog.setFilterPath( file.getPath() );
                }
                else
                {
                    directoryDialog.setFilterPath( BrowserCommonActivator.getDefault().getDialogSettings().get(
                        BrowserCommonConstants.DIALOGSETTING_KEY_RECENT_FILE_PATH ) );
                }

                String returnedFileName = directoryDialog.open();
                if ( returnedFileName != null )
                {
                    directoryCombo.setText( returnedFileName );
                    File file2 = new File( returnedFileName );
                    BrowserCommonActivator.getDefault().getDialogSettings().put(
                        BrowserCommonConstants.DIALOGSETTING_KEY_RECENT_FILE_PATH, file2.getParent() );
                }
            }
        } );

        loadDialogSettings();
    }


    /**
     * Gets the directory path.
     * 
     * @return the directory path or <code>null</code>
     */
    public String getDirectoryPath()
    {
        String directoryPath = directoryCombo.getText();

        if ( ( directoryPath != null ) && ( !"".equals( directoryPath ) ) )
        {
            return directoryPath;
        }

        return null;
    }


    /**
     * Sets the directory path.
     * 
     * @param directoryPath the directory path
     */
    public void setDirectoryPath( String directoryPath )
    {
        if ( directoryPath == null )
        {
            directoryCombo.setText( "" );
        }
        else
        {
            directoryCombo.setText( directoryPath );
        }
    }


    /**
     * Saves dialog settings.
     */
    public void loadDialogSettings()
    {
        String[] history = HistoryUtils.load( OpenLdapCommonUiPlugin.getDefault().getDialogSettings(),
            OpenLdapCommonUiConstants.DIALOGSETTING_KEY_DIRECTORY_HISTORY );
        directoryCombo.setItems( history );
    }


    /**
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( OpenLdapCommonUiPlugin.getDefault().getDialogSettings(),
            OpenLdapCommonUiConstants.DIALOGSETTING_KEY_DIRECTORY_HISTORY, directoryCombo.getText() );
    }


    /**
     * Sets the focus.
     */
    public void setFocus()
    {
        directoryCombo.setFocus();
    }


    /**
     * Enables or disables the widget.
     * 
     * @param b true to enable the widget, false otherwise
     */
    public void setEnabled( boolean b )
    {
        directoryCombo.setEnabled( b );
        browseButton.setEnabled( b );
    }
}
