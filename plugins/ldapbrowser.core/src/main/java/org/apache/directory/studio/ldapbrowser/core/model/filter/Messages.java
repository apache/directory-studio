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
package org.apache.directory.studio.ldapbrowser.core.model.filter;


import org.eclipse.osgi.util.NLS;


public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.apache.directory.studio.ldapbrowser.core.model.filter.messages"; //$NON-NLS-1$
    public static String LdapAndFilterComponent_InvalidAndFilter;
    public static String LdapAndFilterComponent_MissingAndCharacter;
    public static String LdapAndFilterComponent_MissingFilters;
    public static String LdapFilterComponent_ParentIsNull;
    public static String LdapFilterExtensibleComponent_MissingAttributeType;
    public static String LdapFilterExtensibleComponent_MissingColon;
    public static String LdapFilterExtensibleComponent_MissingDn;
    public static String LdapFilterExtensibleComponent_MissingEquals;
    public static String LdapFilterExtensibleComponent_MissingMatchingRule;
    public static String LdapFilterExtensibleComponent_MissingValue;
    public static String LdapFilterItemComponent_MissingAttributeName;
    public static String LdapFilterItemComponent_MissingFilterType;
    public static String LdapFilterItemComponent_MissingValue;
    public static String LdapNotFilterComponent_InvalidNotFilter;
    public static String LdapNotFilterComponent_MissingFilterExpression;
    public static String LdapNotFilterComponent_MissingNotCharacter;
    public static String LdapOrFilterComponent_InvalidOrFilter;
    public static String LdapOrFilterComponent_MissingFilters;
    public static String LdapOrFilterComponent_MissingOrCharacter;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages( BUNDLE_NAME, Messages.class );
    }


    private Messages()
    {
    }
}
