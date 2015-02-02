package eu.cityopt.sim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import eu.cityopt.sim.eval.apros.AprosManager;

/**
 * Configuration of Apros simulation server in CityOPT.
 * @author Hannu Rummukainen
 */
@Component
@PropertySource("classpath:/application.properties")
public class AprosConfig implements InitializingBean {
    /**
     * Apros profile directory names delimited by system path separator
     * (semicolon on Windows).
     */
    @Value("${APROS_PROFILE_PATH}")
    private String profilePath;

    @Value("${APROS_PROFILE_CHECK:true}")
    private boolean checkProfile;

    @Override
    public void afterPropertiesSet() throws IOException {
        if (profilePath != null) {
            String[] dirNames = profilePath.split(
                    Pattern.quote(System.getProperty("path.separator"))); 
            boolean valid = false;
            for (String dirName : dirNames) {
                Path dirPath = Paths.get(dirName);
                if (Files.isDirectory(dirPath)) {
                    try {
                        AprosManager.register(dirPath);
                        valid = true;
                    } catch (IOException e) {
                        if (checkProfile) {
                            throw new IOException(
                                    "Invalid Apros profile directory: " + dirPath, e);
                        }
                    }
                } else {
                    if (checkProfile) {
                        throw new IOException("Invalid Apros profile directory: " + dirPath);
                    }
                }
            }
            if (checkProfile && !valid) {
                throw new IOException("Invalid Apros profile path: " + profilePath);
            }
        }
    }
}
