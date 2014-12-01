package eu.cityopt.sim.runner;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

public class TempDirTest {

    @Test
    public void testTempDir() throws IOException {
        TempDir tmp = new TempDir("test");
        try {
            assertTrue("Directory not created",
                       Files.isDirectory(tmp.path));
        } finally {
            tmp.close();
        }
        assertFalse("Temporary directory not removed.",
                    Files.exists(tmp.path));
    }
    
    @Test
    public void nonEmpty() throws IOException {
        String[] lines = {"Hello sailor."};
        TempDir tmp = new TempDir("test");
        try {
            Files.write(tmp.path.resolve("foo.txt"), Arrays.asList(lines));
        } finally {
            tmp.close();
        }
        assertFalse("Temporary directory not removed.",
                    Files.exists(tmp.path));
    }
    
    @Test
    public void doubleClose() throws IOException {
        TempDir tmp = new TempDir("test");
        tmp.close();
        tmp.close();
    }
}
