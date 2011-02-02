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
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
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
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
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
                return ""; // TODO getMessage( warningWrapper.getSchemaWarning() );
            }
            else if ( columnIndex == 1 )
            {
                return "";
                // TODO
                //                String name = warningWrapper.getSchemaWarning().getSource().getName();
                //
                //                if ( ( name != null ) && ( !name.equals( "" ) ) ) //$NON-NLS-1$
                //                {
                //                    return name;
                //                }
                //                else
                //                {
                //                    return warningWrapper.getSchemaWarning().getSource().getOid();
                //                }
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
                    return getMessageNonExistentOrderingMatchingRule( exception );
                case AT_NONEXISTENT_SUBSTRING_MATCHING_RULE:
                    return getMessageNonExistentSubstringMatchingRule( exception );
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
                    return getMessageOCAbstractOCMustInheritFromAbstractOC( exception );
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

        //        if ( element instanceof DuplicateAliasError )
        //        {
        //            DuplicateAliasError duplicateAliasError = ( DuplicateAliasError ) element;
        //
        //            SchemaObject duplicate = duplicateAliasError.getDuplicate();
        //            if ( duplicate instanceof AttributeType )
        //            {
        //                return NLS
        //                        .bind(
        //                            Messages.getString( "ProblemsViewLabelProvider.DuplicateAliasErrorAttributeType" ), new String[] { duplicateAliasError.getAlias(), duplicate.getOid() } ); //$NON-NLS-1$
        //            }
        //            else if ( duplicate instanceof ObjectClass )
        //            {
        //                return NLS
        //                        .bind(
        //                            Messages.getString( "ProblemsViewLabelProvider.DuplicateAliasErrorObjectClass" ), new String[] { duplicateAliasError.getAlias(), duplicate.getOid() } ); //$NON-NLS-1$
        //            }
        //        }
        //        else if ( element instanceof DuplicateOidError )
        //        {
        //            DuplicateOidError duplicateOidError = ( DuplicateOidError ) element;
        //            SchemaObject duplicate = duplicateOidError.getDuplicate();
        //            if ( duplicate instanceof AttributeType )
        //            {
        //                return NLS
        //                        .bind(
        //                            Messages.getString( "ProblemsViewLabelProvider.DuplicateOidErrorAttributeType" ), new String[] { duplicateOidError.getOid(), duplicate.getName() } ); //$NON-NLS-1$
        //            }
        //            else if ( duplicate instanceof ObjectClass )
        //            {
        //                return NLS
        //                        .bind(
        //                            Messages.getString( "ProblemsViewLabelProvider.DuplicateOidErrorObjectClass" ), new String[] { duplicateOidError.getOid(), duplicate.getName() } ); //$NON-NLS-1$
        //            }
        //        }
        //        else if ( element instanceof NonExistingATSuperiorError )
        //        {
        //            NonExistingATSuperiorError nonExistingATSuperiorError = ( NonExistingATSuperiorError ) element;
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.NonExistingSuperiorAttribute" ), new String[] { nonExistingATSuperiorError.getSuperiorAlias() } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof NonExistingOCSuperiorError )
        //        {
        //            NonExistingOCSuperiorError nonExistingOCSuperiorError = ( NonExistingOCSuperiorError ) element;
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.NonExistingSuperiorObject" ), new String[] { nonExistingOCSuperiorError.getSuperiorAlias() } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof NonExistingMandatoryATError )
        //        {
        //            NonExistingMandatoryATError nonExistingMandatoryATError = ( NonExistingMandatoryATError ) element;
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.NonExistingMandatoryAttribute" ), new String[] { nonExistingMandatoryATError.getAlias() } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof NonExistingOptionalATError )
        //        {
        //            NonExistingOptionalATError nonExistingOptionalATError = ( NonExistingOptionalATError ) element;
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.NonExistingOptionalAttribute" ), new String[] { nonExistingOptionalATError.getAlias() } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof NonExistingSyntaxError )
        //        {
        //            NonExistingSyntaxError nonExistingSyntaxError = ( NonExistingSyntaxError ) element;
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.NonExistingSyntax" ), new String[] { nonExistingSyntaxError.getSyntaxOid() } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof NonExistingMatchingRuleError )
        //        {
        //            NonExistingMatchingRuleError nonExistingMatchingRuleError = ( NonExistingMatchingRuleError ) element;
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.NonExistingMatchingRule" ), new String[] { nonExistingMatchingRuleError.getMatchingRuleAlias() } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof NoAliasWarning )
        //        {
        //            NoAliasWarning noAliasWarning = ( NoAliasWarning ) element;
        //            SchemaObject source = noAliasWarning.getSourceObject();
        //            if ( source instanceof AttributeType )
        //            {
        //                return NLS
        //                        .bind(
        //                            Messages.getString( "ProblemsViewLabelProvider.NoAliasWarningAttributeType" ), new String[] { source.getOid() } ); //$NON-NLS-1$
        //            }
        //            else if ( source instanceof ObjectClass )
        //            {
        //                return NLS
        //                        .bind(
        //                            Messages.getString( "ProblemsViewLabelProvider.NoAliasWarningObjectClass" ), new String[] { source.getOid() } ); //$NON-NLS-1$
        //            }
        //        }
        //        else if ( element instanceof ClassTypeHierarchyError )
        //        {
        //            ClassTypeHierarchyError classTypeHierarchyError = ( ClassTypeHierarchyError ) element;
        //            ObjectClass source = ( ObjectClass ) classTypeHierarchyError.getSourceObject();
        //            ObjectClass superior = ( ObjectClass ) classTypeHierarchyError.getSuperior();
        //            if ( source.getType().equals( ObjectClassTypeEnum.ABSTRACT ) )
        //            {
        //                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
        //                {
        //                    return NLS
        //                            .bind(
        //                                Messages
        //                                    .getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAbstractStructuralObject" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        //                }
        //                else if ( superior.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
        //                {
        //                    return NLS
        //                            .bind(
        //                                Messages
        //                                    .getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAbstractAuxiliaryObject" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        //                }
        //            }
        //            else if ( source.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
        //            {
        //                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
        //                {
        //                    return NLS
        //                            .bind(
        //                                Messages
        //                                    .getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAuxiliaryStructuralObject" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        //                }
        //                else
        //                {
        //                    return NLS
        //                            .bind(
        //                                Messages.getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAuxiliary" ), new String[] { getDisplayName( source ) } ); //$NON-NLS-1$
        //                }
        //            }
        //        }
        //        else if ( element instanceof DifferentUsageAsSuperiorError )
        //        {
        //            DifferentUsageAsSuperiorError differentUsageAsSuperiorError = ( DifferentUsageAsSuperiorError ) element;
        //            AttributeType source = ( AttributeType ) differentUsageAsSuperiorError.getSourceObject();
        //            AttributeType superior = ( AttributeType ) differentUsageAsSuperiorError.getSuperior();
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.AttributeTypeUsage" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        //        }
        //        else if ( element instanceof DifferentCollectiveAsSuperiorError )
        //        {
        //            DifferentCollectiveAsSuperiorError differentCollectiveAsSuperiorError = ( DifferentCollectiveAsSuperiorError ) element;
        //            AttributeType source = ( AttributeType ) differentCollectiveAsSuperiorError.getSourceObject();
        //            AttributeType superior = ( AttributeType ) differentCollectiveAsSuperiorError.getSuperior();
        //            return NLS
        //                    .bind(
        //                        Messages.getString( "ProblemsViewLabelProvider.AttributeTypeCollective" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        //        }

        //        return ""; //$NON-NLS-1$
    }


    private String getMessageNameAlreadyRegistered( LdapSchemaException exception )
    {
        SchemaObject duplicate = exception.getOtherObject();
        String message = null;
        if ( duplicate instanceof AttributeType )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.DuplicateAliasErrorAttributeType" ); //$NON-NLS-1$
        }
        else if ( duplicate instanceof ObjectClass )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.DuplicateAliasErrorObjectClass" ); //$NON-NLS-1$
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
            message = Messages.getString( "ProblemsViewLabelProvider.DuplicateOidErrorAttributeType" ); //$NON-NLS-1$
        }
        else if ( duplicate instanceof ObjectClass )
        {
            message = Messages.getString( "ProblemsViewLabelProvider.DuplicateOidErrorObjectClass" ); //$NON-NLS-1$
        }
        return NLS.bind( message, new String[]
            { exception.getRelatedId(), duplicate.getName() } );
    }


    private String getMessageNonExistentSchema( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATNonExistentSuperior( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATCannotSubtypeCollectiveAT( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATCycleTypeHierarchy( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATNonExistentSyntax( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATSyntaxOrSuperiorRequired( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATNonExistentEqualityMatchingRule( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageNonExistentOrderingMatchingRule( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageNonExistentSubstringMatchingRule( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATMustHaveSameUsageThanSuperior( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATUserApplicationsUsageMustBeUserModifiable( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATCollectiveMustHaveUserApplicationsUsage( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageATCollectiveCannotBeSingleValued( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCAbstractOCMustInheritFromAbstractOC( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCAuxiliaryCannotInheritFromStructuralOC( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCStructuralCannotInheritFromAuxiliaryOC( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCNonExistentSuperior( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCCycleClassHierarchy( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCCollectiveNotAllowedInMust( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCCollectiveNotAllowedInMay( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCDuplicateATInMust( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCDuplicateATInMay( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCNonExistentMustAT( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCNonExistentMayAT( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageOCDuplicateATInMayAndMust( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
    }


    private String getMessageMRNonExistentSyntax( LdapSchemaException exception )
    {
        // TODO Auto-generated method stub
        return null;
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
        String name = so.getName();

        if ( ( name != null ) && ( !name.equals( "" ) ) ) //$NON-NLS-1$
        {
            return name;
        }
        else
        {
            return so.getOid();
        }
    }
}
