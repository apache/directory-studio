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

package org.apache.directory.ldapstudio.browser.ui.dialogs;


import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.EntryWidget;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class DnDialog extends Dialog implements WidgetModifyListener
{

    public static final String DIALOG_TITLE = "DN Editor";

    private EntryWidget entryWidget;

    private IConnection connection;

    private DN dn;


    public DnDialog( Shell parentShell, IConnection connection, DN dn )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.connection = connection;
        this.dn = dn;
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_DNEDITOR ) );
    }


    public boolean close()
    {
        this.entryWidget.removeWidgetModifyListener( this );
        return super.close();
    }


    protected void okPressed()
    {
        this.dn = this.entryWidget.getDn();
        this.entryWidget.saveDialogSettings();
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        composite.setLayoutData( gd );

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        this.entryWidget = new EntryWidget( connection, dn );
        this.entryWidget.addWidgetModifyListener( this );
        this.entryWidget.createWidget( innerComposite );

        applyDialogFont( composite );
        return composite;
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( this.entryWidget.getDn() != null );
        }
    }


    public DN getDn()
    {
        return this.dn;
    }

}
