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
package org.apache.directory.ldapstudio.proxy.view.wrappers;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;


/**
 * This abstract class implements the IWrapper interface and represents a Wrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractWrapper implements IWrapper
{
    /** The parent element */
    protected IWrapper fParent;

    /** The children */
    protected List<IWrapper> fChildren;


    /**
     * Creates a new instance of Wrapper.
     *
     * @param parent
     *      the parent element
     */
    public AbstractWrapper( IWrapper parent )
    {
        fParent = parent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.proxy.view.wrappers.IWrapper#getText()
     */
    public String getText()
    {
        return toString();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.proxy.view.wrappers.IWrapper#getImage()
     */
    public Image getImage()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.proxy.view.wrappers.IWrapper#getChildren()
     */
    public List<IWrapper> getChildren()
    {
        if ( fChildren != null )
        {
            return fChildren;
        }

        fChildren = new ArrayList<IWrapper>();
        createChildren( fChildren );

        return fChildren;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.proxy.view.wrappers.IWrapper#getParent()
     */
    public IWrapper getParent()
    {
        return fParent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.proxy.view.wrappers.IWrapper#hasChildren()
     */
    public boolean hasChildren()
    {
        return true;
    }


    /**
     * Creates the children of the elements and adds them to the given children List
     *
     * @param children
     *      the children List to add children to
     */
    protected abstract void createChildren( List<IWrapper> children );
}
