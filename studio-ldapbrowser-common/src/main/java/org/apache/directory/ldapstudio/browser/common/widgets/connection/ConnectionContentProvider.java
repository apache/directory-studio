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

package org.apache.directory.ldapstudio.browser.common.widgets.connection;


import org.apache.directory.ldapstudio.browser.core.ConnectionManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * The ConnectionContentProvider represents the content provider for
 * the connection widget. It accepts the ConnectionManager as input
 * and returns its connections as elements.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionContentProvider implements IStructuredContentProvider
{

    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing.
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing.
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation accepts the ConnectionManager and returns its connections.
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement != null && inputElement instanceof ConnectionManager )
        {
            ConnectionManager cm = ( ConnectionManager ) inputElement;
            return cm.getConnections();
        }
        else
        {
            return new Object[]
                {};
        }
    }

}