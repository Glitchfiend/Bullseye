/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package bullseye.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bullseye.init.ModCrafting;
import bullseye.init.ModEntities;
import bullseye.init.ModItems;
import bullseye.init.ModVanillaCompat;

@Mod(modid = Bullseye.MOD_ID, name = Bullseye.MOD_NAME)
public class Bullseye
{
    public static final String MOD_NAME = "Bullseye";
    public static final String MOD_ID = "Bullseye";
    
    @Instance(MOD_ID)
    public static Bullseye instance;
    
    @SidedProxy(clientSide = "bullseye.core.ClientProxy", serverSide = "bullseye.core.CommonProxy")
    public static CommonProxy proxy;
    
    public static Logger logger = LogManager.getLogger(MOD_ID);
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	ModEntities.init();
        ModItems.init();
        ModVanillaCompat.init();
        ModCrafting.init();
        
        proxy.registerRenderers();
    }
}
