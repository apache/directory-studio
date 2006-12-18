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


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.FilterWidget;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class FilterSubtreeDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Filter Subtree";

    private IConnection connection;

    private FilterWidget filterWidget;

    private String filter;


    public FilterSubtreeDialog( Shell parentShell, String filter, IConnection connection )
    {
        super( parentShell );
        this.filter = filter;
        this.connection = connection;
        setShellStyle( SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
    }


    public String getFilter()
    {
        return this.filter;
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( DIALOG_TITLE );
    }


    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            this.filter = filterWidget.getFilter();
            filterWidget.saveDialogSettings();
        }

        // call super implementation
        super.buttonPressed( buttonId );
    }


    protected Control createButtonBar( Composite parent )
    {
        Composite composite = ( Composite ) super.createButtonBar( parent );
        return composite;
    }


    protected Control createDialogArea( Composite parent )
    {
        // Composite composite = parent;
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        // gd.heightHint =
        // convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH)/2;
        composite.setLayoutData( gd );

        Composite inner = new Composite( composite, SWT.NONE );
        GridLayout gridLayout = new GridLayout( 2, false );
        inner.setLayout( gridLayout );
        gd = new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        inner.setLayoutData( gd );

        filterWidget = new FilterWidget( connection, filter != null ? filter : "" );
        filterWidget.createWidget( inner );
        // filterWidget.addWidgetModifyListener(new WidgetModifyListener(){
        // public void widgetModified(WidgetModifyEvent event) {
        // wizard.setExportFilter(filterWidget.getFilter());
        // validate();
        // }
        // });
        filterWidget.setFocus();

        return composite;
    }


    protected boolean canHandleShellCloseEvent()
    {
        // proposal popup is opened, don't close dialog!
        return super.canHandleShellCloseEvent();
    }

}
