package org.teneted.neotenet.launcher.install;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.teneted.neotenet.launcher.NeoTenetAgent;
import org.teneted.neotenet.launcher.data.InstallProfile;
import org.teneted.neotenet.launcher.data.Library;
import org.teneted.neotenet.launcher.utils.FileUtils;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Actions {

    private static final File libraries = new File("libraries");
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static MethodHandle addExportsToAllUnnamed;
    private static MethodHandle addOpensToAllUnnamed;
    private static MethodHandle loadModule;
    private static MethodHandle SET_bootLayer;
    private static ModuleLayer.Controller bootPath;
    private static String mcVersion;
    private static URLClassLoader installerLoader;

    private static final List<String> neededFiles = new ArrayList<>() {{
        add("neodev/unix-server-args.txt");
        add("neodev/windows-server-args.txt");
        add("neodev/installer-profile.json");
        add("neodev/server-binpatches.lzma");
    }};

    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        // NeoTent - add module
        open(getModule("java.base").orElseThrow(), "java.lang", Actions.class.getModule());
        open(getModule("java.base").orElseThrow(), "jdk.internal.loader", Actions.class.getModule());
        export(getModule("java.base").orElseThrow(), "jdk.internal.loader", Actions.class.getModule());
        export(getModule("java.base").orElseThrow(), "jdk.internal.module", Actions.class.getModule());

        // NeoTent - find
        try {
            Class<?> modulesCl = lookup.findClass("jdk.internal.module.Modules");
            addExportsToAllUnnamed = lookup.findStatic(modulesCl, "addExportsToAllUnnamed", MethodType.methodType(Void.TYPE, Module.class, String.class));
            addOpensToAllUnnamed = lookup.findStatic(modulesCl, "addOpensToAllUnnamed", MethodType.methodType(Void.TYPE, Module.class, String.class));
            Class<?> builtinCL = lookup.findClass("jdk.internal.loader.BuiltinClassLoader");
            loadModule = lookup.findVirtual(builtinCL, "loadModule", MethodType.methodType(Void.TYPE, ModuleReference.class));
            SET_bootLayer = MethodHandles.privateLookupIn(System.class, lookup).unreflectSetter(System.class.getDeclaredField("bootLayer"));
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    private static void appendToLoader(List<File> files) {
        files.forEach(f -> {
            try {
                NeoTenetAgent.instrumentation.appendToSystemClassLoaderSearch(new JarFile(f.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void find(File dir, List<File> libraries) {
        if (!dir.exists()) return;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) find(file, libraries);
            else if (file.getName().endsWith(".jar")) libraries.add(file);
            //libraries.add(file);
        }
    }

    public static void init() throws Throwable {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        URI file = Actions.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI();
        if (file.getPath().endsWith(".jar")) {
            // NeoTent - for jar
            System.out.println("Extracting files from launcher...");
            JarFile jar = new JarFile(new File(file));
            List<JarEntry> files = jar.stream().filter(entry -> neededFiles.contains(entry.getName()) || entry.getName().endsWith("-universal.jar")).toList();
            parseVersion(jar.getInputStream(files.stream().filter(entry -> entry.getName().contains("unix-server-args.txt"))
                    .findFirst()
                    .orElseThrow()));
            InstallProfile profile = mapper.readValue(jar.getInputStream(files.stream()
                    .filter(entry -> entry.getName().contains("installer-profile"))
                    .findFirst()
                    .orElseThrow()), InstallProfile.class);
            // NeoTent - wait for download
            System.out.println("Download libraries...");
            LibrariesAction.download(profile.libraries(), libraries);
            File universalJar = new File(libraries, profile.libraries().stream().filter(library -> library.name().endsWith(":universal"))
                    .findFirst()
                    .orElseThrow()
                    .downloads().artifact().path());
            if (!universalJar.getParentFile().exists()) universalJar.getParentFile().mkdirs();
            if (!universalJar.exists()) universalJar.createNewFile();
            FileUtils.copyTo(jar.getInputStream(files.stream().filter(jarEntry -> jarEntry.getName().endsWith("-universal.jar"))
                    .findFirst()
                    .orElseThrow()), new FileOutputStream(universalJar));
            installerTask(file, profile);
            File argsFile = getArgsFile();
            if (!argsFile.exists()) argsFile.createNewFile();
            FileUtils.copyTo(jar.getInputStream(files.stream().filter(entry -> entry.getName().contains(argsFile.getName()))
                            .findFirst()
                            .orElseThrow()),
                    new FileOutputStream(argsFile));
            // Close the installer class loader to release all file handles and resources
            if (installerLoader != null) {
                installerLoader.close();
                installerLoader = null;
            }
        } else {
            // NeoTent - for project
            throw new RuntimeException("Not support");
        }
    }

    private static void parseVersion(InputStream fis) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileUtils.copyTo(fis, bos);
        String str = bos.toString(StandardCharsets.UTF_8);
        mcVersion = Arrays.stream(str.split("\n"))
                .filter(arg -> arg.startsWith("--fml.mcVersion"))
                .findFirst()
                .orElseThrow()
                .split(" ")[1];
    }


    private static void installerTask(URI file, InstallProfile profile) throws Throwable {
        File launcherJar = new File(file);
        List<Library> libraries = profile.libraries();
        System.out.println("try download minecraft server for " + mcVersion + "...");
        File serverJar = new File(Actions.libraries, "net/minecraft/server/" + mcVersion + "/server-" + mcVersion + ".jar");
        Library neoform = libraries.stream().filter(library -> library.name().startsWith("net.neoforged:neoform"))
                .findFirst()
                .orElseThrow();
        Library neoforge = libraries.stream().filter(library -> library.name().startsWith("net.neoforged:neoforge"))
                .findFirst()
                .orElseThrow();

        File neoformZip = new File(Actions.libraries, neoform.downloads().artifact().path());
        File serverPatched = new File(Actions.libraries, neoforge.downloads().artifact().path().replace("-universal", "-server"));

        String neoformVer = neoform.name().split(":")[2].replace("@zip", "");
        File serverDir = new File(Actions.libraries, "net/minecraft/server/" + neoformVer);
        if (!serverDir.exists()) {
            serverDir.mkdirs();
        }
        File serverJarUnpacked = new File(serverDir, "server-" + neoformVer + "-unpacked.jar");
        File serverJarSlim = new File(serverDir, "server-" + neoformVer + "-slim.jar");
        File serverJarExtra = new File(serverDir, "server-" + neoformVer + "-extra.jar");
        File serverJarSrg = new File(serverDir, "server-" + neoformVer + "-srg.jar");
        File serverMapping = new File(serverDir, neoformVer + ".srg");
        File serverPatcher = new File(serverDir, "server.lzma");
        File mojangMapping = new File(serverDir, neoformVer + ".mojang");
        File mergeMapping = new File(serverDir, neoformVer + ".mapping");

        if (!serverJar.exists())
            ServerAction.download(mcVersion, serverJar);

        String jvm = System.getProperty("java.home") + "/bin/java";
        if (System.getProperty("os.name").contains("Windows")) {
            jvm += ".exe";
        }
        StringBuilder installerToolClassPaths = new StringBuilder();
        StringBuilder jarsplitterToolsClassPaths = new StringBuilder();
        StringBuilder binarypatcherClassPaths = new StringBuilder();
        StringBuilder autoRenamingToolClassPaths = new StringBuilder();
        parseLibraries(profile, installerToolClassPaths, "net.neoforged.installertools:installertools");
        parseLibraries(profile, autoRenamingToolClassPaths, "net.neoforged:AutoRenamingTool");
        parseLibraries(profile, binarypatcherClassPaths, "net.neoforged.installertools:binarypatcher");
        parseLibraries(profile, jarsplitterToolsClassPaths, "net.neoforged.installertools:jarsplitter");

        String installerTools = parseJar(profile, "net.neoforged.installertools:installertools");
        String autoRenamingTool = parseJar(profile, "net.neoforged:AutoRenamingTool");
        String binarypatcher = parseJar(profile, "net.neoforged.installertools:binarypatcher");
        String jarsplitter = parseJar(profile, "net.neoforged.installertools:jarsplitter");
        List<List<String>> cmds = new ArrayList<>();

        String finalJvm = jvm;
        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", installerToolClassPaths.toString(), "net.neoforged.installertools.ConsoleTool"));
            addAll(List.of("--task", "EXTRACT_FILES"));
            addAll(List.of("--archive", launcherJar.getPath(), "--from", "neodev/server-binpatches.lzma", "--to", serverPatcher.getPath()));
        }});
        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", installerToolClassPaths.toString(), "net.neoforged.installertools.ConsoleTool"));
            addAll(List.of("--task", "BUNDLER_EXTRACT"));
            addAll(List.of("--input", serverJar.getPath(), "--output", Actions.libraries.getPath(), "--libraries"));
        }});
        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", installerToolClassPaths.toString(), "net.neoforged.installertools.ConsoleTool"));
            addAll(List.of("--task", "BUNDLER_EXTRACT"));
            addAll(List.of("--input", serverJar.getPath(), "--output", serverJarUnpacked.getPath(), "--jar-only"));
        }});
        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", installerToolClassPaths.toString(), "net.neoforged.installertools.ConsoleTool"));
            addAll(List.of("--task", "MCP_DATA"));
            addAll(List.of("--input", neoformZip.getPath(), "--output", serverMapping.getPath(), "--key", "mappings"));
        }});

        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", installerToolClassPaths.toString(), "net.neoforged.installertools.ConsoleTool"));
            addAll(List.of("--task", "DOWNLOAD_MOJMAPS"));
            addAll(List.of("--version", mcVersion, "--side", "server", "--output", mojangMapping.getPath()));
        }});

        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", installerToolClassPaths.toString(), "net.neoforged.installertools.ConsoleTool"));
            addAll(List.of("--task", "MERGE_MAPPING"));
            addAll(List.of("--left", serverMapping.getPath(), "--output", mergeMapping.getPath(), "--right", mojangMapping.getPath(), "--classes", "--fields", "--methods", "--reverse-right"));
        }});

        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", jarsplitterToolsClassPaths.toString(), "net.neoforged.jarsplitter.ConsoleTool"));
            addAll(List.of("--input", serverJarUnpacked.getPath(), "--slim", serverJarSlim.getPath(), "--extra", serverJarExtra.getPath(), "--srg", mergeMapping.getPath()));
        }});
        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", autoRenamingToolClassPaths.toString(), "net.neoforged.art.Main"));
            addAll(List.of("--input", serverJarSlim.getPath(), "--output", serverJarSrg.getPath(), "--names", mergeMapping.getPath(), "--ann-fix", "--ids-fix", "--src-fix", "--record-fix"));
        }});
        cmds.add(new ArrayList<>() {{
            addAll(List.of(finalJvm, "-cp", binarypatcherClassPaths.toString(), "net.neoforged.binarypatcher.ConsoleTool"));
            addAll(List.of("--clean", serverJarSrg.getPath(), "--output", serverPatched.getPath(), "--apply", serverPatcher.getPath()));
        }});

        System.out.println("try remap server jar...");
        //System.out.println(cmds);
        cmds.forEach(cmd -> {
            try {
                Process process = Runtime.getRuntime().exec(cmd.toArray(new String[0]));

                int size = 0;
                byte[] read = new byte[1024];
                while ((size = process.getInputStream().read(read)) != -1) {

                }
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private static void parseLibraries(InstallProfile profile, StringBuilder classpath, String target) {
        profile.processors().stream().filter(processor -> processor.jar().startsWith(target))
                .findFirst()
                .orElseThrow()
                .classpath().forEach(cp -> {
                    if (!classpath.isEmpty()) {
                        classpath.append(File.pathSeparator);
                    }
                    classpath.append(new File(Actions.libraries, profile.libraries().stream().filter(library -> library.name().equalsIgnoreCase(cp))
                            .findFirst()
                            .orElseThrow()
                            .downloads()
                            .artifact()
                            .path()).getPath());
                });
    }

    private static String parseJar(InstallProfile profile, String target) {

        return new File(libraries, profile.libraries().stream().filter(library -> library.name().startsWith(target))
                .findFirst()
                .orElseThrow()
                .downloads()
                .artifact()
                .path()).getPath();
    }

    private static void loadLibraries() {
        // NeoTent - load libraries
        // TODO: Check enable from config
        List<File> loaders = new ArrayList<>();
        find(libraries, loaders);
        appendToLoader(loaders);
    }

    private static void loadInstallerLibraries(List<Library> libraries) {
        List<URL> urls = new ArrayList<>();
        libraries.forEach(library -> {
            try {
                urls.add(new File(Actions.libraries, library.downloads().artifact().path()).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
        installerLoader = new URLClassLoader(urls.toArray(new URL[0]));
    }

    private static Optional<Module> getModule(String name) {
        if (bootPath == null) return ModuleLayer.boot().findModule(name);
        return bootPath.layer().findModule(name);
    }


    private static void open(Module module, String open, Module to) {
        NeoTenetAgent.instrumentation.redefineModule(module,
                Set.of(),
                Map.of(),
                Map.of(open, Set.of(to)),
                Set.of(),
                Map.of());
    }

    private static void export(Module module, String export, Module to) {
        NeoTenetAgent.instrumentation.redefineModule(module,
                Set.of(),
                Map.of(export, Set.of(to)),
                Map.of(),
                Set.of(),
                Map.of());
    }

    public static File getArgsFile() {
        String args = "unix-server-args.txt";
        if (System.getProperty("os.name").contains("Windows")) {
            args = "windows-server-args.txt";
        }
        return new File(libraries, "net/neoforged/neoforge/" + args);
    }

    public static boolean ready() {
        return getArgsFile().exists();
    }

    public static String[] parseArgs(String[] commandLineArgs) throws Throwable {
        addOpensToAllUnnamed.invoke(ModuleLayer.boot().findModule("java.base").orElseThrow(), "java.lang.invoke");
        addExportsToAllUnnamed.invoke(ModuleLayer.boot().findModule("java.base").orElseThrow(), "java.lang.invoke");
        loadLibraries();
        File argsFile = getArgsFile();
        List<String> args = FileUtils.readTexts(argsFile);
        List<String> launcherArgs = new ArrayList<>();
        args.forEach(arg -> {
            try {
                if (arg.startsWith("-p")) {
                    Path[] modules = Arrays.stream(arg.replace("-p", "").trim().split(File.pathSeparator))
                            .map(Path::of)
                            .toArray(Path[]::new);
                    ClassLoader systemCl = ClassLoader.getSystemClassLoader();
                    ModuleFinder finder = ModuleFinder.of(modules);
                    Set<ModuleReference> allModules = finder.findAll();
                    for (ModuleReference module : allModules) {
                        loadModule.invoke(systemCl, module);
                    }
                    bootPath = ModuleLayer.defineModules(ModuleLayer.boot().configuration().resolve(finder, ModuleFinder.of(new Path[0]), (Collection) allModules.stream().map(mr -> mr.descriptor().name()).collect(Collectors.toSet())), List.of(ModuleLayer.boot()), s -> ClassLoader.getSystemClassLoader());
                    SET_bootLayer.invokeExact(bootPath.layer());
                } else if (arg.startsWith("--add-opens")) {
                    String[] tryOpen = arg.replace("--add-opens", "").trim().split("=");
                    String[] modules = tryOpen[0].split("/");
                    if (tryOpen[1].equalsIgnoreCase("ALL-UNNAMED")) {
                        addOpensToAllUnnamed.invoke(ModuleLayer.boot().findModule(modules[0]).orElseThrow(), modules[1]);
                    } else {
                        open(ModuleLayer.boot().findModule(modules[0]).orElseThrow(), modules[1], getModule(tryOpen[1]).orElseThrow());
                    }
                } else if (arg.startsWith("--add-exports")) {
                    String[] tryOpen = arg.replace("--add-exports", "").trim().split("=");
                    String[] modules = tryOpen[0].split("/");
                    if (tryOpen[1].equalsIgnoreCase("ALL-UNNAMED")) {
                        addExportsToAllUnnamed.invoke(ModuleLayer.boot().findModule(modules[0]).orElseThrow(), modules[1]);
                    } else {
                        export(ModuleLayer.boot().findModule(modules[0]).orElseThrow(), modules[1], getModule(tryOpen[1]).orElseThrow());
                    }
                } else if (arg.startsWith("-D")) {
                    String[] prop = arg.replace("-D", "").trim().split("=");
                    if (prop.length > 1) {
                        System.setProperty(prop[0], prop[1]);
                    } else {
                        System.setProperty(prop[0], "");
                    }
                } else if (arg.startsWith("--") && !arg.contains("add-modules")) {
                    String[] largs = arg.split(" ");
                    launcherArgs.addAll(List.of(largs));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        launcherArgs.addAll(List.of(commandLineArgs));
        return launcherArgs.toArray(new String[0]);
    }
}
