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


import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.schemachecker.ClassTypeHierarchyError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.DifferentCollectiveAsSuperiorError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.DifferentUsageAsSuperiorError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.DuplicateAliasError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.DuplicateOidError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NoAliasWarning;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingATSuperiorError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingMandatoryATError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingMatchingRuleError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingOCSuperiorError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingOptionalATError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingSyntaxError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaCheckerElement;
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
                return getMessage( errorWrapper.getSchemaError() );
            }
            else if ( columnIndex == 1 )
            {
                return getDisplayName( errorWrapper.getSchemaError().getSource() );
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


    private String getMessage( SchemaCheckerElement element )
    {
        if ( element instanceof DuplicateAliasError )
        {
            DuplicateAliasError duplicateAliasError = ( DuplicateAliasError ) element;

            SchemaObject duplicate = duplicateAliasError.getDuplicate();
            if ( duplicate instanceof AttributeType )
            {
                return NLS
                    .bind(
                        Messages.getString( "ProblemsViewLabelProvider.DuplicateAliasErrorAttributeType" ), new String[] { duplicateAliasError.getAlias(), duplicate.getOid() } ); //$NON-NLS-1$
            }
            else if ( duplicate instanceof ObjectClass )
            {
                return NLS
                    .bind(
                        Messages.getString( "ProblemsViewLabelProvider.DuplicateAliasErrorObjectClass" ), new String[] { duplicateAliasError.getAlias(), duplicate.getOid() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof DuplicateOidError )
        {
            DuplicateOidError duplicateOidError = ( DuplicateOidError ) element;
            SchemaObject duplicate = duplicateOidError.getDuplicate();
            if ( duplicate instanceof AttributeType )
            {
                return NLS
                    .bind(
                        Messages.getString( "ProblemsViewLabelProvider.DuplicateOidErrorAttributeType" ), new String[] { duplicateOidError.getOid(), duplicate.getName() } ); //$NON-NLS-1$
            }
            else if ( duplicate instanceof ObjectClass )
            {
                return NLS
                    .bind(
                        Messages.getString( "ProblemsViewLabelProvider.DuplicateOidErrorObjectClass" ), new String[] { duplicateOidError.getOid(), duplicate.getName() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof NonExistingATSuperiorError )
        {
            NonExistingATSuperiorError nonExistingATSuperiorError = ( NonExistingATSuperiorError ) element;
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.NonExistingSuperiorAttribute" ), new String[] { nonExistingATSuperiorError.getSuperiorAlias() } ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingOCSuperiorError )
        {
            NonExistingOCSuperiorError nonExistingOCSuperiorError = ( NonExistingOCSuperiorError ) element;
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.NonExistingSuperiorObject" ), new String[] { nonExistingOCSuperiorError.getSuperiorAlias() } ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingMandatoryATError )
        {
            NonExistingMandatoryATError nonExistingMandatoryATError = ( NonExistingMandatoryATError ) element;
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.NonExistingMandatoryAttribute" ), new String[] { nonExistingMandatoryATError.getAlias() } ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingOptionalATError )
        {
            NonExistingOptionalATError nonExistingOptionalATError = ( NonExistingOptionalATError ) element;
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.NonExistingOptionalAttribute" ), new String[] { nonExistingOptionalATError.getAlias() } ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingSyntaxError )
        {
            NonExistingSyntaxError nonExistingSyntaxError = ( NonExistingSyntaxError ) element;
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.NonExistingSyntax" ), new String[] { nonExistingSyntaxError.getSyntaxOid() } ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingMatchingRuleError )
        {
            NonExistingMatchingRuleError nonExistingMatchingRuleError = ( NonExistingMatchingRuleError ) element;
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.NonExistingMatchingRule" ), new String[] { nonExistingMatchingRuleError.getMatchingRuleAlias() } ); //$NON-NLS-1$
        }
        else if ( element instanceof NoAliasWarning )
        {
            NoAliasWarning noAliasWarning = ( NoAliasWarning ) element;
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
        else if ( element instanceof ClassTypeHierarchyError )
        {
            ClassTypeHierarchyError classTypeHierarchyError = ( ClassTypeHierarchyError ) element;
            ObjectClass source = ( ObjectClass ) classTypeHierarchyError.getSource();
            ObjectClass superior = ( ObjectClass ) classTypeHierarchyError.getSuperior();
            if ( source.getType().equals( ObjectClassTypeEnum.ABSTRACT ) )
            {
                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
                {
                    return NLS
                        .bind(
                            Messages
                                .getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAbstractStructuralObject" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
                }
                else if ( superior.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
                {
                    return NLS
                        .bind(
                            Messages
                                .getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAbstractAuxiliaryObject" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
                }
            }
            else if ( source.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
            {
                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
                {
                    return NLS
                        .bind(
                            Messages
                                .getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAuxiliaryStructuralObject" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
                }
                else
                {
                    return NLS
                        .bind(
                            Messages.getString( "ProblemsViewLabelProvider.ClassTypeHierarchyErrorAuxiliary" ), new String[] { getDisplayName( source ) } ); //$NON-NLS-1$
                }
            }
        }
        else if ( element instanceof DifferentUsageAsSuperiorError )
        {
            DifferentUsageAsSuperiorError differentUsageAsSuperiorError = ( DifferentUsageAsSuperiorError ) element;
            AttributeType source = ( AttributeType ) differentUsageAsSuperiorError.getSource();
            AttributeType superior = ( AttributeType ) differentUsageAsSuperiorError.getSuperior();
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.AttributeTypeUsage" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        }
        else if ( element instanceof DifferentCollectiveAsSuperiorError )
        {
            DifferentCollectiveAsSuperiorError differentCollectiveAsSuperiorError = ( DifferentCollectiveAsSuperiorError ) element;
            AttributeType source = ( AttributeType ) differentCollectiveAsSuperiorError.getSource();
            AttributeType superior = ( AttributeType ) differentCollectiveAsSuperiorError.getSuperior();
            return NLS
                .bind(
                    Messages.getString( "ProblemsViewLabelProvider.AttributeTypeCollective" ), new String[] { getDisplayName( source ), getDisplayName( superior ) } ); //$NON-NLS-1$
        }

        return ""; //$NON-NLS-1$
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
