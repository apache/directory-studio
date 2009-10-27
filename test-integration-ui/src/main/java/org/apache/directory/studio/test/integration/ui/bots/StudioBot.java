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
package org.apache.directory.studio.test.integration.ui.bots;


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class StudioBot
{

    public ConnectionsViewBot getConnectionView()
    {
        return new ConnectionsViewBot();
    }


    public BrowserViewBot getBrowserView()
    {
        return new BrowserViewBot();
    }


    public EntryEditorBot getEntryEditorBot( String title )
    {
        return new EntryEditorBot( title );
    }


    public void resetLdapPerspective()
    {
        UIThreadRunnable.syncExec( new VoidResult()
        {
            public void run()
            {
                try
                {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

                    // close welcome view
                    IWorkbenchPage page = window.getActivePage();
                    for ( IViewReference viewref : page.getViewReferences() )
                    {
                        if ( "org.eclipse.ui.internal.introview".equals( viewref.getId() ) )
                        {
                            page.hideView( viewref );
                        }
                    }

                    // close shells (open dialogs)
                    Shell activeShell = Display.getCurrent().getActiveShell();
                    if ( activeShell != null && activeShell != window.getShell() )
                    {
                        activeShell.close();
                    }

                    // open LDAP perspective
                    workbench.showPerspective(
                        "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective", window );

                    // reset LDAP perspective
                    page.closeAllEditors( false );
                    page.resetPerspective();

                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        } );

    }

}
