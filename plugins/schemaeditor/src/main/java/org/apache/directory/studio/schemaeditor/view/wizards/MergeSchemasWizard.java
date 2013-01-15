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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.api.ldap.model.schema.AbstractSchemaObject;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.dialogs.MessageDialogWithTextarea;
import org.apache.directory.studio.schemaeditor.view.wizards.MergeSchemasSelectionWizardPage.AttributeTypeFolder;
import org.apache.directory.studio.schemaeditor.view.wizards.MergeSchemasSelectionWizardPage.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wizards.MergeSchemasSelectionWizardPage.ObjectClassFolder;
import org.apache.directory.studio.schemaeditor.view.wizards.MergeSchemasSelectionWizardPage.ObjectClassWrapper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to merge schema projects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MergeSchemasWizard extends Wizard implements IImportWizard
{
    // The pages of the wizard
    private MergeSchemasSelectionWizardPage selectionPage;
    private MergeSchemasOptionsWizardPage optionsPage;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // Creating pages
        selectionPage = new MergeSchemasSelectionWizardPage();
        optionsPage = new MergeSchemasOptionsWizardPage();

        // Adding pages
        addPage( selectionPage );
        addPage( optionsPage );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        Object[] sourceObjects = selectionPage.getSelectedObjects();

        boolean replaceUnknownSyntax = optionsPage.isReplaceUnknownSyntax();
        boolean mergeDependencies = optionsPage.isMergeDependencies();
        boolean pullUpAttributes = optionsPage.isPullUpAttributes();

        List<String> errorMessages = new ArrayList<String>();
        mergeObjects( sourceObjects, errorMessages, replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
        if ( !errorMessages.isEmpty() )
        {
            StringBuilder sb = new StringBuilder();
            for ( String errorMessage : errorMessages )
            {
                sb.append( errorMessage );
                sb.append( '\n' );
            }
            new MessageDialogWithTextarea( getShell(), Messages.getString( "MergeSchemasWizard.MergeResultTitle" ), //$NON-NLS-1$
                Messages.getString( "MergeSchemasWizard.MergeResultMessage" ), sb.toString() ).open(); //$NON-NLS-1$
        }

        return true;
    }


    private void mergeObjects( Object[] sourceObjects, List<String> errorMessages, boolean replaceUnknownSyntax,
        boolean mergeDependencies, boolean pullUpAttributes )
    {
        /*
         * List of already processed schema objects. Used to avoid that schema objects are process multiple time.
         */
        Set<Object> processedObjects = new HashSet<Object>();

        /*
         * List of created target schemas.
         */
        Map<String, Schema> targetSchemas = new HashMap<String, Schema>();

        Project targetProject = Activator.getDefault().getProjectsHandler().getOpenProject();

        // merge all source objects to the target project
        for ( Object sourceObject : sourceObjects )
        {
            if ( sourceObject instanceof Project )
            {
                Project sourceProject = ( Project ) sourceObject;
                for ( Schema sourceSchema : sourceProject.getSchemaHandler().getSchemas() )
                {
                    Schema targetSchema = getTargetSchema( sourceSchema.getProject(), targetProject, targetSchemas );
                    mergeSchema( sourceSchema, targetProject, targetSchema, processedObjects, errorMessages,
                        replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                }
            }
            if ( sourceObject instanceof Schema )
            {
                Schema sourceSchema = ( Schema ) sourceObject;
                Schema targetSchema = getTargetSchema( sourceSchema.getProject(), targetProject, targetSchemas );
                mergeSchema( sourceSchema, targetProject, targetSchema, processedObjects, errorMessages,
                    replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
            }
            if ( sourceObject instanceof AttributeTypeFolder )
            {
                AttributeTypeFolder atf = ( AttributeTypeFolder ) sourceObject;
                Schema targetSchema = getTargetSchema( atf.schema.getProject(), targetProject, targetSchemas );
                List<AttributeType> sourceAttributeTypes = atf.schema.getAttributeTypes();
                for ( AttributeType sourceAttributeType : sourceAttributeTypes )
                {
                    mergeAttributeType( sourceAttributeType, targetProject, targetSchema, processedObjects,
                        errorMessages, replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                }
            }
            if ( sourceObject instanceof ObjectClassFolder )
            {
                ObjectClassFolder ocf = ( ObjectClassFolder ) sourceObject;
                Schema targetSchema = getTargetSchema( ocf.schema.getProject(), targetProject, targetSchemas );
                List<MutableObjectClass> sourceObjectClasses = ocf.schema.getObjectClasses();
                for ( ObjectClass sourceObjectClass : sourceObjectClasses )
                {
                    mergeObjectClass( sourceObjectClass, targetProject, targetSchema, processedObjects, errorMessages,
                        replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                }
            }
            if ( sourceObject instanceof AttributeTypeWrapper )
            {
                AttributeTypeWrapper atw = ( AttributeTypeWrapper ) sourceObject;
                Schema targetSchema = getTargetSchema( atw.folder.schema.getProject(), targetProject, targetSchemas );
                mergeAttributeType( atw.attributeType, targetProject, targetSchema, processedObjects, errorMessages,
                    replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
            }
            if ( sourceObject instanceof ObjectClassWrapper )
            {
                ObjectClassWrapper ocw = ( ObjectClassWrapper ) sourceObject;
                Schema targetSchema = getTargetSchema( ocw.folder.schema.getProject(), targetProject, targetSchemas );
                mergeObjectClass( ocw.objectClass, targetProject, targetSchema, processedObjects, errorMessages,
                    replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
            }
        }

        //add created target schemas to project
        for ( Schema targetSchema : targetSchemas.values() )
        {
            if ( !targetProject.getSchemaHandler().getSchemas().contains( targetSchema ) )
            {
                targetProject.getSchemaHandler().addSchema( targetSchema );
            }
        }
    }


    private Schema getTargetSchema( Project sourceProject, Project targetProject, Map<String, Schema> targetSchemas )
    {
        String targetSchemaName = "merge-from-" + sourceProject.getName(); //$NON-NLS-1$
        Schema targetSchema = targetProject.getSchemaHandler().getSchema( targetSchemaName );
        if ( targetSchema != null )
        {
            targetProject.getSchemaHandler().removeSchema( targetSchema );
        }
        else if ( targetSchemas.containsKey( targetSchemaName ) )
        {
            targetSchema = targetSchemas.get( targetSchemaName );
        }
        else
        {
            targetSchema = new Schema( targetSchemaName );
            targetSchema.setProject( targetProject );
        }
        targetSchemas.put( targetSchemaName, targetSchema );
        return targetSchema;
    }


    /**
     * Merges all attribute types and object classes and form the given sourceSchema to the targetSchema.
     */
    private void mergeSchema( Schema sourceSchema, Project targetProject, Schema targetSchema,
        Set<Object> processedObjects, List<String> errorMessages, boolean replaceUnknownSyntax,
        boolean mergeDependencies, boolean pullUpAttributes )
    {
        List<AttributeType> sourceAttributeTypes = sourceSchema.getAttributeTypes();
        for ( AttributeType sourceAttributeType : sourceAttributeTypes )
        {
            mergeAttributeType( sourceAttributeType, targetProject, targetSchema, processedObjects, errorMessages,
                replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
        }

        List<MutableObjectClass> sourceObjectClasses = sourceSchema.getObjectClasses();
        for ( ObjectClass sourceObjectClass : sourceObjectClasses )
        {
            mergeObjectClass( sourceObjectClass, targetProject, targetSchema, processedObjects, errorMessages,
                replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
        }
    }


    /**
     * Merges the given attribute type to the targetSchema.
     */
    private void mergeAttributeType( AttributeType sourceAttributeType, Project targetProject, Schema targetSchema,
        Set<Object> processedObjects, List<String> errorMessages, boolean replaceUnknownSyntax,
        boolean mergeDependencies, boolean pullUpAttributes )
    {
        if ( processedObjects.contains( sourceAttributeType ) )
        {
            return;
        }
        processedObjects.add( sourceAttributeType );

        // check if attribute (identified by OID or name) already exists in the project
        AttributeType targetAttributeType = targetProject.getSchemaHandler().getAttributeType(
            sourceAttributeType.getOid() );
        if ( targetAttributeType == null )
        {
            for ( String name : sourceAttributeType.getNames() )
            {
                targetAttributeType = targetProject.getSchemaHandler().getAttributeType( name );
                if ( targetAttributeType != null )
                {
                    break;
                }
            }
        }

        // check if OID or alias name already exist in target project
        boolean oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isOidAlreadyTaken(
            sourceAttributeType.getOid() );
        if ( !oidOrAliasAlreadyTaken )
        {
            for ( String name : sourceAttributeType.getNames() )
            {
                oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isAliasAlreadyTakenForAttributeType( name );
                if ( oidOrAliasAlreadyTaken )
                {
                    break;
                }
            }
        }

        if ( targetAttributeType != null )
        {
            errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.AttributeTypeExistsInTargetProject" ), //$NON-NLS-1$
                getIdString( sourceAttributeType ) ) );
        }
        else
        {
            if ( oidOrAliasAlreadyTaken )
            {
                errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.OidOrAliasAlreadyTaken" ), //$NON-NLS-1$
                    getIdString( sourceAttributeType ) ) );
            }
            else
            {
                // remove attribute type if already there from previous merge
                AttributeType at = targetSchema.getAttributeType( sourceAttributeType.getOid() );
                if ( at != null )
                {
                    targetSchema.removeAttributeType( at );
                }

                // clone attribute type
                MutableAttributeType clonedAttributeType = new MutableAttributeType( sourceAttributeType.getOid() );
                clonedAttributeType.setNames( sourceAttributeType.getNames() );
                clonedAttributeType.setDescription( sourceAttributeType.getDescription() );
                clonedAttributeType.setSuperiorOid( sourceAttributeType.getSuperiorOid() );
                clonedAttributeType.setUsage( sourceAttributeType.getUsage() );
                clonedAttributeType.setSyntaxOid( sourceAttributeType.getSyntaxOid() );
                clonedAttributeType.setSyntaxLength( sourceAttributeType.getSyntaxLength() );
                clonedAttributeType.setObsolete( sourceAttributeType.isObsolete() );
                clonedAttributeType.setCollective( sourceAttributeType.isCollective() );
                clonedAttributeType.setSingleValued( sourceAttributeType.isSingleValued() );
                clonedAttributeType.setUserModifiable( sourceAttributeType.isUserModifiable() );
                clonedAttributeType.setEqualityOid( sourceAttributeType.getEqualityOid() );
                clonedAttributeType.setOrderingOid( sourceAttributeType.getOrderingOid() );
                clonedAttributeType.setSubstringOid( sourceAttributeType.getSubstringOid() );
                clonedAttributeType.setSchemaName( targetSchema.getSchemaName() );

                // if no/unknown syntax: set "Directory String" syntax and appropriate matching rules
                if ( replaceUnknownSyntax )
                {
                    if ( clonedAttributeType.getSyntaxOid() == null
                        || targetProject.getSchemaHandler().getSyntax( clonedAttributeType.getSyntaxOid() ) == null )
                    {
                        errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.ReplacedSyntax" ), //$NON-NLS-1$
                            new String[]
                                {
                                    getIdString( sourceAttributeType ),
                                    clonedAttributeType.getSyntaxOid(),
                                    "1.3.6.1.4.1.1466.115.121.1.15 (Directory String)" } ) ); //$NON-NLS-1$
                        clonedAttributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.15" ); //$NON-NLS-1$
                        clonedAttributeType.setEqualityOid( "caseIgnoreMatch" ); //$NON-NLS-1$
                        clonedAttributeType.setOrderingOid( null );
                        clonedAttributeType.setSubstringOid( "caseIgnoreSubstringsMatch" ); //$NON-NLS-1$
                    }
                }
                // TODO: if unknown (single) matching rule: set appropriate matching rule according to syntax
                // TODO: if no (all) matching rules: set appropriate matching rules according to syntax

                // merge dependencies: super attribute type
                if ( mergeDependencies )
                {
                    String superiorName = clonedAttributeType.getSuperiorOid();
                    if ( superiorName != null )
                    {
                        AttributeType superiorAttributeType = Activator.getDefault().getSchemaHandler()
                            .getAttributeType( superiorName );
                        if ( superiorAttributeType != null )
                        {
                            mergeAttributeType( superiorAttributeType, targetProject, targetSchema, processedObjects,
                                errorMessages, replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                        }
                    }
                }

                targetSchema.addAttributeType( clonedAttributeType );
            }
        }
    }


    /**
     * Merges the given object class to the targetSchema.
     */
    private void mergeObjectClass( ObjectClass sourceObjectClass, Project targetProject, Schema targetSchema,
        Set<Object> processedObjects, List<String> errorMessages, boolean replaceUnknownSyntax,
        boolean mergeDependencies, boolean pullUpAttributes )
    {
        if ( processedObjects.contains( sourceObjectClass ) )
        {
            return;
        }
        processedObjects.add( sourceObjectClass );

        // check if object class (identified by OID or alias name) already exists in the target project
        ObjectClass targetObjectClass = targetProject.getSchemaHandler()
            .getObjectClass( sourceObjectClass.getOid() );
        if ( targetObjectClass == null )
        {
            for ( String name : sourceObjectClass.getNames() )
            {
                targetObjectClass = targetProject.getSchemaHandler().getObjectClass( name );
                if ( targetObjectClass != null )
                {
                    break;
                }
            }
        }

        // check if OID or alias name already exist in target project
        boolean oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isOidAlreadyTaken(
            sourceObjectClass.getOid() );
        if ( !oidOrAliasAlreadyTaken )
        {
            for ( String name : sourceObjectClass.getNames() )
            {
                oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isAliasAlreadyTakenForObjectClass( name );
                if ( oidOrAliasAlreadyTaken )
                {
                    break;
                }
            }
        }

        if ( targetObjectClass != null )
        {
            errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.ObjectClassExistsInTargetProject" ), //$NON-NLS-1$
                getIdString( sourceObjectClass ) ) );
        }
        else
        {
            if ( oidOrAliasAlreadyTaken )
            {
                errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.OidOrAliasAlreadyTaken" ), //$NON-NLS-1$
                    getIdString( sourceObjectClass ) ) );
            }
            else
            {
                // remove object class if already there from previous merge
                ObjectClass oc = targetSchema.getObjectClass( sourceObjectClass.getOid() );
                if ( oc != null )
                {
                    targetSchema.removeObjectClass( oc );
                }

                // create object class
                MutableObjectClass clonedObjectClass = new MutableObjectClass( sourceObjectClass.getOid() );
                clonedObjectClass.setOid( sourceObjectClass.getOid() );
                clonedObjectClass.setNames( sourceObjectClass.getNames() );
                clonedObjectClass.setDescription( sourceObjectClass.getDescription() );
                clonedObjectClass.setSuperiorOids( sourceObjectClass.getSuperiorOids() );
                clonedObjectClass.setType( sourceObjectClass.getType() );
                clonedObjectClass.setObsolete( sourceObjectClass.isObsolete() );
                clonedObjectClass.setMustAttributeTypeOids( sourceObjectClass.getMustAttributeTypeOids() );
                clonedObjectClass.setMayAttributeTypeOids( sourceObjectClass.getMayAttributeTypeOids() );
                clonedObjectClass.setSchemaName( targetSchema.getSchemaName() );

                // merge dependencies: super object classes and must/may attributes
                if ( mergeDependencies )
                {
                    List<String> superClassesNames = clonedObjectClass.getSuperiorOids();
                    if ( superClassesNames != null )
                    {
                        for ( String superClassName : superClassesNames )
                        {
                            if ( superClassName != null )
                            {
                                ObjectClass superSourceObjectClass = Activator.getDefault().getSchemaHandler()
                                    .getObjectClass( superClassName );
                                ObjectClass superTargetObjectClass = targetProject.getSchemaHandler()
                                    .getObjectClass( superClassName );
                                if ( superSourceObjectClass != null )
                                {
                                    if ( superTargetObjectClass == null )
                                    {
                                        mergeObjectClass( superSourceObjectClass, targetProject, targetSchema,
                                            processedObjects, errorMessages, replaceUnknownSyntax, mergeDependencies,
                                            pullUpAttributes );
                                    }
                                    else
                                    {
                                        // pull-up may and must attributes to this OC if super already exists in target
                                        if ( pullUpAttributes )
                                        {
                                            pullUpAttributes( clonedObjectClass, superSourceObjectClass,
                                                superTargetObjectClass );
                                        }
                                    }
                                }
                            }
                        }
                    }

                    List<String> mustNamesList = clonedObjectClass.getMustAttributeTypeOids();
                    List<String> mayNamesList = clonedObjectClass.getMayAttributeTypeOids();
                    List<String> attributeNames = new ArrayList<String>();
                    if ( mustNamesList != null )
                    {
                        attributeNames.addAll( mustNamesList );
                    }
                    if ( mayNamesList != null )
                    {
                        attributeNames.addAll( mayNamesList );
                    }
                    for ( String attributeName : attributeNames )
                    {
                        if ( attributeName != null )
                        {
                            AttributeType attributeType = Activator.getDefault().getSchemaHandler()
                                .getAttributeType( attributeName );
                            if ( attributeType != null )
                            {
                                mergeAttributeType( attributeType, targetProject, targetSchema, processedObjects,
                                    errorMessages, replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                            }
                        }
                    }
                }

                targetSchema.addObjectClass( clonedObjectClass );
            }
        }
    }


    private void pullUpAttributes( MutableObjectClass targetObjectClass, ObjectClass sourceSuperObjectClass,
        ObjectClass targetSuperObjectClass )
    {
        // must
        Set<String> sourceMustAttributeNames = new HashSet<String>();
        fetchAttributes( sourceMustAttributeNames, sourceSuperObjectClass, true );
        Set<String> targetMustAttributeNames = new HashSet<String>();
        fetchAttributes( targetMustAttributeNames, targetSuperObjectClass, true );
        sourceMustAttributeNames.removeAll( targetMustAttributeNames );
        if ( !sourceMustAttributeNames.isEmpty() )
        {
            sourceMustAttributeNames.addAll( targetObjectClass.getMustAttributeTypeOids() );
            targetObjectClass.setMustAttributeTypeOids( new ArrayList<String>( sourceMustAttributeNames ) );
        }

        // may
        Set<String> sourceMayAttributeNames = new HashSet<String>();
        fetchAttributes( sourceMayAttributeNames, sourceSuperObjectClass, false );
        Set<String> targetMayAttributeNames = new HashSet<String>();
        fetchAttributes( targetMayAttributeNames, targetSuperObjectClass, false );
        sourceMayAttributeNames.removeAll( targetMayAttributeNames );
        if ( !sourceMayAttributeNames.isEmpty() )
        {
            sourceMayAttributeNames.addAll( targetObjectClass.getMayAttributeTypeOids() );
            targetObjectClass.setMayAttributeTypeOids( new ArrayList<String>( sourceMayAttributeNames ) );
        }
    }


    private void fetchAttributes( Set<String> attributeNameList, ObjectClass oc, boolean must )
    {
        List<String> attributeNames = must ? oc.getMustAttributeTypeOids() : oc.getMayAttributeTypeOids();
        attributeNameList.addAll( attributeNames );

        for ( String superClassName : oc.getSuperiorOids() )
        {
            ObjectClass superObjectClass = Activator.getDefault().getSchemaHandler().getObjectClass(
                superClassName );
            fetchAttributes( attributeNameList, superObjectClass, must );
        }
    }


    private String getIdString( AbstractSchemaObject schemaObject )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );
        if ( schemaObject.getNames() != null )
        {
            for ( String name : schemaObject.getNames() )
            {
                sb.append( name );
                sb.append( ',' );
            }
        }
        sb.append( schemaObject.getOid() );
        sb.append( ']' );
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }
}
