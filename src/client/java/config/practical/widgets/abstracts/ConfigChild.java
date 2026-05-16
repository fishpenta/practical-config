package config.practical.widgets.abstracts;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class ConfigChild extends AbstractWidget {

    private final int normalWidth;
    private final ConfigParent parent;

    public ConfigChild(ConfigParent parent, int width, int height) {
        super(0, 0, 0, height, Component.empty());
        this.normalWidth = width;
        this.parent = parent;
    }

    public void update(int x, int y) {
        if (showWidget()) {
            setWidth(normalWidth);
        } else {
            setWidth(0);
        }

        updatePosition(x, y);
    }

    @Override
    public boolean isHoveredOrFocused() {
        return super.isHoveredOrFocused();
    }

    public int getNormalWidth() {return normalWidth;}

    protected abstract boolean showWidget();

    protected abstract void updatePosition(int x, int y);

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
