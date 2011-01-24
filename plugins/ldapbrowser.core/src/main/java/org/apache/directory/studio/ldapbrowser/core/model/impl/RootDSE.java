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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import java.util.Arrays;

import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;


/**
 * The RootDSE class represents a root DSE entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class RootDSE extends BaseDNEntry implements IRootDSE
{

    private static final long serialVersionUID = -8445018787232919754L;


    protected RootDSE()
    {
    }


    /**
     * Creates a new instance of RootDSE.
     * 
     * @param browserConnection the browser connection
     */
    public RootDSE( IBrowserConnection browserConnection )
    {
        super( Dn.EMPTY_DN, browserConnection );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry#getParententry()
     */
    public IEntry getParententry()
    {
        return null;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#getSupportedExtensions()
     */
    public String[] getSupportedExtensions()
    {
        return getAttributeValues( SchemaConstants.SUPPORTED_EXTENSION_AT );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#getSupportedControls()
     */
    public String[] getSupportedControls()
    {
        return getAttributeValues( SchemaConstants.SUPPORTED_CONTROL_AT );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#getSupportedFeatures()
     */
    public String[] getSupportedFeatures()
    {
        return getAttributeValues( SchemaConstants.SUPPORTED_FEATURES_AT );
    }


    /**
     * Gets the attribute values.
     * 
     * @param attributeDescription the attribute description
     * 
     * @return the attribute values
     */
    private String[] getAttributeValues( String attributeDescription )
    {
        IAttribute supportedFeaturesAttr = getAttribute( attributeDescription );
        if ( supportedFeaturesAttr != null )
        {
            String[] stringValues = supportedFeaturesAttr.getStringValues();
            Arrays.sort( stringValues );
            return stringValues;
        }
        else
        {
            return new String[0];
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#isSubentry()
     */
    public boolean isSubentry()
    {
        return false;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#isControlSupported(java.lang.String)
     */
    public boolean isControlSupported( String oid )
    {
        String[] supportedControls = getSupportedControls();
        return Arrays.asList( supportedControls ).contains( oid );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#isFeatureSupported(java.lang.String)
     */
    public boolean isFeatureSupported( String oid )
    {
        String[] supportedFeatures = getSupportedFeatures();
        return Arrays.asList( supportedFeatures ).contains( oid );
    }

}
