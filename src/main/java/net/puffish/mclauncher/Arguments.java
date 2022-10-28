package net.puffish.mclauncher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Arguments{
	private final Path runDirectory;
	private final Session session;
	private final List<String> originalLaunchArgs;

	public Arguments(String[] originalLaunchArgs)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		this.runDirectory = client.runDirectory.toPath();
		this.session = client.getSession();
		
		this.originalLaunchArgs = new ArrayList<>(ManagementFactory.getRuntimeMXBean().getInputArguments());
		Collections.addAll(this.originalLaunchArgs, originalLaunchArgs);
	}
	
	public Path getRunDirectory(){
		return runDirectory;
	}
	
	public Session getSession(){
		return session;
	}
	
	public Optional<String> getOriginalArg(String name)
	{
		for (int i = 0; i < originalLaunchArgs.size(); i++) if (originalLaunchArgs.get(i).equals(name)) return Optional.of(originalLaunchArgs.get(i + 1));
		return Optional.empty();
	}
	public void forEachVariable(Consumer<String> consumer)
	{
		for (String arg : originalLaunchArgs)
		{
			if (arg.startsWith("-D") || arg.startsWith("-XX:")) consumer.accept(arg);
		}
	}
	public Optional<String> getMinRamArgument()
	{
		for (String originalLaunchArg : originalLaunchArgs) if (originalLaunchArg.startsWith("-Xms")) return Optional.of(originalLaunchArg);
		return Optional.empty();
	}
	public Optional<String> getMaxRamArgument()
	{
		for (String originalLaunchArg : originalLaunchArgs) if (originalLaunchArg.startsWith("-Xmx")) return Optional.of(originalLaunchArg);
		return Optional.empty();
	}
}
