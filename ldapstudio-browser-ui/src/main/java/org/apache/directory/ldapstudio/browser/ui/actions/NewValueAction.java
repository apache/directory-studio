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


import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action adds a new Value to an Attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewValueAction extends BrowserAction implements ModelModifier
{
    /**
     * Creates a new instance of NewValueAction.
     */
    public NewValueAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( getSelectedValues().length == 1 )
        {
            getSelectedValues()[0].getAttribute().addEmptyValue( this );
        }
        else if ( getSelectedAttributes().length == 1 )
        {
            getSelectedAttributes()[0].addEmptyValue( this );
        }
        else if ( getSelectedAttributeHierarchies().length == 1 )
        {
            getSelectedAttributeHierarchies()[0].getAttribute().addEmptyValue( this );
        }

        if ( getSelectedSearchResults().length > 0 )
        {

        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "New Value";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_VALUE_ADD );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.apache.directory.ldapstudio.browser.action.addValue";
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        // System.out.println(getSelectedAttributeArrays());
        // System.out.print("==> ");
        // IAttribute[][] attArr = getSelectedAttributeArrays();
        // for (int i = 0; i < attArr.length; i++) {
        // for (int j = 0; j < attArr[i].length; j++) {
        // IAttribute att = attArr[i][j];
        // System.out.print(att + "|");
        // }
        // }
        // System.out.println();

        return ( getSelectedSearchResults().length == 0 && getSelectedAttributes().length == 0
            && getSelectedValues().length == 1 && SchemaUtils.isModifyable( getSelectedValues()[0].getAttribute()
            .getAttributeTypeDescription() ) )

            || ( getSelectedSearchResults().length == 0 && getSelectedValues().length == 0
                && getSelectedAttributes().length == 1 && SchemaUtils.isModifyable( getSelectedAttributes()[0]
                .getAttributeTypeDescription() ) )

            || ( getSelectedSearchResults().length == 1 && getSelectedValues().length == 0
                && getSelectedAttributes().length == 0 && getSelectedAttributeHierarchies().length == 1 && SchemaUtils
                .isModifyable( getSelectedAttributeHierarchies()[0].getAttribute().getAttributeTypeDescription() ) );
    }
}
