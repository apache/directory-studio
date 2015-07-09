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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.common.ui.wrappers.StringValueWrapper;
import org.apache.directory.studio.openldap.common.ui.model.RequireConditionEnum;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.SizeLimitDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.TimeLimitDialog;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.TcpBufferWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;


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
 *   +---------------------------------------------------------------------------------+
 *   | Tuning                                                                          |
 *   +---------------------------------------------------------------------------------+
 *   | .-------------------------------------. .-------------------------------------. |
 *   | | TCP configuration                   | | Concurrency                         | |
 *   | +-------------------------------------+ +-------------------------------------+ |
 *   | | TCPBuffers                          | |                                     | |
 *   | | +-----------------------+           | | Concurrency              : [      ] | |
 *   | | | xyz                   | (Add)     | | Max Pending Conn         : [      ] | |
 *   | | | abc                   | (Edit)    | | Max Pending Conn Auth    : [      ] | |
 *   | | |                       | (Delete)  | | Nb Threads               : [      ] | |
 *   | | +-----------------------+           | | Nb Threads Tool Mode     : [      ] | |
 *   | |                                     | | Nb Listener threads      : [      ] | |
 *   | | Max Incoming Buffer      : [      ] | |                                     | |
 *   | | Max Incoming Buffer Auth : [      ] | |                                     | |
 *   | +-------------------------------------+ +-------------------------------------+ |
 *   | .-------------------------------------. .-------------------------------------. |
 *   | | LDAP Limits                         | | Index Limits                        | |
 *   | +-------------------------------------+ +-------------------------------------+ |
 *   | | Write Timeout : [      ]            | | Integer Indices Length   : [      ] | |
 *   | | Idle Timeout  : [      ]            | | Subany Indices Length    : [      ] | |
 *   | | Size Limit : [             ] (Edit) | | Subany Indices Step      : [      ] | |
 *   | | Time Limit : [             ] (Edit) | | Sub indices Max length   : [      ] | |
 *   | | +-----------------------+           | | Sub indices Min length   : [      ] | |
 *   | | | xyz                   | (Add)     | +-------------------------------------+ |
 *   | | | abc                   | (Edit)    |                                         |
 *   | | |                       | (Delete)  |                                         |
 *   | | +-----------------------+           |                                         |
 *   | +-------------------------------------+                                         |
 *   +---------------------------------------------------------------------------------+
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
    private TableWidget<TcpBufferWrapper> tcpBufferTableWidget;
    
    // UI Controls for the Concurrency part
    /** The olcConcurrency Text */
    private Text concurrencyText;

    /** The olcConnMaxPending Text */
    private Text connMaxPendingText;
    
    /** The olcConnMaxPendingAuth Text */
    private Text connMaxPendingAuthText;
    
    /** The olcListenerThreads Text */
    private Text listenerThreadsText;
    
    /** The olcThreads Text */
    private Text threadsText;
    
    /** The olcToolThreads Text */
    private Text toolThreadsText;
    
    // UI Controls for the LDAP Limits
    /** The olcSizeLimit */
    private Text sizeLimitText;
    
    /** The SizeLimit edit Button */
    private Button sizeLimitEditButton;
    
    /** The TimeLimit edit Button */
    private TableWidget<TimeLimitWrapper> timeLimitTableViewer;

    /** The olcWriteTimeout */
    private Text writeTimeoutText;
    
    /** The olcIdleTimeout */
    private Text idleTimeoutText;
    
    // UI Controls for the Index Limits
    /** The olcIndexIntLenText Text */
    private Text indexIntLenText;

    /** The olcIndexSubstrAnyLen Text */
    private Text indexSubstrAnyLenText;

    /** The olcIndexSubstrAnyStep Text */
    private Text indexSubstrAnyStepText;

    /** The olcIndexSubstrIfMaxLen Text */
    private Text indexSubstrIfMaxLenText;

    /** The olcIndexSubstrIfMinLen Text */
    private Text indexSubstrIfMinLenText;


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
                int sockbufMaxIncomingValue = Integer.parseInt( sockbufMaxIncomingText.getText() );

                // The value must be between 0 and 0x3FFFF
                if ( ( sockbufMaxIncomingValue < 0 ) || ( sockbufMaxIncomingValue > 0x3FFFF ) )
                {
                    sockbufMaxIncomingText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                sockbufMaxIncomingText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcSockbufMaxIncoming( sockbufMaxIncomingValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
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
                String sockbufMaxIncomingAuthstr = sockbufMaxIncomingAuthText.getText();
                int sockbufMaxIncomingAuthValue = Integer.parseInt( sockbufMaxIncomingAuthstr );

                // The value must be between 0 and 0x3FFFFF
                if ( ( sockbufMaxIncomingAuthValue < 0 ) || ( sockbufMaxIncomingAuthValue > 0x3FFFFF ) )
                {
                    sockbufMaxIncomingAuthText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                sockbufMaxIncomingAuthText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcSockbufMaxIncomingAuth( sockbufMaxIncomingAuthstr );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                sockbufMaxIncomingAuthText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the concurrencyText Text
     */
    private ModifyListener concurrencyTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = concurrencyText.getDisplay();

            try
            {
                int concurrencyValue = Integer.parseInt( concurrencyText.getText() );

                // The value must be >= 0
                if ( concurrencyValue < 0 )
                {
                    concurrencyText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                concurrencyText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcConcurrency( concurrencyValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                concurrencyText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the connMaxPendingText Text
     */
    private ModifyListener connMaxPendingTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = connMaxPendingText.getDisplay();

            try
            {
                int connMaxPendingValue = Integer.parseInt( connMaxPendingText.getText() );

                // The value must be >= 0
                if ( connMaxPendingValue < 0 )
                {
                    connMaxPendingText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                connMaxPendingText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcConnMaxPending( connMaxPendingValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                connMaxPendingText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the connMaxPendingAuthText Text
     */
    private ModifyListener connMaxPendingAuthTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = connMaxPendingAuthText.getDisplay();

            try
            {
                int connMaxPendingAuthValue = Integer.parseInt( connMaxPendingAuthText.getText() );

                // The value must be >= 0
                if ( connMaxPendingAuthValue < 0 )
                {
                    connMaxPendingAuthText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                connMaxPendingAuthText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcConnMaxPendingAuth( connMaxPendingAuthValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                connMaxPendingAuthText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the listenerThreadsText Text
     */
    private ModifyListener listenerThreadsTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = listenerThreadsText.getDisplay();

            try
            {
                int listenerThreadsValue = Integer.parseInt( listenerThreadsText.getText() );

                // The value must be >= 0
                if ( listenerThreadsValue < 0 )
                {
                    listenerThreadsText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                listenerThreadsText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcListenerThreads( listenerThreadsValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                listenerThreadsText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the ThreadsText Text
     */
    private ModifyListener threadsTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = threadsText.getDisplay();

            try
            {
                int threadsValue = Integer.parseInt( threadsText.getText() );

                // The value must be >= 0
                if ( threadsValue < 0 )
                {
                    threadsText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                threadsText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcThreads( threadsValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                threadsText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the ToolThreadsText Text
     */
    private ModifyListener toolThreadsTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = toolThreadsText.getDisplay();

            try
            {
                int toolThreadsValue = Integer.parseInt( toolThreadsText.getText() );

                // The value must be >= 0
                if ( toolThreadsValue < 0 )
                {
                    toolThreadsText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                toolThreadsText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcToolThreads( toolThreadsValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                toolThreadsText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the IndexIntLenText Text
     */
    private ModifyListener indexIntLenTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = indexIntLenText.getDisplay();

            try
            {
                int indexIntLenValue = Integer.parseInt( indexIntLenText.getText() );

                // The value must be >= 0
                if ( indexIntLenValue < 0 )
                {
                    indexIntLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                indexIntLenText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcIndexIntLen( indexIntLenValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                indexIntLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the IndexSubstrAnyLenText Text
     */
    private ModifyListener indexSubstrAnyLenTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = indexSubstrAnyLenText.getDisplay();

            try
            {
                int indexSubstrAnyLenValue = Integer.parseInt( indexSubstrAnyLenText.getText() );

                // The value must be >= 0
                if ( indexSubstrAnyLenValue < 0 )
                {
                    indexSubstrAnyLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                indexSubstrAnyLenText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcIndexSubstrAnyLen( indexSubstrAnyLenValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                indexSubstrAnyLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the IndexSubstrAnyStepText Text
     */
    private ModifyListener indexSubstrAnyStepTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = indexSubstrAnyStepText.getDisplay();

            try
            {
                int indexSubstrAnyStepValue = Integer.parseInt( indexSubstrAnyStepText.getText() );

                // The value must be >= 0
                if ( indexSubstrAnyStepValue < 0 )
                {
                    indexSubstrAnyStepText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                indexSubstrAnyStepText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcIndexSubstrAnyStep( indexSubstrAnyStepValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                indexSubstrAnyStepText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the IndexSubstrIfMaxLenText Text
     */
    private ModifyListener indexSubstrIfMaxLenTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = indexSubstrIfMaxLenText.getDisplay();

            try
            {
                int indexSubstrIfMaxLenValue = Integer.parseInt( indexSubstrIfMaxLenText.getText() );

                // The value must be >= 0
                if ( indexSubstrIfMaxLenValue < 0 )
                {
                    indexSubstrIfMaxLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                indexSubstrIfMaxLenText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcIndexSubstrIfMaxLen( indexSubstrIfMaxLenValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                indexSubstrIfMaxLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the IndexSubstrIfMinLenText Text
     */
    private ModifyListener indexSubstrIfMinLenTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = indexSubstrIfMinLenText.getDisplay();

            try
            {
                int indexSubstrIfMinLenValue = Integer.parseInt( indexSubstrIfMinLenText.getText() );

                // The value must be >= 0
                if ( indexSubstrIfMinLenValue < 0 )
                {
                    indexSubstrIfMinLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                indexSubstrIfMinLenText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcIndexSubstrIfMinLen( indexSubstrIfMinLenValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                indexSubstrIfMinLenText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the writeTimeout Text
     */
    private ModifyListener writeTimeoutTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = writeTimeoutText.getDisplay();

            try
            {
                int writeTimeoutValue = Integer.parseInt( writeTimeoutText.getText() );

                // The value must be >= 0
                if ( writeTimeoutValue < 0 )
                {
                    writeTimeoutText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                writeTimeoutText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcWriteTimeout( writeTimeoutValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                writeTimeoutText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    /**
     * The listener for the idleTimeout Text
     */
    private ModifyListener idleTimeoutTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = idleTimeoutText.getDisplay();

            try
            {
                int idleTimeoutValue = Integer.parseInt( idleTimeoutText.getText() );

                // The value must be >= 0
                if ( idleTimeoutValue < 0 )
                {
                    idleTimeoutText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    return;
                }
                
                idleTimeoutText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getConfiguration().getGlobal().setOlcIdleTimeout( idleTimeoutValue );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                idleTimeoutText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        }
    };
    
    
    private WidgetModifyListener timeLimitTableListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> timeLimits = new ArrayList<String>();
            
            for ( LimitWrapper limitWrapper : timeLimitTableViewer.getElements() )
            {
                timeLimits.add( limitWrapper.toString() );
            }
            
            getConfiguration().getGlobal().setOlcTimeLimit( timeLimits );
        }
    };
    
    
    /**
     * The listener for the sizeLimit Text
     */
    private SelectionListener sizeLimitEditSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            SizeLimitDialog dialog = new SizeLimitDialog( sizeLimitText.getShell(), sizeLimitText.getText() );

            if ( dialog.open() == OverlayDialog.OK )
            {
                String newSizeLimitStr = dialog.getNewLimit();
                
                if ( newSizeLimitStr != null )
                {
                    sizeLimitText.setText( newSizeLimitStr );
                    getConfiguration().getGlobal().setOlcSizeLimit( newSizeLimitStr );
                }
            }
        }
    };
    
    
    // The listener for the TcpBufferTableWidget
    private WidgetModifyListener tcpBufferTableWidgetListener = new WidgetModifyListener()
    {
        @Override
        public void widgetModified( WidgetModifyEvent event )
        {
            // Process the parameter modification
            TableWidget<TcpBufferWrapper> tcpBufferWrapperTable = (TableWidget<TcpBufferWrapper>)event.getSource();
            List<String> tcpBuffers = new ArrayList<String>();
            
            for ( Object tcpBufferWrapper : tcpBufferWrapperTable.getElements() )
            {
                String str = tcpBufferWrapper.toString();
                tcpBuffers.add( str );
            }
            
            getConfiguration().getGlobal().setOlcTCPBuffer( tcpBuffers );
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
        twl.makeColumnsEqualWidth = true;
        parent.setLayout( twl );

        // The Network part
        Composite networkComposite = toolkit.createComposite( parent );
        networkComposite.setLayout( new GridLayout() );
        TableWrapData networkCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        networkCompositeTableWrapData.grabHorizontal = true;
        networkComposite.setLayoutData( networkCompositeTableWrapData );

        // The Concurrency part
        Composite concurrencyComposite = toolkit.createComposite( parent );
        concurrencyComposite.setLayout( new GridLayout() );
        TableWrapData concurrencyCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        concurrencyCompositeTableWrapData.grabHorizontal = true;
        concurrencyComposite.setLayoutData( concurrencyCompositeTableWrapData );

        // The LDAP Limits part
        Composite ldapLimitsComposite = toolkit.createComposite( parent );
        ldapLimitsComposite.setLayout( new GridLayout() );
        TableWrapData ldapLimitsCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        ldapLimitsCompositeTableWrapData.grabHorizontal = true;
        ldapLimitsComposite.setLayoutData( ldapLimitsCompositeTableWrapData );

        // The Index Limits part
        Composite indexLimitsComposite = toolkit.createComposite( parent );
        indexLimitsComposite.setLayout( new GridLayout() );
        TableWrapData indexLimitsCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        indexLimitsCompositeTableWrapData.grabHorizontal = true;
        indexLimitsComposite.setLayoutData( indexLimitsCompositeTableWrapData );

        // Now, create the sections
        createNetworkSection( toolkit, networkComposite );
        createConcurrencySection( toolkit, concurrencyComposite );
        createLdapLimitsSection( toolkit, ldapLimitsComposite );
        createIndexLimitsSection( toolkit, indexLimitsComposite );
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
        Composite networkSectionComposite = createSectionComposite( toolkit, section, 2, false );

        // The TCPBuffers Label
        Label serverIdLabel = toolkit.createLabel( networkSectionComposite, Messages.getString( "OpenLDAPTuningPage.TCPBuffers" ) ); //$NON-NLS-1$
        serverIdLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The TCPBuffers widget
        tcpBufferTableWidget = new TableWidget<TcpBufferWrapper>( new TcpBufferDecorator( networkSectionComposite.getShell() ) );

        tcpBufferTableWidget.createWidgetWithEdit( networkSectionComposite, toolkit );
        tcpBufferTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        tcpBufferTableWidget.addWidgetModifyListener( tcpBufferTableWidgetListener );

        // The olcSockbufMaxIncoming parameter.
        toolkit.createLabel( networkSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.SockbufMaxIncoming" ) ); //$NON-NLS-1$
        sockbufMaxIncomingText = toolkit.createText( networkSectionComposite, "" );
        sockbufMaxIncomingText.setLayoutData( new GridData( SWT.LEFT, SWT.NONE, false, false ) );
        // From 0 to 262 143 (0x3FFFF)
        sockbufMaxIncomingText.setTextLimit( 6 );
        // Attach a listener to check the value
        sockbufMaxIncomingText.addModifyListener( sockbufMaxIncomingTextListener );

        // The olcSockbufMaxIncomingAuth parameter.
        toolkit.createLabel( networkSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.SockbufMaxIncomingAuth" ) ); //$NON-NLS-1$
        sockbufMaxIncomingAuthText = toolkit.createText( networkSectionComposite, "" );
        sockbufMaxIncomingAuthText.setLayoutData( new GridData( SWT.LEFT, SWT.NONE, false, false ) );
        // From 0 to 4 193 303 (0x3FFFFF)
        sockbufMaxIncomingAuthText.setTextLimit( 7 );
        // Attach a listener to check the value
        sockbufMaxIncomingAuthText.addModifyListener( sockbufMaxIncomingAuthTextListener );
    }
    

    /**
     * Creates the Concurrency section. We support the configuration
     * of those parameters :
     * <ul>
     *   <li>olcConcurrency</li>
     *   <li>olcConnMaxPending</li>
     *   <li>olcConnMaxPendingAuth</li>
     *   <li>olcListenerThreads</li>
     *   <li>olcThreads</li>
     *   <li>olcToolThreads</li>
     * </ul>
     * 
     * <pre>
     * .-------------------------------------.
     * | Concurrency                         |
     * +-------------------------------------+
     * | Concurrency              : [      ] |
     * | Max Pending Conn         : [      ] |
     * | Max Pending Conn Auth    : [      ] |
     * | Nb Threads               : [      ] |
     * | Nb Threads Tool Mode     : [      ] |
     * | Nb Listener threads      : [      ] |
     * +-------------------------------------+
     * </pre>
     *
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createConcurrencySection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.ConcurrencySection" ) );

        // The content
        Composite concurrencySectionComposite = createSectionComposite( toolkit, section, 2, false );

        // The olcConcurrency parameter.
        concurrencyText = CommonUIUtils.createText( toolkit, concurrencySectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.Concurrency" ), "", 5, concurrencyTextListener );

        // The olcConnMaxPending parameter.
        connMaxPendingText = CommonUIUtils.createText( toolkit, concurrencySectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.ConnMaxPending" ), "", 5, connMaxPendingTextListener );

        // The olcConnMaxPendingAuth parameter.
        connMaxPendingAuthText = CommonUIUtils.createText( toolkit, concurrencySectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.ConnMaxPendingAuth" ), "", 5, connMaxPendingAuthTextListener );

        // The olcListenerThreads parameter.
        listenerThreadsText = CommonUIUtils.createText( toolkit, concurrencySectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.ListenerThreads" ), "", 5, listenerThreadsTextListener );

        // The olcThreads parameter.
        threadsText = CommonUIUtils.createText( toolkit, concurrencySectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.Threads" ), "", 5, threadsTextListener );

        // The olcToolThreads parameter.
        toolThreadsText = CommonUIUtils.createText( toolkit, concurrencySectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.ToolThreads" ), "", 5, toolThreadsTextListener );
    }

    
    /**
     * The Index Limits section. We support the configuration
     * of those parameters :
     * <ul>
     * </ul>
     *   <li>olcIdleTimeout</li>
     *   <li>olcSizeLimit</li>
     *   <li>olcTimeLimit</li>
     *   <li>olcWriteTimeout</li>
     * <pre>
     * .-------------------------------------.
     * | LDAP Limits                         |
     * +-------------------------------------+
     * | Write Timeout            : [      ] |
     * | Idle Timeout             : [      ] |
     * | Size Limit : [                    ] |
     * | Time Limit :                        |
     * | +-----------------------+           |
     * | | xyz                   | (Add)     |
     * | | abc                   | (Edit)    |
     * | |                       | (Delete)  |
     * | +-----------------------+           |
     * |                                     |
     * +-------------------------------------+
     * </pre>
     * 
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createLdapLimitsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.LdapLimitsSection" ) );

        // The content
        Composite ldapLimitSectionComposite = createSectionComposite( toolkit, section, 4, false );
        
        // The olcWriteTimeout parameter.
        toolkit.createLabel( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.WriteTimeout" ) ); //$NON-NLS-1$
        writeTimeoutText = toolkit.createText( ldapLimitSectionComposite, "" );
        writeTimeoutText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        // Attach a listener to check the value
        writeTimeoutText.addModifyListener( writeTimeoutTextListener );
        //toolkit.createLabel( ldapLimitSectionComposite, "" );
        //toolkit.createLabel( ldapLimitSectionComposite, "" );
        
        // The olcIdleTimeout parameter.
        toolkit.createLabel( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.IdleTimeout" ) ); //$NON-NLS-1$
        idleTimeoutText = toolkit.createText( ldapLimitSectionComposite, "" );
        idleTimeoutText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        // Attach a listener to check the value
        idleTimeoutText.addModifyListener( idleTimeoutTextListener );
        //toolkit.createLabel( ldapLimitSectionComposite, "" );
        //toolkit.createLabel( ldapLimitSectionComposite, "" );
        
        // The olcSizeLimit parameter.
        toolkit.createLabel( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.SizeLimit" ) ); //$NON-NLS-1$
        sizeLimitText = toolkit.createText( ldapLimitSectionComposite, "" );
        GridData sizeLimitData= new GridData( SWT.FILL, SWT.NONE, false, false, 1, 1 );
        sizeLimitData.horizontalAlignment = SWT.FILL;
        Rectangle rect = sizeLimitText.getShell().getMonitor().getClientArea();
        sizeLimitData.widthHint = rect.width/12;
        sizeLimitText.setLayoutData(sizeLimitData );
        sizeLimitText.setEditable( false );

        // The SizeLimit edit button
        sizeLimitEditButton = BaseWidgetUtils.createButton( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPSecurityPage.Edit" ), 1 ); //$NON-NLS-1$
        sizeLimitEditButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
        sizeLimitEditButton.addSelectionListener( sizeLimitEditSelectionListener );
        toolkit.createLabel( ldapLimitSectionComposite, "" );

        // The olcTimeLimit parameter.
        toolkit.createLabel( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.TimeLimit" ) ); //$NON-NLS-1$
        timeLimitTableViewer = new TableWidget<TimeLimitWrapper>( new TimeLimitDecorator( ldapLimitSectionComposite.getShell() ) );

        timeLimitTableViewer.createWidgetWithEdit( ldapLimitSectionComposite, toolkit );
        timeLimitTableViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
        timeLimitTableViewer.addWidgetModifyListener( timeLimitTableListener );

        /*
        toolkit.createLabel( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.TimeLimit" ) ); //$NON-NLS-1$
        timeLimitText = toolkit.createText( ldapLimitSectionComposite, "" );
        timeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        timeLimitText.setEditable( false );

        // The TimeLimit edit button
        timeLimitEditButton = BaseWidgetUtils.createButton( ldapLimitSectionComposite, 
            Messages.getString( "OpenLDAPSecurityPage.Edit" ), 1 );
        timeLimitEditButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
        timeLimitEditButton.addSelectionListener( timeLimitEditSelectionListener );
        */
    }

    
    /**
     * The Index Limits section. We support the configuration
     * of those parameters :
     * <ul>
     *   <li>olcIndexIntLen</li>
     *   <li>olcIndexSubstrAnyLen</li>
     *   <li>olcIndexSubstrAnyStep</li>
     *   <li>olcIndexSubstrIfMaxLen</li>
     *   <li>olcIndexSubstrIfMinLen</li>
     * </ul>
     * 
     * <pre>
     * .-------------------------------------.
     * | Concurrency                         |
     * +-------------------------------------+
     * | Integer Indices Length   : [      ] |
     * | Subany Indices Length    : [      ] |
     * | Subany Indices Step      : [      ] |
     * | Sub indices Max length   : [      ] |
     * | Sub indices Min length   : [      ] |
     * +-------------------------------------+
     * </pre>
     * 
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createIndexLimitsSection( FormToolkit toolkit, Composite parent )
    {
        // Creation of the section
        Section section = createSection( toolkit, parent, Messages.getString( "OpenLDAPTuningPage.IndexLimitsSection" ) );

        // The content
        Composite indexLimitSectionComposite = createSectionComposite( toolkit, section, 2, false );

        // The olcIndexIntLen parameter.
        indexIntLenText = CommonUIUtils.createText( toolkit, indexLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.IndexIntLen" ), "", 5, indexIntLenTextListener );

        // The olcIndexSubstrAnyLen parameter.
        indexSubstrAnyLenText = CommonUIUtils.createText( toolkit, indexLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.IndexSubstrAnyLen" ), "", 5, indexSubstrAnyLenTextListener );

        // The olcIndexSubstrAnyStep parameter.
        indexSubstrAnyStepText = CommonUIUtils.createText( toolkit, indexLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.IndexSubstrAnyStep" ), "", 5, indexSubstrAnyStepTextListener );

        // The olcIndexSubstrIfMaxLen parameter.
        indexSubstrIfMaxLenText = CommonUIUtils.createText( toolkit, indexLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.IndexSubstrIfMaxLen" ), "", 5, indexSubstrIfMaxLenTextListener );

        // The olcIndexSubstrIfMinLen parameter.
        indexSubstrIfMinLenText = CommonUIUtils.createText( toolkit, indexLimitSectionComposite, 
            Messages.getString( "OpenLDAPTuningPage.IndexSubstrIfMinLen" ), "", 5, indexSubstrIfMinLenTextListener );
    }
    
    
    /**
     * Construct a list of TcpBufferWrapper list from the String we get from the LDAP server.
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

        // Getting the global configuration object
        OlcGlobal global = getConfiguration().getGlobal();

        if ( global != null )
        {
            //
            // Assigning values to UI Controls
            //
            // TCPBuffer Text
            List<TcpBufferWrapper> tcpBufferList = createTcpBufferList( global.getOlcTCPBuffer() );
            tcpBufferTableWidget.setElements( tcpBufferList );
    
            // Socket Buffer Max Incoming Text
            BaseWidgetUtils.setValue( global.getOlcSockbufMaxIncoming(), sockbufMaxIncomingText );
    
            // Socket Buffer Max Incoming Text
            BaseWidgetUtils.setValue( global.getOlcSockbufMaxIncomingAuth(), sockbufMaxIncomingAuthText );
    
            // Concurrency Text
            BaseWidgetUtils.setValue( global.getOlcConcurrency(), concurrencyText );
    
            // ConnMaxPending Text
            BaseWidgetUtils.setValue( global.getOlcConnMaxPending(), connMaxPendingText );
    
            // ConnMaxPendingAuth Text
            BaseWidgetUtils.setValue( global.getOlcConnMaxPendingAuth(), connMaxPendingAuthText );
    
            // ListenerThreads Text
            BaseWidgetUtils.setValue( global.getOlcListenerThreads(), listenerThreadsText );
    
            // Threads Text
            BaseWidgetUtils.setValue( global.getOlcThreads(), threadsText );
    
            // ToolThreads Text
            BaseWidgetUtils.setValue( global.getOlcToolThreads(), toolThreadsText );
    
            // IndexIntLen Text
            BaseWidgetUtils.setValue( global.getOlcIndexIntLen(), indexIntLenText );
    
            // IndexSubstrAnyLen Text
            BaseWidgetUtils.setValue( global.getOlcIndexSubstrAnyLen(), indexSubstrAnyLenText );
    
            // IndexSubstrAnyStep Text
            BaseWidgetUtils.setValue( global.getOlcIndexSubstrAnyStep(), indexSubstrAnyStepText );
    
            // IndexSubstrIfMaxLen Text
            BaseWidgetUtils.setValue( global.getOlcIndexSubstrIfMaxLen(), indexSubstrIfMaxLenText );
    
            // IndexSubstrIfMinLen Text
            BaseWidgetUtils.setValue( global.getOlcIndexSubstrIfMinLen(), indexSubstrIfMinLenText );
    
            // IndexSubstrIfMinLen Text
            BaseWidgetUtils.setValue( global.getOlcWriteTimeout(), writeTimeoutText );
    
            // IdleTiemout Text
            BaseWidgetUtils.setValue( global.getOlcIdleTimeout(), idleTimeoutText );
    
            // SizeLimit Text
            BaseWidgetUtils.setValue( global.getOlcSizeLimit(), sizeLimitText );
    
            // TimeLimit Text Text
            List<String> timeLimitList = getConfiguration().getGlobal().getOlcTimeLimit();
            List<TimeLimitWrapper> limitWrappers = new ArrayList<TimeLimitWrapper>();

            if ( ( timeLimitList != null ) && ( timeLimitList.size() > 0 ) )
            {
                for ( String timeLimit : timeLimitList )
                {
                    limitWrappers.add( new TimeLimitWrapper( timeLimit ) );
                }
                
                timeLimitTableViewer.setElements( limitWrappers );
            }
        }
        
        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        addDirtyListener( tcpBufferTableWidget );
        addDirtyListener( sockbufMaxIncomingText );
        addDirtyListener( sockbufMaxIncomingAuthText );
        addDirtyListener( concurrencyText );
        addDirtyListener( connMaxPendingText );
        addDirtyListener( connMaxPendingAuthText );
        addDirtyListener( listenerThreadsText );
        addDirtyListener( threadsText );
        addDirtyListener( toolThreadsText );
        addDirtyListener( indexIntLenText );
        addDirtyListener( indexSubstrAnyLenText );
        addDirtyListener( indexSubstrAnyStepText );
        addDirtyListener( indexSubstrIfMaxLenText );
        addDirtyListener( indexSubstrIfMinLenText );
        addDirtyListener( writeTimeoutText );
        addDirtyListener( idleTimeoutText);
        addDirtyListener( sizeLimitText );
        addDirtyListener( timeLimitTableViewer );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        removeDirtyListener( tcpBufferTableWidget );
        removeDirtyListener( sockbufMaxIncomingText );
        removeDirtyListener( sockbufMaxIncomingAuthText );
        removeDirtyListener( concurrencyText );
        removeDirtyListener( connMaxPendingText );
        removeDirtyListener( connMaxPendingAuthText );
        removeDirtyListener( listenerThreadsText );
        removeDirtyListener( threadsText );
        removeDirtyListener( toolThreadsText );
        removeDirtyListener( indexIntLenText );
        removeDirtyListener( indexSubstrAnyLenText );
        removeDirtyListener( indexSubstrAnyStepText );
        removeDirtyListener( indexSubstrIfMaxLenText );
        removeDirtyListener( indexSubstrIfMinLenText );
        removeDirtyListener( writeTimeoutText );
        removeDirtyListener( idleTimeoutText);
        removeDirtyListener( sizeLimitText );
        removeDirtyListener( timeLimitTableViewer );
    }
}
