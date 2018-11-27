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
package org.apache.directory.studio.apacheds.configuration.editor;


import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.core.partition.impl.btree.AbstractBTreePartition;
import org.apache.directory.server.core.partition.ldif.AbstractLdifPartition;


/**
 * Configuration of the server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Configuration
{

    private ConfigBean configBean;
    private AbstractBTreePartition configPartition;


    public Configuration( ConfigBean configBean, AbstractBTreePartition configPartition )
    {
        this.configBean = configBean;
        this.configPartition = configPartition;
    }


    public ConfigBean getConfigBean()
    {
        return configBean;
    }


    public void setConfigBean( ConfigBean configBean )
    {
        this.configBean = configBean;
    }


    public AbstractBTreePartition getConfigPartition()
    {
        return configPartition;
    }


    public void setConfigPartition( AbstractLdifPartition configPartition )
    {
        this.configPartition = configPartition;
    }

}
