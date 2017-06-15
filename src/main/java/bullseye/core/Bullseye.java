/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package bullseye.core;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bullseye.init.ModConfiguration;
import bullseye.init.ModCrafting;
import bullseye.init.ModEntities;
import bullseye.init.ModItems;
import bullseye.init.ModVanillaCompat;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Bullseye.MOD_ID, version = Bullseye.MOD_VERSION, name = Bullseye.MOD_NAME, guiFactory = Bullseye.GUI_FACTORY)
public class Bullseye
{
    public static final String MOD_NAME = "Bullseye";
    public static final String MOD_ID = "bullseye";
    public static final String MOD_VERSION = "3.0.1";
    public static final String GUI_FACTORY = "bullseye.client.gui.GuiBEFactory";
    
    @Instance(MOD_ID)
    public static Bullseye instance;
    
    @SidedProxy(clientSide = "bullseye.core.ClientProxy", serverSide = "bullseye.core.CommonProxy")
    public static CommonProxy proxy;
    
    public static Logger logger = LogManager.getLogger(MOD_ID);
    public static File configDirectory;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	configDirectory = new File(event.getModConfigurationDirectory(), "bullseye");
    	ModConfiguration.init(configDirectory);
    	
    	ModEntities.init();
        ModItems.init();
        ModVanillaCompat.init();
        ModCrafting.init();
        
        proxy.registerRenderers();
    }
}
