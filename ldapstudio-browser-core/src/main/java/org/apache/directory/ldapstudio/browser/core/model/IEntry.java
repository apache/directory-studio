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

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.schema.Subschema;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.ConnectionPropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


public interface IEntry extends Serializable, IAdaptable, EntryPropertyPageProvider, ConnectionPropertyPageProvider
{

    /**
     * Adds the given child to this entry.
     * 
     * @param childToAdd
     *                the child to add
     * @param source
     *                the ModelModifier
     */
    public abstract void addChild( IEntry childToAdd, ModelModifier source );


    /**
     * Deletes the given child and all its children from this entry.
     * 
     * @param childToDelete
     *                the child to delete
     * @param source
     *                the ModelModifier
     */
    public abstract void deleteChild( IEntry childToDelete, ModelModifier source );


    /**
     * Adds the given attribute to this entry. The attribute's entry must be
     * this entry.
     * 
     * @param attributeToAdd
     *                the attribute to add
     * @param source
     *                the ModelModifier
     * @throws ModelModificationException
     *                 if the attribute is alreade present in this entry or
     *                 if the attribute's entry isn't this entry.
     */
    public abstract void addAttribute( IAttribute attributeToAdd, ModelModifier source )
        throws ModelModificationException;


    /**
     * Deletes the given attribute from this entry.
     * 
     * @param attributeToDelete
     *                the attribute to delete
     * @param source
     *                the ModelModifier
     * @throws ModelModificationException
     *                 if the attribute isn't present in this entry.
     */
    public abstract void deleteAttribute( IAttribute attributeToDelete, ModelModifier source )
        throws ModelModificationException;


    /**
     * Checks if the entry with its attributes is consistent. The following
     * conditions must be fulfilled:
     * 
     * <ul>
     * <li>The objectClass attrbute must be present</li>
     * <li>All must attributes must be present</li>
     * <li>There mustn't be any empty value</li>
     * </ul>
     * 
     * @return true if the entry is consistent
     */
    public abstract boolean isConsistent();


    /**
     * Indicates wheater the entry was created from directory. Otherwise it
     * was created from the user.
     * 
     * @return true it the entry is a directory entry
     */
    public abstract boolean isDirectoryEntry();


    /**
     * 
     * 
     * @param isDirectoryEntry
     *                true if the entry is created from directory.
     */
    public abstract void setDirectoryEntry( boolean isDirectoryEntry );


    public abstract boolean isAlias();


    public abstract void setAlias( boolean b );


    public abstract boolean isReferral();


    public abstract void setReferral( boolean b );


    public abstract boolean isSubentry();


    public abstract void setSubentry( boolean b );


    /**
     * Returns the DN of the entry, never null.
     * 
     * @return the DN of the entry, never null.
     */
    public abstract DN getDn();


    /**
     * Returns the RDN of the entry, never null.
     * 
     * @return the RDN of the entry, never null.
     */
    public abstract RDN getRdn();


    /**
     * Indicates wheater the attributes were initialized from directory.
     * 
     * @return true if the attributes were initialized from directory.
     */
    public abstract boolean isAttributesInitialized();


    /**
     * Sets if the attributes of this entry are initialized.
     * 
     * @param b
     */
    public abstract void setAttributesInitialized( boolean b, ModelModifier source );


    /**
     * Returns the attributes of the entry.
     * 
     * @return The attributes of the entry or null if the attributes arn't
     *         initialized
     */
    public abstract IAttribute[] getAttributes();


    /**
     * Returns the attribute of the entry.
     * 
     * @param attributeDescription
     * @return The attributes of the entry or null if the attribute doesn't
     *         exist or if the attributes arn't initialized.
     */
    public abstract IAttribute getAttribute( String attributeDescription );


    /**
     * Returns a AttributeHierachie containing the requested attribute and
     * all its subtypes.
     * 
     * @param attributeDescription
     * @return The attributes of the entry or null if the attribute doesn't
     *         exist or if the attributes arn't initialized.
     */
    public abstract AttributeHierachie getAttributeWithSubtypes( String attributeDescription );


    /**
     * Returns the subschema of the entry.
     * 
     * @return The subschema of the entry or null if the attributes aren't
     *         initialized.
     */
    public abstract Subschema getSubschema();


    /**
     * Indicates wheater the children were initialized from directory.
     * 
     * @return true if the children were initialized from directory.
     */
    public abstract boolean isChildrenInitialized();


    /**
     * Sets if the children of this entry are initialized from directory.
     * 
     * @param b
     */
    public abstract void setChildrenInitialized( boolean b, ModelModifier source );


    /**
     * Returns true if the entry has children.
     * 
     * @return true if the entry has children.
     */
    public abstract boolean hasChildren();


    /**
     * Sets if the entry has children.
     * 
     * @param b
     */
    public abstract void setHasChildrenHint( boolean b, ModelModifier source );


    /**
     * Returns the children of the entry.
     * 
     * @return The children of the entry or null if the children arn't
     *         initialized
     */
    public abstract IEntry[] getChildren();


    /**
     * Returns the number of children of the entry.
     * 
     * @return The number of children of the entry or -1 if the children
     *         arn't initialized
     */
    public abstract int getChildrenCount();


    /**
     * Indicates wheather this entry has more children than
     * getChildrenCount() returns. This occurs when the count or time limit
     * of the LDAP connection exeeded while fetching children.
     * 
     * @return true if this entry has (maybe) more children than the given.
     */
    public abstract boolean hasMoreChildren();


    /**
     * Sets if the entry has more children in the directory.
     * 
     * @param b
     */
    public abstract void setHasMoreChildren( boolean b, ModelModifier source );


    /**
     * Indicates wheather this entry has a parent entry. Each entry except
     * the root DSE and the base entry should have a parent entry.
     * 
     * @return true if the entry has a parent entry.
     */
    public abstract boolean hasParententry();


    /**
     * Return the parent entry.
     * 
     * @return the parent entry or null if this entry hasn't a parent.
     */
    public abstract IEntry getParententry();


    public abstract String getChildrenFilter();


    public abstract void setChildrenFilter( String filter );


    /**
     * Return the connection of this entry, never null.
     * 
     * @return the connection of this entry, never null.
     */
    public abstract IConnection getConnection();


    public abstract URL getUrl();

}
