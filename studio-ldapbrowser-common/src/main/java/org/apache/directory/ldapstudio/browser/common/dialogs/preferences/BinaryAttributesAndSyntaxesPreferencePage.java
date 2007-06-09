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

package org.apache.directory.ldapstudio.browser.common.dialogs.preferences;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.core.ConnectionManager;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.BinaryAttribute;
import org.apache.directory.ldapstudio.browser.core.model.schema.BinarySyntax;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class BinaryAttributesAndSyntaxesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private SortedMap attributeOid2AtdMap;

    private SortedMap attributeNames2AtdMap;

    private String[] attributeNamesAndOids;

    private SortedMap syntaxOid2LsdMap;

    private SortedMap syntaxDesc2LsdMap;

    private String[] syntaxOids;

    private List attributeList;

    private TableViewer attributeViewer;

    private Button attributeAddButton;

    private Button attributeEditButton;

    private Button attributeRemoveButton;

    private List syntaxList;

    private TableViewer syntaxViewer;

    private Button syntaxAddButton;

    private Button syntaxEditButton;

    private Button syntaxRemoveButton;


    public BinaryAttributesAndSyntaxesPreferencePage()
    {
        super();
        super.setDescription( "Specify attributes to handle as binary:" );
    }


    public void init( IWorkbench workbench )
    {
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // init available attribute types
        this.attributeNames2AtdMap = new TreeMap();
        this.attributeOid2AtdMap = new TreeMap();
        ConnectionManager cm = BrowserCorePlugin.getDefault().getConnectionManager();
        IConnection[] connections = cm.getConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            Schema schema = connections[i].getSchema();
            if ( schema != null )
            {
                createAttributeMaps( schema );
            }
        }
        createAttributeMaps( Schema.DEFAULT_SCHEMA );
        this.attributeNamesAndOids = new String[this.attributeNames2AtdMap.size() + this.attributeOid2AtdMap.size()];
        System.arraycopy( this.attributeNames2AtdMap.keySet().toArray(), 0, this.attributeNamesAndOids, 0,
            this.attributeNames2AtdMap.size() );
        System.arraycopy( this.attributeOid2AtdMap.keySet().toArray(), 0, this.attributeNamesAndOids,
            this.attributeNames2AtdMap.size(), this.attributeOid2AtdMap.size() );

        // init available syntaxes
        this.syntaxOid2LsdMap = new TreeMap();
        this.syntaxDesc2LsdMap = new TreeMap();
        for ( int i = 0; i < connections.length; i++ )
        {
            Schema schema = connections[i].getSchema();
            if ( schema != null )
            {
                createSyntaxMaps( schema );
            }
        }
        createSyntaxMaps( Schema.DEFAULT_SCHEMA );
        this.syntaxOids = new String[this.syntaxOid2LsdMap.size()];
        System
            .arraycopy( this.syntaxOid2LsdMap.keySet().toArray(), 0, this.syntaxOids, 0, this.syntaxOid2LsdMap.size() );

        // create attribute contents
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        createAttributeContents( composite );
        attributeList = new ArrayList( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getBinaryAttributes() ) );
        attributeViewer.setInput( this.attributeList );
        attributeViewer.getTable().getColumn( 0 ).pack();
        // attributeViewer.getTable().getColumn(1).pack();
        attributeViewer.getTable().pack();

        // create syntax contents
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        createSyntaxContents( composite );
        syntaxList = new ArrayList( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getBinarySyntaxes() ) );
        syntaxViewer.setInput( this.syntaxList );
        syntaxViewer.getTable().getColumn( 0 ).pack();
        // syntaxViewer.getTable().getColumn(1).pack();
        syntaxViewer.getTable().pack();

        return composite;
    }


    private void createAttributeMaps( Schema schema )
    {
        AttributeTypeDescription[] atds = schema.getAttributeTypeDescriptions();
        for ( int i = 0; i < atds.length; i++ )
        {

            attributeOid2AtdMap.put( atds[i].getNumericOID(), atds[i] );

            String[] names = atds[i].getNames();
            for ( int j = 0; j < names.length; j++ )
            {
                attributeNames2AtdMap.put( names[j], atds[i] );
            }

        }
    }


    private void createSyntaxMaps( Schema schema )
    {
        LdapSyntaxDescription[] lsds = schema.getLdapSyntaxDescriptions();
        for ( int i = 0; i < lsds.length; i++ )
        {

            syntaxOid2LsdMap.put( lsds[i].getNumericOID(), lsds[i] );

            if ( lsds[i].getDesc() != null )
            {
                syntaxDesc2LsdMap.put( lsds[i].getDesc(), lsds[i] );
            }

        }
    }


    private void createAttributeContents( Composite parent )
    {

        BaseWidgetUtils.createLabel( parent, "Binary Attributes", 1 );

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        Composite listComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        listComposite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        Composite buttonComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        buttonComposite.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );

        Table table = new Table( listComposite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 360;
        data.heightHint = convertHeightInCharsToPixels( 10 );
        table.setLayoutData( data );
        table.setHeaderVisible( true );
        table.setLinesVisible( true );
        attributeViewer = new TableViewer( table );

        TableColumn c1 = new TableColumn( table, SWT.NONE );
        c1.setText( "Attribute" );
        c1.setWidth( 300 );
        TableColumn c2 = new TableColumn( table, SWT.NONE );
        c2.setText( "Alias" );
        c2.setWidth( 60 );

        attributeViewer.setColumnProperties( new String[]
            { "Attribute" } );
        attributeViewer.setContentProvider( new ArrayContentProvider() );
        attributeViewer.setLabelProvider( new AttributeLabelProvider() );

        attributeViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editAttribute();
            }
        } );

        attributeAddButton = BaseWidgetUtils.createButton( buttonComposite, "Add...", 1 );
        attributeAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addAttribute();
            }
        } );
        attributeEditButton = BaseWidgetUtils.createButton( buttonComposite, "Edit...", 1 );
        attributeEditButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editAttribute();
            }
        } );
        attributeRemoveButton = BaseWidgetUtils.createButton( buttonComposite, "Remove", 1 );
        attributeRemoveButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                removeAttribute();
            }
        } );

        // c1.pack();
        // c2.pack();
        // table.pack();
    }


    private void createSyntaxContents( Composite parent )
    {

        BaseWidgetUtils.createLabel( parent, "Binary Syntaxes", 1 );

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        Composite listComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        listComposite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        Composite buttonComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        buttonComposite.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );

        Table table = new Table( listComposite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 360;
        data.heightHint = convertHeightInCharsToPixels( 10 );
        table.setLayoutData( data );
        table.setHeaderVisible( true );
        table.setLinesVisible( true );
        syntaxViewer = new TableViewer( table );

        TableColumn c1 = new TableColumn( table, SWT.NONE );
        c1.setText( "Syntax" );
        c1.setWidth( 300 );
        TableColumn c2 = new TableColumn( table, SWT.NONE );
        c2.setText( "Desc" );
        c2.setWidth( 60 );

        syntaxViewer.setColumnProperties( new String[]
            { "Syntax" } );
        syntaxViewer.setContentProvider( new ArrayContentProvider() );
        syntaxViewer.setLabelProvider( new SyntaxLabelProvider() );

        syntaxViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                editSyntax();
            }
        } );

        syntaxAddButton = BaseWidgetUtils.createButton( buttonComposite, "Add...", 1 );
        syntaxAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addSyntax();
            }
        } );
        syntaxEditButton = BaseWidgetUtils.createButton( buttonComposite, "Edit...", 1 );
        syntaxEditButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                editSyntax();
            }
        } );
        syntaxRemoveButton = BaseWidgetUtils.createButton( buttonComposite, "Remove", 1 );
        syntaxRemoveButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                removeSyntax();
            }
        } );

        // c1.pack();
        // c2.pack();
        // table.pack();
    }


    protected void addAttribute()
    {
        AttributeDialog dialog = new AttributeDialog( getShell(), null, this.attributeNamesAndOids );
        if ( dialog.open() == AttributeValueEditorDialog.OK )
        {
            this.attributeList.add( dialog.getAttribute() );
            this.attributeViewer.refresh();
        }
    }


    protected void removeAttribute()
    {
        Object o = ( ( StructuredSelection ) this.attributeViewer.getSelection() ).getFirstElement();
        this.attributeList.remove( o );
        this.attributeViewer.refresh();
    }


    protected void editAttribute()
    {
        StructuredSelection sel = ( StructuredSelection ) this.attributeViewer.getSelection();
        if ( !sel.isEmpty() )
        {
            BinaryAttribute attribute = ( BinaryAttribute ) sel.getFirstElement();
            AttributeDialog dialog = new AttributeDialog( getShell(), attribute, this.attributeNamesAndOids );
            if ( dialog.open() == AttributeValueEditorDialog.OK )
            {
                int index = this.attributeList.indexOf( attribute );
                this.attributeList.set( index, dialog.getAttribute() );
                this.attributeViewer.refresh();
            }
        }
    }


    protected void addSyntax()
    {
        SyntaxDialog dialog = new SyntaxDialog( getShell(), null, this.syntaxOids );
        if ( dialog.open() == SyntaxValueEditorDialog.OK )
        {
            this.syntaxList.add( dialog.getSyntax() );
            this.syntaxViewer.refresh();
        }
    }


    protected void removeSyntax()
    {
        Object o = ( ( StructuredSelection ) this.syntaxViewer.getSelection() ).getFirstElement();
        this.syntaxList.remove( o );
        this.syntaxViewer.refresh();
    }


    protected void editSyntax()
    {
        StructuredSelection sel = ( StructuredSelection ) this.syntaxViewer.getSelection();
        if ( !sel.isEmpty() )
        {
            BinarySyntax syntax = ( BinarySyntax ) sel.getFirstElement();
            SyntaxDialog dialog = new SyntaxDialog( getShell(), syntax, this.syntaxOids );
            if ( dialog.open() == SyntaxValueEditorDialog.OK )
            {
                int index = this.syntaxList.indexOf( syntax );
                this.syntaxList.set( index, dialog.getSyntax() );
                this.syntaxViewer.refresh();
            }
        }
    }


    public boolean performOk()
    {
        BinaryAttribute[] attributes = ( BinaryAttribute[] ) this.attributeList
            .toArray( new BinaryAttribute[this.attributeList.size()] );
        BrowserCorePlugin.getDefault().getCorePreferences().setBinaryAttributes( attributes );

        BinarySyntax[] syntaxes = ( BinarySyntax[] ) this.syntaxList.toArray( new BinarySyntax[this.syntaxList.size()] );
        BrowserCorePlugin.getDefault().getCorePreferences().setBinarySyntaxes( syntaxes );

        return true;
    }


    protected void performDefaults()
    {
        this.attributeList.clear();
        this.attributeList.addAll( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getDefaultBinaryAttributes() ) );
        this.attributeViewer.refresh();

        this.syntaxList.clear();
        this.syntaxList.addAll( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getDefaultBinarySyntaxes() ) );
        this.syntaxViewer.refresh();

        super.performDefaults();
    }

    class AttributeLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText( Object obj, int index )
        {
            if ( obj instanceof BinaryAttribute )
            {
                BinaryAttribute attribute = ( BinaryAttribute ) obj;
                if ( index == 0 )
                {
                    return attribute.getAttributeNumericOidOrName();
                }
                else if ( index == 1 )
                {
                    if ( attribute.getAttributeNumericOidOrName() != null )
                    {
                        if ( attributeNames2AtdMap.containsKey( attribute.getAttributeNumericOidOrName() ) )
                        {
                            AttributeTypeDescription atd = ( AttributeTypeDescription ) attributeNames2AtdMap
                                .get( attribute.getAttributeNumericOidOrName() );
                            String s = atd.getNumericOID();
                            for ( int i = 0; i < atd.getNames().length; i++ )
                            {
                                if ( !attribute.getAttributeNumericOidOrName().equals( atd.getNames()[i] ) )
                                {
                                    s += ", " + atd.getNames()[i];
                                }
                            }
                            return s;
                        }
                        else if ( attributeOid2AtdMap.containsKey( attribute.getAttributeNumericOidOrName() ) )
                        {
                            AttributeTypeDescription atd = ( AttributeTypeDescription ) attributeOid2AtdMap
                                .get( attribute.getAttributeNumericOidOrName() );
                            return atd.toString();
                        }
                    }
                }
            }
            return null;
        }


        public Image getColumnImage( Object obj, int index )
        {
            return null;
        }
    }

    class SyntaxLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText( Object obj, int index )
        {
            if ( obj instanceof BinarySyntax )
            {
                BinarySyntax syntax = ( BinarySyntax ) obj;
                if ( index == 0 )
                {
                    return syntax.getSyntaxNumericOid();
                }
                else if ( index == 1 )
                {
                    if ( syntax.getSyntaxNumericOid() != null )
                    {
                        if ( syntaxOid2LsdMap.containsKey( syntax.getSyntaxNumericOid() ) )
                        {
                            LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) syntaxOid2LsdMap.get( syntax
                                .getSyntaxNumericOid() );
                            return lsd.toString();
                        }
                    }
                }
            }
            return null;
        }


        public Image getColumnImage( Object obj, int index )
        {
            return null;
        }
    }
}
