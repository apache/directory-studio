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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import java.util.List;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.common.ui.widgets.FileBrowserWidget;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.model.overlay.OlcAuditlogConfig;


/**
 * This class implements a block for the configuration of the Audit Log overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AuditLogOverlayConfigurationBlock extends AbstractOverlayDialogConfigurationBlock<OlcAuditlogConfig>
{
    // UI widgets
    private FileBrowserWidget fileBrowserWidget;


    public AuditLogOverlayConfigurationBlock( OverlayDialog dialog )
    {
        super( dialog );
        setOverlay( new OlcAuditlogConfig() );
    }


    public AuditLogOverlayConfigurationBlock( OverlayDialog dialog, OlcAuditlogConfig overlay )
    {
        super( dialog );
        if ( overlay == null )
        {
            overlay = new OlcAuditlogConfig();
        }

        setOverlay( overlay );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        BaseWidgetUtils.createLabel( composite, "Log File:", 1 );

        fileBrowserWidget = new FileBrowserWidget( "", new String[]
            { ".ldif", ".log" }, FileBrowserWidget.TYPE_OPEN );
        fileBrowserWidget.createWidget( composite );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            List<String> auditLogFilesList = overlay.getOlcAuditlogFile();

            if ( auditLogFilesList != null && auditLogFilesList.size() > 0 )
            {
                fileBrowserWidget.setFilename( auditLogFilesList.get( 0 ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            overlay.clearOlcAuditlogFile();

            String filename = fileBrowserWidget.getFilename();

            if ( !Strings.isEmpty( filename ) )
            {
                overlay.addOlcAuditlogFile( filename );
            }
        }
    }
}
