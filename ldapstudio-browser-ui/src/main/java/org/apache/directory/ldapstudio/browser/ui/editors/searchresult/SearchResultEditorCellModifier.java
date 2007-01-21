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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import java.util.Iterator;

import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;


public class SearchResultEditorCellModifier implements ICellModifier
{

    private TableViewer viewer;

    private ValueEditorManager valueEditorManager;


    public SearchResultEditorCellModifier( TableViewer viewer, ValueEditorManager valueEditorManager )
    {
        this.viewer = viewer;
        this.valueEditorManager = valueEditorManager;
    }


    public void dispose()
    {
        this.viewer = null;
        this.valueEditorManager = null;
    }


    public boolean canModify( Object element, String property )
    {

        if ( element != null && element instanceof ISearchResult && property != null )
        {
            ISearchResult result = ( ISearchResult ) element;
            AttributeHierarchy ah = result.getAttributeWithSubtypes( property );

            // check DN
            if ( BrowserUIConstants.DN.equals( property ) )
            {
                return false;
            }

            // attribute dummy
            if ( ah == null )
            {
                try
                {
                    ah = new AttributeHierarchy( result.getEntry(), property, new IAttribute[]
                        { new Attribute( result.getEntry(), property ) } );
                }
                catch ( ModelModificationException e )
                {
                    e.printStackTrace();
                    return false;
                }
            }

            // check schema modifyable
            boolean isOneModifyable = false;
            for ( Iterator it = ah.iterator(); it.hasNext(); )
            {
                IAttribute attribute = ( IAttribute ) it.next();
                if ( SchemaUtils.isModifyable( attribute.getAttributeTypeDescription() ) )
                {
                    isOneModifyable = true;
                    break;
                }
            }
            if ( !isOneModifyable )
            {
                return false;
            }

            // check if property is valid for the entry
            boolean isOneValid = false;
            for ( Iterator it = ah.iterator(); it.hasNext(); )
            {
                IAttribute attribute = ( IAttribute ) it.next();
                if ( attribute.isObjectClassAttribute() || attribute.isMustAttribute() || attribute.isMayAttribute() )
                {
                    isOneValid = true;
                    break;
                }
            }
            if ( !isOneValid )
            {
                return false;
            }

            // call value editor
            return this.valueEditorManager.getCurrentValueEditor( ah ).getRawValue( ah ) != null;
        }
        else
        {
            return false;
        }
    }


    public Object getValue( Object element, String property )
    {

        if ( element != null && element instanceof ISearchResult && property != null )
        {
            ISearchResult result = ( ISearchResult ) element;
            AttributeHierarchy ah = result.getAttributeWithSubtypes( property );

            if ( !this.canModify( element, property ) )
            {
                return null;
            }

            if ( ah == null )
            {
                try
                {
                    ah = new AttributeHierarchy( result.getEntry(), property, new IAttribute[]
                        { new Attribute( result.getEntry(), property ) } );
                }
                catch ( ModelModificationException e )
                {
                    e.printStackTrace();
                    return null;
                }
            }

            return this.valueEditorManager.getCurrentValueEditor( ah ).getRawValue( ah );
        }
        else
        {
            return null;
        }
    }


    public void modify( Object element, String property, Object newRawValue )
    {

        if ( element != null && element instanceof Item )
        {
            element = ( ( Item ) element ).getData();
        }

        if ( element != null && element instanceof ISearchResult && property != null )
        {
            ISearchResult result = ( ISearchResult ) element;
            AttributeHierarchy ah = result.getAttributeWithSubtypes( property );

            try
            {
                // switch operation:
                if ( ah == null && newRawValue != null )
                {
                    this.valueEditorManager.getCurrentValueEditor( result.getEntry(), property ).createValue(
                        result.getEntry(), property, newRawValue );
                }
                else if ( ah != null && newRawValue == null )
                {
                    this.valueEditorManager.getCurrentValueEditor( ah ).deleteAttribute( ah );
                }
                else if ( ah != null && ah.size() == 1 && ah.getAttribute().getValueSize() == 1 && newRawValue != null )
                {
                    this.valueEditorManager.getCurrentValueEditor( ah ).modifyValue( ah.getAttribute().getValues()[0],
                        newRawValue );
                }
            }
            catch ( ModelModificationException mme )
            {
                MessageDialog.openError( this.viewer.getTable().getShell(), "Error While Modifying Value", mme
                    .getMessage() );
            }
        }
    }

}
