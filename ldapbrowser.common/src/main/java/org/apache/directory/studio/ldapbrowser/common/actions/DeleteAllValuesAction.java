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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;


/**
 * This Action deletes all the values of an Attribute (a whole Attribute).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteAllValuesAction extends DeleteAction
{

    private static Collection<IValue> EMPTY_VALUES = new HashSet<IValue>();


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
            return NLS
                .bind(
                    Messages.getString( "DeleteAllValuesAction.DeleteAttributeX" ), getSelectedValues()[0].getAttribute().getDescription() ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "DeleteAllValuesAction.DeleteAttribute" ); //$NON-NLS-1$
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
    }


    /**
     * {@inheritDoc}
     */
    protected Collection<IValue> getValues()
    {
        if ( getSelectedAttributes().length == 0 && getSelectedValues().length == 1
            && getSelectedValues()[0].getAttribute().getValueSize() > 1 )
        {
            Collection<IValue> values = new HashSet<IValue>();
            values.addAll( Arrays.asList( getSelectedValues()[0].getAttribute().getValues() ) );
            return values;
        }
        else
        {
            return EMPTY_VALUES;
        }
    }
}
