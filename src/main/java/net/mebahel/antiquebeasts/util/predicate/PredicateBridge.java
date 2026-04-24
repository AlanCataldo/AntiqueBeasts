package net.mebahel.antiquebeasts.util.predicate;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public final class PredicateBridge {
    private PredicateBridge() {}

    public enum RuntimePlatform {
        FABRIC,
        FORGE,
        NEOFORGE,
        CONNECTOR
    }

    @FunctionalInterface
    public interface UniversalPredicate {
        float call(@Nullable Object stack, @Nullable Object world, @Nullable Object entity, int seed) throws Throwable;
    }

    private static final ClassLoader CL = PredicateBridge.class.getClassLoader();
    private static final RuntimePlatform PLATFORM = detectPlatform();

    public static void register(Item item, Identifier id, UniversalPredicate predicate) {
        if (item == null || id == null || predicate == null) return;

        if (shouldUseVanillaItemProperties()) {
            try {
                registerVanillaItemProperty(item, id, predicate);
                return;
            } catch (Throwable t) {
                throw t instanceof RuntimeException re ? re : new RuntimeException(t);
            }
        }

        ModelPredicateProviderRegistry.register(
                item,
                id,
                (ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) ->
                        safeCall(predicate, stack, world, entity, seed)
        );
    }

    private static boolean shouldUseVanillaItemProperties() {
        return PLATFORM == RuntimePlatform.FORGE
                || PLATFORM == RuntimePlatform.NEOFORGE
                || PLATFORM == RuntimePlatform.CONNECTOR;
    }

    private static RuntimePlatform detectPlatform() {
        if (classExists("org.sinytra.connector.mod.ConnectorMod")
                || classExists("org.sinytra.connector.Connector")
                || classExists("org.sinytra.connector.service.ConnectorService")
                || classExists("org.sinytra.connector.loader.ConnectorLoader")) {
            return RuntimePlatform.CONNECTOR;
        }

        if (classExists("net.neoforged.fml.ModList")) {
            return RuntimePlatform.NEOFORGE;
        }

        if (classExists("net.minecraftforge.fml.ModList")) {
            return RuntimePlatform.FORGE;
        }

        return RuntimePlatform.FABRIC;
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name, false, CL);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static float safeCall(UniversalPredicate predicate, Object stack, Object world, Object entity, int seed) {
        try {
            return predicate.call(stack, world, entity, seed);
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    public static boolean isUsingSameItem(@Nullable Object stackObj, @Nullable Object entityObj) {
        if (!(stackObj instanceof ItemStack stack)) return false;
        if (!(entityObj instanceof LivingEntity entity)) return false;

        try {
            return entity.isUsingItem() && ItemStack.areEqual(entity.getActiveItem(), stack);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static float computeBowPull(@Nullable Object stackObj, @Nullable Object entityObj) {
        if (!(stackObj instanceof ItemStack stack)) return 0.0F;
        if (!(entityObj instanceof LivingEntity entity)) return 0.0F;

        try {
            if (!entity.isUsingItem()) return 0.0F;
            if (!ItemStack.areEqual(entity.getActiveItem(), stack)) return 0.0F;

            return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    private static void registerVanillaItemProperty(Item item, Identifier id, UniversalPredicate fn) throws Throwable {
        Class<?> itemPropertiesClz =
                Class.forName("net.minecraft.client.renderer.item.ItemProperties", false, CL);
        Class<?> resourceLocationClz =
                Class.forName("net.minecraft.resources.ResourceLocation", false, CL);
        Class<?> clampedFnClz =
                Class.forName("net.minecraft.client.renderer.item.ClampedItemPropertyFunction", false, CL);

        Object resourceLocation = createResourceLocation(resourceLocationClz, id.getNamespace(), id.getPath());

        Object proxy = Proxy.newProxyInstance(
                CL,
                new Class<?>[]{clampedFnClz},
                (proxyObj, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "toString" -> "PredicateBridgeProxy[" + id + "]";
                            case "hashCode" -> System.identityHashCode(proxyObj);
                            case "equals" -> args != null && args.length > 0 && proxyObj == args[0];
                            default -> null;
                        };
                    }

                    if (method.getReturnType() == float.class && method.getParameterCount() == 4) {
                        Object stack = args != null && args.length > 0 ? args[0] : null;
                        Object level = args != null && args.length > 1 ? args[1] : null;
                        Object entity = args != null && args.length > 2 ? args[2] : null;
                        int seed = args != null && args.length > 3 && args[3] instanceof Integer i ? i : 0;

                        return safeCall(fn, stack, level, entity, seed);
                    }

                    return 0.0F;
                }
        );

        Method register = Arrays.stream(itemPropertiesClz.getDeclaredMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 3)
                .filter(m -> Item.class.isAssignableFrom(m.getParameterTypes()[0]))
                .filter(m -> m.getParameterTypes()[1].equals(resourceLocationClz))
                .filter(m -> m.getParameterTypes()[2].equals(clampedFnClz))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException(
                        "ItemProperties.register(Item, ResourceLocation, ClampedItemPropertyFunction) not found"
                ));

        register.setAccessible(true);
        register.invoke(null, item, resourceLocation, proxy);
    }

    private static Object createResourceLocation(Class<?> resourceLocationClz, String namespace, String path) throws Throwable {
        try {
            Constructor<?> constructor = resourceLocationClz.getConstructor(String.class, String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(namespace, path);
        } catch (NoSuchMethodException ignored) {
        }

        String combined = namespace + ":" + path;

        try {
            Constructor<?> constructor = resourceLocationClz.getConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(combined);
        } catch (NoSuchMethodException ignored) {
        }

        for (String methodName : new String[]{"fromNamespaceAndPath", "tryParse", "parse", "of", "fromString"}) {
            Object out = tryInvokeResourceLocationFactory(resourceLocationClz, methodName, namespace, path, combined);
            if (out != null) return out;
        }

        throw new NoSuchMethodException("Could not construct ResourceLocation for " + combined);
    }

    private static Object tryInvokeResourceLocationFactory(
            Class<?> resourceLocationClz,
            String methodName,
            String namespace,
            String path,
            String combined
    ) throws Throwable {
        Method twoArg = findStaticMethod(resourceLocationClz, methodName, String.class, String.class);
        if (twoArg != null) {
            twoArg.setAccessible(true);
            Object out = twoArg.invoke(null, namespace, path);
            if (out != null) return out;
        }

        Method oneArg = findStaticMethod(resourceLocationClz, methodName, String.class);
        if (oneArg != null) {
            oneArg.setAccessible(true);
            Object out = oneArg.invoke(null, combined);
            if (out != null) return out;
        }

        return null;
    }

    private static Method findStaticMethod(Class<?> clz, String name, Class<?>... params) {
        try {
            Method m = clz.getDeclaredMethod(name, params);
            if (Modifier.isStatic(m.getModifiers())) return m;
        } catch (NoSuchMethodException ignored) {
        }

        try {
            Method m = clz.getMethod(name, params);
            if (Modifier.isStatic(m.getModifiers())) return m;
        } catch (NoSuchMethodException ignored) {
        }

        return null;
    }
}