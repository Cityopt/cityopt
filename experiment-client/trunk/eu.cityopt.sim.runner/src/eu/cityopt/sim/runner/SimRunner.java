package eu.cityopt.sim.runner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.simantics.experiment.client.Experiment;
import org.simantics.experiment.client.ExperimentJob;
import org.simantics.experiment.client.SimulationServer;
import org.simantics.experiment.core.common.ExperimentException;
import org.simantics.experiment.core.directory.IDirectory;
import org.simantics.experiment.core.directory.MapDirectory;
import org.simantics.experiment.core.file.IFile;
import org.simantics.experiment.core.file.LocalFile;

/**
 * This class controls all aspects of the application's execution
 */
public class SimRunner implements IApplication {
    private String
        appname = "simrun",
        server = "zk:localhost:2181",
        dirname = ".",
        profile = "Apros-N3D-5.13.06-64bit",
        resfile = "results.dat";
    private int runs = 1;

    @Override
    public Object start(IApplicationContext context) throws Exception {
        //appname = context.getBrandingName();
        String[] files = parseArgs(Platform.getApplicationArgs());
        if (files == null)
            //return 1;
            return IApplication.EXIT_OK;
        System.out.println("Server: " + server);
        Path fdir = Paths.get(dirname);
        MapDirectory mdir = new MapDirectory();
        for (String f : files)
            mdir.put(f, new LocalFile(fdir.resolve(f).toFile()));
        SimulationServer srv = new SimulationServer(server);
        Experiment experiment = srv.createExperiment(
                new HashMap<String, Object>());

        waitJobs(startJobs(experiment, files[0], mdir));

        experiment.remove();
        srv.dispose();

        return IApplication.EXIT_OK;
    }

    private List<ExperimentJob> startJobs(Experiment experiment, String script,
                                          MapDirectory mdir)
            throws ExperimentException {
        List<ExperimentJob> jobs = new ArrayList<ExperimentJob>();

        for (int i = 0; i != runs; ++i) {
            Map<String, Object> jobDescription = new HashMap<String, Object>();
            jobDescription.put("launcher", "profile");
            jobDescription.put("profile", profile);
            String[] cmdParameters = {
                    script, String.valueOf(i)
            };
            jobDescription.put("parameters", cmdParameters);
            jobDescription.put("inputFiles", experiment.put(mdir));
            jobDescription.put("outputFiles", resfile);
            jobs.add(experiment.createJob(jobDescription));
        }
        return jobs;
    }

    private void waitJobs(List<ExperimentJob> jobs)
            throws ExperimentException, IOException {
        for(int i = 0; i != runs; ++i) {
            IDirectory result = jobs.get(i).getResult();
            IFile r = result.getFile(resfile);
            try (OutputStream ostr = new FileOutputStream(
                    String.format("%02d-%s", i, resfile))) {
                r.copyTo(ostr);
            }
        }
    }

    private String[] parseArgs(String[] args) {
        Options opts = new Options();
        Option opt = new Option("s", "server", true, "Server connection");
        opt.setArgName("zk:server:port");
        opts.addOption(opt);
        opt = new Option("d", "directory", true, "Input file directory");
        opt.setArgName("dir");
        opts.addOption(opt);
        opt = new Option("p", "profile", true, "Apros profile");
        opt.setArgName("prof");
        opts.addOption(opt);
        opt = new Option("r", "results", true, "Result file");
        opt.setArgName("file");
        opts.addOption(opt);
        opt = new Option("n", "runs", true, "Number of runs");
        opt.setArgName("num");
        opts.addOption(opt);
        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cline = parser.parse(opts, args);
            String s;
            if ((s = cline.getOptionValue("server")) != null)
                server = s;
            if ((s = cline.getOptionValue("directory")) != null)
                dirname = s;
            if ((s = cline.getOptionValue("profile")) != null)
                profile = s;
            if ((s = cline.getOptionValue("results")) != null)
                resfile = s;
            if ((s = cline.getOptionValue("runs")) != null)
                runs = Integer.valueOf(s);
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

    @Override
    public void stop() {}
}
