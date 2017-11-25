/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread that writes values to the console.
 */
class QueueHandler implements Runnable {
    private final BlockingQueue<String> valuesToHandle = new LinkedBlockingQueue<>();

    /**
     * Queues a value that will eventually be written to the console. Values are written in the same
     * order they are passed to this method.
     *
     * @param output The value to write.
     */
    void print(String output) {
        try {
            valuesToHandle.put(output);
        } catch (InterruptedException ignore) {
        }
    }

    /** 
     * Queues a newline character for printing.
     */
    void println() {
        print("\n");
    }

    /**
     * Prints queued values.
     */
    @Override
    public void run() {
        String output = null;
        for (;;) {
            try {
                output = valuesToHandle.take(); //read from queue
            } catch (InterruptedException ignore) {
                return;
            }
            System.out.print(output); //write to stdout

        }
    }

}