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


import java.util.HashMap;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog with an text area.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TextDialog extends Dialog
{
    /** The dialog title. */
    private static final String DIALOG_TITLE = Messages.getString( "TextDialog.TextEditor" ); //$NON-NLS-1$

    /** The initial value. */
    private String initialValue;

    /** The return value. */
    private String returnValue;
    
    /** The button ID for the save button. */
    private static final int TOGGLE_BUTTON_ID = 9999;
    
    /**
     * Collection of buttons created by the <code>createButton</code> method.
     */
   private HashMap<Integer, Button> buttons = new HashMap<>();

    /** The text area. */
    private Text text;
    
    private int defaultTextStyle = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
   
    /** The check box to enable line wrap */
    GridData gd = new GridData();


    /**
     * Creates a new instance of TextDialog.
     * 
     * @param parentShell the parent shell
     * @param initialValue the initial value
     */
    public TextDialog( Shell parentShell, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE | SWT.MAX );
        this.initialValue = initialValue;
        this.returnValue = null;
    }

    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, String, boolean)
     */
    @Override
   protected Button createButton( Composite parent, int id, String label, boolean defaultButton ) 
    {
       return createButton( parent, id, label, defaultButton, SWT.PUSH );
   }
       
   protected Button createButton( Composite parent, int id, String label, boolean defaultButton, int style ) 
   {
       // increment the number of columns in the button bar
       ( ( GridLayout ) parent.getLayout() ).numColumns++;
       Button button = new Button( parent, style );
       button.setText( label );
       button.setFont( JFaceResources.getDialogFont() );
       button.setData( Integer.valueOf( id ) );
       button.addSelectionListener( 
           new SelectionAdapter() 
           {
               @Override
               public void widgetSelected( SelectionEvent event ) 
               {
                   buttonPressed( ( ( Integer ) event.widget.getData() ).intValue() );
               }
           });
       
       if ( defaultButton ) 
       {
           Shell shell = parent.getShell();
           
           if ( shell != null ) 
           {
               shell.setDefaultButton( button );
           }
       }
       
       buttons.put( Integer.valueOf( id ), button );
       setButtonLayoutData( button );
       
       return button;
   }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_TEXTEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, TOGGLE_BUTTON_ID, Messages.getString( "TextDialog.WrapLines" ), false, SWT.TOGGLE ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }
    
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
       if ( TOGGLE_BUTTON_ID == buttonId ) 
       {
           String currentValue = text.getText();
           Composite composite = text.getParent();
           text.dispose();
           createText(composite, currentValue, getButton( TOGGLE_BUTTON_ID ).getSelection() );
           text.requestLayout();   
        }
       
        super.buttonPressed( buttonId );
    }
    
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#getButton()
     */
    @Override
    protected Button getButton( int id ) 
    {
       return buttons.get( Integer.valueOf( id ) );
   }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        returnValue = text.getText();
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        
        composite.setLayoutData( new GridData( SWT.FILL,SWT.FILL,true,true ) );
        
        // text widget
        createText( composite, this.initialValue, false );
        
        return composite;
    }
    
    
     protected void createText( Composite composite, String value, boolean wrap ) 
     {
         if ( wrap ) 
         {
             text = new Text( composite, defaultTextStyle | SWT.WRAP);
         } 
         else 
         {
             text = new Text( composite, defaultTextStyle );
         }
        
         text.setText( value );
         gd = new GridData( SWT.FILL,SWT.FILL,true,true );
         gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 2);
         gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
         text.setLayoutData( gd );
         applyDialogFont( composite );
    }


    /**
     * Gets the text.
     * 
     * @return the text
     */
    public String getText()
    {
        return returnValue;
    }
}
