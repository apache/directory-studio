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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;


/**
 * ACI item editor specific value editor to edit the MaxValueCount protected item.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MaxValueCountValueEditor extends AbstractDialogStringValueEditor
{

    private static final String L_CURLY_TYPE = "{ type "; //$NON-NLS-1$
    private static final String SEP_MAXCOUNT = ", maxCount "; //$NON-NLS-1$
    private static final String R_CURLY = " }"; //$NON-NLS-1$
    private static final String EMPTY = ""; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     * 
     * This implementation opens the MaxValueCountDialog.
     */
    public boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof MaxValueCountValueEditorRawValueWrapper )
        {
            MaxValueCountValueEditorRawValueWrapper wrapper = ( MaxValueCountValueEditorRawValueWrapper ) value;
            MaxValueCountDialog dialog = new MaxValueCountDialog( shell, wrapper.schema, wrapper.type, wrapper.maxCount );
            if ( dialog.open() == TextDialog.OK && !EMPTY.equals( dialog.getType() ) && dialog.getMaxCount() > -1 )
            {
                setValue( L_CURLY_TYPE + dialog.getType() + SEP_MAXCOUNT + dialog.getMaxCount() + R_CURLY );
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
        return value != null ? getRawValue( value.getAttribute().getEntry().getBrowserConnection(), value.getStringValue() )
            : null;
    }


    private Object getRawValue( IBrowserConnection connection, Object value )
    {
        Schema schema = null;
        if ( connection != null )
        {
            schema = connection.getSchema();
        }
        if ( schema == null || value == null || !( value instanceof String ) )
        {
            return null;
        }

        String stringValue = ( String ) value;
        String type = EMPTY;
        int maxCount = 0;
        try
        {
            // for example: { type userPassword, maxCount 10 }
            Pattern pattern = Pattern.compile( "\\s*\\{\\s*type\\s*([^,]*),\\s*maxCount\\s*(\\d*)\\s*\\}\\s*" ); //$NON-NLS-1$
            Matcher matcher = pattern.matcher( stringValue );
            type = matcher.matches() ? matcher.group( 1 ) : EMPTY;
            maxCount = matcher.matches() ? Integer.valueOf( matcher.group( 2 ) ) : 0;
        }
        catch ( Exception e )
        {
        }

        MaxValueCountValueEditorRawValueWrapper wrapper = new MaxValueCountValueEditorRawValueWrapper( schema, type,
            maxCount );
        return wrapper;
    }

    /**
     * The MaxValueCountValueEditorRawValueWrapper is used to pass contextual 
     * information to the opened MaxValueCountDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class MaxValueCountValueEditorRawValueWrapper
    {
        /** 
         * The schema, used in MaxValueCountDialog to build the list
         * with possible attribute types.
         */
        private Schema schema;

        /** The attribute type, used as initial attribute type. */
        private String type;

        /** The max count, used as initial value. */
        private int maxCount;


        /**
         * Creates a new instance of AttributeTypeAndValueValueEditorRawValueWrapper.
         * 
         * @param schema the schema
         * @param attributeType the attribute type
         * @param value the value
         */
        private MaxValueCountValueEditorRawValueWrapper( Schema schema, String type, int maxCount )
        {
            this.schema = schema;
            this.type = type;
            this.maxCount = maxCount;
        }
    }

    /**
     * This class provides a dialog to enter the MaxValueCount values.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class MaxValueCountDialog extends Dialog
    {

        /** The schema. */
        private Schema schema;

        /** The initial attribute type. */
        private String initialType;

        /** The initial max count. */
        private int initialMaxCount;

        /** The attribute type combo field. */
        private DecoratedField attributeTypeComboField;

        /** The attribute type combo. */
        private Combo attributeTypeCombo;

        /** The attribute type content proposal adapter */
        private ContentProposalAdapter attributeTypeCPA;

        /** The max count spinner. */
        private Spinner maxCountSpinner;

        /** The return attribute type. */
        private String returnType;

        /** The return value. */
        private int returnMaxCount;


        /**
         * Creates a new instance of AttributeTypeDialog.
         * 
         * @param parentShell the parent shell
         * @param schema the schema
         * @param initialType the initial attribute type
         * @param initialMaxCount the initial max count
         */
        public MaxValueCountDialog( Shell parentShell, Schema schema, String initialType, int initialMaxCount )
        {
            super( parentShell );
            super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
            this.initialType = initialType;
            this.initialMaxCount = initialMaxCount;
            this.schema = schema;
            this.returnType = null;
            this.returnMaxCount = -1;
        }


        /**
         * {@inheritDoc}
         */
        protected void configureShell( Shell shell )
        {
            super.configureShell( shell );
            shell.setText( Messages.getString( "MaxValueCountValueEditor.title" ) ); //$NON-NLS-1$
            shell.setImage( Activator.getDefault().getImage( Messages.getString( "MaxValueCountValueEditor.icon" ) ) ); //$NON-NLS-1$
        }


        /**
         * {@inheritDoc}
         */
        protected void createButtonsForButtonBar( Composite parent )
        {
            super.createButtonsForButtonBar( parent );
        }


        /**
         * {@inheritDoc}
         */
        protected void okPressed()
        {
            returnType = attributeTypeCombo.getText();
            returnMaxCount = maxCountSpinner.getSelection();
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
            String[] allAtNames = schema.getAttributeTypeDescriptionNames();
            Arrays.sort( allAtNames );

            final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                FieldDecorationRegistry.DEC_CONTENT_PROPOSAL );
            attributeTypeComboField = new DecoratedField( composite, SWT.NONE, new IControlCreator()
            {
                public Control createControl( Composite parent, int style )
                {
                    Combo combo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
                    combo.setVisibleItemCount( 20 );
                    return combo;
                }
            } );
            attributeTypeComboField.addFieldDecoration( fieldDecoration, SWT.TOP | SWT.LEFT, true );
            attributeTypeComboField.getLayoutControl()
                .setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
            attributeTypeCombo = ( Combo ) attributeTypeComboField.getControl();
            attributeTypeCombo.setItems( allAtNames );
            attributeTypeCombo.setText( initialType );

            // content proposal adapter
            attributeTypeCPA = new ContentProposalAdapter( attributeTypeCombo, new ComboContentAdapter(),
                new ListContentProposalProvider( attributeTypeCombo.getItems() ), null, null );
            attributeTypeCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
            attributeTypeCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );

            BaseWidgetUtils.createLabel( composite, SEP_MAXCOUNT, 1 );

            maxCountSpinner = new Spinner( composite, SWT.BORDER );
            maxCountSpinner.setMinimum( 0 );
            maxCountSpinner.setMaximum( Integer.MAX_VALUE );
            maxCountSpinner.setDigits( 0 );
            maxCountSpinner.setIncrement( 1 );
            maxCountSpinner.setPageIncrement( 100 );
            maxCountSpinner.setSelection( initialMaxCount );
            maxCountSpinner.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

            BaseWidgetUtils.createLabel( composite, R_CURLY, 1 );

            applyDialogFont( composite );
            return composite;
        }


        /**
         * Gets the attribute type.
         * 
         * @return the attribute type, null if canceled
         */
        public String getType()
        {
            return returnType;
        }


        /**
         * Gets the max count.
         * 
         * @return the max count, -1 if canceled
         */
        public int getMaxCount()
        {
            return returnMaxCount;
        }

    }

}
