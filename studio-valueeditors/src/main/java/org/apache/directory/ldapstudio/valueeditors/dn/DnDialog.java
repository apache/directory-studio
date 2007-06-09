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

package org.apache.directory.ldapstudio.valueeditors.dn;


import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.common.widgets.search.EntryWidget;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsActivator;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsConstants;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
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
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_DNEDITOR ) );
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


    protected Control createButtonBar( Composite parent )
    {
        Control control = super.createButtonBar( parent );
        widgetModified( null );
        return control;
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
            getButton( IDialogConstants.OK_ID ).setEnabled(
                this.entryWidget.getDn() != null && !"".equals( this.entryWidget.getDn().toString() ) );
        }
    }


    public DN getDn()
    {
        return this.dn;
    }

}
