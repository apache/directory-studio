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
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;


/**
 * This Action adds a new Value to an Attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewValueAction extends BrowserAction
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
        IAttribute attribute = null;
        if ( getSelectedValues().length == 1 )
        {
            attribute = getSelectedValues()[0].getAttribute();
        }
        else if ( getSelectedAttributes().length == 1 )
        {
            attribute = getSelectedAttributes()[0];
        }
        else if ( getSelectedAttributeHierarchies().length == 1 )
        {
            attribute = getSelectedAttributeHierarchies()[0].getAttribute();
        }

        // validate non-modifiable and single-valued attributes
        StringBuffer message = new StringBuffer();
        if ( !SchemaUtils.isModifiable( attribute.getAttributeTypeDescription() ) )
        {
            message.append( NLS.bind( Messages.getString( "NewValueAction.NewValueNotModifiable" ), attribute //$NON-NLS-1$
                .getDescription() ) );
            message.append( BrowserCoreConstants.LINE_SEPARATOR );
            message.append( BrowserCoreConstants.LINE_SEPARATOR );
        }
        if ( attribute.getAttributeTypeDescription().isSingleValued() )
        {
            message.append( NLS.bind( Messages.getString( "NewValueAction.NewValueSingleValued" ), attribute //$NON-NLS-1$
                .getDescription() ) );
            message.append( BrowserCoreConstants.LINE_SEPARATOR );
            message.append( BrowserCoreConstants.LINE_SEPARATOR );
        }

        boolean ok = true;
        if ( message.length() > 0 )
        {
            message.append( Messages.getString( "NewValueAction.NewValueQuestion" ) ); //$NON-NLS-1$
            ok = MessageDialog.openConfirm( getShell(), getText(), message.toString() );
        }
        if ( ok )
        {
            attribute.addEmptyValue();
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "NewValueAction.NewValue" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_VALUE_ADD );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return BrowserCommonConstants.CMD_ADD_VALUE;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return ( getSelectedSearchResults().length == 0 && getSelectedAttributes().length == 0 && getSelectedValues().length == 1 )

            || ( getSelectedSearchResults().length == 0 && getSelectedValues().length == 0 && getSelectedAttributes().length == 1 )

            || ( getSelectedSearchResults().length == 1 && getSelectedValues().length == 0
                && getSelectedAttributes().length == 0 && getSelectedAttributeHierarchies().length == 1 );
    }
}
