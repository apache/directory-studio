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

package org.apache.directory.studio.ldapbrowser.core;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;


/**
 * This class contains all the constants used by the Browser Core Plugin
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class BrowserCoreConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private BrowserCoreConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = BrowserCoreConstants.class.getPackage().getName();

    public static final String PREFERENCE_BINARY_SYNTAXES = "binarySyntaxes"; //$NON-NLS-1$

    public static final String PREFERENCE_BINARY_ATTRIBUTES = "binaryAttributes"; //$NON-NLS-1$

    public static final String PREFERENCE_OBJECT_CLASS_ICONS = "objectClassIcons"; //$NON-NLS-1$

    public static final String BINARY = "BINARY"; //$NON-NLS-1$

    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" ); //$NON-NLS-1$

    public static final String DEFAULT_ENCODING = new OutputStreamWriter( new ByteArrayOutputStream() ).getEncoding();

    public static final String PREFERENCE_CHECK_FOR_CHILDREN = "checkForChildren"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_CSV_ATTRIBUTEDELIMITER = "formatCsvAttributeDelimiter"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_CSV_VALUEDELIMITER = "formatCsvValueDelimiter"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_CSV_QUOTECHARACTER = "formatCsvQuoteCharacter"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_CSV_LINESEPARATOR = "formatCsvLineSeparator"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_CSV_BINARYENCODING = "formatCsvBinaryEncoding"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_CSV_ENCODING = "formatCsvEncoding"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_XLS_VALUEDELIMITER = "formatXlsValueDelimiter"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_XLS_BINARYENCODING = "formatXlsBinaryEncoding"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_ODF_VALUEDELIMITER = "formatOdfValueDelimiter"; //$NON-NLS-1$

    public static final String PREFERENCE_FORMAT_ODF_BINARYENCODING = "formatOdfBinaryEncoding"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIF_LINE_WIDTH = "ldifLineWidth"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIF_LINE_SEPARATOR = "ldifLineSeparator"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIF_SPACE_AFTER_COLON = "ldifSpaceAfterColon"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIF_INCLUDE_VERSION_LINE = "ldifIncludeVersionLine"; //$NON-NLS-1$

    public static final int BINARYENCODING_IGNORE = 0;

    public static final int BINARYENCODING_BASE64 = 1;

    public static final int BINARYENCODING_HEX = 2;

    public static final int SORT_BY_NONE = 0;

    public static final int SORT_BY_RDN = 1;

    public static final int SORT_BY_RDN_VALUE = 2;

    public static final int SORT_BY_ATTRIBUTE_DESCRIPTION = 3;

    public static final int SORT_BY_VALUE = 4;

    public static final int SORT_ORDER_NONE = 0;

    public static final int SORT_ORDER_ASCENDING = 1;

    public static final int SORT_ORDER_DESCENDING = 2;

    public static final String LDAP_SEARCH_PAGE_ID = "org.apache.directory.studio.ldapbrowser.ui.search.SearchPage"; //$NON-NLS-1$
}
