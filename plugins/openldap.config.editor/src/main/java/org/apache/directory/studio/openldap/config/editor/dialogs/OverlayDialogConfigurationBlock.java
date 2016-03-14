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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;


/**
 * This interface represents a configuration block for Overlay Dialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface OverlayDialogConfigurationBlock<Overlay extends OlcOverlayConfig>
{
    /**
     * Creates the block content.
     *
     * @param parent the parent composite
     */
    void createBlockContent( Composite parent );


    /**
     * Gets the dialog.
     * 
     * @return the dialog
     */
    OverlayDialog getDialog();


    /**
     * Gets the overlay.
     *
     * @return the overlay
     */
    Overlay getOverlay();


    /**
     * Refreshes the UI based on the input.
     */
    void refresh();


    /**
     * Saves the data to the overlay.
     */
    void save();


    /**
     * Sets the dialog.
     * 
     * @param dialog the dialog
     */
    void setDialog( OverlayDialog dialog );


    /**
     * Sets the overlay.
     *
     * @param overlay the overlay
     */
    void setOverlay( Overlay overlay );
}
