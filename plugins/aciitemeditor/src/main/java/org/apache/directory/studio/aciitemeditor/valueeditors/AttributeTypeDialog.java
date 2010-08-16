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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * This class provides a dialog to enter or select an attribute type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeDialog extends Dialog
{

    /** The schema. */
    private Schema schema;

    /** The initial value. */
    private String initialValue;

    /** The attribute type combo. */
    private Combo attributeTypeCombo;

    /** The return value. */
    private String returnValue;


    /**
     * Creates a new instance of AttributeTypeDialog.
     * 
     * @param parentShell the parent shell
     * @param schema the schema
     * @param initialValue the initial value
     */
    public AttributeTypeDialog( Shell parentShell, Schema schema, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
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
        shell.setText( Messages.getString( "AttributeTypeDialog.title" ) ); //$NON-NLS-1$
        shell.setImage( Activator.getDefault().getImage( Messages.getString( "AttributeTypeDialog.icon" ) ) ); //$NON-NLS-1$
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
        returnValue = attributeTypeCombo.getText();
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

        // combo widget
        Collection<String> names = SchemaUtils.getNames( schema.getAttributeTypeDescriptions() );
        String[] allAtNames = names.toArray( new String[names.size()] );
        Arrays.sort( allAtNames );

        // attribute combo with field decoration and content proposal
        attributeTypeCombo = BaseWidgetUtils.createCombo( composite, allAtNames, -1, 1 );
        attributeTypeCombo.setText( initialValue );
        new ExtendedContentAssistCommandAdapter( attributeTypeCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( attributeTypeCombo.getItems() ), null, null, true );

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
        return returnValue;
    }

}
