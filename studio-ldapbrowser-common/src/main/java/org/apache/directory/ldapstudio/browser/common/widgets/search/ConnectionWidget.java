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

package org.apache.directory.ldapstudio.browser.common.widgets.search;


import org.apache.directory.ldapstudio.browser.common.dialogs.SelectConnectionDialog;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * The ConnectionWidget could be used to select a connection. 
 * It is composed of a text to display the selected connection
 * and a browse button to open a {@link SelectConnectionDialog}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionWidget extends BrowserWidget
{

    /** The connection text, displays the selected connection */
    private Text connectionText;

    /** The connection browse button, opens the dialog */
    private Button connectionBrowseButton;

    /** The selected connection */
    private IConnection selectedConnection;


    /**
     * Creates a new instance of ConnectionWidget.
     * 
     * @param connection the initial connection
     */
    public ConnectionWidget( IConnection connection )
    {
        this.selectedConnection = connection;
    }


    /**
     * Creates a new instance of ConnectionWidget with no initial connection.
     */
    public ConnectionWidget()
    {
        this.selectedConnection = null;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( final Composite parent )
    {

        // Text
        connectionText = BaseWidgetUtils.createReadonlyText( parent, "", 1 );

        // Button
        connectionBrowseButton = BaseWidgetUtils.createButton( parent, "B&rowse...", 1 );
        connectionBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                // if(selectedConnection != null) {
                SelectConnectionDialog dialog = new SelectConnectionDialog( parent.getShell(), "Select Connection",
                    selectedConnection );
                dialog.open();
                IConnection connection = dialog.getSelectedConnection();
                if ( connection != null )
                {
                    setConnection( connection );
                    notifyListeners();
                }
                // }
            }
        } );

        // initial values
        setConnection( selectedConnection );

    }


    /**
     * Gets the selected connection.
     * 
     * @return the connection
     */
    public IConnection getConnection()
    {
        return selectedConnection;
    }


    /**
     * Sets the selected connection.
     * 
     * @param connection the connection
     */
    public void setConnection( IConnection connection )
    {
        selectedConnection = connection;
        connectionText.setText( selectedConnection != null ? selectedConnection.getName() : "" );
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        connectionText.setEnabled( b );
        connectionBrowseButton.setEnabled( b );
    }

}
