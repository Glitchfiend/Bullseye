/*******************************************************************************
 * Copyright 2015-2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package bullseye.config;

import java.io.File;

import bullseye.core.Bullseye;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler
{
    public static Configuration config;

    public static String arrowSettings = "Arrow Settings";
    
    public static boolean explodeBombArrows;
    public static boolean burnFireArrows;
    public static boolean fireLightningArrows;

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        try
        {
            explodeBombArrows = config.getBoolean("Bombs Arrows Destroy Blocks", arrowSettings, true, "Allow Bomb Arrows to destroy blocks.");
            burnFireArrows = config.getBoolean("Fire Arrows Burn Blocks", arrowSettings, true, "Allow Fire Arrows to catch fire to blocks.");
            fireLightningArrows = config.getBoolean("Lightning Arrows Burn Blocks", arrowSettings, true, "Allow Lightning Arrows to catch fire to blocks.");
        }
        catch (Exception e)
        {
            Bullseye.logger.error("Bullseye has encountered a problem loading misc.cfg", e);
        }
        finally
        {
            if (config.hasChanged()) config.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(Bullseye.MOD_ID))
        {
            loadConfiguration();
        }
    }
}