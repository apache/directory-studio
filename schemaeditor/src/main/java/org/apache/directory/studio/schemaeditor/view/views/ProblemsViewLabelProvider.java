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


import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
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
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the LabelProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
        StringBuffer message = new StringBuffer();

        if ( element instanceof DuplicateAliasError )
        {
            DuplicateAliasError duplicateAliasError = ( DuplicateAliasError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.AliasBegin") + duplicateAliasError.getAlias() + Messages.getString("ProblemsViewLabelProvider.AliasEnd") ); //$NON-NLS-1$ //$NON-NLS-2$
            SchemaObject duplicate = duplicateAliasError.getDuplicate();
            if ( duplicate instanceof AttributeTypeImpl )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.AttributeTypeSmall") ); //$NON-NLS-1$
            }
            else if ( duplicate instanceof ObjectClassImpl )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.ObjectClassSmall") ); //$NON-NLS-1$
            }
            message.append( Messages.getString("ProblemsViewLabelProvider.WithOID") + duplicate.getOid() + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof DuplicateOidError )
        {
            DuplicateOidError duplicateOidError = ( DuplicateOidError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.OIDBegin") + duplicateOidError.getOid() + Messages.getString("ProblemsViewLabelProvider.OIDEnd") ); //$NON-NLS-1$ //$NON-NLS-2$
            SchemaObject duplicate = duplicateOidError.getDuplicate();
            if ( duplicate instanceof AttributeTypeImpl )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.AttributeTypeSmall") ); //$NON-NLS-1$
            }
            else if ( duplicate instanceof ObjectClassImpl )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.ObjectClassSmall") ); //$NON-NLS-1$
            }
            message.append( Messages.getString("ProblemsViewLabelProvider.WithAlias") + duplicate.getName() + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof NonExistingATSuperiorError )
        {
            NonExistingATSuperiorError nonExistingATSuperiorError = ( NonExistingATSuperiorError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.SuperiorAttributeBegin") + nonExistingATSuperiorError.getSuperiorAlias() //$NON-NLS-1$
                + Messages.getString("ProblemsViewLabelProvider.SuperiorAttributeEnd") ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingOCSuperiorError )
        {
            NonExistingOCSuperiorError nonExistingOCSuperiorError = ( NonExistingOCSuperiorError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.SuperiorObjectBegin") + nonExistingOCSuperiorError.getSuperiorAlias() //$NON-NLS-1$
                + Messages.getString("ProblemsViewLabelProvider.SuperiorObjectEnd") ); //$NON-NLS-1$
        }
        else if ( element instanceof NonExistingMandatoryATError )
        {
            NonExistingMandatoryATError nonExistingMandatoryATError = ( NonExistingMandatoryATError ) element;

            message
                .append( Messages.getString("ProblemsViewLabelProvider.MandatoryAttributeBegin") + nonExistingMandatoryATError.getAlias() + Messages.getString("ProblemsViewLabelProvider.MandatoryAttributeEnd") ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof NonExistingOptionalATError )
        {
            NonExistingOptionalATError nonExistingOptionalATError = ( NonExistingOptionalATError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.OptionalAttributeBegin") + nonExistingOptionalATError.getAlias() + Messages.getString("ProblemsViewLabelProvider.OptionalAttributeEnd") ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof NonExistingSyntaxError )
        {
            NonExistingSyntaxError nonExistingSyntaxError = ( NonExistingSyntaxError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.SyntaxOIDBegin") + nonExistingSyntaxError.getSyntaxOid() + Messages.getString("ProblemsViewLabelProvider.SyntaxOIDEnd") ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof NonExistingMatchingRuleError )
        {
            NonExistingMatchingRuleError nonExistingMatchingRuleError = ( NonExistingMatchingRuleError ) element;

            message.append( Messages.getString("ProblemsViewLabelProvider.MatchingRuleBegin") + nonExistingMatchingRuleError.getMatchingRuleAlias() //$NON-NLS-1$
                + Messages.getString("ProblemsViewLabelProvider.MatchingRuleEnd") ); //$NON-NLS-1$
        }
        else if ( element instanceof NoAliasWarning )
        {
            NoAliasWarning noAliasWarning = ( NoAliasWarning ) element;
            SchemaObject source = noAliasWarning.getSource();
            if ( source instanceof AttributeTypeImpl )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.AttributeType") ); //$NON-NLS-1$
            }
            else if ( source instanceof ObjectClassImpl )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.ObjectClass") ); //$NON-NLS-1$
            }
            message.append( Messages.getString("ProblemsViewLabelProvider.WithOIDBegin") + source.getOid() + Messages.getString("ProblemsViewLabelProvider.WithOIDEnd") ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof ClassTypeHierarchyError )
        {
            ClassTypeHierarchyError classTypeHierarchyError = ( ClassTypeHierarchyError ) element;
            ObjectClassImpl source = ( ObjectClassImpl ) classTypeHierarchyError.getSource();
            ObjectClassImpl superior = ( ObjectClassImpl ) classTypeHierarchyError.getSuperior();

            if ( source.getType().equals( ObjectClassTypeEnum.ABSTRACT ) )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.AbstractObjectBegin") + getDisplayName( source ) + Messages.getString("ProblemsViewLabelProvider.AbstractObjectEnd") ); //$NON-NLS-1$ //$NON-NLS-2$

                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
                {
                    message.append( Messages.getString("ProblemsViewLabelProvider.StructuralObject") + getDisplayName( superior ) + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else if ( superior.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
                {
                    message.append( Messages.getString("ProblemsViewLabelProvider.AuxiliaryObject") + getDisplayName( superior ) + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            else if ( source.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
            {
                message.append( Messages.getString("ProblemsViewLabelProvider.AuxiliaryObjectBegin") + getDisplayName( source ) + Messages.getString("ProblemsViewLabelProvider.AuxiliaryObjectEnd") ); //$NON-NLS-1$ //$NON-NLS-2$

                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
                {
                    message.append( Messages.getString("ProblemsViewLabelProvider.StructuralObject") + getDisplayName( superior ) + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        else if ( element instanceof DifferentUsageAsSuperiorError )
        {
            DifferentUsageAsSuperiorError differentUsageAsSuperiorError = ( DifferentUsageAsSuperiorError ) element;
            AttributeTypeImpl source = ( AttributeTypeImpl ) differentUsageAsSuperiorError.getSource();
            AttributeTypeImpl superior = ( AttributeTypeImpl ) differentUsageAsSuperiorError.getSuperior();

            message.append( Messages.getString("ProblemsViewLabelProvider.AttributeTypeBegin") + getDisplayName( source ) //$NON-NLS-1$
                + Messages.getString("ProblemsViewLabelProvider.AttributeTypeEnd") + getDisplayName( superior ) + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( element instanceof DifferentCollectiveAsSuperiorError )
        {
            DifferentCollectiveAsSuperiorError differentCollectiveAsSuperiorError = ( DifferentCollectiveAsSuperiorError ) element;
            AttributeTypeImpl source = ( AttributeTypeImpl ) differentCollectiveAsSuperiorError.getSource();
            AttributeTypeImpl superior = ( AttributeTypeImpl ) differentCollectiveAsSuperiorError.getSuperior();

            message.append( Messages.getString("ProblemsViewLabelProvider.AttributeTypeCollectiveBegin") + getDisplayName( source ) + Messages.getString("ProblemsViewLabelProvider.AttributeTypeCollectiveEnd") //$NON-NLS-1$ //$NON-NLS-2$
                + getDisplayName( superior ) + "'." ); //$NON-NLS-1$
        }

        return message.toString();
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
