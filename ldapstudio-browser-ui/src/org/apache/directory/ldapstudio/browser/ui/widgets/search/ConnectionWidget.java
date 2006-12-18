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

package org.apache.directory.ldapstudio.browser.ui.widgets.search;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.dialogs.SelectConnectionDialog;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class ConnectionWidget extends BrowserWidget
{

    private Text connectionText;

    private Button connectionBrowseButton;

    private IConnection selectedConnection;


    public ConnectionWidget( IConnection connection )
    {
        this.selectedConnection = connection;
    }


    public ConnectionWidget()
    {
        this.selectedConnection = null;
    }


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
        this.setConnection( this.selectedConnection );

    }


    public IConnection getConnection()
    {
        return this.selectedConnection;
    }


    public void setConnection( IConnection connection )
    {
        this.selectedConnection = connection;
        connectionText.setText( this.selectedConnection != null ? this.selectedConnection.getName() : "" );
    }


    public void setEnabled( boolean b )
    {
        this.connectionText.setEnabled( b );
        this.connectionBrowseButton.setEnabled( b );
    }

}
