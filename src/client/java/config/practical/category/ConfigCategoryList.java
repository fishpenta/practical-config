package config.practical.category;

import config.practical.ConfigurableScreen;
import config.practical.utilities.DrawHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class ConfigCategoryList extends AbstractWidget {

    private static final int WIDTH = 120;
    private static final int CATEGORY_HEIGHT = 26;

    private static final int PADDING_X = 4;

    private static final int BACKGROUND_COLOR = 0xff222222;
    private static final int TEXT_COLOR = 0xffffffff;

    private static final int SELECTED_COLOR = 0xffffffff;
    private static final int UNSELECTED_COLOR = 0xff000000;

    private final ArrayList<ConfigCategory> categories;
    private final ConfigurableScreen screen;

    private ConfigCategory selected;

    public ConfigCategoryList(ConfigurableScreen screen, int x, int y) {
        super(x, y, WIDTH, CATEGORY_HEIGHT, Component.literal(""));
        this.categories = new ArrayList<>();
        this.screen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {

        int x = getX();
        int y = getY();

        graphics.enableScissor(x, y, x + width, y + height);
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        Font font = screen.getFont();

        int textPadding = (CATEGORY_HEIGHT - font.lineHeight) / 2;

        for (int i = 0; i < categories.size(); i++) {

            ConfigCategory category = categories.get(i);
            DrawHelper.drawBorder(graphics, x, y + i * CATEGORY_HEIGHT, width, CATEGORY_HEIGHT, (category == selected ? SELECTED_COLOR : UNSELECTED_COLOR));
            String name = category.name;

            graphics.drawString(font, name, x + PADDING_X, y + i * CATEGORY_HEIGHT + textPadding, TEXT_COLOR, true);
        }

        graphics.disableScissor();
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubled) {
        int index = (int) ((event.y() - getY()) / CATEGORY_HEIGHT);

        if (index < categories.size() && index > -1) {
            selected = categories.get(index);
            screen.update();
        }
    }


    public void addCategory(ConfigCategory category) {
        if (category == null) return;
        if (selected == null) selected = category;
        categories.add(category);

        height = CATEGORY_HEIGHT * categories.size();
    }

    public ArrayList<AbstractWidget> searchWidgets(String term) {
        if (term.isEmpty()) {
            return selected.searchWidgets(term);
        }

        ArrayList<AbstractWidget> temp = new ArrayList<>();

        for (ConfigCategory category: categories) {
            temp.addAll(category.searchWidgets(term));
        }

        return temp;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
