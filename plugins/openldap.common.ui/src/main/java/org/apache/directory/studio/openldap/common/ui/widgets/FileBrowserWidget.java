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
import org.apache.directory.studio.ldapbrowser.common.widgets.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * The DirectoryBrowserWidget provides a combo with a history of recently
 * used directory and a browse button to open the directory browser.
 */
public class FileBrowserWidget extends org.apache.directory.studio.ldapbrowser.common.widgets.FileBrowserWidget
{
    /**
     * Creates a new instance of FileBrowserWidget.
     *
     * @param title The title
     * @param extensions The valid file extensions
     * @param type The type, one of {@link #TYPE_OPEN} or {@link #TYPE_SAVE}
     */
    public FileBrowserWidget( String title, String[] extensions, int type )
    {
        super( title, extensions, type );
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
        fileCombo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        if ( toolkit != null )
        {
            toolkit.adapt( fileCombo );
        }
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.widthHint = 50;
        fileCombo.setLayoutData( gd );
        fileCombo.setVisibleItemCount( 20 );
        fileCombo.addModifyListener( new ModifyListener()
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
                FileDialog fileDialog = new FileDialog( browseButton.getShell(), type );
                fileDialog.setText( title );

                fileDialog.setFilterExtensions( extensions );

                File file = new File( fileCombo.getText() );
                if ( file.isFile() )
                {
                    fileDialog.setFilterPath( file.getParent() );
                    fileDialog.setFileName( file.getName() );
                }
                else if ( file.isDirectory() )
                {
                    fileDialog.setFilterPath( file.getPath() );
                }
                else
                {
                    fileDialog.setFilterPath( BrowserCommonActivator.getDefault().getDialogSettings().get(
                        BrowserCommonConstants.DIALOGSETTING_KEY_RECENT_FILE_PATH ) );
                }

                String returnedFileName = fileDialog.open();
                if ( returnedFileName != null )
                {
                    fileCombo.setText( returnedFileName );
                    File file2 = new File( returnedFileName );
                    BrowserCommonActivator.getDefault().getDialogSettings().put(
                        BrowserCommonConstants.DIALOGSETTING_KEY_RECENT_FILE_PATH, file2.getParent() );
                }
            }
        } );

        // file history
        String[] history = HistoryUtils.load( BrowserCommonActivator.getDefault().getDialogSettings(),
            BrowserCommonConstants.DIALOGSETTING_KEY_FILE_HISTORY );
        fileCombo.setItems( history );
    }


    /**
     * {@inheritDoc}
     */
    public String getFilename()
    {
        String filename = fileCombo.getText();

        if ( ( filename != null ) && ( !"".equals( filename ) ) )
        {
            return filename;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void setFilename( String filename )
    {
        if ( filename == null )
        {
            fileCombo.setText( filename );
        }
        else
        {
            fileCombo.setText( filename );
        }
    }
}
