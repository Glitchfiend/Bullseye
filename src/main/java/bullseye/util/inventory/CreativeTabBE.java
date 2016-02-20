package bullseye.util.inventory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import bullseye.api.BEItems;

public class CreativeTabBE extends CreativeTabs
{
    public static final CreativeTabs instance = new CreativeTabBE(CreativeTabs.getNextID(), "tabBullseye");

    private CreativeTabBE(int index, String label)
    {
        super(index, label);
    }

    @Override
    public Item getTabIconItem()
    {
        return BEItems.be_icon;
    }
}
