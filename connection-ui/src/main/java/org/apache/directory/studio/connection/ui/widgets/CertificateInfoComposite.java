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
package org.apache.directory.studio.connection.ui.widgets;


import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


/**
 * This composite contains the tabs with general and detail of an certificate.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CertificateInfoComposite extends Composite
{

    /** The default attributes of an X500Principal: CN, L, ST, O, OU, C, STREET, DC, UID */
    String[] ATTRIBUTES =
        { "CN", //$NON-NLS-1$
            "L", //$NON-NLS-1$
            "ST", //$NON-NLS-1$
            "O", //$NON-NLS-1$
            "OU", //$NON-NLS-1$
            "C", //$NON-NLS-1$
            "STREET", //$NON-NLS-1$
            "DC", //$NON-NLS-1$
            "UID" //$NON-NLS-1$
        };

    /** The index of the general tab */
    public static final int GENERAL_TAB_INDEX = 0;

    /** The index of the details tab */
    public static final int DETAILS_TAB_INDEX = 1;

    /** The tab folder */
    private TabFolder tabFolder;

    /** The general tab */
    private TabItem generalTab;

    private Text issuedToCN;
    private Text issuedToO;
    private Text issuedToOU;
    private Text serialNumber;
    private Text issuedByCN;
    private Text issuedByO;
    private Text issuedByOU;
    private Text issuesOn;
    private Text expiresOn;
    private Text fingerprintSHA1;
    private Text fingerprintMD5;

    /** The details tab */
    private TabItem detailsTab;

    private TreeViewer hierarchyTreeViewer;
    private Tree certificateTree;
    private Text valueText;


    /**
     * Creates a new instance of CertificateInfoComposite.
     *
     * @param parent
     * @param style
     */
    public CertificateInfoComposite( Composite parent, int style )
    {
        super( parent, style );
        setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout( layout );

        createTabFolder();
        createGeneralTab();
        createDetailsTab();
    }


    /**
     * Creates the tab folder.
     */
    private void createTabFolder()
    {
        tabFolder = new TabFolder( this, SWT.TOP );
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        tabFolder.setLayout( mainLayout );
        tabFolder.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
    }


    /**
     * Creates the general tab.
     */
    private void createGeneralTab()
    {
        // create inner container
        Composite generalContainer = new Composite( tabFolder, SWT.NONE );
        GridLayout currentLayout = new GridLayout( 1, false );
        generalContainer.setLayout( currentLayout );
        generalContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        // issues to section
        Group issuedToGroup = BaseWidgetUtils.createGroup( generalContainer, Messages
            .getString( "CertificateInfoComposite.IssuedToLabel" ), 1 ); //$NON-NLS-1$
        issuedToGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite issuedToComposite = BaseWidgetUtils.createColumnContainer( issuedToGroup, 2, 1 );
        BaseWidgetUtils.createLabel( issuedToComposite,
            Messages.getString( "CertificateInfoComposite.CommonNameLabel" ), 1 ); //$NON-NLS-1$
        issuedToCN = BaseWidgetUtils.createLabeledText( issuedToComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( issuedToComposite, Messages
            .getString( "CertificateInfoComposite.OrganizationLabel" ), 1 ); //$NON-NLS-1$
        issuedToO = BaseWidgetUtils.createLabeledText( issuedToComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( issuedToComposite, Messages
            .getString( "CertificateInfoComposite.OrganizationalUnitLabel" ), 1 ); //$NON-NLS-1$
        issuedToOU = BaseWidgetUtils.createLabeledText( issuedToComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( issuedToComposite, Messages
            .getString( "CertificateInfoComposite.SerialNumberLabel" ), 1 ); //$NON-NLS-1$
        serialNumber = BaseWidgetUtils.createLabeledText( issuedToComposite, StringUtils.EMPTY, 1 );

        // issuer section
        Group issuedFromGroup = BaseWidgetUtils.createGroup( generalContainer, Messages
            .getString( "CertificateInfoComposite.IssuedByLabel" ), 1 ); //$NON-NLS-1$
        issuedFromGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite issuedFromComposite = BaseWidgetUtils.createColumnContainer( issuedFromGroup, 2, 1 );
        BaseWidgetUtils.createLabel( issuedFromComposite, Messages
            .getString( "CertificateInfoComposite.CommonNameLabel" ), 1 ); //$NON-NLS-1$
        issuedByCN = BaseWidgetUtils.createLabeledText( issuedFromComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( issuedFromComposite, Messages
            .getString( "CertificateInfoComposite.OrganizationLabel" ), 1 ); //$NON-NLS-1$
        issuedByO = BaseWidgetUtils.createLabeledText( issuedFromComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( issuedFromComposite, Messages
            .getString( "CertificateInfoComposite.OrganizationalUnitLabel" ), 1 ); //$NON-NLS-1$
        issuedByOU = BaseWidgetUtils.createLabeledText( issuedFromComposite, StringUtils.EMPTY, 1 );

        // validity section
        Group validityGroup = BaseWidgetUtils.createGroup( generalContainer, Messages
            .getString( "CertificateInfoComposite.ValidityLabel" ), 1 ); //$NON-NLS-1$
        validityGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite generalComposite = BaseWidgetUtils.createColumnContainer( validityGroup, 2, 1 );
        BaseWidgetUtils.createLabel( generalComposite,
            Messages.getString( "CertificateInfoComposite.IssuedOnLabel" ), 1 ); //$NON-NLS-1$
        issuesOn = BaseWidgetUtils.createLabeledText( generalComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( generalComposite,
            Messages.getString( "CertificateInfoComposite.ExpiresOnLabel" ), 1 ); //$NON-NLS-1$
        expiresOn = BaseWidgetUtils.createLabeledText( generalComposite, StringUtils.EMPTY, 1 );

        // fingerprint section
        Group fingerprintsGroup = BaseWidgetUtils.createGroup( generalContainer, Messages
            .getString( "CertificateInfoComposite.FingerprintsLabel" ), 1 ); //$NON-NLS-1$
        fingerprintsGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite fingerprintsComposite = BaseWidgetUtils.createColumnContainer( fingerprintsGroup, 2, 1 );
        BaseWidgetUtils.createLabel( fingerprintsComposite, Messages
            .getString( "CertificateInfoComposite.SHA1FingerprintLabel" ), 1 ); //$NON-NLS-1$
        fingerprintSHA1 = BaseWidgetUtils.createLabeledText( fingerprintsComposite, StringUtils.EMPTY, 1 );
        BaseWidgetUtils.createLabel( fingerprintsComposite, Messages
            .getString( "CertificateInfoComposite.MD5FingerprintLabel" ), 1 ); //$NON-NLS-1$
        fingerprintMD5 = BaseWidgetUtils.createLabeledText( fingerprintsComposite, StringUtils.EMPTY, 1 );

        // create tab
        generalTab = new TabItem( tabFolder, SWT.NONE, GENERAL_TAB_INDEX );
        generalTab.setText( Messages.getString( "CertificateInfoComposite.General" ) ); //$NON-NLS-1$
        generalTab.setControl( generalContainer );
    }


    /**
     * Creates the details tab.
     */
    private void createDetailsTab()
    {
        SashForm detailsForm = new SashForm( tabFolder, SWT.VERTICAL );
        detailsForm.setLayout( new FillLayout() );

        Composite hierarchyContainer = new Composite( detailsForm, SWT.NONE );
        hierarchyContainer.setLayout( new GridLayout( 1, false ) );
        BaseWidgetUtils.createLabel( hierarchyContainer, Messages
            .getString( "CertificateInfoComposite.CertificateHierarchyLabel" ), 1 ); //$NON-NLS-1$
        hierarchyTreeViewer = new TreeViewer( hierarchyContainer );
        hierarchyTreeViewer.getTree().setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
        hierarchyTreeViewer.setContentProvider( new HierarchyContentProvider() );
        hierarchyTreeViewer.setLabelProvider( new HierarchyLabelProvider() );
        hierarchyTreeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                event.getSelection();
                populateCertificateTree();
            }
        } );

        Composite certificateContainer = new Composite( detailsForm, SWT.NONE );
        certificateContainer.setLayout( new GridLayout( 1, false ) );
        BaseWidgetUtils.createLabel( certificateContainer, Messages
            .getString( "CertificateInfoComposite.CertificateFieldsLabel" ), 1 ); //$NON-NLS-1$
        certificateTree = new Tree( certificateContainer, SWT.BORDER );
        certificateTree.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
        certificateTree.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( final SelectionEvent e )
            {
                TreeItem item = ( TreeItem ) e.item;
                if ( ( item != null ) && ( item.getData() != null ) )
                {
                    valueText.setText( item.getData().toString() );
                }
                else
                {
                    valueText.setText( StringUtils.EMPTY );
                }
            }
        } );

        Composite valueContainer = new Composite( detailsForm, SWT.NONE );
        valueContainer.setLayout( new GridLayout( 1, false ) );
        BaseWidgetUtils.createLabel( valueContainer,
            Messages.getString( "CertificateInfoComposite.FieldValuesLabel" ), 1 ); //$NON-NLS-1$
        valueText = new Text( valueContainer, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        valueText.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
        valueText.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );
        valueText.setBackground( detailsForm.getBackground() );

        // create tab
        detailsForm.setWeights( new int[]
            { 1, 2, 1 } );
        detailsTab = new TabItem( tabFolder, SWT.NONE, DETAILS_TAB_INDEX );
        detailsTab.setText( Messages.getString( "CertificateInfoComposite.Details" ) ); //$NON-NLS-1$
        detailsTab.setControl( detailsForm );
    }


    /**
     * Sets the input for this composite. 
     *
     * @param certificateChain certificate chain input
     */
    public void setInput( X509Certificate[] certificateChain )
    {
        X509Certificate certificate = certificateChain[0];

        X500Principal issuedToPrincipal = certificate.getSubjectX500Principal();
        Map<String, String> issuedToAttributes = getAttributeMap( issuedToPrincipal );
        issuedToCN.setText( issuedToAttributes.get( "CN" ) ); //$NON-NLS-1$
        issuedToO.setText( issuedToAttributes.get( "O" ) ); //$NON-NLS-1$
        issuedToOU.setText( issuedToAttributes.get( "OU" ) ); //$NON-NLS-1$
        serialNumber.setText( certificate.getSerialNumber().toString( 16 ) );

        X500Principal issuedFromPrincipal = certificate.getIssuerX500Principal();
        Map<String, String> issuedFromAttributes = getAttributeMap( issuedFromPrincipal );
        issuedByCN.setText( issuedFromAttributes.get( "CN" ) ); //$NON-NLS-1$
        issuedByO.setText( issuedFromAttributes.get( "O" ) ); //$NON-NLS-1$
        issuedByOU.setText( issuedFromAttributes.get( "OU" ) ); //$NON-NLS-1$

        issuesOn.setText( DateFormatUtils.ISO_DATE_FORMAT.format( certificate.getNotBefore() ) );
        expiresOn.setText( DateFormatUtils.ISO_DATE_FORMAT.format( certificate.getNotAfter() ) );

        byte[] encoded2 = null;
        try
        {
            encoded2 = certificate.getEncoded();
        }
        catch ( CertificateEncodingException e )
        {
        }
        byte[] md5 = DigestUtils.md5( encoded2 );
        String md5HexString = getHexString( md5 );
        fingerprintMD5.setText( md5HexString );
        byte[] sha = DigestUtils.sha( encoded2 );
        String shaHexString = getHexString( sha );
        fingerprintSHA1.setText( shaHexString );

        // Details: certificate chain
        CertificateChainItem parentItem = null;
        CertificateChainItem certificateItem = null;
        for ( X509Certificate cert : certificateChain )
        {
            CertificateChainItem item = new CertificateChainItem( cert );
            if ( parentItem != null )
            {
                item.child = parentItem;
                parentItem.parent = item;
            }
            if ( certificateItem == null )
            {
                certificateItem = item;
            }
            parentItem = item;
        }
        hierarchyTreeViewer.setInput( new CertificateChainItem[]
            { parentItem } );
        hierarchyTreeViewer.expandAll();
        hierarchyTreeViewer.setSelection( new StructuredSelection( certificateItem ), true );

        // Details: 
        certificateTree.removeAll();
        populateCertificateTree();
        valueText.setText( StringUtils.EMPTY );
    }


    private void populateCertificateTree()
    {
        certificateTree.removeAll();
        valueText.setText( StringUtils.EMPTY );

        IStructuredSelection selection = ( IStructuredSelection ) hierarchyTreeViewer.getSelection();
        if ( selection.size() != 1 )
        {
            return;
        }

        CertificateChainItem certificateItem = ( CertificateChainItem ) selection.getFirstElement();;
        X509Certificate certificate = certificateItem.certificate;

        TreeItem rootItem = new TreeItem( certificateTree, SWT.NONE );
        Map<String, String> attributeMap = getAttributeMap( certificate.getSubjectX500Principal() );
        rootItem.setText( attributeMap.get( "CN" ) ); //$NON-NLS-1$

        TreeItem certItem = createTreeItem( rootItem,
            Messages.getString( "CertificateInfoComposite.Certificate" ), StringUtils.EMPTY ); //$NON-NLS-1$
        createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.Version" ), String.valueOf( certificate.getVersion() ) ); //$NON-NLS-1$
        createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.SerialNumber" ), certificate.getSerialNumber().toString( 16 ) ); //$NON-NLS-1$
        createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.Signature" ), certificate.getSigAlgName() ); //$NON-NLS-1$

        // TODO: formatting
        createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.Issuer" ), certificate.getIssuerX500Principal().getName() ); //$NON-NLS-1$

        TreeItem validityItem = createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.Validity" ), StringUtils.EMPTY ); //$NON-NLS-1$
        createTreeItem( validityItem,
            Messages.getString( "CertificateInfoComposite.NotBefore" ), certificate.getNotBefore().toString() ); //$NON-NLS-1$
        createTreeItem( validityItem,
            Messages.getString( "CertificateInfoComposite.NotAfter" ), certificate.getNotAfter().toString() ); //$NON-NLS-1$

        // TODO: formatting
        createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.Subject" ), certificate.getSubjectX500Principal().getName() ); //$NON-NLS-1$

        TreeItem pkiItem = createTreeItem( certItem, Messages
            .getString( "CertificateInfoComposite.SubjectPublicKeyInfo" ), StringUtils.EMPTY ); //$NON-NLS-1$
        createTreeItem(
            pkiItem,
            Messages.getString( "CertificateInfoComposite.SubjectPublicKeyAlgorithm" ), certificate.getPublicKey().getAlgorithm() ); //$NON-NLS-1$
        // TODO: formatting
        createTreeItem(
            pkiItem,
            Messages.getString( "CertificateInfoComposite.SubjectPublicKey" ), new String( Hex.encodeHex( certificate.getPublicKey() //$NON-NLS-1$
                        .getEncoded() ) ) );

        TreeItem extItem = createTreeItem( certItem,
            Messages.getString( "CertificateInfoComposite.Extensions" ), StringUtils.EMPTY ); //$NON-NLS-1$
        populateExtensions( extItem, certificate, true );
        populateExtensions( extItem, certificate, false );

        createTreeItem( rootItem,
            Messages.getString( "CertificateInfoComposite.SignatureAlgorithm" ), certificate.getSigAlgName() ); //$NON-NLS-1$
        // TODO: formatting
        createTreeItem(
            rootItem,
            Messages.getString( "CertificateInfoComposite.Signature" ), new String( Hex.encodeHex( certificate.getSignature() ) ) ); //$NON-NLS-1$

        rootItem.setExpanded( true );
        certItem.setExpanded( true );
        validityItem.setExpanded( true );
        pkiItem.setExpanded( true );
        extItem.setExpanded( true );
    }


    private TreeItem createTreeItem( final TreeItem parent, final String field, final String value )
    {
        TreeItem item = new TreeItem( parent, SWT.NONE );
        item.setText( field );
        item.setData( value );
        return item;
    }


    private void populateExtensions( final TreeItem extensionsItem, final X509Certificate certificate, boolean critical )
    {
        Set<String> oids = critical ? certificate.getCriticalExtensionOIDs() : certificate
            .getNonCriticalExtensionOIDs();

        if ( oids != null )
        {
            for ( String oid : oids )
            {
                // try to parse the extension value byte[] to an ASN1 object
                byte[] extensionValueBin = certificate.getExtensionValue( oid );
                String extensionValue = null;
                try
                {
                    ASN1Object extension = X509ExtensionUtil.fromExtensionValue( extensionValueBin );
                    extensionValue = extension.toString();
                }
                catch ( IOException e )
                {
                    extensionValue = new String( Hex.encodeHex( extensionValueBin ) );
                }

                String value = Messages.getString( "CertificateInfoComposite.ExtensionOIDColon" ) + oid + '\n'; //$NON-NLS-1$
                value += Messages.getString( "CertificateInfoComposite.CriticalColon" ) + Boolean.toString( critical ) + '\n'; //$NON-NLS-1$
                value += Messages.getString( "CertificateInfoComposite.ExtensionValueColon" ) + extensionValue + '\n'; //$NON-NLS-1$

                // TODO: OID descriptions
                // TODO: formatting of extension value
                TreeItem item = createTreeItem( extensionsItem, oid, value );
                createTreeItem( item, Messages.getString( "CertificateInfoComposite.ExtensionOID" ), oid ); //$NON-NLS-1$
                createTreeItem( item,
                    Messages.getString( "CertificateInfoComposite.Critical" ), Boolean.toString( critical ) ); //$NON-NLS-1$
                createTreeItem( item, Messages.getString( "CertificateInfoComposite.ExtensionValue" ), extensionValue ); //$NON-NLS-1$
            }
        }
    }


    private String getHexString( byte[] bytes )
    {
        char[] hex = Hex.encodeHex( bytes );
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < hex.length; i++ )
        {
            if ( i % 2 == 0 && i > 0 )
            {
                sb.append( ':' );
            }
            sb.append( Character.toUpperCase( hex[i] ) );
        }
        return sb.toString();
    }


    /**
     * Converts the distinguished name of the principal 
     * to a map containing the attribute types and values, 
     * one for each relative distinguished name.
     *
     * @param principal the principal
     * @return the map containing attribute types and values
     */
    private Map<String, String> getAttributeMap( X500Principal principal )
    {
        Map<String, String> map = new HashMap<String, String>();

        // populate map with default values
        for ( String attribute : ATTRIBUTES )
        {
            map.put( attribute, "-" ); //$NON-NLS-1$

        }

        // populate map with principal's name
        try
        {
            String name = principal.getName();
            LdapName dn = new LdapName( name );
            List<Rdn> rdns = dn.getRdns();
            for ( Rdn rdn : rdns )
            {
                map.put( rdn.getType().toUpperCase(), rdn.getValue().toString() );
            }
        }
        catch ( NamingException e )
        {
            map.put( "CN", e.getMessage() ); //$NON-NLS-1$
        }

        return map;
    }

    class HierarchyContentProvider implements ITreeContentProvider
    {

        public Object[] getChildren( Object parentElement )
        {
            if ( parentElement instanceof CertificateChainItem )
            {
                CertificateChainItem item = ( CertificateChainItem ) parentElement;
                if ( item.child != null )
                {
                    return new CertificateChainItem[]
                        { item.child };
                }
            }
            return new Object[0];
        }


        public Object getParent( Object element )
        {
            if ( element instanceof CertificateChainItem )
            {
                CertificateChainItem item = ( CertificateChainItem ) element;
                return item.parent;
            }
            return null;
        }


        public boolean hasChildren( Object element )
        {
            return getChildren( element ).length > 0;
        }


        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof CertificateChainItem[] )
            {
                return ( CertificateChainItem[] ) inputElement;
            }
            return getChildren( inputElement );
        }


        public void dispose()
        {
        }


        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

    }

    class HierarchyLabelProvider extends LabelProvider
    {
        @Override
        public String getText( Object element )
        {
            if ( element instanceof CertificateChainItem )
            {
                CertificateChainItem item = ( CertificateChainItem ) element;
                Map<String, String> attributeMap = getAttributeMap( item.certificate.getSubjectX500Principal() );
                return attributeMap.get( "CN" ); //$NON-NLS-1$
            }
            return null;
        }
    }

    class CertificateChainItem
    {
        X509Certificate certificate;
        CertificateChainItem parent;
        CertificateChainItem child;


        public CertificateChainItem( X509Certificate certificate )
        {
            this.certificate = certificate;
        }
    }
}
