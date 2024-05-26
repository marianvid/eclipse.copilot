package org.vgcpge.copilot.ls.test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.vgcpge.copilot.ls.TextDocumentPositionParams;
import org.junit.Before;
import org.junit.Test;
import org.vgcpge.copilot.ls.CompletionParams;
import org.vgcpge.copilot.ls.rpc.CheckStatusOptions;
import org.vgcpge.copilot.ls.rpc.CheckStatusResult;
import org.vgcpge.copilot.ls.rpc.Completions;
import org.vgcpge.copilot.ls.rpc.CopilotLanguageServer;
import org.vgcpge.copilot.ls.rpc.SignInConfirmParams;
import org.vgcpge.copilot.ls.rpc.SignInInitiateResult;
import org.vgcpge.copilot.ls.rpc.Status;
import org.mockito.Mockito;

public class CopilotLanguageServerTest {

    private CopilotLanguageServer mockServer;

    @Before
    public void setUp() {
        mockServer = mock(CopilotLanguageServer.class);
    }

    @Test
    public void testCheckStatus() {
        CheckStatusResult result = new CheckStatusResult();
        CompletableFuture<CheckStatusResult> future = CompletableFuture.completedFuture(result);
        when(mockServer.checkStatus(any(CheckStatusOptions.class))).thenReturn(future);
        
        CompletableFuture<CheckStatusResult> returnedFuture = mockServer.checkStatus(new CheckStatusOptions());
        assertNotNull(returnedFuture);
    }

    @Test
    public void testSignInInitiate() {
        SignInInitiateResult result = new SignInInitiateResult();
        CompletableFuture<SignInInitiateResult> future = CompletableFuture.completedFuture(result);
        when(mockServer.signInInitiate()).thenReturn(future);

        CompletableFuture<SignInInitiateResult> returnedFuture = mockServer.signInInitiate();
        assertNotNull(returnedFuture);
    }

    @Test
    public void testSignInConfirm() {
        CheckStatusResult result = new CheckStatusResult();
        CompletableFuture<CheckStatusResult> future = CompletableFuture.completedFuture(result);
        when(mockServer.signInConfirm(any(SignInConfirmParams.class))).thenReturn(future);

        CompletableFuture<CheckStatusResult> returnedFuture = mockServer.signInConfirm(new SignInConfirmParams("userCode"));
        assertNotNull(returnedFuture);
    }

    @Test
    public void testGetCompletions() {
        Completions result = new Completions();
        CompletableFuture<Completions> future = CompletableFuture.completedFuture(result);
        when(mockServer.getCompletions(any(CompletionParams.class))).thenReturn(future);

        TextDocumentPositionParams params = new TextDocumentPositionParams("file://testfile", new Position(0, 0), 0);
        CompletionParams completionParams = new CompletionParams(params);

        CompletableFuture<Completions> returnedFuture = mockServer.getCompletions(completionParams);
        assertNotNull(returnedFuture);
    }

}
