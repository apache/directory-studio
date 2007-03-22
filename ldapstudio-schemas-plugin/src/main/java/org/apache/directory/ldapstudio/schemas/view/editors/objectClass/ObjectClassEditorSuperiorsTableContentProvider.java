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
package org.apache.directory.ldapstudio.schemas.view.editors.objectClass;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Superiors Table of the Object Class Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassEditorSuperiorsTableContentProvider implements IStructuredContentProvider
{
    /** The Schema Pool */
    private SchemaPool schemaPool;


    /**
     * Creates a new instance of ObjectClassFormEditorSuperiorsTableContentProvider.
     */
    public ObjectClassEditorSuperiorsTableContentProvider()
    {
        schemaPool = SchemaPool.getInstance();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String[] )
        {
            List<ObjectClassWrapper> results = new ArrayList<ObjectClassWrapper>();

            String[] superiors = ( String[] ) inputElement;
            for ( String superior : superiors )
            {
                ObjectClass oc = schemaPool.getObjectClass( superior );
                if ( oc != null )
                {
                    results.add( new ObjectClassWrapper( oc, null ) );
                }
                else
                {
                    ObjectClassLiteral ocl = new ObjectClassLiteral( "" );
                    ocl.setNames( new String[]
                        { superior } );
                    ObjectClass newOC = new ObjectClass( ocl, null );
                    results.add( new ObjectClassWrapper( newOC, null ) );
                }
            }

            return results.toArray();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }
}
