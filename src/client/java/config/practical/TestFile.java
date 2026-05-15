package config.practical;

import config.practical.category.ConfigCategory;
import config.practical.data.SoundData;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigManager;
import config.practical.manager.ConfigValue;
import config.practical.utilities.Constants;
import config.practical.widgets.ConfigBool;
import config.practical.widgets.ConfigSection;
import config.practical.widgets.ConfigString;
import config.practical.widgets.ConfigTextArea;
import config.practical.widgets.color.ConfigColor;
import config.practical.widgets.options.ConfigOptions;
import config.practical.widgets.sliders.ConfigDouble;
import config.practical.widgets.sliders.ConfigFloat;
import config.practical.widgets.sliders.ConfigInt;
import config.practical.widgets.sound.ConfigSound;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * This is a test file where there is a kind of
 * prebuilt config and screen, please copy from this
 * to learn how to make a screen and save variables
 */
class TestFile {

    enum Directions {
        NORTH("North"), SOUTH ("South"), EAST("East"), WEST("West");

        final String name;

        Directions(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @ConfigValue
    public static SoundData someSound = new SoundData(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);

    @ConfigValue
    public static Identifier identifier = Identifier.of("some_identifier");

    @ConfigValue
    public static boolean someBoolean = false;

    @ConfigValue
    public static int someColor = 0xff22ff22;

    @ConfigValue
    public static int noTransparencyColor = 0xff22ff22;

    @ConfigValue
    public static int someInt = 5;

    @ConfigValue
    public static float someFloat = 0.5f;

    @ConfigValue
    public static double someDouble = 0.5;

    @ConfigValue
    public static String someString = "Hello world";

    @ConfigValue
    public static Directions someEnum = Directions.NORTH;

    @ConfigValue
    public static HUDComponent myComponent = new HUDComponent(0, 0, 100, 50, 1, () -> true, (component, context) -> {
        int x = component.getScaledX();
        int y = component.getScaledY();
        context.fill(x, y, x + component.getWidth(), y + component.getHeight(), noTransparencyColor);
    });

    @ConfigValue
    public static HUDComponent myOtherComponent = new HUDComponent(0, 0, 100, 20, 1, () -> true, (component, context) -> {
        int x = component.getScaledX();
        int y = component.getScaledY();

        Text text = Text.literal("This is a string");
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int centered = (component.getWidth() - textRenderer.getWidth(text)) / 2;
        //context.fill(x, y, x + component.getWidth(), y + component.getHeight(), someColor);
        context.drawText(textRenderer, text, x + centered, y + (component.getHeight() - Constants.TEXT_HEIGHT) / 2, Constants.WHITE_COLOR, true);
    });


    public static final ConfigManager manager = new ConfigManager("./config/test-" + Constants.NAMESPACE + ".json",
            List.of(TestFile.class));

    private static KeyBinding openConfig;


    public static void register() {
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "opens a Config menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if (openConfig.wasPressed()) {
                client.setScreen(createScreen(null));
            }
        }));

        manager.load();
    }

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(Text.literal("This is the title"), parent, manager);
        ConfigCategory category = new ConfigCategory("This is a category");
        category.add(new ConfigInt(Text.literal("This is a int slider"), () -> someInt, newInt -> someInt = newInt, 1, 0, 10));
        category.add(new ConfigFloat(Text.literal("This is a float slider"), () -> someFloat, newFloat -> someFloat = newFloat, 0.1f, 0, 1));
        category.add(new ConfigDouble(Text.literal("This is a double slider"), () -> someDouble, newDouble -> someDouble = newDouble, 0.1, 0, 1));
        category.add(new ConfigString(Text.literal("This is a string"), () -> someString, newString -> someString = newString));
        category.add(new ConfigOptions<>(Text.literal("This is an enum"),Directions.values(), () -> someEnum, newEnum -> someEnum = newEnum));

        ConfigSection section = new ConfigSection(Text.literal("This is a section"));
        section.add(new ConfigBool(Text.literal("This is a bool"), () -> someBoolean, bool -> someBoolean = bool));
        section.add(new ConfigSound(Text.literal("This is a sound"), someSound));
        section.add(new ConfigTextArea(
                "To be, or not to be, that is the question:\n" +
                "Whether 'tis nobler in the mind to suffer\n" +
                "The slings and arrows of outrageous fortune,\n" +
                "Or to take arms against a sea of troubles\n" +
                "And by opposing end them. To die—to sleep,\n" +
                "No more; and by a sleep to say we end\n" +
                "The heart-ache and the thousand natural shocks\n" +
                "That flesh is heir to: 'tis a consummation\n"));
        category.add(section);

        ConfigSection colorSection = new ConfigSection(Text.literal("Color selectors"));
        colorSection.add(new ConfigColor(Text.literal("Color selection"), () -> someColor, color -> someColor = color, "some-identifier", true));
        colorSection.add(new ConfigColor(Text.literal("Color with max alpha"), () -> noTransparencyColor, color -> noTransparencyColor = color, "another-identifier", false));
        category.add(colorSection);

        screen.addCategory(category);

        ConfigCategory category2 = new ConfigCategory("This is another category");
        category2.add(new ConfigTextArea("Some text"));

        screen.addCategory(category2);

        return screen;
    }
}
