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

package org.apache.directory.ldapstudio.browser.ui.dialogs.properties;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.entryeditor.EntryEditorWidgetTableMetadata;
import org.apache.directory.ldapstudio.browser.core.internal.model.RootDSE;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class RootDSEPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    private TabFolder tabFolder;

    private TabItem commonsTab;

    private TabItem controlsTab;

    private TabItem extensionsTab;

    private TabItem featuresTab;

    private TabItem rawTab;


    public RootDSEPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    protected Control createContents( Composite parent )
    {

        final IConnection connection = ConnectionPropertyPage.getConnection( getElement() );

        this.tabFolder = new TabFolder( parent, SWT.TOP );
        RowLayout mainLayout = new RowLayout();
        mainLayout.fill = true;
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        this.tabFolder.setLayout( mainLayout );

        Composite composite = new Composite( this.tabFolder, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        composite.setLayout( gl );
        BaseWidgetUtils.createLabel( composite, "Directory Type:", 1 );
        Text typeText = BaseWidgetUtils.createLabeledText( composite, "-", 1 );
        if ( connection != null && connection.getRootDSE() != null )
        {

            boolean typeDetected = false;

            // check OpenLDAP
            IAttribute ocAttribute = connection.getRootDSE().getAttribute( "objectClass" );
            if ( ocAttribute != null )
            {
                for ( int i = 0; i < ocAttribute.getStringValues().length; i++ )
                {
                    if ( "OpenLDAProotDSE".equals( ocAttribute.getStringValues()[i] ) )
                    {
                        IAttribute ccAttribute = connection.getRootDSE().getAttribute( "configContext" );
                        if ( ccAttribute != null )
                        {
                            typeText.setText( "OpenLDAP 2.3" );
                            typeDetected = true;
                        }
                        if ( !typeDetected )
                        {
                            IAttribute scAttribute = connection.getRootDSE().getAttribute( "supportedControl" );
                            if ( scAttribute != null )
                            {
                                for ( int sci = 0; sci < scAttribute.getStringValues().length; sci++ )
                                {
                                    // if("1.2.840.113556.1.4.319".equals(scAttribute.getStringValues()[sci]))
                                    // {
                                    if ( "2.16.840.1.113730.3.4.18".equals( scAttribute.getStringValues()[sci] ) )
                                    {
                                        typeText.setText( "OpenLDAP 2.2" );
                                        typeDetected = true;
                                    }
                                }
                            }

                        }
                        if ( !typeDetected )
                        {
                            IAttribute seAttribute = connection.getRootDSE().getAttribute( "supportedExtension" );
                            if ( seAttribute != null )
                            {
                                for ( int sei = 0; sei < seAttribute.getStringValues().length; sei++ )
                                {
                                    if ( "1.3.6.1.4.1.4203.1.11.3".equals( seAttribute.getStringValues()[sei] ) )
                                    {
                                        typeText.setText( "OpenLDAP 2.1" );
                                        typeDetected = true;
                                    }
                                }
                            }
                        }
                        if ( !typeDetected )
                        {
                            IAttribute sfAttribute = connection.getRootDSE().getAttribute( "supportedFeatures" );
                            if ( sfAttribute != null )
                            {
                                for ( int sfi = 0; sfi < sfAttribute.getStringValues().length; sfi++ )
                                {
                                    if ( "1.3.6.1.4.1.4203.1.5.4".equals( sfAttribute.getStringValues()[sfi] ) )
                                    {
                                        typeText.setText( "OpenLDAP 2.0" );
                                        typeDetected = true;
                                    }
                                }
                            }
                        }
                        if ( !typeDetected )
                        {
                            typeText.setText( "OpenLDAP" );
                            typeDetected = true;
                        }
                    }
                }
            }

            // check Siemens DirX
            IAttribute ssseAttribute = connection.getRootDSE().getAttribute( "subSchemaSubentry" );
            if ( ssseAttribute != null )
            {
                for ( int i = 0; i < ssseAttribute.getStringValues().length; i++ )
                {
                    if ( "cn=LDAPGlobalSchemaSubentry".equals( ssseAttribute.getStringValues()[i] ) )
                    {
                        typeText.setText( "Siemens DirX" );
                    }
                }
            }

            // check active directory
            IAttribute rdncAttribute = connection.getRootDSE().getAttribute( "rootDomainNamingContext" );
            if ( rdncAttribute != null )
            {
                IAttribute ffAttribute = connection.getRootDSE().getAttribute( "forestFunctionality" );
                if ( ffAttribute != null )
                {
                    typeText.setText( "Microsoft Active Directory 2003" );
                }
                else
                {
                    typeText.setText( "Microsoft Active Directory 2000" );
                }
            }

            // check Novell eDirectory / Sun Directory Server / Netscape
            // Directory Server
            IAttribute vnAttribute = connection.getRootDSE().getAttribute( "vendorName" );
            IAttribute vvAttribute = connection.getRootDSE().getAttribute( "vendorVersion" );
            if ( vnAttribute != null && vnAttribute.getStringValues().length > 0 && vvAttribute != null
                && vvAttribute.getStringValues().length > 0 )
            {
                if ( vnAttribute.getStringValues()[0].indexOf( "Novell" ) > -1
                    || vvAttribute.getStringValues()[0].indexOf( "eDirectory" ) > -1 )
                {
                    typeText.setText( "Novell eDirectory" );
                }
                if ( vnAttribute.getStringValues()[0].indexOf( "Sun" ) > -1
                    || vvAttribute.getStringValues()[0].indexOf( "Sun" ) > -1 )
                {
                    typeText.setText( "Sun Directory Server" );
                }
                if ( vnAttribute.getStringValues()[0].indexOf( "Netscape" ) > -1
                    || vvAttribute.getStringValues()[0].indexOf( "Netscape" ) > -1 )
                {
                    typeText.setText( "Netscape Directory Server" );
                }
            }
        }
        addInfo( connection, composite, "vendorName", "Vendor Name:" );
        addInfo( connection, composite, "vendorVersion", "Vendor Version:" );
        addInfo( connection, composite, "supportedLDAPVersion", "Supported LDAP Versions:" );
        addInfo( connection, composite, "supportedSASLMechanisms", "Supported SASL Mechanisms:" );

        this.commonsTab = new TabItem( this.tabFolder, SWT.NONE );
        this.commonsTab.setText( "Info" );
        this.commonsTab.setControl( composite );

        // naming contexts
        // alt servers
        // schema DN
        // ldap version

        Composite controlsComposite = new Composite( this.tabFolder, SWT.NONE );
        controlsComposite.setLayoutData( new RowData( 10, 10 ) );
        GridLayout controlsLayout = new GridLayout();
        controlsComposite.setLayout( controlsLayout );
        ListViewer controlsViewer = new ListViewer( controlsComposite );
        controlsViewer.getList().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        controlsViewer.setContentProvider( new ArrayContentProvider() );
        controlsViewer.setLabelProvider( new LabelProvider() );
        if ( connection != null && connection.getRootDSE() != null )
        {
            controlsViewer.setInput( ((RootDSE)connection.getRootDSE()).getSupportedControls() );
        }
        this.controlsTab = new TabItem( this.tabFolder, SWT.NONE );
        this.controlsTab.setText( "Controls" );
        this.controlsTab.setControl( controlsComposite );

        Composite extensionComposite = new Composite( this.tabFolder, SWT.NONE );
        extensionComposite.setLayoutData( new RowData( 10, 10 ) );
        GridLayout extensionLayout = new GridLayout();
        extensionComposite.setLayout( extensionLayout );
        ListViewer extensionViewer = new ListViewer( extensionComposite );
        extensionViewer.getList().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        extensionViewer.setContentProvider( new ArrayContentProvider() );
        extensionViewer.setLabelProvider( new LabelProvider() );
        if ( connection != null && connection.getRootDSE() != null )
        {
            extensionViewer.setInput( ((RootDSE)connection.getRootDSE()).getSupportedExtensions() );
        }
        this.extensionsTab = new TabItem( this.tabFolder, SWT.NONE );
        this.extensionsTab.setText( "Extensions" );
        this.extensionsTab.setControl( extensionComposite );

        Composite featureComposite = new Composite( this.tabFolder, SWT.NONE );
        featureComposite.setLayoutData( new RowData( 10, 10 ) );
        GridLayout featureLayout = new GridLayout();
        featureComposite.setLayout( featureLayout );
        ListViewer featureViewer = new ListViewer( featureComposite );
        featureViewer.getList().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        featureViewer.setContentProvider( new ArrayContentProvider() );
        featureViewer.setLabelProvider( new LabelProvider() );
        if ( connection != null && connection.getRootDSE() != null )
        {
            featureViewer.setInput( ((RootDSE)connection.getRootDSE()).getSupportedFeatures() );
        }
        this.featuresTab = new TabItem( this.tabFolder, SWT.NONE );
        this.featuresTab.setText( "Features" );
        this.featuresTab.setControl( featureComposite );

        Composite rawComposite = new Composite( this.tabFolder, SWT.NONE );
        rawComposite.setLayoutData( new RowData( 10, 10 ) );
        GridLayout rawLayout = new GridLayout();
        rawComposite.setLayout( rawLayout );
        Table table = new Table( rawComposite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( GridData.FILL_BOTH );
        table.setLayoutData( gridData );
        table.setHeaderVisible( true );
        table.setLinesVisible( true );
        TableViewer viewer = new TableViewer( table );
        for ( int i = 0; i < EntryEditorWidgetTableMetadata.COLUM_NAMES.length; i++ )
        {
            TableColumn column = new TableColumn( table, SWT.LEFT, i );
            column.setText( EntryEditorWidgetTableMetadata.COLUM_NAMES[i] );
            column.setWidth( 150 );
            column.setResizable( true );
        }
        viewer.setColumnProperties( EntryEditorWidgetTableMetadata.COLUM_NAMES );
        viewer.setSorter( new InnerViewerSorter() );
        viewer.setContentProvider( new InnerContentProvider() );
        viewer.setLabelProvider( new InnerLabelProvider() );
        if ( connection != null )
        {
            IEntry entry = connection.getRootDSE();
            viewer.setInput( entry );
        }
        this.rawTab = new TabItem( this.tabFolder, SWT.NONE );
        this.rawTab.setText( "Raw" );
        this.rawTab.setControl( rawComposite );

        // setControl(composite);
        return this.tabFolder;
    }


    private void addInfo( final IConnection connection, Composite composite, String attributeName, String labelName )
    {
        Label label = new Label( composite, SWT.NONE );
        label.setText( labelName );
        Text text = new Text( composite, SWT.NONE );
        text.setEditable( false );
        text.setBackground( composite.getBackground() );
        try
        {
            String[] versions = connection.getRootDSE().getAttribute( attributeName ).getStringValues();
            String version = Arrays.asList( versions ).toString();
            text.setText( version.substring( 1, version.length() - 1 ) );
        }
        catch ( Exception e )
        {
            text.setText( "-" );
        }
    }

    class InnerContentProvider implements IStructuredContentProvider
    {
        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof IEntry )
            {
                IEntry entry = ( IEntry ) inputElement;
                if ( !entry.isAttributesInitialized() && entry.isDirectoryEntry() )
                {
                    return new Object[]
                        {};
                }
                else
                {
                    IAttribute[] attributes = entry.getAttributes();
                    List valueList = new ArrayList();
                    for ( int i = 0; attributes != null && i < attributes.length; i++ )
                    {
                        IValue[] values = attributes[i].getValues();
                        for ( int j = 0; j < values.length; j++ )
                        {
                            valueList.add( values[j] );
                        }
                    }
                    return valueList.toArray();
                }
            }
            return new Object[0];
        }


        public void dispose()
        {
        }


        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    class InnerLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText( Object obj, int index )
        {
            if ( obj != null && obj instanceof IValue )
            {
                IValue attributeValue = ( IValue ) obj;
                switch ( index )
                {
                    case EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX:
                        return attributeValue.getAttribute().getDescription();
                    case EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX:
                        return attributeValue.getStringValue();
                    default:
                        return "";
                }
            }
            return "";
        }


        public Image getColumnImage( Object obj, int index )
        {
            return super.getImage( obj );
        }
    }

    class InnerViewerSorter extends ViewerSorter
    {

    }

}
