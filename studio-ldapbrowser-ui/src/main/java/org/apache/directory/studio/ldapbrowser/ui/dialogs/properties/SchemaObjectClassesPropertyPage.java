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

package org.apache.directory.studio.ldapbrowser.ui.dialogs.properties;


import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class SchemaObjectClassesPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    public SchemaObjectClassesPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    protected Control createContents( Composite parent )
    {

        Table table = new Table( parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( GridData.FILL_BOTH );
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        table.setLayoutData( gridData );
        table.setHeaderVisible( true );
        table.setLinesVisible( true );
        TableViewer viewer = new TableViewer( table );
        TableColumn column = new TableColumn( table, SWT.LEFT, 0 );
        column.setText( "Object Class Definition" );
        column.setWidth( 200 );
        column.setResizable( true );
        viewer.setColumnProperties( new String[]
            { "Object Class Definition" } );

        viewer.setSorter( new ViewerSorter() );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new LabelProvider() );

        if ( getElement() instanceof IConnection )
        {
            IConnection connection = ( IConnection ) getElement();
            if ( connection != null )
            {
                Object[] ocds = connection.getSchema().getObjectClassDescriptions();
                viewer.setInput( ocds );
                column.pack();
            }
        }
        else if ( getElement() instanceof IEntry )
        {
            IEntry entry = ( IEntry ) getElement();
            if ( entry != null )
            {
                Object[] ocds = entry.getSubschema().getObjectClassNames();
                viewer.setInput( ocds );
                column.pack();
            }
        }

        return parent;
    }

}
