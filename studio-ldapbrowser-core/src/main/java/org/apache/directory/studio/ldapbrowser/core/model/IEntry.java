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

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Subschema;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IEntry represents an LDAP entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IEntry extends Serializable, IAdaptable, EntryPropertyPageProvider, ConnectionPropertyPageProvider
{

    /**
     * Adds the given child to this entry.
     * 
     * @param childToAdd
     *                the child to add
     */
    public abstract void addChild( IEntry childToAdd );


    /**
     * Deletes the given child and all its children from this entry.
     * 
     * @param childToDelete
     *                the child to delete
     */
    public abstract void deleteChild( IEntry childToDelete );


    /**
     * Adds the given attribute to this entry. The attribute's entry must be
     * this entry.
     * 
     * @param attributeToAdd
     *                the attribute to add
     * @throws IllegalArgumentException
     *                 if the attribute is already present in this entry or
     *                 if the attribute's entry isn't this entry.
     */
    public abstract void addAttribute( IAttribute attributeToAdd ) throws IllegalArgumentException;


    /**
     * Deletes the given attribute from this entry.
     * 
     * @param attributeToDelete
     *                the attribute to delete
     * @throws IllegalArgumentException
     *                 if the attribute isn't present in this entry.
     */
    public abstract void deleteAttribute( IAttribute attributeToDelete ) throws IllegalArgumentException;


    /**
     * Checks if this entry with its attributes is consistent. The following
     * conditions must be fulfilled:
     * 
     * <ul>
     * <li>The objectClass attribute must be present</li>
     * <li>All must attributes must be present</li>
     * <li>There mustn't be any empty value</li>
     * </ul>
     * 
     * @return true if this entry is consistent
     */
    public abstract boolean isConsistent();


    /**
     * Indicates whether this entry exists in directory. Otherwise it
     * is a new entry created from the user and not still written to directory.
     * 
     * @return true it this entry is a directory entry
     */
    public abstract boolean isDirectoryEntry();


    /**
     * Sets whether this entry exists in directory.
     * 
     * @param isDirectoryEntry
     *                true if this entry exists in directory.
     */
    public abstract void setDirectoryEntry( boolean isDirectoryEntry );


    /**
     * Indicates whether this entry is an alias entry.
     * 
     * An entry is an alias entry if it has the object class 'alias'.
     * 
     * Even if the object class attribute is not initialized an entry
     * is supposed to be an alias entry if the alias flag is set.
     * 
     * @return true, if this entry is an alias entry
     */
    public abstract boolean isAlias();


    /**
     * Sets a flag whether this entry is an alias entry.
     * 
     * This method is called during a search if the initialization
     * of the alias flag is requested. 
     * 
     * @param b the alias flag
     */
    public abstract void setAlias( boolean b );


    /**
     * Indicates whether this entry is a referral entry.
     * 
     * An entry is a referral entry if it has the objectClass 'referral'.
     * 
     * Even if the object class attribute is not initialized an entry
     * is supposed to be a referral entry if the referral flag is set.
     * 
     * @return true, if this entry is a referral entry
     */
    public abstract boolean isReferral();


    /**
     * Sets a flag whether this entry is a referral entry.
     * 
     * This method is called during a search if the initialization
     * fo the referral hint is requested. 
     * 
     * @param b the referral flag
     */
    public abstract void setReferral( boolean b );


    /**
     * Indicates whether this entry is a subentry.
     * 
     * An entry is a subentry if it has the objectClass 'subentry'.
     * 
     * Even if the object class attribute is not initialized an entry
     * is supposed to be a subentry if the subentry flag is set.
     * 
     * @return true, if this entry is a subentry entry
     */
    public abstract boolean isSubentry();


    /**
     * Sets a flag whether this entry is a subentry.
     * 
     * This method is called during a search if the initialization
     * fo the subentry is requested. 
     * 
     * @param b the subentry flag
     */
    public abstract void setSubentry( boolean b );


    /**
     * Gets the DN of this entry, never null.
     * 
     * @return the DN of this entry, never null.
     */
    public abstract LdapDN getDn();
    
    
    /**
     * Gets the RDN of this entry, never null.
     * 
     * @return the RDN of this entry, never null.
     */
    public abstract Rdn getRdn();


    /**
     * Indicates whether this entry's attributes are initialized.
     * 
     * True means that the entry's attributes are completely initialized
     * and getAttributes() will return all attributes.
     * 
     * False means that the attributes are not or only partially
     * initialized. The getAttributes() method will return null
     * or only a part of the entry's attributes.  
     * 
     * @return true if this entry's attributes are initialized
     */
    public abstract boolean isAttributesInitialized();


    /**
     * Sets a flag whether this entry's attributes are initialized.
     * 
     * @param b the attributes initialized flag
     */
    public abstract void setAttributesInitialized( boolean b );


    /**
     * Indicates whether this entry's operational attributes are initialized.
     * 
     * True means that the entry's operational attributes are completely 
     * initialized and getAttributes() will return all operational attributes 
     * (additionally to the non-operational attributes).
     * 
     * False means that the operational attributes are not or only partially
     * initialized. The getAttributes() method will return none
     * or only a part of the entry's operational attributes.  
     * 
     * @return true if this entry's attributes are initialized
     */
    public abstract boolean isOperationalAttributesInitialized();
    
    
    /**
     * Sets a flag whether this entry's operational attributes are initialized.
     * 
     * @param b the operational attributes initialized flag
     */
    public abstract void setOperationalAttributesInitialized( boolean b );
    
    
    /**
     * Gets the attributes of the entry.
     * 
     * If isAttributesInitialized() returns false the returned attributes 
     * may only be a subset of the attributes in directory.
     * 
     * @return The attributes of the entry or null if no attribute was added yet
     */
    public abstract IAttribute[] getAttributes();


    /**
     * Gets the attribute of the entry.
     * 
     * @param attributeDescription the attribute description
     * @return The attributes of the entry or null if the attribute doesn't
     *         exist or if the attributes aren't initialized
     */
    public abstract IAttribute getAttribute( String attributeDescription );


    /**
     * Gets a AttributeHierachie containing the requested attribute and
     * all its subtypes.
     * 
     * @param attributeDescription the attribute description
     * @return The attributes of the entry or null if the attribute doesn't
     *         exist or if the attributes aren't initialized
     */
    public abstract AttributeHierarchy getAttributeWithSubtypes( String attributeDescription );


    /**
     * Returns the subschema of the entry.
     * 
     * @return The subschema of the entry or null if the attributes aren't
     *         initialized
     */
    public abstract Subschema getSubschema();


    /**
     * Indicates whether the entry's children are initialized.
     * 
     * True means that the entry's children are completely initialized
     * and getChildren() will return all children.
     * 
     * False means that the children are not or only partially
     * initialized. The getChildren() method will return null
     * or only a part of the entry's children.  
     * 
     * @return true if this entry's children are initialized
     */
    public abstract boolean isChildrenInitialized();


    /**
     * Sets a flag whether this entry's children are initialized..
     * 
     * @param b the children initialized flag
     */
    public abstract void setChildrenInitialized( boolean b );


    /**
     * Returns true if the entry has children.
     * 
     * @return true if the entry has children.
     */
    public abstract boolean hasChildren();


    /**
     * Sets a hint whether this entry has children.
     * 
     * @param b the has children hint
     */
    public abstract void setHasChildrenHint( boolean b );


    /**
     * Gets the children of the entry.
     * 
     * If isChildrenInitialized() returns false the returned children 
     * may only be a subset of the children in directory.
     * 
     * @return The children of the entry or null if no child was added yet.
     */
    public abstract IEntry[] getChildren();


    /**
     * Gets the number of children of the entry.
     * 
     * @return The number of children of the entry or -1 if no child was added yet
     */
    public abstract int getChildrenCount();


    /**
     * Indicates whether this entry has more children than
     * getChildrenCount() returns. This occurs if the count or time limit
     * was exceeded while fetching children.
     * 
     * @return true if this entry has (maybe) more children.
     */
    public abstract boolean hasMoreChildren();


    /**
     * Sets a flag whether this entry more children.
     * 
     * @param b the has more children flag
     */
    public abstract void setHasMoreChildren( boolean b );


    /**
     * Indicates whether this entry has a parent entry. Each entry except
     * the root DSE and the base entries should have a parent entry.
     * 
     * @return true if the entry has a parent entry.
     */
    public abstract boolean hasParententry();


    /**
     * Gets the parent entry.
     * 
     * @return the parent entry or null if this entry hasn't a parent.
     */
    public abstract IEntry getParententry();


    /**
     * Gets the children filter or null if none is set
     *
     * @return the children filter or null if none is set
     */
    public abstract String getChildrenFilter();


    /**
     * Sets the children filter. Null clears the filter.
     * 
     * @param filter the children filter
     */
    public abstract void setChildrenFilter( String filter );


    /**
     * Gets the browser connection of this entry.
     * 
     * @return the browser connection of this entry, never null.
     */
    public abstract IBrowserConnection getBrowserConnection();


    /**
     * Gets the LDAP URL of this entry.
     * 
     * @return the  LDAP URL of this entry
     */
    public abstract URL getUrl();

}
