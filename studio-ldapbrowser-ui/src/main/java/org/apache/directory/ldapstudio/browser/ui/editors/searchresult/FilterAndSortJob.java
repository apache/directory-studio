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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;


/**
 * This job to filter and sort the search result editor asynchrously to avoid 
 * freezing the GUI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterAndSortJob extends AbstractEclipseJob
{

    /** The configuration. */
    private SearchResultEditorConfiguration configuration;

    /** The main widget. */
    private SearchResultEditorWidget mainWidget;

    /** All elements, unfiltered and unsorted. */
    private Object[] elements;

    /** The filtered and sorted elements. */
    private Object[] filteredAndSortedElements;


    /**
     * Creates a new instance of FilterAndSortJob.
     * 
     * @param configuration the configuration
     * @param mainWidget the main widget
     * @param elements the elements, unfiltered and unsorted
     */
    public FilterAndSortJob( SearchResultEditorConfiguration configuration, SearchResultEditorWidget mainWidget,
        Object[] elements )
    {
        this.configuration = configuration;
        this.mainWidget = mainWidget;
        this.elements = elements;
    }


    /**
     * {@inheritDoc}
     */
    protected Object[] getLockedObjects()
    {
        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    protected void executeAsyncJob( ExtendedProgressMonitor monitor ) throws Exception
    {
        monitor.beginTask( "Filter and Sort", 3 );
        monitor.worked( 1 );

        monitor.setTaskName( "Filter and Sort" );

        monitor.reportProgress( "Filtering..." );
        this.filteredAndSortedElements = this.configuration.getFilter().filter( this.mainWidget.getViewer(), "",
            elements );
        monitor.worked( 1 );

        monitor.reportProgress( "Sorting..." );
        this.configuration.getSorter().sort( this.mainWidget.getViewer(), this.filteredAndSortedElements );
        monitor.worked( 1 );
    }


    /**
     * {@inheritDoc}
     */
    protected IConnection[] getConnections()
    {
        return new IConnection[0];
    }


    /**
     * Gets the filtered and sorted elements.
     * 
     * @return the filtered and sorted elements
     */
    public Object[] getFilteredAndSortedElements()
    {
        return filteredAndSortedElements;
    }

}
