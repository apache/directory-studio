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
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.DnBuilderWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.jobs.EntryExistsCopyStrategyDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * A dialog to select the copy strategy if an entry already exists.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryExistsCopyStrategyDialogImpl extends Dialog implements EntryExistsCopyStrategyDialog
{

    /** The dialog title. */
    private String dialogTitle = Messages.getString( "EntryExistsCopyStrategyDialogImpl.SelectCopyStrategy" ); //$NON-NLS-1$

    /** The break button. */
    private Button breakButton;

    /** The ignore button. */
    private Button ignoreButton;

    /** The overwrite button. */
    private Button overwriteButton;

    /** The rename button. */
    private Button renameButton;
    //
    //    /** The remember check box. */
    //    private Button rememberCheckbox;

    /** The DN builder widget. */
    private DnBuilderWidget dnBuilderWidget;

    /** The new RDN. */
    private Rdn rdn;

    /** The strategy */
    private EntryExistsCopyStrategy strategy;

    /** The remember flag */
    private boolean isRememberStrategy;

    private IBrowserConnection browserConnection;

    private LdapDN dn;


    /**
     * Creates a new instance of ScopeDialog.
     * 
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param multipleEntriesSelected the multiple entries selected
     */
    public EntryExistsCopyStrategyDialogImpl( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        strategy = EntryExistsCopyStrategy.BREAK;
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
        rdn = null;
        //        isRememberStrategy = rememberCheckbox.getSelection() && rememberCheckbox.isEnabled();

        if ( breakButton.getSelection() )
        {
            strategy = EntryExistsCopyStrategy.BREAK;
        }
        else if ( ignoreButton.getSelection() )
        {
            strategy = EntryExistsCopyStrategy.IGNORE_AND_CONTINUE;
        }
        else if ( overwriteButton.getSelection() )
        {
            strategy = EntryExistsCopyStrategy.OVERWRITE_AND_CONTINUE;
        }
        else if ( renameButton.getSelection() )
        {
            strategy = EntryExistsCopyStrategy.RENAME_AND_CONTINUE;
            rdn = dnBuilderWidget.getRdn();
        }

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
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

        String text = NLS.bind(
            Messages.getString( "EntryExistsCopyStrategyDialogImpl.SelectCopyStrategyDescription" ), dn.getUpName() ); //$NON-NLS-1$
        BaseWidgetUtils.createLabel( composite, text, 1 );

        Composite group2 = BaseWidgetUtils.createGroup( composite, "", 1 ); //$NON-NLS-1$
        Composite group = BaseWidgetUtils.createColumnContainer( group2, 2, 1 );

        SelectionListener listener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        };

        breakButton = BaseWidgetUtils.createRadiobutton( group, Messages
            .getString( "EntryExistsCopyStrategyDialogImpl.StopCopyProcess" ), 2 ); //$NON-NLS-1$
        breakButton.setSelection( true );
        breakButton.addSelectionListener( listener );

        ignoreButton = BaseWidgetUtils.createRadiobutton( group, Messages
            .getString( "EntryExistsCopyStrategyDialogImpl.IgnoreEntryAndContinue" ), 2 ); //$NON-NLS-1$
        ignoreButton.addSelectionListener( listener );

        overwriteButton = BaseWidgetUtils.createRadiobutton( group, Messages
            .getString( "EntryExistsCopyStrategyDialogImpl.OverwriteEntryAndContinue" ), 2 ); //$NON-NLS-1$
        overwriteButton.addSelectionListener( listener );

        renameButton = BaseWidgetUtils.createRadiobutton( group, Messages
            .getString( "EntryExistsCopyStrategyDialogImpl.RenameEntryAndContinue" ), 2 ); //$NON-NLS-1$
        renameButton.addSelectionListener( listener );

        BaseWidgetUtils.createRadioIndent( group, 1 );
        dnBuilderWidget = new DnBuilderWidget( true, false );
        dnBuilderWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        dnBuilderWidget.createContents( group );
        dnBuilderWidget.setInput( browserConnection, SchemaUtils.getNamesAsArray( browserConnection.getSchema()
            .getAttributeTypeDescriptions() ), dn.getRdn(), null );

        //        rememberCheckbox = BaseWidgetUtils.createCheckbox( composite, "Remember decision", 2 );

        validate();

        applyDialogFont( composite );
        return composite;
    }


    private void validate()
    {
        if ( renameButton.getSelection() )
        {
            dnBuilderWidget.setEnabled( true );
            getButton( IDialogConstants.OK_ID ).setEnabled( dnBuilderWidget.getRdn() != null );
        }
        else
        {
            dnBuilderWidget.setEnabled( false );
        }
        //        rememberCheckbox.setEnabled( overwriteButton.getSelection() || ignoreButton.getSelection() );
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
                result[0] = EntryExistsCopyStrategyDialogImpl.super.open();
            }
        } );
        return result[0];
    }


    /**
     * {@inheritDoc}
     */
    public EntryExistsCopyStrategy getStrategy()
    {
        return strategy;
    }


    /**
     * {@inheritDoc}
     */
    public Rdn getRdn()
    {
        return rdn;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isRememberSelection()
    {
        return isRememberStrategy;
    }


    /**
     * {@inheritDoc}
     */
    public void setExistingEntry( IBrowserConnection browserConnection, LdapDN dn )
    {
        this.browserConnection = browserConnection;
        this.dn = dn;
    }

}
