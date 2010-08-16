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
package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingObjectClass;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Superiors Table of the Object
 * Class Editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassEditorSuperiorsTableContentProvider implements IStructuredContentProvider
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;


    /**
     * Creates a new instance of
     * ObjectClassFormEditorSuperiorsTableContentProvider.
     */
    public ObjectClassEditorSuperiorsTableContentProvider()
    {
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String[] )
        {
            List<Object> results = new ArrayList<Object>();

            String[] superiors = ( String[] ) inputElement;
            for ( String superior : superiors )
            {
                ObjectClassImpl oc = schemaHandler.getObjectClass( superior );
                if ( oc != null )
                {
                    results.add( oc );
                }
                else
                {
                    results.add( new NonExistingObjectClass( superior ) );
                }
            }

            // Sorting Elements
            Collections.sort( results, new Comparator<Object>()
            {
                public int compare( Object o1, Object o2 )
                {
                    if ( o1 instanceof ObjectClassImpl && o2 instanceof ObjectClassImpl )
                    {
                        String[] oc1Names = ( ( ObjectClassImpl ) o1 ).getNamesRef();
                        String[] oc2Names = ( ( ObjectClassImpl ) o2 ).getNamesRef();

                        if ( ( oc1Names != null ) && ( oc2Names != null ) && ( oc1Names.length > 0 )
                            && ( oc2Names.length > 0 ) )
                        {
                            return oc1Names[0].compareToIgnoreCase( oc2Names[0] );
                        }
                    }
                    else if ( o1 instanceof ObjectClassImpl && o2 instanceof NonExistingObjectClass )
                    {
                        String[] oc1Names = ( ( ObjectClassImpl ) o1 ).getNamesRef();
                        String oc2Name = ( ( NonExistingObjectClass ) o2 ).getName();

                        if ( ( oc1Names != null ) && ( oc2Name != null ) && ( oc1Names.length > 0 ) )
                        {
                            return oc1Names[0].compareToIgnoreCase( oc2Name );
                        }
                    }
                    else if ( o1 instanceof NonExistingObjectClass && o2 instanceof ObjectClassImpl )
                    {
                        String oc1Name = ( ( NonExistingObjectClass ) o1 ).getName();
                        String[] oc2Names = ( ( ObjectClassImpl ) o2 ).getNamesRef();

                        if ( ( oc1Name != null ) && ( oc2Names != null ) && ( oc2Names.length > 0 ) )
                        {
                            return oc1Name.compareToIgnoreCase( oc2Names[0] );
                        }
                    }
                    else if ( o1 instanceof NonExistingObjectClass && o2 instanceof NonExistingObjectClass )
                    {
                        String oc1Name = ( ( NonExistingObjectClass ) o1 ).getName();
                        String oc2Name = ( ( NonExistingObjectClass ) o2 ).getName();

                        if ( ( oc1Name != null ) && ( oc2Name != null ) )
                        {
                            return oc1Name.compareToIgnoreCase( oc2Name );
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


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }
}
