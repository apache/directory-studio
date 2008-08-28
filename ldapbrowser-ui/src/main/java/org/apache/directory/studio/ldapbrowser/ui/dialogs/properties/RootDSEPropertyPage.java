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
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
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
 * @version $Rev$, $Date$
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
        BaseWidgetUtils.createLabel( infoComposite, "Directory Type:", 1 );
        Text typeText = BaseWidgetUtils.createWrappedLabeledText( infoComposite, "-", 1, 150);
        if ( connection != null && connection.getRootDSE() != null )
        {
            // Try to detect LDAP server from RootDSE
            IRootDSE rootDSE = connection.getRootDSE();
            String type = detectOpenLDAP( rootDSE );
            if ( type == null )
            {
                type = detectSiemensDirX( rootDSE );
                if ( type == null )
                {
                    type = detectActiveDirectory( rootDSE );
                    if ( type == null )
                    {
                        type = detectByVendorName( rootDSE );
                    }
                }
            }

            if ( type != null )
            {
                typeText.setText( type );
            }
        }
        addInfo( connection, infoComposite, "vendorName", "Vendor Name:" );
        addInfo( connection, infoComposite, "vendorVersion", "Vendor Version:" );
        addInfo( connection, infoComposite, "supportedLDAPVersion", "Supported LDAP Versions:" );
        addInfo( connection, infoComposite, "supportedSASLMechanisms", "Supported SASL Mechanisms:" );
        infoTab = new TabItem( tabFolder, SWT.NONE );
        infoTab.setText( "Info" );
        infoTab.setControl( infoComposite );

        // Controls tab 
        Composite controlsComposite = new Composite( tabFolder, SWT.NONE );
        controlsComposite.setLayout( new GridLayout() );
        Composite controlsComposite2 = BaseWidgetUtils.createColumnContainer( controlsComposite, 2, 1 );
        addOidInfo( connection, controlsComposite2, "supportedControl" );
        controlsTab = new TabItem( tabFolder, SWT.NONE );
        controlsTab.setText( "Controls" );
        controlsTab.setControl( controlsComposite );

        // Extensions tab
        Composite extensionComposite = new Composite( tabFolder, SWT.NONE );
        extensionComposite.setLayout( new GridLayout() );
        Composite extensionComposite2 = BaseWidgetUtils.createColumnContainer( extensionComposite, 2, 1 );
        addOidInfo( connection, extensionComposite2, "supportedExtension" );
        extensionsTab = new TabItem( tabFolder, SWT.NONE );
        extensionsTab.setText( "Extensions" );
        extensionsTab.setControl( extensionComposite );

        // Features tab
        Composite featureComposite = new Composite( tabFolder, SWT.NONE );
        featureComposite.setLayout( new GridLayout() );
        Composite featureComposite2 = BaseWidgetUtils.createColumnContainer( featureComposite, 2, 1 );
        addOidInfo( connection, featureComposite2, "supportedFeatures" );
        featuresTab = new TabItem( tabFolder, SWT.NONE );
        featuresTab.setText( "Features" );
        featuresTab.setControl( featureComposite );

        return tabFolder;
    }


    /** 
     * Check various LDAP servers via vendorName attribute.
     * 
     * @param rootDSE
     */
    private String detectByVendorName( IRootDSE rootDSE )
    {
        String result = null;

        IAttribute vnAttribute = rootDSE.getAttribute( "vendorName" );
        IAttribute vvAttribute = rootDSE.getAttribute( "vendorVersion" );

        if ( vnAttribute != null && vnAttribute.getStringValues().length > 0 && vvAttribute != null
            && vvAttribute.getStringValues().length > 0 )
        {
            if ( vnAttribute.getStringValues()[0].indexOf( "Apache Software Foundation" ) > -1 )
            {
                result = "Apache Directory Server";
            }
            if ( vnAttribute.getStringValues()[0].indexOf( "Novell" ) > -1
                || vvAttribute.getStringValues()[0].indexOf( "eDirectory" ) > -1 )
            {
                result = "Novell eDirectory";
            }
            if ( vnAttribute.getStringValues()[0].indexOf( "Sun" ) > -1
                || vvAttribute.getStringValues()[0].indexOf( "Sun" ) > -1 )
            {
                result = "Sun Directory Server";
            }
            if ( vnAttribute.getStringValues()[0].indexOf( "Netscape" ) > -1
                || vvAttribute.getStringValues()[0].indexOf( "Netscape" ) > -1 )
            {
                result = "Netscape Directory Server";
            }
            if ( vnAttribute.getStringValues()[0].indexOf( "International Business Machines" ) > -1
                && ( ( vvAttribute.getStringValues()[0].indexOf( "6.0" ) > -1 ) || ( vvAttribute.getStringValues()[0]
                    .indexOf( "5.2" ) > -1 ) ) )
            {
                result = "IBM Tivoli Directory Server";
            }
        }

        return result;
    }


    /**
     * Tries to detect a Microsoft Active Directory.
     * 
     * @param rootDSE
     * @return name of directory type, or null if no Active Directory server server was detected
     */
    private String detectActiveDirectory( IRootDSE rootDSE )
    {
        String result = null;

        // check active directory
        IAttribute rdncAttribute = rootDSE.getAttribute( "rootDomainNamingContext" );
        if ( rdncAttribute != null )
        {
            IAttribute ffAttribute = rootDSE.getAttribute( "forestFunctionality" );
            if ( ffAttribute != null )
            {
                result = "Microsoft Active Directory 2003";
            }
            else
            {
                result = "Microsoft Active Directory 2000";
            }
        }

        return result;
    }


    /**
     * Tries to detect a Siemens DirX server.
     * 
     * @param rootDSE 
     * @return name of directory type, or null if no DirX server server was detected
     */
    private String detectSiemensDirX( IRootDSE rootDSE )
    {
        String result = null;

        IAttribute ssseAttribute = rootDSE.getAttribute( "subSchemaSubentry" );
        if ( ssseAttribute != null )
        {
            for ( int i = 0; i < ssseAttribute.getStringValues().length; i++ )
            {
                if ( "cn=LDAPGlobalSchemaSubentry".equals( ssseAttribute.getStringValues()[i] ) )
                {
                    result = "Siemens DirX";
                }
            }
        }

        return result;
    }


    /**
     * Tries to detect an OpenLDAP server
     * 
     * @param rootDSE
     * @return name (and sometimes version) of directory type, or null if no OpenLDAP server was detected
     */
    private String detectOpenLDAP( IRootDSE rootDSE )
    {
        String result = null;
        boolean typeDetected = false;

        // check OpenLDAP
        IAttribute ocAttribute = rootDSE.getAttribute( "objectClass" );
        if ( ocAttribute != null )
        {
            for ( int i = 0; i < ocAttribute.getStringValues().length; i++ )
            {
                if ( "OpenLDAProotDSE".equals( ocAttribute.getStringValues()[i] ) )
                {
                    IAttribute ccAttribute = rootDSE.getAttribute( "configContext" );
                    if ( ccAttribute != null )
                    {
                        result = "OpenLDAP 2.3";
                        typeDetected = true;
                    }
                    if ( !typeDetected )
                    {
                        IAttribute scAttribute = rootDSE.getAttribute( "supportedControl" );
                        if ( scAttribute != null )
                        {
                            for ( int sci = 0; sci < scAttribute.getStringValues().length; sci++ )
                            {
                                // if("1.2.840.113556.1.4.319".equals(scAttribute.getStringValues()[sci]))
                                // {
                                if ( "2.16.840.1.113730.3.4.18".equals( scAttribute.getStringValues()[sci] ) )
                                {
                                    result = "OpenLDAP 2.2";
                                    typeDetected = true;
                                }
                            }
                        }

                    }
                    if ( !typeDetected )
                    {
                        IAttribute seAttribute = rootDSE.getAttribute( "supportedExtension" );
                        if ( seAttribute != null )
                        {
                            for ( int sei = 0; sei < seAttribute.getStringValues().length; sei++ )
                            {
                                if ( "1.3.6.1.4.1.4203.1.11.3".equals( seAttribute.getStringValues()[sei] ) )
                                {
                                    result = "OpenLDAP 2.1";
                                    typeDetected = true;
                                }
                            }
                        }
                    }
                    if ( !typeDetected )
                    {
                        IAttribute sfAttribute = rootDSE.getAttribute( "supportedFeatures" );
                        if ( sfAttribute != null )
                        {
                            for ( int sfi = 0; sfi < sfAttribute.getStringValues().length; sfi++ )
                            {
                                if ( "1.3.6.1.4.1.4203.1.5.4".equals( sfAttribute.getStringValues()[sfi] ) )
                                {
                                    result = "OpenLDAP 2.0";
                                    typeDetected = true;
                                }
                            }
                        }
                    }
                    if ( !typeDetected )
                    {
                        result = "OpenLDAP";
                        typeDetected = true;
                    }
                }
            }
        }

        return result;
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
            sb.append( "-" );
        }

        BaseWidgetUtils.createLabel( composite, labelName, 1 );
        BaseWidgetUtils.createWrappedLabeledText( composite, sb.toString(), 1, 150);
    }

}
