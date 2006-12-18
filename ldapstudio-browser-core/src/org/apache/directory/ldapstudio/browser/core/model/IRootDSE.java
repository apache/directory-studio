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

package org.apache.directory.ldapstudio.browser.core.model;


public interface IRootDSE extends IEntry
{

    public static final String ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS = "namingContexts"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_SUBSCHEMASUBENTRY = "subschemaSubentry"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_MONITORCONTEXT = "monitorContext"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_CONFIGCONTEXT = "configContext"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_DSANAME = "dsaName"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION = "supportedExtension"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL = "supportedControl"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES = "supportedFeatures"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_SUPPORTEDLDAPVERSION = "supportedLDAPVersion"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_ALTSERVER = "altServer"; //$NON-NLS-1$

    public static final String ROOTDSE_ATTRIBUTE_SUPPORTEDSASLMECHANISM = "supportedSASLMechanisms"; //$NON-NLS-1$


    public String[] getSupportedExtensions();


    public String[] getSupportedControls();


    public String[] getSupportedFeatures();

}
