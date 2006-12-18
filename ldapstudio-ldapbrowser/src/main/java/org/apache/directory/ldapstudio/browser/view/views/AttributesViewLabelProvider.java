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

package org.apache.directory.ldapstudio.browser.view.views;


import org.apache.directory.ldapstudio.browser.view.views.wrappers.AttributeValueWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.AttributeWrapper;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;


/**
 * This class is the Label Provider for the Attributes View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider,
    ITableColorProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText( Object element, int columnIndex )
    {
        if ( element instanceof AttributeWrapper )
        {
            return ( ( AttributeWrapper ) element ).getColumnText( element, columnIndex );
        }
        else if ( element instanceof AttributeValueWrapper )
        {
            return ( ( AttributeValueWrapper ) element ).getColumnText( element, columnIndex );
        }
        // Default return (should never be used)
        return "";
    }


    public Font getFont( Object element, int columnIndex )
    {
        if ( ( element instanceof AttributeWrapper ) && ( columnIndex == 1 ) )
        {
            AttributeWrapper attributeWrapper = ( AttributeWrapper ) element;

            if ( attributeWrapper.getChildren().length > 1 )
            {
                return new Font( null, "Geneva", 9, SWT.ITALIC );
            }
        }
        return null;
    }


    public Color getBackground( Object element, int columnIndex )
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Color getForeground( Object element, int columnIndex )
    {
        if ( ( element instanceof AttributeWrapper ) && ( columnIndex == 1 ) )
        {
            AttributeWrapper attributeWrapper = ( AttributeWrapper ) element;

            if ( attributeWrapper.getChildren().length > 1 )
            {
                return PlatformUI.getWorkbench().getDisplay().getSystemColor( SWT.COLOR_GRAY );
            }
        }
        return null;
    }
}
