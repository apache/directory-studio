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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferWrapper.TcpTypeEnum;


/**
 * The TcpBufferDialog is used to edit a TcpBuffer, which can contain an URL and a type of TCP buffer,
 * plus the size.<br/>
 * The dialog overlay is like :
 * 
 * <pre>
 * +---------------------------------------+
 * |  TcpBuffer                            |
 * | .-----------------------------------. |
 * | | Size : [    ]    () read () write | |
 * | | URL  : [                        ] | |
 * | '-----------------------------------' |
 * | .-----------------------------------. |
 * | | TcpBuffer : <///////////////////> | |
 * | '-----------------------------------' |
 * |                                       |
 * |  (cancel)                       (OK)  |
 * +---------------------------------------+
 * 
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TcpBufferDialog extends AddEditDialog<TcpBufferWrapper>
{
    /** The list of existing TcpBuffer */
    List<TcpBufferWrapper> tcpBufferList;

    // UI widgets
    /** The Size Text */
    private Text sizeText;
    
    /** The Read and Write checkboxes */
    private Button readCheckbox;
    private Button writeCheckbox;
    
    /** The Listener text */
    private Text listenerText;
    
    /** The resulting TcpBuffer Text, or an error message */
    private Text tcpBufferText;


    /**
     * Create a new instance of the TcpBufferDialog
     * 
     * @param parentShell The parent Shell
     */
    public TcpBufferDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * The listener for the size Text
     */
    private ModifyListener sizeTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = tcpBufferText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            try
            {
                long sizeValue = Long.parseLong( sizeText.getText() );

                // The size must be between 0 and 2^32-1
                if ( ( sizeValue < 0L ) || ( sizeValue > TcpBufferWrapper.MAX_TCP_BUFFER_SIZE ) )
                {
                    sizeText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    okButton.setEnabled( false );
                    return;
                }
                
                sizeText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getEditedElement().setSize( sizeValue );
                tcpBufferText.setText( getEditedElement().toString() );
                
                if ( TcpBufferWrapper.isValid( sizeText.getText(), listenerText.getText() ) )
                {
                    tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    okButton.setEnabled( true );
                }
                else
                {
                    tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    okButton.setEnabled( false );
                }
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                sizeText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                tcpBufferText.setText( getEditedElement().toString() );
                tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        }
    };
    
    
    /**
     * The listener for the URL Text
     */
    private ModifyListener urlTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = tcpBufferText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            try
            {
                URL newUrl = new URL( listenerText.getText() );

                getEditedElement().setListener( newUrl );
                listenerText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                tcpBufferText.setText( getEditedElement().toString() );
                
                if ( TcpBufferWrapper.isValid( sizeText.getText(), listenerText.getText() ) )
                {
                    tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    okButton.setEnabled( true );
                }
                else
                {
                    tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    okButton.setEnabled( false );
                }
            }
            catch ( MalformedURLException mue )
            {
                listenerText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                tcpBufferText.setText( getEditedElement().toString() );
                tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        }
    };


    /**
     * The listener in charge of exposing the changes when the read or write buttons are checked
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = tcpBufferText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( readCheckbox.getSelection() )
            {
                if ( writeCheckbox.getSelection())
                {
                    getEditedElement().setTcpType( TcpTypeEnum.BOTH );
                }
                else
                {
                    getEditedElement().setTcpType( TcpTypeEnum.READ );
                }
            }
            else if ( writeCheckbox.getSelection() )
            {
                if ( readCheckbox.getSelection() )
                {
                    getEditedElement().setTcpType( TcpTypeEnum.BOTH );
                }
                else
                {
                    getEditedElement().setTcpType( TcpTypeEnum.WRITE );
                }
            }
            else
            {
                getEditedElement().setTcpType( TcpTypeEnum.BOTH );
            }
            
            // Set the TcpBuffer into the text box
            tcpBufferText.setText( getEditedElement().toString() );

            if ( TcpBufferWrapper.isValid( sizeText.getText(), listenerText.getText() ) )
            {
                tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                okButton.setEnabled( true );
            }
            else
            {
                tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        }
    };

    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "TcpBuffer" );
    }


    /**
     * Construct the TcpBufferWrapper from what we have in the dialog
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        super.okPressed();
    }


    /**
     * Create the Dialog for TcpBuffer :
     * <pre>
     * +---------------------------------------+
     * |  TcpBuffer                            |
     * | .-----------------------------------. |
     * | | Size : [    ]    () read () write | |
     * | | URL  : [                        ] | |
     * | '-----------------------------------' |
     * | .-----------------------------------. |
     * | | TcpBuffer : <///////////////////> | |
     * | '-----------------------------------' |
     * |                                       |
     * |  (cancel)                       (OK)  |
     * +---------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createTcpBufferEditGroup( composite );
        createTcpBufferShowGroup( composite );

        initDialog();
        addListeners();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the TcpBuffer input group. This is the part of the dialog
     * where one can insert the TcpBuffer size and URL
     * 
     * <pre>
     *  TcpBuffer Input
     * .-----------------------------------.
     * | Size : [    ]    () read () write |
     * | URL  : [                        ] |
     * '-----------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createTcpBufferEditGroup( Composite parent )
    {
        // TcpBuffer Group
        Group tcpBufferGroup = BaseWidgetUtils.createGroup( parent, "TcpBuffer input", 1 );
        GridLayout tcpBufferGroupGridLayout = new GridLayout( 6, false );
        tcpBufferGroup.setLayout( tcpBufferGroupGridLayout );
        tcpBufferGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Size Text
        BaseWidgetUtils.createLabel( tcpBufferGroup, "Size :", 1 );
        sizeText = BaseWidgetUtils.createText( tcpBufferGroup, "", 1 );
        sizeText.addModifyListener( sizeTextListener );

        // Read checkbox Button
        readCheckbox = BaseWidgetUtils.createCheckbox( tcpBufferGroup, "read", 2 );

        // Write checkbox Button
        writeCheckbox = BaseWidgetUtils.createCheckbox( tcpBufferGroup, "write", 2 );

        // URL Text
        BaseWidgetUtils.createLabel( tcpBufferGroup, "URL:", 1 );
        listenerText = BaseWidgetUtils.createText( tcpBufferGroup, "", 5 );
        listenerText.addModifyListener( urlTextListener );
    }


    /**
     * Creates the TcpBuffer show group. This is the part of the dialog
     * where the real TcpBuffer is shown, or an error message if the TcpBuffer
     * is invalid.
     * 
     * <pre>
     * .-----------------------------------.
     * | TcpBuffer : <///////////////////> |
     * '-----------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createTcpBufferShowGroup( Composite parent )
    {
        // TcpBuffer Group
        Group tcpBufferGroup = BaseWidgetUtils.createGroup( parent, "", 1 );
        GridLayout tcpBufferGroupGridLayout = new GridLayout( 2, false );
        tcpBufferGroup.setLayout( tcpBufferGroupGridLayout );
        tcpBufferGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // TcpBuffer Text
        tcpBufferText = BaseWidgetUtils.createText( tcpBufferGroup, "", 1 );
        tcpBufferText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        tcpBufferText.setEditable( false );
    }


    /**
     * Initializes the UI from the TcpBuffer
     */
    protected void initDialog()
    {
        TcpBufferWrapper editedElement = (TcpBufferWrapper)getEditedElement();
        
        if ( editedElement != null )
        {
            sizeText.setText( Long.toString( editedElement.getSize() ) );
            
            URL listener =  editedElement.getListener();
            
            if ( listener == null )
            {
                listenerText.setText( "" );
            }
            else
            {
                listenerText.setText( listener.toString() );
            }
            
            tcpBufferText.setText( editedElement.toString() );
        }
    }


    /**
     * Add a new Element that will be edited
     */
    public void addNewElement()
    {
        setEditedElement( new TcpBufferWrapper( "" ) );
    }
    

    /**
     * Add a new Element that will be edited
     */
    protected void addNewElement( TcpBufferWrapper editedElement )
    {
        TcpBufferWrapper newElement = (TcpBufferWrapper)editedElement.clone();
        setEditedElement( newElement );
    }

    
    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        readCheckbox.addSelectionListener( checkboxSelectionListener );
        writeCheckbox.addSelectionListener( checkboxSelectionListener );
    }

}
