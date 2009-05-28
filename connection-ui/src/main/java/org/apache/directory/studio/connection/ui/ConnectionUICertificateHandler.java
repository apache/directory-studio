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

package org.apache.directory.studio.connection.ui;


import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.directory.studio.connection.core.ICertificateHandler;
import org.apache.directory.studio.connection.ui.dialogs.CertificateTrustDialog;
import org.eclipse.ui.PlatformUI;


/**
 * Default implementation of {@link ICertificateHandler}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionUICertificateHandler implements ICertificateHandler
{

    /**
     * {@inheritDoc}
     */
    public TrustLevel verifyTrustLevel( final String host, final X509Certificate[] certChain,
        final List<ICertificateHandler.FailCause> failCauses )
    {
        // open dialog
        final TrustLevel[] trustLevel = new TrustLevel[1];
        PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
        {
            public void run()
            {
                CertificateTrustDialog dialog = new CertificateTrustDialog( PlatformUI.getWorkbench().getDisplay()
                    .getActiveShell(), host, certChain, failCauses );
                dialog.open();
                trustLevel[0] = dialog.getTrustLevel();
            }
        } );

        return trustLevel[0];
    }

}
