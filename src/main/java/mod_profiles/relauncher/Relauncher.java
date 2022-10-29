package mod_profiles.relauncher;

import mod_profiles.ModProfilesMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.knot.KnotClient;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.puffish.mclauncher.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public final class Relauncher
{
    public static final String LAUNCHER_NAME = "mod-profiles-relauncher";
    public static final String ENTRY_POINT = "mod_profiles.relauncher.Relauncher";
    
    public static String[] LAUNCH_ARGS;
    
    public static void main(String[] args)
    {
        KnotClient.main(args);
    }
    public static void relaunch(boolean softRelaunch)
    {
        try
        {
            ModProfilesMod.LOGGER.info("#################### RELAUNCH ####################");
            Arguments args = new Arguments(LAUNCH_ARGS);
            OperationSystem os = OperationSystem.detect().getOrElseThrow(() -> new IllegalStateException("Could not detect Operating System!"));
    
            // Find .minecraft Directory
            Path installationPath;
            Optional<String> assetsPathOptional = args.getOriginalArg("--assetsDir");
            if (assetsPathOptional.isPresent()) installationPath = Path.of(assetsPathOptional.get()).getParent();
            else
            {
                installationPath = switch (os)
                {
                    case WINDOWS -> Path.of(System.getenv("APPDATA")).resolve(".minecraft");
                    case LINUX -> Path.of(System.getProperty("user.home")).resolve(".minecraft");
                    case MACOS -> Path.of(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("minecraft");
                };
            }
            ModProfilesMod.LOGGER.info("Install Path: " + installationPath);
            
            // Create Launcher
            MinecraftClient client = MinecraftClient.getInstance();
            GameDirectory gameDirectory = new GameDirectory(installationPath);
            Launcher launcher = new Launcher(gameDirectory, os);
            ModProfilesMod.LOGGER.info("Operating System: " + os.getName());
            
            // Get Version Name
            String profileVersion = args.getOriginalArg("--version").orElseGet(() ->
            {
                String fabricLoaderVersion = FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString();
                return "fabric-loader-" + fabricLoaderVersion + "-" + SharedConstants.getGameVersion().getReleaseTarget();
            });
    
            // Get Version
            VersionManifest versions = launcher.getVersionManifest();
            Version version = versions.getVersionInfo(profileVersion).get();
            ModProfilesMod.LOGGER.info("Version Name: " + profileVersion);
            
            // Create Launch Command
            String javaExecutablePath = ProcessHandle.current()
                    .info()
                    .command()
                    .orElseThrow();
            Command launchCommand = version.getLaunchCommand(args, Path.of(javaExecutablePath));
            ModProfilesMod.LOGGER.info("Relaunch Command: " + launchCommand.toString());
            
            // Execute Relaunch
            if (!softRelaunch)
            {
                launchCommand.createProcess();
                client.scheduleStop();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
