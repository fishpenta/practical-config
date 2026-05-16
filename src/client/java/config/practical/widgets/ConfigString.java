package config.practical.widgets;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigString extends EditBox {

    private static final int HEIGHT = 40;
    private static final int INPUT_PADDING = 2;
    private static final int TEXT_OFFSET = 13;
    private static final int INPUT_HEIGHT = 14;
    private static final int INPUT_COLOR = 0xff222222;

    private int maxLength;
    private int selectionEnd;
    private Consumer<String> responderListener;

    public ConfigString(Component message, Supplier<String> supplier, Consumer<String> consumer, boolean formatText) {
        super(Minecraft.getInstance().font, Constants.WIDGET_WIDTH, HEIGHT, message);
        this.setMaxLength(100);
        this.setValue(supplier.get());
        this.setResponder(string -> {
            if (formatText) {
                consumer.accept(string.replace('&', '§'));
            } else {
                consumer.accept(string);
            }
        });
    }

    public ConfigString(Component message, Supplier<String> supplier, Consumer<String> consumer) {
        this(message, supplier, consumer, true);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        DrawHelper.drawBackground(graphics, getX(), super.getY(), width, getMainBackgroundHeight());
        graphics.drawString(Minecraft.getInstance().font, getMessage(), getX() + Constants.TEXT_PADDING, super.getY() + (getMainBackgroundHeight() - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
        DrawHelper.drawBackground(graphics, getX(), super.getY() + (height - INPUT_HEIGHT), width, INPUT_HEIGHT, INPUT_COLOR);
        super.renderWidget(graphics, mouseX, mouseY, deltaTicks);

    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.isActive()) {
            return false;
        } else if (event.isAllowedChatCharacter() || isValid(event)) {
            this.insertText(event.codepointAsString());
            return true;
        } else {
            return false;
        }
    }


    private boolean isValid(CharacterEvent input) {
        return input.codepointAsString().contains("§");
    }

    /**
     * A slightly modified version of the TextFieldWidget write(String)
     * because it removes chars like §
     * @param text The text to add
     */
    @Override
    public void insertText(String text) {
        int i = Math.min(getCursorPosition(), this.selectionEnd);
        int j = Math.max(getCursorPosition(), this.selectionEnd);
        int k = this.maxLength - getValue().length() - (i - j);
        if (k > 0) {
            //String string = StringHelper.stripInvalidChars(text);
            String string = text;
            int l = string.length();
            if (k < l) {
                if (Character.isHighSurrogate(string.charAt(k - 1))) {
                    k--;
                }

                string = string.substring(0, k);
                l = k;
            }
            String string2 = new StringBuilder(getValue()).replace(i, j, string).toString();
            setValue(string2);
            this.setCursorPosition(i + l);
            this.setHighlightPos(getCursorPosition());
            responderListener.accept(getValue());
        }
    }

    @Override
    public void setHighlightPos(int index) {
        this.selectionEnd = Mth.clamp(index, 0, getValue().length());
        super.setHighlightPos(index);
    }

    @Override
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        super.setMaxLength(maxLength);
    }

    @Override
    public void setResponder(Consumer<String> responderListener) {
        this.responderListener = responderListener;
        super.setResponder(responderListener);
    }

    @Override
    public int getY() {
        return super.getY() + TEXT_OFFSET;
    }

    private int getMainBackgroundHeight() {
        return height - INPUT_HEIGHT - 1;
    }

    @Override
    public int getInnerWidth() {
        return width - INPUT_PADDING * 2 - 4;
    }

    @Override
    public boolean isBordered() {
        return false;
    }
}
