package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.apache.directory.ldapstudio.apacheds.configuration.Activator;
import org.apache.directory.ldapstudio.apacheds.configuration.PluginConstants;
import org.apache.directory.ldapstudio.apacheds.configuration.model.Partition;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


public class PartitionsMasterDetailsBlock extends MasterDetailsBlock
{
    private FormPage page;


    public PartitionsMasterDetailsBlock( FormPage page )
    {
        this.page = page;
    }


    @Override
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        //final ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "All Partitions" );
//        section.setDescription( "The list contains all the partitions whose details are editable on the right" ); //$NON-NLS-1$
        section.marginWidth = 10;
        section.marginHeight = 5;
        Composite client = toolkit.createComposite( section, SWT.WRAP );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout( layout );
        Table t = toolkit.createTable( client, SWT.NULL );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.heightHint = 20;
        gd.widthHint = 100;
        t.setLayoutData( gd );
        toolkit.paintBordersFor( client );
        Button b = toolkit.createButton( client, "Add...", SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        b.setLayoutData( gd );
        section.setClient( client );
        final SectionPart spart = new SectionPart( section );
        managedForm.addPart( spart );
        TableViewer viewer = new TableViewer( t );
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                managedForm.fireSelectionChanged( spart, event.getSelection() );
            }
        } );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new LabelProvider() );
        viewer.setInput( new Object[] { new Partition( "System Partition" ), new Partition( "Example Partition" ) } );
    }


    @Override
    protected void createToolBarActions( IManagedForm managedForm )
    {
        final ScrolledForm form = managedForm.getForm();
        Action haction = new Action( "Horizontal Orientation", Action.AS_RADIO_BUTTON ) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation( SWT.HORIZONTAL );
                form.reflow( true );
            }
        };
        haction.setChecked( true );
        haction.setToolTipText( "Horizontal Orientation" ); //$NON-NLS-1$
        haction.setImageDescriptor( Activator.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_HORIZONTAL_ORIENTATION ) );
        Action vaction = new Action( "Vertical Orientation", Action.AS_RADIO_BUTTON ) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation( SWT.VERTICAL );
                form.reflow( true );
            }
        };
        vaction.setChecked( false );
        vaction.setToolTipText( "Vertical Orientation" ); //$NON-NLS-1$
        vaction.setImageDescriptor( Activator.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_VERTICAL_ORIENTATION ) );
        form.getToolBarManager().add( haction );
        form.getToolBarManager().add( vaction );
    }


    @Override
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPart.registerPage( Partition.class, new PartitionDetailsPage() );
    }

}
