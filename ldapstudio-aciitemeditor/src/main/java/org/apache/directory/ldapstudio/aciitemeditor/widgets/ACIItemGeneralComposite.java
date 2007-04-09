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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.aci.AuthenticationLevel;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


/**
 * This is used to edit general ACI item properties:
 * <ul>
 *   <li>identification tag
 *   <li>precedence
 *   <li>authentication level
 *   <li>selection for userFirst or itemFirst
 * </ul>
 * 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemGeneralComposite extends Composite
{
    /** The inner composite for all the content */
    private Composite composite = null;

    /** The identification tag label */
    private Label identificationTagLabel = null;

    /** The identification tag text field */
    private Text identificationTagText = null;

    /** The precedence label */
    private Label precedenceLabel = null;

    /** The spinner to select a valid precedence between 0 and 255 */
    private Spinner precedenceSpinner = null;

    /** The authentication level label */
    private Label authenticationLevelLabel = null;

    /** The combo to select a valid uthentication level */
    private Combo authenticationLevelCombo = null;

    /** 
     * The combo viewer is attached to authenticationLevelCombo to work with
     * AuthenticationLevel objects rather than Strings 
     */
    private ComboViewer authenticationLevelComboViewer = null;

    /** The user or item first label */
    private Label userOrItemFirstLabel = null;

    /** The user first radio button */
    private Button userFirstRadioButton = null;

    /** The item first radio button */
    private Button itemFirstRadioButton = null;

    /** The list with listers */
    private List<WidgetModifyListener> listenerList = new ArrayList<WidgetModifyListener>();


    /**
     * Creates a new instance of ACIItemGeneralComposite.
     *
     * @param parent
     * @param style
     */
    public ACIItemGeneralComposite( Composite parent, int style )
    {
        super( parent, style );

        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout( layout );

        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = GridData.CENTER;
        setLayoutData( layoutData );

        createComposite();
    }


    /**
     * This method initializes composite	
     *
     */
    private void createComposite()
    {

        GridData identificationTagGridData = new GridData();
        identificationTagGridData.grabExcessHorizontalSpace = true;
        identificationTagGridData.verticalAlignment = GridData.CENTER;
        identificationTagGridData.horizontalSpan = 2;
        identificationTagGridData.horizontalAlignment = GridData.FILL;

        GridData precedenceGridData = new GridData();
        precedenceGridData.grabExcessHorizontalSpace = true;
        precedenceGridData.verticalAlignment = GridData.CENTER;
        precedenceGridData.horizontalSpan = 2;
        precedenceGridData.horizontalAlignment = GridData.BEGINNING;
        precedenceGridData.widthHint = 3 * 12;

        GridData authenticationLevelGridData = new GridData();
        authenticationLevelGridData.grabExcessHorizontalSpace = true;
        authenticationLevelGridData.verticalAlignment = GridData.CENTER;
        authenticationLevelGridData.horizontalSpan = 2;
        authenticationLevelGridData.horizontalAlignment = GridData.FILL;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;

        composite = new Composite( this, SWT.NONE );
        composite.setLayout( gridLayout );
        composite.setLayoutData( gridData );

        identificationTagLabel = new Label( composite, SWT.NONE );
        identificationTagLabel.setText( Messages.getString( "ACIItemGeneralComposite.idTag.label" ) ); //$NON-NLS-1$
        identificationTagText = new Text( composite, SWT.BORDER );
        identificationTagText.setLayoutData( identificationTagGridData );
        identificationTagText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                fire( event );
            }
        } );

        precedenceLabel = new Label( composite, SWT.NONE );
        precedenceLabel.setText( Messages.getString( "ACIItemGeneralComposite.precedence.label" ) ); //$NON-NLS-1$
        precedenceSpinner = new Spinner( composite, SWT.BORDER );
        precedenceSpinner.setMinimum( 0 );
        precedenceSpinner.setMaximum( 255 );
        precedenceSpinner.setDigits( 0 );
        precedenceSpinner.setIncrement( 1 );
        precedenceSpinner.setPageIncrement( 10 );
        precedenceSpinner.setSelection( 0 );
        precedenceSpinner.setLayoutData( precedenceGridData );
        precedenceSpinner.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                fire( event );
            }
        } );

        authenticationLevelLabel = new Label( composite, SWT.NONE );
        authenticationLevelLabel.setText( Messages.getString( "ACIItemGeneralComposite.authLevel.label" ) ); //$NON-NLS-1$
        authenticationLevelCombo = new Combo( composite, SWT.READ_ONLY );
        authenticationLevelCombo.setLayoutData( authenticationLevelGridData );
        AuthenticationLevel[] authenticationLevels = new AuthenticationLevel[3];
        authenticationLevels[0] = AuthenticationLevel.NONE;
        authenticationLevels[1] = AuthenticationLevel.SIMPLE;
        authenticationLevels[2] = AuthenticationLevel.STRONG;
        authenticationLevelComboViewer = new ComboViewer( authenticationLevelCombo );
        authenticationLevelComboViewer.setContentProvider( new ArrayContentProvider() );
        authenticationLevelComboViewer.setLabelProvider( new LabelProvider() );
        authenticationLevelComboViewer.setInput( authenticationLevels );
        authenticationLevelComboViewer.setSelection( new StructuredSelection( AuthenticationLevel.NONE ) );
        authenticationLevelCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                fire( event );
            }
        } );

        userOrItemFirstLabel = new Label( composite, SWT.NONE );
        userOrItemFirstLabel.setText( Messages.getString( "ACIItemGeneralComposite.userOrItemFirst.label" ) ); //$NON-NLS-1$
        userFirstRadioButton = new Button( composite, SWT.RADIO );
        userFirstRadioButton.setText( Messages.getString( "ACIItemGeneralComposite.userFirst.label" ) ); //$NON-NLS-1$
        userFirstRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                fire( event );
            }
        } );
        itemFirstRadioButton = new Button( composite, SWT.RADIO );
        itemFirstRadioButton.setText( Messages.getString( "ACIItemGeneralComposite.itemFirst.label" ) ); //$NON-NLS-1$
        itemFirstRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                fire( event );
            }
        } );

    }


    /**
     * Add the listener to the list of listeners.
     *
     * @param listener
     */
    public void addWidgetModifyListener( WidgetModifyListener listener )
    {
        checkWidget();
        if ( listener == null )
            SWT.error( SWT.ERROR_NULL_ARGUMENT );
        listenerList.add( listener );
    }


    /**
     * Removes the listener from the list of listeners.
     *
     * @param listener
     */
    public void removeWidgetModifyListener( WidgetModifyListener listener )
    {
        checkWidget();
        if ( listener == null )
            SWT.error( SWT.ERROR_NULL_ARGUMENT );
        listenerList.remove( listener );
    }


    /**
     * Fires WidgetModifyEvents.
     *
     * @param event the original event
     */
    private void fire( TypedEvent event )
    {
        for ( WidgetModifyListener listener : listenerList )
        {
            listener.widgetModified( new WidgetModifyEvent( this ) );
        }
    }


    /**
     * Returns the identification tag.
     *
     * @return the identification tag
     */
    public String getIdentificationTag()
    {
        return identificationTagText.getText();
    }


    /**
     * Sets the identification tag
     *
     * @param identificationTag the identification tag
     */
    public void setIdentificationTag( String identificationTag )
    {
        identificationTagText.setText( identificationTag );
    }


    /**
     * Returns the selected precedence.
     *
     * @return the selected precedence
     */
    public int getPrecedence()
    {
        return precedenceSpinner.getSelection();
    }


    /**
     * Sets the precedence
     *
     * @param precedence the precedence
     */
    public void setPrecedence( int precedence )
    {
        precedenceSpinner.setSelection( precedence );
    }


    /**
     * Returns the selected authentication level.
     *
     * @return the selected authentication level
     */
    public AuthenticationLevel getAuthenticationLevel()
    {
        IStructuredSelection selection = ( IStructuredSelection ) authenticationLevelComboViewer.getSelection();
        return ( AuthenticationLevel ) selection.getFirstElement();
    }


    /**
     * Sets the authentication level.
     *
     * @param authenticationLevel the authentication level
     */
    public void setAuthenticationLevel( AuthenticationLevel authenticationLevel )
    {
        IStructuredSelection selection = new StructuredSelection( authenticationLevel );
        authenticationLevelComboViewer.setSelection( selection );
    }


    /**
     * Returns true if user first is selected.
     *
     * @return true if user first is selected
     */
    public boolean isUserFirst()
    {
        return userFirstRadioButton.getSelection();
    }


    /**
     * Selects user first.
     */
    public void setUserFirst()
    {
        userFirstRadioButton.setSelection( true );
        itemFirstRadioButton.setSelection( false );
    }


    /**
     * Returns true if item first is selected.
     *
     * @return true if item first is selected
     */
    public boolean isItemFirst()
    {
        return itemFirstRadioButton.getSelection();
    }


    /**
     * Selects item first.
     */
    public void setItemFirst()
    {
        itemFirstRadioButton.setSelection( true );
        userFirstRadioButton.setSelection( false );
    }

}
