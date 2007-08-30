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
package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.ClassTypeHierarchyError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.DifferentCollectiveAsSuperiorError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.DifferentUsageAsSuperiorError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.DuplicateAliasError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.DuplicateOidError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NoAliasWarning;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingATSuperiorError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingMandatoryATError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingMatchingRuleError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingOCSuperiorError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingOptionalATError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.NonExistingSyntaxError;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaCheckerElement;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaErrorWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWarningWrapper;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


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
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_PROBLEMS_ERROR ).createImage();
            }
            else if ( element instanceof SchemaWarningWrapper )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_PROBLEMS_WARNING ).createImage();
            }
            else if ( element instanceof Folder )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_PROBLEMS_GROUP ).createImage();
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

                if ( ( name != null ) && ( !name.equals( "" ) ) )
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
                return folder.getName() + " (" + folder.getChildren().size() + ")";
            }
            else
            {
                return "";
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

            message.append( "Alias '" + duplicateAliasError.getAlias() + "' is already used by another item: " );
            SchemaObject duplicate = duplicateAliasError.getDuplicate();
            if ( duplicate instanceof AttributeTypeImpl )
            {
                message.append( "attribute type" );
            }
            else if ( duplicate instanceof ObjectClassImpl )
            {
                message.append( "object class" );
            }
            message.append( " with OID '" + duplicate.getOid() + "'." );
        }
        else if ( element instanceof DuplicateOidError )
        {
            DuplicateOidError duplicateOidError = ( DuplicateOidError ) element;

            message.append( "OID '" + duplicateOidError.getOid() + "' is already used by another item: " );
            SchemaObject duplicate = duplicateOidError.getDuplicate();
            if ( duplicate instanceof AttributeTypeImpl )
            {
                message.append( "attribute type" );
            }
            else if ( duplicate instanceof ObjectClassImpl )
            {
                message.append( "object class" );
            }
            message.append( " with alias '" + duplicate.getName() + "'." );
        }
        else if ( element instanceof NonExistingATSuperiorError )
        {
            NonExistingATSuperiorError nonExistingATSuperiorError = ( NonExistingATSuperiorError ) element;

            message.append( "Superior attribute type '" + nonExistingATSuperiorError.getSuperiorAlias()
                + "' does not exist." );
        }
        else if ( element instanceof NonExistingOCSuperiorError )
        {
            NonExistingOCSuperiorError nonExistingOCSuperiorError = ( NonExistingOCSuperiorError ) element;

            message.append( "Superior object class '" + nonExistingOCSuperiorError.getSuperiorAlias()
                + "' does not exist." );
        }
        else if ( element instanceof NonExistingMandatoryATError )
        {
            NonExistingMandatoryATError nonExistingMandatoryATError = ( NonExistingMandatoryATError ) element;

            message
                .append( "Mandatory attribute type '" + nonExistingMandatoryATError.getAlias() + "' does not exist." );
        }
        else if ( element instanceof NonExistingOptionalATError )
        {
            NonExistingOptionalATError nonExistingOptionalATError = ( NonExistingOptionalATError ) element;

            message.append( "Optional attribute type '" + nonExistingOptionalATError.getAlias() + "' does not exist." );
        }
        else if ( element instanceof NonExistingSyntaxError )
        {
            NonExistingSyntaxError nonExistingSyntaxError = ( NonExistingSyntaxError ) element;

            message.append( "Syntax with OID '" + nonExistingSyntaxError.getSyntaxOid() + "' does not exist." );
        }
        else if ( element instanceof NonExistingMatchingRuleError )
        {
            NonExistingMatchingRuleError nonExistingMatchingRuleError = ( NonExistingMatchingRuleError ) element;

            message.append( "Matching rule '" + nonExistingMatchingRuleError.getMatchingRuleAlias()
                + "' does not exist." );
        }
        else if ( element instanceof NoAliasWarning )
        {
            NoAliasWarning noAliasWarning = ( NoAliasWarning ) element;
            SchemaObject source = noAliasWarning.getSource();
            if ( source instanceof AttributeTypeImpl )
            {
                message.append( "Attribute type" );
            }
            else if ( source instanceof ObjectClassImpl )
            {
                message.append( "Object class" );
            }
            message.append( " with OID '" + source.getOid() + "' does not have any alias." );
        }
        else if ( element instanceof ClassTypeHierarchyError )
        {
            ClassTypeHierarchyError classTypeHierarchyError = ( ClassTypeHierarchyError ) element;
            ObjectClassImpl source = ( ObjectClassImpl ) classTypeHierarchyError.getSource();
            ObjectClassImpl superior = ( ObjectClassImpl ) classTypeHierarchyError.getSuperior();

            if ( source.getType().equals( ObjectClassTypeEnum.ABSTRACT ) )
            {
                message.append( "Abstract object class Ô" + getDisplayName( source ) + "' can not extend " );

                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
                {
                    message.append( "Structural object class :'" + getDisplayName( superior ) + "'." );
                }
                else if ( superior.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
                {
                    message.append( "Auxiliary object class :'" + getDisplayName( superior ) + "'." );
                }
            }
            else if ( source.getType().equals( ObjectClassTypeEnum.AUXILIARY ) )
            {
                message.append( "Auxiliary object class Ô" + getDisplayName( source ) + "' can not extend " );

                if ( superior.getType().equals( ObjectClassTypeEnum.STRUCTURAL ) )
                {
                    message.append( "Structural object class :'" + getDisplayName( superior ) + "'." );
                }
            }
        }
        else if ( element instanceof DifferentUsageAsSuperiorError )
        {
            DifferentUsageAsSuperiorError differentUsageAsSuperiorError = ( DifferentUsageAsSuperiorError ) element;
            AttributeTypeImpl source = ( AttributeTypeImpl ) differentUsageAsSuperiorError.getSource();
            AttributeTypeImpl superior = ( AttributeTypeImpl ) differentUsageAsSuperiorError.getSuperior();

            message.append( "Attribute type '" + getDisplayName( source )
                + "' has a different usage value than its superior '" + getDisplayName( superior ) + "'." );
        }
        else if ( element instanceof DifferentCollectiveAsSuperiorError )
        {
            DifferentCollectiveAsSuperiorError differentCollectiveAsSuperiorError = ( DifferentCollectiveAsSuperiorError ) element;
            AttributeTypeImpl source = ( AttributeTypeImpl ) differentCollectiveAsSuperiorError.getSource();
            AttributeTypeImpl superior = ( AttributeTypeImpl ) differentCollectiveAsSuperiorError.getSuperior();

            message.append( "Attribute type '" + getDisplayName( source ) + "' must be collective as its superior '"
                + getDisplayName( superior ) + "'." );
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

        if ( ( name != null ) && ( !name.equals( "" ) ) )
        {
            return name;
        }
        else
        {
            return so.getOid();
        }
    }
}
