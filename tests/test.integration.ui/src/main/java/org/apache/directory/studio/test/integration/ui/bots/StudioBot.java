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


import java.util.Random;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class StudioBot
{

    public StudioBot()
    {
        SWTBotPreferences.KEYBOARD_LAYOUT = "org.eclipse.swtbot.swt.finder.keyboard.EN_US";
    }


    public ConnectionsViewBot getConnectionView()
    {
        return new ConnectionsViewBot();
    }


    public BrowserViewBot getBrowserView()
    {
        return new BrowserViewBot();
    }


    public SearchLogsViewBot getSearchLogsViewBot()
    {
        return new SearchLogsViewBot();
    }


    public ModificationLogsViewBot getModificationLogsViewBot()
    {
        return new ModificationLogsViewBot();
    }


    public ApacheDSServersViewBot getApacheDSServersViewBot()
    {
        return new ApacheDSServersViewBot();
    }


    public ProgressViewBot getProgressView()
    {
        return new ProgressViewBot();
    }


    public EntryEditorBot getEntryEditorBot( String title )
    {
        return new EntryEditorBot( title );
    }


    public SearchResultEditorBot getSearchResultEditorBot( String title )
    {
        return new SearchResultEditorBot( title );
    }


    public ConsoleViewBot getConsoleView()
    {
        ShowViewsBot showViewsBot = openShowViews();
        showViewsBot.openView( "General", "Console" );
        return new ConsoleViewBot();
    }


    public SchemaProjectsViewBot getSchemaProjectsView()
    {
        return new SchemaProjectsViewBot();
    }


    public SchemaViewBot getSchemaView()
    {
        return new SchemaViewBot();
    }


    public SchemaSearchViewBot getSchemaSearchView()
    {
        return new SchemaSearchViewBot();
    }


    public void resetLdapPerspective()
    {
        resetPerspective( "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective" );
    }


    public void resetSchemaPerspective()
    {
        resetPerspective( "org.apache.directory.studio.schemaeditor.perspective" );
    }


    private void resetPerspective( final String perspectiveId )
    {
        UIThreadRunnable.syncExec( new VoidResult()
        {
            public void run()
            {
                try
                {
                    // https://wiki.eclipse.org/SWTBot/Troubleshooting#No_active_Shell_when_running_SWTBot_tests_in_Xvfb
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();

                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

                    // set default/dark theme
                    IThemeManager tm = workbench.getService( IThemeManager.class );
                    IThemeEngine te = tm.getEngineForDisplay( Display.getCurrent() );
                    if ( new Random().nextBoolean() )
                    {
                        te.setTheme( "org.eclipse.e4.ui.css.theme.e4_dark", false );
                    }
                    else
                    {
                        te.setTheme( "org.eclipse.e4.ui.css.theme.e4_default", false );
                    }

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
                    workbench.showPerspective( perspectiveId, window );

                    // close "LDAP Browser view" as it sometimes does not respond, will be re-opened by the following reset
                    for ( IViewReference viewref : page.getViewReferences() )
                    {
                        if ( "org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView".equals( viewref
                            .getId() ) )
                        {
                            page.hideView( viewref );
                        }
                    }

                    // reset LDAP perspective
                    if ( page.getActivePart() != null )
                    {
                        page.closeAllEditors( false );
                        page.resetPerspective();
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        } );
    }


    public PreferencesBot openPreferences()
    {
        if ( SWTUtils.isMac() )
        {
            // new SWTBot().activeShell().pressShortcut( SWT.COMMAND, ',' );
            final IWorkbench workbench = PlatformUI.getWorkbench();
            workbench.getDisplay().asyncExec( new Runnable()
            {
                public void run()
                {
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    if ( window != null )
                    {
                        Menu appMenu = workbench.getDisplay().getSystemMenu();
                        for ( MenuItem item : appMenu.getItems() )
                        {
                            if ( item.getText().startsWith( "Preferences" ) )
                            {
                                Event event = new Event();
                                event.time = ( int ) System.currentTimeMillis();
                                event.widget = item;
                                event.display = workbench.getDisplay();
                                item.setSelection( true );
                                item.notifyListeners( SWT.Selection, event );
                                break;
                            }
                        }
                    }
                }
            } );
        }
        else
        {
            new SWTBot().menu( "Window" ).menu( "Preferences" ).click();
        }
        return new PreferencesBot();
    }


    public NewWizardBot openNewWizard()
    {
        SWTBotMenu file = new SWTBot().menu( "File" );
        if ( file.menuItems().contains( "New" ) )
        {
            // In RCP application
            file.menu( "New" ).menu( "Other..." ).click();
        }
        else
        {
            // In IDE
            file.menu( "New..." ).click();
        }
        return new NewWizardBot();
    }


    public ExportWizardBot openExportWizard()
    {
        new SWTBot().menu( "File" ).menu( "Export..." ).click();
        return new ExportWizardBot();
    }


    public ImportWizardBot openImportWizard()
    {
        new SWTBot().menu( "File" ).menu( "Import..." ).click();
        return new ImportWizardBot();
    }


    public ShowViewsBot openShowViews()
    {
        new SWTBot().menu( "Window" ).menu( "Show View" ).menu( "Other..." ).click();
        return new ShowViewsBot();
    }


    public void navigationHistoryBack()
    {
        SWTBotMenu backMenu = new SWTWorkbenchBot().menu( "Navigate" ).menu( "Back" );
        String firstItem = backMenu.menuItems().get( 0 );
        backMenu.menu( firstItem ).click();
    }


    public void navigationHistoryForward()
    {
        SWTBotMenu forwardMenu = new SWTWorkbenchBot().menu( "Navigate" ).menu( "Forward" );
        String firstItem = forwardMenu.menuItems().get( 0 );
        forwardMenu.menu( firstItem ).click();
    }

}
