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


import org.apache.commons.lang.StringUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionServerType;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.ServerTypeDetector;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * This page shows some info about the Root DSE.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RootDSEPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{
    /** The tab folder. */
    private TabFolder tabFolder;

    /** The info tab. */
    private TabItem infoTab;

    /** The controls tab. */
    private TabItem controlsTab;

    /** The extensions tab. */
    private TabItem extensionsTab;

    /** The features tab. */
    private TabItem featuresTab;


    /**
     * Creates a new instance of RootDSEPropertyPage.
     */
    public RootDSEPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * Gets the browser connection, or null if the given element
     * isn't adaptable to a browser connection.
     * 
     * @param element the element
     * 
     * @return the browser connection
     */
    static IBrowserConnection getConnection( Object element )
    {
        IBrowserConnection browserConnection = null;
        if ( element instanceof IAdaptable )
        {
            browserConnection = ( IBrowserConnection ) ( ( IAdaptable ) element ).getAdapter( IBrowserConnection.class );
            if ( browserConnection == null )
            {
                Connection connection = ( Connection ) ( ( IAdaptable ) element ).getAdapter( Connection.class );
                browserConnection = BrowserCorePlugin.getDefault().getConnectionManager().getBrowserConnection(
                    connection );
            }
        }
        return browserConnection;
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        final IBrowserConnection connection = getConnection( getElement() );

        tabFolder = new TabFolder( parent, SWT.TOP );
        RowLayout mainLayout = new RowLayout();
        mainLayout.fill = true;
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        tabFolder.setLayout( mainLayout );

        // Info tab
        Composite infoComposite = new Composite( tabFolder, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        infoComposite.setLayout( gl );
        BaseWidgetUtils.createLabel( infoComposite, Messages.getString( "RootDSEPropertyPage.DirectoryType" ), 1 ); //$NON-NLS-1$
        Text typeText = BaseWidgetUtils.createWrappedLabeledText( infoComposite, "-", 1, 150 ); //$NON-NLS-1$
        if ( connection != null && connection.getRootDSE() != null )
        {
            // Try to detect LDAP server from RootDSE
            ConnectionServerType serverType = ServerTypeDetector.detectServerType( connection.getRootDSE() );
            if ( serverType != null )
            {
                switch ( serverType )
                {
                    case APACHEDS:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.ApacheDirectoryServer" ) ); //$NON-NLS-1$
                        break;
                    case IBM_DIRECTORY_SERVER:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.IBMDirectory" ) ); //$NON-NLS-1$
                        break;
                    case IBM_SECUREWAY_DIRECTORY:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.IBMSecureWay" ) ); //$NON-NLS-1$
                        break;
                    case IBM_TIVOLI_DIRECTORY_SERVER:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.IBMTivoli" ) ); //$NON-NLS-1$
                        break;
                    case MICROSOFT_ACTIVE_DIRECTORY_2000:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.MSAD2000" ) ); //$NON-NLS-1$
                        break;
                    case MICROSOFT_ACTIVE_DIRECTORY_2003:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.MSAD2003" ) ); //$NON-NLS-1$
                        break;
                    case NETSCAPE:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.NetscapeDirectoryServer" ) ); //$NON-NLS-1$
                        break;
                    case NOVELL:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.NovellEDirectory" ) ); //$NON-NLS-1$
                        break;
                    case OPENLDAP:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.OpenLDAP" ) ); //$NON-NLS-1$
                        break;
                    case OPENLDAP_2_0:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.OpenLDAP20" ) ); //$NON-NLS-1$
                        break;
                    case OPENLDAP_2_1:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.OpenLDAP21" ) ); //$NON-NLS-1$
                        break;
                    case OPENLDAP_2_2:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.OpenLDAP22" ) ); //$NON-NLS-1$
                        break;
                    case OPENLDAP_2_3:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.OpenLDAP23" ) ); //$NON-NLS-1$
                        break;
                    case OPENLDAP_2_4:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.OpenLDAP24" ) ); //$NON-NLS-1$
                        break;
                    case SIEMENS_DIRX:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.SiemensDirX" ) ); //$NON-NLS-1$
                        break;
                    case SUN_DIRECTORY_SERVER:
                        typeText.setText( Messages.getString( "RootDSEPropertyPage.SunDirectoryServer" ) ); //$NON-NLS-1$
                        break;
                }
            }
        }
        addInfo( connection, infoComposite, "vendorName", Messages.getString( "RootDSEPropertyPage.VendorName" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        addInfo( connection, infoComposite, "vendorVersion", Messages.getString( "RootDSEPropertyPage.VendorVersion" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        addInfo( connection, infoComposite,
            "supportedLDAPVersion", Messages.getString( "RootDSEPropertyPage.SupportedLDAPVersion" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        addInfo( connection, infoComposite,
            "supportedSASLMechanisms", Messages.getString( "RootDSEPropertyPage.SupportedSASL" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        infoTab = new TabItem( tabFolder, SWT.NONE );
        infoTab.setText( Messages.getString( "RootDSEPropertyPage.Info" ) ); //$NON-NLS-1$
        infoTab.setControl( infoComposite );

        // Controls tab 
        Composite controlsComposite = new Composite( tabFolder, SWT.NONE );
        controlsComposite.setLayout( new GridLayout() );
        Composite controlsComposite2 = BaseWidgetUtils.createColumnContainer( controlsComposite, 2, 1 );
        addOidInfo( connection, controlsComposite2, "supportedControl" ); //$NON-NLS-1$
        controlsTab = new TabItem( tabFolder, SWT.NONE );
        controlsTab.setText( Messages.getString( "RootDSEPropertyPage.Controls" ) ); //$NON-NLS-1$
        controlsTab.setControl( controlsComposite );

        // Extensions tab
        Composite extensionComposite = new Composite( tabFolder, SWT.NONE );
        extensionComposite.setLayout( new GridLayout() );
        Composite extensionComposite2 = BaseWidgetUtils.createColumnContainer( extensionComposite, 2, 1 );
        addOidInfo( connection, extensionComposite2, "supportedExtension" ); //$NON-NLS-1$
        extensionsTab = new TabItem( tabFolder, SWT.NONE );
        extensionsTab.setText( Messages.getString( "RootDSEPropertyPage.Extensions" ) ); //$NON-NLS-1$
        extensionsTab.setControl( extensionComposite );

        // Features tab
        Composite featureComposite = new Composite( tabFolder, SWT.NONE );
        featureComposite.setLayout( new GridLayout() );
        Composite featureComposite2 = BaseWidgetUtils.createColumnContainer( featureComposite, 2, 1 );
        addOidInfo( connection, featureComposite2, "supportedFeatures" ); //$NON-NLS-1$
        featuresTab = new TabItem( tabFolder, SWT.NONE );
        featuresTab.setText( Messages.getString( "RootDSEPropertyPage.Features" ) ); //$NON-NLS-1$
        featuresTab.setControl( featureComposite );

        return tabFolder;
    }


    /**
     * Adds text fields to the composite. The text fields contain
     * the OID values of the given attribute and the OID description.
     * 
     * @param browserConnection the browser connection
     * @param composite the composite
     * @param attributeType the attribute type
     */
    private void addOidInfo( final IBrowserConnection browserConnection, Composite composite, String attributeType )
    {
        try
        {
            String[] values = browserConnection.getRootDSE().getAttribute( attributeType ).getStringValues();
            for ( String value : values )
            {
                String description = Utils.getOidDescription( value );
                if ( description == null )
                {
                    description = StringUtils.EMPTY;
                }
                BaseWidgetUtils.createLabeledText( composite, value, 1, 15 );
                BaseWidgetUtils.createLabeledText( composite, description, 1, 15 );
            }
        }
        catch ( Exception e )
        {
        }
    }


    /**
     * Adds an text field to the composite. It contains the given label and
     * the values of the given attribute.
     * 
     * @param browserConnection the browser connection
     * @param composite the composite
     * @param attributeType the attribute type
     * @param labelName the label name
     */
    private void addInfo( final IBrowserConnection browserConnection, Composite composite, String attributeType,
        String labelName )
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            String[] values = browserConnection.getRootDSE().getAttribute( attributeType ).getStringValues();
            boolean isFirst = true;
            for ( String value : values )
            {
                if ( !isFirst )
                {
                    sb.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
                sb.append( value );
                isFirst = false;
            }
        }
        catch ( Exception e )
        {
            sb.append( Messages.getString( "RootDSEPropertyPage.Dash" ) ); //$NON-NLS-1$
        }

        BaseWidgetUtils.createLabel( composite, labelName, 1 );
        BaseWidgetUtils.createWrappedLabeledText( composite, sb.toString(), 1, 150 );
    }

}
