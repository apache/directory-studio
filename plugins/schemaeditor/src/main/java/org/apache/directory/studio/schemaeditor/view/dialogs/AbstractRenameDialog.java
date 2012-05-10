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
package org.apache.directory.studio.schemaeditor.view.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * This dialog is used to rename items like projects or schemas.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractRenameDialog extends Dialog
{
    /** The original name*/
    private String originalName;

    /** The new name */
    private String newName;

    // UI Fields
    private Text newNameText;
    private Composite errorComposite;
    private Image errorImage;
    private Label errorLabel;
    private Button okButton;


    /**
     * Creates a new instance of AbstractRenameDialog.
     *
     * @param originalName
     *      the original name
     */
    public AbstractRenameDialog( String originalName )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.originalName = originalName;
        this.newName = originalName;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "RenameProjectDialog.Rename" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // New Name
        Label newNameLabel = new Label( composite, SWT.NONE );
        newNameLabel.setText( Messages.getString( "AbstractRenameDialog.NewName" ) ); //$NON-NLS-1$
        newNameText = new Text( composite, SWT.BORDER );
        newNameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        newNameText.setText( originalName );
        newNameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                // Getting the new name
                newName = newNameText.getText();

                if ( !newName.equalsIgnoreCase( originalName ) )
                {
                    // Checking if the new is already taken
                    boolean checkNewName = isNewNameAlreadyTaken();

                    // Enabling (or not) the ok button and showing (or not) the error composite
                    okButton.setEnabled( !checkNewName );
                    errorComposite.setVisible( checkNewName );
                }
                else
                {
                    // Enabling the ok button and showing the error composite
                    okButton.setEnabled( true );
                    errorComposite.setVisible( false );
                }
            }
        } );

        // Error Composite
        errorComposite = new Composite( composite, SWT.NONE );
        errorComposite.setLayout( new GridLayout( 2, false ) );
        errorComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        errorComposite.setVisible( false );

        // Error Image
        errorImage = PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
        Label label = new Label( errorComposite, SWT.NONE );
        label.setImage( errorImage );
        label.setSize( 16, 16 );

        // Error Label
        errorLabel = new Label( errorComposite, SWT.NONE );
        errorLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        errorLabel.setText( getErrorMessage() );

        newNameText.setFocus();
        newNameText.selectAll();

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
    }


    /**
     * Returns the new name.
     *
     * @return
     *      the new name
     */
    public String getNewName()
    {
        return newName;
    }


    /**
     * Gets the error message.
     *
     * @return the error message
     */
    protected abstract String getErrorMessage();


    /**
     * Checks if the new name is already taken.
     *
     * @return <code>true</code> if the new name is already taken,
     *         <code>false</code> if not.
     */
    protected abstract boolean isNewNameAlreadyTaken();
}
