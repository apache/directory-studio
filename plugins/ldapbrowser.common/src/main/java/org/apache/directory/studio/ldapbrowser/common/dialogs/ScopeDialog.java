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


import org.apache.directory.shared.ldap.model.message.SearchScope;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


/**
 * A dialog to select the scope of a copy operation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ScopeDialog extends Dialog
{

    /** The dialog title. */
    private String dialogTitle;

    /** The multiple entries selected flag. */
    private boolean multipleEntriesSelected;

    /** The scope. */
    private SearchScope scope;

    /** The object scope button. */
    private Button objectScopeButton;

    /** The onelevel scope button. */
    private Button onelevelScopeButton;

    /** The subtree scope button. */
    private Button subtreeScopeButton;


    /**
     * Creates a new instance of ScopeDialog.
     * 
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param multipleEntriesSelected the multiple entries selected
     */
    public ScopeDialog( Shell parentShell, String dialogTitle, boolean multipleEntriesSelected )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.dialogTitle = dialogTitle;
        this.multipleEntriesSelected = multipleEntriesSelected;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( dialogTitle );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        scope = objectScopeButton.getSelection() ? SearchScope.OBJECT
            : onelevelScopeButton.getSelection() ? SearchScope.ONELEVEL : SearchScope.SUBTREE;
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        Group group = BaseWidgetUtils.createGroup( composite, Messages.getString( "ScopeDialog.SelectCopyDepth" ), 1 ); //$NON-NLS-1$
        objectScopeButton = new Button( group, SWT.RADIO );
        objectScopeButton.setSelection( true );
        objectScopeButton.setText( multipleEntriesSelected ? Messages.getString( "ScopeDialog.OnlyCopiedEntries" ) //$NON-NLS-1$
            : Messages.getString( "ScopeDialog.OnlyCopiedEntry" ) ); //$NON-NLS-1$
        onelevelScopeButton = new Button( group, SWT.RADIO );
        onelevelScopeButton.setText( multipleEntriesSelected ? Messages
            .getString( "ScopeDialog.CopiedEntriesAndDirectChildren" ) //$NON-NLS-1$
            : Messages.getString( "ScopeDialog.CopiedEntryAndDirectChildren" ) ); //$NON-NLS-1$
        subtreeScopeButton = new Button( group, SWT.RADIO );
        subtreeScopeButton.setText( multipleEntriesSelected ? Messages.getString( "ScopeDialog.WholeSubtrees" ) //$NON-NLS-1$
            : Messages.getString( "ScopeDialog.WholeSubtree" ) ); //$NON-NLS-1$

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Gets the scope.
     * 
     * @return the scope
     */
    public SearchScope getScope()
    {
        return scope;
    }

}
