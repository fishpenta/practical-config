package config.practical.hud;

import config.practical.Practicalconfig;
import config.practical.utilities.Constants;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

public class HUDComponent implements HudElement {

    private static int componentCount = 0;

    private static final float MIN_SCALE = 0.2f;
    private static final float MAX_SCALE = 5f;

    private static final int HIGHLIGHT_COLOR = 0x99ffffff;
    private static final int HIGHLIGHT_MARGIN = 2;

    private static final Identifier AFTER_IDENTIFIER = Identifier.of("boss_bar");

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private transient final double defaultX, defaultY;
    private transient final float defaultScale;

    private transient int width, height;

    private double x, y;
    private float scale;
    private final String info;
    private transient final ConditionSupplier conditionSupplier;
    private transient final RenderSupplier renderSupplier;
    private transient final EditSupplier editSupplier;

    /**
     * x, y goes from 0 to 1 and get scaled
     * up using the Window with getScaledWidth
     * and get getScaledHeight
     *
     * @param x                 0 to 1
     * @param y                 0 to 1
     * @param width             int
     * @param height            int
     * @param scale             a scale that's between MIN_SCALE and MAX_SCALE
     * @param info              text info that will display when its selected
     * @param conditionSupplier the condition to render
     * @param renderSupplier    the function that is used to render it
     */
    public HUDComponent(double x, double y, int width, int height, float scale, String info, @NotNull ConditionSupplier conditionSupplier, @NotNull RenderSupplier renderSupplier, @NotNull EditSupplier editSupplier) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
        this.info = info;

