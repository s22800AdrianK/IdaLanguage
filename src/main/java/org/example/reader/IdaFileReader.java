package org.example.reader;

import java.io.IOException;

public interface IdaFileReader {
    String readFile(String path) throws IOException;
}
