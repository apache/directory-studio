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
package org.apache.directory.ldapstudio.schemas.view.views;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * This class provides the TextAttributes elements for each kind of attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
        attributes.put( DEFAULT_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0, 0, 0 ) ) ) );

        attributes.put( KEYWORD_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 127, 0, 85 ) ),
            null, SWT.BOLD ) );

        attributes.put( STRING_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0, 0, 255 ) ) ) );

        attributes.put( ATTRIBUTETYPE_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 89, 71,
            158 ) ), null, SWT.BOLD ) );

        attributes.put( OBJECTCLASS_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(),
            new RGB( 45, 124, 68 ) ), null, SWT.BOLD ) );

        attributes.put( OID_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 255, 0, 0 ) ) ) );
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
