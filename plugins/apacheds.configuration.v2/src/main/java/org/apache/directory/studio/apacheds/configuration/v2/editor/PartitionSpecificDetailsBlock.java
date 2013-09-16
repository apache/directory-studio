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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;



/**
 * This interface represents a block for Partition Specific Details.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface PartitionSpecificDetailsBlock
{
    /**
     * Creates the block content.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    public Composite createBlockContent( Composite parent, FormToolkit toolkit );


    /**
     * Gets the associated details page.
     *
     * @return the associated details page.
     */
    public PartitionDetailsPage getDetailsPage();


    /**
     * Refreshes the UI based on the input.
     */
    public void refresh();


    /**
     * If part is displaying information loaded from a model, this method
     * instructs it to commit the new (modified) data back into the model.
     * 
     * @param onSave
     *            indicates if commit is called during 'save' operation or for
     *            some other reason (for example, if form is contained in a
     *            wizard or a multi-page editor and the user is about to leave
     *            the page).
     */
    void commit( boolean onSave );
}