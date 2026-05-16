package config.practical.widgets.sliders;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class Slider extends AbstractWidget {

    private static final int HEIGHT = 30;

    private static final int THUMB_RADIUS = 7;

    private static final int SLIDER_WIDTH = 100;
    private static final int SLIDER_HEIGHT = 14;
    private static final int SLIDER_RIGHT_PADDING = 7;
    private static final int SLIDER_BACKGROUND_COLOR = 0xff444444;


    private int thumbPos;
    private boolean isDragging = false;

    public Slider(Component message) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, message);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        int sliderX = getSliderX();
        int sliderY = getSliderY();

        String currStr = getCurrValue();

        Font font = Minecraft.getInstance().font;

        //background
        DrawHelper.drawBackground(graphics, x, y, width, height);
        graphics.drawString(font, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);

        //slider
        DrawHelper.drawBackground(graphics, sliderX, sliderY, SLIDER_WIDTH, SLIDER_HEIGHT, SLIDER_BACKGROUND_COLOR);

        //thumb
        DrawHelper.drawBackground(graphics, sliderX + thumbPos, sliderY, THUMB_RADIUS * 2, THUMB_RADIUS * 2);

        graphics.drawString(font, currStr, sliderX - font.width(currStr) - 2, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
    }

    abstract String getCurrValue();

    abstract void updateValue(float delta);

    protected int getSliderX() {
        return getX() + getWidth() - SLIDER_WIDTH - SLIDER_RIGHT_PADDING;
    }

    protected int getSliderY() {
        return getY() + (HEIGHT - SLIDER_HEIGHT) / 2;
    }

    protected boolean insideSlider(int x, int y) {
        int sliderX = getSliderX();
        int sliderY = getSliderY();

        return x >= sliderX && x <= sliderX + SLIDER_WIDTH && y >= sliderY && y <= sliderY + SLIDER_HEIGHT;
    }

    protected void updateThumbPosWithDelta(float delta) {
        float clampedDelta = Mth.clamp(delta, 0, 1);
        thumbPos = (int)(clampedDelta * (SLIDER_WIDTH - THUMB_RADIUS * 2));
    }

    public void updateThumbPos(double mouseX) {
        thumbPos = Mth.clamp((int) mouseX - getSliderX() - THUMB_RADIUS, 0, (SLIDER_WIDTH - THUMB_RADIUS * 2));
        float delta = (float) thumbPos / (SLIDER_WIDTH - THUMB_RADIUS * 2);
        updateValue(delta);
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        super.onRelease(event);
        isDragging = false;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        if (insideSlider((int) event.x(), (int) event.y())) {
            isDragging = true;
            updateThumbPos(event.x());
        }
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double offsetX, double offsetY) {
        super.onDrag(event, offsetX, offsetY);
        if (isDragging) {
            updateThumbPos(event.x());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
