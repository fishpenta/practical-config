package config.practical.widgets.sliders;

import config.practical.utilities.Constants;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ConfigDouble extends Slider {
    private final double step, min, max;
    private final Supplier<Double> supplier;
    private final Consumer<Double> consumer;

    /**
     * @param message  the text that will be displayed
     * @param supplier a supplier to get the current value
     * @param consumer a consumer to set the current value
     * @param step     the steps inbetween each value
     * @param min      the min value
     * @param max      the max value
     */
    @SuppressWarnings("unused")
    public ConfigDouble(Component message, Supplier<Double> supplier, Consumer<Double> consumer, double step, double min, double max) {
        super(message);
        this.supplier = supplier;
        this.consumer = consumer;
        this.step = step;
        this.min = min;
        this.max = max;
        this.updateThumbPosWithDelta((float) (supplier.get() - min) / (float) (max - min));
    }

    @Override
    String getCurrValue() {
        return Constants.DECIMAL_FORMAT.format(supplier.get());
    }


    @Override
    void updateValue(float delta) {
        double accurateValue;
        if (delta == 1.0) {
            accurateValue = max;
        } else {
            accurateValue = ((max - min) * delta) + min;
            accurateValue -= accurateValue % step;
        }
        consumer.accept(accurateValue);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            consumer.accept(Math.max(supplier.get() - step, min));
            updateThumbPosWithDelta((float)((supplier.get() - min) / (max - min)));
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            consumer.accept(Math.min(supplier.get() + step, max));
            updateThumbPosWithDelta((float)((supplier.get() - min) / (max - min)));
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        this.defaultButtonNarrationText(narration);
    }
}
