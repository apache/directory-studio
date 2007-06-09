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
package org.apache.directory.ldapstudio.proxy.view;


import org.apache.directory.ldapstudio.proxy.Activator;
import org.apache.directory.ldapstudio.proxy.controller.ProxyViewController;
import org.apache.directory.ldapstudio.proxy.model.LdapMessageWithPDU;
import org.apache.directory.ldapstudio.proxy.model.LdapProxy;
import org.apache.directory.ldapstudio.proxy.model.LdapProxyListener;
import org.apache.directory.ldapstudio.proxy.view.wrappers.IWrapper;
import org.apache.directory.ldapstudio.proxy.view.wrappers.LdapMessageWrapper;
import org.apache.directory.ldapstudio.proxy.view.wrappers.LdapProxyWrapper;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapOrFilterComponent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Proxy View where all LDAP Messages Hierarchy (LDAP Requests and Responses) is displayed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProxyView extends ViewPart
{
    /** The view ID*/
    public static final String ID = Activator.PLUGIN_ID + ".view.ProxyView";

    /** The controller */
    private ProxyViewController controller;

    /** The tree viewer */
    private TreeViewer viewer;

    /** The LDAP Proxy */
    private LdapProxy ldapProxy;

    /** The LDAP Proxy Wrapper */
    private LdapProxyWrapper ldapProxyWrapper;

    /** The Proxy Listener */
    private LdapProxyListener proxyListener = new LdapProxyListener()
    {
        public void ldapMessageReceived( final LdapMessageWithPDU ldapMessage )
        {
            System.out.println( ldapMessage.getLdapMessage() );
            PlatformUI.getWorkbench().getDisplay().asyncExec( new Runnable()
            {
                public void run()
                {
//                    viewer.refresh();
                    ( ( LdapProxyWrapper ) viewer.getInput() ).addChild( new LdapMessageWrapper( ( IWrapper ) viewer.getInput(), ldapMessage ) );
                }
            } );
        }
    };


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        viewer.setContentProvider( new ProxyViewContentProvider() );
        viewer.setLabelProvider( new ProxyViewLabelProvider() );
        ldapProxyWrapper = new LdapProxyWrapper( this );
        viewer.setInput( ldapProxyWrapper );

        controller = new ProxyViewController( this );

        //        // TODO Remove
        //        TreeItem treeItem = new TreeItem( viewer, SWT.NONE );
        //        treeItem.setText( "Bind [id=1]" );
        //
        //        treeItem = new TreeItem( treeItem, SWT.NONE );
        //        treeItem.setText( "Bind Request" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.IN )
        //            .createImage() );
        //
        //        treeItem = new TreeItem( viewer.getTopItem(), SWT.NONE );
        //        treeItem.setText( "Bind Response" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OUT )
        //            .createImage() );
        //
        //        TreeItem treeItem2 = new TreeItem( viewer, SWT.NONE );
        //        treeItem2.setText( "Search [id=2]" );
        //
        //        treeItem = new TreeItem( treeItem2, SWT.NONE );
        //        treeItem.setText( "Search Request" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.IN )
        //            .createImage() );
        //
        //        treeItem = new TreeItem( treeItem2, SWT.NONE );
        //        treeItem.setText( "Search Result Entry" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OUT )
        //            .createImage() );
        //
        //        treeItem = new TreeItem( treeItem2, SWT.NONE );
        //        treeItem.setText( "Search Result Entry" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OUT )
        //            .createImage() );
        //
        //        treeItem = new TreeItem( treeItem2, SWT.NONE );
        //        treeItem.setText( "Search Result Entry" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OUT )
        //            .createImage() );
        //
        //        treeItem = new TreeItem( treeItem2, SWT.NONE );
        //        treeItem.setText( "Search Result Done" );
        //        treeItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OUT )
        //            .createImage() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


    public ProxyViewController getController()
    {
        return controller;
    }


    public TreeViewer getViewer()
    {
        return viewer;
    }


    public LdapProxy getLdapProxy()
    {
        return ldapProxy;
    }


    public void setLdapProxy( LdapProxy ldapProxy )
    {
        if ( this.ldapProxy != null )
        {
            this.ldapProxy.removeListener( proxyListener );
        }
        this.ldapProxy = ldapProxy;
        if ( ldapProxy != null )
        {
            ldapProxy.addListener( proxyListener );
        }
    }
}
