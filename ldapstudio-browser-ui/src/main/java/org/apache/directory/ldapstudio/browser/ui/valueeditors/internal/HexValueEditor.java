package org.apache.directory.ldapstudio.browser.ui.valueeditors.internal;


import org.apache.directory.ldapstudio.browser.ui.dialogs.HexDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogBinaryValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * The default editor for binary values. Uses the HexDialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HexValueEditor extends AbstractDialogBinaryValueEditor
{

    public HexValueEditor()
    {
        super();
    }


    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof byte[] )
        {
            byte[] initialData = ( byte[] ) value;
            HexDialog dialog = new HexDialog( shell, initialData );
            if ( dialog.open() == HexDialog.OK && dialog.getData() != null )
            {
                setValue( dialog.getData() );
                return true;
            }
        }
        return false;
    }

}
