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
package org.apache.directory.studio.apacheds.configuration.v2.jobs;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.server.core.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.interceptor.context.LookupOperationContext;
import org.apache.directory.server.core.interceptor.context.SearchOperationContext;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.DefaultModification;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.*;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.filter.FilterParser;
import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.model.ldif.ChangeType;
import org.apache.directory.shared.ldap.model.ldif.LdifEntry;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.AttributeTypeOptions;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.SchemaUtils;
import org.apache.directory.shared.ldap.model.schema.UsageEnum;


public class PartitionsDiffComputer
{
    /** The original partition */
    private Partition originalPartition;

    /** The destination partition */
    private Partition destinationPartition;


    public PartitionsDiffComputer()
    {
    }


    public PartitionsDiffComputer( Partition originalPartition, Partition destinationPartition )
    {
        this.originalPartition = originalPartition;
        this.destinationPartition = destinationPartition;
    }


    public List<LdifEntry> computeModifications() throws Exception
    {
        // Using the original partition suffix as base 
        // '*' for all user attributes, '+' for all operational attributes
        return computeModifications( originalPartition.getSuffix(), new String[]
            { "*", "+" } );
    }


    public List<LdifEntry> computeModifications( String[] attributeIds ) throws Exception
    {
        return computeModifications( originalPartition.getSuffix(), attributeIds );
    }


    public List<LdifEntry> computeModifications( Dn baseDn, String[] attributeIds ) throws Exception
    {
        // Checking partitions
        checkPartitions();

        return comparePartitions(baseDn, attributeIds );
    }


