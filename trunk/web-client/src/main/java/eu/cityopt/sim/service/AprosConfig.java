package eu.cityopt.sim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.TaskScheduler;

import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.apros.AprosManager;
import eu.cityopt.sim.eval.util.DelayedDeleter;

/**
 * Configuration of Apros simulation server in CityOPT.
 * @author Hannu Rummukainen
 */
@Configuration
@PropertySource("classpath:/application.properties")
public class AprosConfig implements InitializingBean, DisposableBean {
    private static final long DELETE_PERIOD_MINUTES = 60;

    /**
     * Apros profile directory names delimited by system path separator
     * (semicolon on Windows).
     */
    @Value("${APROS_PROFILE_PATH}")
    private String profilePath;

    @Value("${APROS_PROFILE_CHECK:true}")
    private boolean checkProfile;

    @Autowired
    ExecutorService executor;

    @Autowired
    TaskScheduler scheduler;

    DelayedDeleter deleter = DelayedDeleter.activate();

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }

    // Allow overriding application.properties via system environment variables.
    // In Tomcat system environment variables can be set in $CATALINA_BASE/setenv.bat
    private void readSystemEnvironment() {
        String p = System.getenv("APROS_PROFILE_PATH");
        if (p != null) profilePath = p;

        String c = System.getenv("APROS_PROFILE_CHECK");
        if (c != null) checkProfile = Boolean.valueOf(c);
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        scheduler.scheduleAtFixedRate(deleter::tryDelete,
                TimeUnit.MINUTES.toMillis(DELETE_PERIOD_MINUTES));
        readSystemEnvironment();
        if (profilePath != null) {
            String[] dirNames = profilePath.split(
                    Pattern.quote(System.getProperty("path.separator"))); 
            boolean valid = false;
            for (String dirName : dirNames) {
                Path dirPath = Paths.get(dirName);
                if (Files.isDirectory(dirPath)) {
                    try {
                        AprosManager.register(dirPath, executor);
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

    @Override
    public void destroy() throws Exception {
        SimulatorManagers.shutdown();
        deleter.tryDelete();
   }
}
