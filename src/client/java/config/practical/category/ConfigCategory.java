package config.practical.category;

import config.practical.widgets.ConfigSection;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;

public class ConfigCategory {
    public final String name;
    public final ArrayList<AbstractWidget> widgets;

    /**
     * @param name The name of the category
     */
    public ConfigCategory(String name) {
        this.name = name;
        widgets = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public void add(AbstractWidget widget) {
        if (widget == null) return;
        widgets.add(widget);
    }

    public ArrayList<AbstractWidget> searchWidgets(String term) {

        ArrayList<AbstractWidget> temp = new ArrayList<>();

        for (AbstractWidget widget : widgets) {
            if (widget instanceof ConfigSection section) {
                if (section.contains(term)) {
                    temp.addAll(section.getAllWidgets());
                    temp.add(widget);
                }
            } else {

                String message = widget.getMessage().getString().toLowerCase();
                if (message.contains(term)) {

                    if (widget instanceof ConfigParent parent) {
                        temp.addAll(parent.getAllWidgets());
                    }

                    temp.add(widget);
                }
            }

        }

        return temp;
    }
}
