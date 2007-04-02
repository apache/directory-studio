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

import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
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
 * TODO ExclusionValueEditor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExclusionValueEditor extends AbstractDialogStringValueEditor
{

    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogValueEditor#openDialog(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof ExclusionWrapper )
        {
            ExclusionWrapper wrapper = ( ExclusionWrapper ) value;
            ExclusionDialog dialog = new ExclusionDialog( shell, wrapper.type, null );
            dialog.open();
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns an MaxValueCountValueEditorRawValueWrapper.
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
            Pattern pattern = Pattern.compile( "\\s*([chopBefore|chopAfter]):\\s*\"(.*)\"\\s*" );
            Matcher matcher = pattern.matcher( stringValue );
            type = matcher.matches() ? matcher.group( 1 ) : "";
            rdn = matcher.matches() ? matcher.group( 2 ) : "";
        }
        catch ( Exception e )
        {
        }

        ExclusionWrapper wrapper = new ExclusionWrapper( type, rdn );
        return wrapper;
    }

    /**
     * The ExclusionWrapper is used to pass contextual 
     * information to the opened ExclusionDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class ExclusionWrapper
    {
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
        private ExclusionWrapper( String type, String rdn )
        {
            this.type = type;
            this.rdn = rdn;
        }
    }

    /**
     * TODO ExclusionDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class ExclusionDialog extends Dialog
    {
        /** The dialog title */
        public static final String DIALOG_TITLE = "Exclusion Editor";

        /** The initial type */
        private String initialType;

        /** The initial RDN */
        private String initialRDN;

        /** The return type */
        private String returnType;

        /** The return RDN */
        private String returnRDN;

        private Combo typeCombo;

        private static final String CHOP_BEFORE = "chopBefore";

        private static final String CHOP_AFTER = "chopAfter";


        /**
         * Creates a new instance of ExclusionDialog.
         *
         * @param parentShell
         */
        protected ExclusionDialog( Shell parentShell, String initialType, String initialRDN )
        {
            super( parentShell );
            this.initialType = initialType;
            this.initialRDN = initialRDN;
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
         */
        protected void configureShell( Shell shell )
        {
            super.configureShell( shell );
            shell.setText( DIALOG_TITLE );
        }


        /**
         * {@inheritDoc}
         */
        protected void okPressed()
        {
            returnType = typeCombo.getText();
            // initialRDN = rdnFields.getSelection();
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
            composite.setLayout( new GridLayout( 2, false ) );

            BaseWidgetUtils.createLabel( composite, "Type:", 1 );
            typeCombo = new Combo( composite, SWT.NONE );
            String[] types = new String[2];
            types[0] = CHOP_BEFORE;
            types[1] = CHOP_AFTER;
            ComboViewer typeComboViewer = new ComboViewer( typeCombo );
            typeComboViewer.setContentProvider( new ArrayContentProvider() );
            typeComboViewer.setLabelProvider( new LabelProvider() );
            typeComboViewer.setInput( types );
            typeComboViewer.setSelection( new StructuredSelection( initialType ) );

            BaseWidgetUtils.createLabel( composite, "RDN:", 2 );

            return composite;
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
