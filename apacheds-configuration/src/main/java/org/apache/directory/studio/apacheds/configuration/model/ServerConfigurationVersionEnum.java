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
package org.apache.directory.studio.apacheds.configuration.model;


/**
 * This enum contains all the versions supported by the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum ServerConfigurationVersionEnum
{
    /** Version 1.5.7 */
    VERSION_1_5_7
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version157" ); //$NON-NLS-1$
        }
    },
    /** Version 1.5.6 */
    VERSION_1_5_6
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version156" ); //$NON-NLS-1$
        }
    },
    /** Version 1.5.5 */
    VERSION_1_5_5
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version155" ); //$NON-NLS-1$
        }
    },
    /** Version 1.5.4 */
    VERSION_1_5_4
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version154" ); //$NON-NLS-1$
        }
    },

    /** Version 1.5.3 */
    VERSION_1_5_3
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version153" ); //$NON-NLS-1$
        }
    },

    /** Version 1.5.2 */
    VERSION_1_5_2
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version152" ); //$NON-NLS-1$
        }
    },

    /** Version 1.5.1 */
    VERSION_1_5_1
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version151" ); //$NON-NLS-1$
        }
    },

    /** Version 1.5.0 */
    VERSION_1_5_0
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return Messages.getString( "ServerConfigurationVersionEnum.Version150" ); //$NON-NLS-1$
        }
    }
}
