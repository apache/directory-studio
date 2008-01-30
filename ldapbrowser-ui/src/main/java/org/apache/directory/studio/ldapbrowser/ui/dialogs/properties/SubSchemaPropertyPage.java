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


import org.apache.directory.studio.ldapbrowser.core.model.IEntry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class SubSchemaPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    private TabFolder tabFolder;

    private TabItem ocTab;

    private TabItem atTab;


    public SubSchemaPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    protected Control createContents( Composite parent )
    {

        this.tabFolder = new TabFolder( parent, SWT.TOP );
        RowLayout mainLayout = new RowLayout();
        mainLayout.fill = true;
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        this.tabFolder.setLayout( mainLayout );

        Composite ocComposite = new Composite( this.tabFolder, SWT.NONE );
        ocComposite.setLayoutData( new RowData( 10, 10 ) );
        GridLayout ocLayout = new GridLayout();
        ocComposite.setLayout( ocLayout );
        ListViewer ocViewer = new ListViewer( ocComposite );
        ocViewer.getList().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        ocViewer.setContentProvider( new ArrayContentProvider() );
        ocViewer.setLabelProvider( new LabelProvider() );
        if ( EntryPropertyPage.getEntry( getElement() ) != null )
        {
            IEntry entry = EntryPropertyPage.getEntry( getElement() );
            if ( entry != null )
            {
                Object[] ocds = entry.getSubschema().getObjectClassNames();
                ocViewer.setInput( ocds );
            }
        }
        this.ocTab = new TabItem( this.tabFolder, SWT.NONE );
        this.ocTab.setText( "Object Classes" );
        this.ocTab.setControl( ocComposite );

        Composite atComposite = new Composite( this.tabFolder, SWT.NONE );
        atComposite.setLayoutData( new RowData( 10, 10 ) );
        GridLayout atLayout = new GridLayout();
        atComposite.setLayout( atLayout );
        ListViewer atViewer = new ListViewer( atComposite );
        atViewer.getList().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        atViewer.setContentProvider( new ArrayContentProvider() );
        atViewer.setLabelProvider( new LabelProvider() );
        if ( EntryPropertyPage.getEntry( getElement() ) != null )
        {
            IEntry entry = EntryPropertyPage.getEntry( getElement() );
            if ( entry != null )
            {
                Object[] atds = entry.getSubschema().getAllAttributeNames();
                atViewer.setInput( atds );
            }
        }
        this.atTab = new TabItem( this.tabFolder, SWT.NONE );
        this.atTab.setText( "Attribute Types" );
        this.atTab.setControl( atComposite );

        return this.tabFolder;
    }

}
