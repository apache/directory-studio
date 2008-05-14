import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v151.ServerXmlIOV151;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerXmlIOV152;


public class Main
{

    /**
     * TODO main.
     *
     * @param args
     * @throws ServerXmlIOException 
     */
    public static void main( String[] args ) throws ServerXmlIOException
    {
        ServerXmlIOV152 serverXmlV152IO = new ServerXmlIOV152();
        ServerXmlIOV151 serverXmlIO = new ServerXmlIOV151();
        ServerConfiguration serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
            .getResourceAsStream( "default-server.xml" ) );
        
        String string = serverXmlV152IO.toXml( serverConfiguration );
        System.out.println( string );
    }

}
