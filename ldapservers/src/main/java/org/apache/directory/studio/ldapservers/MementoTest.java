package org.apache.directory.studio.ldapservers;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;


public class MementoTest
{
    public static void main( String[] args ) throws IOException, WorkbenchException
    {
        XMLMemento memento = XMLMemento.createReadRoot( new FileReader( new File( "testfile" ) ) );


        XMLMemento memento2 = XMLMemento.createWriteRoot( "type" );

        memento2.putMemento( memento );
        memento2.save( new FileWriter( new File( "testfile2" ) ) );
        
        //        memento.putBoolean( "bool", true );
        //
        //        IMemento mem = memento.createChild( "child" );
        //
        //        mem.putTextData( "blablabla" );
        //
        //        memento.save( new FileWriter( new File( "testfile" ) ) );
        //        System.out.println( "written." );
    }
}
