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
package org.apache.directory.studio.schemaeditor.view.wrappers;


import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to wrap an AttributeType in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeWrapper extends AbstractTreeNode
{
    /** The wrapped AttributeType */
    private AttributeType attributeType;


    /**
     * Creates a new instance of AttributeTypeWrapper.
     *
     * @param at
     *      the wrapped AttributeType
     */
    public AttributeTypeWrapper( AttributeType at )
    {
        super( null );
        attributeType = at;
    }


    /**
     * Creates a new instance of AttributeTypeWrapper.
     * 
     * @param at
     *      the wrapped AttributeType
     * @param parent
     *      the parent TreeNode
     */
    public AttributeTypeWrapper( AttributeType at, TreeNode parent )
    {
        super( parent );
        attributeType = at;
    }


    /**
     * Gets the wrapped AttributeType.
     *
     * @return
     *      the wrapped AttributeType
     */
    public AttributeType getAttributeType()
    {
        return attributeType;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.view.wrappers.AbstractTreeNode#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof AttributeTypeWrapper )
        {
            if ( super.equals( obj ) )
            {
                AttributeTypeWrapper atw = ( AttributeTypeWrapper ) obj;

                if ( ( attributeType != null ) && ( !attributeType.equals( atw.getAttributeType() ) ) )
                {
                    return false;
                }

                return true;
            }
        }

        // Default
        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.view.wrappers.AbstractTreeNode#hashCode()
     */
    public int hashCode()
    {
        int result = super.hashCode();

        if ( attributeType != null )
        {
            result = 37 * result + attributeType.hashCode();
        }

        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return NLS.bind(
            Messages.getString( "AttributeTypeWrapper.AttributeTypeWrapper" ), new Object[] { attributeType, fParent } ); //$NON-NLS-1$
    }
}
