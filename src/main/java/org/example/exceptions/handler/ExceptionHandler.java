package org.example.exceptions.handler;

import org.example.ast.ProgramNode;
import org.example.exceptions.IdaException;
import org.example.handler.Handler;

import java.util.stream.IntStream;

public class ExceptionHandler implements Handler {
    private Handler next;
    private  String program;
    @Override
    public void setNextHandler(Handler handler) {
        this.next = handler;
    }

    @Override
    public void handle(ProgramNode programNode) {
        try {
            next.handle(programNode);
        }catch (IdaException exception) {
            System.err.println(exception.getMessage());
            printLines(exception.getLine());
            System.exit(1);
        }
    }

    private void printLines(int targetLine) {
        String[] arr = program.split("\n");
        IntStream.range(Math.max(0,targetLine-3),arr.length)
                .limit(6)
                .forEach(i->printLine(i,targetLine,arr[i]));
    }

    private void printLine(int i,int targetLine,String line){
        String s = --targetLine==i? "> ".concat(line) : line;
        System.err.println(i+":"+s);
    }
    public void setProgram(String program) {
        this.program = program;
    }
}
