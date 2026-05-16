package config.practical.widgets.sound;

import config.practical.utilities.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

class SoundWidget extends AbstractWidget {

    private static final Identifier BUTTON_TEXTURE = Identifier.withDefaultNamespace("widget/button");
    private static final Identifier MUSIC_NOTE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "music_note");
    private static final Identifier CHECKMARK = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "checkmark");
    private static final int HEIGHT = 30;
    private static final int BUTTON_SIZE = 20;
    private static final int PADDING = 4;

    private final Identifier identifier;
    private final SoundScreen screen;

    public SoundWidget(Identifier identifier, SoundScreen screen) {
        super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, Component.literal(identifier.toString()));
        this.identifier = identifier;
        this.screen = screen;

    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        int x = getX();
        int y = getY();
        graphics.drawString(font, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_TEXTURE, buttonX(), buttonY(), BUTTON_SIZE, BUTTON_SIZE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CHECKMARK, buttonX(), buttonY(), BUTTON_SIZE, BUTTON_SIZE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_TEXTURE, soundX(), soundY(), BUTTON_SIZE, BUTTON_SIZE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, MUSIC_NOTE, soundX(), soundY(), BUTTON_SIZE, BUTTON_SIZE);
    }

    public boolean contains(String searchTerm) {
        return identifier.toString().contains(searchTerm);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        soundClick(event.x(), event.y());
        buttonClick(event.x(), event.y());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private int buttonX() {
        return getX() + Constants.WIDGET_WIDTH - BUTTON_SIZE - PADDING;
    }

    private int buttonY() {
        return getY();
    }

    private int soundX() {
        return getX() + Constants.WIDGET_WIDTH - (BUTTON_SIZE + PADDING) * 2;
    }

    private int soundY() {
        return getY();
    }

    private void soundClick(double mouseX, double mouseY) {
        if (soundX() <= mouseX && mouseX <= soundX() + BUTTON_SIZE && soundY() <= mouseY && mouseY <= soundY() + BUTTON_SIZE) {
            screen.playSound(identifier);
        }
    }

    private void buttonClick(double mouseX, double mouseY) {
        if (buttonX() <= mouseX && mouseX <= buttonX() + BUTTON_SIZE && buttonY() <= mouseY && mouseY <= buttonY() + BUTTON_SIZE) {
            screen.setIdentifier(identifier);
        }
    }

}
