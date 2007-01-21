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

import org.apache.directory.ldapstudio.browser.core.internal.model.AttributeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.AttributePropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.ConnectionPropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IAttribute represents an LDAP attribute.
 */
public interface IAttribute extends Serializable, IAdaptable, AttributePropertyPageProvider, EntryPropertyPageProvider,
    ConnectionPropertyPageProvider
{

    /**
     * ( 2.5.18.3 NAME 'creatorsName' EQUALITY distinguishedNameMatch SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.12 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_CREATORS_NAME = "creatorsName"; //$NON-NLS-1$

    /**
     * ( 2.5.18.1 NAME 'createTimestamp' EQUALITY generalizedTimeMatch
     * ORDERING generalizedTimeOrderingMatch SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.24 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP = "createTimestamp"; //$NON-NLS-1$

    /**
     * ( 2.5.18.4 NAME 'modifiersName' EQUALITY distinguishedNameMatch
     * SYNTAX 1.3.6.1.4.1.1466.115.121.1.12 SINGLE-VALUE
     * NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_MODIFIERS_NAME = "modifiersName"; //$NON-NLS-1$

    /**
     * ( 2.5.18.2 NAME 'modifyTimestamp' EQUALITY generalizedTimeMatch
     * ORDERING generalizedTimeOrderingMatch SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.24 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP = "modifyTimestamp"; //$NON-NLS-1$

    /**
     * ( 2.5.21.9 NAME 'structuralObjectClass' EQUALITY
     * objectIdentifierMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.38
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_STRUCTURAL_OBJECT_CLASS = "structuralObjectClass"; //$NON-NLS-1$

    /**
     * ( 2.5.21.10 NAME 'governingStructureRule' EQUALITY integerMatch
     * SYNTAX 1.3.6.1.4.1.1466.115.121.1.27 SINGLE-VALUE
     * NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_GOVERNING_STRUCTURE_RULE = "governingStructureRule"; //$NON-NLS-1$

    /**
     * ( 1.3.6.1.1.16.4 NAME 'entryUUID' DESC 'UUID of the entry' EQUALITY
     * uuidMatch ORDERING uuidOrderingMatch SYNTAX 1.3.6.1.1.16.1
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_ENTRY_UUID = "entryUUID"; //$NON-NLS-1$

    /**
     * ( 2.5.18.10 NAME 'subschemaSubentry' EQUALITY distinguishedNameMatch
     * SYNTAX 1.3.6.1.4.1.1466.115.121.1.12 SINGLE-VALUE
     * NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_SUBSCHEMA_SUBENTRY = "subschemaSubentry"; //$NON-NLS-1$

    /**
     * ( 2.5.18.9 NAME 'hasSubordinates' DESC 'X.501: entry has children'
     * EQUALITY booleanMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.7
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES = "hasSubordinates"; //$NON-NLS-1$

    /**
     * ( 1.3.1.1.4.1.453.16.2.103 NAME 'numSubordinates' DESC 'count of
     * immediate subordinates' EQUALITY integerMatch ORDERING
     * integerOrderingMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.27
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation X-ORIGIN
     * 'numSubordinates Internet Draft' )
     */
    public static final String OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES = "numSubordinates"; //$NON-NLS-1$

    /**
     * ( 2.16.840.1.113719.1.27.4.49 NAME 'subordinateCount' DESC
     * 'Operational Attribute' SYNTAX 1.3.6.1.4.1.1466.115.121.1.27
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT = "subordinateCount"; //$NON-NLS-1$

    /**
     * ( 1.3.6.1.1.4 NAME 'vendorName' EQUALITY caseExactIA5Match SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.15 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * dSAOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_VENDOR_NAME = "vendorName"; //$NON-NLS-1$

    /**
     * ( 1.3.6.1.1.5 NAME 'vendorVersion' EQUALITY caseExactIA5Match SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.15 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * dSAOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_VENDOR_VERSION = "vendorVersion"; //$NON-NLS-1$

    /** The attribute type objectClass */
    public static final String OBJECTCLASS_ATTRIBUTE = "objectClass"; //$NON-NLS-1$

    /** The OID of the objectClass attribute, 2.5.4.0 */
    public static final String OBJECTCLASS_ATTRIBUTE_OID = "2.5.4.0"; //$NON-NLS-1$

    /** The attribute type ref */
    public static final String REFERRAL_ATTRIBUTE = "ref"; //$NON-NLS-1$

    /** The attribute type aliasedObjectName */
    public static final String ALIAS_ATTRIBUTE = "aliasedObjectName"; //$NON-NLS-1$

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
     * @throws ModelModificationException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void addValue( IValue valueToAdd ) throws ModelModificationException;


    /**
     * Deletes the given value from this attribute.
     * 
     * @param valueToDelete
     *                the value to delete
     * @throws ModelModificationException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void deleteValue( IValue valueToDelete ) throws ModelModificationException;


    /**
     * Replaces the old value with the new value.
     * 
     * @param oldValue
     *                the value that should be replaced
     * @param newValue
     *                the value that should be added
     * @throws ModelModificationException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void modifyValue( IValue oldValue, IValue newValue )
        throws ModelModificationException;


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
