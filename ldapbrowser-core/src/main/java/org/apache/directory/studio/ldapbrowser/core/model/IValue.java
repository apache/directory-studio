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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;

import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.AttributePropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.EntryPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.ValuePropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IValue represents a value of a LDAP attribute.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IValue extends Serializable, IAdaptable, ValuePropertyPageProvider, AttributePropertyPageProvider,
    EntryPropertyPageProvider, ConnectionPropertyPageProvider
{

    /**
     * EmptyValue is used to indicate an empty value.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    interface EmptyValue
    {

        /**
         * Gets the string value.
         *
         * @return the string value
         */
        public String getStringValue();


        /**
         * Gets the binary value.
         * 
         * @return the binary value
         */
        public byte[] getBinaryValue();


        /**
         * Checks if is string.
         * 
         * @return true, if is string
         */
        public boolean isString();


        /**
         * Checks if is binary.
         * 
         * @return true, if is binary
         */
        public boolean isBinary();
    }

    /**
     * This object represents the empty string value.
     */
    public static final EmptyValue EMPTY_STRING_VALUE = new EmptyValue()
    {

        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return BrowserCoreMessages.model__empty_string_value;
        }


        /**
         * {@inheritDoc}
         */
        public boolean isString()
        {
            return true;
        }


        /**
         * {@inheritDoc}
         */
        public boolean isBinary()
        {
            return false;
        }


        /**
         * {@inheritDoc}
         */
        public byte[] getBinaryValue()
        {
            return new byte[0];
        }


        /**
         * {@inheritDoc}
         */
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

        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return BrowserCoreMessages.model__empty_binary_value;
        }


        /**
         * {@inheritDoc}
         */
        public boolean isString()
        {
            return false;
        }


        /**
         * {@inheritDoc}
         */
        public boolean isBinary()
        {
            return true;
        }


        /**
         * {@inheritDoc}
         */
        public byte[] getBinaryValue()
        {
            return new byte[0];
        }


        /**
         * {@inheritDoc}
         */
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
     * Gets the raw value or an EmptyValue
     * 
     * @return The raw value or an EmptyValue, never null.
     */
    public abstract Object getRawValue();


    /**
     * Gets the string value of this value.
     * 
     * If the value is binary a String with UTF-8 decoded
     * byte[] is returned. 
     * 
     * @return the String value
     */
    public abstract String getStringValue();


    /**
     * Gets the binary value of this value.
     * 
     * If the value is string a byte[] with the 
     * UTF-8 encoded String is returned. 
     * 
     * @return the binary value
     */
    public abstract byte[] getBinaryValue();


    /**
     * Returns true if the value is empty.
     * 
     * A value is empty if its raw value is an EmptyValue.
     * 
     * @return true if the value is empty.
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
     * Returns true if this value is part of its entry's RDN.
     * 
     * @return true if this value is part of its entry's RDN.
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
