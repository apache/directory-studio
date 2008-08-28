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

import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.syntax.LdapSyntaxDescription;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinaryAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinarySyntax;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
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


/**
 * The BinaryAttributesAndSyntaxesPreferencePage is used to specify
 * binary attributes and syntaxes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BinaryAttributesAndSyntaxesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    /** Map with attribute OID => attribute type description */
    private SortedMap<String, AttributeTypeDescription> attributeOid2AtdMap;

    /** Map with attribute name => attribute type description */
    private SortedMap<String, AttributeTypeDescription> attributeNames2AtdMap;

    /** The attribute names and OIDs. */
    private String[] attributeNamesAndOids;

    /** Map with syntax OID => syntax description */
    private SortedMap<String, LdapSyntaxDescription> syntaxOid2LsdMap;

    /** Map with syntax DESC => syntax description */
    private SortedMap<String, LdapSyntaxDescription> syntaxDesc2LsdMap;

    /** The syntax OIDs. */
    private String[] syntaxOids;

    /** The attribute list. */
    private List<BinaryAttribute> attributeList;

    /** The attribute viewer. */
    private TableViewer attributeViewer;

    /** The attribute add button. */
    private Button attributeAddButton;

    /** The attribute edit button. */
    private Button attributeEditButton;

    /** The attribute remove button. */
    private Button attributeRemoveButton;

    /** The syntax list. */
    private List<BinarySyntax> syntaxList;

    /** The syntax viewer. */
    private TableViewer syntaxViewer;

    /** The syntax add button. */
    private Button syntaxAddButton;

    /** The syntax edit button. */
    private Button syntaxEditButton;

    /** The syntax remove button. */
    private Button syntaxRemoveButton;


    /**
     * Creates a new instance of BinaryAttributesAndSyntaxesPreferencePage.
     */
    public BinaryAttributesAndSyntaxesPreferencePage()
    {
        super( "Binary Attributes" );
        super.setDescription( "Specify attributes and syntaxes to handle as binary:" );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // init available attribute types
        attributeNames2AtdMap = new TreeMap<String, AttributeTypeDescription>();
        attributeOid2AtdMap = new TreeMap<String, AttributeTypeDescription>();
        BrowserConnectionManager cm = BrowserCorePlugin.getDefault().getConnectionManager();
        IBrowserConnection[] connections = cm.getBrowserConnections();
        for ( IBrowserConnection browserConnection : connections )
        {
            Schema schema = browserConnection.getSchema();
            createAttributeMaps( schema );
        }
        createAttributeMaps( Schema.DEFAULT_SCHEMA );
        attributeNamesAndOids = new String[attributeNames2AtdMap.size() + attributeOid2AtdMap.size()];
        System.arraycopy( attributeNames2AtdMap.keySet().toArray(), 0, attributeNamesAndOids, 0, attributeNames2AtdMap
            .size() );
        System.arraycopy( attributeOid2AtdMap.keySet().toArray(), 0, attributeNamesAndOids, attributeNames2AtdMap
            .size(), attributeOid2AtdMap.size() );

        // init available syntaxes
        syntaxOid2LsdMap = new TreeMap<String, LdapSyntaxDescription>();
        syntaxDesc2LsdMap = new TreeMap<String, LdapSyntaxDescription>();
        for ( IBrowserConnection browserConnection : connections )
        {
            Schema schema = browserConnection.getSchema();
            createSyntaxMaps( schema );
        }
        createSyntaxMaps( Schema.DEFAULT_SCHEMA );
        syntaxOids = new String[syntaxOid2LsdMap.size()];
        System.arraycopy( syntaxOid2LsdMap.keySet().toArray(), 0, syntaxOids, 0, syntaxOid2LsdMap.size() );

        // create attribute contents
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        createAttributeContents( composite );
        attributeList = new ArrayList<BinaryAttribute>( Arrays.asList( BrowserCorePlugin.getDefault()
            .getCorePreferences().getBinaryAttributes() ) );
        attributeViewer.setInput( attributeList );
        attributeViewer.getTable().getColumn( 0 ).pack();

        // create syntax contents
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        createSyntaxContents( composite );
        syntaxList = new ArrayList<BinarySyntax>( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getBinarySyntaxes() ) );
        syntaxViewer.setInput( syntaxList );
        syntaxViewer.getTable().getColumn( 0 ).pack();
        syntaxViewer.getTable().pack();

        return composite;
    }


    private void createAttributeMaps( Schema schema )
    {
        Collection<AttributeTypeDescription> atds = schema.getAttributeTypeDescriptions();
        for ( AttributeTypeDescription atd : atds )
        {
            attributeOid2AtdMap.put( atd.getNumericOid(), atd );
            for ( String name : atd.getNames() )
            {
                attributeNames2AtdMap.put( name, atd );
            }
        }
    }


    private void createSyntaxMaps( Schema schema )
    {
        Collection<LdapSyntaxDescription> lsds = schema.getLdapSyntaxDescriptions();
        for ( LdapSyntaxDescription lsd : lsds )
        {
            syntaxOid2LsdMap.put( lsd.getNumericOid(), lsd );

            if ( lsd.getDescription() != null )
            {
                syntaxDesc2LsdMap.put( lsd.getDescription(), lsd );
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
    }


    private void addAttribute()
    {
        AttributeDialog dialog = new AttributeDialog( getShell(), null, attributeNamesAndOids );
        if ( dialog.open() == AttributeValueEditorDialog.OK )
        {
            attributeList.add( dialog.getAttribute() );
            attributeViewer.refresh();
        }
    }


    private void removeAttribute()
    {
        Object o = ( ( StructuredSelection ) attributeViewer.getSelection() ).getFirstElement();
        attributeList.remove( o );
        attributeViewer.refresh();
    }


    private void editAttribute()
    {
        StructuredSelection sel = ( StructuredSelection ) attributeViewer.getSelection();
        if ( !sel.isEmpty() )
        {
            BinaryAttribute attribute = ( BinaryAttribute ) sel.getFirstElement();
            AttributeDialog dialog = new AttributeDialog( getShell(), attribute, attributeNamesAndOids );
            if ( dialog.open() == AttributeValueEditorDialog.OK )
            {
                int index = attributeList.indexOf( attribute );
                attributeList.set( index, dialog.getAttribute() );
                attributeViewer.refresh();
            }
        }
    }


    private void addSyntax()
    {
        SyntaxDialog dialog = new SyntaxDialog( getShell(), null, syntaxOids );
        if ( dialog.open() == SyntaxValueEditorDialog.OK )
        {
            syntaxList.add( dialog.getSyntax() );
            syntaxViewer.refresh();
        }
    }


    private void removeSyntax()
    {
        Object o = ( ( StructuredSelection ) syntaxViewer.getSelection() ).getFirstElement();
        syntaxList.remove( o );
        syntaxViewer.refresh();
    }


    private void editSyntax()
    {
        StructuredSelection sel = ( StructuredSelection ) syntaxViewer.getSelection();
        if ( !sel.isEmpty() )
        {
            BinarySyntax syntax = ( BinarySyntax ) sel.getFirstElement();
            SyntaxDialog dialog = new SyntaxDialog( getShell(), syntax, syntaxOids );
            if ( dialog.open() == SyntaxValueEditorDialog.OK )
            {
                int index = syntaxList.indexOf( syntax );
                syntaxList.set( index, dialog.getSyntax() );
                syntaxViewer.refresh();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        BinaryAttribute[] attributes = attributeList.toArray( new BinaryAttribute[attributeList.size()] );
        BrowserCorePlugin.getDefault().getCorePreferences().setBinaryAttributes( attributes );

        BinarySyntax[] syntaxes = syntaxList.toArray( new BinarySyntax[syntaxList.size()] );
        BrowserCorePlugin.getDefault().getCorePreferences().setBinarySyntaxes( syntaxes );

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        attributeList.clear();
        attributeList.addAll( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getDefaultBinaryAttributes() ) );
        attributeViewer.refresh();

        syntaxList.clear();
        syntaxList.addAll( Arrays.asList( BrowserCorePlugin.getDefault().getCorePreferences()
            .getDefaultBinarySyntaxes() ) );
        syntaxViewer.refresh();

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
                            String s = atd.getNumericOid();
                            for ( String attributeName : atd.getNames() )
                            {
                                if ( !attribute.getAttributeNumericOidOrName().equals( attributeName ) )
                                {
                                    s += ", " + attributeName;
                                }
                            }
                            return s;
                        }
                        else if ( attributeOid2AtdMap.containsKey( attribute.getAttributeNumericOidOrName() ) )
                        {
                            AttributeTypeDescription atd = ( AttributeTypeDescription ) attributeOid2AtdMap
                                .get( attribute.getAttributeNumericOidOrName() );
                            return SchemaUtils.toString( atd );
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
                            return SchemaUtils.toString( lsd );
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
