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

package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This class is the Label Provider for the Used By Tables of the Attribute Type Editor (Used By Page).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATEUsedByTablesLabelProvider extends LabelProvider implements ITableLabelProvider
{
    /**
     * {@inheritDoc}
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        if ( element instanceof ObjectClass )
        {
            return Activator.getDefault().getImage( PluginConstants.IMG_OBJECT_CLASS );
        }

        // Default
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getColumnText( Object element, int columnIndex )
    {
        if ( element instanceof ObjectClass )
        {
            return ViewUtils.concateAliases( ( ( ObjectClass ) element ).getNames() );
        }

        // Default
        return null;
    }
}
