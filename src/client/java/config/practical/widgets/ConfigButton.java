package config.practical.widgets;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public class ConfigButton extends AbstractWidget {
    public static final int HEIGHT = 25;

    private final Runnable runnable;
    private long clickedTime = 0;
    private final long delay;

    public ConfigButton(Component message, Runnable runnable, long delay) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, message);
        this.delay = delay;
        this.runnable = runnable;
    }

    @SuppressWarnings("unused")
    public ConfigButton(Component message, Runnable runnable) {
        this(message, runnable, 200);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();

        Font font = Minecraft.getInstance().font;

        int centered = (width - font.width(getMessage())) / 2;

        long diff = System.currentTimeMillis() - clickedTime;

        if (diff > delay) {
            DrawHelper.drawBackground(graphics, x, y, width, height, 0xff666666);
        } else {
            DrawHelper.drawBackground(graphics, x, y, width, height, 0xff333333);

        }

        graphics.drawString(font, getMessage(), x + centered, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);


    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        long diff = System.currentTimeMillis() - clickedTime;

        if (diff > delay) {
            this.runnable.run();
            clickedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }


}
