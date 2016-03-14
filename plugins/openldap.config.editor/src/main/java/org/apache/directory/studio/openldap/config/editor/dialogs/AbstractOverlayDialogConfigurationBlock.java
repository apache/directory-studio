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


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;

import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;


/**
 * This interface represents a block for overlay configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractOverlayDialogConfigurationBlock<Overlay extends OlcOverlayConfig> implements
    OverlayDialogConfigurationBlock<Overlay>
{
    /** The dialog */
    protected OverlayDialog dialog;

    /** The overlay */
    protected Overlay overlay;

    /** The connection */
    protected IBrowserConnection browserConnection;


    /**
     * Creates a new instance of AbstractOverlayConfigurationBlock.
     *
     * @param dialog the dialog
     */
    public AbstractOverlayDialogConfigurationBlock( OverlayDialog dialog )
    {
        this.dialog = dialog;
    }


    /**
     * Creates a new instance of AbstractOverlayConfigurationBlock.
     *
     * @param dialog the dialog
     * @param browserConnection the connection
     */
    public AbstractOverlayDialogConfigurationBlock( OverlayDialog dialog, IBrowserConnection browserConnection )
    {
        this.dialog = dialog;
        this.browserConnection = browserConnection;
    }


    /**
     * {@inheritDoc}
     */
    public OverlayDialog getDialog()
    {
        return dialog;
    }


    /**
     * {@inheritDoc}
     */
    public Overlay getOverlay()
    {
        return overlay;
    }


    /**
     * {@inheritDoc}
     */
    public void setDialog( OverlayDialog dialog )
    {
        this.dialog = dialog;
    }


    /**
     * {@inheritDoc}
     */
    public void setOverlay( Overlay overlay )
    {
        this.overlay = overlay;
    }
}
