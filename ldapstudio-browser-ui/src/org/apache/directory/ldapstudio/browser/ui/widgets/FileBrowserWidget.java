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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.io.File;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;


public class FileBrowserWidget extends BrowserWidget
{

    public static final int TYPE_OPEN = SWT.OPEN;

    public static final int TYPE_SAVE = SWT.SAVE;

    private Combo fileCombo;

    private Button browseButton;

    private String title;

    private String[] extensions;

    private int type;


    public FileBrowserWidget( String title, String[] extensions, int type )
    {
        this.title = title;
        this.extensions = extensions;
        this.type = type;
    }


    public void createWidget( final Composite parent )
    {

        // Combo
        fileCombo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
        fileCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );

        // Button
        browseButton = BaseWidgetUtils.createButton( parent, "Bro&wse...", 1 );
        browseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                FileDialog fileDialog = new FileDialog( parent.getShell(), type );
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
                    fileDialog.setFilterPath( BrowserUIPlugin.getDefault().getDialogSettings().get(
                        BrowserUIConstants.DIALOGSETTING_KEY_RECENT_FILE_PATH ) );
                }

                String returnedFileName = fileDialog.open();
                if ( returnedFileName != null )
                {
                    fileCombo.setText( returnedFileName );
                    File file2 = new File( returnedFileName );
                    BrowserUIPlugin.getDefault().getDialogSettings().put(
                        BrowserUIConstants.DIALOGSETTING_KEY_RECENT_FILE_PATH, file2.getParent() );
                }
            }
        } );

        // filter history
        String[] history = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_FILE_HISTORY );
        fileCombo.setItems( history );
    }


    public String getFilename()
    {
        return this.fileCombo.getText();
    }


    public void setFilename( String filename )
    {
        this.fileCombo.setText( filename );
    }


    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_FILE_HISTORY, this.fileCombo.getText() );
    }


    public void setFocus()
    {
        fileCombo.setFocus();
    }


    public void setEnabled( boolean b )
    {
        this.fileCombo.setEnabled( b );
        this.browseButton.setEnabled( b );
    }

}
