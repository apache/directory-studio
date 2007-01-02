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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif;


import java.io.File;
import java.util.ResourceBundle;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.ExecuteLdifAction;
import org.apache.directory.ldapstudio.browser.ui.actions.ValueEditorPreferencesAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.EditLdifAttributeAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.EditLdifRecordAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.FormatLdifDocumentAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.FormatLdifRecordAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.OpenBestValueEditorAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.OpenDefaultValueEditorAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions.OpenValueEditorAction;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.text.LdifPartitionScanner;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
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
import org.eclipse.jface.commands.ActionHandler;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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
import org.eclipse.ui.commands.ICommandService;
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


public class LdifEditor extends TextEditor implements ILdifEditor, ConnectionUpdateListener, IPartListener2
{

    protected ViewForm control;

    protected Combo connectionCombo;

    protected ToolBar actionToolBar;

    protected IToolBarManager actionToolBarManager;

    private IConnection connection;

    private ProjectionSupport projectionSupport;

    private LdifOutlinePage outlinePage;

    private ValueEditorManager valueEditorManager;

    private OpenBestValueEditorAction openBestValueEditorAction;

    private OpenValueEditorAction[] openValueEditorActions;

    private ValueEditorPreferencesAction valueEditorPreferencesAction;


    public LdifEditor()
    {
        super();

        setSourceViewerConfiguration( new LdifSourceViewerConfiguration( this, true ) );
        setDocumentProvider( new LdifDocumentProvider() );

        IPreferenceStore editorStore = EditorsUI.getPreferenceStore();
        IPreferenceStore browserStore = BrowserUIPlugin.getDefault().getPreferenceStore();
        IPreferenceStore combinedStore = new ChainedPreferenceStore( new IPreferenceStore[]
            { browserStore, editorStore } );
        setPreferenceStore( combinedStore );

        setHelpContextId( BrowserUIPlugin.PLUGIN_ID + "." + "tools_ldif_editor" );
    }


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


    protected String[] collectContextMenuPreferencePages()
    {
        String[] ids = super.collectContextMenuPreferencePages();
        String[] more = new String[ids.length + 4];
        more[0] = BrowserUIConstants.PREFERENCEPAGEID_LDIFEDITOR;
        more[1] = BrowserUIConstants.PREFERENCEPAGEID_LDIFEDITOR_CONTENTASSIST;
        more[2] = BrowserUIConstants.PREFERENCEPAGEID_LDIFEDITOR_SYNTAXCOLORING;
        more[3] = BrowserUIConstants.PREFERENCEPAGEID_LDIFEDITOR_TEMPLATES;
        System.arraycopy( ids, 0, more, 4, ids.length );
        return more;
    }


