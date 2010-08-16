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

package org.apache.directory.studio.ldapbrowser.common.dialogs;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.DnBuilderWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog to select and enter the new parent of some entries. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MoveEntriesDialog extends Dialog implements WidgetModifyListener
{

    /** The dialog title. */
    private static final String DIALOG_TITLE = Messages.getString( "MoveEntriesDialog.MoveEntries" ); //$NON-NLS-1$

    /** The entries to move. */
    private IEntry[] entries;

    /** The dn builder widget. */
    private DnBuilderWidget dnBuilderWidget;

    /** The ok button. */
    private Button okButton;

    /** The parent DN. */
    private LdapDN parentDn;


    /**
     * Creates a new instance of MoveEntriesDialog.
     * 
     * @param parentShell the parent shell
     * @param entries the entries
     */
    public MoveEntriesDialog( Shell parentShell, IEntry[] entries )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.entries = entries;
        this.parentDn = null;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        dnBuilderWidget.removeWidgetModifyListener( this );
        dnBuilderWidget.dispose();
        return super.close();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        parentDn = dnBuilderWidget.getParentDn();
        dnBuilderWidget.saveDialogSettings();
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        composite.setLayoutData( gd );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "MoveEntriesDialog.MoveEntriesDescription" ), 1 ); //$NON-NLS-1$

        dnBuilderWidget = new DnBuilderWidget( false, true );
        dnBuilderWidget.addWidgetModifyListener( this );
        dnBuilderWidget.createContents( composite );
        dnBuilderWidget
            .setInput( entries[0].getBrowserConnection(), null, null, DnUtils.getParent( entries[0].getDn() ) );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener#widgetModified(org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent)
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        if ( okButton != null )
        {
            okButton.setEnabled( dnBuilderWidget.getParentDn() != null );
        }
    }


    /**
     * Gets the parent dn.
     * 
     * @return the parent dn
     */
    public LdapDN getParentDn()
    {
        return parentDn;
    }

}
