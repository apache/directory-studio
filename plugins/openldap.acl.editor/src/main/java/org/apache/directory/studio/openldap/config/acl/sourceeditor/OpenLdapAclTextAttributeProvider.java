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
package org.apache.directory.studio.openldap.config.acl.sourceeditor;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;


/**
 * This class provides the TextAttributes elements for each kind of attribute
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclTextAttributeProvider
{
    public static final String DEFAULT_ATTRIBUTE = "__pos_acl_default_attribute"; //$NON-NLS-1$
    public static final String KEYWORD_ATTRIBUTE = "__pos_acl_keyword_attribute"; //$NON-NLS-1$
    public static final String STRING_ATTRIBUTE = "__pos_acl_string_attribute"; //$NON-NLS-1$

    private Map<String, TextAttribute> attributes = new HashMap<String, TextAttribute>();


    /**
     * Creates a new instance of AciTextAttributeProvider.
     */
    public OpenLdapAclTextAttributeProvider()
    {
        attributes.put( DEFAULT_ATTRIBUTE, new TextAttribute( CommonUIConstants.BLACK_COLOR ) );

        attributes.put( KEYWORD_ATTRIBUTE, new TextAttribute( CommonUIConstants.M_PURPLE_COLOR, null, SWT.BOLD ) );

        attributes.put( STRING_ATTRIBUTE, new TextAttribute( CommonUIConstants.BLUE_COLOR ) );
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
