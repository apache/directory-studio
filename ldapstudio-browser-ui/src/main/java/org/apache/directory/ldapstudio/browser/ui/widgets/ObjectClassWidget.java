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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class ObjectClassWidget extends BrowserWidget
{

    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;

    private final static int SIZING_SELECTION_WIDGET_WIDTH = 400;

    private IConnection connection;

    private String[] initialObjectClasses;

    private List availableObjectClasses;

    private ListViewer availableObjectClassesViewer;

    private List selectedObjectClasses;

    private ListViewer selectedObjectClassesViewer;

    private Button addButton;

    private Button removeButton;


    public ObjectClassWidget( IConnection connection )
    {
        this( connection, ( IEntry ) null );
    }


    public ObjectClassWidget( IConnection connection, String[] ocValues )
    {
        this.connection = connection;
        this.initialObjectClasses = ocValues;

        if ( this.connection != null )
        {
            this.availableObjectClasses = new ArrayList( Arrays.asList( this.connection.getSchema()
                .getObjectClassDescriptionNames() ) );
            this.selectedObjectClasses = new ArrayList();
        }
        else
        {
            this.availableObjectClasses = new ArrayList();
            this.selectedObjectClasses = new ArrayList();
        }

        if ( this.initialObjectClasses != null )
        {
            selectedObjectClasses.addAll( Arrays.asList( this.initialObjectClasses ) );
        }
    }


    public ObjectClassWidget( IConnection connection, IEntry entry )
    {
        this.connection = connection;
        this.initialObjectClasses = new String[0];

        if ( this.connection != null )
        {
            this.availableObjectClasses = new ArrayList( Arrays.asList( this.connection.getSchema()
                .getObjectClassDescriptionNames() ) );
            this.selectedObjectClasses = new ArrayList();
        }
        else
        {
            this.availableObjectClasses = new ArrayList();
            this.selectedObjectClasses = new ArrayList();
        }

        if ( entry != null )
        {
            IAttribute oca = entry.getAttribute( IAttribute.OBJECTCLASS_ATTRIBUTE );
            if ( oca != null )
            {
                selectedObjectClasses.addAll( Arrays.asList( oca.getStringValues() ) );
            }
        }
    }


    public void dispose()
    {

    }


    public String[] getSelectedObjectClassNames()
    {
        return ( String[] ) selectedObjectClasses.toArray( new String[selectedObjectClasses.size()] );
    }


    public Composite createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        Label availableLabel = new Label( composite, SWT.NONE );
        availableLabel.setText( "Available object classes" );
        Label buttonLabel = new Label( composite, SWT.NONE );
        buttonLabel.setText( "" );
        Label selectedLabel = new Label( composite, SWT.NONE );
        selectedLabel.setText( "Selected object classes" );

        availableObjectClassesViewer = new ListViewer( composite );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        availableObjectClassesViewer.getList().setLayoutData( data );
        availableObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        availableObjectClassesViewer.setLabelProvider( new LabelProvider() );
        availableObjectClassesViewer.setSorter( new ViewerSorter() );
        availableObjectClassesViewer.setInput( availableObjectClasses );
        availableObjectClassesViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                add( event.getSelection() );
            }
        } );

        Composite buttonComposite = new Composite( composite, SWT.NONE );
        GridLayout gl = new GridLayout( 1, true );
        buttonComposite.setLayout( gl );
        data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        // data.widthHint = (int)(SIZING_SELECTION_WIDGET_WIDTH * 0.2);
        data.horizontalAlignment = SWT.CENTER;
        buttonComposite.setLayoutData( data );
        Label label0 = new Label( buttonComposite, SWT.NONE );
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        label0.setLayoutData( data );
        addButton = BaseWidgetUtils.createButton( buttonComposite, "&Add", 1 );
        removeButton = BaseWidgetUtils.createButton( buttonComposite, "&Remove", 1 );
        Label label3 = new Label( buttonComposite, SWT.NONE );
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        label3.setLayoutData( data );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                add( availableObjectClassesViewer.getSelection() );
            }
        } );

        removeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                remove( selectedObjectClassesViewer.getSelection() );
            }
        } );

        selectedObjectClassesViewer = new ListViewer( composite );
        data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        selectedObjectClassesViewer.getList().setLayoutData( data );
        selectedObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        selectedObjectClassesViewer.setLabelProvider( new LabelProvider() );
        selectedObjectClassesViewer.setSorter( new ViewerSorter() );
        selectedObjectClassesViewer.setInput( selectedObjectClasses );
        selectedObjectClassesViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                remove( event.getSelection() );
            }
        } );

        return composite;
    }


    private void add( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Iterator it = selection.iterator();
        while ( it.hasNext() )
        {
            Object oc = it.next();
            availableObjectClasses.remove( oc );
            selectedObjectClasses.add( oc );
            availableObjectClassesViewer.refresh();
            selectedObjectClassesViewer.refresh();
        }
    }


    private void remove( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Iterator it = selection.iterator();
        while ( it.hasNext() )
        {
            Object oc = it.next();
            selectedObjectClasses.remove( oc );
            availableObjectClasses.add( oc );
            availableObjectClassesViewer.refresh();
            selectedObjectClassesViewer.refresh();
        }
    }

}
