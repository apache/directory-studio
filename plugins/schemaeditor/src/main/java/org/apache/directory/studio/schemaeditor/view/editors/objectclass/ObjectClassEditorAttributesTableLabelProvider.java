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

package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;


/**
 * This class is the Label Provider for the Attributes Table of the Object Class Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassEditorAttributesTableLabelProvider extends LabelProvider implements ITableLabelProvider
{
    /**
     * {@inheritDoc}
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        if ( ( element instanceof AttributeType ) || ( element instanceof NonExistingAttributeType ) )
        {
            return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
        }

        // Default
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getColumnText( Object element, int columnIndex )
    {
        if ( element instanceof AttributeType )
        {
            AttributeType at = ( AttributeType ) element;

            List<String> names = at.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                return ViewUtils.concateAliases( names ) + "  -  (" + at.getOid() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                return NLS
                    .bind(
                        Messages.getString( "ObjectClassEditorAttributesTableLabelProvider.none" ), new String[] { at.getOid() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof NonExistingAttributeType )
        {
            return ( ( NonExistingAttributeType ) element ).getDisplayName();
        }

        // Default
        return null;
    }
}
