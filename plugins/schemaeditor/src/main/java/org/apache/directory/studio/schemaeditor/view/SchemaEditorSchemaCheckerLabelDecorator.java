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
package org.apache.directory.studio.schemaeditor.view;


import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;


/**
 * This class is the Schemas Editor Schema Checker Label Decorator. 
 * It displays specific icons overlays for attribute types and object classes 
 * based on their state in the schema checker.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaEditorSchemaCheckerLabelDecorator extends LabelProvider implements ILightweightLabelDecorator
{
    /**
     * {@inheritDoc}
     */
    public void decorate( Object element, IDecoration decoration )
    {
        SchemaChecker schemaChecker = Activator.getDefault().getSchemaChecker();
        ElementState state = ElementState.NONE;

        if ( element instanceof AttributeTypeWrapper )
        {
            AttributeType at = ( ( AttributeTypeWrapper ) element ).getAttributeType();

            if ( schemaChecker.hasErrors( at ) )
            {
                decorateState( ElementState.ERROR, decoration );
                return;
            }

            if ( schemaChecker.hasWarnings( at ) )
            {
                state = ElementState.WARNING;
            }
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClass oc = ( ( ObjectClassWrapper ) element ).getObjectClass();

            if ( schemaChecker.hasErrors( oc ) )
            {
                decorateState( ElementState.ERROR, decoration );
                return;
            }

            if ( schemaChecker.hasWarnings( oc ) )
            {
                state = ElementState.WARNING;
            }
        }
        else if ( element instanceof SchemaWrapper )
        {
            Schema schema = ( ( SchemaWrapper ) element ).getSchema();

            for ( AttributeType at : schema.getAttributeTypes() )
            {
                if ( schemaChecker.hasErrors( at ) )
                {
                    decorateState( ElementState.ERROR, decoration );
                    return;
                }

                if ( schemaChecker.hasWarnings( at ) )
                {
                    state = ElementState.WARNING;
                }
            }

            for ( ObjectClass oc : schema.getObjectClasses() )
            {
                if ( schemaChecker.hasErrors( oc ) )
                {
                    decorateState( ElementState.ERROR, decoration );
                    return;
                }

                if ( schemaChecker.hasWarnings( oc ) )
                {
                    state = ElementState.WARNING;
                }
            }
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;

            if ( childrenHasErrors( folder.getChildren(), schemaChecker ) )
            {
                decorateState( ElementState.ERROR, decoration );
                return;
            }

            if ( childrenHasWarnings( folder.getChildren(), schemaChecker ) )
            {
                state = ElementState.WARNING;
            }
        }

        decorateState( state, decoration );
    }


    /**
     * Decorates the element from the value of its state.
     *
     * @param state
     *      the state
     * @param decoration
     *      the decoration
     */
    private void decorateState( ElementState state, IDecoration decoration )
    {
        switch ( state )
        {
            case WARNING:
                decoration.addOverlay(
                    Activator.getDefault().getImageDescriptor( PluginConstants.IMG_OVERLAY_WARNING ),
                    IDecoration.BOTTOM_LEFT );
                break;
            case ERROR:
                decoration.addOverlay( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_OVERLAY_ERROR ),
                    IDecoration.BOTTOM_LEFT );
                break;
            default:
                break;
        }
    }


    /**
     * Verifies if the given children list contains elements that have warnings.
     *
     * @param children
     *      the children list
     * @param schemaChecker
     *      the schemaChecker
     * @return
     *      true if the given children list contains elements that have warnings
     */
    public boolean childrenHasWarnings( List<TreeNode> children, SchemaChecker schemaChecker )
    {
        if ( children != null )
        {
            for ( TreeNode child : children )
            {
                if ( child instanceof AttributeTypeWrapper )
                {
                    AttributeType at = ( ( AttributeTypeWrapper ) child ).getAttributeType();

                    if ( schemaChecker.hasWarnings( at ) )
                    {
                        return true;
                    }
                    else
                    {
                        if ( childrenHasWarnings( child.getChildren(), schemaChecker ) )
                        {
                            return true;
                        }
                    }
                }
                else if ( child instanceof ObjectClassWrapper )
                {
                    ObjectClass oc = ( ( ObjectClassWrapper ) child ).getObjectClass();

                    if ( schemaChecker.hasWarnings( oc ) )
                    {
                        return true;
                    }
                    else
                    {
                        if ( childrenHasWarnings( child.getChildren(), schemaChecker ) )
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    /**
    * Verifies if the given children list contains elements that have warnings.
    *
    * @param children
    *      the children list
    * @param schemaChecker
    *      the schemaChecker
    * @return
    *      true if the given children list contains elements that have warnings
    */
    public boolean childrenHasErrors( List<TreeNode> children, SchemaChecker schemaChecker )
    {
        if ( children != null )
        {
            for ( TreeNode child : children )
            {
                if ( child instanceof AttributeTypeWrapper )
                {
                    AttributeType at = ( ( AttributeTypeWrapper ) child ).getAttributeType();

                    if ( schemaChecker.hasErrors( at ) )
                    {
                        return true;
                    }
                    else
                    {
                        if ( childrenHasErrors( child.getChildren(), schemaChecker ) )
                        {
                            return true;
                        }
                    }
                }
                else if ( child instanceof ObjectClassWrapper )
                {
                    ObjectClass oc = ( ( ObjectClassWrapper ) child ).getObjectClass();

                    if ( schemaChecker.hasErrors( oc ) )
                    {
                        return true;
                    }
                    else
                    {
                        if ( childrenHasErrors( child.getChildren(), schemaChecker ) )
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * This enum defines the state of an element.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private enum ElementState
    {
        NONE, WARNING, ERROR;
    }
}
