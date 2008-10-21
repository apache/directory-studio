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
package org.apache.directory.studio.aciitemeditor;


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * The ACIItemValueContext is used to pass contextual
 * information to the opened ACIItemDialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemValueWithContext
{

    /** The connection, used to browse the directory. */
    private IBrowserConnection connection;

    /** The entry. */
    private IEntry entry;

    /** The ACI item. */
    private String aciItemValue;


    /**
     * Creates a new instance of ACIItemValueContext.
     * 
     * @param aciItemValue the ACI item value
     * @param connection the connection
     * @param entry the entry
     */
    public ACIItemValueWithContext( IBrowserConnection connection, IEntry entry, String aciItemValue )
    {
        this.connection = connection;
        this.entry = entry;
        this.aciItemValue = aciItemValue;
    }


    /**
     * Gets the aci item value.
     * 
     * @return the aciItemValue
     */
    public String getACIItemValue()
    {
        return aciItemValue;
    }


    /**
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return connection;
    }


    /**
     * @return the entry
     */
    public IEntry getEntry()
    {
        return entry;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return aciItemValue == null ? "" : aciItemValue; //$NON-NLS-1$
    }

}