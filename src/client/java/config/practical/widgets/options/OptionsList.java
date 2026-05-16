package config.practical.widgets.options;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigChild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.function.Consumer;

class OptionsList <T> extends ConfigChild {

    public static final int WIDTH = 80;
    public static final int ELEMENT_HEIGHT = 20;
    private final T[] options;
    private final ConfigOptions<T> parent;

    private final Consumer<T> consumer;

    public OptionsList(ConfigOptions<T> parent, T[] options, Consumer<T> consumer) {
        super(parent, WIDTH, options.length * ELEMENT_HEIGHT);
        this.parent = parent;
        this.options = options;
        this.consumer = consumer;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {

        if (!parent.displayList()) return;

        int x = getX();
        int y = getY();

        Font font = Minecraft.getInstance().font;

        DrawHelper.drawBackground(graphics, x, y, width, ELEMENT_HEIGHT* options.length);

        for (int i = 0; i < options.length; i++) {
            T option = options[i];
            graphics.drawString(font, option.toString(),x + Constants.TEXT_PADDING, y + ELEMENT_HEIGHT * i + ((ELEMENT_HEIGHT - Constants.TEXT_HEIGHT) / 2), 0xffffffff, true);
        }

    }

    @Override
    protected boolean showWidget() {
        return parent.displayList();
    }

    @Override
    protected void updatePosition(int x, int y) {
        this.setX(x + ConfigOptions.WIDTH - WIDTH - ConfigOptions.MARGIN);
        this.setY(y + ConfigOptions.MARGIN + ELEMENT_HEIGHT);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        int index =(int) (event.y() - getY()) / ELEMENT_HEIGHT;
        if (index < 0 || index >= options.length) return;
        consumer.accept(options[index]);
        parent.hideList();
    }
}
