package org.apache.directory.ldapstudio.browser.ui.valueeditors.internal;


import org.apache.directory.ldapstudio.browser.ui.dialogs.HexDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogBinaryValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * The default editor for binary values. Uses the HexDialog.
 * 
 * The HexDialog is currently only able to save and load binary data
 * to and from file. It is not possible to edit the data in the dialog
 * directly.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HexValueEditor extends AbstractDialogBinaryValueEditor
{

    /**
     * This implementation opens the HexDialog.
     */
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
