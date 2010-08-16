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
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The DnDialog is used from the DN value editor to edit and select a DN.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */

public class DnDialog extends Dialog
{

    /** The title. */
    private String title;

    /** The description. */
    private String description;

    /** The entry widget. */
    private EntryWidget entryWidget;

    /** The connection. */
    private IBrowserConnection connection;

    /** The dn. */
    private LdapDN dn;


    /**
     * Creates a new instance of DnDialog.
     * 
     * @param parentShell the parent shell
     * @param title the title of the dialog
     * @param description the description of the dialog
     * @param connection the connection used to browse the directory
     * @param dn the initial DN, may be null
     */
    public DnDialog( Shell parentShell, String title, String description, IBrowserConnection connection, LdapDN dn )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.title = title;
        this.description = description;
        this.connection = connection;
        this.dn = dn;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( title );
        shell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_DNEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        dn = entryWidget.getDn();
        entryWidget.saveDialogSettings();
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected Control createButtonBar( Composite parent )
    {
        Control control = super.createButtonBar( parent );
        updateWidgets();
        return control;
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

        if ( description != null )
        {
            BaseWidgetUtils.createLabel( composite, description, 1 );
        }

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        entryWidget = new EntryWidget( connection, dn );
        entryWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                updateWidgets();
            }
        } );
        entryWidget.createWidget( innerComposite );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Updates the widgets.
     */
    private void updateWidgets()
    {
        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            getButton( IDialogConstants.OK_ID ).setEnabled(
                entryWidget.getDn() != null && !"".equals( entryWidget.getDn().toString() ) ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the dn.
     * 
     * @return the dn
     */
    public LdapDN getDn()
    {
        return dn;
    }

}
