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

package org.apache.directory.ldapstudio.schemas.view.viewers.wrappers;


import org.apache.directory.ldapstudio.schemas.model.ObjectClass;


/**
 * Specific Object Class Wrapper for the Schemas View
 */
public class PoolManagerObjectClassWrapper extends ObjectClassWrapper
{

    /**
     * Default constructor
     * @param myObjectClass
     * @param parent
     */
    public PoolManagerObjectClassWrapper( ObjectClass myObjectClass, IntermediateNode parent )
    {
        super( myObjectClass, parent );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper#getDisplayName()
     */
    public String getDisplayName()
    {
        return getNames()[0];
    }
}
