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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.jobs.StudioRunnableWithProgress;


/**
 * Runnable to filter and sort the search result editor asynchronously to avoid 
 * freezing the GUI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterAndSortRunnable implements StudioRunnableWithProgress
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
     * Creates a new instance of FilterAndSortRunnable.
     * 
     * @param configuration the configuration
     * @param mainWidget the main widget
     * @param elements the elements, unfiltered and unsorted
     */
    public FilterAndSortRunnable( SearchResultEditorConfiguration configuration, SearchResultEditorWidget mainWidget,
        Object[] elements )
    {
        this.configuration = configuration;
        this.mainWidget = mainWidget;
        this.elements = elements;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return ""; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( Messages.getString( "FilterAndSortRunnable.FilterAndSort" ), 3 ); //$NON-NLS-1$
        monitor.worked( 1 );

        monitor.setTaskName( Messages.getString( "FilterAndSortRunnable.FilterAndSort" ) ); //$NON-NLS-1$

        monitor.reportProgress( Messages.getString( "FilterAndSortRunnable.Filtering" ) ); //$NON-NLS-1$
        this.filteredAndSortedElements = this.configuration.getFilter().filter( this.mainWidget.getViewer(), "", //$NON-NLS-1$
            elements );
        monitor.worked( 1 );

        monitor.reportProgress( Messages.getString( "FilterAndSortRunnable.Sorting" ) ); //$NON-NLS-1$
        this.configuration.getSorter().sort( this.mainWidget.getViewer(), this.filteredAndSortedElements );
        monitor.worked( 1 );
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return ""; //$NON-NLS-1$
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
