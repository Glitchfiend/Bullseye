package bullseye.init;

import net.minecraft.block.BlockDispenser;
import bullseye.api.BEItems;
import bullseye.entities.projectiles.dispenser.DispenserBehaviorBEArrow;
import bullseye.entities.projectiles.dispenser.DispenserBehaviorDyeArrow;

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
