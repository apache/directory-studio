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
package org.apache.directory.studio.valueeditors;


/**
 * Contains constants for the value editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ValueEditorsConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = ValueEditorsActivator.getDefault().getPluginProperties().getString(
        "Plugin_id" ); //$NON-NLS-1$

    /** The relative path to the image editor icon */
    public static final String IMG_IMAGEEDITOR = "resources/icons/imageeditor.gif"; //$NON-NLS-1$

    /** The relative path to the address editor icon */
    public static final String IMG_ADDRESSEDITOR = "resources/icons/addresseditor.gif"; //$NON-NLS-1$

    /** The relative path to the DN editor icon */
    public static final String IMG_DNEDITOR = "resources/icons/dneditor.gif"; //$NON-NLS-1$

    /** The relative path to the password editor icon */
    public static final String IMG_PASSWORDEDITOR = "resources/icons/passwordeditor.gif"; //$NON-NLS-1$

    /** The relative path to the generalized time editor icon */
    public static final String IMG_GENERALIZEDTIMEEDITOR = "resources/icons/generalizedtimeeditor.gif"; //$NON-NLS-1$

    /** The relative path to the object class editor icon */
    public static final String IMG_OCDEDITOR = "resources/icons/objectclasseditor.png"; //$NON-NLS-1$

    /** The relative path to the integer editor icon */
    public static final String IMG_INTEGEREDITOR = "resources/icons/integereditor.gif"; //$NON-NLS-1$

    /** The relative path to the administrative role editor icon */
    public static final String IMG_ADMINISTRATIVEROLEEDITOR = "resources/icons/administrativeroleeditor.gif"; //$NON-NLS-1$

    /** The relative path to the certificate editor icon */
    public static final String IMG_CERTIFICATEEDITOR = "resources/icons/certificateeditor.png"; //$NON-NLS-1$

    /** The relative path to the text field error icon */
    public static final String IMG_TEXTFIELD_ERROR = "resources/icons/textfield_error.png"; //$NON-NLS-1$

    /** The relative path to the text field ok icon */
    public static final String IMG_TEXTFIELD_OK = "resources/icons/textfield_ok.png"; //$NON-NLS-1$

}
