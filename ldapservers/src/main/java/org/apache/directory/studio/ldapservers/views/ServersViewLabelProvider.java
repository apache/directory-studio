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
package org.apache.directory.studio.ldapservers.views;


import org.apache.directory.studio.ldapservers.LdapServersPlugin;
import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the label provider for the Servers view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServersViewLabelProvider extends LabelProvider implements ITableLabelProvider
{
    private static final String THREE_DOTS = "..."; //$NON-NLS-1$
    private static final String TWO_DOTS = ".."; //$NON-NLS-1$
    private static final String ONE_DOT = "."; //$NON-NLS-1$
    private int count = 1;


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText( Object element, int columnIndex )
    {
        if ( element instanceof LdapServer )
        {
            LdapServer server = ( LdapServer ) element;
            if ( columnIndex == 0 )
            {
                return server.getName();
            }
            else if ( columnIndex == 1 )
            {
                LdapServerStatus status = ( ( LdapServer ) element ).getStatus();
                switch ( status )
                {
                    case STARTED:
                        return "Started"; // TODO
                    case STARTING:
                        return "Starting" + getDots(); // TODO
                    case STOPPED:
                        return "Stopped"; // TODO
                    case STOPPING:
                        return "Stopping" + getDots(); // TODO
                    case UNKNOWN:
                        return "Unkown"; // TODO
                }
            }

        }

        return null;
    }


    private String getDots()
    {
        if ( count == 1 )
        {
            return ServersViewLabelProvider.ONE_DOT;
        }
        else if ( count == 2 )
        {
            return ServersViewLabelProvider.TWO_DOTS;
        }
        else
        {
            return ServersViewLabelProvider.THREE_DOTS;
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        if ( element instanceof LdapServer )
        {
            if ( columnIndex == 0 )
            {
                return LdapServersPlugin.getDefault().getImage( LdapServersPluginConstants.IMG_SERVER );
            }
            else if ( columnIndex == 1 )
            {
                switch ( ( ( LdapServer ) element ).getStatus() )
                {
                    case STARTED:
                        return LdapServersPlugin.getDefault().getImage( LdapServersPluginConstants.IMG_SERVER_STARTED );
                    case STARTING:
                        switch ( count )
                        {
                            case 1:
                                return LdapServersPlugin.getDefault().getImage(
                                    LdapServersPluginConstants.IMG_SERVER_STARTING1 );
                            case 2:
                                return LdapServersPlugin.getDefault().getImage(
                                    LdapServersPluginConstants.IMG_SERVER_STARTING2 );
                            case 3:
                                return LdapServersPlugin.getDefault().getImage(
                                    LdapServersPluginConstants.IMG_SERVER_STARTING3 );
                        }
                    case STOPPED:
                        return LdapServersPlugin.getDefault().getImage( LdapServersPluginConstants.IMG_SERVER_STOPPED );
                    case STOPPING:
                        switch ( count )
                        {
                            case 1:
                                return LdapServersPlugin.getDefault().getImage(
                                    LdapServersPluginConstants.IMG_SERVER_STOPPING1 );
                            case 2:
                                return LdapServersPlugin.getDefault().getImage(
                                    LdapServersPluginConstants.IMG_SERVER_STOPPING2 );
                            case 3:
                                return LdapServersPlugin.getDefault().getImage(
                                    LdapServersPluginConstants.IMG_SERVER_STOPPING3 );
                        }
                    case UNKNOWN:
                        return LdapServersPlugin.getDefault().getImage( LdapServersPluginConstants.IMG_SERVER );
                }
            }
        }

        return null;
    }


    /**
     * Increase the counter of the animation.
     */
    public void animate()
    {
        count++;

        if ( count > 3 )
        {
            count = 1;
        }
    }
}
