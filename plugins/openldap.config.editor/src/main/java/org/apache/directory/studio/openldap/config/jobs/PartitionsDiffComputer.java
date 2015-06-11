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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.filter.FilterParser;
import org.apache.directory.api.ldap.model.ldif.ChangeType;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.server.core.api.entry.ClonedServerEntry;
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
    public static List<LdifEntry> computeModifications( Partition originalPartition, Partition modifiedPartition ) throws Exception
    {
        // Using the original partition suffix as base 
        // '*' for all user attributes, '+' for all operational attributes
        return computeModifications( originalPartition, modifiedPartition, originalPartition.getSuffixDn(), new String[]
            { "*", "+" } );
    }


    public static List<LdifEntry> computeModifications(  Partition originalPartition, Partition modifiedPartition, String[] attributeIds ) throws Exception
    {
        return computeModifications( originalPartition, modifiedPartition, originalPartition.getSuffixDn(), attributeIds );
    }


    private static List<LdifEntry> computeModifications( Partition originalPartition, Partition modifiedPartition, Dn baseDn, String[] attributeIds ) throws Exception
    {
        // Checking partitions
        checkPartitions( originalPartition, modifiedPartition );

        return comparePartitions( originalPartition, modifiedPartition, baseDn, attributeIds );
    }


    /**
     * Checks the partitions.
     *
     * @throws PartitionsDiffException
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
            throw new PartitionsDiffException( "The destination partition must not be 'null'." );
        }
        else
        {
            if ( !modifiedPartition.isInitialized() )
            {
                throw new PartitionsDiffException( "The destination partition must be intialized." );
            }
            else if ( modifiedPartition.getSuffixDn() == null )
            {
                throw new PartitionsDiffException( "The destination suffix is null." );
            }
        }
    }


    /**
     * Compare the two partitions.
     *
     * @param baseDn the base Dn
     * @param attributeIds the IDs of the attributes
     * @return a list containing LDIF entries with all modifications
     * @throws Exception
     */
    public static List<LdifEntry> comparePartitions(  Partition originalPartition, Partition modifiedPartition,Dn baseDn, String[] attributeIds ) throws PartitionsDiffException
    {
        // Creating the list containing all modifications
        List<LdifEntry> modifications = new ArrayList<LdifEntry>();

        try
        {
            // Looking up the original base entry
            Entry originalBaseEntry = originalPartition
                .lookup( new LookupOperationContext( null, baseDn, attributeIds ) );
            
            if ( originalBaseEntry == null )
            {
                throw new PartitionsDiffException( "Unable to find the base entry in the original partition." );
            }

            // Creating the list containing all the original entries to be processed
            // and adding it the original base entry
            List<Entry> originalEntries = new ArrayList<Entry>();
            originalEntries.add( originalBaseEntry );

            // Looping until all original entries are being processed
            while ( originalEntries.size() > 0 )
            {
                // Getting the first original entry from the list
                Entry originalEntry = originalEntries.remove( 0 );

                // Creating a modification entry to hold all modifications
                LdifEntry modificationEntry = new LdifEntry();
                modificationEntry.setDn( originalEntry.getDn() );

                // Looking for the equivalent entry in the destination partition
                Entry destinationEntry = modifiedPartition.lookup( new LookupOperationContext( null, originalEntry
                    .getDn(), attributeIds ) );
                
                if ( destinationEntry != null )
                {
                    // Setting the changetype to delete
                    modificationEntry.setChangeType( ChangeType.Modify );

                    // Comparing both entries
                    compareEntries( originalEntry, destinationEntry, modificationEntry );
                }
                else
                {
                    // The original entry is no longer present in the destination partition

                    // Setting the changetype to delete
                    modificationEntry.setChangeType( ChangeType.Delete );
                }

                // Checking if modifications occurred on the original entry
                ChangeType modificationEntryChangeType = modificationEntry.getChangeType();
                
                if ( modificationEntryChangeType != ChangeType.None )
                {
                    if ( modificationEntryChangeType == ChangeType.Delete
                        || ( modificationEntryChangeType == ChangeType.Modify && modificationEntry
                            .getModifications().size() > 0 ) )
                    {
                        // Adding the modification entry to the list
                        modifications.add( modificationEntry );
                    }
                }

                // Creating a search operation context to get the children of the current entry
                SearchOperationContext soc = new SearchOperationContext( null, originalEntry.getDn(),
                    SearchScope.ONELEVEL,
                    FilterParser.parse( originalPartition.getSchemaManager(), "(objectClass=*)" ), attributeIds );
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = originalPartition.search( soc );
                
                while ( cursor.next() )
                {
                    originalEntries.add( ( ( ClonedServerEntry ) cursor.get() ).getClonedEntry() );
                }
            }

            // Looking up the destination base entry
            Entry destinationBaseEntry = modifiedPartition
                .lookup( new LookupOperationContext( null, baseDn, attributeIds ) );
            
            if ( destinationBaseEntry == null )
            {
                throw new PartitionsDiffException( "Unable to find the base entry in the destination partition." );
            }

            // Creating the list containing all the destination entries to be processed
            // and adding it the destination base entry
            List<Entry> destinationEntries = new ArrayList<Entry>();
            destinationEntries.add( originalBaseEntry );

            // Looping until all destination entries are being processed
            while ( destinationEntries.size() > 0 )
            {
                // Getting the first destination entry from the list
                Entry destinationEntry = destinationEntries.remove( 0 );

                // Looking for the equivalent entry in the destination partition
                Entry originalEntry = originalPartition.lookup( new LookupOperationContext( null, destinationEntry
                    .getDn(), attributeIds ) );
                
                // We're only looking for new entries, modified or removed 
                // entries have already been computed
                if ( originalEntry == null )
                {
                    // Creating a modification entry to hold all modifications
                    LdifEntry modificationEntry = new LdifEntry();
                    modificationEntry.setDn( destinationEntry.getDn() );

                    // Setting the changetype to addition
                    modificationEntry.setChangeType( ChangeType.Add );

                    // Copying attributes
                    for ( Attribute attribute : destinationEntry )
                    {
                        modificationEntry.addAttribute( attribute );
                    }

                    // Adding the modification entry to the list
                    modifications.add( modificationEntry );
                }

                // Creating a search operation context to get the children of the current entry
                SearchOperationContext soc = new SearchOperationContext( null, destinationEntry.getDn(),
                    SearchScope.ONELEVEL,
                    FilterParser.parse( originalPartition.getSchemaManager(), "(objectClass=*)" ), attributeIds );
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = modifiedPartition.search( soc );

                while ( cursor.next() )
                {
                    destinationEntries.add( ( ( ClonedServerEntry ) cursor.get() ).getClonedEntry() );
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
     * Compares the two given entries.
     *
     * @param originalEntry the original entry
     * @param destinationEntry the destination entry
     * @param modificationEntry the modification LDIF entry holding the modifications between both entries
     */
    private static void compareEntries( Entry originalEntry, Entry destinationEntry, LdifEntry modificationEntry )
    {
        // Creating a list to store the already evaluated attribute type
        List<AttributeType> evaluatedATs = new ArrayList<AttributeType>();

        // Checking attributes of the original entry
        for ( Attribute originalAttribute : originalEntry )
        {
            AttributeType originalAttributeType = originalAttribute.getAttributeType();

            // We're only working on 'userApplications' attributes
            if ( originalAttributeType.getUsage() == UsageEnum.USER_APPLICATIONS )
            {
                Attribute destinationAttribute = destinationEntry.get( originalAttributeType );
                
                if ( destinationAttribute == null )
                {
                    // Creating a modification for the removed AT
                    Modification modification = new DefaultModification();
                    modification.setOperation( ModificationOperation.REMOVE_ATTRIBUTE );
                    modification.setAttribute( new DefaultAttribute( originalAttribute.getAttributeType() ) );

                    modificationEntry.addModification( modification );
                }
                else
                {
                    // Comparing both attributes
                    compareAttributes( originalAttribute, destinationAttribute, modificationEntry );
                }

                evaluatedATs.add( originalAttributeType );
            }
        }

        // Checking attributes of the destination entry
        for ( Attribute destinationAttribute : destinationEntry )
        {
            AttributeType destinationAttributeType = destinationAttribute.getAttributeType();

            // We're only working on 'userApplications' attributes
            if ( destinationAttributeType.getUsage() == UsageEnum.USER_APPLICATIONS )
            {
                // Checking if the current AT has already been evaluated
                if ( !evaluatedATs.contains( destinationAttributeType ) )
                {
                    // Creating a modification for the added AT
                    Modification modification = new DefaultModification();
                    modification.setOperation( ModificationOperation.ADD_ATTRIBUTE );
                    Attribute attribute = new DefaultAttribute( destinationAttributeType );
                    modification.setAttribute( attribute );

                    for ( Value<?> value : destinationAttribute )
                    {
                        try
                        {
                            attribute.add( value );
                        }
                        catch ( LdapInvalidAttributeValueException liave )
                        {
                            // TODO : handle the exception
                        }
                    }

                    modificationEntry.addModification( modification );
                }
            }
        }
    }


    /**
     * Compares the two given attributes.
     *
     * @param originalAttribute the original attribute
     * @param destinationAttribute the destination attribute
     * @param modificationEntry the modification LDIF entry holding the modifications between both attributes
     */
    private static void compareAttributes( Attribute originalAttribute, Attribute destinationAttribute,
        LdifEntry modificationEntry )
    {
        // Special case for 'objectClass' attribute, due to a bug in OpenLDAP
        // which does not allow us to modify the 'objectClass' attribute
        if ( !SchemaConstants.OBJECT_CLASS_AT.equalsIgnoreCase( originalAttribute.getUpId() ) )
        {
            // Checking if the two attributes are equivalent
            if ( !originalAttribute.equals( destinationAttribute ) )
            {
                // Creating a modification for the modified AT values
                Modification modification = new DefaultModification();
                modification.setOperation( ModificationOperation.REPLACE_ATTRIBUTE );
                modification.setAttribute( destinationAttribute );

                modificationEntry.addModification( modification );
            }
        }
    }
}
