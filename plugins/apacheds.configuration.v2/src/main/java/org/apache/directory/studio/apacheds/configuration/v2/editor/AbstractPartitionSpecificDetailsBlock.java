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
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;


/**
 * This interface represents a block for Partition configuration.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractPartitionSpecificDetailsBlock<P extends PartitionBean> implements
    PartitionSpecificDetailsBlock
{
    /** The details page*/
    protected PartitionDetailsPage detailsPage;

    /** The partition */
    protected P partition;

    // Listeners
    protected ModifyListener dirtyModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            detailsPage.setEditorDirty();
        }
    };
    protected WidgetModifyListener dirtyWidgetModifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            detailsPage.setEditorDirty();
        }
    };
    protected SelectionListener dirtySelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            detailsPage.setEditorDirty();
        }
    };


    /**
     * Creates a new instance of AbstractPartitionSpecificDetailsBlock.
     *
     * @param detailsPage the details page
     * @param partition the partition
     */
    public AbstractPartitionSpecificDetailsBlock( PartitionDetailsPage detailsPage, P partition )
    {
        this.detailsPage = detailsPage;
        this.partition = partition;
    }


    /**
     * {@inheritDoc}
     */
    public PartitionDetailsPage getDetailsPage()
    {
        return detailsPage;
    }
}