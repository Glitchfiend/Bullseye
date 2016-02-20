package bullseye.init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import bullseye.handler.ArrowEventHandler;

public class ModHandlers
{
    public static void init()
    {
        ArrowEventHandler arrowEventHandler = new ArrowEventHandler();
        
        FMLCommonHandler.instance().bus().register(arrowEventHandler);
        MinecraftForge.EVENT_BUS.register(arrowEventHandler);
    }
}
