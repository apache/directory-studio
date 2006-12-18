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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.apache.directory.ldapstudio.browser.view.views.wrappers.AttributeValueWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.AttributeWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;


/**
 * This class is the Content Provider for the Attributes View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof EntryWrapper )
        {
            // Initializing the retun list
            List<AttributeWrapper> returnList = new ArrayList<AttributeWrapper>();

            // Getting the entry and looping on its attributes
            SearchResultEntry entry = ( ( EntryWrapper ) inputElement ).getEntry();
            NamingEnumeration ne = entry.getPartialAttributeList().getAll();

            while ( ne.hasMoreElements() )
            {
                returnList.add( new AttributeWrapper( ( Attribute ) ne.nextElement(), ( EntryWrapper ) inputElement ) );
            }
            return returnList.toArray();
        }

        // Default return (should never be used)
        return new Object[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do here but the method is needed 
        // by IContentProvider Interface
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do here but the method is needed 
        // by IContentProvider Interface
    }


    public Object[] getChildren( Object parentElement )
    {
        if ( parentElement instanceof AttributeWrapper )
        {
            return ( ( AttributeWrapper ) parentElement ).getChildren();
            
        }
        return null;
    }


    public Object getParent( Object element )
    {
        if ( element instanceof AttributeValueWrapper )
        {
            return ( ( AttributeValueWrapper ) element ).getParent();
        }
        return null;
    }


    public boolean hasChildren( Object element )
    {
        if ( element instanceof AttributeWrapper )
        {
            return ( ( AttributeWrapper ) element).hasChildren();
            
        }
        return false;
    }

}
