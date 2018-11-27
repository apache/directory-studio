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


import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * ACI item editor specific value editor to edit the RestrictedBy protected item.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RestrictedByValueEditor extends AbstractDialogStringValueEditor
{

    private static final String L_CURLY_TYPE = "{ type "; //$NON-NLS-1$
    private static final String SEP_VALUESIN = ", valuesIn "; //$NON-NLS-1$
    private static final String R_CURLY = " }"; //$NON-NLS-1$
    private static final String EMPTY = ""; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     * 
     * This implementation opens the RestrictedByDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        
        if ( value instanceof RestrictedByValueEditorRawValueWrapper )
        {
            RestrictedByValueEditorRawValueWrapper wrapper = ( RestrictedByValueEditorRawValueWrapper ) value;
            RestrictedByDialog dialog = new RestrictedByDialog( shell, wrapper.schema, wrapper.type, wrapper.valuesIn );
            
            if ( dialog.open() == TextDialog.OK && !EMPTY.equals( dialog.getType() )
                && !EMPTY.equals( dialog.getValuesIn() ) )
            {
                setValue( L_CURLY_TYPE + dialog.getType() + SEP_VALUESIN + dialog.getValuesIn() + R_CURLY );
                return true;
            }
        }
        
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns an AttributeTypeAndValueValueEditorRawValueWrapper.
     */
    public Object getRawValue( IValue value )
    {
        if ( value != null )
        {
            return getRawValue( value.getAttribute().getEntry().getBrowserConnection(), value
                .getStringValue() );
        }
        
        return null;
    }


    private Object getRawValue( IBrowserConnection connection, Object value )
    {
        Schema schema = null;
        
        if ( connection != null )
        {
            schema = connection.getSchema();
        }
        
        if ( schema == null || !( value instanceof String ) )
        {
            return null;
        }

        String stringValue = ( String ) value;
        String type = EMPTY;
        String valuesIn = EMPTY;
        try
        {
            // for example: { type sn, valuesIn cn }
            Pattern pattern = Pattern
                .compile( "\\s*\\{\\s*type\\s*([^,\\s]*)\\s*,\\s*valuesIn\\s*([^,\\s]*)\\s*\\}\\s*" ); //$NON-NLS-1$
            Matcher matcher = pattern.matcher( stringValue );
            type = matcher.matches() ? matcher.group( 1 ) : EMPTY;
            valuesIn = matcher.matches() ? matcher.group( 2 ) : EMPTY;
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }

        RestrictedByValueEditorRawValueWrapper wrapper = new RestrictedByValueEditorRawValueWrapper( schema, type,
            valuesIn );
        return wrapper;
    }

    /**
     * The RestrictedByValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened RestrictedByDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class RestrictedByValueEditorRawValueWrapper
    {
        /** 
         * The schema, used in RestrictedByDialog to build the list
         * with possible attribute types.
         */
        private final Schema schema;

        /** The type, used as initial type. */
        private final String type;

        /** The values in, used as initial values in. */
        private final String valuesIn;


        /**
         * Creates a new instance of RestrictedByValueEditorRawValueWrapper.
         * 
         * @param schema the schema
         * @param type the type
         * @param valuesIn the values in
         */
        private RestrictedByValueEditorRawValueWrapper( Schema schema, String type, String valuesIn )
        {
            this.schema = schema;
            this.type = type;
            this.valuesIn = valuesIn;
        }
    }

    /**
     * This class provides a dialog to enter the RestrictedBy values.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class RestrictedByDialog extends Dialog
    {

        /** The schema. */
        private Schema schema;

        /** The initial type. */
        private String initialType;

        /** The initial values in. */
        private String initialValuesIn;

        /** The type combo. */
        private Combo typeCombo;

        /** The values in combo. */
        private Combo valuesInCombo;

        /** The return type. */
        private String returnType;

        /** The return values in. */
        private String returnValuesIn;


        /**
         * Creates a new instance of RestrictedByDialog.
         * 
         * @param parentShell the parent shell
         * @param schema the schema
         * @param initialType the initial type
         * @param initialValuesIn the initial values in
         */
        public RestrictedByDialog( Shell parentShell, Schema schema, String initialType, String initialValuesIn )
        {
            super( parentShell );
            super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
            this.initialType = initialType;
            this.initialValuesIn = initialValuesIn;
            this.schema = schema;
            this.returnType = null;
            this.returnValuesIn = null;
        }


        /**
         * {@inheritDoc}
         */
        protected void configureShell( Shell shell )
        {
            super.configureShell( shell );
            shell.setText( Messages.getString( "RestrictedByValueEditor.title" ) ); //$NON-NLS-1$
            shell.setImage( Activator.getDefault().getImage( Messages.getString( "RestrictedByValueEditor.icon" ) ) ); //$NON-NLS-1$
        }


        /**
         * {@inheritDoc}
         */
        protected void okPressed()
        {
            returnType = typeCombo.getText();
            returnValuesIn = valuesInCombo.getText();
            super.okPressed();
        }


        /**
         * {@inheritDoc}
         */
        protected Control createDialogArea( Composite parent )
        {
            // create composite
            Composite composite = ( Composite ) super.createDialogArea( parent );
            GridData gd = new GridData( GridData.FILL_BOTH );
            gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
            composite.setLayoutData( gd );
            composite.setLayout( new GridLayout( 5, false ) );

            BaseWidgetUtils.createLabel( composite, L_CURLY_TYPE, 1 );

            // combo widget
            Collection<String> names = SchemaUtils.getNames( schema.getAttributeTypeDescriptions() );
            String[] allAtNames = names.toArray( new String[names.size()] );
            Arrays.sort( allAtNames );

            // type combo with field decoration and content proposal
            typeCombo = BaseWidgetUtils.createCombo( composite, allAtNames, -1, 1 );
            typeCombo.setText( initialType );
            new ExtendedContentAssistCommandAdapter( typeCombo, new ComboContentAdapter(),
                new ListContentProposalProvider( typeCombo.getItems() ), null, null, true );

            BaseWidgetUtils.createLabel( composite, SEP_VALUESIN, 1 );

            // valuesIn combo with field decoration and content proposal
            valuesInCombo = BaseWidgetUtils.createCombo( composite, allAtNames, -1, 1 );
            valuesInCombo.setText( initialValuesIn );
            new ExtendedContentAssistCommandAdapter( valuesInCombo, new ComboContentAdapter(),
                new ListContentProposalProvider( valuesInCombo.getItems() ), null, null, true );

            BaseWidgetUtils.createLabel( composite, R_CURLY, 1 );

            applyDialogFont( composite );
            return composite;
        }


        /**
         * Gets the type.
         * 
         * @return the type, null if canceled
         */
        public String getType()
        {
            return returnType;
        }


        /**
         * Gets the values in.
         * 
         * @return the values in, null if canceled
         */
        public String getValuesIn()
        {
            return returnValuesIn;
        }
    }
}
