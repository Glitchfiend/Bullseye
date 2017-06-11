package bullseye.client.gui;

import java.util.ArrayList;
import java.util.List;

import bullseye.config.ConfigurationHandler;
import bullseye.core.Bullseye;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import static bullseye.config.ConfigurationHandler.ARROW_SETTINGS;

public class GuiBEFactory extends DefaultGuiFactory
{
    public GuiBEFactory()
    {
        super(Bullseye.MOD_ID, Bullseye.MOD_NAME);
    }
    
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new GuiConfig(parentScreen, getConfigElements(), modid, false, false, title);
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        List<IConfigElement> arrow_settings = new ConfigElement(ConfigurationHandler.config.getCategory(ARROW_SETTINGS.toLowerCase())).getChildElements();

        list.add(new DummyCategoryElement(I18n.translateToLocal("config.category.arrowSettings.title"), "config.category.arrowSettings", arrow_settings));

        return list;
    }
}