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
package org.apache.directory.studio.openldap.config.jobs;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.filter.FilterParser;
import org.apache.directory.api.ldap.model.ldif.ChangeType;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.server.core.api.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.api.interceptor.context.LookupOperationContext;
import org.apache.directory.server.core.api.interceptor.context.SearchOperationContext;
import org.apache.directory.server.core.api.partition.Partition;


/**
 * An utility class that computes the difference between two Partitions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionsDiffComputer
{
    private PartitionsDiffComputer()
    {
        // Nothing to do
    }
    
    /**
     * Compute the difference between two partitions :
     * <ul>
     *   <li>Added entries</li>
     *   <li>Removed entries</li>
     *   <li>Modified entries
     *     <ul>
     *       <li>Added Attributes</li>
     *       <li>Removed Attributes</li>
     *       <li>Modified Attributes
     *         <ul>
     *           <li>Added Values</li>
     *           <li>Removed Values</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     * </ul>
     * @param originalPartition The original partition
     * @param modifiedPartition The modified partition
     * @return A list of LDIF Additions, Deletions or Modifications 
     * @throws Exception If something went wrong
     */
    public static List<LdifEntry> computeModifications( Partition originalPartition, Partition modifiedPartition ) throws Exception
    {
        // '*' for all user attributes, '+' for all operational attributes
        return computeModifications( originalPartition, modifiedPartition, SchemaConstants.ALL_ATTRIBUTES_ARRAY );
    }


    /**
     * Compute the difference between two partitions :
     * <ul>
     *   <li>Added entries</li>
     *   <li>Removed entries</li>
     *   <li>Modified entries
     *     <ul>
     *       <li>Added Attributes</li>
     *       <li>Removed Attributes</li>
     *       <li>Modified Attributes
     *         <ul>
     *           <li>Added Values</li>
     *           <li>Removed Values</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     * </ul>
     * @param originalPartition The original partition
     * @param modifiedPartition The modified partition
     * @param attributeIds The list of attributes we want to compare
     * @return A list of LDIF Additions, Deletions or Modifications 
     * @throws Exception If something went wrong
     */
    public static List<LdifEntry> computeModifications(  Partition originalPartition, Partition modifiedPartition, String[] attributeIds ) throws Exception
    {
        return computeModifications( originalPartition, modifiedPartition, originalPartition.getSuffixDn(), attributeIds );
    }


    /** 
     * Compute the actual diff.
     */
    private static List<LdifEntry> computeModifications( Partition originalPartition, Partition modifiedPartition, Dn baseDn, String[] attributeIds ) throws Exception
    {
        // Checking partitions
        checkPartitions( originalPartition, modifiedPartition );

        return comparePartitions( originalPartition, modifiedPartition, baseDn, attributeIds );
    }


    /**
     * Checks the partitions.
     */
    private static void checkPartitions( Partition originalPartition, Partition modifiedPartition ) throws PartitionsDiffException
    {
        // Checking the original partition
        if ( originalPartition == null )
        {
            throw new PartitionsDiffException( "The original partition must not be 'null'." );
        }
        else
        {
            if ( !originalPartition.isInitialized() )
            {
                throw new PartitionsDiffException( "The original partition must be intialized." );
            }
            else if ( originalPartition.getSuffixDn() == null )
            {
                throw new PartitionsDiffException( "The original suffix is null." );
            }
        }

        // Checking the destination partition
        if ( modifiedPartition == null )
        {
            throw new PartitionsDiffException( "The modified partition must not be 'null'." );
        }
        else
        {
            if ( !modifiedPartition.isInitialized() )
            {
                throw new PartitionsDiffException( "The modified partition must be intialized." );
            }
            else if ( modifiedPartition.getSuffixDn() == null )
            {
                throw new PartitionsDiffException( "The modified suffix is null." );
            }
        }
    }


    /**
     * Compare two partitions.
     *
     * @param originalPartition The original partition
     * @param modifiedPartition The modified partition
     * @param baseDn the Dn from which we will iterate
     * @param attributeIds the IDs of the attributes we will compare
     * @return a list containing LDIF entries with all modifications
     * @throws Exception If something went wrong
     */
    public static List<LdifEntry> comparePartitions(  Partition originalPartition, Partition modifiedPartition,
        Dn baseDn, String[] attributeIds ) throws PartitionsDiffException
    {
        // Creating the list containing all the modifications
        List<LdifEntry> modifications = new ArrayList<>();

        try
        {
            // Looking up the original base entry
            Entry originalBaseEntry = originalPartition.lookup( new LookupOperationContext( null, baseDn, attributeIds ) );
            
            if ( originalBaseEntry == null )
            {
                throw new PartitionsDiffException( "Unable to find the base entry in the original partition." );
            }

            // Creating the list containing all the original entries to be processed
            // and adding it the original base entry. This is done going down the tree.
            List<Entry> originalEntries = new ArrayList<>();
            originalEntries.add( originalBaseEntry );

            // Looping until all original entries are being processed. We will read all the children,
            // adding each of them at the end of the list, consuming the first element of the list
            // at every iteration. When we have processed all the tree in depth, we should not have 
            // any left entries in the list.
            // We don't dereference aliases and referrals.
            while ( !originalEntries.isEmpty() )
            {
                // Getting the first original entry from the list
                Entry originalEntry = originalEntries.remove( 0 );

                // Creating a modification entry to hold all modifications
                LdifEntry ldifEntry = new LdifEntry();
                ldifEntry.setDn( originalEntry.getDn() );

                // Looking for the equivalent entry in the destination partition
                Entry modifiedEntry = modifiedPartition.lookup( new LookupOperationContext( null, originalEntry
                    .getDn(), attributeIds ) );
                
                if ( modifiedEntry != null )
                {
                    // Setting the changeType to Modify atm
                    ldifEntry.setChangeType( ChangeType.Modify );

                    // Comparing both entries
                    compareEntries( originalEntry, modifiedEntry, ldifEntry );
                }
                else
                {
                    // The entry has been deleted from the partition. It has to be deleted.
                    // Note : we *must* delete all of it's children first !!!
                    List<LdifEntry> deletions = deleteEntry( originalPartition, originalEntry.getDn() );
                    
                    // Append the children
                    modifications.addAll( deletions );

                    // and add the parent entry
                    ldifEntry.setChangeType( ChangeType.Delete );
                    
                    // And go on with the remaining entries
                    continue;
                }

                // Checking if modifications occurred on the original entry
                ChangeType modificationEntryChangeType = ldifEntry.getChangeType();
                
                if ( modificationEntryChangeType != ChangeType.None )
                {
                    if ( modificationEntryChangeType == ChangeType.Delete
                        || ( modificationEntryChangeType == ChangeType.Modify && !ldifEntry.getModifications().isEmpty() ) )
                    {
                        // Adding the modification entry to the list
                        modifications.add( ldifEntry );
                    }
                }

                // Creating a search operation context to get the children of the current entry
                SearchOperationContext soc = new SearchOperationContext( null, originalEntry.getDn(),
                    SearchScope.ONELEVEL,
                    FilterParser.parse( originalPartition.getSchemaManager(), LdapConstants.OBJECT_CLASS_STAR ), attributeIds );
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = originalPartition.search( soc );
                
                while ( cursor.next() )
                {
                    originalEntries.add( cursor.get() );
                }
            }

            // Now, iterate on the modified partition, to see if some entries have
            // been added.
            Entry destinationBaseEntry = modifiedPartition
                .lookup( new LookupOperationContext( null, baseDn, attributeIds ) );
            
            if ( destinationBaseEntry == null )
            {
                throw new PartitionsDiffException( "Unable to find the base entry in the destination partition." );
            }

            // Creating the list containing all the destination entries to be processed
            // and adding it the destination base entry
            List<Entry> modifiedEntries = new ArrayList<>();
            modifiedEntries.add( originalBaseEntry );

            // Looping until all modified entries are being processed
            while ( !modifiedEntries.isEmpty() )
            {
                // Getting the first modification entry from the list
                Entry modifiedEntry = modifiedEntries.remove( 0 );

                // Looking for the equivalent entry in the destination partition
                Entry originalEntry = originalPartition.lookup( new LookupOperationContext( null, modifiedEntry
                    .getDn(), attributeIds ) );
                
                // We're only looking for new entries, modified or removed 
                // entries have already been computed
                if ( originalEntry == null )
                {
                    // Creating a modification entry to hold all modifications
                    LdifEntry modificationEntry = new LdifEntry();
                    modificationEntry.setDn( modifiedEntry.getDn() );

                    // Setting the changeType to addition
                    modificationEntry.setChangeType( ChangeType.Add );

                    // Copying attributes
                    for ( Attribute attribute : modifiedEntry )
                    {
                        modificationEntry.addAttribute( attribute );
                    }

                    // Adding the modification entry to the list
                    modifications.add( modificationEntry );
                }

                // Creating a search operation context to get the children of the current entry
                SearchOperationContext soc = new SearchOperationContext( null, modifiedEntry.getDn(),
                    SearchScope.ONELEVEL,
                    FilterParser.parse( originalPartition.getSchemaManager(), LdapConstants.OBJECT_CLASS_STAR ), attributeIds );
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = modifiedPartition.search( soc );

                while ( cursor.next() )
                {
                    modifiedEntries.add( cursor.get() );
                }
            }
        }
        catch ( Exception e )
        {
            throw new PartitionsDiffException( e );
        }

        return modifications;
    }

    
    /**
     * Delete recursively the entries under a parent
     */
    private static List<LdifEntry> deleteEntry( Partition originalPartition, Dn parentDn ) throws LdapException, ParseException, CursorException
    {
        List<LdifEntry> deletions = new ArrayList<>();
        
        // Lookup for the children
        SearchOperationContext soc = new SearchOperationContext( null, parentDn,
            SearchScope.ONELEVEL,
            FilterParser.parse( originalPartition.getSchemaManager(), LdapConstants.OBJECT_CLASS_STAR ), SchemaConstants.NO_ATTRIBUTE_ARRAY );
        soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

        // Looking for the children of the current entry
        EntryFilteringCursor cursor = originalPartition.search( soc );

        while ( cursor.next() )
        {
            LdifEntry deletion = new LdifEntry( cursor.get().getDn() );
            
            // Iterate 
            List<LdifEntry> childrenDeletions = deleteEntry( originalPartition, deletion.getDn() );
            deletions.addAll( childrenDeletions );
            deletions.add( deletion );
        }
        
        return deletions;
    }

    /**
     * Compares the two given entries.
     *
     * @param originalEntry the original entry
     * @param modifiedEntry the destination entry
     * @param modificationEntry the modification LDIF entry holding the modifications between both entries
     * @throws LdapInvalidAttributeValueException 
     */
    private static void compareEntries( Entry originalEntry, Entry modifiedEntry, LdifEntry modificationEntry ) 
        throws LdapInvalidAttributeValueException
    {
        // We loop on all the attributes of the original entries, to detect the 
        // modified ones and the deleted ones
        for ( Attribute originalAttribute : originalEntry )
        {
            AttributeType originalAttributeType = originalAttribute.getAttributeType();

            // We're only working on 'userApplications' attributes
            if ( originalAttributeType.getUsage() == UsageEnum.USER_APPLICATIONS )
            {
                Attribute modifiedAttribute = modifiedEntry.get( originalAttributeType );
                
                if ( modifiedAttribute == null )
                {
                    // The attribute has been deleted
                    // Creating a modification for the removed AT
                    Modification modification = new DefaultModification(
                        ModificationOperation.REMOVE_ATTRIBUTE, originalAttributeType );

                    modificationEntry.addModification( modification );
                }
                else
                {
                    // Comparing both attributes
                    compareAttributes( originalAttribute, modifiedAttribute, modificationEntry );
                }
            }
        }

        // Now, check all the modified entry's attributes to see what are the added ones
        for ( Attribute destinationAttribute : modifiedEntry )
        {
            AttributeType destinationAttributeType = destinationAttribute.getAttributeType();

            // We're only working on 'userApplications' attributes
            if ( destinationAttributeType.getUsage() == UsageEnum.USER_APPLICATIONS )
            {
                // Checking if the current AT is not present in the original entry : if so,
                // it has been added
                if ( !originalEntry.containsAttribute( destinationAttributeType ) )
                {
                    // Creating a modification for the added AT
                    Modification modification = new DefaultModification( 
                        ModificationOperation.ADD_ATTRIBUTE,
                        destinationAttribute );

                    modificationEntry.addModification( modification );
                }
            }
        }
    }


    /**
     * Compares two attributes.
     *
     * @param originalAttribute the original attribute
     * @param modifiedAttribute the destination attribute
     * @param modificationEntry the modification LDIF entry holding the modifications between both attributes
     */
    private static void compareAttributes( Attribute originalAttribute, Attribute modifiedAttribute,
        LdifEntry modificationEntry )
    {
        // Special case for 'objectClass' attribute, due to a limitation in OpenLDAP
        // which does not allow us to modify the 'objectClass' attribute
        if ( !SchemaConstants.OBJECT_CLASS_AT.equalsIgnoreCase( originalAttribute.getAttributeType().getName() ) )
        {
            // Checking if the two attributes are equivalent
            if ( !originalAttribute.equals( modifiedAttribute ) )
            {
                // Creating a modification for the modified AT values. We do that globally, for all the values
                // The values should also be ordered if this is required (X-ORDERED)
                Modification modification = new DefaultModification(
                    ModificationOperation.REPLACE_ATTRIBUTE, modifiedAttribute );

                modificationEntry.addModification( modification );
            }
        }
    }
}
