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
package org.apache.directory.studio.schemaeditor.view.widget;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.CommonUIPlugin;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;


/**
 * This class provides the TextAttributes elements for each kind of attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaTextAttributeProvider
{
    public static final String DEFAULT_ATTRIBUTE = "__pos_schema_default_attribute"; //$NON-NLS-1$
    public static final String STRING_ATTRIBUTE = "__pos_schema_string_attribute"; //$NON-NLS-1$
    public static final String KEYWORD_ATTRIBUTE = "__pos_schema_keyword_attribute"; //$NON-NLS-1$
    public static final String ATTRIBUTETYPE_ATTRIBUTE = "__pos_schema_attributetype_attribute"; //$NON-NLS-1$
    public static final String OBJECTCLASS_ATTRIBUTE = "__pos_schema_objectclass_attribute"; //$NON-NLS-1$
    public static final String OID_ATTRIBUTE = "__pos_schema_oid_attribute"; //$NON-NLS-1$

    private Map<String, TextAttribute> attributes = new HashMap<String, TextAttribute>();


    /**
     * Creates a new instance of SchemaTextAttributeProvider.
     *
     */
    public SchemaTextAttributeProvider()
    {
        CommonUIPlugin plugin = CommonUIPlugin.getDefault();
        attributes.put( DEFAULT_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.DEFAULT_COLOR ) ) );
        attributes.put( KEYWORD_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.KEYWORD_2_COLOR ), null, SWT.BOLD ) );
        attributes.put( STRING_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.VALUE_COLOR ) ) );
        attributes.put( ATTRIBUTETYPE_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.ATTRIBUTE_TYPE_COLOR ), null, SWT.BOLD ) );
        attributes.put( OBJECTCLASS_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.OBJECT_CLASS_COLOR ), null, SWT.BOLD ) );
        attributes.put( OID_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.OID_COLOR ) ) );
    }


    /**
     * Gets the correct TextAttribute for the given type
     *
     * @param type
     *      the type of element
     * @return
     *      the correct TextAttribute for the given type
     */
    public TextAttribute getAttribute( String type )
    {
        TextAttribute attr = ( TextAttribute ) attributes.get( type );
        if ( attr == null )
        {
            attr = ( TextAttribute ) attributes.get( DEFAULT_ATTRIBUTE );
        }
        return attr;
    }
}
