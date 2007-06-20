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
package org.apache.directory.studio.apacheds.schemaeditor.view.widget;


import java.util.Collections;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the DifferencesWidget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetContentProvider implements IStructuredContentProvider
{
    /** The DifferencesWidgetPropertySorter */
    private DifferencesWidgetPropertySorter propertySorter;

    /** The DifferencesWidgetTypeSorter */
    private DifferencesWidgetTypeSorter typeSorter;

    /** The PreferenceStore */
    private IPreferenceStore store;


    /**
     * Creates a new instance of DifferencesWidgetContentProvider.
     *
     */
    public DifferencesWidgetContentProvider()
    {
        propertySorter = new DifferencesWidgetPropertySorter();
        typeSorter = new DifferencesWidgetTypeSorter();

        store = Activator.getDefault().getPreferenceStore();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof List )
        {
            List<Difference> differences = ( List<Difference> ) inputElement;

            int prefValue = store.getInt( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING );
            if ( prefValue == PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY )
            {
                Collections.sort( differences, propertySorter );
            }
            else if ( prefValue == PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE )
            {
                Collections.sort( differences, typeSorter );
            }

            return differences.toArray();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing do to
    }
}
