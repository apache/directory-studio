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
package org.apache.directory.studio.aciitemeditor.valueeditors;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * This class provides a dialog to enter the Exclusion values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
class ExclusionDialog extends Dialog
{

    /** The connection. */
    private IBrowserConnection connection;

    /** The base. */
    private LdapDN base;

    /** The initial type. */
    private String initialType;

    /** The initial DN */
    private String initalDN;

    /** The return type */
    private String returnType;

    /** The return DN */
    private String returnDN;

    private static final String EMPTY = ""; //$NON-NLS-1$
    private static final String CHOP_BEFORE = "chopBefore"; //$NON-NLS-1$
    private static final String CHOP_AFTER = "chopAfter"; //$NON-NLS-1$

    // UI Fields
    private Combo typeCombo;
    private ComboViewer typeComboViewer;
    private EntryWidget entryWidget;


    /**
     * Creates a new instance of ExclusionDialog.
     *
     * @param parentShell the parent shell
     * @param connection the connection
     * @param base the base DN
     * @param exclusion the exclusion string
     */
    protected ExclusionDialog( Shell parentShell, IBrowserConnection connection, LdapDN base, String exclusion )
    {
        super( parentShell );
        this.connection = connection;
        this.base = base;

        try
        {
            // for example: chopAfter: "ou=A"
            Pattern pattern = Pattern.compile( "\\s*(chopBefore|chopAfter):\\s*\"(.*)\"\\s*" ); //$NON-NLS-1$
            Matcher matcher = pattern.matcher( exclusion );
            initialType = matcher.matches() ? matcher.group( 1 ) : EMPTY;
            initalDN = matcher.matches() ? matcher.group( 2 ) : EMPTY;
        }
        catch ( Exception e )
        {
            initialType = EMPTY;
            initalDN = EMPTY;
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "ExclusionValueEditor.title" ) ); //$NON-NLS-1$
        shell.setImage( Activator.getDefault().getImage( Messages.getString( "ExclusionValueEditor.icon" ) ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        returnType = typeCombo.getText();
        returnDN = entryWidget.getDn().toString();

        // save dn history 
        entryWidget.saveDialogSettings();

        super.okPressed();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );
        composite.setLayout( new GridLayout( 3, false ) );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "ExclusionValueEditor.label.type" ), 1 ); //$NON-NLS-1$
        typeCombo = new Combo( composite, SWT.READ_ONLY );
        String[] types = new String[2];
        types[0] = CHOP_BEFORE;
        types[1] = CHOP_AFTER;
        typeComboViewer = new ComboViewer( typeCombo );
        typeComboViewer.setContentProvider( new ArrayContentProvider() );
        typeComboViewer.setLabelProvider( new LabelProvider() );
        typeComboViewer.setInput( types );
        typeComboViewer.setSelection( new StructuredSelection( CHOP_BEFORE ), true );
        typeComboViewer.setSelection( new StructuredSelection( initialType ), true );
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.horizontalAlignment = GridData.BEGINNING;
        typeCombo.setLayoutData( gridData );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "ExclusionValueEditor.label.rdn" ), 1 ); //$NON-NLS-1$
        entryWidget = new EntryWidget( connection, null, base, true );
        entryWidget.createWidget( composite );
        try
        {
            LdapDN dn = new LdapDN( initalDN );
            entryWidget.setInput( connection, dn, base, true );
        }
        catch ( InvalidNameException e )
        {
        }
        entryWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        validate();

        return composite;
    }


    /**
     * Validates if the dn is valid.
     */
    private void validate()
    {
        boolean valid = entryWidget.getDn() != null && entryWidget.getDn().size() > 0;

        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( valid );
        }
    }


    /**
     * Get the type.
     *
     * @return
     *      the type, null if canceled
     */
    public String getType()
    {
        return returnType;
    }


    /**
     * Gets the DN.
     *
     * @return
     *      the DN, null if canceled
     */
    public String getDN()
    {
        return returnDN;
    }
}