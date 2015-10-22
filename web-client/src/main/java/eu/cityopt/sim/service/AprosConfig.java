package eu.cityopt.sim.service;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
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
    private static final PrintStream
        logStream = IoBuilder.forLogger(AprosManager.class)
                .setLevel(Level.DEBUG).setAutoFlush(true).buildPrintStream();

    private static final long DELETE_PERIOD_MINUTES = 60;

    /** Apros profile directory */
    @Value("${APROS_PROFILE_PATH}")
    private String profilePath;

    /** Whether to check that the Apros profile directory is valid on start-up. */
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
            Path path = Paths.get(profilePath);
            if (Files.isDirectory(path)) {
                try {
                    AprosManager.register(path, executor, logStream);
                } catch (IOException e) {
                    if (checkProfile) {
                        throw new IOException(
                                "Invalid Apros profile directory: " + path, e);
                    }
                }
            } else {
                if (checkProfile) {
                    throw new IOException("Invalid Apros profile directory: " + path);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        SimulatorManagers.shutdown();
        deleter.tryDelete();
   }
}
