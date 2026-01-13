package net.mebahel.antiquebeasts.util.predicate;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public final class PredicateBridge {
    private PredicateBridge() {}

    public enum RuntimePlatform {
        FABRIC,
        FORGE,
        NEOFORGE
    }

    private static final RuntimePlatform PLATFORM = detectPlatform();

    /**
     * Register a model predicate in a Connector-safe way.
     * - Fabric: uses ModelPredicateProviderRegistry
     * - Forge/NeoForge (incl Connector): uses vanilla ItemProperties.register via reflection+proxy
     */
    public static void register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
        if (item == null) return;

        if (PLATFORM == RuntimePlatform.FORGE || PLATFORM == RuntimePlatform.NEOFORGE) {
            try {
                registerVanillaItemProperty(item, id, (stack, level, entity, seed) ->
                        provider.unclampedCall((ItemStack) stack, null, (net.minecraft.entity.LivingEntity) entity, seed)
                );
            } catch (Throwable t) {
                throw (t instanceof RuntimeException re) ? re : new RuntimeException(t);
            }
            return;
        }

        ModelPredicateProviderRegistry.register(item, id, provider);
    }

    // =========================
    // Platform detection
    // =========================

    private static RuntimePlatform detectPlatform() {
        ClassLoader cl = PredicateBridge.class.getClassLoader();

        // NeoForge first
        if (classExists("net.neoforged.fml.ModList", cl)) {
            return RuntimePlatform.NEOFORGE;
        }

        // Forge / Connector
        if (classExists("net.minecraftforge.fml.ModList", cl)) {
            return RuntimePlatform.FORGE;
        }

        return RuntimePlatform.FABRIC;
    }

    private static boolean classExists(String name, ClassLoader cl) {
        try {
            Class.forName(name, false, cl);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    // =========================
    // Forge/NeoForge bridge (vanilla ItemProperties)
    // =========================

    @FunctionalInterface
    private interface FourArgFloat {
        float call(@Nullable Object stack, @Nullable Object level, @Nullable Object entity, int seed) throws Throwable;
    }

    /**
     * Registers net.minecraft.client.renderer.item.ItemProperties.register(Item, ResourceLocation, ClampedItemPropertyFunction)
     * via reflection + dynamic proxy (no compile-time Forge deps).
     */
    private static void registerVanillaItemProperty(Item item, Identifier id, FourArgFloat fn) throws Throwable {
        Class<?> itemPropertiesClz = Class.forName("net.minecraft.client.renderer.item.ItemProperties");
        Class<?> resourceLocationClz = Class.forName("net.minecraft.resources.ResourceLocation");
        Class<?> clampedFnClz = Class.forName("net.minecraft.client.renderer.item.ClampedItemPropertyFunction");

        Object rl = resourceLocationClz
                .getConstructor(String.class, String.class)
                .newInstance(id.getNamespace(), id.getPath());

        Object proxy = Proxy.newProxyInstance(
                PredicateBridge.class.getClassLoader(),
                new Class<?>[]{clampedFnClz},
                (p, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(p, args);
                    }

                    if (method.getReturnType() == float.class && method.getParameterCount() == 4) {
                        Object stack = args[0];
                        Object level = args[1];
                        Object entity = args[2];
                        int seed = (int) args[3];
                        return fn.call(stack, level, entity, seed);
                    }

                    throw new UnsupportedOperationException("Unexpected method on ClampedItemPropertyFunction: " + method);
                }
        );

        Method register = Arrays.stream(itemPropertiesClz.getDeclaredMethods())
                .filter(m -> m.getParameterCount() == 3)
                .filter(m -> m.getParameterTypes()[1].equals(resourceLocationClz))
                .filter(m -> m.getParameterTypes()[2].equals(clampedFnClz))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException(
                        "ItemProperties.register(Item, ResourceLocation, ClampedItemPropertyFunction) not found"
                ));

        register.setAccessible(true);
        register.invoke(null, item, rl, proxy);
    }
}
