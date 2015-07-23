package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

import eu.cityopt.sim.eval.util.TempDir;

public class TempDirTest {

    @Test
    public void testTempDir() throws IOException {
        TempDir tmp = new TempDir("cityopt_test");
        try {
            assertTrue("Directory not created",
                       Files.isDirectory(tmp.getPath()));
        } finally {
            tmp.close();
        }
        assertFalse("Temporary directory not removed.",
                    Files.exists(tmp.getPath()));
    }
    
    @Test
    public void nonEmpty() throws IOException {
        String[] lines = {"Hello sailor."};
        TempDir tmp = new TempDir("test");
        try {
            Files.write(tmp.getPath().resolve("foo.txt"),
                        Arrays.asList(lines));
        } finally {
            tmp.close();
        }
        assertFalse("Temporary directory not removed.",
                    Files.exists(tmp.getPath()));
    }
    
    @Test
    public void doubleClose() throws IOException {
        TempDir tmp = new TempDir("test");
        tmp.close();
        tmp.close();
    }
}
