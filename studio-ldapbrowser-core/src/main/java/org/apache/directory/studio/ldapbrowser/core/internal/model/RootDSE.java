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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import java.util.Arrays;

import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;


/**
 * The RootDSE class represents a root DSE entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
        super( new DN(), browserConnection );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.internal.model.BaseDNEntry#getParententry()
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
        return getAttributeValues( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#getSupportedControls()
     */
    public String[] getSupportedControls()
    {
        return getAttributeValues( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IRootDSE#getSupportedFeatures()
     */
    public String[] getSupportedFeatures()
    {
        return getAttributeValues( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES );
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
     * @see org.apache.directory.studio.ldapbrowser.core.internal.model.AbstractEntry#isSubentry()
     */
    public boolean isSubentry()
    {
        return false;
    }

}
