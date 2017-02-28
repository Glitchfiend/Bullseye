package bullseye.util.inventory;

import bullseye.api.BEItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabBE extends CreativeTabs
{
    public static final CreativeTabs instance = new CreativeTabBE(CreativeTabs.getNextID(), "tabBullseye");

    private CreativeTabBE(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(BEItems.be_icon);
    }
}
