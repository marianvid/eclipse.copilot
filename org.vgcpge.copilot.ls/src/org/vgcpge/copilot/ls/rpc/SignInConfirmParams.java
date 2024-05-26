package org.vgcpge.copilot.ls.rpc;

import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

public class SignInConfirmParams {
	@NonNull
	private String userCode;

	public SignInConfirmParams(String userCode) {
		this.setUserCode(Objects.requireNonNull(userCode));
	}

	public @NonNull String getUserCode() {
		return userCode;
	}

	public void setUserCode(@NonNull String userCode) {
		this.userCode = userCode;
	}
}
