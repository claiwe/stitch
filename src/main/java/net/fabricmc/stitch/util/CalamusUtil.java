package net.fabricmc.stitch.util;

import net.fabricmc.stitch.commands.GenState;
import net.fabricmc.stitch.representation.JarReader;
import net.fabricmc.stitch.representation.JarRootEntry;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class CalamusUtil
{
    public static void generateCalamus(File jarFile, File calamusFile, String[] args) throws IOException {
        JarRootEntry jarEntry = new JarRootEntry(jarFile);
        try {
            JarReader reader = new JarReader(jarEntry);
            reader.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GenState state = new GenState();
        boolean clearedPatterns = false;

        if (args != null) {
            for (int i = 2; i < args.length; i++) {
                switch (args[i].toLowerCase(Locale.ROOT)) {
                    case "-t", "--target-namespace" -> {
                        state.setTargetNamespace(args[i + 1]);
                        i++;
                    }
                    case "-p", "--obfuscation-pattern" -> {
                        if (!clearedPatterns)
                            state.clearObfuscatedPatterns();
                        clearedPatterns = true;
                        state.addObfuscatedPattern(args[i + 1]);
                        i++;
                    }
                }
            }
        }

        System.err.println("Generating new mappings...");
        state.generate(calamusFile, jarEntry, null);
        System.err.println("Done!");
    }

    public static void updateCalamus(File oldJarFile, File newJarFile, File oldCalamusFile, File newCalamusFile, File matchesFile, String[] args) throws IOException {
        JarRootEntry jarOld = new JarRootEntry(oldJarFile);
        try {
            JarReader reader = new JarReader(jarOld);
            reader.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JarRootEntry jarNew = new JarRootEntry(newJarFile);
        try {
            JarReader reader = new JarReader(jarNew);
            reader.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GenState state = new GenState();
        boolean clearedPatterns = false;

        for (int i = 5; i < args.length; i++) {
            switch (args[i].toLowerCase(Locale.ROOT)) {
                case "-t", "--target-namespace" -> {
                    state.setTargetNamespace(args[i + 1]);
                    i++;
                }
                case "-p", "--obfuscation-pattern" -> {
                    if (!clearedPatterns)
                        state.clearObfuscatedPatterns();
                    clearedPatterns = true;
                    state.addObfuscatedPattern(args[i + 1]);
                    i++;
                }
            }
        }

        System.err.println("Loading remapping files...");
        state.prepareUpdate(oldCalamusFile, matchesFile);

        System.err.println("Generating new mappings...");
        state.generate(newCalamusFile, jarNew, jarOld);
        System.err.println("Done!");
    }
}