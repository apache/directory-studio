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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.shared.ldap.schema.AbstractSchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SchemaImpl;
import org.apache.directory.studio.schemaeditor.view.dialogs.MessageDialogWithTextarea;
import org.apache.directory.studio.schemaeditor.view.wizards.MergeSchemasSelectionWizardPage.AttributeTypeFolder;
import org.apache.directory.studio.schemaeditor.view.wizards.MergeSchemasSelectionWizardPage.ObjectClassFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to merge schema projects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MergeSchemasWizard extends Wizard implements IImportWizard
{
    // The pages of the wizard
    private MergeSchemasSelectionWizardPage selectionPage;
    private MergeSchemasOptionsWizardPage optionsPage;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
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
            new MessageDialogWithTextarea( getShell(), Messages.getString( "MergeSchemasWizard.MergeResultTitle" ),
                Messages.getString( "MergeSchemasWizard.MergeResultMessage" ), sb.toString() ).open();
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
                List<AttributeTypeImpl> sourceAttributeTypes = atf.schema.getAttributeTypes();
                for ( AttributeTypeImpl sourceAttributeType : sourceAttributeTypes )
                {
                    mergeAttributeType( sourceAttributeType, targetProject, targetSchema, processedObjects,
                        errorMessages, replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                }
            }
            if ( sourceObject instanceof ObjectClassFolder )
            {
                ObjectClassFolder ocf = ( ObjectClassFolder ) sourceObject;
                Schema targetSchema = getTargetSchema( ocf.schema.getProject(), targetProject, targetSchemas );
                List<ObjectClassImpl> sourceObjectClasses = ocf.schema.getObjectClasses();
                for ( ObjectClassImpl sourceObjectClass : sourceObjectClasses )
                {
                    mergeObjectClass( sourceObjectClass, targetProject, targetSchema, processedObjects, errorMessages,
                        replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
                }
            }
            if ( sourceObject instanceof AttributeTypeImpl )
            {
                AttributeTypeImpl at = ( AttributeTypeImpl ) sourceObject;
                Schema targetSchema = getTargetSchema( at.getSchemaObject().getProject(), targetProject, targetSchemas );
                mergeAttributeType( at, targetProject, targetSchema, processedObjects, errorMessages,
                    replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
            }
            if ( sourceObject instanceof ObjectClassImpl )
            {
                ObjectClassImpl oc = ( ObjectClassImpl ) sourceObject;
                Schema targetSchema = getTargetSchema( oc.getSchemaObject().getProject(), targetProject, targetSchemas );
                mergeObjectClass( oc, targetProject, targetSchema, processedObjects, errorMessages,
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
        String targetSchemaName = "merge-from-" + sourceProject.getName();
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
            targetSchema = new SchemaImpl( targetSchemaName );
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
        List<AttributeTypeImpl> sourceAttributeTypes = sourceSchema.getAttributeTypes();
        for ( AttributeTypeImpl sourceAttributeType : sourceAttributeTypes )
        {
            mergeAttributeType( sourceAttributeType, targetProject, targetSchema, processedObjects, errorMessages,
                replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
        }

        List<ObjectClassImpl> sourceObjectClasses = sourceSchema.getObjectClasses();
        for ( ObjectClassImpl sourceObjectClass : sourceObjectClasses )
        {
            mergeObjectClass( sourceObjectClass, targetProject, targetSchema, processedObjects, errorMessages,
                replaceUnknownSyntax, mergeDependencies, pullUpAttributes );
        }
    }


    /**
     * Merges the given attribute type to the targetSchema. 
     */
    private void mergeAttributeType( AttributeTypeImpl sourceAttributeType, Project targetProject, Schema targetSchema,
        Set<Object> processedObjects, List<String> errorMessages, boolean replaceUnknownSyntax,
        boolean mergeDependencies, boolean pullUpAttributes )
    {
        if ( processedObjects.contains( sourceAttributeType ) )
        {
            return;
        }
        processedObjects.add( sourceAttributeType );

        // check if attribute (identified by OID or name) already exists in the project
        AttributeTypeImpl targetAttributeType = targetProject.getSchemaHandler().getAttributeType(
            sourceAttributeType.getOid() );
        if ( targetAttributeType == null )
        {
            for ( String name : sourceAttributeType.getNamesRef() )
            {
                targetAttributeType = targetProject.getSchemaHandler().getAttributeType( name );
                if ( targetAttributeType != null )
                {
                    break;
                }
            }
        }

        // check if OID or alias name already exist in target project
        boolean oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isAliasOrOidAlreadyTaken(
            sourceAttributeType.getOid() );
        if ( !oidOrAliasAlreadyTaken )
        {
            for ( String name : sourceAttributeType.getNamesRef() )
            {
                oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isAliasOrOidAlreadyTaken( name );
                if ( oidOrAliasAlreadyTaken )
                {
                    break;
                }
            }
        }

        if ( targetAttributeType != null )
        {
            errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.AttributeTypeExistsInTargetProject" ),
                getIdString( sourceAttributeType ) ) );
        }
        else
        {
            if ( oidOrAliasAlreadyTaken )
            {
                errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.OidOrAliasAlreadyTaken" ),
                    getIdString( sourceAttributeType ) ) );
            }
            else
            {
                // remove attribute type if already there from previous merge
                AttributeTypeImpl at = targetSchema.getAttributeType( sourceAttributeType.getOid() );
                if ( at != null )
                {
                    targetSchema.removeAttributeType( at );
                }

                // clone attribute type
                AttributeTypeImpl clonedAttributeType = new AttributeTypeImpl( sourceAttributeType.getOid() );
                clonedAttributeType.setOid( sourceAttributeType.getOid() );
                clonedAttributeType.setNames( sourceAttributeType.getNamesRef() );
                clonedAttributeType.setDescription( sourceAttributeType.getDescription() );
                clonedAttributeType.setSuperiorName( sourceAttributeType.getSuperiorName() );
                clonedAttributeType.setUsage( sourceAttributeType.getUsage() );
                clonedAttributeType.setSyntaxOid( sourceAttributeType.getSyntaxOid() );
                clonedAttributeType.setLength( sourceAttributeType.getLength() );
                clonedAttributeType.setObsolete( sourceAttributeType.isObsolete() );
                clonedAttributeType.setCollective( sourceAttributeType.isCollective() );
                clonedAttributeType.setSingleValue( sourceAttributeType.isSingleValue() );
                clonedAttributeType.setCanUserModify( sourceAttributeType.isCanUserModify() );
                clonedAttributeType.setEqualityName( sourceAttributeType.getEqualityName() );
                clonedAttributeType.setOrderingName( sourceAttributeType.getOrderingName() );
                clonedAttributeType.setSubstrName( sourceAttributeType.getSubstrName() );
                clonedAttributeType.setSchema( targetSchema.getName() );
                clonedAttributeType.setSchemaObject( targetSchema );

                // if no/unknown syntax: set "Directory String" syntax and appropriate matching rules
                if ( replaceUnknownSyntax )
                {
                    if ( clonedAttributeType.getSyntaxOid() == null
                        || targetProject.getSchemaHandler().getSyntax( clonedAttributeType.getSyntaxOid() ) == null )
                    {
                        errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.ReplacedSyntax" ),
                            new String[]
                                { getIdString( sourceAttributeType ), clonedAttributeType.getSyntaxOid(),
                                    "1.3.6.1.4.1.1466.115.121.1.15 (Directory String)" } ) );
                        clonedAttributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.15" );
                        clonedAttributeType.setEqualityName( "caseIgnoreMatch" );
                        clonedAttributeType.setOrderingName( null );
                        clonedAttributeType.setSubstrName( "caseIgnoreSubstringsMatch" );
                    }
                }
                // TODO: if unknown (single) matching rule: set appropriate matching rule according to syntax
                // TODO: if no (all) matching rules: set appropriate matching rules according to syntax

                // merge dependencies: super attribute type
                if ( mergeDependencies )
                {
                    String superiorName = clonedAttributeType.getSuperiorName();
                    if ( superiorName != null )
                    {
                        AttributeTypeImpl superiorAttributeType = sourceAttributeType.getSchemaObject().getProject()
                            .getSchemaHandler().getAttributeType( superiorName );
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
    private void mergeObjectClass( ObjectClassImpl sourceObjectClass, Project targetProject, Schema targetSchema,
        Set<Object> processedObjects, List<String> errorMessages, boolean replaceUnknownSyntax,
        boolean mergeDependencies, boolean pullUpAttributes )
    {
        if ( processedObjects.contains( sourceObjectClass ) )
        {
            return;
        }
        processedObjects.add( sourceObjectClass );

        // check if object class (identified by OID or alias name) already exists in the target project
        ObjectClassImpl targetObjectClass = targetProject.getSchemaHandler()
            .getObjectClass( sourceObjectClass.getOid() );
        if ( targetObjectClass == null )
        {
            for ( String name : sourceObjectClass.getNamesRef() )
            {
                targetObjectClass = targetProject.getSchemaHandler().getObjectClass( name );
                if ( targetObjectClass != null )
                {
                    break;
                }
            }
        }

        // check if OID or alias name already exist in target project
        boolean oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isAliasOrOidAlreadyTaken(
            sourceObjectClass.getOid() );
        if ( !oidOrAliasAlreadyTaken )
        {
            for ( String name : sourceObjectClass.getNamesRef() )
            {
                oidOrAliasAlreadyTaken = targetProject.getSchemaHandler().isAliasOrOidAlreadyTaken( name );
                if ( oidOrAliasAlreadyTaken )
                {
                    break;
                }
            }
        }

        if ( targetObjectClass != null )
        {
            errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.ObjectClassExistsInTargetProject" ),
                getIdString( sourceObjectClass ) ) );
        }
        else
        {
            if ( oidOrAliasAlreadyTaken )
            {
                errorMessages.add( NLS.bind( Messages.getString( "MergeSchemasWizard.OidOrAliasAlreadyTaken" ),
                    getIdString( sourceObjectClass ) ) );
            }
            else
            {
                // remove object class if already there from previous merge
                ObjectClassImpl oc = targetSchema.getObjectClass( sourceObjectClass.getOid() );
                if ( oc != null )
                {
                    targetSchema.removeObjectClass( oc );
                }

                // create object class
                ObjectClassImpl clonedObjectClass = new ObjectClassImpl( sourceObjectClass.getOid() );
                clonedObjectClass.setOid( sourceObjectClass.getOid() );
                clonedObjectClass.setNames( sourceObjectClass.getNamesRef() );
                clonedObjectClass.setDescription( sourceObjectClass.getDescription() );
                clonedObjectClass.setSuperClassesNames( sourceObjectClass.getSuperClassesNames() );
                clonedObjectClass.setType( sourceObjectClass.getType() );
                clonedObjectClass.setObsolete( sourceObjectClass.isObsolete() );
                clonedObjectClass.setMustNamesList( sourceObjectClass.getMustNamesList() );
                clonedObjectClass.setMayNamesList( sourceObjectClass.getMayNamesList() );
                clonedObjectClass.setSchema( targetSchema.getName() );
                clonedObjectClass.setSchemaObject( targetSchema );

                // merge dependencies: super object classes and must/may attributes
                if ( mergeDependencies )
                {
                    String[] superClassesNames = clonedObjectClass.getSuperClassesNames();
                    if ( superClassesNames != null )
                    {
                        for ( String superClassName : superClassesNames )
                        {
                            if ( superClassName != null )
                            {
                                ObjectClassImpl superSourceObjectClass = sourceObjectClass.getSchemaObject()
                                    .getProject().getSchemaHandler().getObjectClass( superClassName );
                                ObjectClassImpl superTargetObjectClass = targetProject.getSchemaHandler()
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

                    String[] mustNamesList = clonedObjectClass.getMustNamesList();
                    String[] mayNamesList = clonedObjectClass.getMayNamesList();
                    List<String> attributeNames = new ArrayList<String>();
                    if ( mustNamesList != null )
                    {
                        attributeNames.addAll( Arrays.asList( mustNamesList ) );
                    }
                    if ( mayNamesList != null )
                    {
                        attributeNames.addAll( Arrays.asList( mayNamesList ) );
                    }
                    for ( String attributeName : attributeNames )
                    {
                        if ( attributeName != null )
                        {
                            AttributeTypeImpl attributeType = sourceObjectClass.getSchemaObject().getProject()
                                .getSchemaHandler().getAttributeType( attributeName );
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


    private void pullUpAttributes( ObjectClassImpl targetObjectClass, ObjectClassImpl sourceSuperObjectClass,
        ObjectClassImpl targetSuperObjectClass )
    {
        // must
        Set<String> sourceMustAttributeNames = new HashSet<String>();
        fetchAttributes( sourceMustAttributeNames, sourceSuperObjectClass, true );
        Set<String> targetMustAttributeNames = new HashSet<String>();
        fetchAttributes( targetMustAttributeNames, targetSuperObjectClass, true );
        sourceMustAttributeNames.removeAll( targetMustAttributeNames );
        if ( !sourceMustAttributeNames.isEmpty() )
        {
            sourceMustAttributeNames.addAll( Arrays.asList( targetObjectClass.getMustNamesList() ) );
            targetObjectClass.setMustNamesList( sourceMustAttributeNames.toArray( new String[0] ) );
        }

        // may
        Set<String> sourceMayAttributeNames = new HashSet<String>();
        fetchAttributes( sourceMayAttributeNames, sourceSuperObjectClass, false );
        Set<String> targetMayAttributeNames = new HashSet<String>();
        fetchAttributes( targetMayAttributeNames, targetSuperObjectClass, false );
        sourceMayAttributeNames.removeAll( targetMayAttributeNames );
        if ( !sourceMayAttributeNames.isEmpty() )
        {
            sourceMayAttributeNames.addAll( Arrays.asList( targetObjectClass.getMayNamesList() ) );
            targetObjectClass.setMayNamesList( sourceMayAttributeNames.toArray( new String[0] ) );
        }
    }


    private void fetchAttributes( Set<String> attributeNameList, ObjectClassImpl oc, boolean must )
    {
        String[] attributeNames = must ? oc.getMustNamesList() : oc.getMayNamesList();
        attributeNameList.addAll( Arrays.asList( attributeNames ) );

        for ( String superClassName : oc.getSuperClassesNames() )
        {
            ObjectClassImpl superObjectClass = oc.getSchemaObject().getProject().getSchemaHandler().getObjectClass(
                superClassName );
            fetchAttributes( attributeNameList, superObjectClass, must );
        }
    }


    private String getIdString( AbstractSchemaObject schemaObject )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );
        if ( schemaObject.getNamesRef() != null )
        {
            for ( String name : schemaObject.getNamesRef() )
            {
                sb.append( name );
                sb.append( ',' );
            }
        }
        sb.append( schemaObject.getOid() );
        sb.append( ']' );
        return sb.toString();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }
}
