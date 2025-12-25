package org.teneted.neotenet.launcher.install;

import org.teneted.neotenet.launcher.data.Library;
import org.teneted.neotenet.launcher.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LibrariesAction implements Runnable {

    private static final Queue<Library> queue = new LinkedList<Library>();
    private static int count = 3;
    private final File root;

    public LibrariesAction(File librariesDir) {
        this.root = librariesDir;
    }

    public static void download(List<Library> libraries, File librariesDir) {
        // TODO: download libraries
        queue.addAll(libraries);

        for (int i = 0; i < count; i++) {
            new Thread(new LibrariesAction(librariesDir)).start();
        }
        // NeoTent - Start {count} thread to download
        while (queue.peek() != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Something is wrong...");
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Library library = queue.poll();
                if (library == null) {
                    //System.out.println("A Thread finished.");
                    break;
                }
                //System.out.println("Try download " + library.name());
                File target = new File(root, library.downloads().artifact().path());
                if (target.exists() && FileUtils.checkFile(target, library.downloads().artifact().sha1())) {

                    continue;
                }
                FileUtils.download(library.downloads().artifact().url(), target);
                System.out.println("Try check " + library.name());
                if (!FileUtils.checkFile(target, library.downloads().artifact().sha1())) {
                    System.out.println("Checked failed " + library.name());
                    target.deleteOnExit();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
