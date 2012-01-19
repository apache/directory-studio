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

package org.apache.directory.studio;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;


/**
 * The workbench window advisor object is created in response to a workbench window 
 * being created (one per window), and is used to configure the window.<br />
 * <br />
 * The following advisor methods are called at strategic points in the workbench window's 
 * lifecycle (as with the workbench advisor, all occur within the dynamic scope of the call 
 * to PlatformUI.createAndRunWorkbench):<br />
 * <br />
 *  - preWindowOpen - called as the window is being opened; use to configure aspects of the 
 *  window other than actions bars<br />
 *  - postWindowRestore - called after the window has been recreated from a previously saved 
 *  state; use to adjust the restored window<br />
 *  - postWindowCreate - called after the window has been created, either from an initial 
 *  state or from a restored state; used to adjust the window<br />
 *  - openIntro - called immediately before the window is opened in order to create the 
 *  introduction component, if any.<br />
 *  - postWindowOpen - called after the window has been opened; use to hook window listeners, 
 *  etc.<br />
 *  - preWindowShellClose - called when the window's shell is closed by the user; use to 
 *  pre-screen window closings
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
    private IEditorPart lastActiveEditor = null;
    private IPerspectiveDescriptor lastPerspective = null;
    private IWorkbenchPage lastActivePage;
    private String lastEditorTitle = ""; //$NON-NLS-1$
    private IAdaptable lastInput;
    private IPropertyListener editorPropertyListener = new IPropertyListener()
    {
        public void propertyChanged( Object source, int propId )
        {
            if ( propId == IWorkbenchPartConstants.PROP_TITLE )
            {
                if ( lastActiveEditor != null )
                {
                    String newTitle = lastActiveEditor.getTitle();
                    if ( !lastEditorTitle.equals( newTitle ) )
                    {
                        recomputeTitle();
                    }
                }
            }
        }
    };


    /**
     * Default constructor
     * @param configurer 
     *          an object for configuring the workbench window
     */
    public ApplicationWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer configurer )
    {
        super( configurer );
    }


    /**
     * Creates a new action bar advisor to configure the action bars of the window via 
     * the given action bar configurer. The default implementation returns a new instance 
     * of ActionBarAdvisor.
     */
    public ActionBarAdvisor createActionBarAdvisor( IActionBarConfigurer configurer )
    {
        return new ApplicationActionBarAdvisor( configurer );
    }


    /**
     * Performs arbitrary actions before the window is opened.<br />
     * <br />
     * This method is called before the window's controls have been created. Clients must 
     * not call this method directly (although super calls are okay). The default 
     * implementation does nothing. Subclasses may override. Typical clients will use the 
     * window configurer to tweak the workbench window in an application-specific way; 
     * however, filling the window's menu bar, tool bar, and status line must be done in 
     * ActionBarAdvisor.fillActionBars, which is called immediately after this method is 
     * called. 
     */
    public void preWindowOpen()
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize( new Point( 950, 708 ) );
        configurer.setShowCoolBar( true );
        configurer.setShowStatusLine( false );
        configurer.setShowPerspectiveBar( true );
        configurer.setShowProgressIndicator( true );
        configurer.setShowFastViewBars( true );

        // hopk up the listeners to update the window title
        // adapted from org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor 
        // http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.ui.ide.application/src/org/eclipse/ui/internal/ide/application/IDEWorkbenchWindowAdvisor.java?view=markup 
        hookTitleUpdateListeners( configurer );
    }


    /**
     * Hooks up the listeners to update the window title.
     * 
     * @param configurer
     */
    private void hookTitleUpdateListeners( IWorkbenchWindowConfigurer configurer )
    {
        configurer.getWindow().addPageListener( new IPageListener()
        {
            public void pageActivated( IWorkbenchPage page )
            {
                updateTitle( false );
            }


            public void pageClosed( IWorkbenchPage page )
            {
                updateTitle( false );
            }


            public void pageOpened( IWorkbenchPage page )
            {
                // do nothing
            }
        } );
        configurer.getWindow().addPerspectiveListener( new PerspectiveAdapter()
        {
            public void perspectiveActivated( IWorkbenchPage page, IPerspectiveDescriptor perspective )
            {
                updateTitle( false );
            }


            public void perspectiveSavedAs( IWorkbenchPage page, IPerspectiveDescriptor oldPerspective,
                IPerspectiveDescriptor newPerspective )
            {
                updateTitle( false );
            }


            public void perspectiveDeactivated( IWorkbenchPage page, IPerspectiveDescriptor perspective )
            {
                updateTitle( false );
            }
        } );
        configurer.getWindow().getPartService().addPartListener( new IPartListener2()
        {
            public void partActivated( IWorkbenchPartReference ref )
            {
                if ( ref instanceof IEditorReference )
                {
                    updateTitle( false );
                }
            }


            public void partBroughtToTop( IWorkbenchPartReference ref )
            {
                if ( ref instanceof IEditorReference )
                {
                    updateTitle( false );
                }
            }


            public void partClosed( IWorkbenchPartReference ref )
            {
                updateTitle( false );
            }


            public void partDeactivated( IWorkbenchPartReference ref )
            {
                // do nothing
            }


            public void partOpened( IWorkbenchPartReference ref )
            {
                // do nothing
            }


            public void partHidden( IWorkbenchPartReference ref )
            {
                if ( ref.getPart( false ) == lastActiveEditor && lastActiveEditor != null )
                {
                    updateTitle( true );
                }
            }


            public void partVisible( IWorkbenchPartReference ref )
            {
                if ( ref.getPart( false ) == lastActiveEditor && lastActiveEditor != null )
                {
                    updateTitle( false );
                }
            }


            public void partInputChanged( IWorkbenchPartReference ref )
            {
                // do nothing
            }
        } );

    }


    /**
     * Computes the title.
     * 
     * @return the computed title
     */
    private String computeTitle()
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        IWorkbenchPage currentPage = configurer.getWindow().getActivePage();
        IEditorPart activeEditor = null;
        if ( currentPage != null )
        {
            activeEditor = lastActiveEditor;
        }

        String title = null;
        IProduct product = Platform.getProduct();
        if ( product != null )
        {
            title = product.getName();
        }
        if ( title == null )
        {
            title = ""; //$NON-NLS-1$
        }

        if ( currentPage != null )
        {
            if ( activeEditor != null )
            {
                lastEditorTitle = activeEditor.getTitleToolTip();
                title = NLS.bind( "{0} - {1}", lastEditorTitle, title ); //$NON-NLS-1$ 
            }
            IPerspectiveDescriptor persp = currentPage.getPerspective();
            String label = ""; //$NON-NLS-1$
            if ( persp != null )
            {
                label = persp.getLabel();
            }
            IAdaptable input = currentPage.getInput();
            if ( input != null )
            {
                label = currentPage.getLabel();
            }
            if ( label != null && !label.equals( "" ) ) { //$NON-NLS-1$ 
                title = NLS.bind( "{0} - {1}", label, title ); //$NON-NLS-1$ 
            }
        }

        return title;
    }


    /**
     * Recomputes the title.
     */
    private void recomputeTitle()
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        String oldTitle = configurer.getTitle();
        String newTitle = computeTitle();
        if ( !newTitle.equals( oldTitle ) )
        {
            configurer.setTitle( newTitle );
        }
    }


    /**
     * Updates the window title. Format will be: [pageInput -]
     * [currentPerspective -] [editorInput -] [workspaceLocation -] productName
     * @param editorHidden 
     */
    private void updateTitle( boolean editorHidden )
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        IWorkbenchWindow window = configurer.getWindow();
        IEditorPart activeEditor = null;
        IWorkbenchPage currentPage = window.getActivePage();
        IPerspectiveDescriptor persp = null;
        IAdaptable input = null;

        if ( currentPage != null )
        {
            activeEditor = currentPage.getActiveEditor();
            persp = currentPage.getPerspective();
            input = currentPage.getInput();
        }

        if ( editorHidden )
        {
            activeEditor = null;
        }

        // Nothing to do if the editor hasn't changed
        if ( activeEditor == lastActiveEditor && currentPage == lastActivePage && persp == lastPerspective
            && input == lastInput )
        {
            return;
        }

        if ( lastActiveEditor != null )
        {
            lastActiveEditor.removePropertyListener( editorPropertyListener );
        }

        lastActiveEditor = activeEditor;
        lastActivePage = currentPage;
        lastPerspective = persp;
        lastInput = input;

        if ( activeEditor != null )
        {
            activeEditor.addPropertyListener( editorPropertyListener );
        }

        recomputeTitle();
    }
}
