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


import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.apache.directory.server.config.beans.MavibotPartitionBean;
import org.apache.directory.server.config.beans.PartitionBean;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum PartitionType
{
    /** The JDBM partition type */
    JDBM,

    /** The Mavibot partition type */
    MAVIBOT;

    /**
     * Gets the partition type.
     *
     * @param partition the partition
     * @return the type corresponding to the partition (or <code>null</code>)
     */
    public static PartitionType fromPartition( PartitionBean partition )
    {
        if ( partition instanceof JdbmPartitionBean )
        {
            return JDBM;
        }
        else if ( partition instanceof MavibotPartitionBean )
        {
            return MAVIBOT;
        }

        return null;
    }


    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    public String toString()
    {
        switch ( this )
        {
            case JDBM:
                return "JDBM";
            case MAVIBOT:
                return "Mavibot";
        }
        
        return super.toString();
    }
}
