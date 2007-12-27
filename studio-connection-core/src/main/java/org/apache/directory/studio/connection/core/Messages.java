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

package org.apache.directory.studio.connection.core;


import org.eclipse.osgi.util.NLS;


/**
 * This class contains most of the Strings used by the Plugin
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.apache.directory.studio.connection.core.messages"; //$NON-NLS-1$


    /**
     * Creates a new instance of Messages.
     */
    private Messages()
    {
    }

    static
    {
        // initialize resource bundle
        NLS.initializeMessages( BUNDLE_NAME, Messages.class );
    }

    public static String copy_n_of_s;

    public static String model__no_auth_handler;
    public static String model__no_credentials;

    public static String jobs__error_occurred;
    public static String jobs__progressmonitor_check_cancellation;
    public static String jobs__progressmonitor_report_progress;

    public static String jobs__check_bind_name;
    public static String jobs__check_bind_task;
    public static String jobs__check_bind_error;
    public static String jobs__check_network_name;
    public static String jobs__check_network_task;
    public static String jobs__check_network_error;

    public static String jobs__open_connections_name_1;
    public static String jobs__open_connections_name_n;
    public static String jobs__open_connections_task;
    public static String jobs__open_connections_error_1;
    public static String jobs__open_connections_error_n;

    public static String jobs__close_connections_name_1;
    public static String jobs__close_connections_name_n;
    public static String jobs__close_connections_task;
    public static String jobs__close_connections_error_1;
    public static String jobs__close_connections_error_n;

}
