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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.studio.ldapbrowser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SyntaxValueProviderRelation;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.apache.directory.studio.valueeditors.ValueEditorManager.ValueEditorExtension;
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


public class ValueEditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private SortedMap<String, ValueEditorExtension> class2ValueEditorProxyMap;

    private SortedMap attributeOid2AtdMap;

    private SortedMap attributeTypes2AtdMap;

    private String[] attributeTypesAndOids;

    private SortedMap syntaxOid2LsdMap;

    private SortedMap syntaxDesc2LsdMap;

    private String[] syntaxDescsAndOids;

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


    public ValueEditorsPreferencePage()
    {
        super();
        super.setDescription( "Specify value editors:" );
    }


    public void init( IWorkbench workbench )
    {
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // init available value providers
        this.class2ValueEditorProxyMap = new TreeMap<String, ValueEditorExtension>();
        Composite dummyComposite = new Composite( composite, SWT.NONE );
        dummyComposite.setLayoutData( new GridData( 1, 1 ) );
        
        Collection<ValueEditorExtension> valueEditorProxys = ValueEditorManager.getValueEditorProxys();
        for ( ValueEditorExtension proxy : valueEditorProxys )
        {
            this.class2ValueEditorProxyMap.put( proxy.className, proxy );
        }

        // init available attribute types
        this.attributeTypes2AtdMap = new TreeMap();
        this.attributeOid2AtdMap = new TreeMap();
        BrowserConnectionManager cm = BrowserCorePlugin.getDefault().getConnectionManager();
        IBrowserConnection[] connections = cm.getBrowserConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            Schema schema = connections[i].getSchema();
            if ( schema != null )
            {
                createAttributeMaps( schema );
            }
        }
        createAttributeMaps( Schema.DEFAULT_SCHEMA );
        this.attributeTypesAndOids = new String[this.attributeTypes2AtdMap.size() + this.attributeOid2AtdMap.size()];
        System.arraycopy( this.attributeTypes2AtdMap.keySet().toArray(), 0, this.attributeTypesAndOids, 0,
            this.attributeTypes2AtdMap.size() );
        System.arraycopy( this.attributeOid2AtdMap.keySet().toArray(), 0, this.attributeTypesAndOids,
            this.attributeTypes2AtdMap.size(), this.attributeOid2AtdMap.size() );

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
        this.syntaxDescsAndOids = new String[this.syntaxOid2LsdMap.size()];
        System.arraycopy( this.syntaxOid2LsdMap.keySet().toArray(), 0, this.syntaxDescsAndOids, 0,
            this.syntaxOid2LsdMap.size() );

        // create attribute contents
        // BaseWidgetUtils.createSpacer(composite, 1);
        BaseWidgetUtils.createSpacer( composite, 1 );
        this.createAttributeContents( composite );
        this.attributeList = new ArrayList( Arrays.asList( BrowserCommonActivator.getDefault().getValueEditorsPreferences()
            .getAttributeValueProviderRelations() ) );
        attributeViewer.setInput( this.attributeList );
        attributeViewer.getTable().getColumn( 0 ).pack();
        // attributeViewer.getTable().getColumn(1).pack();
        attributeViewer.getTable().getColumn( 2 ).pack();
        attributeViewer.getTable().pack();

        // create syntax contents
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        this.createSyntaxContents( composite );
        this.syntaxList = new ArrayList( Arrays.asList( BrowserCommonActivator.getDefault().getValueEditorsPreferences()
            .getSyntaxValueProviderRelations() ) );
        syntaxViewer.setInput( this.syntaxList );
        syntaxViewer.getTable().getColumn( 0 ).pack();
        // syntaxViewer.getTable().getColumn(1).pack();
        syntaxViewer.getTable().getColumn( 2 ).pack();
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
                attributeTypes2AtdMap.put( names[j], atds[i] );
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

        BaseWidgetUtils.createLabel( parent, "Value Editors by Attribute Types", 1 );

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
        c1.setWidth( 80 );
        TableColumn c2 = new TableColumn( table, SWT.NONE );
        c2.setText( "Alias" );
        c2.setWidth( 80 );
        TableColumn c3 = new TableColumn( table, SWT.NONE );
        c3.setText( "Value Editor" );
        c3.setWidth( 200 );

        attributeViewer.setColumnProperties( new String[]
            { "Attribute", "Value Editor" } );
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

        BaseWidgetUtils.createLabel( parent, "Value Editors by Syntax", 1 );

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
        c1.setWidth( 80 );
        TableColumn c2 = new TableColumn( table, SWT.NONE );
        c2.setText( "Desc" );
        c2.setWidth( 80 );
        TableColumn c3 = new TableColumn( table, SWT.NONE );
        c3.setText( "Value Editor" );
        c3.setWidth( 200 );

        syntaxViewer.setColumnProperties( new String[]
            { "Syntax", "Value Editor" } );
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
        AttributeValueEditorDialog dialog = new AttributeValueEditorDialog( getShell(), null,
            this.class2ValueEditorProxyMap, this.attributeTypesAndOids );
        if ( dialog.open() == AttributeValueEditorDialog.OK )
        {
            this.attributeList.add( dialog.getRelation() );
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
            AttributeValueProviderRelation relation = ( AttributeValueProviderRelation ) sel.getFirstElement();
            AttributeValueEditorDialog dialog = new AttributeValueEditorDialog( getShell(), relation,
                this.class2ValueEditorProxyMap, this.attributeTypesAndOids );
            if ( dialog.open() == AttributeValueEditorDialog.OK )
            {
                int index = this.attributeList.indexOf( relation );
                this.attributeList.set( index, dialog.getRelation() );
                this.attributeViewer.refresh();
            }
        }
    }


    protected void addSyntax()
    {
        SyntaxValueEditorDialog dialog = new SyntaxValueEditorDialog( getShell(), null,
            this.class2ValueEditorProxyMap, this.syntaxDescsAndOids );
        if ( dialog.open() == SyntaxValueEditorDialog.OK )
        {
            this.syntaxList.add( dialog.getRelation() );
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
            SyntaxValueProviderRelation relation = ( SyntaxValueProviderRelation ) sel.getFirstElement();
            SyntaxValueEditorDialog dialog = new SyntaxValueEditorDialog( getShell(), relation,
                this.class2ValueEditorProxyMap, this.syntaxDescsAndOids );
            if ( dialog.open() == SyntaxValueEditorDialog.OK )
            {
                int index = this.syntaxList.indexOf( relation );
                this.syntaxList.set( index, dialog.getRelation() );
                this.syntaxViewer.refresh();
            }
        }
    }


    public boolean performOk()
    {
        AttributeValueProviderRelation[] aRelations = ( AttributeValueProviderRelation[] ) this.attributeList
            .toArray( new AttributeValueProviderRelation[this.attributeList.size()] );
        BrowserCommonActivator.getDefault().getValueEditorsPreferences().setAttributeValueProviderRelations( aRelations );

        SyntaxValueProviderRelation[] sRelations = ( SyntaxValueProviderRelation[] ) this.syntaxList
            .toArray( new SyntaxValueProviderRelation[this.syntaxList.size()] );
        BrowserCommonActivator.getDefault().getValueEditorsPreferences().setSyntaxValueProviderRelations( sRelations );

        return true;
    }


    protected void performDefaults()
    {
        this.attributeList.clear();
        this.attributeList.addAll( Arrays.asList( BrowserCommonActivator.getDefault().getValueEditorsPreferences()
            .getDefaultAttributeValueProviderRelations() ) );
        this.attributeViewer.refresh();

        this.syntaxList.clear();
        this.syntaxList.addAll( Arrays.asList( BrowserCommonActivator.getDefault().getValueEditorsPreferences()
            .getDefaultSyntaxValueProviderRelations() ) );
        this.syntaxViewer.refresh();

        super.performDefaults();
    }

    class AttributeLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText( Object obj, int index )
        {
            if ( obj instanceof AttributeValueProviderRelation )
            {
                AttributeValueProviderRelation relation = ( AttributeValueProviderRelation ) obj;
                if ( index == 0 )
                {
                    return relation.getAttributeNumericOidOrType();
                }
                else if ( index == 1 )
                {
                    if ( relation.getAttributeNumericOidOrType() != null )
                    {
                        if ( attributeTypes2AtdMap.containsKey( relation.getAttributeNumericOidOrType() ) )
                        {
                            AttributeTypeDescription atd = ( AttributeTypeDescription ) attributeTypes2AtdMap
                                .get( relation.getAttributeNumericOidOrType() );
                            String s = atd.getNumericOID();
                            for ( int i = 0; i < atd.getNames().length; i++ )
                            {
                                if ( !relation.getAttributeNumericOidOrType().equals( atd.getNames()[i] ) )
                                {
                                    s += ", " + atd.getNames()[i];
                                }
                            }
                            return s;
                        }
                        else if ( attributeOid2AtdMap.containsKey( relation.getAttributeNumericOidOrType() ) )
                        {
                            AttributeTypeDescription atd = ( AttributeTypeDescription ) attributeOid2AtdMap
                                .get( relation.getAttributeNumericOidOrType() );
                            return atd.toString();
                        }
                    }
                }
                else if ( index == 2 )
                {
                    ValueEditorExtension vp = class2ValueEditorProxyMap.get( relation.getValueProviderClassname() );
                    return vp != null ? vp.name : null;
                }
            }
            return null;
        }


        public Image getColumnImage( Object obj, int index )
        {
            if ( obj instanceof AttributeValueProviderRelation )
            {
                AttributeValueProviderRelation relation = ( AttributeValueProviderRelation ) obj;
                if ( index == 2 )
                {
                    ValueEditorExtension vp = class2ValueEditorProxyMap.get( relation.getValueProviderClassname() );
                    return vp != null ? vp.icon.createImage() : null;
                }
            }

            return null;
        }
    }

    class SyntaxLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText( Object obj, int index )
        {
            if ( obj instanceof SyntaxValueProviderRelation )
            {
                SyntaxValueProviderRelation relation = ( SyntaxValueProviderRelation ) obj;
                if ( index == 0 )
                {
                    return relation.getSyntaxOID();
                }
                else if ( index == 1 )
                {
                    if ( relation.getSyntaxOID() != null )
                    {
                        if ( syntaxOid2LsdMap.containsKey( relation.getSyntaxOID() ) )
                        {
                            LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) syntaxOid2LsdMap.get( relation
                                .getSyntaxOID() );
                            return lsd.toString();
                        }
                    }
                }
                else if ( index == 2 )
                {
                    ValueEditorExtension vp = class2ValueEditorProxyMap.get( relation.getValueProviderClassname() );
                    return vp != null ? vp.name : null;
                }
            }
            return null;
        }


        public Image getColumnImage( Object obj, int index )
        {
            if ( obj instanceof SyntaxValueProviderRelation )
            {
                SyntaxValueProviderRelation relation = ( SyntaxValueProviderRelation ) obj;
                if ( index == 2 )
                {
                    ValueEditorExtension vp = class2ValueEditorProxyMap.get( relation.getValueProviderClassname() );
                    return vp != null ? vp.icon.createImage() : null;
                }
            }

            return null;
        }
    }

}
