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
package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Optional Table of the Attribute Type Editor (Used By Page).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATEUsedByOptionalTableContentProvider implements IStructuredContentProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof AttributeTypeImpl )
        {
            List<ObjectClassImpl> results = new ArrayList<ObjectClassImpl>();
            AttributeTypeImpl inputAT = ( AttributeTypeImpl ) inputElement;
            SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();

            List<String> names = new ArrayList<String>();

            String[] atNames = inputAT.getNamesRef();
            if ( atNames != null )
            {
                for ( String name : atNames )
                {
                    names.add( name.toLowerCase() );
                }
            }

            List<ObjectClassImpl> objectClasses = schemaHandler.getObjectClasses();
            for ( ObjectClassImpl oc : objectClasses )
            {
                String[] mays = oc.getMayNamesList();
                if ( mays != null )
                {
                    for ( String may : mays )
                    {
                        if ( names.contains( may.toLowerCase() ) )
                        {
                            results.add( oc );
                        }
                    }
                }
            }

            // Sorting Results
            Collections.sort( results, new Comparator<ObjectClassImpl>()
            {
                public int compare( ObjectClassImpl oc1, ObjectClassImpl oc2 )
                {
                    if ( oc1 instanceof ObjectClassImpl && oc1 instanceof ObjectClassImpl )
                    {
                        String[] oc1Names = ( ( ObjectClassImpl ) oc1 ).getNamesRef();
                        String[] oc2Names = ( ( ObjectClassImpl ) oc2 ).getNamesRef();

                        if ( ( oc1Names != null ) && ( oc2Names != null ) && ( oc1Names.length > 0 )
                            && ( oc2Names.length > 0 ) )
                        {
                            return oc1Names[0].compareToIgnoreCase( oc2Names[0] );
                        }
                    }

                    return 0;
                }
            } );

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
