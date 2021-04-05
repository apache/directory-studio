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
import java.util.Collection;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IEntry represents an LDAP entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IEntry extends Serializable, IAdaptable, EntryPropertyPageProvider, ConnectionPropertyPageProvider
{

    /**
     * Adds the given child to this entry.
     * 
     * @param childToAdd
     *                the child to add
     */
    void addChild( IEntry childToAdd );


    /**
     * Deletes the given child and all its children from this entry.
     * 
     * @param childToDelete
     *                the child to delete
     */
    void deleteChild( IEntry childToDelete );


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
    void addAttribute( IAttribute attributeToAdd ) throws IllegalArgumentException;


    /**
     * Deletes the given attribute from this entry.
     * 
     * @param attributeToDelete
     *                the attribute to delete
     * @throws IllegalArgumentException
     *                 if the attribute isn't present in this entry.
     */
    void deleteAttribute( IAttribute attributeToDelete ) throws IllegalArgumentException;


    /**
     * Sets whether this entry exists in directory.
     * 
     * @param isDirectoryEntry
     *                true if this entry exists in directory.
     */
    void setDirectoryEntry( boolean isDirectoryEntry );


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
    boolean isAlias();


    /**
     * Sets a flag whether this entry is an alias entry.
     * 
     * This method is called during a search if the initialization
     * of the alias flag is requested. 
     * 
     * @param b the alias flag
     */
    void setAlias( boolean b );


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
    boolean isReferral();


    /**
     * Sets a flag whether this entry is a referral entry.
     * 
     * This method is called during a search if the initialization
     * fo the referral hint is requested. 
     * 
     * @param b the referral flag
     */
    void setReferral( boolean b );


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
    boolean isSubentry();


    /**
     * Sets a flag whether this entry is a subentry.
     * 
     * This method is called during a search if the initialization
     * fo the subentry is requested. 
     * 
     * @param b the subentry flag
     */
    void setSubentry( boolean b );


    /**
     * Gets the Dn of this entry, never null.
     * 
     * @return the Dn of this entry, never null.
     */
    Dn getDn();


    /**
     * Gets the Rdn of this entry, never null.
     * 
     * @return the Rdn of this entry, never null.
     */
    Rdn getRdn();


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
    boolean isAttributesInitialized();


    /**
     * Sets a flag whether this entry's attributes are initialized.
     * 
     * @param b the attributes initialized flag
     */
    void setAttributesInitialized( boolean b );


    /**
     * Indicates whether this entry's operational attributes should be initialized.
     * 
     * @return true if this entry's attributes should be initialized
     */
    boolean isInitOperationalAttributes();


    /**
     * Sets a flag whether this entry's operational attributes should be initialized.
     * 
     * @param b the initialize operational attributes flag
     */
    void setInitOperationalAttributes( boolean b );


    /**
     * Indicates whether this entry's alias children should be fetched.
     * 
     * @return true if this entry's alias children should be fetched
     */
    boolean isFetchAliases();


    /**
     * Sets a flag whether this entry's alias children should be fetched.
     * 
     * @param b the fetch aliases flag
     */
    void setFetchAliases( boolean b );


    /**
     * Indicates whether this entry's referral children should be fetched.
     * 
     * @return true if this entry's referral children should be fetched
     */
    boolean isFetchReferrals();


    /**
     * Sets a flag whether this entry's referral children should be fetched.
     * 
     * @param b the fetch referral flag
     */
    void setFetchReferrals( boolean b );


    /**
     * Indicates whether this entry's sub-entries should be fetched.
     * 
     * @return true if this entry's sub-entries should be fetched
     */
    boolean isFetchSubentries();


    /**
     * Sets a flag whether this entry's sub-entries should be fetched.
     * 
     * @param b the fetch sub-entries flag
     */
    void setFetchSubentries( boolean b );


    /**
     * Gets the attributes of the entry.
     * 
     * If isAttributesInitialized() returns false the returned attributes 
     * may only be a subset of the attributes in directory.
     * 
     * @return The attributes of the entry or null if no attribute was added yet
     */
    IAttribute[] getAttributes();


    /**
     * Gets the attribute of the entry.
     * 
     * @param attributeDescription the attribute description
     * @return The attributes of the entry or null if the attribute doesn't
     *         exist or if the attributes aren't initialized
     */
    IAttribute getAttribute( String attributeDescription );


    /**
     * Gets a AttributeHierachie containing the requested attribute and
     * all its subtypes.
     * 
     * @param attributeDescription the attribute description
     * @return The attributes of the entry or null if the attribute doesn't
     *         exist or if the attributes aren't initialized
     */
    AttributeHierarchy getAttributeWithSubtypes( String attributeDescription );


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
    boolean isChildrenInitialized();


    /**
     * Sets a flag whether this entry's children are initialized..
     * 
     * @param b the children initialized flag
     */
    void setChildrenInitialized( boolean b );


    /**
     * Returns true if the entry has children.
     * 
     * @return true if the entry has children.
     */
    boolean hasChildren();


    /**
     * Sets a hint whether this entry has children.
     * 
     * @param b the has children hint
     */
    void setHasChildrenHint( boolean b );


    /**
     * Gets the children of the entry.
     * 
     * If isChildrenInitialized() returns false the returned children 
     * may only be a subset of the children in directory.
     * 
     * @return The children of the entry or null if no child was added yet.
     */
    IEntry[] getChildren();


    /**
     * Gets the number of children of the entry.
     * 
     * @return The number of children of the entry or -1 if no child was added yet
     */
    int getChildrenCount();


    /**
     * Indicates whether this entry has more children than
     * getChildrenCount() returns. This occurs if the count or time limit
     * was exceeded while fetching children.
     * 
     * @return true if this entry has (maybe) more children.
     */
    boolean hasMoreChildren();


    /**
     * Sets a flag whether this entry more children.
     * 
     * @param b the has more children flag
     */
    void setHasMoreChildren( boolean b );


    /**
     * Gets the runnable used to fetch the top page of children.
     * 
     * @return the runnable used to fetch the top page of children, null if none
     */
    StudioConnectionBulkRunnableWithProgress getTopPageChildrenRunnable();


    /**
     * Sets the runnable used to fetch the top page of children.
     * 
     * @param moreChildrenRunnable the runnable used to fetch the top page of children
     */
    void setTopPageChildrenRunnable( StudioConnectionBulkRunnableWithProgress topPageChildrenRunnable );


    /**
     * Gets the runnable used to fetch the next page of children.
     * 
     * @return the runnable used to fetch the next page of children, null if none
     */
    StudioConnectionBulkRunnableWithProgress getNextPageChildrenRunnable();


    /**
     * Sets the runnable used to fetch the next page of children.
     * 
     * @param moreChildrenRunnable the runnable used to fetch the next page of children
     */
    void setNextPageChildrenRunnable( StudioConnectionBulkRunnableWithProgress nextPageChildrenRunnable );


    /**
     * Indicates whether this entry has a parent entry. Each entry except
     * the root DSE and the base entries should have a parent entry.
     * 
     * @return true if the entry has a parent entry.
     */
    boolean hasParententry();


    /**
     * Gets the parent entry.
     * 
     * @return the parent entry or null if this entry hasn't a parent.
     */
    IEntry getParententry();


    /**
     * Gets the children filter or null if none is set
     *
     * @return the children filter or null if none is set
     */
    String getChildrenFilter();


    /**
     * Sets the children filter. Null clears the filter.
     * 
     * @param filter the children filter
     */
    void setChildrenFilter( String filter );


    /**
     * Gets the browser connection of this entry.
     * 
     * @return the browser connection of this entry, never null.
     */
    IBrowserConnection getBrowserConnection();


    /**
     * Gets the LDAP URL of this entry.
     * 
     * @return the  LDAP URL of this entry
     */
    LdapUrl getUrl();


    /**
     * Gets the object class descriptions of this entry.
     * 
     * @return the object class descriptions of this entry
     */
    Collection<ObjectClass> getObjectClassDescriptions();

}
