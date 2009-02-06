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

package org.apache.directory.studio.connection.ui.widgets;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * The ConnectionLabelProvider represents the label provider for
 * the connection widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionLabelProvider extends LabelProvider
{

    /**
     * {@inheritDoc}
     * 
     * This implementation returns the connection name and appends information
     * about the used encryption method.
     */
    public String getText( Object obj )
    {
        if ( obj instanceof ConnectionFolder )
        {
            ConnectionFolder folder = ( ConnectionFolder ) obj;
            return folder.getName();
        }
        if ( obj instanceof Connection )
        {
            Connection conn = ( Connection ) obj;
            if ( conn.getEncryptionMethod() == EncryptionMethod.LDAPS )
            {
                return conn.getName() + " (LDAPS)"; //$NON-NLS-1$
            }
            else if ( conn.getEncryptionMethod() == EncryptionMethod.START_TLS )
            {
                return conn.getName() + " (StartTLS)"; //$NON-NLS-1$
            }
            else
            {
                return conn.getName();
            }
        }
        else if ( obj != null )
        {
            return obj.toString();
        }
        else
        {
            return ""; //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns a icon for connected or disconnected state.
     */
    public Image getImage( Object obj )
    {
        if ( obj instanceof ConnectionFolder )
        {
            return ConnectionUIPlugin.getDefault().getImage( ConnectionUIConstants.IMG_CONNECTION_FOLDER );
        }
        else if ( obj instanceof Connection )
        {
            Connection conn = ( Connection ) obj;
            if ( ( conn.getEncryptionMethod() == EncryptionMethod.LDAPS )
                || ( conn.getEncryptionMethod() == EncryptionMethod.START_TLS ) )
            {
                return conn.getJNDIConnectionWrapper().isConnected() ? ConnectionUIPlugin.getDefault().getImage(
                    ConnectionUIConstants.IMG_CONNECTION_SSL_CONNECTED ) : ConnectionUIPlugin.getDefault().getImage(
                    ConnectionUIConstants.IMG_CONNECTION_SSL_DISCONNECTED );
            }
            else
            {
                return conn.getJNDIConnectionWrapper().isConnected() ? ConnectionUIPlugin.getDefault().getImage(
                    ConnectionUIConstants.IMG_CONNECTION_CONNECTED ) : ConnectionUIPlugin.getDefault().getImage(
                    ConnectionUIConstants.IMG_CONNECTION_DISCONNECTED );
            }
        }
        else
        {
            return null;
        }
    }

}