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


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.DnBuilderWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class RenameEntryDialog extends Dialog implements WidgetModifyListener
{

    public static final String DELETE_OLD_RDN_DIALOGSETTING_KEY = RenameEntryDialog.class.getName() + ".deleteOldRdn";

    public static final String DIALOG_TITLE = "Rename Entry";

    private IEntry entry;

    private DnBuilderWidget dnBuilderWidget;

    private Button deleteOldRdnButton;

    private Button simulateRenameButton;

    private Button okButton;

    private RDN rdn;

    private boolean deleteOldRdn;

    private boolean simulateRename;


    public RenameEntryDialog( Shell parentShell, IEntry entry )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.entry = entry;
        this.rdn = null;

        if ( BrowserCommonActivator.getDefault().getDialogSettings().get( DELETE_OLD_RDN_DIALOGSETTING_KEY ) == null )
            BrowserCommonActivator.getDefault().getDialogSettings().put( DELETE_OLD_RDN_DIALOGSETTING_KEY, true );
        this.deleteOldRdn = BrowserCommonActivator.getDefault().getDialogSettings().getBoolean( DELETE_OLD_RDN_DIALOGSETTING_KEY );
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
        this.rdn = this.dnBuilderWidget.getRdn();
        this.deleteOldRdn = this.deleteOldRdnButton.getSelection();
        this.simulateRename = this.simulateRenameButton.getSelection();

        BrowserCommonActivator.getDefault().getDialogSettings().put( DELETE_OLD_RDN_DIALOGSETTING_KEY, this.deleteOldRdn );
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

        BaseWidgetUtils.createLabel( composite, "Please enter the new RDN of the selected entry.", 1 );

        this.dnBuilderWidget = new DnBuilderWidget( true, false );
        this.dnBuilderWidget.addWidgetModifyListener( this );
        this.dnBuilderWidget.createContents( composite );
        this.dnBuilderWidget.setInput( this.entry.getConnection(), this.entry.getSubschema().getAllAttributeNames(),
            this.entry.getRdn(), null );

        this.deleteOldRdnButton = BaseWidgetUtils.createCheckbox( composite, "Delete old RDN", 1 );
        this.deleteOldRdnButton.setSelection( this.deleteOldRdn );

        this.simulateRenameButton = BaseWidgetUtils.createCheckbox( composite,
            "Simulate subtree renaming by searching/adding/deleting recursively", 1 );
        this.simulateRenameButton.setSelection( false );
        this.simulateRenameButton.setEnabled( false );

        applyDialogFont( composite );
        return composite;
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        if ( this.okButton != null )
        {
            this.okButton.setEnabled( this.dnBuilderWidget.getRdn() != null );
        }
    }


    public RDN getRdn()
    {
        return this.rdn;
    }


    public boolean isDeleteOldRdn()
    {
        return this.deleteOldRdn;
    }


    public boolean isSimulateRename()
    {
        return simulateRename;
    }

}
