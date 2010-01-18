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

package org.apache.directory.studio.ldifeditor.editor;


import java.io.File;
import java.util.ResourceBundle;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.ValueEditorPreferencesAction;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.BrowserConnectionWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifeditor.editor.actions.EditLdifAttributeAction;
import org.apache.directory.studio.ldifeditor.editor.actions.EditLdifRecordAction;
import org.apache.directory.studio.ldifeditor.editor.actions.FormatLdifDocumentAction;
import org.apache.directory.studio.ldifeditor.editor.actions.FormatLdifRecordAction;
import org.apache.directory.studio.ldifeditor.editor.actions.OpenBestValueEditorAction;
import org.apache.directory.studio.ldifeditor.editor.actions.OpenDefaultValueEditorAction;
import org.apache.directory.studio.ldifeditor.editor.actions.OpenValueEditorAction;
import org.apache.directory.studio.ldifeditor.editor.text.LdifPartitionScanner;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.utils.ActionUtils;
import org.apache.directory.studio.valueeditors.AbstractDialogValueEditor;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * This class implements the LDIF editor
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifEditor extends TextEditor implements ILdifEditor, ConnectionUpdateListener, IPartListener2
{
    private ViewForm control;

    private BrowserConnectionWidget browserConnectionWidget;

    private ToolBar actionToolBar;

    private IToolBarManager actionToolBarManager;

    private IBrowserConnection browserConnection;

    private ProjectionSupport projectionSupport;

    protected LdifOutlinePage outlinePage;

    private ValueEditorManager valueEditorManager;

    private OpenBestValueEditorAction openBestValueEditorAction;

    private OpenValueEditorAction[] openValueEditorActions;

    private ValueEditorPreferencesAction valueEditorPreferencesAction;

    protected boolean showToolBar = true;


    /**
     * Creates a new instance of LdifEditor.
     */
    public LdifEditor()
    {
        super();

        setSourceViewerConfiguration( new LdifSourceViewerConfiguration( this, true ) );
        setDocumentProvider( new LdifDocumentProvider() );

        IPreferenceStore editorStore = EditorsUI.getPreferenceStore();
        IPreferenceStore browserStore = LdifEditorActivator.getDefault().getPreferenceStore();
        IPreferenceStore combinedStore = new ChainedPreferenceStore( new IPreferenceStore[]
            { browserStore, editorStore } );
        setPreferenceStore( combinedStore );

        setHelpContextId( LdifEditorConstants.PLUGIN_ID + "." + "tools_ldif_editor" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
     */
    protected void handlePreferenceStoreChanged( PropertyChangeEvent event )
    {
        try
        {

            ISourceViewer sourceViewer = getSourceViewer();
            if ( sourceViewer == null )
            {
                return;
            }

            int topIndex = getSourceViewer().getTopIndex();
            getSourceViewer().getDocument().set( getSourceViewer().getDocument().get() );
            getSourceViewer().setTopIndex( topIndex );

        }
        finally
        {
            super.handlePreferenceStoreChanged( event );
        }
    }


    /**
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#collectContextMenuPreferencePages()
     */
    protected String[] collectContextMenuPreferencePages()
    {
        String[] ids = super.collectContextMenuPreferencePages();
        String[] more = new String[ids.length + 4];
        more[0] = LdifEditorConstants.PREFERENCEPAGEID_LDIFEDITOR;
        more[1] = LdifEditorConstants.PREFERENCEPAGEID_LDIFEDITOR_CONTENTASSIST;
        more[2] = LdifEditorConstants.PREFERENCEPAGEID_LDIFEDITOR_SYNTAXCOLORING;
        more[3] = LdifEditorConstants.PREFERENCEPAGEID_LDIFEDITOR_TEMPLATES;
        System.arraycopy( ids, 0, more, 4, ids.length );
        return more;
    }


    /**
     * Gets the ID of the LDIF Editor
     *
     * @return
     *      the ID of the LDIF Editor
     */
    public static String getId()
    {
        return LdifEditorConstants.EDITOR_LDIF_EDITOR;
    }


    /**
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        if ( input instanceof IPathEditorInput )
        {
            IPathEditorInput pei = ( IPathEditorInput ) input;
            IPath path = pei.getPath();
            File javaIoFile = path.toFile();
            long fileLength = javaIoFile.length();
            if ( fileLength > ( 1 * 1024 * 1024 ) )
            {
                MessageDialog.openError( site.getShell(), Messages.getString( "LdifEditor.LDIFFileIsTooBig" ), //$NON-NLS-1$
                    Messages.getString( "LdifEditor.LDIFFileIsTooBigDescription" ) ); //$NON-NLS-1$
                super.init( site, new NonExistingLdifEditorInput() );
                return;
            }
        }

        super.init( site, input );

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );
        getSite().getPage().addPartListener( this );

        this.valueEditorManager = new ValueEditorManager( getSite().getShell(), false, false );
    }


    /**
     * @see org.eclipse.ui.editors.text.TextEditor#dispose()
     */
    public void dispose()
    {
        valueEditorManager.dispose();

        deactivateGlobalActionHandlers();

        ConnectionEventRegistry.removeConnectionUpdateListener( this );
        getSite().getPage().removePartListener( this );

        super.dispose();
    }


    /**
     * @see org.eclipse.ui.editors.text.TextEditor#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class required )
    {
        if ( IShowInTargetList.class.equals( required ) )
        {
            return new IShowInTargetList()
            {
                public String[] getShowInTargetIds()
                {
                    return new String[]
                        { IPageLayout.ID_RES_NAV };
                }
            };
        }
        if ( IContentOutlinePage.class.equals( required ) )
        {
            if ( outlinePage == null || outlinePage.getControl() == null || outlinePage.getControl().isDisposed() )
            {
                outlinePage = new LdifOutlinePage( this );
            }
            return outlinePage;
        }
        if ( ISourceViewer.class.equals( required ) )
        {
            return getSourceViewer();
        }
        if ( IAnnotationHover.class.equals( required ) )
        {
            if ( getSourceViewerConfiguration() != null && getSourceViewer() != null )
                return getSourceViewerConfiguration().getAnnotationHover( getSourceViewer() );
        }
        if ( ITextHover.class.equals( required ) )
        {
            if ( getSourceViewerConfiguration() != null && getSourceViewer() != null )
                return getSourceViewerConfiguration().getTextHover( getSourceViewer(), null );
        }
        if ( IContentAssistProcessor.class.equals( required ) )
        {
            if ( getSourceViewerConfiguration() != null && getSourceViewer() != null )
                return getSourceViewerConfiguration().getContentAssistant( getSourceViewer() )
                    .getContentAssistProcessor( LdifPartitionScanner.LDIF_RECORD );
        }
        if ( projectionSupport != null )
        {
            Object adapter = projectionSupport.getAdapter( getSourceViewer(), required );
            if ( adapter != null )
                return adapter;
        }
        return super.getAdapter( required );
    }


    /**
     * @see org.eclipse.ui.editors.text.TextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    protected void editorContextMenuAboutToShow( IMenuManager menu )
    {
        super.editorContextMenuAboutToShow( menu );

        IContributionItem[] items = menu.getItems();
        for ( int i = 0; i < items.length; i++ )
        {
            if ( items[i] instanceof ActionContributionItem )
            {
                ActionContributionItem aci = ( ActionContributionItem ) items[i];
                if ( aci.getAction() == getAction( ITextEditorActionConstants.SHIFT_LEFT ) )
                {
                    menu.remove( items[i] );
                }
                if ( aci.getAction() == getAction( ITextEditorActionConstants.SHIFT_RIGHT ) )
                {
                    menu.remove( items[i] );
                }
            }
        }

        // add Edit actions
        addAction( menu, ITextEditorActionConstants.GROUP_EDIT,
            LdifEditorConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION );
        addAction( menu, ITextEditorActionConstants.GROUP_EDIT, BrowserCommonConstants.ACTION_ID_EDIT_VALUE );

        MenuManager valueEditorMenuManager = new MenuManager( Messages.getString( "LdifEditor.EditValueWith" ) ); //$NON-NLS-1$
        if ( this.openBestValueEditorAction.isEnabled() )
        {
            valueEditorMenuManager.add( this.openBestValueEditorAction );
            valueEditorMenuManager.add( new Separator() );
        }
        for ( int i = 0; i < this.openValueEditorActions.length; i++ )
        {
            this.openValueEditorActions[i].update();
            if ( this.openValueEditorActions[i].isEnabled()
                && this.openValueEditorActions[i].getValueEditor().getClass() != this.openBestValueEditorAction
                    .getValueEditor().getClass()
                && this.openValueEditorActions[i].getValueEditor() instanceof AbstractDialogValueEditor )
            {
                valueEditorMenuManager.add( this.openValueEditorActions[i] );
            }
        }
        valueEditorMenuManager.add( new Separator() );
        valueEditorMenuManager.add( this.valueEditorPreferencesAction );
        menu.appendToGroup( ITextEditorActionConstants.GROUP_EDIT, valueEditorMenuManager );

        addAction( menu, ITextEditorActionConstants.GROUP_EDIT, LdifEditorConstants.ACTION_ID_EDIT_RECORD );

        // add Format actions
        MenuManager formatMenuManager = new MenuManager( Messages.getString( "LdifEditor.Format" ) ); //$NON-NLS-1$
        addAction( formatMenuManager, LdifEditorConstants.ACTION_ID_FORMAT_LDIF_DOCUMENT );
        addAction( formatMenuManager, LdifEditorConstants.ACTION_ID_FORMAT_LDIF_RECORD );
        menu.appendToGroup( ITextEditorActionConstants.GROUP_EDIT, formatMenuManager );
    }


    /**
     * @see org.eclipse.ui.editors.text.TextEditor#createActions()
     */
    protected void createActions()
    {
        super.createActions();

        // add content assistant
        ResourceBundle bundle = LdifEditorActivator.getDefault().getResourceBundle();
        IAction action = new ContentAssistAction( bundle, "ldifeditor__contentassistproposal_", this ); //$NON-NLS-1$
        action.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS );
        setAction( "ContentAssistProposal", action ); //$NON-NLS-1$

        // add execute action (for tool bar)
        if ( actionToolBarManager != null )
        {
            ExecuteLdifAction executeLdifAction = new ExecuteLdifAction( this );
            actionToolBarManager.add( executeLdifAction );
            setAction( LdifEditorConstants.ACTION_ID_EXECUTE_LDIF, executeLdifAction );
            actionToolBarManager.update( true );
        }

        // add context menu edit actions
        EditLdifAttributeAction editLdifAttributeAction = new EditLdifAttributeAction( this );
        setAction( BrowserCommonConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION, editLdifAttributeAction );

        openBestValueEditorAction = new OpenBestValueEditorAction( this );
        IValueEditor[] valueEditors = valueEditorManager.getAllValueEditors();
        openValueEditorActions = new OpenValueEditorAction[valueEditors.length];
        for ( int i = 0; i < this.openValueEditorActions.length; i++ )
        {
            openValueEditorActions[i] = new OpenValueEditorAction( this, valueEditors[i] );
        }
        valueEditorPreferencesAction = new ValueEditorPreferencesAction();

        OpenDefaultValueEditorAction openDefaultValueEditorAction = new OpenDefaultValueEditorAction( this,
            openBestValueEditorAction );
        setAction( BrowserCommonConstants.ACTION_ID_EDIT_VALUE, openDefaultValueEditorAction );

        EditLdifRecordAction editRecordAction = new EditLdifRecordAction( this );
        setAction( LdifEditorConstants.ACTION_ID_EDIT_RECORD, editRecordAction );

        // add context menu format actions
        FormatLdifDocumentAction formatDocumentAction = new FormatLdifDocumentAction( this );
        setAction( LdifEditorConstants.ACTION_ID_FORMAT_LDIF_DOCUMENT, formatDocumentAction );
        FormatLdifRecordAction formatRecordAction = new FormatLdifRecordAction( this );
        setAction( LdifEditorConstants.ACTION_ID_FORMAT_LDIF_RECORD, formatRecordAction );

        // update cut, copy, paste
        IAction cutAction = getAction( ITextEditorActionConstants.CUT );
        if ( cutAction != null )
        {
            cutAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_CUT ) );
        }
        IAction copyAction = getAction( ITextEditorActionConstants.COPY );
        if ( copyAction != null )
        {
            copyAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_COPY ) );
        }
        IAction pasteAction = getAction( ITextEditorActionConstants.PASTE );
        if ( pasteAction != null )
        {
            pasteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_PASTE ) );
        }

        activateGlobalActionHandlers();
    }


    /**
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        setHelpContextId( LdifEditorConstants.PLUGIN_ID + "." + "tools_ldif_editor" ); //$NON-NLS-1$ //$NON-NLS-2$

        if ( showToolBar )
        {
            // create the toolbar (including connection widget and execute button) on top of the editor 
            Composite composite = new Composite( parent, SWT.NONE );
            composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            layout.verticalSpacing = 0;
            composite.setLayout( layout );

            control = new ViewForm( composite, SWT.NONE );
            control.setLayoutData( new GridData( GridData.FILL_BOTH ) );

            Composite browserConnectionWidgetControl = BaseWidgetUtils.createColumnContainer( control, 2, 1 );
            browserConnectionWidget = new BrowserConnectionWidget();
            browserConnectionWidget.createWidget( browserConnectionWidgetControl );
            connectionUpdated( null );
            browserConnectionWidget.addWidgetModifyListener( new WidgetModifyListener()
            {
                public void widgetModified( WidgetModifyEvent event )
                {
                    IBrowserConnection browserConnection = browserConnectionWidget.getBrowserConnection();
                    setConnection( browserConnection );
                }
            } );
            control.setTopLeft( browserConnectionWidgetControl );

            // tool bar
            actionToolBar = new ToolBar( control, SWT.FLAT | SWT.RIGHT );
            actionToolBar.setLayoutData( new GridData( SWT.END, SWT.NONE, true, false ) );
            actionToolBarManager = new ToolBarManager( actionToolBar );
            control.setTopCenter( actionToolBar );

            // local menu
            control.setTopRight( null );

            // content
            Composite editorComposite = new Composite( control, SWT.NONE );
            editorComposite.setLayout( new FillLayout() );
            GridData data = new GridData( GridData.FILL_BOTH );
            data.widthHint = 450;
            data.heightHint = 250;
            editorComposite.setLayoutData( data );
            super.createPartControl( editorComposite );
            control.setContent( editorComposite );
        }
        else
        {
            super.createPartControl( parent );
        }

        ProjectionViewer projectionViewer = ( ProjectionViewer ) getSourceViewer();
        projectionSupport = new ProjectionSupport( projectionViewer, getAnnotationAccess(), getSharedColors() );
        projectionSupport.install();
        projectionViewer.doOperation( ProjectionViewer.TOGGLE );
    }


    /**
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    protected ISourceViewer createSourceViewer( Composite parent, IVerticalRuler ruler, int styles )
    {
        getAnnotationAccess();
        getOverviewRuler();
        ISourceViewer viewer = new ProjectionViewer( parent, ruler, getOverviewRuler(), true, styles );
        getSourceViewerDecorationSupport( viewer );

        return viewer;
    }


    /**
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#configureSourceViewerDecorationSupport(org.eclipse.ui.texteditor.SourceViewerDecorationSupport)
     */
    protected void configureSourceViewerDecorationSupport( SourceViewerDecorationSupport support )
    {
        super.configureSourceViewerDecorationSupport( support );
    }


    /**
     * @see org.apache.directory.studio.ldifeditor.editor.ILdifEditor#getLdifModel()
     */
    public LdifFile getLdifModel()
    {
        IDocumentProvider provider = getDocumentProvider();
        if ( provider instanceof LdifDocumentProvider )
        {
            return ( ( LdifDocumentProvider ) provider ).getLdifModel();
        }
        else
        {
            return null;
        }
    }


    /**
     * This method is used to notify the LDIF Editor that the Outline Page has been closed.
     */
    public void outlinePageClosed()
    {
        projectionSupport.dispose();
        outlinePage = null;
    }


    /**
     * @see org.apache.directory.studio.ldifeditor.editor.ILdifEditor#getConnection()
     */
    public IBrowserConnection getConnection()
    {
        return browserConnection;
    }


    /**
     * Sets the Connection
     *
     * @param browserConnection
     *      the browser connection to set
     */
    protected void setConnection( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
        getEditorSite().getActionBars().getStatusLineManager().setMessage(
            Messages.getString( "LdifEditor.UsedConnection" ) //$NON-NLS-1$
                + ( browserConnection == null || browserConnection.getConnection() == null ? "-" : browserConnection //$NON-NLS-1$
                    .getConnection().getName() ) );
        // getStatusField("ldapconnection").setText();

        IAction action = getAction( LdifEditorConstants.ACTION_ID_EXECUTE_LDIF );
        if ( action != null )
        {
            action.setEnabled( browserConnection == null );
            action.setEnabled( browserConnection != null );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public final void connectionUpdated( Connection connection )
    {
        if ( browserConnectionWidget != null )
        {
            IBrowserConnection browserConnection = browserConnectionWidget.getBrowserConnection();
            setConnection( browserConnection );
            browserConnectionWidget.setBrowserConnection( browserConnection );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderAdded(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderRemoved(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder )
    {
    }


    /**
     * This implementation checks if the input is of type
     * NonExistingLdifEditorInput. In that case doSaveAs() is
     * called to prompt for a new file name and location.
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor progressMonitor )
    {
        final IEditorInput input = getEditorInput();
        if ( input instanceof NonExistingLdifEditorInput )
        {
            super.doSaveAs();
            return;
        }

        super.doSave( progressMonitor );
    }


    /**
     * The input could be one of the following types:
     * - NonExistingLdifEditorInput: New file, not yet saved
     * - PathEditorInput: file opened with our internal "Open File.." action
     * - FileEditorInput: file is within workspace
     * - JavaFileEditorInput: file opend with "Open File..." action from org.eclipse.ui.editor
     *
     * In RCP the FileDialog appears.
     * In IDE the super implementation is called.
     * To detect if this plugin runs in IDE the org.eclipse.ui.ide extension point is checked.
     *
     * @see org.eclipse.ui.editors.text.TextEditor#performSaveAs(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void performSaveAs( IProgressMonitor progressMonitor )
    {
        // detect IDE or RCP:
        // check if perspective org.eclipse.ui.resourcePerspective is available
        boolean isIDE = BrowserCommonActivator.isIDEEnvironment();

        if ( isIDE )
        {
            // Just call super implementation for now
            IPreferenceStore store = EditorsUI.getPreferenceStore();
            String key = getEditorSite().getId() + ".internal.delegateSaveAs"; // $NON-NLS-1$ //$NON-NLS-1$
            store.setValue( key, true );
            super.performSaveAs( progressMonitor );
        }
        else
        {
            // Open FileDialog
            Shell shell = getSite().getShell();
            final IEditorInput input = getEditorInput();

            IDocumentProvider provider = getDocumentProvider();
            final IEditorInput newInput;

            FileDialog dialog = new FileDialog( shell, SWT.SAVE );

            String path = dialog.open();
            if ( path == null )
            {
                if ( progressMonitor != null )
                {
                    progressMonitor.setCanceled( true );
                }
                return;
            }

            // Check whether file exists and if so, confirm overwrite
            final File externalFile = new File( path );
            if ( externalFile.exists() )
            {
                MessageDialog overwriteDialog = new MessageDialog( shell,
                    Messages.getString( "LdifEditor.Overwrite" ), null, Messages.getString( "OverwriteQuestion" ), //$NON-NLS-1$ //$NON-NLS-2$
                    MessageDialog.WARNING, new String[]
                        { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 1 ); // 'No' is the default
                if ( overwriteDialog.open() != Window.OK )
                {
                    if ( progressMonitor != null )
                    {
                        progressMonitor.setCanceled( true );
                        return;
                    }
                }
            }

            IPath iPath = new Path( path );
            newInput = new PathEditorInput( iPath );

            boolean success = false;
            try
            {
                provider.aboutToChange( newInput );
                provider.saveDocument( progressMonitor, newInput, provider.getDocument( input ), true );
                success = true;
            }
            catch ( CoreException x )
            {
                final IStatus status = x.getStatus();
                if ( status == null || status.getSeverity() != IStatus.CANCEL )
                {
                    String title = Messages.getString( "LdifEditor.ErrorInSaveAs" ); //$NON-NLS-1$
                    String msg = Messages.getString( "LdifEditor.ErrorInSaveAs" ) + x.getMessage(); //$NON-NLS-1$
                    MessageDialog.openError( shell, title, msg );
                }
            }
            finally
            {
                provider.changed( newInput );
                if ( success )
                {
                    setInput( newInput );
                }
            }

            if ( progressMonitor != null )
            {
                progressMonitor.setCanceled( !success );
            }
        }

    }

    private IContextActivation contextActivation;


    /**
     * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partDeactivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this && contextActivation != null )
        {
            deactivateGlobalActionHandlers();

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextService.deactivateContext( contextActivation );
            contextActivation = null;
        }
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partActivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this )
        {
            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextActivation = contextService.activateContext( BrowserCommonConstants.CONTEXT_WINDOWS );

            activateGlobalActionHandlers();
        }
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partBroughtToTop( IWorkbenchPartReference partRef )
    {
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partClosed( IWorkbenchPartReference partRef )
    {
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partHidden( IWorkbenchPartReference partRef )
    {
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partInputChanged( IWorkbenchPartReference partRef )
    {
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partOpened( IWorkbenchPartReference partRef )
    {
    }


    /**
     * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partVisible( IWorkbenchPartReference partRef )
    {
    }


    /**
     * Activates global action handlers
     */
    public void activateGlobalActionHandlers()
    {
        IAction elaa = getAction( BrowserCommonConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION );
        ActionUtils.activateActionHandler( elaa );
        IAction elva = getAction( BrowserCommonConstants.ACTION_ID_EDIT_VALUE );
        ActionUtils.activateActionHandler( elva );
        IAction elra = getAction( LdifEditorConstants.ACTION_ID_EDIT_RECORD );
        ActionUtils.activateActionHandler( elra );
    }


    /**
     * Deactivates global action handlers
     */
    public void deactivateGlobalActionHandlers()
    {
        IAction elaa = getAction( BrowserCommonConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION );
        ActionUtils.deactivateActionHandler( elaa );
        IAction elva = getAction( BrowserCommonConstants.ACTION_ID_EDIT_VALUE );
        ActionUtils.deactivateActionHandler( elva );
        IAction elra = getAction( LdifEditorConstants.ACTION_ID_EDIT_RECORD );
        ActionUtils.deactivateActionHandler( elra );
    }


    /**
     * Gets the Value Editor Manager
     *
     * @return
     *      the Value Editor Manager
     */
    public ValueEditorManager getValueEditorManager()
    {
        return valueEditorManager;
    }

}
