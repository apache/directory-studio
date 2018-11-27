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
package org.apache.directory.studio.schemaeditor.view.widget;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * This class implements a CoreSchemasSelectionWidget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CoreSchemasSelectionWidget
{
    /**
     * This enum represents the different server types.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum ServerTypeEnum
    {
        APACHE_DS, OPENLDAP
    }

    /** The array containing the 'core' from ApacheDS */
    private static final String[] coreSchemasFromApacheDS = new String[]
        { "adsconfig", "apache", "apachedns", "apachemeta", "autofs", "collective", "corba", "core", "cosine", "dhcp", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
            "inetorgperson", "java", "krb5kdc", "mozilla", "nis", "pwdpolicy", "samba", "system" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    /** The array containing the 'core' from OpenLDAP */
    private static final String[] coreSchemasFromOpenLdap = new String[]
        { "collective", "corba", "core", "cosine", "dyngroup", "duaconf", "inetorgperson", "java", "misc", "nis", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
            "openldap", "ppolicy", "system" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    // UI Fields
    private Button typeApacheDSButton;
    private Button typeOpenLDAPButton;
    private CheckboxTableViewer coreSchemasTableViewer;


    /**
     * Creates the widget.
     * 
     * @param parent
     *            the parent Composite
     * @return
     *      the associated composite
     */
    public Composite createWidget( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Server Type Group
        Group serverTypeGroup = new Group( composite, SWT.NONE );
        serverTypeGroup.setText( Messages.getString( "CoreSchemasSelectionWidget.ServerType" ) ); //$NON-NLS-1$
        serverTypeGroup.setLayout( new GridLayout( 2, false ) );
        serverTypeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Type ApacheDS Button
        typeApacheDSButton = new Button( serverTypeGroup, SWT.RADIO );
        typeApacheDSButton.setText( Messages.getString( "CoreSchemasSelectionWidget.ApacheDS" ) ); //$NON-NLS-1$
        typeApacheDSButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                resetTableViewerWithCoreSchemasFromApacheDS();
            }
        } );

        // Type OpenLDAP Button
        typeOpenLDAPButton = new Button( serverTypeGroup, SWT.RADIO );
        typeOpenLDAPButton.setText( Messages.getString( "CoreSchemasSelectionWidget.OpenLDAP" ) ); //$NON-NLS-1$
        typeOpenLDAPButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                resetTableViewerWithCoreSchemasFromOpenLdap();
            }
        } );

        // Core Schemas Label
        Label coreSchemaslabel = new Label( composite, SWT.NONE );
        coreSchemaslabel.setText( Messages.getString( "CoreSchemasSelectionWidget.ChooseCoreSchemas" ) ); //$NON-NLS-1$
        coreSchemaslabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Core Schemas TableViewer
        coreSchemasTableViewer = new CheckboxTableViewer( new Table( composite, SWT.BORDER | SWT.CHECK
            | SWT.FULL_SELECTION ) );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        gridData.heightHint = 127;
        coreSchemasTableViewer.getTable().setLayoutData( gridData );
        coreSchemasTableViewer.setContentProvider( new ArrayContentProvider() );
        coreSchemasTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA );
            }
        } );

        Button coreSchemasTableSelectAllButton = new Button( composite, SWT.PUSH );
        coreSchemasTableSelectAllButton.setText( Messages.getString( "CoreSchemasSelectionWidget.SelectAll" ) ); //$NON-NLS-1$
        coreSchemasTableSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        coreSchemasTableSelectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                coreSchemasTableViewer.setAllChecked( true );
            }
        } );

        Button coreSchemasTableDeselectAllButton = new Button( composite, SWT.PUSH );
        coreSchemasTableDeselectAllButton.setText( Messages.getString( "CoreSchemasSelectionWidget.DeselectAll" ) ); //$NON-NLS-1$
        coreSchemasTableDeselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        coreSchemasTableDeselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                coreSchemasTableViewer.setAllChecked( false );
            }
        } );

        return composite;
    }


    /**
     * Gets the checked 'core' schemas.
     *
     * @return
     *      the checked 'core' schemas.
     */
    public String[] getCheckedCoreSchemas()
    {
        return Arrays.asList( coreSchemasTableViewer.getCheckedElements() ).toArray( new String[0] );
    }


    /**
     * Gets the grayed 'core' schemas.
     *
     * @return
     *      the grayed 'core' schemas
     */
    public String[] getGrayedCoreSchemas()
    {
        return Arrays.asList( coreSchemasTableViewer.getGrayedElements() ).toArray( new String[0] );
    }


    /**
     * Gets the selected 'core' schemas.
     *
     * @return
     *      the selected 'core' schemas
     */
    public String[] getSelectedCoreSchemas()
    {
        List<String> selectedSchemas = new ArrayList<String>();

        selectedSchemas.addAll( Arrays.asList( getCheckedCoreSchemas() ) );
        selectedSchemas.removeAll( Arrays.asList( getGrayedCoreSchemas() ) );

        return selectedSchemas.toArray( new String[0] );
    }


    /**
     * Gets the Server Type
     *
     * @return
     *      the Server Type
     */
    public ServerTypeEnum getServerType()
    {
        if ( typeApacheDSButton.getSelection() )
        {
            return ServerTypeEnum.APACHE_DS;
        }
        else if ( typeOpenLDAPButton.getSelection() )
        {
            return ServerTypeEnum.OPENLDAP;
        }
        else
        {
            // Default
            return null;
        }
    }


    /**
     * Initializes the widget.
     *
     * @param selectedButton
     *      the selected button:
     *      <ul>
     *      <li>{@link ServerTypeEnum}.APACHE_DS for the "ApacheDS" button</li>
     *      <li>{@link ServerTypeEnum}.OPENLDAP for the "OpenLDAP" button</li>
     *      </ul>
     */
    public void init( ServerTypeEnum selectedButton )
    {
        // Setting the selected button
        if ( selectedButton != null )
        {
            switch ( selectedButton )
            {
                case APACHE_DS:
                    typeApacheDSButton.setSelection( true );
                    resetTableViewerWithCoreSchemasFromApacheDS();
                    break;
                case OPENLDAP:
                    typeOpenLDAPButton.setSelection( true );
                    resetTableViewerWithCoreSchemasFromOpenLdap();
                    break;
            }
        }
    }


    /**
     * Re-initializes the Table Viewer with the 'core' schemas from ApacheDS.
     */
    private void resetTableViewerWithCoreSchemasFromApacheDS()
    {
        coreSchemasTableViewer.setAllChecked( false );
        coreSchemasTableViewer.setInput( coreSchemasFromApacheDS );
    }


    /**
     * Re-initializes the Table Viewer with the 'core' schemas from OpenLDAP.
     */
    private void resetTableViewerWithCoreSchemasFromOpenLdap()
    {
        coreSchemasTableViewer.setAllChecked( false );
        coreSchemasTableViewer.setInput( coreSchemasFromOpenLdap );
    }


    /**
     * Sets the checked 'core' schemas.
     *
     * @param checkedCoreSchemas
     */
    public void setCheckedCoreSchemas( String[] checkedCoreSchemas )
    {
        coreSchemasTableViewer.setCheckedElements( checkedCoreSchemas );
    }


    /**
     * Sets the grayed 'core' schemas.
     *
     * @param grayedCoreSchemas
     */
    public void setGrayedCoreSchemas( String[] grayedCoreSchemas )
    {
        coreSchemasTableViewer.setGrayedElements( grayedCoreSchemas );
    }
}
