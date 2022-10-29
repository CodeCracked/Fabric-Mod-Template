package mod_profiles;

import mod_profiles.relauncher.Relauncher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.loader.impl.launch.knot.KnotClient;
import net.minecraft.client.main.Main;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ModProfilesMod implements ClientModInitializer
{
    public static final String MODID = "mod_profiles";
    public static final Logger LOGGER = LogManager.getLogger();

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
            String jarPath = URLDecoder.decode(jarFile.getPath(), StandardCharsets.UTF_8);
            LOGGER.info(jarPath);
    
            jarFile = new File(KnotClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            jarPath = URLDecoder.decode(jarFile.getPath(), StandardCharsets.UTF_8);
            LOGGER.info(jarPath);
    
            jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            jarPath = URLDecoder.decode(jarFile.getPath(), StandardCharsets.UTF_8);
            LOGGER.info(jarPath);
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
