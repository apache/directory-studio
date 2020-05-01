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

package org.apache.directory.studio.common.ui;


/**
 * Constants used in the connection UI plugin.
 * Final reference -&gt; class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class CommonUIConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private CommonUIConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = CommonUIConstants.class.getPackage().getName();

    /** The pull-down image */
    public static final String IMG_PULLDOWN = "resources/icons/pulldown.gif"; //$NON-NLS-1$

    /*
     * Names of semantic colors. Actual color values are theme specific and defined in default.css and dark.css.
     */
    public static final String DEFAULT_COLOR = "defaultColor";
    public static final String DISABLED_COLOR = "disabledColor";
    public static final String ERROR_COLOR = "errorColor";
    public static final String COMMENT_COLOR = "commentColor";
    public static final String KEYWORD_1_COLOR = "keyword1Color";
    public static final String KEYWORD_2_COLOR = "keyword2Color";
    public static final String OBJECT_CLASS_COLOR = "objectClassColor";
    public static final String ATTRIBUTE_TYPE_COLOR = "attributeTypeColor";
    public static final String VALUE_COLOR = "valueColor";
    public static final String OID_COLOR = "oidColor";
    public static final String SEPARATOR_COLOR = "separatorColor";
    public static final String ADD_COLOR = "addColor";
    public static final String DELETE_COLOR = "deleteColor";
    public static final String MODIFY_COLOR = "modifyColor";
    public static final String RENAME_COLOR = "renameColor";

    public static final String IMG_INFORMATION = "resources/icons/information.gif"; //$NON-NLS-1$

}
