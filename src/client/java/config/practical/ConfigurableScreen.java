package config.practical;


import com.mojang.blaze3d.platform.Window;
import config.practical.category.ConfigCategory;
import config.practical.category.ConfigCategoryList;
import config.practical.manager.ConfigManager;
import config.practical.utilities.Constants;
import config.practical.widgets.abstracts.ConfigParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class ConfigurableScreen extends Screen {

    private static final int WIDGET_X_OFFSET = 4;

    private static final int LIST_Y_OFFSET = 50;

    private static final int CATEGORY_Y_OFFSET = 10;

    private static final int TITLE_COLOR = 0xffffffff;
    private static final float TITLE_SCALAR = 1.5f;
    private static final int TITLE_Y_OFFSET = 20;

    private final ConfigSearch search;
    private final ConfigCategoryList categories;
    private final ConfigScroll scroll;
    private final ConfigHudEdit hudEdit;

    private final Screen parent;
    private final ConfigManager manager;

    /**
     * The main screen of practical-config
     * To add widgets make a ConfigCategory
     * and add widgets to the category which
     * you then register to the screen
     * @param title The title that will be displayed
     * @param parent A screen that will be the parent of this
     * @param manager The configManager that should save when the screen is closed
     */
    public ConfigurableScreen(Component title, Screen parent, @NotNull ConfigManager manager) {
        super(title);

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        scroll = new ConfigScroll(0, LIST_Y_OFFSET, window.getGuiScaledWidth(), window.getGuiScaledHeight() - LIST_Y_OFFSET, Constants.WIDGET_WIDTH + 100);
        search = new ConfigSearch(client.font, WIDGET_X_OFFSET, (LIST_Y_OFFSET - ConfigSearch.HEIGHT) / 2, this::updateScroll);
        categories = new ConfigCategoryList(this, WIDGET_X_OFFSET, LIST_Y_OFFSET + CATEGORY_Y_OFFSET);
        hudEdit = new ConfigHudEdit(this, window.getGuiScaledWidth() - ConfigHudEdit.WIDTH - WIDGET_X_OFFSET, (LIST_Y_OFFSET - ConfigHudEdit.HEIGHT) / 2);

        this.parent = parent;
        this.manager = manager;
    }

    @SuppressWarnings("unused")
    public ConfigurableScreen(Component title, @NotNull ConfigManager manager) {
        this(title, null, manager);
    }

    @Override
    protected void init() {
        addRenderableWidget(search);
        addRenderableWidget(categories);
        addRenderableWidget(scroll);
        addRenderableWidget(hudEdit);
        update();
    }

    @SuppressWarnings("unused")
    public void addCategory(ConfigCategory category) {
        categories.addCategory(category);
    }

    public void update() {
        updateScroll("");
    }

    public void updateScroll(String searchTerm) {
        scroll.setFocused(null);
        scroll.children().clear();

        for (AbstractWidget widget: categories.searchWidgets(searchTerm.toLowerCase())) {
            if (widget instanceof ConfigParent configParent) {
                configParent.hideAll();
                configParent.update();
            }
            scroll.add(widget);
        }

        scroll.setScrollAmount(0);
        scroll.hideChildComponents(false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);

        float centerX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - (this.font.width(this.title) * TITLE_SCALAR)) / 2;
        Matrix3x2fStack stack = graphics.pose();
        stack.pushMatrix();
        stack.translate(centerX, TITLE_Y_OFFSET);
        stack.scale(TITLE_SCALAR, TITLE_SCALAR);
        graphics.drawString(this.font, this.title, 0, 0, TITLE_COLOR, true);
        stack.popMatrix();
    }

    @Override
    public void onClose() {
        manager.save();
        this.minecraft.setScreen(this.parent);
    }
}
