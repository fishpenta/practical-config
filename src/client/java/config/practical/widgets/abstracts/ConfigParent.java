package config.practical.widgets.abstracts;

import config.practical.widgets.ConfigSection;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigParent extends AbstractWidget implements ContainerEventHandler {

    private final ArrayList<ConfigChild> widgets = new ArrayList<>();

    public ConfigParent(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return null;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {

    }

    public ArrayList<AbstractWidget> getAllWidgets() {

        ArrayList<AbstractWidget> temp = new ArrayList<>();

        for (AbstractWidget widget: widgets) {

            if (widget instanceof ConfigParent parent) {
                temp.addAll(parent.getAllWidgets());

            } else if (widget instanceof ConfigSection section) {
                temp.addAll(section.getAllWidgets());
            }

            temp.add(widget);
        }

        return temp;
    }

    public void addChild(ConfigChild widget) {
        if (widget == null) return;
        widgets.add(widget);
    }

    public void update() {
        int x = getX();
        int y = getY();

        for(ConfigChild widget: widgets) {
            widget.update(x, y);
        }
    }

    public abstract void hideAll();

    public boolean hasSelectedComponent() {
        if (this.isHoveredOrFocused()) return true;

        for (ConfigChild widget: widgets) {
            if (widget.isHoveredOrFocused()) return true;
        }

        return false;
    }
}
