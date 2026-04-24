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
        if (item == null || id == null || predicate == null) {
            return;
        }

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
        if (PLATFORM == RuntimePlatform.FORGE
                || PLATFORM == RuntimePlatform.NEOFORGE
                || PLATFORM == RuntimePlatform.CONNECTOR) {
            return true;
        }

        return classExists("net.minecraft.client.renderer.item.ItemProperties", CL)
                && classExists("net.minecraft.client.renderer.item.ClampedItemPropertyFunction", CL)
                && classExists("net.minecraft.resources.ResourceLocation", CL);
    }

    private static float safeCall(UniversalPredicate predicate, Object stack, Object world, Object entity, int seed) {
        try {
            return predicate.call(stack, world, entity, seed);
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    private static RuntimePlatform detectPlatform() {
        if (classExists("org.sinytra.connector.mod.ConnectorMod", CL)
                || classExists("org.sinytra.connector.Connector", CL)
                || classExists("org.sinytra.connector.service.ConnectorService", CL)
                || classExists("org.sinytra.connector.loader.ConnectorLoader", CL)) {
            return RuntimePlatform.CONNECTOR;
        }

        if (classExists("net.neoforged.fml.ModList", CL)) {
            return RuntimePlatform.NEOFORGE;
        }

        if (classExists("net.minecraftforge.fml.ModList", CL)) {
            return RuntimePlatform.FORGE;
        }

        return RuntimePlatform.FABRIC;
    }

    private static boolean classExists(String name, ClassLoader cl) {
        try {
            Class.forName(name, false, cl);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isUsingSameItem(@Nullable Object stackObj, @Nullable Object entityObj) {
        if (stackObj == null || entityObj == null) {
            return false;
        }

        try {
            if (stackObj instanceof ItemStack stack && entityObj instanceof LivingEntity entity) {
                return entity.isUsingItem() && entity.getActiveItem() == stack;
            }
        } catch (Throwable ignored) {
        }

        try {
            boolean using = invokeNoArgBoolean(entityObj, "isUsingItem");
            if (!using) {
                return false;
            }

            Object active = invokeAnyNoArg(entityObj, "getUseItem", "getActiveItem");
            if (active == null) {
                return false;
            }

            return active == stackObj || active.equals(stackObj);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static float computeBowPull(@Nullable Object stackObj, @Nullable Object entityObj) {
        if (stackObj == null || entityObj == null) {
            return 0.0F;
        }

        try {
            if (stackObj instanceof ItemStack stack && entityObj instanceof LivingEntity entity) {
                if (entity.getActiveItem() != stack) {
                    return 0.0F;
                }

                return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        } catch (Throwable ignored) {
        }

        try {
            boolean using = invokeNoArgBoolean(entityObj, "isUsingItem");
            if (!using) {
                return 0.0F;
            }

            Object active = invokeAnyNoArg(entityObj, "getUseItem", "getActiveItem");
            if (active == null) {
                return 0.0F;
            }

            if (!(active == stackObj || active.equals(stackObj))) {
                return 0.0F;
            }

            int remaining = invokeAnyNoArgInt(entityObj, "getUseItemRemainingTicks", "getItemUseTimeLeft");
            int duration = invokeAnyNoArgInt(stackObj, "getUseDuration", "getMaxUseTime");

            return (float) (duration - remaining) / 20.0F;
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    private static Object invokeAnyNoArg(Object target, String... methodNames) throws Throwable {
        Throwable last = null;

        for (String methodName : methodNames) {
            try {
                return invokeNoArg(target, methodName);
            } catch (Throwable t) {
                last = t;
            }
        }

        if (last != null) {
            throw last;
        }

        throw new NoSuchMethodException(target.getClass().getName() + "#" + Arrays.toString(methodNames));
    }

    private static int invokeAnyNoArgInt(Object target, String... methodNames) throws Throwable {
        Object value = invokeAnyNoArg(target, methodNames);
        return value instanceof Integer i ? i : (int) value;
    }

    private static Object invokeNoArg(Object target, String methodName) throws Throwable {
        Method m = findNoArgMethod(target.getClass(), methodName);

        if (m == null) {
            throw new NoSuchMethodException(target.getClass().getName() + "#" + methodName + "()");
        }

        m.setAccessible(true);
        return m.invoke(target);
    }

    private static int invokeNoArgInt(Object target, String methodName) throws Throwable {
        Object v = invokeNoArg(target, methodName);
        return v instanceof Integer i ? i : (int) v;
    }

    private static boolean invokeNoArgBoolean(Object target, String methodName) throws Throwable {
        Object v = invokeNoArg(target, methodName);
        return v instanceof Boolean b ? b : (boolean) v;
    }

    private static Method findNoArgMethod(Class<?> clz, String name) {
        try {
            return clz.getMethod(name);
        } catch (NoSuchMethodException ignored) {
        }

        try {
            return clz.getDeclaredMethod(name);
        } catch (NoSuchMethodException ignored) {
        }

        for (Method m : clz.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) {
                return m;
            }
        }

        for (Method m : clz.getDeclaredMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) {
                return m;
            }
        }

        return null;
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
                            case "equals" -> proxyObj == args[0];
                            default -> null;
                        };
                    }

                    if (method.getReturnType() == float.class && method.getParameterCount() == 4) {
                        Object stack = args != null && args.length > 0 ? args[0] : null;
                        Object level = args != null && args.length > 1 ? args[1] : null;
                        Object entity = args != null && args.length > 2 ? args[2] : null;
                        int seed = 0;

                        if (args != null && args.length > 3 && args[3] instanceof Integer i) {
                            seed = i;
                        }

                        return safeCall(fn, stack, level, entity, seed);
                    }

                    throw new UnsupportedOperationException(
                            "Unexpected method on ClampedItemPropertyFunction: " + method
                    );
                }
        );

        Method register = Arrays.stream(itemPropertiesClz.getDeclaredMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 3)
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

        for (String methodName : new String[]{"tryParse", "parse", "of", "fromString", "fromNamespaceAndPath"}) {
            Object out = tryInvokeResourceLocationFactory(resourceLocationClz, methodName, namespace, path, combined);
            if (out != null) {
                return out;
            }
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
        Method oneArg = findStaticMethod(resourceLocationClz, methodName, String.class);
        if (oneArg != null) {
            oneArg.setAccessible(true);
            Object out = oneArg.invoke(null, combined);
            if (out != null) {
                return out;
            }
        }

        Method twoArg = findStaticMethod(resourceLocationClz, methodName, String.class, String.class);
        if (twoArg != null) {
            twoArg.setAccessible(true);
            Object out = twoArg.invoke(null, namespace, path);
            if (out != null) {
                return out;
            }
        }

        return null;
    }

    private static Method findStaticMethod(Class<?> clz, String name, Class<?>... params) {
        try {
            Method m = clz.getDeclaredMethod(name, params);
            if (Modifier.isStatic(m.getModifiers())) {
                return m;
            }
        } catch (NoSuchMethodException ignored) {
        }

        try {
            Method m = clz.getMethod(name, params);
            if (Modifier.isStatic(m.getModifiers())) {
                return m;
            }
        } catch (NoSuchMethodException ignored) {
        }

        return null;
    }
}