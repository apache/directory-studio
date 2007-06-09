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

package org.apache.directory.ldapstudio.browser.ui.dialogs.properties;


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class ValuePropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    private Text descriptionText;

    private Text valueText;

    private Text typeText;

    private Text sizeText;


    public ValuePropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    protected Control createContents( Composite parent )
    {

        IValue value = getValue( getElement() );

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        Composite mainGroup = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        BaseWidgetUtils.createLabel( mainGroup, "Attribute Description:", 1 );
        descriptionText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );

        BaseWidgetUtils.createLabel( mainGroup, "Value Type:", 1 );
        typeText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );

        BaseWidgetUtils.createLabel( mainGroup, "Value Size:", 1 );
        sizeText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );

        BaseWidgetUtils.createLabel( mainGroup, "Data:", 1 );
        if ( value != null && value.isString() )
        {
            valueText = new Text( mainGroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
            valueText.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );
            GridData gd = new GridData( GridData.FILL_BOTH );
            gd.widthHint = convertHorizontalDLUsToPixels( ( int ) ( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 2 ) );
            gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 4 );
            valueText.setLayoutData( gd );
            valueText.setBackground( parent.getBackground() );
        }
        else
        {
            valueText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );
        }

        if ( value != null )
        {

            super.setMessage( "Value " + Utils.shorten( value.toString(), 30 ) );

            descriptionText.setText( value.getAttribute().getDescription() );
            // valueText.setText(LdifUtils.mustEncode(value.getBinaryValue())?"Binary":value.getStringValue());
            valueText.setText( value.isString() ? value.getStringValue() : "Binary" );
            typeText.setText( value.isString() ? "String" : "Binary" );

            int bytes = value.getBinaryValue().length;
            int chars = value.isString() ? value.getStringValue().length() : 0;
            String size = value.isString() ? chars + ( chars > 1 ? " Characters, " : " Character, " ) : "";
            size += Utils.formatBytes( bytes );
            sizeText.setText( size );
        }

        return parent;
    }


    private static IValue getValue( Object element )
    {
        IValue value = null;
        if ( element instanceof IAdaptable )
        {
            value = ( IValue ) ( ( IAdaptable ) element ).getAdapter( IValue.class );
        }
        return value;
    }

}
