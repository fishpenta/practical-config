package config.practical.widgets.sound;

import config.practical.data.SoundData;
import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.ConfigButton;
import config.practical.widgets.ConfigSection;
import config.practical.widgets.sliders.ConfigFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ConfigSound extends ConfigSection {

    static class SoundSelector extends AbstractWidget {

        private static final Identifier COG = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "cog");
        public static final int HEIGHT = 30;
        public static final int SPRITE_SIZE = 20;
        public static final int PADDING = 4;
        private final SoundData soundData;

        public SoundSelector(SoundData soundData) {
            super(0, 0, Constants.WIDGET_WIDTH, HEIGHT, createMessage(soundData));
            this.soundData = soundData;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
            int x = getX();
            int y = getY();

            Minecraft client = Minecraft.getInstance();
            Font font = client.font;

            DrawHelper.drawBackground(graphics, x, y, width, height);
            graphics.drawString(font, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COG, x + width - SPRITE_SIZE - PADDING, y + (height - SPRITE_SIZE) / 2, SPRITE_SIZE, SPRITE_SIZE);

        }

        @Override
        public void onClick(MouseButtonEvent event, boolean doubled) {
            super.onClick(event, doubled);
            Minecraft.getInstance().setScreen(new SoundScreen(soundData, this));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        public static Component createMessage(SoundData soundData) {
            return Component.literal("Sound: " + soundData.getSound().toString());
        }

        public void updateMessage() {
            this.setMessage(createMessage(soundData));
        }


    }

    public ConfigSound(Component text, SoundData soundData, float maxVolume, float maxPitch, boolean includeTestButton) {
        super(text);
        SoundSelector sound = new SoundSelector(soundData);
        ConfigFloat volume = new ConfigFloat(Component.literal("Volume"), soundData::getVolume, soundData::setVolume, 0.01f, 0, maxVolume);
        ConfigFloat pitch = new ConfigFloat(Component.literal("Pitch"), soundData::getPitch, soundData::setPitch, 0.01f, 0, maxPitch);

        add(sound);
        add(volume);
        add(pitch);

        if (includeTestButton) {
            add(new ConfigButton(Component.literal("Test sound"), () -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;
                player.playSound(SoundEvent.createVariableRangeEvent(soundData.getSound()), soundData.getVolume(), soundData.getPitch());
            }));
        }
    }

    public ConfigSound(Component text, SoundData soundData) {
        this(text, soundData, 2, 2, true);
    }
}
