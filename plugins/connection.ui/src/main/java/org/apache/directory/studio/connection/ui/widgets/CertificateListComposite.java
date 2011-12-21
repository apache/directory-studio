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


import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.StudioKeyStoreManager;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.dialogs.CertificateInfoDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;


/**
 * This composite displays a list of certificates and buttons
 * to add, delete and view certificates.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertificateListComposite extends Composite
{
    private StudioKeyStoreManager keyStoreManager;
    private Composite container;
    private TableViewer tableViewer;
    private Button viewButton;
    private Button addButton;
    private Button removeButton;


    /**
     * Creates a new instance of CertificateInfoComposite.
     *
     * @param parent
     * @param style
     */
    public CertificateListComposite( Composite parent, int style )
    {
        super( parent, style );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout( layout );
        setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        container = new Composite( this, SWT.NONE );
        layout = new GridLayout( 2, false );
        container.setLayout( layout );
        container.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        createTreeViewer();
        createButtons();
    }


    private void createTreeViewer()
    {
        tableViewer = new TableViewer( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData gd = new GridData( GridData.FILL, GridData.FILL, true, true );
        gd.widthHint = 360;
        gd.heightHint = 10;
        tableViewer.getTable().setLayoutData( gd );
        tableViewer.setContentProvider( new KeyStoreContentProvider() );
        tableViewer.setLabelProvider( new KeyStoreLabelProvider() );
        tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                viewButton.setEnabled( !event.getSelection().isEmpty() );
                removeButton.setEnabled( !event.getSelection().isEmpty() );
            }
        } );
        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                IStructuredSelection selection = ( IStructuredSelection ) event.getSelection();
                X509Certificate certificate = ( X509Certificate ) selection.getFirstElement();
                new CertificateInfoDialog( getShell(), new X509Certificate[]
                    { certificate } ).open();
            }
        } );
    }


    private void createButtons()
    {
        Composite buttonContainer = BaseWidgetUtils.createColumnContainer( container, 1, 1 );
        buttonContainer.setLayoutData( new GridData( GridData.FILL, GridData.FILL, false, false ) );
        viewButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.ViewButton" ), 1 );//$NON-NLS-1$
        viewButton.setEnabled( false );
        viewButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                IStructuredSelection selection = ( IStructuredSelection ) tableViewer.getSelection();
                X509Certificate certificate = ( X509Certificate ) selection.getFirstElement();
                new CertificateInfoDialog( getShell(), new X509Certificate[]
                    { certificate } ).open();
            }
        } );

        addButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.AddButton" ), 1 ); //$NON-NLS-1$
        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                // Asking the user for the certificate file
                FileDialog dialog = new FileDialog( getShell(), SWT.OPEN );
                dialog.setText( Messages.getString( "CertificateListComposite.LoadCertificate" ) ); //$NON-NLS-1$
                String returnedFileName = dialog.open();

                if ( returnedFileName != null )
                {
                    try
                    {
                        // Reading the certificate
                        X509Certificate certificate = generateCertificate( FileUtils.readFileToByteArray( new File(
                            returnedFileName ) ) );

                        // Adding the certificate
                        keyStoreManager.addCertificate( certificate );

                        // Refreshing the table viewer
                        tableViewer.refresh();
                        tableViewer.setSelection( new StructuredSelection( certificate ) );
                    }
                    catch ( Exception ex )
                    {
                        MessageDialog.openError( addButton.getShell(),
                            Messages.getString( "CertificateListComposite.ErrorDialogTitle" ), //$NON-NLS-1$
                            NLS.bind( Messages.getString( "CertificateListComposite.ErrorDialogMessage" ),
                                ex.getMessage() ) );
                    }
                }
            };
        } );

        removeButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.RemoveButton" ), 1 ); //$NON-NLS-1$
        removeButton.setEnabled( false );
        removeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                IStructuredSelection selection = ( IStructuredSelection ) tableViewer.getSelection();
                Iterator<?> iterator = selection.iterator();
                while ( iterator.hasNext() )
                {
                    X509Certificate certificate = ( X509Certificate ) iterator.next();
                    try
                    {
                        keyStoreManager.removeCertificate( certificate );
                    }
                    catch ( CertificateException ce )
                    {
                        throw new RuntimeException( ce );
                    }
                }
                tableViewer.refresh();
            }
        } );
    }


    private static X509Certificate generateCertificate( byte[] data ) throws CertificateException
    {
        CertificateFactory cf = CertificateFactory.getInstance( "X.509" ); //$NON-NLS-1$
        Certificate certificate = cf.generateCertificate( new ByteArrayInputStream( data ) );
        if ( certificate instanceof X509Certificate )
        {
            return ( X509Certificate ) certificate;
        }

        return null;
    }


    /**
     * Sets the input for this composite.
     * 
     * @param keyStoreManager the key store manager
     */
    public void setInput( StudioKeyStoreManager keyStoreManager )
    {
        this.keyStoreManager = keyStoreManager;
        tableViewer.setInput( keyStoreManager );
    }

    class KeyStoreContentProvider implements IStructuredContentProvider
    {

        /**
         * {@inheritDoc}
         */
        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof StudioKeyStoreManager )
            {
                StudioKeyStoreManager keyStoreManager = ( StudioKeyStoreManager ) inputElement;
                try
                {
                    return keyStoreManager.getCertificates();
                }
                catch ( CertificateException e )
                {
                    throw new RuntimeException( e );
                }
            }

            return null;
        }


        /**
         * {@inheritDoc}
         */
        public void dispose()
        {
        }


        /**
         * {@inheritDoc}
         */
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

    }

    class KeyStoreLabelProvider extends LabelProvider
    {
        public String getText( Object element )
        {
            if ( element instanceof X509Certificate )
            {
                X509Certificate certificate = ( X509Certificate ) element;

                String certificateName = certificate.getSubjectX500Principal().getName();
                if ( ( certificateName != null ) && ( !"".equals( certificateName ) ) )
                {
                    return certificateName;
                }
                else
                {
                    return Messages.getString( "CertificateListComposite.UntitledCertificate" ); //$NON-NLS-1$
                }
            }

            return super.getText( element );
        }


        public Image getImage( Object element )
        {
            if ( element instanceof X509Certificate )
            {
                return ConnectionUIPlugin.getDefault().getImage( ConnectionUIConstants.IMG_CERTIFICATE );
            }

            return super.getImage( element );
        }
    }
}
