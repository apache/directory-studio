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
    // Static strings for dots
    private static final String THREE_DOTS = "..."; //$NON-NLS-1$
    private static final String TWO_DOTS = ".."; //$NON-NLS-1$
    private static final String ONE_DOT = "."; //$NON-NLS-1$

    /** The counter used for dots */
    private int dotsCount = 1;


    /**
     * {@inheritDoc}
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
                        return Messages.getString( "ServersViewLabelProvider.Started" ); //$NON-NLS-1$
                    case STARTING:
                        return Messages.getString( "ServersViewLabelProvider.Starting" ) + getDots(); //$NON-NLS-1$
                    case STOPPED:
                        return Messages.getString( "ServersViewLabelProvider.Stopped" ); //$NON-NLS-1$
                    case STOPPING:
                        return Messages.getString( "ServersViewLabelProvider.Stopping" ) + getDots(); //$NON-NLS-1$
                    case UNKNOWN:
                        return Messages.getString( "ServersViewLabelProvider.Unknown" ); //$NON-NLS-1$
                }
            }

        }

        return null;
    }


    /**
     * Gets the dotted string, based on the current dotsCount.
     *
     * @return
     *      the dotted string, based on the current dotsCount
     */
    private String getDots()
    {
        if ( dotsCount == 1 )
        {
            return ServersViewLabelProvider.ONE_DOT;
        }
        else if ( dotsCount == 2 )
        {
            return ServersViewLabelProvider.TWO_DOTS;
        }
        else
        {
            return ServersViewLabelProvider.THREE_DOTS;
        }
    }


    /**
     * {@inheritDoc}
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
                        switch ( dotsCount )
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
                        switch ( dotsCount )
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
        dotsCount++;

        if ( dotsCount > 3 )
        {
            dotsCount = 1;
        }
    }
}
