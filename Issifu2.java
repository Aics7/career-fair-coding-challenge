import java.io.*;
import java.util.*;
import java.lang.*;
/**
 * @author Issifu Alhassan
 * @version 1.0.1
 * Issifu2 Class, a class that that contains the main method
 */
class Issifu2{
    //strings to contain names of input and output files
    private static String covid = "";
    private static String time = "";
    private static String outputFileName="";
    static int len=0;
    static String dateEnded="";
    //Creating ArrayLists to store data from input files
    static ArrayList<String> timeseries=new ArrayList<String>();
    static ArrayList<String> countries=new ArrayList<String>();
    static ArrayList<String> countryData=new ArrayList<String>();
    public static void main(String args[]) {
        //checking for availability of input
        if (args.length > 0) { covid = args[0] + ".csv"; }
        else {System.out.println("Fewer input argument"); return; }
        if (args.length > 1) { time = args[1] + ".csv";outputFileName="task2_solution-"+args[1]+".txt"; }
        else {System.out.println("Fewer input argument"); return; }
        //calling findCountry method
        findCountry();
        //checks to see if country is found in data, if yes, countryData will not be empty
        if(!(countryData.isEmpty())){
            //obtaining country name
            String country=countryData.get(0).split(",")[1];
            //obtaining start date
            String dateStarted=countryData.get(len-1).split(",")[0];
            try {
                //Creating a FileWriter and BufferedWriter objects
                FileWriter writer = new FileWriter(outputFileName);
                BufferedWriter br = new BufferedWriter(writer);
                String finalAnswer=country +"\n"+dateStarted;
                //now printing answer into textfile
                br.write(finalAnswer);
                br.close();
                System.out.println("Done!");
            }
            catch (IOException e) { e.printStackTrace(); }
            catch (Exception e){System.out.println("An error occured");}
        }
        else{ System.out.println("Sorry, country not found"); }
    }
    /**findCountry method, goes through data and tries to find the country whose data corresponds to the time series provided */
    public static void findCountry(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(time));
            if(br.readLine()==null){System.out.println("Times series file is empty"); return;}//reads first line
            String st;
            //puts time series data into the timeseries ArrayList
            while ((st = br.readLine()) != null) { timeseries.add(st); }
            br.close();
            //reading covid data file
            br=new BufferedReader(new FileReader(covid));
            if(br.readLine()==null){System.out.println("covid data file is empty"); return;}//reads first line
            //puts data into the countries ArrayList
            while ((st = br.readLine()) != null) { countries.add(st); }
            //finding the lengths of ArrayLists
            len=timeseries.size();
            int countLen=countries.size();
            for(int i=0; i<countLen;i++){
                //checks if first numbers match
                if(countries.get(i).split(",")[2].equalsIgnoreCase(timeseries.get(0))){
                    //loops through to compare remaining numbers
                    for(int j=0;j<len;j++){
                        //compares infections and adds to countryData ArrayList of there is a match
                        if(countries.get(i+j).split(",")[2].equalsIgnoreCase(timeseries.get(j))){
                            countryData.add(countries.get(i+j));
                        }
                        else{
                            //Assigns countryData ArrayList to an empty list and terminates the loop if there is a mismatch
                            countryData=new ArrayList<String>();
                            break;
                        }
                    }
                    if(!(countryData.isEmpty())){
                        //checks to see if all matching data belong to the same country, if yes, no need to continue looking
                        if(countryData.get(0).split(",")[1].equalsIgnoreCase(countryData.get(len-1).split(",")[1])){
                            return;
                        }
                        //if no, countryData is emptied and the search continues
                        else{countryData=new ArrayList<String>();}
                    }
                }
            }
        }
        catch(FileNotFoundException e){System.out.println("The specified file(s) could not be found");}
        catch(Exception e){System.out.println(e.getMessage());}
    }
}