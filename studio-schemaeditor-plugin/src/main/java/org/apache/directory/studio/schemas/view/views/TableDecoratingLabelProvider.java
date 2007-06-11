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
package org.apache.directory.studio.schemas.view.views;


import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.swt.graphics.Image;


/**
 *  Class that supports Decoration of TableViewer and TreeViewer with TreeColumns
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class TableDecoratingLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider
{
    ITableLabelProvider provider;
    ILabelDecorator decorator;


    /**
     * @param provider
     * @param decorator
     */
    public TableDecoratingLabelProvider( ILabelProvider provider, ILabelDecorator decorator )
    {
        super( provider, decorator );
        this.provider = ( ITableLabelProvider ) provider;
        this.decorator = decorator;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        Image image = provider.getColumnImage( element, columnIndex );
        if ( decorator != null )
        {
            if ( decorator instanceof LabelDecorator )
            {
                LabelDecorator ld2 = ( LabelDecorator ) decorator;
                Image decorated = ld2.decorateImage( image, element, getDecorationContext() );
                if ( decorated != null )
                {
                    return decorated;
                }
            }
            else
            {
                Image decorated = decorator.decorateImage( image, element );
                if ( decorated != null )
                {
                    return decorated;
                }
            }
        }
        return image;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText( Object element, int columnIndex )
    {
        String text = provider.getColumnText( element, columnIndex );
        if ( decorator != null )
        {
            if ( decorator instanceof LabelDecorator )
            {
                LabelDecorator ld2 = ( LabelDecorator ) decorator;
                String decorated = ld2.decorateText( text, element, getDecorationContext() );
                if ( decorated != null )
                {
                    return decorated;
                }
            }
            else
            {
                String decorated = decorator.decorateText( text, element );
                if ( decorated != null )
                {
                    return decorated;
                }
            }
        }
        return text;
    }
}