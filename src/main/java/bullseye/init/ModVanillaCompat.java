package bullseye.init;

import net.minecraft.block.BlockDispenser;
import bullseye.api.BEItems;
import bullseye.entities.projectiles.dispenser.DispenserBehaviorBEArrow;

public class ModVanillaCompat
{
    public static void init()
    {
    	registerDispenserBehaviors();
    }
    
    private static void registerDispenserBehaviors()
    {
    	BlockDispenser.dispenseBehaviorRegistry.putObject(BEItems.arrow, new DispenserBehaviorBEArrow());
    }
}
