package org.apache.directory.studio.test.integration.ui;


import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.junit5.SkipTestIfLdapServerIsNotAvailableInterceptor;
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

    @BeforeEach
    void setUp() throws Exception
    {
        bot = new SWTWorkbenchBot();
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        browserViewBot = studioBot.getBrowserView();
    }


    @AfterEach
    void tearDown() throws Exception
    {
        connectionsViewBot.closeSelectedConnections();
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }

}
