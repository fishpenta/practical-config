package config.practical.widgets.sound;

import com.mojang.blaze3d.platform.Window;
import config.practical.ConfigScroll;
import config.practical.ConfigSearch;
import config.practical.data.SoundData;
import config.practical.utilities.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;

class SoundScreen extends Screen {
    private final Screen parent;

    private static final int BUTTON_HEIGHT = 20;

    private final Button confirm;
    private final ConfigScroll scroll;
    private final ConfigSearch search;
    private final StringWidget text;
    private final ArrayList<SoundWidget> soundWidgets = new ArrayList<>();

    private final SoundData soundData;
    private final ConfigSound.SoundSelector soundSelector;

    private Identifier identifier;
    private SoundInstance prevSound;

    public SoundScreen(SoundData soundData, ConfigSound.SoundSelector soundSelector) {
        super(Component.empty());
        loadWidgets();
        Minecraft client = Minecraft.getInstance();
        parent = client.screen;
        Window window = client.getWindow();

        this.soundData = soundData;
        this.soundSelector = soundSelector;

        this.identifier = soundData.getSound();

        int textX = (window.getGuiScaledWidth() - Constants.WIDGET_WIDTH) / 2;

        int scrollHeight = window.getGuiScaledHeight() - ConfigSearch.HEIGHT - BUTTON_HEIGHT - 20;

        Component btnText = Component.literal("Select");
        Font font = client.font;
        int btnWidth = font.width(btnText) * 2;

        text = new StringWidget(textX, 0, Constants.WIDGET_WIDTH, BUTTON_HEIGHT, Component.literal("Sound: " + soundData.getSound().toString()), font);
        confirm = Button.builder(btnText, this::confirmSound).pos(textX + Constants.WIDGET_WIDTH, 0).width(btnWidth).build();
        scroll = new ConfigScroll(0, BUTTON_HEIGHT + 10, window.getGuiScaledWidth(), scrollHeight, Constants.WIDGET_WIDTH);
        search = new ConfigSearch(client.font, (window.getGuiScaledWidth() - ConfigSearch.WIDTH) / 2, scrollHeight + scroll.getY() + 5, this::updateScroll);
        updateScroll("");
    }

    @Override
    protected void init() {
        this.addRenderableWidget(text);
        this.addRenderableWidget(confirm);
        this.addRenderableWidget(search);
        this.addRenderableWidget(scroll);
    }

    private void updateScroll(String searchTerm) {
        scroll.setFocused(null);
        scroll.children().clear();

        for (SoundWidget widget : soundWidgets) {
            if (widget.contains(searchTerm)) {
                scroll.add(widget);
            }
        }

        scroll.update();
        scroll.setScrollAmount(0);
    }

    public void setIdentifier(Identifier identifier) {
        if (identifier == null) return;
        this.identifier = identifier;
        text.setMessage(Component.literal("Sound: " + identifier));
    }

    public void playSound(Identifier identifier) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        stopSound();

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        prevSound = new SimpleSoundInstance(SoundEvent.createVariableRangeEvent(identifier), SoundSource.MASTER, 1, 1, SoundInstance.createUnseededRandom(), player.blockPosition());
        soundManager.play(prevSound);
    }

    private void stopSound() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        if (prevSound != null) {
            soundManager.stop(prevSound);
        }
    }

    private void loadWidgets() {
        for (Identifier id : BuiltInRegistries.SOUND_EVENT.keySet()) {
            soundWidgets.add(new SoundWidget(id, this));
        }
    }

    private void confirmSound(Button button) {
        soundData.setSound(identifier);
        soundSelector.updateMessage();
        onClose();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
        stopSound();

    }
}
