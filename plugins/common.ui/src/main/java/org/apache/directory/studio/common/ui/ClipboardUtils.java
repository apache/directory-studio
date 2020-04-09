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
package org.apache.directory.studio.common.ui;


import java.util.function.Function;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * Clipboard utilities.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
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
