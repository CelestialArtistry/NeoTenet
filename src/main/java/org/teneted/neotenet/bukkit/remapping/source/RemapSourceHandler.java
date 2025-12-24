package org.teneted.neotenet.bukkit.remapping.source;

import com.google.common.io.ByteStreams;
import cpw.mods.modlauncher.ClassTransformer;
import cpw.mods.modlauncher.TransformingClassLoader;
import org.teneted.neotenet.NeoTenet;
import org.teneted.neotenet.bukkit.remapping.NeoTenetRemapper;
import org.teneted.neotenet.bukkit.remapping.GlobalClassRepo;
import org.teneted.neotenet.bukkit.remapping.Unsafe;
import org.objectweb.asm.ClassReader;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Hashtable;

public class RemapSourceHandler extends URLStreamHandler {


    private static final MethodHandle MH_TRANSFORM;

    static {
        try {
            ClassLoader classLoader = NeoTenet.class.getClassLoader();
            Field classTransformer = TransformingClassLoader.class.getDeclaredField("classTransformer");
            classTransformer.setAccessible(true);
            ClassTransformer transformer = (ClassTransformer) classTransformer.get(classLoader);
            Method transform = transformer.getClass().getDeclaredMethod("transform", byte[].class, String.class, String.class);
            MH_TRANSFORM = Unsafe.lookup().unreflect(transform).bindTo(transformer);
        } catch (Throwable t) {
            throw new IllegalStateException("Unknown modlauncher version", t);
        }
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new RemapSourceConnection(new URL(u.getFile()));
    }

    private static class RemapSourceConnection extends URLConnection {

        private byte[] array;

        protected RemapSourceConnection(URL url) {
            super(url);
        }

        @Override
        public void connect() throws IOException {
            byte[] bytes = ByteStreams.toByteArray(url.openStream());
            String className = new ClassReader(bytes).getClassName();
            if (className.startsWith("net/minecraft/") || className.equals("com/mojang/brigadier/tree/CommandNode")) {
                try {
                    bytes = (byte[]) MH_TRANSFORM.invokeExact(bytes, className.replace('/', '.'), "source");
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
            this.array = NeoTenetRemapper.getResourceMapper().remapClassFile(bytes, GlobalClassRepo.INSTANCE);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            connect();
            if (this.array == null) {
                throw new FileNotFoundException(this.url.getFile());
            } else {
                return new ByteArrayInputStream(this.array);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void register() {
        try {
            MethodHandles.lookup().ensureInitialized(URL.class);
            MethodHandle getter = Unsafe.lookup().findStaticGetter(URL.class, "handlers", Hashtable.class);
            Hashtable<String, URLStreamHandler> handlers = (Hashtable<String, URLStreamHandler>) getter.invokeExact();
            handlers.put("remap", new RemapSourceHandler());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}