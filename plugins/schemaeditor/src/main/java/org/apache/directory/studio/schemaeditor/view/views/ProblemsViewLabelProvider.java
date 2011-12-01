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
package org.apache.directory.studio.schemaeditor.view.views;


import org.apache.directory.shared.ldap.model.exception.LdapSchemaException;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NoAliasWarning;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaWarning;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaErrorWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWarningWrapper;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the LabelProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProblemsViewLabelProvider extends LabelProvider implements ITableLabelProvider
{
    /**
     * {@inheritDoc}
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        if ( columnIndex == 0 )
        {
            if ( element instanceof SchemaErrorWrapper )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_PROBLEMS_ERROR );
            }
            else if ( element instanceof SchemaWarningWrapper )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_PROBLEMS_WARNING );
            }
            else if ( element instanceof Folder )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_PROBLEMS_GROUP );
            }
        }

        // Default
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getColumnText( Object element, int columnIndex )
    {
        if ( element instanceof SchemaErrorWrapper )
        {
            SchemaErrorWrapper errorWrapper = ( SchemaErrorWrapper ) element;

            if ( columnIndex == 0 )
            {
                return getMessage( errorWrapper.getLdapSchemaException() );
            }
            else if ( columnIndex == 1 )
            {
                return getDisplayName( errorWrapper.getLdapSchemaException().getSourceObject() );
            }
        }
        else if ( element instanceof SchemaWarningWrapper )
        {
            SchemaWarningWrapper warningWrapper = ( SchemaWarningWrapper ) element;

            if ( columnIndex == 0 )
            {
                return getMessage( warningWrapper.getSchemaWarning() );
            }
            else if ( columnIndex == 1 )
            {
                String name = warningWrapper.getSchemaWarning().getSource().getName();

                if ( ( name != null ) && ( !name.equals( "" ) ) ) //$NON-NLS-1$
                {
                    return name;
                }
                else
                {
                    return warningWrapper.getSchemaWarning().getSource().getOid();
                }
            }
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;
            if ( columnIndex == 0 )
            {
                return folder.getName() + " (" + folder.getChildren().size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                return ""; //$NON-NLS-1$
            }
        }

        // Default
        return element.toString();
    }


    private String getMessage( LdapSchemaException exception )
    {
        if ( exception != null )
        {
            switch ( exception.getCode() )
            {
            // Codes for all Schema Objects
                case NAME_ALREADY_REGISTERED:
                    return getMessageNameAlreadyRegistered( exception );
                case OID_ALREADY_REGISTERED:
                    return getMessageOidAlreadyRegistered( exception );
                case NONEXISTENT_SCHEMA:
                    return getMessageNonExistentSchema( exception );

                    // Codes for Attribute Type
                case AT_NONEXISTENT_SUPERIOR:
                    return getMessageATNonExistentSuperior( exception );
                case AT_CANNOT_SUBTYPE_COLLECTIVE_AT:
                    return getMessageATCannotSubtypeCollectiveAT( exception );
                case AT_CYCLE_TYPE_HIERARCHY:
                    return getMessageATCycleTypeHierarchy( exception );
                case AT_NONEXISTENT_SYNTAX:
                    return getMessageATNonExistentSyntax( exception );
                case AT_SYNTAX_OR_SUPERIOR_REQUIRED:
                    return getMessageATSyntaxOrSuperiorRequired( exception );
                case AT_NONEXISTENT_EQUALITY_MATCHING_RULE:
                    return getMessageATNonExistentEqualityMatchingRule( exception );
                case AT_NONEXISTENT_ORDERING_MATCHING_RULE:
                    return getMessageATNonExistentOrderingMatchingRule( exception );
                case AT_NONEXISTENT_SUBSTRING_MATCHING_RULE:
                    return getMessageATNonExistentSubstringMatchingRule( exception );
                case AT_MUST_HAVE_SAME_USAGE_THAN_SUPERIOR:
                    return getMessageATMustHaveSameUsageThanSuperior( exception );
                case AT_USER_APPLICATIONS_USAGE_MUST_BE_USER_MODIFIABLE:
                    return getMessageATUserApplicationsUsageMustBeUserModifiable( exception );
                case AT_COLLECTIVE_MUST_HAVE_USER_APPLICATIONS_USAGE:
                    return getMessageATCollectiveMustHaveUserApplicationsUsage( exception );
                case AT_COLLECTIVE_CANNOT_BE_SINGLE_VALUED:
                    return getMessageATCollectiveCannotBeSingleValued( exception );

                    // Codes for Object Class
                case OC_ABSTRACT_MUST_INHERIT_FROM_ABSTRACT_OC:
                    return getMessageOCAbstractMustInheritFromAbstractOC( exception );
                case OC_AUXILIARY_CANNOT_INHERIT_FROM_STRUCTURAL_OC:
                    return getMessageOCAuxiliaryCannotInheritFromStructuralOC( exception );
                case OC_STRUCTURAL_CANNOT_INHERIT_FROM_AUXILIARY_OC:
                    return getMessageOCStructuralCannotInheritFromAuxiliaryOC( exception );
                case OC_NONEXISTENT_SUPERIOR:
                    return getMessageOCNonExistentSuperior( exception );
                case OC_CYCLE_CLASS_HIERARCHY:
                    return getMessageOCCycleClassHierarchy( exception );
                case OC_COLLECTIVE_NOT_ALLOWED_IN_MUST:
                    return getMessageOCCollectiveNotAllowedInMust( exception );
                case OC_COLLECTIVE_NOT_ALLOWED_IN_MAY:
                    return getMessageOCCollectiveNotAllowedInMay( exception );
                case OC_DUPLICATE_AT_IN_MUST:
                    return getMessageOCDuplicateATInMust( exception );
                case OC_DUPLICATE_AT_IN_MAY:
                    return getMessageOCDuplicateATInMay( exception );
                case OC_NONEXISTENT_MUST_AT:
                    return getMessageOCNonExistentMustAT( exception );
                case OC_NONEXISTENT_MAY_AT:
                    return getMessageOCNonExistentMayAT( exception );
                case OC_DUPLICATE_AT_IN_MAY_AND_MUST:
                    return getMessageOCDuplicateATInMayAndMust( exception );

                    // Codes for Matching Rule
                case MR_NONEXISTENT_SYNTAX:
                    return getMessageMRNonExistentSyntax( exception );
            }
        }

        return ""; //$NON-NLS-1$
    }


    private String getMessage( SchemaWarning warning )
    {

        if ( warning instanceof NoAliasWarning )
        {
            NoAliasWarning noAliasWarning = ( NoAliasWarning ) warning;
            SchemaObject source = noAliasWarning.getSource();
            if ( source instanceof AttributeType )
            {
                return NLS
                    .bind(
                        Messages.getString( "ProblemsViewLabelProvider.NoAliasWarningAttributeType" ), new String[] { source.getOid() } ); //$NON-NLS-1$
            }
            else if ( source instanceof ObjectClass )
            {
                return NLS
                    .bind(
                        Messages.getString( "ProblemsViewLabelProvider.NoAliasWarningObjectClass" ), new String[] { source.getOid() } ); //$NON-NLS-1$
            }
        }

        return ""; //$NON-NLS-1$
    }


    private String getMessageNameAlreadyRegistered( LdapSchemaException exception )
    {
        SchemaObject duplicate = exception.getOtherObject();
        String message = null;

        if ( duplicate instanceof AttributeType )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.NameAlreadyRegisteredAT" ); //$NON-NLS-1$
        }
        else if ( duplicate instanceof ObjectClass )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.NameAlreadyRegisteredOC" ); //$NON-NLS-1$
        }

        return NLS.bind( message, new String[]
            { exception.getRelatedId(), duplicate.getOid() } );
    }


    private String getMessageOidAlreadyRegistered( LdapSchemaException exception )
    {
        SchemaObject duplicate = exception.getOtherObject();
        String message = null;

        if ( duplicate instanceof AttributeType )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.OidAlreadyRegisteredAT" ); //$NON-NLS-1$
        }
        else if ( duplicate instanceof ObjectClass )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.OidAlreadyRegisteredOC" ); //$NON-NLS-1$
        }

        return NLS.bind( message, new String[]
            { exception.getRelatedId(), duplicate.getName() } );
    }


    private String getMessageNonExistentSchema( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.NonExistentSchema" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATNonExistentSuperior( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.ATNonExistentSuperior" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATCannotSubtypeCollectiveAT( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.ATCannotSubtypeCollectiveAT" ); //$NON-NLS-1$;
    }


    private String getMessageATCycleTypeHierarchy( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.ATCycleTypeHierarchy" ); //$NON-NLS-1$;
    }


    private String getMessageATNonExistentSyntax( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.NonExistentSyntax" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATSyntaxOrSuperiorRequired( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.ATSyntaxOrSuperiorRequired" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATNonExistentEqualityMatchingRule( LdapSchemaException exception )
    {
        return NLS.bind(
            Messages.getString( "ProblemsViewLabelProvider.ATNonExistentEqualityMatchingRule" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATNonExistentOrderingMatchingRule( LdapSchemaException exception )
    {
        return NLS.bind(
            Messages.getString( "ProblemsViewLabelProvider.ATNonExistentOrderingMatchingRule" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATNonExistentSubstringMatchingRule( LdapSchemaException exception )
    {
        return NLS.bind(
            Messages.getString( "ProblemsViewLabelProvider.ATNonExistentSubstringMatchingRule" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageATMustHaveSameUsageThanSuperior( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.ATMustHaveSameUsageThanSuperior" );
    }


    private String getMessageATUserApplicationsUsageMustBeUserModifiable( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.ATUserApplicationsUsageMustBeUserModifiable" );
    }


    private String getMessageATCollectiveMustHaveUserApplicationsUsage( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.ATCollectiveMustHaveUserApplicationsUsage" );
    }


    private String getMessageATCollectiveCannotBeSingleValued( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.ATCollectiveCannotBeSingleValued" );
    }


    private String getMessageOCAbstractMustInheritFromAbstractOC( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.OCAbstractMustInheritFromAbstractOC" );
    }


    private String getMessageOCAuxiliaryCannotInheritFromStructuralOC( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.OCAuxiliaryCannotInheritFromStructuralOC" );
    }


    private String getMessageOCStructuralCannotInheritFromAuxiliaryOC( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.OCStructuralCannotInheritFromAuxiliaryOC" );
    }


    private String getMessageOCNonExistentSuperior( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCNonExistentSuperior" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageOCCycleClassHierarchy( LdapSchemaException exception )
    {
        return Messages.getString( "ProblemsViewLabelProvider.OCCycleClassHierarchy" );
    }


    private String getMessageOCCollectiveNotAllowedInMust( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCCollectiveNotAllowedInMust" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageOCCollectiveNotAllowedInMay( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCCollectiveNotAllowedInMay" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageOCDuplicateATInMust( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCDuplicateATInMust" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageOCDuplicateATInMay( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCDuplicateATInMay" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageOCNonExistentMustAT( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCNonExistentMustAT" ), new String[]//$NON-NLS-1$
            { exception.getRelatedId() } );
    }


    private String getMessageOCNonExistentMayAT( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCNonExistentMayAT" ), new String[] //$NON-NLS-1$
            { exception.getRelatedId() } );

    }


    private String getMessageOCDuplicateATInMayAndMust( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.OCDuplicateATInMayAndMust" ), new String[] //$NON-NLS-1$;
            { exception.getRelatedId() } );
    }


    private String getMessageMRNonExistentSyntax( LdapSchemaException exception )
    {
        return NLS.bind( Messages.getString( "ProblemsViewLabelProvider.NonExistentSyntax" ), new String[] //$NON-NLS-1$;
            { exception.getRelatedId() } );
    }


    /**
     * Gets the displayable name of the given SchemaObject.
     *
     * @param so
     *      the SchemaObject
     * @return
     *      the displayable name of the given SchemaObject
     */
    private String getDisplayName( SchemaObject so )
    {
        if ( so != null )
        {
            SchemaObject schemaObject = getSchemaObject( so );
            if ( schemaObject != null )
            {
                String name = schemaObject.getName();
                if ( ( name != null ) && ( !name.equals( "" ) ) ) // $NON-NLS-1$
                {
                    return name;
                }
                else
                {
                    return so.getOid();
                }
            }
            else
            {
                return so.getOid();
            }
        }

        return ""; // $NON-NLS-1$
    }


    /**
     * Gets the original {@link SchemaObject} from the {@link SchemaHandler}.
     *
     * @param so
     *      the schema object
     * @return
     *      the original schema object from the schema handler.
     */
    private SchemaObject getSchemaObject( SchemaObject so )
    {
        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        SchemaObject schemaObject = null;

        if ( so instanceof AttributeType )
        {
            schemaObject = schemaHandler.getAttributeType( so.getOid() );
        }
        else if ( so instanceof LdapSyntax )
        {
            schemaObject = schemaHandler.getSyntax( so.getOid() );
        }
        else if ( so instanceof MatchingRule )
        {
            schemaObject = schemaHandler.getMatchingRule( so.getOid() );
        }
        else if ( so instanceof ObjectClass )
        {
            schemaObject = schemaHandler.getObjectClass( so.getOid() );
        }

        return schemaObject;
    }
}
