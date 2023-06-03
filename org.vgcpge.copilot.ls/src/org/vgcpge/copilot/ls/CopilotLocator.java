package org.vgcpge.copilot.ls;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CopilotLocator {
	private static final Path NVIM_RELATIVE_PATH = Path.of("nvim", "pack", "github", "start", "copilot.vim", "copilot",
			"dist", "agent.js");
	private static final List<Path> NODE_PATH_CANDIDATES = List.of(Paths.get("/opt/homebrew/bin/node"));

	public CopilotLocator() {
		super();
	}
	
	public static List<String> copilotStartCommand() {
		return List.of(findNode(), findAgent().toString());
	}

	private static String findNode() {
		try {
			
			try {
				if (isValidNode("node")) {
					return "node";
				}
			} catch (IOException e) {
				// No PATH
			}
			for (Path path : NODE_PATH_CANDIDATES) {
				if (Files.isExecutable(path)) {
					try {
						if (isValidNode(path.toString())) {
							return path.toString();
						}
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			throw new IllegalStateException("Can't locate Node.js. Configure PATH.");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		}
	}

	private static boolean isValidNode(String nodeCommand) throws InterruptedException, IOException {
		ProcessBuilder builder = new ProcessBuilder(nodeCommand, "--version");

		Process process = builder.start();
		try {
			return process.waitFor() == 0;
		} finally {
			process.destroyForcibly();
		}
	}

	private static Path findAgent() {
		return configurationLocations().stream() //
				.map(location -> location.resolve(NVIM_RELATIVE_PATH)) //
				.filter(Files::isRegularFile) //
				.filter(Files::isReadable) //
				.findFirst() //
				.orElseThrow(() -> new IllegalStateException("Copilot is not installed. Install Github Copilot for Nvim: https://docs.github.com/en/copilot/getting-started-with-github-copilot?tool=neovim"));
	}

	private static List<Path> configurationLocations() {
		var result = new ArrayList<Path>();
		String home = System.getProperty("user.home");
		result.add(Paths.get(home).resolve(".config"));
		String data = System.getenv("LOCALAPPDATA");
		if (data != null) {
			result.add(Paths.get(data));
		}
		return result;
	}
}
