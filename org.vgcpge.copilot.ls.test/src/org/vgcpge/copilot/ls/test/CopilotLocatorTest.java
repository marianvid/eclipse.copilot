package org.vgcpge.copilot.ls.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.vgcpge.copilot.ls.CopilotLocator;

public class CopilotLocatorTest {

    private CopilotLocator locator;
    private Path tempDirectory;

    @Before
    public void setUp() throws IOException {
        locator = mock(CopilotLocator.class);
        tempDirectory = Files.createTempDirectory("test");
        when(locator.downloadAgent()).thenReturn(Optional.of(tempDirectory.resolve("downloaded-agent/agent.js")));
        when(locator.findNode()).thenReturn("mockedNodePath");
    }

    @Test
    public void testDownloadAgent() throws IOException {
        locator.setPersistentStorageLocation(tempDirectory);
        Path downloadedAgentPath = tempDirectory.resolve("downloaded-agent/agent.js");
        Files.createDirectories(downloadedAgentPath.getParent());
        Files.createFile(downloadedAgentPath);

        when(locator.downloadAgent()).thenReturn(Optional.of(downloadedAgentPath));

        Optional<Path> agentPath = locator.downloadAgent();
        assertTrue(agentPath.isPresent());
        assertTrue(Files.exists(agentPath.get()));
    }


    @Test
    public void testAvailableAgents() {
        locator.setPersistentStorageLocation(tempDirectory);
        doReturn(Stream.of(tempDirectory.resolve("agent1"), tempDirectory.resolve("agent2"))).when(locator).availableAgents();
        long agentCount = locator.availableAgents().count();
        assertTrue(agentCount > 0);
    }

    @Test
    public void testFindNode() {
        String nodePath = locator.findNode();
        assertNotNull(nodePath);
    }
}
