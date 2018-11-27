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

import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
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
import org.eclipse.swt.widgets.Text;


/**
 * This class provides a dialog to enter an attribute type and value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeAndValueDialog extends Dialog
{

    /** The schema. */
    private Schema schema;

    /** The initial attribute type. */
    private String initialAttributeType;

    /** The initial value. */
    private String initialValue;

    /** The attribute type combo. */
    private Combo attributeTypeCombo;

    /** The value text. */
    private Text valueText;

    /** The return attribute type. */
    private String returnAttributeType;

    /** The return value. */
    private String returnValue;


    /**
     * Creates a new instance of AttributeTypeDialog.
     * 
     * @param parentShell the parent shell
     * @param schema the schema
     * @param initialAttributeType the initial attribute type
     * @param initialValue the initial value
     */
    public AttributeTypeAndValueDialog( Shell parentShell, Schema schema, String initialAttributeType,
        String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialAttributeType = initialAttributeType;
        this.initialValue = initialValue;
        this.schema = schema;
        this.returnValue = null;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "AttributeTypeAndValueDialog.title" ) ); //$NON-NLS-1$
        shell.setImage( Activator.getDefault().getImage( Messages.getString( "AttributeTypeAndValueDialog.icon" ) ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnAttributeType = attributeTypeCombo.getText();
        returnValue = valueText.getText();
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
        composite.setLayout( new GridLayout( 3, false ) );

        // combo widget
        Collection<String> names = SchemaUtils.getNames( schema.getAttributeTypeDescriptions() );
        String[] allAtNames = names.toArray( new String[names.size()] );
        Arrays.sort( allAtNames );

        // attribute combo with field decoration and content proposal
        attributeTypeCombo = BaseWidgetUtils.createCombo( composite, allAtNames, -1, 1 );
        attributeTypeCombo.setText( initialAttributeType );
        new ExtendedContentAssistCommandAdapter( attributeTypeCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( attributeTypeCombo.getItems() ), null, null, true );

        BaseWidgetUtils.createLabel( composite, " = ", 1 ); //$NON-NLS-1$

        valueText = BaseWidgetUtils.createText( composite, initialValue, 1 );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Gets the attribute type.
     * 
     * @return the attribute type, null if canceled
     */
    public String getAttributeType()
    {
        return returnAttributeType;
    }


    /**
     * Gets the value.
     * 
     * @return the value, null if canceled
     */
    public String getValue()
    {
        return returnValue;
    }

}
