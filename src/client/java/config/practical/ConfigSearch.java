package config.practical;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ConfigSearch extends EditBox {

    public static final int WIDTH = 150;
    public static final int HEIGHT = 30;

    public ConfigSearch(Font font, int x, int y, Consumer<String> responder) {
        super(font, x, y, WIDTH, HEIGHT, Component.empty());
        this.setResponder(responder);
    }


}
