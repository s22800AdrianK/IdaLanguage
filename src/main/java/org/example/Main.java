package org.example;



import org.example.pipeline.IdaProcessingPipeline;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length<1) {
            throw new RuntimeException("Program requires file path");
        }
        new IdaProcessingPipeline(args[0]).run();
    }
}