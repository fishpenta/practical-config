package config.practical.widgets.color;

import com.mojang.blaze3d.platform.NativeImage;
import config.practical.utilities.Constants;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import java.awt.*;

class SBSelector extends ConfigChild {

    private static final Identifier EMPTY_SB_MASK = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "empty-sb-mask");
    public static final int SIZE = ConfigColor.CHILD_WIDTH;
    public static final int SPRITE_SIZE = SIZE - Constants.LINE_THICKNESS * 2;

    private final ConfigColor parent;
    private final Identifier sbMask;

    public SBSelector(ConfigColor parent, String identifier, float hue) {
        super(parent, SIZE, SIZE);
        this.parent = parent;

        sbMask = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, identifier);
        makeSBMask(hue);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!parent.displayColorSelector()) return;

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        graphics.blit(RenderPipelines.GUI_TEXTURED, sbMask, x + Constants.LINE_THICKNESS, y + Constants.LINE_THICKNESS, 0, 0, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, Constants.WHITE_COLOR);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMPTY_SB_MASK, x, y, width, height);
    }

    @Override
    protected boolean showWidget() {
        return parent.displayColorSelector();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigColor.WIDTH + ConfigColor.CHILD_OFFSET);
        this.setY(y + ConfigColor.SLIDER_HEIGHT);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);

        float saturation = Math.clamp((float) (event.x() - getX() - Constants.LINE_THICKNESS) / (float) SPRITE_SIZE, 0f, 1f);
        float brightness = 1 - Math.clamp((float) (event.y() - getY() - Constants.LINE_THICKNESS) / (float) SPRITE_SIZE, 0f, 1f);

        parent.setSBValue(saturation, brightness);
    }

    public void makeSBMask(float hue) {
        try (NativeImage image = new NativeImage(SPRITE_SIZE, SPRITE_SIZE, true)) {

            float saturationFraction = (float) (1.0 / (SPRITE_SIZE - 1));
            float brightnessFraction = (float) (1.0 / (SPRITE_SIZE - 1));

            for (int i = 0; i < SPRITE_SIZE; i++) {
                for (int j = 0; j < SPRITE_SIZE; j++) {

                    float saturation = Math.min(i * saturationFraction, 1);
                    float brightness = Math.min((SPRITE_SIZE - 1 - j) * brightnessFraction, 1);

                    int argb = Color.HSBtoRGB(hue, saturation, brightness) | (0xff << 24);

                    image.setPixel(i, j, argb);
                }
            }

            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            DynamicTexture texture = new DynamicTexture(() -> "TODO: change this", image);

            texture.upload();
            textureManager.release(sbMask);
            textureManager.register(sbMask, texture);
        }
    }

}
