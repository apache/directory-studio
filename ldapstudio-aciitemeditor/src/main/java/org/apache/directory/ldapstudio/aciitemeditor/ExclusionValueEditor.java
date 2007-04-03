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
package org.apache.directory.ldapstudio.aciitemeditor;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.EntryWidget;
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
 * ACI item editor specific value editor to edit an Exclusion.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExclusionValueEditor extends AbstractDialogStringValueEditor
{
    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogValueEditor#openDialog(org.eclipse.swt.widgets.Shell)
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof ExclusionWrapper )
        {
            ExclusionDialog dialog = new ExclusionDialog( shell, ( ExclusionWrapper ) value );
            if ( dialog.open() == TextDialog.OK && !"".equals( dialog.getType() ) && !"".equals( dialog.getRDN() ) )
            {
                setValue( dialog.getType() + ": \"" + dialog.getRDN() + "\"" );
                return true;
            }
        }

        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor#getRawValue(org.apache.directory.ldapstudio.browser.core.model.IConnection, java.lang.Object)
     */
    public Object getRawValue( IConnection connection, Object value )
    {
        if ( value == null || !( value instanceof String ) )
        {
            return null;
        }

        String stringValue = ( String ) value;
        String type = "";
        String rdn = "";
        try
        {
            // for example: chopAfter: "ou=A"
            Pattern pattern = Pattern.compile( "\\s*(chopBefore|chopAfter):\\s*\"(.*)\"\\s*" );
            Matcher matcher = pattern.matcher( stringValue );
            type = matcher.matches() ? matcher.group( 1 ) : "";
            rdn = matcher.matches() ? matcher.group( 2 ) : "";
        }
        catch ( Exception e )
        {
        }

        ExclusionWrapper wrapper = new ExclusionWrapper( connection, type, rdn );
        return wrapper;
    }

    /**
     * The ExclusionWrapper is used to pass contextual information to the opened ExclusionDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class ExclusionWrapper
    {
        /** The connection */
        private IConnection connection;

        /** The type, used as initial type. */
        private String type;

        /** The RDN, used as initial RDN. */
        private String rdn;


        /**
         * Creates a new instance of ExclusionWrapper.
         * 
         * @param type
         *      the type
         * @param rdn
         *      the rdn
         */
        private ExclusionWrapper( IConnection connection, String type, String rdn )
        {
            this.connection = connection;
            this.type = type;
            this.rdn = rdn;
        }
    }

    /**
     * This class provides a dialog to enter the Exclusion values.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class ExclusionDialog extends Dialog
    {
        /** The dialog title */
        public static final String DIALOG_TITLE = "Exclusion Editor";

        /** The return type */
        private String returnType;

        /** The return RDN */
        private String returnRDN;

        private Combo typeCombo;

        private ExclusionWrapper wrapper;

        private ComboViewer typeComboViewer;

        private EntryWidget entryWidget;

        private static final String CHOP_BEFORE = "chopBefore";

        private static final String CHOP_AFTER = "chopAfter";


        /**
         * Creates a new instance of ExclusionDialog.
         *
         * @param parentShell
         */
        protected ExclusionDialog( Shell parentShell, ExclusionWrapper wrapper )
        {
            super( parentShell );
            this.wrapper = wrapper;
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
         */
        protected void configureShell( Shell shell )
        {
            super.configureShell( shell );
            shell.setText( DIALOG_TITLE );
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.dialogs.Dialog#okPressed()
         */
        protected void okPressed()
        {
            returnType = typeCombo.getText();
            returnRDN = entryWidget.getDn().toString();
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

            BaseWidgetUtils.createLabel( composite, "Type:", 1 );
            typeCombo = new Combo( composite, SWT.READ_ONLY );
            String[] types = new String[2];
            types[0] = CHOP_BEFORE;
            types[1] = CHOP_AFTER;
            typeComboViewer = new ComboViewer( typeCombo );
            typeComboViewer.setContentProvider( new ArrayContentProvider() );
            typeComboViewer.setLabelProvider( new LabelProvider() );
            typeComboViewer.setInput( types );
            GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            gridData.grabExcessHorizontalSpace = true;
            gridData.verticalAlignment = GridData.CENTER;
            gridData.horizontalAlignment = GridData.BEGINNING;
            typeCombo.setLayoutData( gridData );

            BaseWidgetUtils.createLabel( composite, "RDN:", 1 );
            entryWidget = new EntryWidget( wrapper.connection, null );
            entryWidget.createWidget( composite );

            initFromInput();

            return composite;
        }


        /**
         * Initializes the Value Editor from the input.
         */
        private void initFromInput()
        {
            typeComboViewer.setSelection( new StructuredSelection( wrapper.type ) );

            try
            {
                DN dn = new DN( wrapper.rdn );
                entryWidget.setInput( wrapper.connection, dn );
            }
            catch ( NameException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
         * Gets the RDN.
         *
         * @return
         *      the RDN, null if canceled
         */
        public String getRDN()
        {
            return returnRDN;
        }
    }
}
