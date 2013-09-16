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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * This class implements a specific details block for the JDBM partition.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class JdbmPartitionSpecificDetailsBlock extends AbstractPartitionSpecificDetailsBlock<JdbmPartitionBean>
{
    // UI widgets
    private Text cacheSizeText;
    private Button enableOptimizerCheckbox;


    /**
     * Creates a new instance of JdbmPartitionSpecificDetailsBlock.
     *
     * @param detailsPage the details page
     * @param partition the partition
     */
    public JdbmPartitionSpecificDetailsBlock( PartitionDetailsPage detailsPage, JdbmPartitionBean partition )
    {
        super( detailsPage, partition );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createBlockContent( Composite parent, FormToolkit toolkit )
    {
        // Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Cache Size
        toolkit.createLabel( composite, Messages.getString( "PartitionDetailsPage.CacheSize" ) ); //$NON-NLS-1$
        cacheSizeText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        cacheSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        cacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Enable Optimizer
        enableOptimizerCheckbox = toolkit.createButton( composite,
            Messages.getString( "PartitionDetailsPage.EnableOptimzer" ), SWT.CHECK ); //$NON-NLS-1$
        enableOptimizerCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        return composite;
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        cacheSizeText.addModifyListener( dirtyModifyListener );
        enableOptimizerCheckbox.addSelectionListener( dirtySelectionListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        cacheSizeText.removeModifyListener( dirtyModifyListener );
        enableOptimizerCheckbox.removeSelectionListener( dirtySelectionListener );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( partition != null )
        {
            // Cache Size
            cacheSizeText.setText( "" + partition.getPartitionCacheSize() ); //$NON-NLS-1$

            // Enable Optimizer
            enableOptimizerCheckbox.setSelection( partition.isJdbmPartitionOptimizerEnabled() );
        }

        addListeners();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( partition != null )
        {
            // Cache Size
            try
            {
                partition.setPartitionCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
            }
            catch ( NumberFormatException nfe )
            {
                // Nothing to do
            }

            // Enable Optimizer
            partition.setJdbmPartitionOptimizerEnabled( enableOptimizerCheckbox.getSelection() );
        }
    }
}