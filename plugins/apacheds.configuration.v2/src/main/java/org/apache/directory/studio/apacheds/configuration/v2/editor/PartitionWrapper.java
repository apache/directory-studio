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


import org.apache.directory.server.config.beans.PartitionBean;


/**
 * This class defines a simple wrapper for a partition.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionWrapper
{
    /** The wrapped partition */
    private PartitionBean partition;


    /**
     * Creates a new instance of PartitionWrapper.
     *
     * @param partition the partition
     */
    public PartitionWrapper( PartitionBean partition )
    {
        this.partition = partition;
    }


    /**
     * Gets the partition.
     *
     * @return the partition
     */
    public PartitionBean getPartition()
    {
        return partition;
    }


    /**
     * Sets the partition.
     *
     * @param partition the partition
     */
    public void setPartition( PartitionBean partition )
    {
        this.partition = partition;
    }
}
