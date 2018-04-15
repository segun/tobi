/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.com.idempotent.resourceinteraction.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author aardvocate
 */
public class FileProcessor {

    private static List<String> fileLines = new ArrayList<>();
    private static List<String> resources = new ArrayList<>();
    
    private static void getFileLines(File f) throws IOException {
        fileLines = new ArrayList<>();
        Files.lines(Paths.get(f.getAbsolutePath())).filter(x -> !x.trim().isEmpty()).forEach(x -> fileLines.add(x));
    }
    
    public static boolean getResourcesFromFile(File f) throws IOException {
        getFileLines(f);
        resources = fileLines.stream().filter(x -> {
            return x.startsWith("Resource ");
        }).collect(Collectors.toList());
        
        return !resources.isEmpty();
    }

    public static List<String> getFileLines() {
        return fileLines;
    }

    public static List<String> getResources() {
        return resources;
    }       
}
