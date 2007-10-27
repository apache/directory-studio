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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Subschema;


/**
 * A ChildrenInfo is used to hold the list of attributes of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeInfo implements Serializable
{

    private static final long serialVersionUID = -298229262461058833L;

    /** The attributes initialized flag. */
    protected volatile boolean attributesInitialized = false;

    /** The attribute map. */
    protected volatile Map<String, IAttribute> attributeMap = new LinkedHashMap<String, IAttribute>();

    /** The subschema. */
    protected volatile Subschema subschema = null;


    /**
     * Creates a new instance of AttributeInfo.
     */
    public AttributeInfo()
    {
    }

}
