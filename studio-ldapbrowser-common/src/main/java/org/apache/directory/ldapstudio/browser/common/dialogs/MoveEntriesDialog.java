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

package org.apache.directory.ldapstudio.browser.common.dialogs;


import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.DnBuilderWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class MoveEntriesDialog extends Dialog implements WidgetModifyListener
{

    public static final String DIALOG_TITLE = "Move Entries";

    private IEntry[] entries;

    private DnBuilderWidget dnBuilderWidget;

    private Button simulateMoveButton;

    private Button okButton;

    private DN parentDn;

    private boolean simulateMove;


    public MoveEntriesDialog( Shell parentShell, IEntry[] entries )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.entries = entries;
        this.parentDn = null;
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
    }


    public boolean close()
    {
        this.dnBuilderWidget.removeWidgetModifyListener( this );
        this.dnBuilderWidget.dispose();
        return super.close();
    }


    protected void okPressed()
    {
        this.parentDn = this.dnBuilderWidget.getParentDn();
        this.simulateMove = this.simulateMoveButton.getSelection();

        this.dnBuilderWidget.saveDialogSettings();

        super.okPressed();
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        composite.setLayoutData( gd );

        BaseWidgetUtils.createLabel( composite,
            "Please enter/select the parent DN where the selected entries should be moved to.", 1 );

        this.dnBuilderWidget = new DnBuilderWidget( false, true );
        this.dnBuilderWidget.addWidgetModifyListener( this );
        this.dnBuilderWidget.createContents( composite );
        this.dnBuilderWidget.setInput( this.entries[0].getConnection(), null, null, this.entries[0].getDn()
            .getParentDn() );

        this.simulateMoveButton = BaseWidgetUtils.createCheckbox( composite,
            "Simulate subtree moving by searching/adding/deleting recursively", 1 );
        this.simulateMoveButton.setSelection( false );
        this.simulateMoveButton.setEnabled( false );

        applyDialogFont( composite );
        return composite;
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        if ( this.okButton != null )
        {
            this.okButton.setEnabled( this.dnBuilderWidget.getParentDn() != null );
        }
    }


    public DN getParentDn()
    {
        return this.parentDn;
    }


    public boolean isSimulateMove()
    {
        return simulateMove;
    }

}
