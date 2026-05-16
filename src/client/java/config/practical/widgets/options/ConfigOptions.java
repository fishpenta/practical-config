package config.practical.widgets.options;

import config.practical.utilities.Constants;
import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigOptions<T> extends ConfigParent {

    public static final int MARGIN = 5;
    public static final int WIDTH = Constants.WIDGET_WIDTH;
    public static final int HEIGHT = OptionsList.ELEMENT_HEIGHT + MARGIN * 2;

    private final OptionsList<T> list;
    private boolean displayList = false;

    private final Supplier<T> supplier;

    /**
     * @param message The text that will be displayed
     * @param options A list of option
     * @param supplier a supplier to the current option
     * @param consumer a consumer to set the current option
     */
    public ConfigOptions(Component message, T[] options, Supplier<T> supplier, Consumer<T> consumer) {
        super(0, 0, WIDTH, HEIGHT, message);
        list = new OptionsList<>(this, options, consumer);
        addChild(list);
        this.supplier = supplier;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        //background and message
        DrawHelper.drawBackground(graphics, x, y, width, height);
        graphics.drawString(Minecraft.getInstance().font, getMessage(), x + Constants.TEXT_PADDING, y + (HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);

        //selected item
        DrawHelper.drawBackground(graphics, x + WIDTH - OptionsList.WIDTH - MARGIN, y + MARGIN, OptionsList.WIDTH, OptionsList.ELEMENT_HEIGHT);
        graphics.drawString(Minecraft.getInstance().font, supplier.get().toString(), x + WIDTH - OptionsList.WIDTH - MARGIN + Constants.TEXT_PADDING, y + MARGIN + (OptionsList.ELEMENT_HEIGHT - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        super.onClick(event, doubled);
        displayList = !displayList;
        update();
    }

    public boolean displayList() {
        return displayList;
    }

    public void hideList() {
        displayList = false;
        update();
    }

    @Override
    public void update() {
        list.update(getX(), getY());
    }

    @Override
    public void hideAll() {
        displayList = false;
    }
}
