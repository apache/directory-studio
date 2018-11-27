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

package org.apache.directory.studio.ldapbrowser.common.widgets;


import java.io.File;

import org.apache.directory.studio.common.ui.HistoryUtils;
import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;


/**
 * The FileBrowserWidget provides a combo with a history of recently
 * used files and a browse button to open the file browser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FileBrowserWidget extends AbstractWidget
{
    /** The Constant TYPE_OPEN is used to create a Open file dialog. */
    public static final int TYPE_OPEN = SWT.OPEN;

    /** The Constant TYPE_SAVE is used to create a Save file dialog. */
    public static final int TYPE_SAVE = SWT.SAVE;

    /** The combo with the history of recently used files */
    protected Combo fileCombo;

    /** The button to launch the file browser */
    protected Button browseButton;

    /** The title */
    protected String title;

    /** File extensions used within the launched file browser */
    protected String[] extensions;

    /** The type */
    protected int type;


    /**
     * Creates a new instance of FileBrowserWidget.
     *
     * @param title The title
     * @param extensions The valid file extensions
     * @param type The type, one of {@link #TYPE_OPEN} or {@link #TYPE_SAVE}
     */
    public FileBrowserWidget( String title, String[] extensions, int type )
    {
        this.title = title;
        this.extensions = extensions;
        this.type = type;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        // Combo
        fileCombo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
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
        browseButton = BaseWidgetUtils.createButton( parent, Messages.getString( "FileBrowserWidget.BrowseButton" ), 1 ); //$NON-NLS-1$
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
     * Gets the filename.
     * 
     * @return the filename
     */
    public String getFilename()
    {
        return fileCombo.getText();
    }


    /**
     * Sets the filename.
     * 
     * @param filename the filename
     */
    public void setFilename( String filename )
    {
        fileCombo.setText( filename );
    }


    /**
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserCommonActivator.getDefault().getDialogSettings(),
            BrowserCommonConstants.DIALOGSETTING_KEY_FILE_HISTORY, fileCombo.getText() );
    }


    /**
     * Sets the focus.
     */
    public void setFocus()
    {
        fileCombo.setFocus();
    }


    /**
     * Enables or disables the widget.
     * 
     * @param b true to enable the widget, false otherwise
     */
    public void setEnabled( boolean b )
    {
        fileCombo.setEnabled( b );
        browseButton.setEnabled( b );
    }

}
