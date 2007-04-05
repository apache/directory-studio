package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.apache.directory.ldapstudio.apacheds.configuration.model.Partition;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


public class PartitionDetailsPage implements IDetailsPage
{
    /** The Managed Form */
    private IManagedForm mform;
    private Partition input;
    private Text name;
    private Text cacheSize;
    private Text suffix;
    private Button enableOptimizer;
    private Button synchOnWrite;
    private Table indexedAttributesTable;
    private Button indexedAttributeAddButton;
    private Button indexedAttributeEditButton;
    private Button indexedAttributeDeleteButton;
    private Table contextEntryTable;
    private Button contextEntryAddButton;
    private Button contextEntryEditButton;
    private Button contextEntryDeleteButton;


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents( Composite parent )
    {
        FormToolkit toolkit = mform.getToolkit();

        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout( layout );

        Section detailsSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        detailsSection.marginWidth = 10;
        detailsSection.setText( "Partition Details" ); //$NON-NLS-1$
        detailsSection.setDescription( "Set the properties of the partition." ); //$NON-NLS-1$
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        detailsSection.setLayoutData( td );
        Composite detailsClient = toolkit.createComposite( detailsSection );
        toolkit.paintBordersFor( detailsClient );
        GridLayout glayout = new GridLayout( 3, false );
        detailsClient.setLayout( glayout );
        detailsSection.setClient( detailsClient );

        // Name
        toolkit.createLabel( detailsClient, "Name:" );
        name = toolkit.createText( detailsClient, "" );
        name.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Cache Size
        toolkit.createLabel( detailsClient, "Cache Size:" );
        cacheSize = toolkit.createText( detailsClient, "" );
        cacheSize.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Suffix
        toolkit.createLabel( detailsClient, "Suffix:" );
        suffix = toolkit.createText( detailsClient, "" );
        suffix.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Enable Optimizer
        enableOptimizer = toolkit.createButton( detailsClient, "Enable optimizer", SWT.CHECK );
        enableOptimizer.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Synchronisation On Write
        synchOnWrite = toolkit.createButton( detailsClient, " Synchronization on write", SWT.CHECK );
        synchOnWrite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        // Context Entry
        Section contextEntrySection = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        contextEntrySection.marginWidth = 10;
        contextEntrySection.setText( "Context Entry" ); //$NON-NLS-1$
        contextEntrySection.setDescription( "Set the attribute/value pairs for the Context Entry of the partition." ); //$NON-NLS-1$
        contextEntrySection.setLayoutData( new TableWrapData( TableWrapData.FILL ) );
        Composite contextEntryClient = toolkit.createComposite( contextEntrySection );
        toolkit.paintBordersFor( contextEntryClient );
        contextEntryClient.setLayout( new GridLayout( 2, false ) );
        contextEntrySection.setClient( contextEntryClient );

        contextEntryTable = toolkit.createTable( contextEntryClient, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 80;
        contextEntryTable.setLayoutData( gd );

        contextEntryAddButton = toolkit.createButton( contextEntryClient, "Add...", SWT.PUSH );
        contextEntryAddButton.setLayoutData( buttonsGD );

        contextEntryEditButton = toolkit.createButton( contextEntryClient, "Edit...", SWT.PUSH );
        contextEntryEditButton.setLayoutData( buttonsGD );

        contextEntryDeleteButton = toolkit.createButton( contextEntryClient, "Delete", SWT.PUSH );
        contextEntryDeleteButton.setLayoutData( buttonsGD );

        // Indexed Attributes
        Section indexedAttributesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        indexedAttributesSection.marginWidth = 10;
        indexedAttributesSection.setText( "Indexed Attributes" ); //$NON-NLS-1$
        indexedAttributesSection.setDescription( "Set the indexed attributes for the partition." ); //$NON-NLS-1$
        indexedAttributesSection.setLayoutData( new TableWrapData( TableWrapData.FILL ) );
        Composite indexedAttributesClient = toolkit.createComposite( indexedAttributesSection );
        toolkit.paintBordersFor( indexedAttributesClient );
        indexedAttributesClient.setLayout( new GridLayout( 2, false ) );
        indexedAttributesSection.setClient( indexedAttributesClient );

        indexedAttributesTable = toolkit.createTable( indexedAttributesClient, SWT.NONE );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 80;
        indexedAttributesTable.setLayoutData( gd );

        indexedAttributeAddButton = toolkit.createButton( indexedAttributesClient, "Add...", SWT.PUSH );
        indexedAttributeAddButton.setLayoutData( buttonsGD );

        indexedAttributeEditButton = toolkit.createButton( indexedAttributesClient, "Edit...", SWT.PUSH );
        indexedAttributeEditButton.setLayoutData( buttonsGD );

        indexedAttributeDeleteButton = toolkit.createButton( indexedAttributesClient, "Delete", SWT.PUSH );
        indexedAttributeDeleteButton.setLayoutData( buttonsGD );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( Partition ) ssel.getFirstElement();
        }
        else
        {
            input = null;
        }
        refresh();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit( boolean onSave )
    {
        input.setName( name.getText() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose()
    {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    public boolean isDirty()
    {
        // TODO Auto-generated method stub
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale()
    {
        // TODO Auto-generated method stub
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh()
    {
        name.setText( input.getName() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus()
    {
        name.setFocus();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
