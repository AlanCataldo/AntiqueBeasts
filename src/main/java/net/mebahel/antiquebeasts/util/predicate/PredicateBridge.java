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
        NEOFORGE
    }

    @FunctionalInterface
    public interface UniversalPredicate {
        float call(@Nullable Object stack, @Nullable Object world, @Nullable Object entity, int seed) throws Throwable;
    }

    private static final RuntimePlatform PLATFORM = detectPlatform();

    public static void register(Item item, Identifier id, UniversalPredicate predicate) {
        if (item == null) return;

        if (PLATFORM == RuntimePlatform.FORGE || PLATFORM == RuntimePlatform.NEOFORGE) {
            try {
                registerVanillaItemProperty(item, id, predicate);
            } catch (Throwable t) {
                throw (t instanceof RuntimeException re) ? re : new RuntimeException(t);
            }
            return;
        }

        ModelPredicateProviderRegistry.register(item, id,
                (ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) ->
                        safeCall(predicate, stack, world, entity, seed)
        );
    }

    private static float safeCall(UniversalPredicate predicate, Object stack, Object world, Object entity, int seed) {
        try {
            return predicate.call(stack, world, entity, seed);
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    private static RuntimePlatform detectPlatform() {
        ClassLoader cl = PredicateBridge.class.getClassLoader();

        if (classExists("net.neoforged.fml.ModList", cl)) {
            return RuntimePlatform.NEOFORGE;
        }

        if (classExists("net.minecraftforge.fml.ModList", cl)) {
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
        if (stackObj == null || entityObj == null) return false;

        try {
            if (stackObj instanceof ItemStack stack && entityObj instanceof LivingEntity entity) {
                return entity.isUsingItem() && entity.getActiveItem() == stack;
            }
        } catch (Throwable ignored) {
        }

        try {
            boolean using = invokeNoArgBoolean(entityObj, "isUsingItem");
            if (!using) return false;

            Object active = invokeNoArg(entityObj, "getUseItem");
            if (active == null) return false;

            return active == stackObj || active.equals(stackObj);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static float computeBowPull(@Nullable Object stackObj, @Nullable Object entityObj) {
        if (stackObj == null || entityObj == null) return 0.0F;

        try {
            if (stackObj instanceof ItemStack stack && entityObj instanceof LivingEntity entity) {
                if (entity.getActiveItem() != stack) return 0.0F;
                return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        } catch (Throwable ignored) {
        }

        try {
            boolean using = invokeNoArgBoolean(entityObj, "isUsingItem");
            if (!using) return 0.0F;

            Object active = invokeNoArg(entityObj, "getUseItem");
            if (active == null) return 0.0F;

            if (!(active == stackObj || active.equals(stackObj))) {
                return 0.0F;
            }

            int remaining = invokeNoArgInt(entityObj, "getUseItemRemainingTicks");
            int duration = invokeNoArgInt(stackObj, "getUseDuration");

            return (float) (duration - remaining) / 20.0F;
        } catch (Throwable ignored) {
            return 0.0F;
        }
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
        return (v instanceof Integer i) ? i : (int) v;
    }

    private static boolean invokeNoArgBoolean(Object target, String methodName) throws Throwable {
        Object v = invokeNoArg(target, methodName);
        return (v instanceof Boolean b) ? b : (boolean) v;
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
        ClassLoader cl = PredicateBridge.class.getClassLoader();

        Class<?> itemPropertiesClz =
                Class.forName("net.minecraft.client.renderer.item.ItemProperties", false, cl);
        Class<?> resourceLocationClz =
                Class.forName("net.minecraft.resources.ResourceLocation", false, cl);
        Class<?> clampedFnClz =
                Class.forName("net.minecraft.client.renderer.item.ClampedItemPropertyFunction", false, cl);

        Object rl = createResourceLocation(resourceLocationClz, id.getNamespace(), id.getPath());

        Object proxy = Proxy.newProxyInstance(
                cl,
                new Class<?>[]{clampedFnClz},
                (p, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "toString" -> "PredicateBridgeProxy[" + id + "]";
                            case "hashCode" -> System.identityHashCode(p);
                            case "equals" -> p == args[0];
                            default -> null;
                        };
                    }

                    if (method.getReturnType() == float.class && method.getParameterCount() == 4) {
                        Object stack = args[0];
                        Object level = args[1];
                        Object entity = args[2];
                        int seed = (int) args[3];

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
        register.invoke(null, item, rl, proxy);
    }

    private static Object createResourceLocation(Class<?> resourceLocationClz, String namespace, String path) throws Throwable {
        try {
            Constructor<?> c = resourceLocationClz.getConstructor(String.class, String.class);
            c.setAccessible(true);
            return c.newInstance(namespace, path);
        } catch (NoSuchMethodException ignored) {
        }

        String combined = namespace + ":" + path;

        try {
            Constructor<?> c = resourceLocationClz.getConstructor(String.class);
            c.setAccessible(true);
            return c.newInstance(combined);
        } catch (NoSuchMethodException ignored) {
        }

        for (String methodName : new String[]{"tryParse", "parse", "of", "fromString"}) {
            Method m = findStaticMethod(resourceLocationClz, methodName, String.class);
            if (m != null) {
                m.setAccessible(true);
                Object out = m.invoke(null, combined);
                if (out != null) return out;
            }
        }

        throw new NoSuchMethodException("Could not construct ResourceLocation for " + combined);
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