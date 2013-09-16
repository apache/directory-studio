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


import org.apache.directory.server.config.beans.MavibotPartitionBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * This class implements a specific details block for the Mavibot partition.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MavibotPartitionSpecificDetailsBlock extends AbstractPartitionSpecificDetailsBlock<MavibotPartitionBean>
{
    /**
     * Creates a new instance of MavibotPartitionSpecificDetailsBlock.
     *
     * @param detailsPage the details page
     * @param partition the partition
     */
    public MavibotPartitionSpecificDetailsBlock( PartitionDetailsPage detailsPage, MavibotPartitionBean partition )
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
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Information Label
        toolkit.createLabel( composite, "No specific settings for a Mavibot partition." );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        // Nothing to do
    }
}