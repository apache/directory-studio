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


import org.apache.directory.studio.common.ui.widgets.ViewFormWidget;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;


/**
 * The EntryEditorWidget is a widget to display and edit the attributes of 
 * the results of a search.
 * 
 * It provides a context menu and a local toolbar with actions to
 * manage attributes. Further there is an instant search feature to filter 
 * the visible search results.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorWidget extends ViewFormWidget
{

    /** The configuration. */
    private SearchResultEditorConfiguration configuration;

    /** The quick filter widget. */
    private SearchResultEditorQuickFilterWidget quickFilterWidget;

    /** The table. */
    private Table table;

    /** The viewer. */
    private TableViewer viewer;


    /**
     * Creates a new instance of SearchResultEditorWidget.
     * 
     * @param configuration the configuration
     */
    public SearchResultEditorWidget( SearchResultEditorConfiguration configuration )
    {
        this.configuration = configuration;
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContent( Composite parent )
    {
        // create quick filter
        quickFilterWidget = new SearchResultEditorQuickFilterWidget( configuration.getFilter() );
        quickFilterWidget.createComposite( parent );

        // create table widget and viewer
        table = new Table( parent, SWT.BORDER | SWT.HIDE_SELECTION | SWT.VIRTUAL );
        table.setHeaderVisible( true );
        table.setLinesVisible( true );
        table.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        viewer = new TableViewer( table );
        viewer.setUseHashlookup( true );

        // setup providers
        viewer.setContentProvider( configuration.getContentProvider( this ) );
        viewer.setLabelProvider( configuration.getLabelProvider( viewer ) );

        // set table cell editors
        viewer.setCellModifier( configuration.getCellModifier( viewer ) );

        return table;
    }


    /**
     * Sets the input.
     * 
     * @param input the new input
     */
    public void setInput( Object input )
    {
        viewer.setInput( input );
    }


    /**
     * Sets the focus.
     */
    public void setFocus()
    {
        configuration.getCursor( viewer ).setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            configuration.dispose();

            if ( quickFilterWidget != null )
            {
                quickFilterWidget.dispose();
                quickFilterWidget = null;
            }

            table = null;
            viewer = null;
        }

        super.dispose();
    }


    /**
     * Gets the viewer.
     * 
     * @return the viewer
     */
    public TableViewer getViewer()
    {
        return viewer;
    }


    /**
     * Gets the quick filter widget.
     * 
     * @return the quick filter widget
     */
    public SearchResultEditorQuickFilterWidget getQuickFilterWidget()
    {
        return quickFilterWidget;
    }

}
