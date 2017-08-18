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

import org.apache.directory.api.util.FileUtils;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.StudioKeyStoreManager;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.dialogs.CertificateInfoDialog;
import org.apache.directory.studio.connection.ui.wizards.ExportCertificateWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
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
 * to add, delete and view certificates :
 * 
 * <pre>
 * +-------------------------------------------------+
 * | +------------------------------------+          |
 * | | abc                                | (View)   |
 * | | xyz                                | (Add)    |
 * | |                                    | (Remove) |
 * | |                                    | (Export) |
 * | +------------------------------------+          |
 * +-------------------------------------------------+
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertificateListComposite extends Composite
{
    /** The KeyStore wrapper */
    private StudioKeyStoreManager keyStoreManager;

    /** The table containing the KeyStore elements */
    private TableViewer tableViewer;

    /** The View action button */
    private Button viewButton;

    /** The Add action button */
    private Button addButton;

    /** The Remove action button */
    private Button removeButton;

    /** The Export action button */
    private Button exportButton;

    /**
     * The listener called when the table selection has changed
     */
    private ISelectionChangedListener tableViewerSelectionListener = event -> {
        // Enable and disable the button accordingly to the selection :
        // - 1 line selected : enable remove, view and export
        // - N lines selected : enable remove, disable view and export
        // - nothing selected, disable view, remove and exprt
        viewButton.setEnabled( ( ( IStructuredSelection ) event.getSelection() ).size() == 1 );
        removeButton.setEnabled( !event.getSelection().isEmpty() );
        exportButton.setEnabled( ( ( IStructuredSelection ) event.getSelection() ).size() == 1 );
    };

    /**
     * The listener called when a line is double-clicked in the table : we will
     * open the Certificate dialog.
     */
    private IDoubleClickListener tableViewerDoubleClickListener = event -> openCertificate( event.getSelection() );

    /**
     * A selection listener on the View button : we will open the Certificate Dialog
     */
    private SelectionAdapter viewButtonSelectionListener = new SelectionAdapter()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            openCertificate( tableViewer.getSelection() );
        }
    };

    /**
     * A selection listener on the Add button : we will open the File Dialog
     * and let the user select the KeyStore location to add in the table
     */
    private SelectionAdapter addButtonSelectionListener = new SelectionAdapter()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected( SelectionEvent event )
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
                        NLS.bind( Messages.getString( "CertificateListComposite.ErrorDialogMessage" ), //$NON-NLS-1$
                            ex.getMessage() ) );
                }
            }
        }
    };

    /**
     * A selection listener on the Remove button.
     */
    private SelectionAdapter removeButtonSelectionListener = new SelectionAdapter()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected( SelectionEvent event )
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
    };

    /**
     * A selection listener on the Export button. We will open the Export wizard.
     */
    private SelectionAdapter exportButtonSelectionListener = new SelectionAdapter()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            X509Certificate certificate = ( X509Certificate ) ( ( IStructuredSelection ) tableViewer.getSelection() )
                .getFirstElement();

            WizardDialog dialog = new WizardDialog( getShell(), new ExportCertificateWizard( certificate ) );
            dialog.open();
        }
    };


    /**
     * Creates a new instance of CertificateInfoComposite.
     *
     * @param parent The paren'ts composite
     * @param style The widget's style
     */
    public CertificateListComposite( Composite parent, int style )
    {
        super( parent, style );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout( layout );
        setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        Composite container = new Composite( this, SWT.NONE );
        layout = new GridLayout( 2, false );
        container.setLayout( layout );
        container.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        // Create the Table Viewer
        tableViewer = new TableViewer( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.widthHint = 360;
        gridData.heightHint = 10;
        tableViewer.getTable().setLayoutData( gridData );
        tableViewer.setContentProvider( new KeyStoreContentProvider() );
        tableViewer.setLabelProvider( new KeyStoreLabelProvider() );
        tableViewer.addSelectionChangedListener( tableViewerSelectionListener );
        tableViewer.addDoubleClickListener( tableViewerDoubleClickListener );

        createButtons( container );
    }


    /**
     * Creates the 4 buttons for this widget :
     * 
     * <pre>
     * (view)
     * (Add)
     * (Remove)
     * (Export)
     * </pre>
     */
    private void createButtons( Composite container )
    {
        Composite buttonContainer = BaseWidgetUtils.createColumnContainer( container, 1, 1 );
        buttonContainer.setLayoutData( new GridData( GridData.FILL, GridData.FILL, false, false ) );

        // The View Button 
        viewButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.ViewButton" ), 1 );//$NON-NLS-1$

        viewButton.setEnabled( false );
        viewButton.addSelectionListener( viewButtonSelectionListener );

        // The Add button
        addButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.AddButton" ), 1 ); //$NON-NLS-1$
        addButton.addSelectionListener( addButtonSelectionListener );

        // The remove button
        removeButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.RemoveButton" ), 1 ); //$NON-NLS-1$
        removeButton.setEnabled( false );
        removeButton.addSelectionListener( removeButtonSelectionListener );

        // The export button
        exportButton = BaseWidgetUtils.createButton( buttonContainer, Messages
            .getString( "CertificateListComposite.ExportButton" ), 1 ); //$NON-NLS-1$
        exportButton.setEnabled( false );
        exportButton.addSelectionListener( exportButtonSelectionListener );
    }


    /**
     * Creates a certificate from a byte[].
     */
    private static X509Certificate generateCertificate( byte[] data ) throws CertificateException
    {
        CertificateFactory certificateFactory = CertificateFactory.getInstance( "X.509" ); //$NON-NLS-1$
        Certificate certificate = certificateFactory.generateCertificate( new ByteArrayInputStream( data ) );

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

    /**
     * This class is used to give back the content of teh Table viewer
     */
    private class KeyStoreContentProvider implements IStructuredContentProvider
    {
        /**
         * Gets the list of Certificates stored into the selected KeyStore
         * {@inheritDoc}
         */
        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof StudioKeyStoreManager )
            {
                try
                {
                    return ( ( StudioKeyStoreManager ) inputElement ).getCertificates();
                }
                catch ( CertificateException e )
                {
                    throw new RuntimeException( e );
                }
            }

            return new Object[]
                {};
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose()
        {
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

    }

    /**
     * This helper class is used to decorate the elements in the table : we use the certificate name if any.
     * @author elecharny
     *
     */
    class KeyStoreLabelProvider extends LabelProvider
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getText( Object element )
        {
            if ( element instanceof X509Certificate )
            {
                X509Certificate certificate = ( X509Certificate ) element;

                String certificateName = certificate.getSubjectX500Principal().getName();

                if ( Strings.isEmpty( certificateName ) )
                {
                    return Messages.getString( "CertificateListComposite.UntitledCertificate" ); //$NON-NLS-1$
                }
                else
                {
                    return certificateName;
                }
            }

            return super.getText( element );
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public Image getImage( Object element )
        {
            if ( element instanceof X509Certificate )
            {
                return ConnectionUIPlugin.getDefault().getImage( ConnectionUIConstants.IMG_CERTIFICATE );
            }

            return super.getImage( element );
        }
    }


    /**
     * A private method that opens the Certificate Dialog
     * @param selection
     */
    private void openCertificate( ISelection selection )
    {
        IStructuredSelection structuredSelection = ( IStructuredSelection ) selection;
        X509Certificate certificate = ( X509Certificate ) structuredSelection.getFirstElement();
        new CertificateInfoDialog( getShell(), new X509Certificate[]
            { certificate } ).open();
    }
}