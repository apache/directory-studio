/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.HistoryUtils;
import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPlugin;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPluginConstants;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesWidget extends AbstractWidget
{
    /** The initial attributes. */
    private String[] initialAttributes;

    /** The attributes combo */
    private Combo attributesCombo;

    /** The proposal provider */
    private AttributesWidgetContentProposalProvider proposalProvider;

    /** The proposal adapter*/
    private ContentProposalAdapter proposalAdapter;

    /** The label provider for the proposal adapter */
    private LabelProvider labelProvider = new LabelProvider()
    {
        public String getText( Object element )
        {
            if ( element instanceof IContentProposal )
            {
                IContentProposal proposal = ( IContentProposal ) element;
                return proposal.getLabel() == null ? proposal.getContent() : proposal.getLabel();
            }

            return super.getText( element );
        };


        public Image getImage( Object element )
        {
            if ( element instanceof AttributeTypeContentProposal )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ATD );
            }
            else if ( element instanceof ObjectClassContentProposal )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD );
            }
            else if ( element instanceof KeywordContentProposal )
            {
                return OpenLdapAclEditorPlugin.getDefault().getImage( OpenLdapAclEditorPluginConstants.IMG_KEYWORD );
            }

            return super.getImage( element );
        }
    };

    /** The verify listener which doesn't allow white spaces*/
    private VerifyListener verifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            // Not allowing white spaces
            if ( Character.isWhitespace( e.character ) )
            {
                e.doit = false;
            }
        }
    };

    /** The modify listener */
    private ModifyListener modifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            notifyListeners();
        }
    };


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        // Combo
        attributesCombo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        attributesCombo.setLayoutData( gd );
        attributesCombo.addVerifyListener( verifyListener );

        // Content assist
        proposalProvider = new AttributesWidgetContentProposalProvider();
        proposalAdapter = new ContentProposalAdapter( attributesCombo, new ComboContentAdapter(),
            proposalProvider, KeyStroke.getInstance( SWT.CTRL, SWT.SPACE ), new char[0] );
        proposalProvider.setProposalAdapter( proposalAdapter );
        proposalAdapter.setLabelProvider( labelProvider );

        // History
        String[] history = HistoryUtils.load( OpenLdapAclEditorPlugin.getDefault().getDialogSettings(),
            OpenLdapAclEditorPluginConstants.DIALOGSETTING_KEY_ATTRIBUTES_HISTORY );
        for ( int i = 0; i < history.length; i++ )
        {
            history[i] = history[i];
        }
        attributesCombo.setItems( history );
        attributesCombo.setText( arrayToString( initialAttributes ) );
        attributesCombo.addModifyListener( modifyListener );
    }


    /**
     * Sets the initial attributes.
     * 
     * @param initialAttributes the initial attributes
     */
    public void setInitialAttributes( String[] initialAttributes )
    {
        this.initialAttributes = initialAttributes;
        attributesCombo.setText( arrayToString( initialAttributes ) );
    }


    /**
     * @param browserConnection the browser connection to set
     */
    public void setBrowserConnection( IBrowserConnection browserConnection )
    {
        proposalProvider.setBrowserConnection( browserConnection );
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        attributesCombo.setEnabled( b );
    }


    /**
     * Gets the attributes.
     * 
     * @return the attributes
     */
    public String[] getAttributes()
    {
        String s = attributesCombo.getText();
        return stringToArray( s );
    }


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings()
    {
        HistoryUtils.save( OpenLdapAclEditorPlugin.getDefault().getDialogSettings(),
            OpenLdapAclEditorPluginConstants.DIALOGSETTING_KEY_ATTRIBUTES_HISTORY,
            arrayToString( getAttributes() ) );
    }


    /**
     * Splits the given string into an array. Only the following
     * characters are kept, all other are used to split the string
     * and are truncated:
     * <li>a-z
     * <li>A-Z
     * <li>0-9
     * <li>-
     * <li>.
     * <li>;
     * <li>_
     * <li>*
     * <li>+
     * <li>@
     * <li>!
     * 
     * @param s the string to split
     * 
     * @return the array with the splitted string, or null
     */
    public static String[] stringToArray( String s )
    {
        if ( s == null )
        {
            return null;
        }
        else
        {
            List<String> attributeList = new ArrayList<String>();

            StringBuffer temp = new StringBuffer();
            for ( int i = 0; i < s.length(); i++ )
            {
                char c = s.charAt( i );

                if ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) || ( c >= '0' && c <= '9' ) || c == '-'
                    || c == '.' || c == ';' || c == '_' || c == '*' || c == '+' || c == '@' || c == '!' )
                {
                    temp.append( c );
                }
                else
                {
                    if ( temp.length() > 0 )
                    {
                        attributeList.add( temp.toString() );
                        temp = new StringBuffer();
                    }
                }
            }
            if ( temp.length() > 0 )
            {
                attributeList.add( temp.toString() );
            }

            return ( String[] ) attributeList.toArray( new String[attributeList.size()] );
        }
    }


    public static String arrayToString( String[] array )
    {
        if ( array == null || array.length == 0 )
        {
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer( array[0] );
            for ( int i = 1; i < array.length; i++ )
            {
                sb.append( "," );
                sb.append( array[i] );
            }
            return sb.toString();
        }
    }
}
