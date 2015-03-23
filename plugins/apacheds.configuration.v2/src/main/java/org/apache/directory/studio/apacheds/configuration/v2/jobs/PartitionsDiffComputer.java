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
import java.util.Collections;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
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
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * A class used to computer a difference between two partitions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionsDiffComputer
{
    /** The original partition */
    private Partition originalPartition;

    /** The destination partition */
    private Partition destinationPartition;


    /**
     * Creates an instance of the PartitionsDiffComputer class
     */
    public PartitionsDiffComputer()
    {
    }


    /**
     * Creates an instance of the PartitionsDiffComputer class, with an original partition
     * and a distination partition
     * @param originalPartition The original partition
     * @param destinationPartition The destination partition
     */
    public PartitionsDiffComputer( Partition originalPartition, Partition destinationPartition )
    {
        this.originalPartition = originalPartition;
        this.destinationPartition = destinationPartition;
    }


    /**
     * Compute the difference between two partitions
     * @return The list of modified entries
     * @throws Exception If the comparison has filed
     */
    public List<LdifEntry> computeModifications() throws Exception
    {
        // Using the original partition suffix as base 
        // '*' for all user attributes, '+' for all operational attributes
        return computeModifications( originalPartition.getSuffixDn(), new String[]
            { SchemaConstants.ALL_USER_ATTRIBUTES, SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES } );
    }


    /**
     * Compute the difference between two partitions
     * @param attributeIds The list of attributes to compare
     * @return The list of modified entries
     * @throws Exception If the comparison has filed
     */
    public List<LdifEntry> computeModifications( String[] attributeIds ) throws Exception
    {
        return computeModifications( originalPartition.getSuffixDn(), attributeIds );
    }


    /**
     * Compare two partitions, checking a list of attributes
     * 
     * @param baseDn The base DN for the partitions
     * @param attributeIds The list of attributes to check
     * @return The list of modifications
     * @throws Exception If the comparison has filed
     */
    public List<LdifEntry> computeModifications( Dn baseDn, String[] attributeIds ) throws Exception
    {
        // Checking partitions
        checkPartitions();

        return comparePartitions( baseDn, attributeIds );
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
            throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.OriginalPartitionIsNull" ) );
        }
        else
        {
            if ( !originalPartition.isInitialized() )
            {
                throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.OriginalPartitionNotInitialized" ) );
            }
            else if ( originalPartition.getSuffixDn() == null )
            {
                throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.OriginalSuffixIsNull" ) );
            }
        }

        // Checking the destination partition
        if ( destinationPartition == null )
        {
            throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.DestinationPartitionIsNull" ) );
        }
        else
        {
            if ( !destinationPartition.isInitialized() )
            {
                throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.DestinationPartitionNotInitialized" ) );
            }
            else if ( destinationPartition.getSuffixDn() == null )
            {
                throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.DestinationPartitionIsNull" ) );
            }
        }
    }


    /**
     * Compare the two partitions.
     *
     * @param baseDn the base Dn
     * @param attributeIds the IDs of the attributes
     * @return a list containing LDIF entries with all modifications
     * @throws Exception If the operation failed
     */
    public List<LdifEntry> comparePartitions( Dn baseDn, String[] attributeIds ) throws PartitionsDiffException
    {
        // Creating the list containing all modifications
        List<LdifEntry> modifications = new ArrayList<LdifEntry>();

        try
        {
            // Looking up the original base entry
            Entry originalBaseEntry = originalPartition.lookup( new LookupOperationContext( null, baseDn, attributeIds ) );
            
            if ( originalBaseEntry == null )
            {
                throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.PartitionNotFound" ) );
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
                            .getModifications().size() > 0 ) )
                    {
                        // Adding the modification entry to the list
                        modifications.add( modificationEntry );
                    }
                }

                // Creating a search operation context to get the children of the current entry
                SearchOperationContext soc = new SearchOperationContext( null, originalEntry.getDn(),
                    SearchScope.ONELEVEL,
                    FilterParser.parse( originalPartition.getSchemaManager(), LdapConstants.OBJECT_CLASS_STAR ), attributeIds ); //$NON-NLS-1$
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = originalPartition.search( soc );

                while ( cursor.next() )
                {
                    originalEntries.add( ( ( ClonedServerEntry ) cursor.get() ).getClonedEntry() );
                }
            }

            // Reversing the list to allow deletion of leafs first (otherwise we would be deleting
            // higher nodes with children first).
            // Order for modified entries does not matter.
            Collections.reverse( modifications );

            // Looking up the destination base entry
            Entry destinationBaseEntry = destinationPartition
                .lookup( new LookupOperationContext( null, baseDn, attributeIds ) );
            
            if ( destinationBaseEntry == null )
            {
                ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                    new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                        Messages.getString( "PartitionDiffComputer.PartitionNotFound" ) ) );
                throw new PartitionsDiffException( Messages.getString( "PartitionDiffComputer.PartitionNotFound" ) );
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
                    FilterParser.parse( originalPartition.getSchemaManager(), "(objectClass=*)" ), attributeIds ); //$NON-NLS-1$
                soc.setAliasDerefMode( AliasDerefMode.DEREF_ALWAYS );

                // Looking for the children of the current entry
                EntryFilteringCursor cursor = destinationPartition.search( soc );

                while ( cursor.next() )
                {
                    destinationEntries.add( ( ( ClonedServerEntry ) cursor.get() ).getClonedEntry() );
                }
            }
        }
        catch ( Exception e )
        {
            ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                    Messages.getString( "PartitionDiffComputer.ComparePartitions" ) ) );

            throw new PartitionsDiffException( e );
        }

        return modifications;
    }


    /**
     * Compares the two given entries.
     *
     * @param originalEntry the original entry
     * @param destinationEnt the destination entry
     * @param modificationEntry the modification LDIF entry holding the modifications 
     *      between both entries
     */
    private void compareEntries( Entry originalEntry, Entry destinationEntry, LdifEntry modificationEntry )
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
                            ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                                new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                                    Messages.getString( "PartitionDiffComputer.InvalidAttributeException" ) ) );

                            ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                                new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                                    liave.getLocalizedMessage() ) );
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
     * @param modificationEntry the modification LDIF entry holding the modifications 
     *      between both attributes
     */
    private void compareAttributes( Attribute originalAttribute, Attribute destinationAttribute,
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
                Attribute attribute = new DefaultAttribute( originalAttribute.getAttributeType() );
                modification.setAttribute( attribute );

                try
                {
                    attribute.add( originalValue );
                }
                catch ( LdapInvalidAttributeValueException liave )
                {
                    ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                        new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                            Messages.getString( "PartitionDiffComputer.InvalidAttributeException" ) ) );

                    ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                        new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                            liave.getLocalizedMessage() ) );
                }

                modificationEntry.addModification( modification );
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
                Attribute attribute = new DefaultAttribute( originalAttribute.getAttributeType() );
                modification.setAttribute( attribute );

                try
                {
                    attribute.add( destinationValue );
                }
                catch ( LdapInvalidAttributeValueException liave )
                {
                    ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                        new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                            Messages.getString( "PartitionDiffComputer.InvalidAttributeException" ) ) );

                    ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                        new Status( IStatus.ERROR, ApacheDS2ConfigurationPluginConstants.PLUGIN_ID, 
                            liave.getLocalizedMessage() ) );
                }

                modificationEntry.addModification( modification );
            }
        }
    }


    /**
     * Gets the original partition.
     *
     * @return the original partition
     */
    public Partition getOriginalPartition()
    {
        return originalPartition;
    }


    /**
     * Sets the original partition.
     *
     * @param originalPartition the original partition
     */
    public void setOriginalPartition( Partition originalPartition )
    {
        this.originalPartition = originalPartition;
    }


    /**
     * Gets the destination partition.
     *
     * @return the destination partition
     */
    public Partition getDestinationPartition()
    {
        return destinationPartition;
    }


    /**
     * Sets the destination partition.
     *
     * @param destinationPartition the destination partition
     */
    public void setDestinationPartition( Partition destinationPartition )
    {
        this.destinationPartition = destinationPartition;
    }
}
