package config.practical;

import config.practical.widgets.ConfigSection;
import config.practical.widgets.abstracts.ConfigChild;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigScroll extends AbstractContainerWidget {

    private static final int ITEM_MARGIN = 4;
    private static final int SLIDER_X_OFFSET = 24;

    private final ArrayList<AbstractWidget> children;
    private final ArrayList<AbstractWidget> childWidgets;
    private int contentHeight;
    private final int maxItemWidth;

    public ConfigScroll(int x, int y, int width, int height, int maxItemWidth) {
        super(x, y, width, height, Component.empty());
        this.children = new ArrayList<>();
        this.childWidgets = new ArrayList<>();
        this.contentHeight = 0;
        this.maxItemWidth = maxItemWidth;
        update();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    public void add(AbstractWidget widget) {
        if (widget == null) return;
        children.add(widget);

        if (widget instanceof ConfigChild child) {
            childWidgets.add(child);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        hideChildComponents(true);

    }

    @Override
    protected int contentHeight() {
        return contentHeight;
    }

    @Override
    protected double scrollRate() {
        return 10;
    }

    @Override
    protected int scrollBarX() {
        return (width + maxItemWidth) / 2 + SLIDER_X_OFFSET;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.renderScrollbar(graphics, mouseX, mouseY);

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();


        graphics.enableScissor(x, y, x + width, y + height);
        for (AbstractWidget widget : children) {
            if (widget instanceof ConfigChild) continue;
            widget.render(graphics, mouseX, mouseY, deltaTicks);
        }
        for (AbstractWidget widget : childWidgets) {
            widget.render(graphics, mouseX, mouseY, deltaTicks);
        }
        graphics.disableScissor();
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
        update();
    }

    public void update() {
        int x = getX();
        int y = getY();

        int currY = y - (int) scrollAmount();

        int halfWidth = width / 2;

        for (AbstractWidget widget : children) {

            if (widget instanceof ConfigChild) continue;

            widget.setPosition(x + halfWidth - widget.getWidth() / 2, currY);
            currY += widget.getHeight() + ITEM_MARGIN;

            if (widget instanceof ConfigParent parent) {
                parent.update();
            }

            if (widget instanceof ConfigSection section) {
                section.hideChildComponents(false);
            }
        }

        //contentHeight = currY - y + (int) getScrollY();

        int highestY = 0;
        for (AbstractWidget widget : children) {
            highestY = Math.max(highestY, (widget.getY() + widget.getHeight()));
        }

        //to fix ConfigChild components being out of bounds
        contentHeight = highestY + (int) scrollAmount();
    }

    public void hideChildComponents(boolean keepFocused) {
        for (AbstractWidget widget : children) {
            if (widget.isFocused() && !(widget instanceof ConfigSection) && keepFocused) {
                continue;
            }

            if (widget instanceof ConfigParent parent) {
                if (parent.hasSelectedComponent()) continue;
                parent.hideAll();
                parent.update();
            }

            if (widget instanceof ConfigSection section) {
                section.hideChildComponents(keepFocused);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
