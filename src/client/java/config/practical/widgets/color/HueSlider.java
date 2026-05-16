package config.practical.widgets.color;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

class HueSlider extends ConfigChild {

    private static final Identifier HUE_SLIDER = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "hue-sprite");
    private static final int THUMB_DIAMETER = Constants.CORNER_RADIUS * 2;

    private final ConfigColor parent;
    private int thumbPosition = 0;

    public HueSlider(ConfigColor parent, float hue) {
        super(parent, ConfigColor.CHILD_WIDTH, ConfigColor.SLIDER_HEIGHT);
        this.parent = parent;
        setHueValue(hue);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!parent.displayColorSelector()) return;

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HUE_SLIDER, x, y, width, height);
        DrawHelper.drawBackground(graphics, x + thumbPosition, y, THUMB_DIAMETER, THUMB_DIAMETER);
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double offsetX, double offsetY) {
        super.onDrag(event, offsetX, offsetY);
        setThumbPosition(event.x());
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        setThumbPosition(event.x());
    }

    public void setHueValue(float hue) {
        thumbPosition = (int)((getNormalWidth()  - THUMB_DIAMETER) * hue);
    }

    private void setThumbPosition(double mouseX) {
        int maxWidth = (getNormalWidth() - THUMB_DIAMETER);
        thumbPosition = Math.clamp((int) mouseX - getX() - THUMB_DIAMETER / 2, 0, maxWidth);
        parent.setHueValue(thumbPosition / (float)maxWidth);
    }

    @Override
    protected boolean showWidget() {
        return parent.displayColorSelector();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigColor.WIDTH + ConfigColor.CHILD_OFFSET);
        this.setY(y + SBSelector.SIZE + ConfigColor.SLIDER_HEIGHT);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
