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
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;


public final class RootDSE extends BaseDNEntry implements IRootDSE
{

    private static final long serialVersionUID = -8445018787232919754L;


    protected RootDSE()
    {
    }


    public RootDSE( IBrowserConnection connection ) throws ModelModificationException
    {
        super( new DN(), connection );
    }


    public IEntry getParententry()
    {
        return null;
    }


    public String[] getSupportedExtensions()
    {
        IAttribute supportedExtensionsAttr = getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION );
        if ( supportedExtensionsAttr != null )
        {
            String[] stringValues = supportedExtensionsAttr.getStringValues();
            Arrays.sort( stringValues );
            return stringValues;
        }
        else
        {
            return new String[0];
        }
    }


    public String[] getSupportedControls()
    {
        IAttribute supportedControlsAttr = getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL );
        if ( supportedControlsAttr != null )
        {
            String[] stringValues = supportedControlsAttr.getStringValues();
            Arrays.sort( stringValues );
            return stringValues;
        }
        else
        {
            return new String[0];
        }
    }


    public String[] getSupportedFeatures()
    {
        IAttribute supportedFeaturesAttr = getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES );
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
    
    
    public boolean isSubentry()
    {
        return false;
    }

}
