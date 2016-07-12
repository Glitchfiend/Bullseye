package bullseye.client.gui;

import java.util.ArrayList;
import java.util.List;

import bullseye.config.ConfigurationHandler;
import bullseye.core.Bullseye;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiBEConfig extends GuiConfig
{
    public GuiBEConfig(GuiScreen parentScreen)
    {
        super(parentScreen, GuiBEConfig.getConfigElements(), Bullseye.MOD_ID, false, false, "/bullseye");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        List<IConfigElement> arrowSettings = new ConfigElement(ConfigurationHandler.config.getCategory(ConfigurationHandler.arrowSettings.toLowerCase())).getChildElements();

        list.add(new DummyConfigElement.DummyCategoryElement(I18n.translateToLocal("config.category.arrowSettings.title"), "config.category.arrowSettings", arrowSettings));

        return list;
    }
}