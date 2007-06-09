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

package org.apache.directory.ldapstudio.schemas.view.dialogs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Object Class Selection Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassSelectionDialogContentProvider implements IStructuredContentProvider
{
    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The hidden Object Classes */
    private List<ObjectClass> hiddenObjectClasses;


    /**
     * Creates a new instance of ObjectClassSelectionDialogContentProvider.
     */
    public ObjectClassSelectionDialogContentProvider( List<ObjectClass> hiddenObjectClasses )
    {
        schemaPool = SchemaPool.getInstance();
        this.hiddenObjectClasses = hiddenObjectClasses;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String )
        {
            ArrayList<ObjectClassWrapper> results = new ArrayList<ObjectClassWrapper>();

            String searchText = ( String ) inputElement;

            String searchRegexp;
            if ( searchText.length() == 0 )
            {
                searchRegexp = ".*"; //$NON-NLS-1$
            }
            else
            {
                searchRegexp = searchText + ".*"; //$NON-NLS-1$
            }
            Pattern pattern = Pattern.compile( searchRegexp, Pattern.CASE_INSENSITIVE );

            List<ObjectClass> ocList = schemaPool.getObjectClasses();

            // Sorting the list
            Collections.sort( ocList, new Comparator<ObjectClass>()
            {
                public int compare( ObjectClass oc1, ObjectClass oc2 )
                {
                    if ( ( oc1.getNames() == null || oc1.getNames().length == 0 )
                        && ( oc2.getNames() == null || oc2.getNames().length == 0 ) )
                    {
                        return 0;
                    }
                    else if ( ( oc1.getNames() == null || oc1.getNames().length == 0 )
                        && ( oc2.getNames() != null && oc2.getNames().length > 0 ) )
                    {
                        return "".compareToIgnoreCase( oc2.getNames()[0] ); //$NON-NLS-1$
                    }
                    else if ( ( oc1.getNames() != null && oc1.getNames().length > 0 )
                        && ( oc2.getNames() == null || oc2.getNames().length == 0 ) )
                    {
                        return oc1.getNames()[0].compareToIgnoreCase( "" ); //$NON-NLS-1$
                    }
                    else
                    {
                        return oc1.getNames()[0].compareToIgnoreCase( oc2.getNames()[0] );
                    }
                }
            } );

            // Searching for all matching elements
            for ( ObjectClass oc : ocList )
            {
                for ( String name : oc.getNames() )
                {
                    Matcher m = pattern.matcher( name );
                    if ( m.matches() )
                    {
                        if ( !hiddenObjectClasses.contains( oc ) )
                        {
                            ObjectClassWrapper ocw = new ObjectClassWrapper( oc, null );
                            if ( !results.contains( ocw ) )
                            {
                                results.add( new ObjectClassWrapper( oc, null ) );
                            }
                        }
                        break;
                    }
                }
                Matcher m = pattern.matcher( oc.getOid() );
                if ( m.matches() )
                {
                    if ( !hiddenObjectClasses.contains( oc ) )
                    {
                        ObjectClassWrapper ocw = new ObjectClassWrapper( oc, null );
                        if ( !results.contains( ocw ) )
                        {
                            results.add( new ObjectClassWrapper( oc, null ) );
                        }
                    }
                }
            }

            // Returns the results
            return results.toArray();
        }

        // Default
        return new Object[0];
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
