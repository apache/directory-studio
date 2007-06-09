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


import org.apache.directory.studio.ldapbrowser.common.widgets.ViewFormWidget;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;


public class SearchResultEditorWidget extends ViewFormWidget
{

    private SearchResultEditorConfiguration configuration;

    private SearchResultEditorQuickFilterWidget quickFilterWidget;

    private Table table;

    private TableViewer viewer;


    public SearchResultEditorWidget( SearchResultEditorConfiguration configuration )
    {
        this.configuration = configuration;
    }


    protected Control createContent( Composite parent )
    {

        // create quick filter
        this.quickFilterWidget = new SearchResultEditorQuickFilterWidget( this.configuration.getFilter() );
        this.quickFilterWidget.createComposite( parent );

        // create table widget and viewer
        this.table = new Table( parent, SWT.BORDER | SWT.HIDE_SELECTION | SWT.VIRTUAL );
        this.table.setHeaderVisible( true );
        this.table.setLinesVisible( true );
        this.table.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        this.viewer = new TableViewer( this.table );
        this.viewer.setUseHashlookup( true );

        // setup providers
        this.viewer.setContentProvider( configuration.getContentProvider( this ) );
        this.viewer.setLabelProvider( configuration.getLabelProvider( this.viewer ) );

        // set table cell editors
        this.viewer.setCellModifier( configuration.getCellModifier( this.viewer ) );

        return this.table;
    }


    public void setInput( Object input )
    {
        this.viewer.setInput( input );
    }


    public void setFocus()
    {
        this.configuration.getCursor( this.viewer ).setFocus();
    }


    public void dispose()
    {
        if ( this.viewer != null )
        {
            this.configuration.dispose();

            if ( this.quickFilterWidget != null )
            {
                this.quickFilterWidget.dispose();
                this.quickFilterWidget = null;
            }

            // this.table.dispose();
            this.table = null;
            this.viewer = null;
        }

        super.dispose();
    }


    public TableViewer getViewer()
    {
        return viewer;
    }


    public SearchResultEditorQuickFilterWidget getQuickFilterWidget()
    {
        return quickFilterWidget;
    }

}
