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

package org.apache.directory.studio.ldapbrowser.common.actions;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;


/**
 * This Action deletes all the values of an Attribute (a whole Attribute).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteAllValuesAction extends DeleteAction
{
    /**
     * Creates a new instance of DeleteAllValuesAction.
     */
    public DeleteAllValuesAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        super.run();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( getSelectedValues().length == 1 )
        {
            return NLS.bind( Messages.getString("DeleteAllValuesAction.DeleteAttributeX"), getSelectedValues()[0].getAttribute().getDescription() ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString("DeleteAllValuesAction.DeleteAttribute"); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_DELETE_ALL );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    protected IValue[] getValues() throws Exception
    {
        return new IValue[0];
    }
}