    /**
     * Checks the partitions.
     *
     * @throws PartitionsDiffException
     */
    private void checkPartitions() throws PartitionsDiffException
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
            else if ( originalPartition.getSuffix() == null )
            {
                throw new PartitionsDiffException( "The original suffix is null." );
            }
        }

        // Checking the destination partition
        if ( destinationPartition == null )
        {
            throw new PartitionsDiffException( "The destination partition must not be 'null'." );
        }
        else
        {
            if ( !destinationPartition.isInitialized() )
            {
                throw new PartitionsDiffException( "The destination partition must be intialized." );
            }
            else if ( destinationPartition.getSuffix() == null )
            {
                throw new PartitionsDiffException( "The destination suffix is null." );
            }
        }
    }


    /**
     * Compare the two partitions.
     *
     * @param baseDn
     *      the base Dn
     * @param attributeIds
     *      the IDs of the attributes
     * @return
     *      a list containing LDIF entries with all modifications
     * @throws Exception
     */
    public List<LdifEntry> comparePartitions( Dn baseDn, String[] attributeIds ) throws PartitionsDiffException
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
                Entry destinationEntry = destinationPartition.lookup( new LookupOperationContext( null, originalEntry
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
                            .getModificationItems().size() > 0 ) )
                    {
                        // Adding the modification entry to the list
                        modifications.add( modificationEntry );
                    }
                }

                // Creating a search operation context to get the children of the current entry
                SearchOperationContext soc = new SearchOperationContext( null );
                setReturningAttributes( originalPartition.getSchemaManager(), attributeIds, soc );
                soc.setDn( originalEntry.getDn() );
                soc.setScope( SearchScope.ONELEVEL );
                soc.setFilter( FilterParser.parse(originalPartition.getSchemaManager(), "(objectClass=*)") );
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = originalPartition.search( soc );
                while ( cursor.next() )
                {
                    originalEntries.add( cursor.get().getClonedEntry() );
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
     * Sets the returning attributes to the search operation context.
     *
     * @param schemaManager
     *      the schema manager
     * @param attributeIds
     *      the attribute IDs
     * @param soc
     *      the search operation context
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    private void setReturningAttributes( SchemaManager schemaManager, String[] attributeIds,
        SearchOperationContext soc ) throws LdapException
    {
        if ( attributeIds != null && attributeIds.length != 0 )
        {
            Set<AttributeTypeOptions> returningAttributes = new HashSet<AttributeTypeOptions>();

            for ( String returnAttribute : attributeIds )
            {
                if ( returnAttribute.equals( SchemaConstants.NO_ATTRIBUTE ) )
                {
                    soc.setNoAttributes( true );
                    continue;
                }

                if ( returnAttribute.equals( SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES ) )
                {
                    soc.setAllOperationalAttributes( true );
                    continue;
                }

                if ( returnAttribute.equals( SchemaConstants.ALL_USER_ATTRIBUTES ) )
                {
                    soc.setAllUserAttributes( true );
                    continue;
                }

                String id = SchemaUtils.stripOptions( returnAttribute );
                Set<String> options = SchemaUtils.getOptions( returnAttribute );

                AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( id );
                AttributeTypeOptions attrOptions = new AttributeTypeOptions( attributeType, options );

                returningAttributes.add( attrOptions );
            }

            // reset the noAttrubte flag if it is already set cause that will be ignored if any other AT is requested
            if ( soc.isNoAttributes()
                && ( soc.isAllUserAttributes() || soc.isAllOperationalAttributes() || ( !returningAttributes.isEmpty() ) ) )
            {
                soc.setNoAttributes( false );
            }

            soc.setReturningAttributes( returningAttributes );
        }
    }


    /**
     * Compares the two given entries.
     *
     * @param originalEntry
     *      the original entry
     * @param destinationEntry
     *      the destination entry
     * @param modificationEntry
     *      the modification LDIF entry holding the modifications 
     *      between both entries
     */
    private void compareEntries( Entry originalEntry, Entry destinationEntry, LdifEntry modificationEntry )
    {
        // Creating a list to store the already evaluated attribute type
        List<AttributeType> evaluatedATs = new ArrayList<AttributeType>();

        // Checking attributes of the original entry
        for ( EntryAttribute originalAttribute : originalEntry )
        {
            AttributeType originalAttributeType = originalAttribute.getAttributeType();

            // We're only working on 'userApplications' attributes
            if ( originalAttributeType.getUsage() == UsageEnum.USER_APPLICATIONS )
            {
                EntryAttribute destinationAttribute = destinationEntry.get( originalAttributeType );
                if ( destinationAttribute == null )
                {
                    // Creating a modification for the removed AT
                    Modification modification = new DefaultModification();
                    modification.setOperation( ModificationOperation.REMOVE_ATTRIBUTE );
                    modification.setAttribute( new DefaultEntryAttribute( originalAttribute.getAttributeType() ) );

                    modificationEntry.addModificationItem( modification );
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
        for ( EntryAttribute destinationAttribute : destinationEntry )
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
                    EntryAttribute attribute = new DefaultEntryAttribute( destinationAttributeType );
                    modification.setAttribute( attribute );
                    for ( Value<?> value : destinationAttribute )
                    {
                        attribute.add( value );
                    }

                    modificationEntry.addModificationItem( modification );
                }
            }
        }
    }


    /**
     * Compares the two given attributes.
     *
     * @param originalAttribute
     *      the original attribute
     * @param destinationAttribute
     *      the destination attribute
     * @param modificationEntry
     *      the modification LDIF entry holding the modifications 
     *      between both attributes
     */
    private void compareAttributes( EntryAttribute originalAttribute, EntryAttribute destinationAttribute,
        LdifEntry modificationEntry )
    {
        // Creating a list to store the already evaluated values
        List<Value<?>> evaluatedValues = new ArrayList<Value<?>>();

        // Checking values of the original attribute
        for ( Value<?> originalValue : originalAttribute )
        {
            if ( !destinationAttribute.contains( originalValue ) )
            {
                // Creating a modification for the removed AT value
                Modification modification = new DefaultModification();
                modification.setOperation( ModificationOperation.REMOVE_ATTRIBUTE );
                EntryAttribute attribute = new DefaultEntryAttribute( originalAttribute.getAttributeType() );
                modification.setAttribute( attribute );
                attribute.add( originalValue );

                modificationEntry.addModificationItem( modification );
            }

            evaluatedValues.add( originalValue );
        }

        // Checking values of the destination attribute
        for ( Value<?> destinationValue : destinationAttribute )
        {
            if ( !evaluatedValues.contains( destinationValue ) )
            {
                // Creating a modification for the added AT value
                Modification modification = new DefaultModification();
                modification.setOperation( ModificationOperation.ADD_ATTRIBUTE );
                EntryAttribute attribute = new DefaultEntryAttribute( originalAttribute.getAttributeType() );
                modification.setAttribute( attribute );
                attribute.add( destinationValue );

                modificationEntry.addModificationItem( modification );
            }
        }
    }


    /**
     * Gets the original partition.
     *
     * @return
     *      the original partition
     */
    public Partition getOriginalPartition()
    {
        return originalPartition;
    }


    /**
     * Sets the original partition.
     *
     * @param originalPartition
     *      the original partition
     */
    public void setOriginalPartition( Partition originalPartition )
    {
        this.originalPartition = originalPartition;
    }


    /**
     * Gets the destination partition.
     *
     * @return
     *      the destination partition
     */
    public Partition getDestinationPartition()
    {
        return destinationPartition;
    }


    /**
     * Sets the destination partition.
     *
     * @param destinationPartition
     *      the destination partition
     */
    public void setDestinationPartition( Partition destinationPartition )
    {
        this.destinationPartition = destinationPartition;
    }
}
