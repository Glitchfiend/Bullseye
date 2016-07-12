/*******************************************************************************
 * Copyright 2014-2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package bullseye.init;

import java.io.File;

import bullseye.config.ConfigurationHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModConfiguration
{
    public static void init(File configDirectory)
    {
        ConfigurationHandler.init(new File(configDirectory, "config.cfg"));
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
    }
}