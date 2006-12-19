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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;


public class DeleteAllValuesAction extends DeleteAction
{

    public DeleteAllValuesAction()
    {
    }


    public void run()
    {
        super.run();
    }


    public String getText()
    {
        if ( getSelectedValues().length == 1 )
        {
            return "Delete Attribute '" + getSelectedValues()[0].getAttribute().getDescription() + "'";
        }
        else
        {
            return "Delete Attribute";
        }
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_DELETE_ALL );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return super.isEnabled();
        // return getSelectedAttributes().length == 0 &&
        // getSelectedValues().length == 1 &&
        // getSelectedValues()[0].getAttribute().getValueSize() > 1 &&
        // !getSelectedValues()[0].getAttribute().isMustAttribute() &&
        // !getSelectedValues()[0].getAttribute().isObjectClassAttribute() &&
        // !getSelectedValues()[0].getAttribute().isOperationalAttribute();
    }


    protected IAttribute[] getAttributes() throws Exception
    {
        if ( getSelectedAttributes().length == 0 && getSelectedValues().length == 1
            && getSelectedValues()[0].getAttribute().getValueSize() > 1
            && !getSelectedValues()[0].getAttribute().isMustAttribute()
            && !getSelectedValues()[0].getAttribute().isObjectClassAttribute()
            && SchemaUtils.isModifyable( getSelectedValues()[0].getAttribute().getAttributeTypeDescription() ) )
        {

            return new IAttribute[]
                { getSelectedValues()[0].getAttribute() };

        }
        else
        {
            return new IAttribute[0];
        }
    }


    protected IValue[] getValues() throws Exception
    {
        return new IValue[0];
    }

}
