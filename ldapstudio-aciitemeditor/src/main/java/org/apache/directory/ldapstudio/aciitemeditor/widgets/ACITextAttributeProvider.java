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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * This class provides the TextAttributes elements for each kind of attribute
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACITextAttributeProvider
{
    public static final String DEFAULT_ATTRIBUTE = "__pos_aci_default_attribute";
    public static final String KEYWORD_ATTRIBUTE = "__pos_aci_keyword_attribute";
    public static final String STRING_ATTRIBUTE = "__pos_aci_string_attribute";
    public static final String GRANT_DENY_ATTRIBUTE = "__pos_aci_grant_deny_attribute";
    public static final String IDENTIFICATION_ATTRIBUTE = "__pos_aci_identification_attribute";
    public static final String PRECEDENCE_ATTRIBUTE = "__pos_aci_precedence_attribute";
    public static final String AUTHENTICATIONLEVEL_ATTRIBUTE = "__pos_aci_authenticationlevel_attribute";
    public static final String ITEMORUSERFIRST_ATTRIBUTE = "__pos_aci_itemoruserfirst_attribute";
    public static final String USER_ATTRIBUTE = "__pos_aci_user_attribute";

    public static final String GRANT_VALUE = "__pos_aci_grant_value";
    public static final String DENY_VALUE = "__pos_aci_deny_value";

    private Map<String, TextAttribute> attributes = new HashMap<String, TextAttribute>();


    /**
     * Creates a new instance of AciTextAttributeProvider.
     */
    public ACITextAttributeProvider()
    {
        attributes.put( DEFAULT_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0, 0, 0 ) ) ) );

        attributes.put( KEYWORD_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 127, 0, 85 ) ),
            null, SWT.BOLD ) );

        attributes.put( STRING_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0, 0, 255 ) ) ) );

        attributes.put( GRANT_DENY_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 125, 125,
            125 ) ) ) );

        attributes.put( GRANT_VALUE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0, 150, 0 ) ) ) );
        attributes.put( DENY_VALUE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 150, 0, 0 ) ) ) );

        attributes.put( IDENTIFICATION_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 125, 0,
            0 ) ), null, SWT.BOLD ) );

        attributes.put( PRECEDENCE_ATTRIBUTE, new TextAttribute(
            new Color( Display.getCurrent(), new RGB( 0, 0, 125 ) ), null, SWT.BOLD ) );

        attributes.put( AUTHENTICATIONLEVEL_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0,
            125, 0 ) ), null, SWT.BOLD ) );

        attributes.put( ITEMORUSERFIRST_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 125, 0,
            125 ) ), null, SWT.BOLD ) );

        attributes.put( USER_ATTRIBUTE, new TextAttribute( new Color( Display.getCurrent(), new RGB( 0, 122, 255 ) ),
            null, SWT.BOLD ) );

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
