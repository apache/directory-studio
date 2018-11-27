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


import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to wrap an ObjectClass in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassWrapper extends AbstractTreeNode
{
    /** The wrapped ObjectClass */
    private ObjectClass objectClass;


    /**
     * Creates a new instance of ObjectClassWrapper.
     *
     * @param oc
     *      the wrapped ObjectClass
     */
    public ObjectClassWrapper( ObjectClass oc )
    {
        super( null );
        objectClass = oc;
    }


    /**
     * Creates a new instance of ObjectClassWrapper.
     * 
     * @param at
     *      the wrapped ObjectClass
     * @param parent
     *      the parent TreeNode
     */
    public ObjectClassWrapper( ObjectClass oc, TreeNode parent )
    {
        super( parent );
        objectClass = oc;
    }


    /**
     * Gets the wrapped ObjectClass.
     *
     * @return
     *      the wrapped ObjectClass
     */
    public ObjectClass getObjectClass()
    {
        return objectClass;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ObjectClassWrapper )
        {
            if ( super.equals( obj ) )
            {
                ObjectClassWrapper ocw = ( ObjectClassWrapper ) obj;

                if ( ( objectClass != null ) && ( !objectClass.equals( ocw.getObjectClass() ) ) )
                {
                    return false;
                }

                return true;
            }
        }

        // Default
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        int result = super.hashCode();

        if ( objectClass != null )
        {
            result = 37 * result + objectClass.hashCode();
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return NLS.bind(
            Messages.getString( "ObjectClassWrapper.ObjectWrapperClass" ), new Object[] { objectClass, fParent } ); //$NON-NLS-1$
    }
}
