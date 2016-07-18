import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//reads a list of values from an inputfile and generates the timeseriesvals as xml
//tseriesid, startTime[format: dd.MM.yyyyHH:mm:ss], inputfile, outputfile and startID
//can be set via commandline parameters or overwriting in code
//time will be increased by 1h for each value, starting with startTime
public class TimeSeriesGenerator {

	public static void main(String[] args) throws Exception {
		//TODO use Apache Commons CLI for arguments?
		if(args.length != 5){
			printUsage();
			return;
		}
		
		//starttime
		Calendar cal = Calendar.getInstance();
		cal.clear();		
	    DateFormat dfIn = new SimpleDateFormat("dd.MM.yyyyHH:mm:ss");
	    DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dfOut.setTimeZone(TimeZone.getTimeZone("UTC"));
	    dfIn.setTimeZone(TimeZone.getTimeZone("UTC"));
	    
		//startID
	    int startID =0;
		//tseriesID
	    int tseriesID =0;
		//inpufile name
		String inputfile = "in.txt";
		//outputfile name
		String outputfile = null;
		try{
			tseriesID = Integer.parseInt(args[0]);
			//set start date
			String target = args[1];
			Date date =  dfIn.parse(target); 
			cal.setTime(date);
			//inputfile
			inputfile = args[2];
			//outputfile
			outputfile = args[3];
			//startID
			startID = Integer.parseInt(args[4]);
		}
		catch(Exception Ex){
			printUsage();
			return;
		}
//		tseriesID=5;
//		startID=35041;
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
        String line;
        PrintWriter writer = new PrintWriter("out.xml", "UTF-8");

        int tseriesvalid = startID;
        while((line = br.readLine()) != null) {
        	String output = "<timeseriesval tseriesvalid=\""
        			+ tseriesvalid
        			+ "\" tseriesid=\""
        			+ tseriesID
        			+ "\" time=\""
        			+ dfOut.format(cal.getTime())
        			+ "\" value=\""
        			+ line
        			+ "\" />";     
//        	System.out.println(output);
            writer.println(output);
            tseriesvalid++;
            cal.add(Calendar.HOUR, 1); //add one h
        }
        br.close();
        writer.close();
		
        System.out.println(String.format("lines processed: %d", tseriesvalid-startID));
	}

	public static void printUsage(){
		System.out.println("usage: TimeSeriesGenerator tseriesid startTime "
				+ "[format: dd.MM.yyyyHH:mm:ss] inputfile outputfile startID");
	}
}
