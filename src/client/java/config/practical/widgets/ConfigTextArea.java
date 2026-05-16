package config.practical.widgets;

import config.practical.utilities.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class ConfigTextArea extends AbstractWidget {

    private final Font font;
    private final FormattedText formattedText;

    public ConfigTextArea(String string) {
        super(0, 0, Constants.WIDGET_WIDTH, getHeight(string, Constants.WIDGET_WIDTH), Component.empty());
        font = Minecraft.getInstance().font;
        formattedText = FormattedText.of(string);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        graphics.drawWordWrap(font, formattedText, getX(), getY(), width, 0xffffffff, true);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private static int getHeight(String string, int width) {
        Font font = Minecraft.getInstance().font;
        FormattedText formattedText = FormattedText.of(string);
        return font.wordWrapHeight(formattedText, width);
    }
}
