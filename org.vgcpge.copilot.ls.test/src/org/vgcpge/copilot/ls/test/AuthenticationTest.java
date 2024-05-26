package org.vgcpge.copilot.ls.test;

import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ShowDocumentResult;
import org.eclipse.lsp4j.ShowDocumentParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.Before;
import org.junit.Test;
import org.vgcpge.copilot.ls.Authentication;
import org.vgcpge.copilot.ls.rpc.CheckStatusOptions;
import org.vgcpge.copilot.ls.rpc.CheckStatusResult;
import org.vgcpge.copilot.ls.rpc.CopilotLanguageServer;
import org.vgcpge.copilot.ls.rpc.SignInConfirmParams;
import org.vgcpge.copilot.ls.rpc.Status;
import org.vgcpge.copilot.ls.rpc.SignInInitiateResult;

public class AuthenticationTest {

    private CopilotLanguageServer mockServer;
    private Authentication auth;
    private LanguageClient mockClient;

    @Before
    public void setUp() {
        mockClient = mock(LanguageClient.class);
        mockServer = mock(CopilotLanguageServer.class);
        auth = new Authentication(mockClient, mockServer);
    }
    

    @Test
    public void testEnsureAuthenticated() throws Exception {
        CheckStatusOptions options = new CheckStatusOptions();
        options.localChecksOnly = false;
        CheckStatusResult checkStatusResult = new CheckStatusResult();
        checkStatusResult.status = Status.Normal; // or whatever status you need for the test
        when(mockServer.checkStatus(argThat(argument -> argument != null && argument.localChecksOnly == false)))
            .thenReturn(CompletableFuture.completedFuture(checkStatusResult));

        // Use reflection to invoke the private method
        Method ensureAuthenticated = Authentication.class.getDeclaredMethod("ensureAuthenticated");
        ensureAuthenticated.setAccessible(true);
        ensureAuthenticated.invoke(auth);

        verify(mockServer, times(2)).checkStatus(any(CheckStatusOptions.class));
    }



    @Test
    public void testAuthenticate() throws Throwable {
        // Mock the signInInitiate method to return a valid CompletableFuture
        SignInInitiateResult signInInitiateResult = new SignInInitiateResult();
        signInInitiateResult.verificationUri = "http://example.com";
        signInInitiateResult.userCode = "userCode";
        signInInitiateResult.expiresIn = 600; // Expiry time in seconds
        signInInitiateResult.interval = 5; // Interval for polling in seconds
        when(mockServer.signInInitiate()).thenReturn(CompletableFuture.completedFuture(signInInitiateResult));

        // Mock the signInConfirm method to return a valid CompletableFuture
        SignInConfirmParams params = new SignInConfirmParams("userCode");
        CheckStatusResult checkStatusResult = new CheckStatusResult();
        checkStatusResult.status = Status.Normal; // Ensure the status is set correctly
        when(mockServer.signInConfirm(any(SignInConfirmParams.class))).thenReturn(CompletableFuture.completedFuture(checkStatusResult));


        // Mock the showDocument method to return a valid CompletableFuture
        ShowDocumentParams showDocumentParams = new ShowDocumentParams(signInInitiateResult.verificationUri);
        showDocumentParams.setSelection(new Range(new Position(0, 0), new Position(0, 0)));
        when(mockClient.showDocument(any(ShowDocumentParams.class))).thenReturn(CompletableFuture.completedFuture(new ShowDocumentResult(true)));


        // Mock the showMessageRequest method to return a valid CompletableFuture
        ShowMessageRequestParams requestParams = new ShowMessageRequestParams();
        requestParams.setMessage("To sign in Github Copilot, enter code userCode on http://example.com?userCode=userCode. For your convenience code has been appended to the URL.");
        requestParams.setType(MessageType.Warning);
        requestParams.setActions(List.of(new MessageActionItem("OK"), new MessageActionItem("Cancel")));
        when(mockClient.showMessageRequest(requestParams)).thenReturn(CompletableFuture.completedFuture(new MessageActionItem("OK")));

        // Use reflection to invoke the private method
        Method authenticate = Authentication.class.getDeclaredMethod("authenticate");
        authenticate.setAccessible(true);
        try {
            authenticate.invoke(auth);
            verify(mockServer).signInInitiate();
            verify(mockServer).signInConfirm(argThat(argument -> "userCode".equals(argument.getUserCode())));
            verify(mockClient).showDocument(any(ShowDocumentParams.class));
            verify(mockClient).showMessageRequest(any(ShowMessageRequestParams.class));
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace(); // Print the underlying cause
            throw e.getCause(); // Re-throw the underlying cause for further inspection
        }
        // Verify that the methods were called
        
        
    }


}