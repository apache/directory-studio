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

package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectBrowserConnectionDialog;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * The BrowserConnectionWidget could be used to select an {@link IBrowserConnection}. 
 * It is composed of a text to display the selected connection
 * and a browse button to open a {@link SelectBrowserConnectionDialog}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserConnectionWidget extends BrowserWidget
{

    /** The connection text, displays the selected connection */
    private Text browserConnectionText;

    /** The connection browse button, opens the dialog */
    private Button connectionBrowseButton;

    /** The selected connection */
    private IBrowserConnection selectedBrowserConnection;


    /**
     * Creates a new instance of ConnectionWidget.
     * 
     * @param connection the initial connection
     */
    public BrowserConnectionWidget( IBrowserConnection connection )
    {
        this.selectedBrowserConnection = connection;
    }


    /**
     * Creates a new instance of ConnectionWidget with no initial connection.
     */
    public BrowserConnectionWidget()
    {
        this.selectedBrowserConnection = null;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( final Composite parent )
    {
        // Text
        browserConnectionText = BaseWidgetUtils.createReadonlyText( parent, "", 1 );

        // Button
        connectionBrowseButton = BaseWidgetUtils.createButton( parent, "B&rowse...", 1 );
        connectionBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                SelectBrowserConnectionDialog dialog = new SelectBrowserConnectionDialog( parent.getShell(),
                    "Select Connection", selectedBrowserConnection );
                dialog.open();
                IBrowserConnection browserConnection = dialog.getSelectedBrowserConnection();
                if ( browserConnection != null )
                {
                    setBrowserConnection( browserConnection );
                    notifyListeners();
                }
            }
        } );

        // initial values
        setBrowserConnection( selectedBrowserConnection );
    }


    /**
     * Gets the selected connection.
     * 
     * @return the connection
     */
    public IBrowserConnection getBrowserConnection()
    {
        return selectedBrowserConnection;
    }


    /**
     * Sets the selected connection.
     * 
     * @param connection the connection
     */
    public void setBrowserConnection( IBrowserConnection connection )
    {
        selectedBrowserConnection = connection;
        browserConnectionText.setText( selectedBrowserConnection != null ? selectedBrowserConnection.getConnection().getName() : "" );
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        browserConnectionText.setEnabled( b );
        connectionBrowseButton.setEnabled( b );
    }

}
