package eu.cityopt.sim.runner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.cli.*;
import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Job;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.Server;
import org.simantics.simulation.scheduling.ServerFactory;
import org.simantics.simulation.scheduling.applications.Application;
import org.simantics.simulation.scheduling.applications.ProfileApplication;
import org.simantics.simulation.scheduling.files.FileSelector;
import org.simantics.simulation.scheduling.files.IDirectory;
import org.simantics.simulation.scheduling.files.IFile;
import org.simantics.simulation.scheduling.files.LocalDirectory;
import org.simantics.simulation.scheduling.files.MemoryDirectory;
import org.simantics.simulation.scheduling.status.JobFinished;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;
import org.simantics.simulation.scheduling.status.StatusWaitingUtils;

import eu.cityopt.sim.eval.util.TempDir;

/**
 * This class controls all aspects of the application's execution
 */
public class SimRunner implements Callable<Integer> {
    private String
        appname = "simrun",
        dirname = ".",
        profile = "Apros-N3D-5.13.06-64bit",
        resfile = "results.dat",
        logTemplate = "job-%02d.log",
        files[];
    private int
        cores = 1,
        runs = 1;
    private Map<String, OutputStream>
        jobLogs = new HashMap<String, OutputStream>();

    public SimRunner(String[] args) {
        //appname = context.getBrandingName();
        files = parseArgs(args);
    }
    
    public static void main(String[] args) {
        SimRunner runner = new SimRunner(args);
        try {
            System.exit(runner.call());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
    
    @Override
    public Integer call() throws IOException, InterruptedException {
        if (files == null)
            return 1;
        Path fdir = Paths.get(dirname);
        Path pdir = Paths.get(profile);
        String pname = pdir.getFileName().toString();
        MemoryDirectory mdir = new MemoryDirectory();
        for (String f : files)
            mdir.addFile(fdir.resolve(f));
        try (TempDir tmp = new TempDir("sim-runner")) {
            System.out.println(
                    "Scheduling: " + runs + " runs on " + cores
                    + " cores\nTempDir: " + tmp.getPath() + "\nProfile: "
                    + pdir + "\nJob logs: " + logTemplate);
            Server srv = ServerFactory.createLocalServer(tmp.getPath());
            srv.installProfile(pname, new LocalDirectory(pdir));
            {
                Map<String, String> p = new HashMap<String, String>();
                p.put("type", "local");
                p.put("cpu", String.valueOf(cores));
                srv.createNode(p);
            }
            StatusLoggingUtils.logServerStatus(System.out, srv);

            Experiment experiment = srv.createExperiment(
                    new HashMap<String, String>());

            waitJobs(startJobs(experiment, pname, files[0], mdir));

            experiment.dispose();
            srv.dispose();
        } finally {
            for (OutputStream log : jobLogs.values())
                log.close();
        }
        return 0;
    }

    private List<Job> startJobs(
            Experiment experiment, String profile, String script,
            MemoryDirectory mdir) throws IOException {
        Application launcher = new ProfileApplication(profile, "Launcher.exe");
        FileSelector res_sel = new FileSelector(resfile); 
        List<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i != runs; ++i) {
            JobConfiguration conf = new JobConfiguration(
                    launcher,
                    new String[] {script, String.valueOf(i)},
                    mdir,
                    res_sel);
            String logname = String.format(logTemplate, i);
            OutputStream log = jobLogs.get(logname);
            if (log == null) {
                log = Files.newOutputStream(Paths.get(logname));
                jobLogs.put(logname, log);
            }
            Job job = experiment.createJob(
                    String.format("job_%02d", i), conf);
            StatusLoggingUtils.redirectJobLog(job, log);
            jobs.add(job);
        }
        experiment.start();
        return jobs;
    }

    private void waitJobs(List<Job> jobs)
            throws IOException, InterruptedException {
        for(int i = 0; i != runs; ++i) {
            Job job = jobs.get(i);
            JobFinished st = StatusWaitingUtils.waitFor(job);
            System.out.printf("Job %d: %s\n", i, st);
            IDirectory resdir = st.outputDirectory;
            for (Map.Entry<String, IFile> kv : resdir.files().entrySet()) {
                kv.getValue().writeTo(
                        Paths.get(String.format("%02d-%s", i, kv.getKey())));
            }
        }
    }

    private String[] parseArgs(String[] args) {
        Options opts = new Options();
        Option opt;
        opt = new Option("d", "directory", true, "Input file directory");
        opt.setArgName("dir");
        opts.addOption(opt);
        opt = new Option("p", "profile", true, "Apros profile directory");
        opt.setArgName("dir");
        opts.addOption(opt);
        opt = new Option("l", "logfile", true, "Job log file name template");
        opt.setArgName("template");
        opts.addOption(opt);
        opt = new Option("r", "results", true, "Result file");
        opt.setArgName("file");
        opts.addOption(opt);
        opt = new Option("n", "runs", true, "Number of runs");
        opt.setArgName("num");
        opts.addOption(opt);
        opt = new Option("c", "cores", true, "Number of CPU cores");
        opt.setArgName("num");
        opts.addOption(opt);
        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cline = parser.parse(opts, args);
            String s;
            if ((s = cline.getOptionValue("directory")) != null)
                dirname = s;
            if ((s = cline.getOptionValue("profile")) != null)
                profile = s;
            if ((s = cline.getOptionValue("logfile")) != null)
                logTemplate = s;
            if ((s = cline.getOptionValue("results")) != null)
                resfile = s;
            if ((s = cline.getOptionValue("runs")) != null)
                runs = Integer.valueOf(s);
            if ((s = cline.getOptionValue("cores")) != null)
                cores = Integer.valueOf(s);
            String[] files = cline.getArgs();
            if (files.length == 0)
                throw new ParseException("No simulation sequence given");
            return files;
        } catch (ParseException | NumberFormatException e) {
            System.err.println(e.getMessage());
            usage(opts);
            return null;
        }
    }

    private void usage(Options opts) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(
                appname + " [options] sequence.scl [data files ...]",
                opts);
    }
}
