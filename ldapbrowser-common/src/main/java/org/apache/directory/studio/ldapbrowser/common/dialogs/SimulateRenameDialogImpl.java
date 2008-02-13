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
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.jobs.SimulateRenameDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * A dialog used to ask the user to simulate the rename operation by 
 * recursively searching/adding/deleting.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SimulateRenameDialogImpl extends Dialog implements SimulateRenameDialog
{

    /** The dialog title. */
    private String dialogTitle = "Simulate moving/renaming?";

    /** The simulate rename flag */
    private boolean isSimulateRename;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The old DN. */
    private LdapDN oldDn;

    /** The new DN. */
    private LdapDN newDn;


    /**
     * Creates a new instance of ScopeDialog.
     * 
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param multipleEntriesSelected the multiple entries selected
     */
    public SimulateRenameDialogImpl( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( dialogTitle );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        isSimulateRename = true;
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        gd = new GridData( GridData.FILL_BOTH );
        innerComposite.setLayoutData( gd );

        String text1 = "You are trying to move/rename '" + oldDn.getUpName() + "' to '" + newDn.getUpName() + "'.";
        BaseWidgetUtils.createLabel( innerComposite, text1, 1 );

        String text2 = "The LDAP server '" + browserConnection.getConnection().getName()
            + "' doesn't support moving/renaming of non-leaf entries.";
        BaseWidgetUtils.createLabel( innerComposite, text2, 1 );

        String text3 = "Press OK to simulate the move/rename operation by searching/copying/deleting.";
        BaseWidgetUtils.createLabel( innerComposite, text3, 1 );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public int open()
    {
        final int[] result = new int[1];
        Display.getDefault().syncExec( new Runnable()
        {
            public void run()
            {
                result[0] = SimulateRenameDialogImpl.super.open();
            }
        } );
        return result[0];
    }


    /**
     * {@inheritDoc}
     */
    public void setEntryInfo( IBrowserConnection browserConnection, LdapDN oldDn, LdapDN newDn )
    {
        this.browserConnection = browserConnection;
        this.oldDn = oldDn;
        this.newDn = newDn;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSimulateRename()
    {
        return isSimulateRename;
    }

}