        defaultX = x;
        defaultY = y;
        defaultScale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);

        this.conditionSupplier = conditionSupplier;
        this.renderSupplier = renderSupplier;
        this.editSupplier = editSupplier;

        //HudElementRegistry.addLast(Identifier.of(Practicalconfig.MOD_ID, "component-" + componentCount), this);
        HudElementRegistry.attachElementAfter(AFTER_IDENTIFIER, Identifier.of(Practicalconfig.MOD_ID, "component-" + componentCount), this);
        componentCount++;
        ComponentEditScreen.addComponent(this);
    }

    //backwards compatibility
    @SuppressWarnings("unused")
    public HUDComponent(double x, double y, int width, int height, float scale, String info, @NotNull ConditionSupplier conditionSupplier, @NotNull RenderSupplier renderSupplier) {
        this(x, y, width, height, scale, info, conditionSupplier, renderSupplier, () -> true);
    }

    //backwards compatibility
    @SuppressWarnings("unused")
    public HUDComponent(double x, double y, int width, int height, float scale, @NotNull ConditionSupplier conditionSupplier, @NotNull RenderSupplier renderSupplier) {
        this(x, y, width, height, scale, "", conditionSupplier, renderSupplier, () -> true);
    }

    public void reset() {
        x = defaultX;
        y = defaultY;
        scale = defaultScale;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void setScale(float scale) {
        this.scale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public void copyAttributes(HUDComponent component) {
        this.x = component.x;
        this.y = component.y;
        this.scale = component.scale;
    }

    public float getScale() {
        return scale;
    }

    public int getScaledX() {
        int screenWidth = client.getWindow().getScaledWidth();
        return (int) (x * screenWidth / scale);
    }

    public int getScaledY() {
        int screenHeight = client.getWindow().getScaledHeight();
        return (int) (y * screenHeight / scale);
    }

    @SuppressWarnings("unused")
    public int getWidth() {
        return width;
    }

    @SuppressWarnings("unused")
    public int getHeight() {
        return height;
    }

    @SuppressWarnings("unused")
    public void setWidth(int width) {
        this.width = width;
    }

    @SuppressWarnings("unused")
    public void setHeight(int height) {
        this.height = height;
    }

    @SuppressWarnings("unused")
    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void centerHorizontally(Window window) {
        int windowWidth = window.getScaledWidth();
        x = (windowWidth - width * scale) / (windowWidth * 2.0);
    }

    public void centerVertically(Window window) {
        int windowHeight = window.getScaledHeight();
        y = (windowHeight - height * scale) / (windowHeight * 2.0);
    }

    public double calcXSnap(double scaledX, Window window) {
        int windowWidth = window.getScaledWidth();

        double diff = scaledX - (windowWidth / 2.0);

        double mod = (Math.abs(diff) % Constants.GRID_SIZE);
        if (mod > Constants.GRID_SIZE / 2.0) {
            mod -= Constants.GRID_SIZE;
        }

        if (diff >= 0) {
            return (scaledX - mod) / windowWidth;
        } else {
            return (scaledX + mod) / windowWidth;
        }
    }

    public double calcYSnap(double scaledY, Window window) {
        int windowHeight = window.getScaledHeight();

        double diff = scaledY - (windowHeight / 2.0);

        double mod = (Math.abs(diff) % Constants.GRID_SIZE);
        if (mod > Constants.GRID_SIZE / 2.0) {
            mod -= Constants.GRID_SIZE;
        }

        if (diff >= 0) {
            return (scaledY - mod) / windowHeight;
        } else {
            return (scaledY + mod) / windowHeight;
        }
    }

    public void snapToGrid(Window window) {
        int windowWidth = window.getScaledWidth();
        int windowHeight = window.getScaledHeight();
        double scaledX = x * windowWidth;
        double scaledY = y * windowHeight;

        x = calcXSnap(scaledX, window);
        y = calcYSnap(scaledY, window);

    }

    public boolean editable() {
        return editSupplier.shouldBeEditable();
    }

    public boolean inBounds(int mouseX, int mouseY) {
        double screenX = x * client.getWindow().getScaledWidth();
        double screenY = y * client.getWindow().getScaledHeight();

        return screenX <= mouseX && mouseX <= screenX + (width * scale)
                && screenY <= mouseY && mouseY <= screenY + (height * scale);
    }

    public void renderIgnoreConditions(DrawContext context) {
        Matrix3x2fStack stack = context.getMatrices();
        stack.pushMatrix();
        stack.scale(scale, scale);
        renderSupplier.render(this, context);
        stack.popMatrix();
    }

    public void renderHighlight(DrawContext context) {
        Matrix3x2fStack stack = context.getMatrices();
        stack.pushMatrix();
        stack.scale(scale, scale);
        int x = getScaledX();
        int y = getScaledY();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.fill(x - HIGHLIGHT_MARGIN, y - HIGHLIGHT_MARGIN, x + width + HIGHLIGHT_MARGIN, y + height + HIGHLIGHT_MARGIN, HIGHLIGHT_COLOR);
        context.drawText(textRenderer, info, x + (width - textRenderer.getWidth(info)) / 2, y - textRenderer.fontHeight - 2, 0xffffffff, true);
        stack.popMatrix();
    }

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!editSupplier.shouldBeEditable() || !conditionSupplier.shouldRender()) return;
        Matrix3x2fStack stack = context.getMatrices();
        stack.pushMatrix();
        stack.scale(scale, scale);
        renderSupplier.render(this, context);
        stack.popMatrix();
    }

    public interface ConditionSupplier {
        /**
         * Will be called when the function render is called
         * Make use of it so the component only renders
         * when you want it to render
         *
         * @return true if it should render, else false
         */
        boolean shouldRender();
    }

    public interface RenderSupplier {
        /**
         * Will be called in function render
         * if ConditionSupplier returns true
         * NOTE: component is the component itself
         * and scaledX and scaledY should be used
         * for the x and y position
         *
         * @param component the component itself
         * @param context   DrawContext
         */
        void render(HUDComponent component, DrawContext context);
    }

    public interface EditSupplier {
        /**
         * Used to determine if the component
         * should render while the user is in the
         * ComponentEditScreen
         *
         */
        boolean shouldBeEditable();
    }
}
