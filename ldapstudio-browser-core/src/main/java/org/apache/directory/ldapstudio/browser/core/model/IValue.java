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


import java.io.Serializable;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.AttributePropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.ConnectionPropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.EntryPropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.ValuePropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * A wrapper for raw LDAP values.
 */
public interface IValue extends Serializable, IAdaptable, ValuePropertyPageProvider, AttributePropertyPageProvider,
    EntryPropertyPageProvider, ConnectionPropertyPageProvider
{

    interface EmptyValue
    {
        public String toString();


        public String getStringValue();


        public byte[] getBinaryValue();


        public boolean isString();


        public boolean isBinary();
    }

    /**
     * This object represents the empty string value.
     */
    public static final EmptyValue EMPTY_STRING_VALUE = new EmptyValue()
    {
        public String toString()
        {
            return BrowserCoreMessages.model__empty_string_value;
        }


        public boolean isString()
        {
            return true;
        }


        public boolean isBinary()
        {
            return false;
        }


        public byte[] getBinaryValue()
        {
            return new byte[0];
        }


        public String getStringValue()
        {
            return ""; //$NON-NLS-1$
        }
    };

    /**
     * This object represents the empty binary value.
     */
    public static final EmptyValue EMPTY_BINARY_VALUE = new EmptyValue()
    {
        public String toString()
        {
            return BrowserCoreMessages.model__empty_binary_value;
        }


        public boolean isString()
        {
            return false;
        }


        public boolean isBinary()
        {
            return true;
        }


        public byte[] getBinaryValue()
        {
            return new byte[0];
        }


        public String getStringValue()
        {
            return ""; //$NON-NLS-1$
        }
    };


    /**
     * The attribute of this value.
     * 
     * @return The attribute of this value, never null.
     */
    public abstract IAttribute getAttribute();


    /**
     * Returns the raw value or an EmptyValue
     * 
     * @return The raw value or an EmptyValue, never null.
     */
    public abstract Object getRawValue();


    /**
     * Returns the String value of this value.
     * 
     * @return the String value
     */
    public abstract String getStringValue();


    /**
     * Returns the binary value of this value.
     * 
     * @return the binary value
     */
    public abstract byte[] getBinaryValue();


    /**
     * Return true if the value is empty.
     * 
     * @return true if the value is the empty.
     */
    public abstract boolean isEmpty();


    /**
     * Convinience method to getAttribute().isString().
     * 
     * @return true if the values attribute is string.
     */
    public abstract boolean isString();


    /**
     * Convinience method to getAttribute().isBinary()
     * 
     * @return true if the values attribute is binary.
     */
    public abstract boolean isBinary();


    /**
     * Returns true if this value is part of its entries RDN.
     * 
     * @return true if this value is part of its entries RDN.
     */
    public abstract boolean isRdnPart();


    /**
     * Return true if the argument is also of type IValue and they are
     * equal.
     * 
     * IValues are equal if there entries, there attributes and there raw
     * values are equal.
     * 
     * @param o
     *                The value to compare, must be of type IValue
     * @return true if the argument is equal to this.
     */
    public abstract boolean equals( Object o );

}
