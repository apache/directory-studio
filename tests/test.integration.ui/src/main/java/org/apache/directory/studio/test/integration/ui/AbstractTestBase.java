package org.apache.directory.studio.test.integration.ui;


import static org.apache.directory.studio.test.integration.junit5.TestFixture.CONTEXT_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRALS_DN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.studio.test.integration.junit5.SkipTestIfLdapServerIsNotAvailableInterceptor;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSServersViewBot;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ModificationLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SearchLogsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.utils.Assertions;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(
    { SWTBotJunit5Extension.class, SkipTestIfLdapServerIsNotAvailableInterceptor.class })
public class AbstractTestBase
{

    protected SWTWorkbenchBot bot;
    protected StudioBot studioBot;
    protected ConnectionsViewBot connectionsViewBot;
    protected BrowserViewBot browserViewBot;
    protected SearchLogsViewBot searchLogsViewBot;
    protected ModificationLogsViewBot modificationLogsViewBot;
    protected ApacheDSServersViewBot serversViewBot;

    @BeforeEach
    final void setUpBase() throws Exception
    {
        bot = new SWTWorkbenchBot();
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        browserViewBot = studioBot.getBrowserView();
        searchLogsViewBot = studioBot.getSearchLogsViewBot();
        modificationLogsViewBot = studioBot.getModificationLogsViewBot();
        serversViewBot = studioBot.getApacheDSServersViewBot();
    }


    @AfterEach
    final void tearDownBase() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        serversViewBot.deleteTestServers();
        Assertions.genericTearDownAssertions();
    }

    public static final String[] ROOT_DSE_PATH =
        { "DIT", "Root DSE" };
    public static final String[] CONTEXT_PATH = path( ROOT_DSE_PATH, CONTEXT_DN.getName() );

    public static String[] path( String[] parents, String leaf )
    {
        return ArrayUtils.addAll( parents, leaf );
    }


    /**
     * Gets the path to the DN in the LDAP browser tree.
     * The path starts with "DIT", "Root DSE", and the context entry.
     */
    public static String[] path( Dn dn )
    {
        List<String> l = new ArrayList<>();

        l.addAll( Arrays.asList( CONTEXT_PATH ) );

        List<Rdn> rdns = dn.getRdns();
        for ( int i = rdns.size() - 3; i >= 0; i-- )
        {
            l.add( rdns.get( i ).getName() );
        }

        return l.toArray( new String[0] );
    }


    /**
     * Gets the path to the RDN  below the DN in the LDAP browser tree.
     * The path starts with "DIT", "Root DSE", and the context entry.
     */
    public static String[] path( Dn dn, Rdn rdn )
    {
        return path( dn, rdn.getName() );
    }


    /**
     * Gets the path to the leaf  below the DN in the LDAP browser tree.
     * The path starts with "DIT", "Root DSE", and the context entry.
     */
    public static String[] path( Dn dn, String... leaf )
    {
        return ArrayUtils.addAll( path( dn ), leaf );
    }


    public static String[] pathWithRefLdapUrl( TestLdapServer ldapServer, Dn dn )
    {
        String leaf = ldapServer.getLdapUrl() + "/" + dn.getName();
        return path( REFERRALS_DN, leaf );
    }

}
