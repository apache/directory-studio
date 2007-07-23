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


import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
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
                String name = errorWrapper.getSchemaError().getSource().getName();

                if ( ( name != null ) && ( !name.equals( "" ) ) )
                {
                    return name;
                }
                else
                {
                    return errorWrapper.getSchemaError().getSource().getOid();
                }
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

        return message.toString();
    }
}