    public static String getId()
    {
        return LdifEditor.class.getName();
    }


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
                MessageDialog.openError( site.getShell(), "LDIF file is too big",
                    "The selected LDIF file is too big. Perhaps it is possilbe to open it in a further version..." );
                super.init( site, new NonExistingLdifEditorInput() );
                return;
            }
        }

        super.init( site, input );

        EventRegistry.addConnectionUpdateListener( this );
        getSite().getPage().addPartListener( this );

        this.valueEditorManager = new ValueEditorManager( getSite().getShell() );
    }


    public void dispose()
    {

        this.valueEditorManager.dispose();

        deactivateGlobalActionHandlers();

        EventRegistry.removeConnectionUpdateListener( this );
        getSite().getPage().removePartListener( this );

        super.dispose();
    }


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
        addAction( menu, ITextEditorActionConstants.GROUP_EDIT, EditLdifAttributeAction.class.getName() );
        addAction( menu, ITextEditorActionConstants.GROUP_EDIT, OpenDefaultValueEditorAction.class.getName() );

        MenuManager valueEditorMenuManager = new MenuManager( "Edit Value With" );
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

        addAction( menu, ITextEditorActionConstants.GROUP_EDIT, EditLdifRecordAction.class.getName() );

        // add Format actions
        MenuManager formatMenuManager = new MenuManager( "Format" );
        addAction( formatMenuManager, FormatLdifDocumentAction.class.getName() );
        addAction( formatMenuManager, FormatLdifRecordAction.class.getName() );
        menu.appendToGroup( ITextEditorActionConstants.GROUP_EDIT, formatMenuManager );

    }


    protected void createActions()
    {
        super.createActions();

        // add content assistant
        ResourceBundle bundle = BrowserUIPlugin.getDefault().getResourceBundle();
        IAction action = new ContentAssistAction( bundle, "ldifeditor__contentassistproposal_", this ); //$NON-NLS-1$
        action.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS );
        setAction( "ContentAssistProposal", action ); //$NON-NLS-1$

        // add execute action (for tool bar)
        ExecuteLdifAction executeLdifAction = new ExecuteLdifAction( this );
        this.actionToolBarManager.add( executeLdifAction );
        setAction( ExecuteLdifAction.class.getName(), executeLdifAction );
        this.actionToolBarManager.update( true );

        // add context menu edit actions
        EditLdifAttributeAction editLdifAttributeAction = new EditLdifAttributeAction( this );
        setAction( EditLdifAttributeAction.class.getName(), editLdifAttributeAction );

        this.openBestValueEditorAction = new OpenBestValueEditorAction( this );
        IValueEditor[] valueEditors = valueEditorManager.getAllValueEditors();
        this.openValueEditorActions = new OpenValueEditorAction[valueEditors.length];
        for ( int i = 0; i < this.openValueEditorActions.length; i++ )
        {
            this.openValueEditorActions[i] = new OpenValueEditorAction( this, valueEditors[i] );
        }
        this.valueEditorPreferencesAction = new ValueEditorPreferencesAction();

        OpenDefaultValueEditorAction openDefaultValueEditorAction = new OpenDefaultValueEditorAction( this,
            openBestValueEditorAction );
        setAction( OpenDefaultValueEditorAction.class.getName(), openDefaultValueEditorAction );

        EditLdifRecordAction editRecordAction = new EditLdifRecordAction( this );
        setAction( EditLdifRecordAction.class.getName(), editRecordAction );

        // add context menu format actions
        FormatLdifDocumentAction formatDocumentAction = new FormatLdifDocumentAction( this );
        setAction( FormatLdifDocumentAction.class.getName(), formatDocumentAction );
        FormatLdifRecordAction formatRecordAction = new FormatLdifRecordAction( this );
        setAction( FormatLdifRecordAction.class.getName(), formatRecordAction );

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


    public void createPartControl( Composite parent )
    {

        setHelpContextId( BrowserUIPlugin.PLUGIN_ID + "." + "tools_ldif_editor" );

        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        // layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        composite.setLayout( layout );

        control = new ViewForm( composite, SWT.NONE );
        control.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // infoText = BaseWidgetUtils.createLabeledText(control, "", 1);
        Composite connectionComboControl = BaseWidgetUtils.createColumnContainer( control, 1, 1 );
        // connectionComboControl.setLayoutData(new
        // GridData(GridData.GRAB_HORIZONTAL));
        connectionCombo = BaseWidgetUtils.createReadonlyCombo( connectionComboControl, new String[0], 0, 1 );
        connectionCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true ) );
        connectionUpdated( new ConnectionUpdateEvent( null, 0 ) );
        connectionCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                IConnection connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection(
                    connectionCombo.getText() );
                setConnection( connection );
                IAction action = getAction( ExecuteLdifAction.class.getName() );
                if ( action != null )
                {
                    action.setEnabled( connection == null );
                    action.setEnabled( connection != null );
                    // actionToolBarManager.update(true);
                }
            }
        } );
        control.setTopLeft( connectionComboControl );

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

        ProjectionViewer projectionViewer = ( ProjectionViewer ) getSourceViewer();
        projectionSupport = new ProjectionSupport( projectionViewer, getAnnotationAccess(), getSharedColors() );
        projectionSupport.install();
        projectionViewer.doOperation( ProjectionViewer.TOGGLE );

    }


    protected ISourceViewer createSourceViewer( Composite parent, IVerticalRuler ruler, int styles )
    {
        getAnnotationAccess();
        getOverviewRuler();
        ISourceViewer viewer = new ProjectionViewer( parent, ruler, getOverviewRuler(), true, styles );
        getSourceViewerDecorationSupport( viewer );

        return viewer;
    }


    protected void configureSourceViewerDecorationSupport( SourceViewerDecorationSupport support )
    {
        super.configureSourceViewerDecorationSupport( support );
    }


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


    public void outlinePageClosed()
    {
        projectionSupport.dispose();
        outlinePage = null;
    }


    public IConnection getConnection()
    {
        return this.connection;
    }


    private void setConnection( IConnection connection )
    {
        this.connection = connection;
        getEditorSite().getActionBars().getStatusLineManager().setMessage(
            "Used Connection: " + ( this.connection == null ? "-" : this.connection.getName() ) );
        // getStatusField("ldapconnection").setText();
    }


    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        IConnection[] connections = BrowserCorePlugin.getDefault().getConnectionManager().getConnections();
        String[] names = new String[connections.length + 1];
        names[0] = "";
        for ( int i = 0; i < connections.length; i++ )
        {
            names[i + 1] = connections[i].getName();
        }
        String old = this.connectionCombo.getText();
        this.connectionCombo.setItems( names );
        this.connectionCombo.setText( old );
        connectionCombo.setVisibleItemCount( Math.max( names.length, 20 ) );

    }


    /**
     * This implementation checks if the input is of type
     * NonExistingLdifEditorInput. In that case doSaveAs() is 
     * called to prompt for a new file name and location.
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
     */
    protected void performSaveAs( IProgressMonitor progressMonitor )
    {
        // detect IDE or RCP: 
        // check if perspective org.eclipse.ui.resourcePerspective is available
        boolean isIDE = BrowserUIPlugin.isIDEEnvironment();

        if ( isIDE )
        {
            // Just call super implementation for now
            IPreferenceStore store = EditorsUI.getPreferenceStore();
            String key = getEditorSite().getId() + ".internal.delegateSaveAs"; // $NON-NLS-1$
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
                MessageDialog overwriteDialog = new MessageDialog( shell, "Overwrite", null, "Overwrite?",
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
                    String title = "Error in Save As...";
                    String msg = "Error in Save As... " + x.getMessage();
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


    public void partDeactivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this && contextActivation != null )
        {

            this.deactivateGlobalActionHandlers();

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextService.deactivateContext( contextActivation );
            contextActivation = null;
        }
    }


    public void partActivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this )
        {

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextActivation = contextService
                .activateContext( "org.apache.directory.ldapstudio.browser.action.context" );

            this.activateGlobalActionHandlers();
        }
    }


    public void partBroughtToTop( IWorkbenchPartReference partRef )
    {
    }


    public void partClosed( IWorkbenchPartReference partRef )
    {
    }


    public void partHidden( IWorkbenchPartReference partRef )
    {
    }


    public void partInputChanged( IWorkbenchPartReference partRef )
    {
    }


    public void partOpened( IWorkbenchPartReference partRef )
    {
    }


    public void partVisible( IWorkbenchPartReference partRef )
    {
    }


    public void activateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction elaa = getAction( EditLdifAttributeAction.class.getName() );
            commandService.getCommand( elaa.getActionDefinitionId() ).setHandler( new ActionHandler( elaa ) );
            IAction elva = getAction( OpenDefaultValueEditorAction.class.getName() );
            commandService.getCommand( elva.getActionDefinitionId() ).setHandler( new ActionHandler( elva ) );
            IAction elra = getAction( EditLdifRecordAction.class.getName() );
            commandService.getCommand( elra.getActionDefinitionId() ).setHandler( new ActionHandler( elra ) );
        }
    }


    public void deactivateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction elaa = getAction( EditLdifAttributeAction.class.getName() );
            commandService.getCommand( elaa.getActionDefinitionId() ).setHandler( null );
            IAction elva = getAction( OpenDefaultValueEditorAction.class.getName() );
            commandService.getCommand( elva.getActionDefinitionId() ).setHandler( null );
            IAction elra = getAction( EditLdifRecordAction.class.getName() );
            commandService.getCommand( elra.getActionDefinitionId() ).setHandler( null );
        }
    }


    public ValueEditorManager getValueEditorManager()
    {
        return valueEditorManager;
    }

}
