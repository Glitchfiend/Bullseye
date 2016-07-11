package bullseye.init;

import bullseye.api.BEItems;
import bullseye.entities.projectiles.dispenser.DispenserBehaviorBEArrow;
import bullseye.entities.projectiles.dispenser.DispenserBehaviorDyeArrow;
import net.minecraft.block.BlockDispenser;

public class ModVanillaCompat
{
    public static void init()
    {
    	registerDispenserBehaviors();
    }
    
    private static void registerDispenserBehaviors()
    {
    	BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BEItems.arrow, new DispenserBehaviorBEArrow());
    	BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BEItems.dye_arrow, new DispenserBehaviorDyeArrow());
    }
}
