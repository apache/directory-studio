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

import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.AttributePropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IAttribute represents an LDAP attribute.
 */
public interface IAttribute extends Serializable, IAdaptable, AttributePropertyPageProvider, EntryPropertyPageProvider,
    ConnectionPropertyPageProvider
{

    /** The options delimiter ';' */
    public static final String OPTION_DELIMITER = ";"; //$NON-NLS-1$

    /** The language tag prefix 'lang-' */
    public static final String OPTION_LANG_PREFIX = "lang-"; //$NON-NLS-1$


    /**
     * Gets the entry of this attribute.
     * 
     * @return the entry of this attribute, never null
     */
    public abstract IEntry getEntry();


    /**
     * Returns true if this attribute is consistent. The following
     * conditions must be fulfilled:
     * 
     * <ul>
     * <li>The attribute must contain at least one value</li>
     * <li>The attribute mustn't contain any empty value</li>
     * </ul>
     * 
     * @return true if the attribute ist consistent
     */
    public abstract boolean isConsistent();


    /**
     * Returns true if this attribute is a must attribute of its entry
     * according to the schema and the entry's object classes.
     * 
     * @return true if this attribute is a must attribute of its entry.
     */
    public abstract boolean isMustAttribute();


    /**
     * Returns true if this attribute is a may attribute of its entry
     * according to the schema and the entry's object classes.
     * 
     * @return true if this attribute is a may attribute of its entry.
     */
    public abstract boolean isMayAttribute();


    /**
     * Returns true if this attribute is an operational attribute according
     * to the schema.
     * 
     * @return true if this attribute is an operational attribute.
     */
    public abstract boolean isOperationalAttribute();


    /**
     * Return true if this attribute is the objeCtclass attribute.
     * 
     * @return true if this attribute is the objectClass attribute.
     */
    public abstract boolean isObjectClassAttribute();


    /**
     * Return true if the attribute is of type String.
     * 
     * @return true if the attribute is of type String.
     */
    public abstract boolean isString();


    /**
     * Return true if the attribute is of type byte[].
     * 
     * @return true if the attribute is of type byte[].
     */
    public abstract boolean isBinary();


    /**
     * Adds an empty value.
     * 
     */
    public abstract void addEmptyValue();


    /**
     * Removes one empty value if one is present.
     * 
     */
    public abstract void deleteEmptyValue();


    /**
     * Adds the given value to this attribute. The value's attribute must be
     * this attribute.
     * 
     * @param valueToAdd
     *                the value to add
     * @throws IllegalArgumentException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void addValue( IValue valueToAdd ) throws IllegalArgumentException;


    /**
     * Deletes the given value from this attribute.
     * 
     * @param valueToDelete
     *                the value to delete
     * @throws IllegalArgumentException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void deleteValue( IValue valueToDelete ) throws IllegalArgumentException;


    /**
     * Replaces the old value with the new value.
     * 
     * @param oldValue
     *                the value that should be replaced
     * @param newValue
     *                the value that should be added
     * @throws IllegalArgumentException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void modifyValue( IValue oldValue, IValue newValue )
        throws IllegalArgumentException;


    /**
     * Gets the values of this attribute.
     * 
     * @return the values of this attribute, may be an empty array, never null.
     */
    public abstract IValue[] getValues();


    /**
     * Gets the number of values in this attribute.
     * 
     * @return the number of values in this attribute.
     */
    public abstract int getValueSize();


    /**
     * Gets the description of this attribute. The description 
     * consists of the attribute type and optional options.
     * 
     * @return the description of this attribute.
     */
    public abstract String getDescription();


    /**
     * Gets the type of this attribute (description without options).
     * 
     * @return the attribute type.
     */
    public abstract String getType();


    /**
     * Gets all values as byte[]. If the values aren't binary they are
     * converted to byte[] using UTF-8 encoding.
     * 
     * @return The binary values
     */
    public abstract byte[][] getBinaryValues();


    /**
     * Gets the first value as string if one is present, null otherwise
     * 
     * @return The first value if one present, null otherwise
     */
    public abstract String getStringValue();


    /**
     * Gets all values as String. If the values aren't strings they are
     * converted using UTF-8 encoding.
     * 
     * @return The string values
     */
    public abstract String[] getStringValues();


    /**
     * Returns true if the argument is also of type IAttribute and they are
     * equal.
     * 
     * IAttributes are equal if there entries and there attribute
     * description are equal.
     * 
     * @param o
     *                The attribute to compare, must be of type IAttribute
     * @return true if the argument is equal to this.
     */
    public abstract boolean equals( Object o );


    /**
     * Gets the AttributeTypeDescription of this attribute.
     * 
     * @return the AttributeTypeDescription of this attribute, may be the
     *         default or a dummy
     */
    public abstract AttributeTypeDescription getAttributeTypeDescription();


    /**
     * Gets the AttributeDescription of this attribute.
     * 
     * @return the AttributeDescription of this attribute,.
     */
    public abstract AttributeDescription getAttributeDescription();

}
