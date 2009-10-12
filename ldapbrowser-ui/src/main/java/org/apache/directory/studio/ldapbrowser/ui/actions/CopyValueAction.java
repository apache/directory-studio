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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


/**
 * This Action copies values of the seleced Entry to the Clipboard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopyValueAction extends BrowserAction
{

    public enum Mode
    {
        /**
         * UTF8 Mode.
         */
        UTF8,

        /**
         * Base64 Mode.
         */
        BASE64,

        /**
         * Hexadecimal Mode. 
         */
        HEX,

        /**
         * LDIF Mode.
         */
        LDIF,

        /**
         * Display mode, copies the display value.
         */
        DISPLAY,
    }

    private Mode mode;

    private ValueEditorManager valueEditorManager;


    /**
     * Creates a new instance of CopyValueAction.
     *
     * @param mode
     *      the copy Mode
     */
    public CopyValueAction( Mode mode, ValueEditorManager valueEditorManager )
    {
        this.mode = mode;
        this.valueEditorManager = valueEditorManager;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        switch ( mode )
        {
            case UTF8:
                return getValueSet().size() > 1 ? Messages.getString( "CopyValueAction.CopyValuesUTF" ) : Messages.getString( "CopyValueAction.CopyValueUTF" ); //$NON-NLS-1$ //$NON-NLS-2$
            case BASE64:
                return getValueSet().size() > 1 ? Messages.getString( "CopyValueAction.CopyValuesBase" ) : Messages.getString( "CopyValueAction.CopyValueBase" ); //$NON-NLS-1$ //$NON-NLS-2$
            case HEX:
                return getValueSet().size() > 1 ? Messages.getString( "CopyValueAction.VopyValuesHex" ) : Messages.getString( "CopyValueAction.CopyValueHex" ); //$NON-NLS-1$ //$NON-NLS-2$
            case LDIF:
                return getValueSet().size() > 1 ? Messages.getString( "CopyValueAction.CopyValuePairs" ) : Messages.getString( "CopyValueAction.CopyValuePair" ); //$NON-NLS-1$ //$NON-NLS-2$
            case DISPLAY:
                return getValueSet().size() > 1 ? Messages.getString( "CopyValueAction.CopyDisplayValues" ) : Messages.getString( "CopyValueAction.CopyDisplayValue" ); //$NON-NLS-1$ //$NON-NLS-2$
            default:
                return Messages.getString( "CopyValueAction.CopyValue" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        switch ( mode )
        {
            case UTF8:
                return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_UTF8 );
            case BASE64:
                return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_BASE64 );
            case HEX:
                return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_HEX );
            case LDIF:
                return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_LDIF );
            case DISPLAY:
                return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_DISPLAY );
            default:
                return null;
        }
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
        return getValueSet().size() > 0 || getSelectedSearchResults().length > 0;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        StringBuffer text = new StringBuffer();
        Set<IValue> valueSet = getValueSet();
        if ( !valueSet.isEmpty() )
        {
            for ( Iterator<IValue> iterator = valueSet.iterator(); iterator.hasNext(); )
            {
                IValue value = iterator.next();
                switch ( mode )
                {
                    case UTF8:
                        text.append( LdifUtils.utf8decode( value.getBinaryValue() ) );
                        if ( iterator.hasNext() )
                        {
                            text.append( BrowserCoreConstants.LINE_SEPARATOR );
                        }
                        break;
                    case BASE64:
                        text.append( LdifUtils.base64encode( value.getBinaryValue() ) );
                        if ( iterator.hasNext() )
                        {
                            text.append( BrowserCoreConstants.LINE_SEPARATOR );
                        }
                        break;
                    case HEX:
                        text.append( LdifUtils.hexEncode( value.getBinaryValue() ) );
                        if ( iterator.hasNext() )
                        {
                            text.append( BrowserCoreConstants.LINE_SEPARATOR );
                        }
                        break;
                    case LDIF:
                        text.append( ModelConverter.valueToLdifAttrValLine( value ).toFormattedString(
                            Utils.getLdifFormatParameters() ) );
                        break;
                    case DISPLAY:
                        IValueEditor ve = valueEditorManager.getCurrentValueEditor( value );
                        String displayValue = ve.getDisplayValue( value );
                        text.append( displayValue );
                        if ( iterator.hasNext() )
                        {
                            text.append( BrowserCoreConstants.LINE_SEPARATOR );
                        }
                        break;
                }
            }
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            LdapDN dn = getSelectedSearchResults()[0].getDn();
            switch ( mode )
            {
                case UTF8:
                case DISPLAY:
                    text.append( dn.getUpName() );
                    break;
                case BASE64:
                    text.append( LdifUtils.base64encode( LdifUtils.utf8encode( dn.getUpName() ) ) );
                    break;
                case HEX:
                    text.append( LdifUtils.hexEncode( LdifUtils.utf8encode( dn.getUpName() ) ) );
                    break;
                case LDIF:
                    text.append( ModelConverter.dnToLdifDnLine( dn )
                        .toFormattedString( Utils.getLdifFormatParameters() ) );
                    break;
            }
        }

        if ( text.length() > 0 )
        {
            CopyAction.copyToClipboard( new Object[]
                { text.toString() }, new Transfer[]
                { TextTransfer.getInstance() } );
        }
    }


    /**
     * Gets a Set containing all the Values
     *
     * @return
     *      a Set containing all the Values
     */
    protected Set<IValue> getValueSet()
    {
        Set<IValue> valueSet = new LinkedHashSet<IValue>();
        for ( AttributeHierarchy ah : getSelectedAttributeHierarchies() )
        {
            for ( IAttribute att : ah )
            {
                valueSet.addAll( Arrays.asList( att.getValues() ) );
            }
        }
        for ( IAttribute att : getSelectedAttributes() )
        {
            valueSet.addAll( Arrays.asList( att.getValues() ) );
        }
        valueSet.addAll( Arrays.asList( getSelectedValues() ) );
        return valueSet;
    }
}
