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


import org.apache.directory.server.config.beans.PartitionBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionsPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = PartitionsPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "PartitionsPage.Partitions" ); //$NON-NLS-1$

    /** The Master Details Block */
    private PartitionsMasterDetailsBlock masterDetailsBlock;

    /** The label provider for partition table viewers */
    public static LabelProvider PARTITIONS_LABEL_PROVIDER = new LabelProvider()
    {
        public String getText( Object element )
        {
            if ( element instanceof PartitionBean )
            {
                PartitionBean partition = ( PartitionBean ) element;

                return NLS
                    .bind(
                        "{0} ({1}) [{2}]", new Object[] { partition.getPartitionId(), partition.getPartitionSuffix(), getPartitionType( partition ) } ); //$NON-NLS-1$
            }
            else if ( element instanceof PartitionWrapper )
            {
                return getText( ( ( PartitionWrapper ) element ).getPartition() );
            }

            return super.getText( element );
        }


        private String getPartitionType( PartitionBean partition )
        {
            PartitionType type = PartitionType.fromPartition( partition );

            if ( type != null )
            {
                return type.toString();
            }
            else
            {
                return "Unknown";
            }
        }


        public Image getImage( Object element )
        {
            if ( element instanceof PartitionBean )
            {
                PartitionBean partition = ( PartitionBean ) element;

                if ( isSystemPartition( partition ) )
                {
                    return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                        ApacheDS2ConfigurationPluginConstants.IMG_PARTITION_SYSTEM );
                }
                else
                {
                    return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                        ApacheDS2ConfigurationPluginConstants.IMG_PARTITION );
                }
            }
            else if ( element instanceof PartitionWrapper )
            {
                return getImage( ( ( PartitionWrapper ) element ).getPartition() );
            }

            return super.getImage( element );
        }
    };

    /** The comparator for partition table viewers */
    public static ViewerComparator PARTITIONS_COMPARATOR = new ViewerComparator()
    {
        public int compare( Viewer viewer, Object e1, Object e2 )
        {
            if ( ( e1 instanceof PartitionBean ) && ( e2 instanceof PartitionBean ) )
            {
                PartitionBean partition1 = ( PartitionBean ) e1;
                PartitionBean partition2 = ( PartitionBean ) e2;

                String partition1Id = partition1.getPartitionId();
                String partition2Id = partition2.getPartitionId();

                if ( ( partition1Id != null ) && ( partition2Id != null ) )
                {
                    return partition1Id.compareTo( partition2Id );
                }
            }
            if ( ( e1 instanceof PartitionWrapper ) && ( e2 instanceof PartitionWrapper ) )
            {
                return compare( viewer, ( ( PartitionWrapper ) e1 ).getPartition(),
                    ( ( PartitionWrapper ) e2 ).getPartition() );
            }

            return super.compare( viewer, e1, e2 );
        }
    };


    /**
     * Creates a new instance of PartitionsPage.
     *
     * @param editor
     *      the associated editor
     */
    public PartitionsPage( ServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        masterDetailsBlock = new PartitionsMasterDetailsBlock( this );
        masterDetailsBlock.createContent( getManagedForm() );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        if ( isInitialized() )
        {
            masterDetailsBlock.refreshUI();
        }
    }


    /**
     * Indicates if the given partition is the system partition.
     *
     * @param partition the partition
     * @return <code>true</code> if the partition is the system partition,
     *         <code>false</code> if not.
     */
    public static boolean isSystemPartition( PartitionBean partition )
    {
        if ( partition != null )
        {
            return "system".equalsIgnoreCase( partition.getPartitionId() ); //$NON-NLS-1$
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        if ( masterDetailsBlock != null )
        {
            masterDetailsBlock.doSave( monitor );
        }
    }
}
