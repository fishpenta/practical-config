package config.practical.hud;

import com.mojang.blaze3d.platform.Window;
import config.practical.Config;
import config.practical.utilities.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ComponentEditScreen extends Screen {

    private static final Identifier CROSSHAIR = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "crosshair");

    private static final Component TITLE = Component.literal("");

    private static final double MOVE_SPEED = 2;
    private static final int CROSSHAIR_SIZE = 4;
    private static final float SCALE_FACTOR = 0.02f;
    private static final int gridColor = 0xff004444;

    private static final String[] KEYS = {
            "r",
            "r + ctrl + shift",
            "g + ctrl",
            "s",
            "h + ctrl",
            "v + ctrl",
            "tab"
    };
    private static final String[] INFO = {
            "reset component",
            "reset All editable components",
            "toggle grid",
            "snap to closet grid",
            "center selected horizontally",
            "center selected vertically",
            "cycle selected component"
    };

    private static final ArrayList<HUDComponent> ALL_COMPONENTS = new ArrayList<>();

    private final ArrayList<HUDComponent> components;

    private final Screen parent;

    private HUDComponent selected;
    private boolean isDragging = false;


    public ComponentEditScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.components = new ArrayList<>();

        ALL_COMPONENTS.forEach(component -> {
            if (component.editable()) {
                components.add(component);
            }
        });
    }

    public static void addComponent(HUDComponent component) {
        if (component == null) return;
        ALL_COMPONENTS.add(component);
    }

    @Override
    protected void init() {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selected != null) {
            float newScale = selected.getScale() + (float) Math.signum(verticalAmount) * SCALE_FACTOR;
            selected.setScale(newScale);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        int x = (int) event.x();
        int y = (int) event.y();

        selected = null;
        for (HUDComponent component : components) {

            if (component.inBounds(x, y)) {
                selected = component;
                isDragging = true;
                break;
            }
        }
        return super.mouseClicked(event, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {

        if (isDragging && selected != null) {
            Window window = minecraft.getWindow();
            selected.move(offsetX / window.getGuiScaledWidth(), offsetY / window.getGuiScaledHeight());
        }

        return super.mouseDragged(event, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        isDragging = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int modifiers = event.modifiers();
        int keyCode = event.key();

        boolean ctrlIsPressed = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        boolean shiftIsPressed = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        if (keyCode == GLFW.GLFW_KEY_R && ctrlIsPressed && shiftIsPressed) {
            for (HUDComponent component : components) {
                component.reset();
            }
        }

        if (keyCode == GLFW.GLFW_KEY_G && ctrlIsPressed) {
            Config.gridEnabled = !Config.gridEnabled;
            Config.manager.save();
        }

        if (keyCode == GLFW.GLFW_KEY_TAB) {
            if (selected == null) selected = components.getFirst();
            else {
                int index = components.indexOf(selected);
                index = (index + 1) % components.size();
                selected = components.get(index);
            }
        }

        if (selected != null) {
            if (keyCode == GLFW.GLFW_KEY_R) {
                selected.reset();
            }

            if (keyCode == GLFW.GLFW_KEY_H && ctrlIsPressed) {
                selected.centerHorizontally(minecraft.getWindow());
            }

            if (keyCode == GLFW.GLFW_KEY_V && ctrlIsPressed) {
                selected.centerVertically(minecraft.getWindow());
            }

            if (keyCode == GLFW.GLFW_KEY_LEFT) {
                selected.move(-MOVE_SPEED / minecraft.getWindow().getGuiScaledWidth(), 0);
            }

            if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                selected.move(MOVE_SPEED / minecraft.getWindow().getGuiScaledWidth(), 0);
            }

            if (keyCode == GLFW.GLFW_KEY_UP) {
                selected.move(0, -MOVE_SPEED / minecraft.getWindow().getGuiScaledHeight());
            }

            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                selected.move(0, MOVE_SPEED / minecraft.getWindow().getGuiScaledHeight());
            }

            if (keyCode == GLFW.GLFW_KEY_S) {
                selected.snapToGrid(minecraft.getWindow());

            }
        }
        return super.keyPressed(event);

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        Font font = this.font;

        if (Config.gridEnabled) {

            Matrix3x2fStack stack = graphics.pose();
            stack.pushMatrix();
            stack.translate(-0.5f, -0.5f);
            for (int i = width / 2; i >= 0; i -= Constants.GRID_SIZE) {
                graphics.vLine(i, 0, height, gridColor);
            }

            for (int i = height / 2; i >= 0; i -= Constants.GRID_SIZE) {
                graphics.hLine(0, width, i, gridColor);
            }

            for (int i = width / 2; i < width; i += Constants.GRID_SIZE) {
                graphics.vLine(i, 0, height, gridColor);
            }

            for (int i = height / 2; i < height; i += Constants.GRID_SIZE) {
                graphics.hLine(0, width, i, gridColor);
            }

            stack.popMatrix();
        }


        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR, width / 2 - CROSSHAIR_SIZE, height / 2 - CROSSHAIR_SIZE, CROSSHAIR_SIZE * 2, CROSSHAIR_SIZE * 2, Constants.WHITE_COLOR);

        int longest = 0;
        for (String str : INFO) {
            longest = Math.max(font.width(str), longest);
        }

        for (int i = 0; i < INFO.length; i++) {
            graphics.drawString(font, INFO[i], 1, height - ((font.lineHeight + 1) * (INFO.length - i)), 0xffffffff, true);
        }

        for (int i = 0; i < KEYS.length; i++) {
            graphics.drawString(font, KEYS[i], 7 + longest, height - ((font.lineHeight + 1) * (KEYS.length - i)), 0xffffffff, true);
        }

        if (selected != null) {
            selected.renderHighlight(graphics);
        }

        for (HUDComponent component : components) {
            component.renderIgnoreConditions(graphics);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
