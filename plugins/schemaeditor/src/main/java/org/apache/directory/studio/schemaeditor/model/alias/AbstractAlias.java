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
package org.apache.directory.studio.schemaeditor.model.alias;


/**
 * Abstract class for the {@link Alias} interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractAlias implements Alias
{
    /** The alias */
    private String alias;


    /**
     * Creates a new instance of AbstractAlias.
     *
     * @param alias
     *      the alias
     */
    public AbstractAlias( String alias )
    {
        this.alias = alias;
    }


    /**
     * {@inheritDoc}
     */
    public String getAlias()
    {
        return alias;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return alias;
    }
}
