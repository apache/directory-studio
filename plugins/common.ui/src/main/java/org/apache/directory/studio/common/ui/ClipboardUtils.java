package org.apache.directory.studio.common.ui;


import java.util.function.Function;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;


public class ClipboardUtils
{

    /**
     * Retrieve the data of the specified type currently available on the system clipboard.
     *
     * @param transfer the transfer agent for the type of data being requested
     * @return the data obtained from the clipboard or null if no data of this type is available
     */
    public static Object getFromClipboard( Transfer transfer )
    {
        return getFromClipboard( transfer, Object.class );
    }


    public static <T> T getFromClipboard( Transfer transfer, Class<T> type )
    {
        return withClipboard( clipboard -> {
            if ( isAvailable( transfer, clipboard ) )
            {
                Object contents = clipboard.getContents( transfer );
                if ( contents != null && type.isAssignableFrom( contents.getClass() ) )
                {
                    return type.cast( contents );
                }
            }
            return null;
        } );
    }


    public static boolean isAvailable( Transfer transfer )
    {
        return withClipboard( clipboard -> {
            return isAvailable( transfer, clipboard );
        } );
    }


    private static Boolean isAvailable( Transfer transfer, Clipboard clipboard )
    {
        for ( org.eclipse.swt.dnd.TransferData transferData : clipboard.getAvailableTypes() )
        {
            if ( transfer.isSupportedType( transferData ) )
            {
                return true;
            }
        }
        return false;
    }


    private static <T> T withClipboard( Function<Clipboard, T> fn )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            return fn.apply( clipboard );
        }
        finally
        {
            if ( clipboard != null )
            {
                clipboard.dispose();
            }
        }
    }
}
