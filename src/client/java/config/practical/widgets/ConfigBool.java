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
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ConfigBool extends AbstractWidget {

    private static final Identifier BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "button-background");

    public static final int HEIGHT = 25;

    public static final int TRACK_WIDTH = 38;
    public static final int TRACK_HEIGHT = 14;

    public static final int THUMB_SIZE = TRACK_HEIGHT + 2;
    public static final int THUMB_MARGIN = 8;

    public static final int THUMB_EXTENDED_X = TRACK_WIDTH - THUMB_SIZE + 2;

    public static final int DISABLED_COLOR = 0xffff0000;
    public static final int ENABLED_COLOR = 0xff00ff00;

    private static final float MAX_TICKS = 2f;

    private float totalTicks = MAX_TICKS;

    private final Consumer<Boolean> consumer;
    private final Supplier<Boolean> supplier;

    /**
     * @param message The text that will be displayed
     * @param supplier a supplier to get the current boolean
     * @param consumer a consumer to set the current boolean
     */
    public ConfigBool(Component message, Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, message);
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();

        if (totalTicks < MAX_TICKS) {
            totalTicks = Math.min(totalTicks + deltaTicks, MAX_TICKS);
        }


        float delta = totalTicks / MAX_TICKS;
        int lerpedPositon;
        int color;

        if (supplier.get()) {
            lerpedPositon = Mth.lerpInt(delta, 0, THUMB_EXTENDED_X);
            color = ENABLED_COLOR;
        } else {
            lerpedPositon = Mth.lerpInt(delta, THUMB_EXTENDED_X, 0);
            color = DISABLED_COLOR;
        }

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        //background and message
        DrawHelper.drawBackground(graphics, x, y, width, height);
        graphics.drawString(font, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);

        //the button itself
        DrawHelper.drawBackground(graphics, x + Constants.WIDGET_WIDTH - TRACK_WIDTH - THUMB_MARGIN, y + (HEIGHT - TRACK_HEIGHT) / 2, TRACK_WIDTH, TRACK_HEIGHT, color);
        DrawHelper.drawBackground(graphics, x + Constants.WIDGET_WIDTH - TRACK_WIDTH - THUMB_MARGIN + lerpedPositon - 1, y + (HEIGHT - TRACK_HEIGHT) / 2 - 1, THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        boolean newValue = !supplier.get();
        consumer.accept(newValue);
        totalTicks = 0;
        return super.mouseClicked(event, doubled);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}
