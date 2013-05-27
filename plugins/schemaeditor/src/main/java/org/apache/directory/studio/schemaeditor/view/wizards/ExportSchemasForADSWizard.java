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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.directory.api.converter.schema.AttributeTypeHolder;
import org.apache.directory.api.converter.schema.ObjectClassHolder;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to export schemas for ApacheDS.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportSchemasForADSWizard extends Wizard implements IExportWizard
{
    /** The selected schemas */
    private Schema[] selectedSchemas = new Schema[0];

    // The pages of the wizard
    private ExportSchemasForADSWizardPage page;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // Creating pages
        page = new ExportSchemasForADSWizardPage();
        page.setSelectedSchemas( selectedSchemas );

        // Adding pages
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Saving the dialog settings
        page.saveDialogSettings();

        // Getting the schemas to be exported and where to export them
        final Schema[] selectedSchemas = page.getSelectedSchemas();
        int exportType = page.getExportType();
        if ( exportType == ExportSchemasAsXmlWizardPage.EXPORT_MULTIPLE_FILES )
        {
            final String exportDirectory = page.getExportDirectory();
            try
            {
                getContainer().run( false, true, new IRunnableWithProgress()
                {
                    public void run( IProgressMonitor monitor )
                    {
                        monitor.beginTask(
                            Messages.getString( "ExportSchemasForADSWizard.ExportingSchemas" ), selectedSchemas.length ); //$NON-NLS-1$
                        for ( Schema schema : selectedSchemas )
                        {
                            monitor.subTask( schema.getSchemaName() );

                            StringBuffer sb = new StringBuffer();
                            DateFormat format = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.MEDIUM );
                            Date date = new Date();
                            sb
                                .append( NLS
                                    .bind(
                                        Messages.getString( "ExportSchemasForADSWizard.GeneratedByApacheComment" ), new String[] { format.format( date ) } ) ); //$NON-NLS-1$

                            try
                            {
                                toLdif( schema, sb );

                                BufferedWriter buffWriter = new BufferedWriter( new FileWriter( exportDirectory + "/" //$NON-NLS-1$
                                    + schema.getSchemaName() + ".ldif" ) ); //$NON-NLS-1$
                                buffWriter.write( sb.toString() );
                                buffWriter.close();
                            }
                            catch ( Exception e )
                            {
                                PluginUtils
                                    .logError(
                                        NLS
                                            .bind(
                                                Messages.getString( "ExportSchemasForADSWizard.ErrorSavingSchema" ), new String[] { schema.getSchemaName() } ), e ); //$NON-NLS-1$
                                ViewUtils
                                    .displayErrorMessageDialog(
                                        Messages.getString( "ExportSchemasForADSWizard.Error" ), NLS.bind( Messages.getString( "ExportSchemasForADSWizard.ErrorSavingSchema" ), new String[] { schema.getSchemaName() } ) ); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            monitor.worked( 1 );
                        }
                        monitor.done();
                    }
                } );
            }
            catch ( InvocationTargetException e )
            {
                // Nothing to do (it will never occur)
            }
            catch ( InterruptedException e )
            {
                // Nothing to do.
            }
        }
        else if ( exportType == ExportSchemasAsXmlWizardPage.EXPORT_SINGLE_FILE )
        {
            final String exportFile = page.getExportFile();
            try
            {
                getContainer().run( false, true, new IRunnableWithProgress()
                {
                    public void run( IProgressMonitor monitor )
                    {
                        monitor.beginTask( Messages.getString( "ExportSchemasForADSWizard.ExportingSchemas" ), 1 ); //$NON-NLS-1$

                        StringBuffer sb = new StringBuffer();
                        DateFormat format = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.MEDIUM );
                        Date date = new Date();
                        sb
                            .append( NLS
                                .bind(
                                    Messages.getString( "ExportSchemasForADSWizard.GeneratedByApacheComment" ), new String[] { format.format( date ) } ) ); //$NON-NLS-1$

                        for ( Schema schema : selectedSchemas )
                        {
                            try
                            {
                                toLdif( schema, sb );
                            }
                            catch ( Exception e )
                            {
                                PluginUtils
                                    .logError(
                                        NLS
                                            .bind(
                                                Messages.getString( "ExportSchemasForADSWizard.ErrorSavingSchema" ), new String[] { schema.getSchemaName() } ), e ); //$NON-NLS-1$
                                ViewUtils
                                    .displayErrorMessageDialog(
                                        Messages.getString( "ExportSchemasForADSWizard.Error" ), NLS.bind( Messages.getString( "ExportSchemasForADSWizard.ErrorSavingSchema" ), new String[] { schema.getSchemaName() } ) ); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }

                        try
                        {
                            BufferedWriter buffWriter = new BufferedWriter( new FileWriter( exportFile ) );
                            buffWriter.write( sb.toString() );
                            buffWriter.close();
                        }
                        catch ( IOException e )
                        {
                            PluginUtils.logError(
                                Messages.getString( "ExportSchemasForADSWizard.ErrorSavingSchemas" ), e ); //$NON-NLS-1$
                            ViewUtils
                                .displayErrorMessageDialog(
                                    Messages.getString( "ExportSchemasForADSWizard.Error" ), Messages.getString( "ExportSchemasForADSWizard.ErrorSavingSchemas" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        monitor.worked( 1 );
                        monitor.done();
                    }
                } );
            }
            catch ( InvocationTargetException e )
            {
                // Nothing to do (it will never occur)
            }
            catch ( InterruptedException e )
            {
                // Nothing to do.
            }
        }

        return true;
    }


    /**
     * Converts the given schema as its LDIF for ApacheDS representation and stores it into the given StringBuffer.
     *
     * @param schema
     *      the schema
     * @param sb
     *      the StringBuffer
     * @throws NamingException
     *      if an error occurs during the conversion
     * @throws LdapException
     */
    private void toLdif( Schema schema, StringBuffer sb ) throws NamingException, LdapException
    {
        sb
            .append( NLS
                .bind(
                    Messages.getString( "ExportSchemasForADSWizard.SchemaComment" ), new String[] { schema.getSchemaName().toUpperCase() } ) ); //$NON-NLS-1$

        sb.append( "dn: cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: metaSchema\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "cn: " + schema.getSchemaName() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        String[] schemaDependencies = getSchemaDependencies( schema );
        for ( String schemaName : schemaDependencies )
        {
            sb.append( "m-dependencies: " + schemaName + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Attribute Types Node
        sb.append( "dn: ou=attributeTypes, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: attributetypes\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generating LDIF for Attributes Types
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            AttributeTypeHolder holder = new AttributeTypeHolder( at.getOid() );
            holder.setCollective( at.isCollective() );
            holder.setDescription( at.getDescription() );
            holder.setEquality( at.getEqualityOid() );
            
            List<String> names = new ArrayList<String>();
            
            for ( String name : at.getNames() )
            {
                names.add( name );
            }
            
            holder.setNames( names );
            
            holder.setNoUserModification( !at.isUserModifiable() );
            holder.setObsolete( at.isObsolete() );
            holder.setOrdering( at.getOrderingOid() );
            holder.setSingleValue( at.isSingleValued() );
            holder.setSubstr( at.getSubstringOid() );
            holder.setSuperior( at.getSuperiorOid() );
            holder.setSyntax( at.getSyntaxOid() );

            if ( at.getSyntaxLength() > 0 )
            {
                holder.setOidLen( at.getSyntaxLength() );
            }

            holder.setUsage( at.getUsage() );

            sb.append( holder.toLdif( schema.getSchemaName() ) + "\n" ); //$NON-NLS-1$
        }

        // Generation the Comparators Node
        sb.append( "dn: ou=comparators, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: comparators\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the DIT Content Rules Node
        sb.append( "dn: ou=ditContentRules, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: ditcontentrules\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the DIT Structure RulesNode
        sb.append( "dn: ou=ditStructureRules, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: ditstructurerules\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Matching Rules Node
        sb.append( "dn: ou=matchingRules, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: matchingrules\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Matching Rule Use Node
        sb.append( "dn: ou=matchingRuleUse, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: matchingruleuse\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Name Forms Node
        sb.append( "dn: ou=nameForms, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: nameforms\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Normalizers Node
        sb.append( "dn: ou=normalizers, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: normalizers\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Object Classes Node
        sb.append( "dn: ou=objectClasses, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: objectClasses\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generating LDIF for Object Classes
        List<MutableObjectClass> sortedObjectClasses = getSortedObjectClasses( schema.getObjectClasses() );
        for ( ObjectClass oc : sortedObjectClasses )
        {
            ObjectClassHolder holder = new ObjectClassHolder( oc.getOid() );
            holder.setClassType( oc.getType() );
            holder.setDescription( oc.getDescription() );
            List<String> mayList = new ArrayList<String>();
            for ( String may : oc.getMayAttributeTypeOids() )
            {
                mayList.add( may );
            }
            holder.setMay( mayList );
            List<String> mustList = new ArrayList<String>();
            for ( String must : oc.getMustAttributeTypeOids() )
            {
                mustList.add( must );
            }
            holder.setMust( mustList );
            List<String> names = new ArrayList<String>();
            for ( String name : oc.getNames() )
            {
                names.add( name );
            }
            holder.setNames( names );
            List<String> superiorList = new ArrayList<String>();
            for ( String superior : oc.getSuperiorOids() )
            {
                superiorList.add( superior );
            }
            holder.setSuperiors( superiorList );
            holder.setObsolete( oc.isObsolete() );

            sb.append( holder.toLdif( schema.getSchemaName() ) + "\n" ); //$NON-NLS-1$
        }

        // Generation the Syntax Checkers Node
        sb.append( "dn: ou=syntaxCheckers, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: syntaxcheckers\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$

        // Generation the Syntaxes Node
        sb.append( "dn: ou=syntaxes, cn=" + schema.getSchemaName() + ", ou=schema\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append( "objectclass: organizationalUnit\n" ); //$NON-NLS-1$
        sb.append( "objectclass: top\n" ); //$NON-NLS-1$
        sb.append( "ou: syntaxes\n" ); //$NON-NLS-1$
        sb.append( "\n" ); //$NON-NLS-1$
    }


    /**
     * Sorts the object classes by hierarchy.
     *
     * @param objectClasses the unsorted object classes
     * @return the sorted object classes
     */
    private List<MutableObjectClass> getSortedObjectClasses( List<MutableObjectClass> objectClasses )
    {
        // clone the unsorted list
        List<MutableObjectClass> unsortedObjectClasses = new ArrayList<MutableObjectClass>( objectClasses );

        // list of all existing names
        Set<String> objectClassNames = new HashSet<String>();
        for ( ObjectClass oc : unsortedObjectClasses )
        {
            for ( String name : oc.getNames() )
            {
                objectClassNames.add( Strings.toLowerCase( name ) );
            }
        }

        // sort object classes
        List<MutableObjectClass> sortedObjectClasses = new ArrayList<MutableObjectClass>();
        Set<String> movedObjectClasses = new HashSet<String>();
        boolean moved = true;
        while ( !unsortedObjectClasses.isEmpty() && moved )
        {
            moved = false;
            Iterator<MutableObjectClass> unsortedIterator = unsortedObjectClasses.iterator();
            while ( unsortedIterator.hasNext() )
            {
                MutableObjectClass oc = unsortedIterator.next();
                for ( String superName : oc.getSuperiorOids() )
                {
                    if ( !objectClassNames.contains( Strings.toLowerCase( superName ) )
                        || movedObjectClasses.contains( Strings.toLowerCase( superName ) ) )
                    {
                        unsortedIterator.remove();
                        sortedObjectClasses.add( oc );
                        for ( String name : oc.getNames() )
                        {
                            movedObjectClasses.add( Strings.toLowerCase( name ) );
                        }
                        moved = true;
                        break;
                    }
                }
            }
        }

        // add the rest
        for ( MutableObjectClass oc : unsortedObjectClasses )
        {
            sortedObjectClasses.add( oc );
        }

        return sortedObjectClasses;
    }


    /**
     * Gets the schema dependencies.
     *
     * @param schema
     *      the schema
     * @return
     *      an array containing the names of all the schemas which the given
     *      schema depends on
     */
    private String[] getSchemaDependencies( Schema schema )
    {
        Set<String> schemaNames = new HashSet<String>();
        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();

        // Looping on Attribute Types
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            // Superior
            String supName = at.getSuperiorOid();
            if ( supName != null )
            {
                AttributeType sup = schemaHandler.getAttributeType( supName );
                if ( sup != null )
                {
                    if ( !Strings.toLowerCase( schema.getSchemaName() ).equals(
                        Strings.toLowerCase( sup.getSchemaName() ) ) )
                    {
                        schemaNames.add( sup.getSchemaName() );
                    }
                }
            }
        }

        // Looping on Object Classes
        for ( ObjectClass oc : schema.getObjectClasses() )
        {
            // Superiors
            List<String> supNames = oc.getSuperiorOids();
            if ( supNames != null )
            {
                for ( String supName : oc.getSuperiorOids() )
                {
                    ObjectClass sup = schemaHandler.getObjectClass( supName );
                    if ( sup != null )
                    {
                        if ( !Strings.toLowerCase( schema.getSchemaName() ).equals(
                            Strings.toLowerCase( sup.getSchemaName() ) ) )
                        {
                            schemaNames.add( sup.getSchemaName() );
                        }
                    }
                }
            }

            // Mays
            List<String> mayNames = oc.getMayAttributeTypeOids();
            if ( mayNames != null )
            {
                for ( String mayName : mayNames )
                {
                    AttributeType may = schemaHandler.getAttributeType( mayName );
                    if ( may != null )
                    {
                        if ( !Strings.toLowerCase( schema.getSchemaName() ).equals(
                            Strings.toLowerCase( may.getSchemaName() ) ) )
                        {
                            schemaNames.add( may.getSchemaName() );
                        }
                    }
                }

            }

            // Musts
            List<String> mustNames = oc.getMustAttributeTypeOids();
            if ( mustNames != null )
            {
                for ( String mustName : oc.getMustAttributeTypeOids() )
                {
                    AttributeType must = schemaHandler.getAttributeType( mustName );
                    if ( must != null )
                    {
                        if ( !Strings.toLowerCase( schema.getSchemaName() ).equals(
                            Strings.toLowerCase( must.getSchemaName() ) ) )
                        {
                            schemaNames.add( must.getSchemaName() );
                        }
                    }
                }
            }
        }

        return schemaNames.toArray( new String[0] );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );
    }


    /**
     * Sets the selected projects.
     *
     * @param schemas
     *      the schemas
     */
    public void setSelectedSchemas( Schema[] schemas )
    {
        selectedSchemas = schemas;
    }
}
