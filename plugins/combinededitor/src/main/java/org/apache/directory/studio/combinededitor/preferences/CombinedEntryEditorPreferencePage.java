package org.apache.directory.studio.combinededitor.preferences;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.apache.directory.studio.combinededitor.CombinedEditorPlugin;
import org.apache.directory.studio.combinededitor.CombinedEditorPluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;


/**
 * This class implements the Combined Entry Editor preference page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CombinedEntryEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** The preferences store */
    private IPreferenceStore store;

    // UI Fields
    private Button defaultEditorTemplateRadioButton;
    private Button autoSwitchToOtherEditorCheckbox;
    private Button autoSwitchToTableEditorRadioButton;
    private Button autoSwitchToLDIFEditorRadioButton;
    private Label autoSwitchLabel;
    private Button defaultEditorTableRadioButton;
    private Button defaultEditorLDIFRadioButton;


    /**
     * Creates a new instance of CombinedEntryEditorPreferencePage.
     */
    public CombinedEntryEditorPreferencePage()
    {
        super();
        super.setPreferenceStore( EntryTemplatePlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "CombinedEntryEditorPreferencePage.PrefPageDescription" ) ); //$NON-NLS-1$

        store = CombinedEditorPlugin.getDefault().getPreferenceStore();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        createUI( composite );
        initListeners();
        initUI();

        return composite;
    }


    /**
     * Creates the user interface.
     *
     * @param parent
     *      the parent composite
     */
    private void createUI( Composite parent )
    {
        // Main Composite
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Default Editor Group
        Group defaultEditorGroup = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "CombinedEntryEditorPreferencePage.UseAsDefaultEditor" ), 1 ); //$NON-NLS-1$
        defaultEditorGroup.setLayout( new GridLayout( 4, false ) );

        // Template Editor Radio Button
        defaultEditorTemplateRadioButton = BaseWidgetUtils.createRadiobutton( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.TemplateEditor" ), 1 ); //$NON-NLS-1$
        defaultEditorTemplateRadioButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );

        // Indent
        BaseWidgetUtils.createRadioIndent( defaultEditorGroup, 1 );

        // Auto Switch Checkbox
        autoSwitchToOtherEditorCheckbox = BaseWidgetUtils.createCheckbox( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.AutoSwitchToFollowingTabNoTemplateAvailable" ), 1 ); //$NON-NLS-1$
        autoSwitchToOtherEditorCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Indent
        BaseWidgetUtils.createRadioIndent( defaultEditorGroup, 1 );

        // Indent
        BaseWidgetUtils.createRadioIndent( defaultEditorGroup, 1 );

        // Table Editor Radio Button
        autoSwitchToTableEditorRadioButton = BaseWidgetUtils.createRadiobutton( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.TableEditor" ), 1 ); //$NON-NLS-1$

        // Table Editor Radio Button
        autoSwitchToLDIFEditorRadioButton = BaseWidgetUtils.createRadiobutton( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.LDIFEditor" ), 1 ); //$NON-NLS-1$

        // Indent
        BaseWidgetUtils.createRadioIndent( defaultEditorGroup, 1 );

        // Auto Switch Label
        autoSwitchLabel = BaseWidgetUtils.createLabel( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.AutoSwitchNote" ), //$NON-NLS-1$
            1 );
        autoSwitchLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Table Editor Radio Button
        defaultEditorTableRadioButton = BaseWidgetUtils.createRadiobutton( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.TableEditor" ), 1 ); //$NON-NLS-1$
        defaultEditorTableRadioButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );

        // LDIF Editor Radio Button
        defaultEditorLDIFRadioButton = BaseWidgetUtils.createRadiobutton( defaultEditorGroup, Messages
            .getString( "CombinedEntryEditorPreferencePage.LDIFEditor" ), 1 ); //$NON-NLS-1$
        defaultEditorLDIFRadioButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 1 ) );
    }


    /**
     * Initializes the listeners
     */
    private void initListeners()
    {
        defaultEditorTemplateRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                defaultEditorTemplateAction();
            }
        } );

        autoSwitchToOtherEditorCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                autoSwitchToOtherEditorAction();
            }
        } );

        autoSwitchToTableEditorRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                autoSwitchToTableEditorAction();
            }
        } );

        autoSwitchToLDIFEditorRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                autoSwitchToLDIFEditorAction();
            }
        } );

        defaultEditorTableRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                defaultEditorTableAction();
            }
        } );

        defaultEditorLDIFRadioButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                defaultEditorLDIFAction();
            }
        } );
    }


    /**
     * This method is called when the 'Default Editor Template Radio Button' is clicked.
     */
    private void defaultEditorTemplateAction()
    {
        defaultEditorTemplateRadioButton.setSelection( true );
        defaultEditorTableRadioButton.setSelection( false );
        defaultEditorLDIFRadioButton.setSelection( false );

        autoSwitchToOtherEditorCheckbox.setEnabled( true );
        autoSwitchToTableEditorRadioButton.setEnabled( autoSwitchToOtherEditorCheckbox.getSelection() );
        autoSwitchToLDIFEditorRadioButton.setEnabled( autoSwitchToOtherEditorCheckbox.getSelection() );
        autoSwitchLabel.setEnabled( true );
    }


    /**
     * This method is called when the 'Auto Switch To Other Editor Checkbox' is clicked.
     */
    private void autoSwitchToOtherEditorAction()
    {
        autoSwitchToTableEditorRadioButton.setEnabled( autoSwitchToOtherEditorCheckbox.getSelection() );
        autoSwitchToLDIFEditorRadioButton.setEnabled( autoSwitchToOtherEditorCheckbox.getSelection() );
        autoSwitchLabel.setEnabled( autoSwitchToOtherEditorCheckbox.getSelection() );
    }


    /**
     * This method is called when the 'Auto Switch To Table Editor Radio Button' is clicked.
     */
    private void autoSwitchToTableEditorAction()
    {
        autoSwitchToTableEditorRadioButton.setSelection( true );
        autoSwitchToLDIFEditorRadioButton.setSelection( false );
    }


    /**
     * This method is called when the 'Auto Switch To LDIF Editor Radio Button' is clicked.
     */
    private void autoSwitchToLDIFEditorAction()
    {
        autoSwitchToTableEditorRadioButton.setSelection( false );
        autoSwitchToLDIFEditorRadioButton.setSelection( true );
    }


    /**
     * This method is called when the 'Default Editor Table Radio Button' is clicked.
     */
    private void defaultEditorTableAction()
    {
        defaultEditorTemplateRadioButton.setSelection( false );
        defaultEditorTableRadioButton.setSelection( true );
        defaultEditorLDIFRadioButton.setSelection( false );

        autoSwitchToOtherEditorCheckbox.setEnabled( false );
        autoSwitchToTableEditorRadioButton.setEnabled( false );
        autoSwitchToLDIFEditorRadioButton.setEnabled( false );
        autoSwitchLabel.setEnabled( false );
    }


    /**
     * This method is called when the 'Default Editor LDIF Radio Button' is clicked.
     */
    private void defaultEditorLDIFAction()
    {
        defaultEditorTemplateRadioButton.setSelection( false );
        defaultEditorTableRadioButton.setSelection( false );
        defaultEditorLDIFRadioButton.setSelection( true );

        autoSwitchToOtherEditorCheckbox.setEnabled( false );
        autoSwitchToTableEditorRadioButton.setEnabled( false );
        autoSwitchToLDIFEditorRadioButton.setEnabled( false );
        autoSwitchLabel.setEnabled( false );
    }


    /**
     * Initializes the User Interface.
     */
    private void initUI()
    {
        initUI( store.getInt( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR ), store
            .getBoolean( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR ), store
            .getInt( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        initUI( store.getDefaultInt( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR ), store
            .getDefaultBoolean( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR ), store
            .getDefaultInt( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR ) );

        super.performDefaults();
    }


    /**
     * Initializes the UI.
     *
     * @param defaultEditor
     *      the default editor
     * @param autoSwitchToOtherEditor
     *      the auto switch to other editor
     * @param autoSwitchEditor
     *      the auto switch editor
     */
    private void initUI( int defaultEditor, boolean autoSwitchToOtherEditor, int autoSwitchEditor )
    {
        // Default Editor
        if ( defaultEditor == CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE )
        {
            defaultEditorTemplateRadioButton.setSelection( true );
            defaultEditorTableRadioButton.setSelection( false );
            defaultEditorLDIFRadioButton.setSelection( false );
        }
        else if ( defaultEditor == CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TABLE )
        {
            defaultEditorTemplateRadioButton.setSelection( false );
            defaultEditorTableRadioButton.setSelection( true );
            defaultEditorLDIFRadioButton.setSelection( false );
        }
        else if ( defaultEditor == CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_LDIF )
        {
            defaultEditorTemplateRadioButton.setSelection( false );
            defaultEditorTableRadioButton.setSelection( false );
            defaultEditorLDIFRadioButton.setSelection( true );
        }

        // Auto Switch
        autoSwitchToOtherEditorCheckbox.setEnabled( defaultEditorTemplateRadioButton.getSelection() );
        autoSwitchToOtherEditorCheckbox.setSelection( autoSwitchToOtherEditor );

        // Auto Switch Editor
        autoSwitchToTableEditorRadioButton.setEnabled( defaultEditorTemplateRadioButton.getSelection()
            && autoSwitchToOtherEditorCheckbox.getSelection() );
        autoSwitchToLDIFEditorRadioButton.setEnabled( defaultEditorTemplateRadioButton.getSelection()
            && autoSwitchToOtherEditorCheckbox.getSelection() );
        if ( autoSwitchEditor == CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_TABLE )
        {
            autoSwitchToTableEditorRadioButton.setSelection( true );
            autoSwitchToLDIFEditorRadioButton.setSelection( false );
        }
        else if ( autoSwitchEditor == CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_LDIF )
        {
            autoSwitchToTableEditorRadioButton.setSelection( false );
            autoSwitchToLDIFEditorRadioButton.setSelection( true );
        }
        autoSwitchLabel.setEnabled( defaultEditorTemplateRadioButton.getSelection() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        // Default Editor
        if ( defaultEditorTemplateRadioButton.getSelection() )
        {
            store.setValue( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR,
                CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE );
        }
        else if ( defaultEditorTableRadioButton.getSelection() )
        {
            store.setValue( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR,
                CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TABLE );
        }
        else if ( defaultEditorLDIFRadioButton.getSelection() )
        {
            store.setValue( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR,
                CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_LDIF );
        }

        if ( defaultEditorTemplateRadioButton.getSelection() )
        {
            store.setValue( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR,
                CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE );
        }

        // Auto Switch
        store.setValue( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR,
            autoSwitchToOtherEditorCheckbox.getSelection() );

        // Auto Switch Editor
        if ( autoSwitchToTableEditorRadioButton.getSelection() )
        {
            store.setValue( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR,
                CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE );
        }
        else if ( autoSwitchToLDIFEditorRadioButton.getSelection() )
        {
            store.setValue( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR,
                CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TABLE );
        }

        return true;
    }
}
