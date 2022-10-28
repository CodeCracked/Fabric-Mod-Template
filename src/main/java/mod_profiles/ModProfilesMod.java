package mod_profiles;

import mod_profiles.relauncher.Relauncher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ModProfilesMod implements ClientModInitializer
{
    public static final String MODID = "mod_profiles";
    public static final Logger LOGGER = LogManager.getLogger();
    public static String MOD_FILE_PATH;

    private boolean ranVersionCheck;
    
    @Override
    public void onInitializeClient()
    {
        ModProfilesKeybinds.register();
        ClientEntityEvents.ENTITY_LOAD.register(this::onWorldLoaded);
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        LOGGER.info("#################### Stack Trace ########################");
        for (StackTraceElement e : stackTrace) LOGGER.info(e);
        
        LOGGER.info(String.join(" ", Relauncher.LAUNCH_ARGS));
        
        LOGGER.info("#################### Advanced ####################");
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = bean.getInputArguments();
        
        StringBuilder launchCommand = new StringBuilder(String.join(" ", jvmArgs));
        launchCommand.append(String.join(" ", Relauncher.LAUNCH_ARGS));
        launchCommand.append(" -classpath " + System.getProperty("java.class.path"));
        LOGGER.info(launchCommand);
        LOGGER.info(System.getProperty("sun.java.command"));
        
        try
        {
            File jarFile = new File(ModProfilesMod.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            MOD_FILE_PATH = URLDecoder.decode(jarFile.getPath(), StandardCharsets.UTF_8);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
    
    private void onWorldLoaded(Entity entity, ClientWorld world)
    {
        if (entity instanceof PlayerEntity)
        {
            if (!ranVersionCheck)
            {
                ranVersionCheck = true;
                VersionChecker.doVersionCheck();
            }
        }
    }
}
