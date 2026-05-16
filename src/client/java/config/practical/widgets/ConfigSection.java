package config.practical.widgets;

import config.practical.utilities.DrawHelper;
import config.practical.widgets.abstracts.ConfigChild;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConfigSection extends AbstractContainerWidget {

    private static final int PADDING = 3;

    private static final int ITEM_MARGIN = 2;
    private static final int TEXT_HEIGHT = 16;
    private static final int DEFAULT_WIDTH = 150;

    private static final int BACKGROUND_COLOR = 0xaa222222;
    private static final int TEXT_COLOR = 0xffffffff;

    private final ArrayList<AbstractWidget> children;

    public ConfigSection(Component text) {
        super(0, 0, DEFAULT_WIDTH, TEXT_HEIGHT + PADDING, text);
        children = new ArrayList<>();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @SuppressWarnings("unused")
    public void add(AbstractWidget widget) {
        if (widget == null) return;
        children.add(widget);
        if (widget instanceof ConfigChild) return;

        height += widget.getHeight() + ITEM_MARGIN;
        width = Math.max(width, widget.getWidth() + PADDING * 2);
    }

    @Override
    protected int contentHeight() {
        return height;
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();

        graphics.enableScissor(x, y, x + width, y + height);
        DrawHelper.drawBackground(graphics, x, y, width, height, BACKGROUND_COLOR);

        Font font = Minecraft.getInstance().font;
        Component text = getMessage();
        graphics.drawString(font, text, (width - font.width(text)) / 2 + x, y + PADDING, TEXT_COLOR, true);
        graphics.disableScissor();

        for (AbstractWidget widget : children) {
            widget.render(graphics, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        update();
    }

    public void update() {
        int x = getX();
        int y = getY();

        int currY = y + TEXT_HEIGHT;
        int halfWidth = width / 2;

        for (AbstractWidget widget : children) {
            width = Math.max(width, widget.getWidth() + PADDING * 2);
        }

        for (AbstractWidget widget : children) {
            if (widget instanceof ConfigChild) continue;

            int width = x + halfWidth - widget.getWidth() / 2;
            int height = currY;
            widget.setPosition(width, height);

            currY += widget.getHeight() + ITEM_MARGIN;
        }
    }

    public void hideChildComponents(boolean keepFocused) {
        for (AbstractWidget widget : children) {
            if (widget.isFocused() && keepFocused) continue;

            if (widget instanceof ConfigParent parent) {
                parent.hideAll();
                parent.update();

            } else if (widget instanceof ConfigSection section) {
                section.hideChildComponents(keepFocused);
            }
        }
    }

    public ArrayList<AbstractWidget> getAllWidgets() {

        ArrayList<AbstractWidget> temp = new ArrayList<>();

        for (AbstractWidget widget : children) {

            if (widget instanceof ConfigParent parent) {
                temp.addAll(parent.getAllWidgets());

            } else if (widget instanceof ConfigSection section) {
                temp.addAll(section.getAllWidgets());
            }
        }

        return temp;
    }

    public boolean contains(String term) {
        String message = this.getMessage().getString().toLowerCase();
        if (message.contains(term)) return true;

        for (AbstractWidget widget : children) {
            if (widget instanceof ConfigSection section) {
                if (section.contains(term)) return true;
            } else {
                message = widget.getMessage().getString().toLowerCase();
                if (message.contains(term)) return true;
            }
        }

        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
