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
package org.apache.directory.studio.openldap.config.editor.pages;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.dialogs.TcpBufferDialog;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferWrapperLabelProvider;


/**
 * This class represents the Tuning Page of the Server Configuration Editor. We
 * manage the global tuning of the server, and more specifically, those parameters :
 * <ul>
 *   <li>Network :
 *     <ul>
 *       <li>olcTCPBuffers</li>
 *       <li>olcSockbufMaxIncoming</li>
 *       <li>olcSockbufMaxIncomingAuth</li>
 *     </ul>
 *   </li>
 *   <li>Concurrency :
 *     <ul>
 *       <li>olcConcurrency</li>
 *       <li>olcConnMaxPending</li>
 *       <li>olcConnMaxPendingAuth</li>
 *       <li>olcListenerThreads</li>
 *       <li>olcThreads</li>
 *       <li>olcToolThreads</li>
 *     </ul>
 *   </li>
 *   <li>LDAP limits :
 *     <ul>
 *       <li>olcIdleTimeout</li>
 *       <li>olcSizeLimit</li>
 *       <li>olcTimeLimit</li>
 *       <li>olcWriteTimeout</li>
 *     </ul>
 *   </li>
 *   <li>Index limits :
 *     <ul>
 *       <li>olcIndexHash64</li>
 *       <li>olcIndexIntLen</li>
 *       <li>olcIndexSubstrAnyLen</li>
 *       <li>olcIndexSubstrAnyStep</li>
 *       <li>olcIndexSubstrIfMaxLen</li>
 *       <li>olcIndexSubstrIfMinLen</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <pre>
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TuningPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = TuningPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OpenLDAPTuningPage.Title" );

    // UI Controls for the Network part
    /** The olcSockbufMaxIncoming Text */
    private Text sockbufMaxIncomingText;
    
    /** The olcSockbufMaxIncomingAuth Text */
    private Text sockbufMaxIncomingAuthText;
    
    /** The olcTCPBuffer widget */
    TableWidget<TcpBufferWrapper> tcpBufferTableWidget;
    
    // UI Controls for the Concurrency part

    /**
     * Creates a new instance of TuningPage.
     *
     * @param editor the associated editor
     */
    public TuningPage( OpenLDAPServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }

    
    
    
    /**
     * The listener for the sockbufMaxIncomingText Text
     */
    private ModifyListener sockbufMaxIncomingTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = sockbufMaxIncomingText.getDisplay();

            try
            {
                int idValue = Integer.parseInt( sockbufMaxIncomingText.getText() );

                // The value must be between 0 and 0x3FFFF
                if ( ( idValue < 0 ) || ( idValue > 0x3FFFF ) )
                {
                    System.out.println( "Wrong value : it must be a value in [0..0x3FFFF]" );
                    sockbufMaxIncomingText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                sockbufMaxIncomingText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                System.out.println( "Wrong value : it must be an integer" );
                sockbufMaxIncomingText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the sockbufMaxIncomingAuthText Text
     */
    private ModifyListener sockbufMaxIncomingAuthTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = sockbufMaxIncomingAuthText.getDisplay();

            try
            {
                int idValue = Integer.parseInt( sockbufMaxIncomingAuthText.getText() );

                // The value must be between 0 and 0x3FFFFF
                if ( ( idValue < 0 ) || ( idValue > 0x3FFFFF ) )
                {
                    System.out.println( "Wrong value : it must be a value in [0..0x3FFFFF]" );
                    sockbufMaxIncomingAuthText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                sockbufMaxIncomingAuthText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                System.out.println( "Wrong value : it must be an integer" );
                sockbufMaxIncomingAuthText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };


    /**
     * Creates the OpenLDAP tuning config Tab. It contains 2 rows, with
     * 2 columns :
     * 
     * <pre>
     * +-----------------------------------+---------------------------------+
     * |                                   |                                 |
     * | Network                           | Concurrency                     |
     * |                                   |                                 |
     * +-----------------------------------+---------------------------------+
     * |                                   |                                 |
     * | LDAP limits                       | Index limits                    |
     * |                                   |                                 |
     * +-----------------------------------+---------------------------------+
     * </pre>
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        parent.setLayout( twl );

        // The Network part
        Composite networkComposite = toolkit.createComposite( parent );
        networkComposite.setLayout( new GridLayout() );
        TableWrapData networkCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        networkCompositeTableWrapData.grabHorizontal = true;
        networkComposite.setLayoutData( networkCompositeTableWrapData );

        // The Concurrency part
        Composite concurrencyComposite = toolkit.createComposite( parent );
        concurrencyComposite.setLayout( new GridLayout() );
        TableWrapData concurrencyCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        concurrencyCompositeTableWrapData.grabHorizontal = true;
        concurrencyComposite.setLayoutData( concurrencyCompositeTableWrapData );

        // The LDAP Limits part
        Composite ldapLimitsComposite = toolkit.createComposite( parent );
        ldapLimitsComposite.setLayout( new GridLayout() );
        TableWrapData ldapLimitsCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        ldapLimitsCompositeTableWrapData.grabHorizontal = true;
        ldapLimitsComposite.setLayoutData( ldapLimitsCompositeTableWrapData );

        // The Index Limits part
        Composite indexLimitsComposite = toolkit.createComposite( parent );
        indexLimitsComposite.setLayout( new GridLayout() );
        TableWrapData indexLimitsCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        indexLimitsCompositeTableWrapData.grabHorizontal = true;
        indexLimitsComposite.setLayoutData( indexLimitsCompositeTableWrapData );

        // Now, create the sections
        createNetworkSection( toolkit, networkComposite );
        createConcurrencySection( toolkit, concurrencyComposite );
        createLdapLimitsSection( toolkit, ldapLimitsComposite );
        createIndexLimitsSection( toolkit, indexLimitsComposite );

        //refreshUI();
    }

    
    /**
     * Creates the Network section. We support the configuration
     * of those parameters :
     * <ul>
     *   <li>olcSockbufMaxIncoming</li>
     *   <li>olcSockbufMaxIncomingAuth</li>
     *   <li>olcTCPBuffer</li>
     * </ul>
     * 
     * <pre>
     * .-------------------------------------.
     * | TCP configuration                   |
     * +-------------------------------------+
     * | TCPBuffers                          |
     * | +-----------------------+           |
     * | | xyz                   | (Add)     |
     * | | abc                   | (Edit)    |
     * | |                       | (Delete)  |
     * | +-----------------------+           |
     * |                                     |
     * | Max Incoming Buffer      : [      ] |
     * | Max Incoming Buffer Auth : [      ] |
     * +-------------------------------------+
     * </pre>
     * 
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createNetworkSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.NetworkSection" ) );

        // The content
        Composite networkSectionComposite = toolkit.createComposite( section );
        toolkit.paintBordersFor( networkSectionComposite );
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        networkSectionComposite.setLayout( gridLayout );
        section.setClient( networkSectionComposite );

        // The TCPBuffers Label
        Label serverIdLabel = toolkit.createLabel( networkSectionComposite, Messages.getString( "OpenLDAPTuningPage.TCPBuffers" ) ); //$NON-NLS-1$
        serverIdLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The TCPBuffers widget
        tcpBufferTableWidget = new TableWidget<TcpBufferWrapper>();
        tcpBufferTableWidget.setLabelProvider( new TcpBufferWrapperLabelProvider() );
        tcpBufferTableWidget.setElementDialog( new TcpBufferDialog( null ) );

        tcpBufferTableWidget.createWidget( networkSectionComposite, toolkit );
        tcpBufferTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );

        // The olcSockbufMaxIncoming parameter.
        Label sockbufMaxIncomingLabel = toolkit.createLabel( networkSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.SockbufMaxIncoming" ) ); //$NON-NLS-1$
        sockbufMaxIncomingText = toolkit.createText( networkSectionComposite, "" );
        sockbufMaxIncomingText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        // From 0 to 262 143 (0x3FFFF)
        sockbufMaxIncomingText.setTextLimit( 6 );
        // Attach a listener to check the value
        sockbufMaxIncomingText.addModifyListener( sockbufMaxIncomingTextListener );

        // The olcSockbufMaxIncomingAuth parameter.
        Label sockbufMaxIncomingAuthLabel = toolkit.createLabel( networkSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.SockbufMaxIncomingAuth" ) ); //$NON-NLS-1$
        sockbufMaxIncomingAuthText = toolkit.createText( networkSectionComposite, "" );
        sockbufMaxIncomingAuthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        // From 0 to 4 193 303 (0x3FFFFF)
        sockbufMaxIncomingAuthText.setTextLimit( 7 );
        // Attach a listener to check the value
        sockbufMaxIncomingAuthText.addModifyListener( sockbufMaxIncomingAuthTextListener );

        //sockbufMaxIncomingText = createConfigDirText
        //serverIdWidget.createWidget( networkSectionComposite );
        //serverIdWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
    }
    

    private void createConcurrencySection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.ConcurrencySection" ) );
    }

    
    private void createLdapLimitsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.LdapLimitsSection" ) );
    }

    
    private void createIndexLimitsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.IndexLimitsSection" ) );
    }
    
    
    /**
     * Construct a list of TcpBufferWrapper list from teh String we get from the LDAP server.
     * We have to parse this :
     * <pre>
     * [listener=<URL>] [{read|write}=]<size>
     * </pre>
     * @param tcpBufferList
     * @return
     */
    private List<TcpBufferWrapper> createTcpBufferList( List<String> tcpBufferList )
    {
        List<TcpBufferWrapper> tcpBufferWrapperList = new ArrayList<TcpBufferWrapper>();
        
        for ( String tcpBuffer : tcpBufferList )
        {
            if ( tcpBuffer != null )
            {
                TcpBufferWrapper tcpBufferWrapper = new TcpBufferWrapper( tcpBuffer );
                
                tcpBufferWrapperList.add( tcpBufferWrapper );
            }
        }
        
        return tcpBufferWrapperList;
    }

    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        removeListeners();

        // TCPBuffer Text
        List<String> list = new ArrayList<String>();
        list.add( "Listener=http://www.apache.org write=65535" );
        List<TcpBufferWrapper> tcpBufferList = createTcpBufferList( list ); //;getConfiguration().getGlobal().getOlcTCPBuffer() );
        tcpBufferTableWidget.setElements( tcpBufferList );

        // Socket Buffer Max Incoming Text
        String sockbufMaxIncomingString = getConfiguration().getGlobal().getOlcSockbufMaxIncoming();
        
        if ( sockbufMaxIncomingString != null )
        {
            sockbufMaxIncomingText.setText( sockbufMaxIncomingString );
        }

        // Socket Buffer Max Incoming Text
        String sockbufMaxIncomingAuthString = getConfiguration().getGlobal().getOlcSockbufMaxIncomingAuth();
        
        if ( sockbufMaxIncomingAuthString != null )
        {
            sockbufMaxIncomingAuthText.setText( sockbufMaxIncomingAuthString );
        }

        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        tcpBufferTableWidget.addWidgetModifyListener( dirtyWidgetModifyListener );

        sockbufMaxIncomingText.addModifyListener( dirtyModifyListener );
        sockbufMaxIncomingAuthText.addModifyListener( dirtyModifyListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        tcpBufferTableWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );

        sockbufMaxIncomingText.removeModifyListener( dirtyModifyListener );
        sockbufMaxIncomingAuthText.removeModifyListener( dirtyModifyListener );
    }
}
