package org.example.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IdaFileReaderImpl implements IdaFileReader{
    private static final String idaFileExtension = "ida";

    @Override
    public String readFile(String path) throws IOException {
        Path pathToFile = Paths.get(path);
        if(!Files.exists(pathToFile)){
            throw new IOException("File doesn't exist: "+pathToFile);
        }
        checkFileExtension(pathToFile.toString());

        return new String(Files.readAllBytes(pathToFile)).trim();
    }

    private static void checkFileExtension(String path) throws IOException {
        int dotPosition = path.indexOf('.');
        String ext = (dotPosition==-1)? "":path.substring(dotPosition+1);
        if(!ext.equals(idaFileExtension)) {
            throw new IOException("Wrong file extension: "+ext);
        }
    }
}
