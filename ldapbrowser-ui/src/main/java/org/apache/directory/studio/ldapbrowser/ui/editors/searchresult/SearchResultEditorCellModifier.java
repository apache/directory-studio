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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;


/**
 * The SearchResultEditorCellModifier implements the {@link ICellModifier} interface
 * for the search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEditorCellModifier implements ICellModifier
{

    /** The value editor manager. */
    private ValueEditorManager valueEditorManager;


    /**
     * Creates a new instance of SearchResultEditorCellModifier.
     * 
     * @param valueEditorManager the value editor manager
     */
    public SearchResultEditorCellModifier( ValueEditorManager valueEditorManager )
    {
        this.valueEditorManager = valueEditorManager;
    }


    /**
     * Disposes this cell modifier.
     */
    public void dispose()
    {
        valueEditorManager = null;
    }


    /**
     * {@inheritDoc}
     */
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
                ah = new AttributeHierarchy( result.getEntry(), property, new IAttribute[]
                    { new Attribute( result.getEntry(), property ) } );
            }

            // check schema modifiable
            boolean isOneModifyable = false;
            for ( IAttribute attribute : ah )
            {
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
            for ( IAttribute attribute : ah )
            {
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
            return valueEditorManager.getCurrentValueEditor( ah ).getRawValue( ah ) != null;
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Object getValue( Object element, String property )
    {
        if ( element != null && element instanceof ISearchResult && property != null )
        {
            ISearchResult result = ( ISearchResult ) element;
            AttributeHierarchy ah = result.getAttributeWithSubtypes( property );

            if ( !canModify( element, property ) )
            {
                return null;
            }

            if ( ah == null )
            {
                ah = new AttributeHierarchy( result.getEntry(), property, new IAttribute[]
                    { new Attribute( result.getEntry(), property ) } );
            }

            return valueEditorManager.getCurrentValueEditor( ah ).getRawValue( ah );
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
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

            // switch operation:
            if ( ah == null && newRawValue != null )
            {
                valueEditorManager.createValue( result.getEntry(), property, newRawValue );
            }
            else if ( ah != null && newRawValue == null )
            {
                valueEditorManager.deleteAttribute( ah );
            }
            else if ( ah != null && ah.size() == 1 && ah.getAttribute().getValueSize() == 1 && newRawValue != null )
            {
                valueEditorManager.modifyValue( ah.getAttribute().getValues()[0], newRawValue );
            }
        }
    }

}
