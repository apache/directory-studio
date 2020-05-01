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
package org.apache.directory.studio.aciitemeditor.sourceeditor;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.CommonUIPlugin;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;


/**
 * This class provides the TextAttributes elements for each kind of attribute
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ACITextAttributeProvider
{
    public static final String DEFAULT_ATTRIBUTE = "__pos_aci_default_attribute"; //$NON-NLS-1$
    public static final String KEYWORD_ATTRIBUTE = "__pos_aci_keyword_attribute"; //$NON-NLS-1$
    public static final String STRING_ATTRIBUTE = "__pos_aci_string_attribute"; //$NON-NLS-1$
    public static final String IDENTIFICATION_ATTRIBUTE = "__pos_aci_identification_attribute"; //$NON-NLS-1$
    public static final String PRECEDENCE_ATTRIBUTE = "__pos_aci_precedence_attribute"; //$NON-NLS-1$
    public static final String AUTHENTICATIONLEVEL_ATTRIBUTE = "__pos_aci_authenticationlevel_attribute"; //$NON-NLS-1$
    public static final String ITEMORUSERFIRST_ATTRIBUTE = "__pos_aci_itemoruserfirst_attribute"; //$NON-NLS-1$
    public static final String USER_ATTRIBUTE = "__pos_aci_user_attribute"; //$NON-NLS-1$

    public static final String GRANT_VALUE = "__pos_aci_grant_value"; //$NON-NLS-1$
    public static final String DENY_VALUE = "__pos_aci_deny_value"; //$NON-NLS-1$

    private Map<String, TextAttribute> attributes = new HashMap<String, TextAttribute>();


    /**
     * Creates a new instance of AciTextAttributeProvider.
     */
    public ACITextAttributeProvider()
    {
        CommonUIPlugin plugin = CommonUIPlugin.getDefault();
        attributes.put( DEFAULT_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.DEFAULT_COLOR ) ) );
        attributes.put( KEYWORD_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.ATTRIBUTE_TYPE_COLOR ), null, SWT.BOLD ) );
        attributes.put( STRING_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.VALUE_COLOR ) ) );
        attributes.put( GRANT_VALUE, new TextAttribute( plugin.getColor( CommonUIConstants.ADD_COLOR ) ) );
        attributes.put( DENY_VALUE, new TextAttribute( plugin.getColor( CommonUIConstants.DELETE_COLOR ) ) );
        attributes.put( IDENTIFICATION_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.KEYWORD_1_COLOR ), null, SWT.BOLD ) );
        attributes.put( PRECEDENCE_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.KEYWORD_1_COLOR ), null, SWT.BOLD ) );
        attributes.put( AUTHENTICATIONLEVEL_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.KEYWORD_1_COLOR ), null, SWT.BOLD ) );
        attributes.put( ITEMORUSERFIRST_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.KEYWORD_1_COLOR ), null, SWT.BOLD ) );
        attributes.put( USER_ATTRIBUTE, new TextAttribute( plugin.getColor( CommonUIConstants.KEYWORD_1_COLOR ), null, SWT.BOLD ) );
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
