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

package org.apache.directory.ldapstudio.browser.core.model;


import java.util.Arrays;
import java.util.Iterator;


public class AttributeHierachie
{

    private IEntry entry;

    private String attributeDescription;

    private IAttribute[] attributes;


    public AttributeHierachie( IEntry entry, String attributeDescription, IAttribute[] attributes )
    {
        if ( entry == null || attributeDescription == null || attributes == null || attributes.length < 1
            || attributes[0] == null )
        {
            throw new IllegalArgumentException( "Empty AttributeHierachie" );
        }
        this.entry = entry;
        this.attributeDescription = attributeDescription;
        this.attributes = attributes;
    }


    public IAttribute[] getAttributes()
    {
        return attributes;
    }


    public boolean contains( IAttribute att )
    {
        return Arrays.asList( attributes ).contains( att );
    }


    public Iterator iterator()
    {
        return Arrays.asList( attributes ).iterator();
    }


    public IAttribute getAttribute()
    {
        return attributes[0];
    }


    public int size()
    {
        return attributes.length;
    }


    public String getAttributeDescription()
    {
        return attributeDescription;
    }


    public IEntry getEntry()
    {
        return entry;
    }

}
