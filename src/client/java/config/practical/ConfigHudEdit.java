package config.practical;

import config.practical.hud.ComponentEditScreen;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

class ConfigHudEdit extends AbstractWidget {

    private static final Component TEXT = Component.literal("Modify GUI");

    public static final int WIDTH = 150;
    public static final int HEIGHT = 20;

    public static final int TEXT_Y_OFFSET = 6;

    public static final int BACKGROUND_COLOR = 0xff666666;
    public static final int BLACK_COLOR = 0xff000000;
    public static final int WHITE_COLOR = 0xffffffff;

    public final Screen screen;

    public ConfigHudEdit(Screen screen, int x, int y) {
        super(x, y, WIDTH, HEIGHT, TEXT);
        this.screen = screen;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        Component message = getMessage();

        graphics.fill(x, y, x + WIDTH, y + HEIGHT, BACKGROUND_COLOR);
        DrawHelper.drawBorder(graphics, x, y, WIDTH, HEIGHT, this.isFocused() ? WHITE_COLOR : BLACK_COLOR);

        int textWidth = font.width(message);

        graphics.drawString(font, message, x + (WIDTH - textWidth) / 2, y + TEXT_Y_OFFSET, WHITE_COLOR, true);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        Minecraft.getInstance().setScreen(new ComponentEditScreen(screen));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
