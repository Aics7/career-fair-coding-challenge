import java.io.*;
import java.util.*;
import java.lang.*;
/**
 * @author Issifu Alhassan
 * @version 1.0.0
 * Issifu Class, a class that contains the main method
 */
class Issifu{
    //strings to contain names of input files
    private static String covid="",population="",outputFileName="";
    private static Double totalDeath=0.0;
    private static boolean wait=true;

    //hash table to store final answers
    static Hashtable<Integer,String> answers = new Hashtable<Integer,String>();
    //hashtable to store country name and number of infections recorded
    static Hashtable<String,Integer> mytable = new Hashtable<String,Integer>();
    //Array in hashtable contains atmost infections for the most recent 7 days
    static Hashtable<String,Double[]> weekly = new Hashtable<String,Double[]>();
    //Array in hashtable contains entire infections for the the country
    static Hashtable<String,String[]> entire = new Hashtable<String,String[]>();

    public static void main(String args[]){
        //checking for availability of input and assigning them to variables
        if(args.length>0){ covid = args[0]+".csv"; outputFileName="task1_solution-"+args[0]+".txt";}
        else{System.out.println("Fewer input argument");return;}
        if(args.length>1){ population = args[1]+".csv";}
        else{System.out.println("Fewer input argument"); wait=false;}

        //calling infectionNumber method
        infectionNumber();
        //Creating instance of InfectionRate and starting a thread to run it
        InfectionRate inf_rate = new InfectionRate();
        Thread t1 = new Thread(inf_rate);
        t1.start();
        //Creating instance of OverallDeathRate and starting a thread to run it
        OverallDeathRate d_rate = new OverallDeathRate();
        Thread t2 = new Thread(d_rate);
        t2.start();
        //Creating instance of HighestDeathRate and starting a thread to run it
        HighestDeathRate hd_rate = new HighestDeathRate();
        Thread t3 = new Thread(hd_rate);
        t3.start();
        //Creating instance of RiseCountries and starting a thread to run it
        RiseCountries r_countries = new RiseCountries();
        Thread t4 = new Thread(r_countries);
        t4.start();
        //Creating instance of DecreaseCountries and starting a thread to run it
        DecreaseCountries d_countries = new DecreaseCountries();
        Thread t5 = new Thread(d_countries);
        t5.start();
        //Creating instance of PrintAnswers and starting a thread to run it
        PrintAnswers printAnswers = new PrintAnswers();
        Thread t6 = new Thread(printAnswers);
        t6.start();
    }
    /**Accessor method for covid variable */
    public static String getCovid(){return covid;}

    /**Accessor method for population variable */
    public static String getPopulation(){return population;}

    /**Accessor method for outputFileName variable */
    public static String getOutputFileName(){return outputFileName;}

    /**Accessor method for totalDeath variable */
    public static Double getTotalDeath(){return totalDeath;}
    /**Accessor method for wait variable */
    public static boolean getWait(){return wait;}
    /**Mutator method for wait variable */
    public static void setWait(boolean wait){Issifu.wait=wait;}

    /**infectionNumber method to get the first and second highest number of infections
     * It also computes the total death recorded
     * It also inserts into a hash table each country and its record for the most recent week if available.
     * */
    public static void infectionNumber(){
        //using try-catch to read input csv files
        try{
            String country="",highestCountry="None",secondHighestCountry="None";
            Integer numOfInfections=0,highestInfections=0,secondHighestInfections=0;
            Double curNum=0.0;
            Double[] week={};
            String [] ent={};
            BufferedReader br = new BufferedReader(new FileReader(Issifu.getCovid()));
            String st;
            if(br.readLine()==null){System.out.println("covid data file is empty"); return;}//reads the firstLine (titles)
            while ((st = br.readLine()) != null) {
                country=st.split(",")[1]; //obtains country name
                numOfInfections=Integer.parseInt(st.split(",")[2]); //obtains number of infections
                curNum=Double.parseDouble(numOfInfections.toString());
                totalDeath+=Double.parseDouble(st.split(",")[3]);
                //inserting data into the weekly hashtable
                if(weekly.containsKey(country)){
                    week=weekly.get(country);
                    if(week.length<7){
                        Double [] newWeek = new Double[week.length+1];
                        for (int i=0; i<week.length;i++){
                            newWeek[i]=week[i];
                        }
                        newWeek[week.length]=curNum;
                        weekly.put(country,newWeek);
                    }
                }
                else{
                    week=new Double[1];
                    week[0]=curNum;
                    weekly.put(country,week);
                }
                //inserting data into the hashtable called entire
                if(entire.containsKey(country)){
                    ent=entire.get(country);
                    String [] newEnt = new String[ent.length+1];
                    for (int i=0; i<ent.length;i++){
                        newEnt[i]=ent[i];
                    }
                    newEnt[ent.length]=st;
                    entire.put(country,newEnt);
                }
                else{
                    ent=new String[1];
                    ent[0]=st;
                    entire.put(country,ent);
                }
                //Computes highest and second highest number of infections
                if(numOfInfections>highestInfections){
                    if(!country.equalsIgnoreCase(highestCountry)){
                        secondHighestInfections=highestInfections;
                    }
                    highestInfections=numOfInfections;
                    if(!country.equalsIgnoreCase(highestCountry)){
                        secondHighestCountry=highestCountry;
                        highestCountry=country;
                    }
                }
                else if(numOfInfections>secondHighestInfections && !country.equalsIgnoreCase(highestCountry)){
                    secondHighestInfections=numOfInfections;
                    secondHighestCountry=country;
                }
                //puts data into hashtable
                Integer oldValue = mytable.put(country,numOfInfections);
                if(!(oldValue==null)){ //if country already exist in hashtable
                    Integer newValue=oldValue+numOfInfections;
                    oldValue=mytable.put(country,newValue);
                    if(newValue>highestInfections){
                        if(!country.equalsIgnoreCase(highestCountry)){
                            secondHighestInfections=highestInfections;
                        }
                        highestInfections=newValue;
                        if(!country.equalsIgnoreCase(highestCountry)){
                            secondHighestCountry=highestCountry;
                            highestCountry=country;
                        }
                    }
                    else if(newValue>secondHighestInfections && !country.equalsIgnoreCase(highestCountry)){
                        secondHighestInfections=newValue;
                        secondHighestCountry=country;
                    }
                }
            }
            br.close();
            //creating answer strings and inserting into the answers hash table
            String ans="(a) "+highestCountry + ", "+ highestInfections;
            answers.put(1,ans);
            ans = "(b) "+secondHighestCountry + ", "+ secondHighestInfections;
            answers.put(2,ans);
        }
        catch(FileNotFoundException e){
            System.out.println("The specified file could not be found, answers will not make sense");
            for(Integer i=1; i<11;i++){
                Issifu.answers.put(i,"");
            }
        }
        catch(Exception e){
            System.out.println("Input file does not satisfy required format, answers will not make sense");
            for(Integer i=1; i<11;i++){
                Issifu.answers.put(i,"");
            }
        }
    }
    /**A method that returns the gradient of the line of best fit for a given set of data */
    public static Double bestFitGradient(Double infections[]){
        int n = infections.length;
        Double m=0.0, c=0.0, sumDays = 0.0, sumOfInfections = 0.0, sumDaysXinfections = 0.0, sumDaysSq = 0.0;
        for (int i = 0; i < n; i++) {
            sumDays += i+1;
            sumOfInfections += infections[n-(i+1)];
            sumDaysXinfections += (i+1) * infections[n-(i+1)];
            sumDaysSq += Math.pow((i+1), 2);
        }
        m = (n * sumDaysXinfections - sumDays * sumOfInfections) / (n * sumDaysSq - Math.pow(sumDays, 2));
        c = (sumOfInfections - m * sumDays) / n;
        return m;
    }
}
/**
 * InfectionRate class computes the infection rates for countries and finds the highest.
 * It implements The runnable interface to make multi-threading possible
 * Hence, the computation is done in the run() method
 */
class InfectionRate implements Runnable{
    @Override
    public void run() {
        try{
            String country="",highestCountry="None";
            double rate=0.0,highestRate=0.0;
            Double population=0.0;
            BufferedReader br = new BufferedReader(new FileReader(Issifu.getPopulation()));
            String st;
            br.readLine();//reads the firstLine (titles)
            while ((st = br.readLine()) != null){
                country = st.split(",")[0]; //obtains country name
                String[] temp = st.split(",");
                population = Double.parseDouble(temp[temp.length-2]); //obtains population
                if(Issifu.mytable.containsKey(country)){
                    //finding rate by dividing number of infections by the population
                    rate=Double.parseDouble(Issifu.mytable.get(country).toString())/population;
                    //compares rates to find the highest rate and its country
                    if(rate>highestRate){
                        highestRate=rate;
                        highestCountry=country;
                    }
                }
            }
            br.close();
            //creating answer string and inserting into the answer hashtable
            String ans="(c) "+ highestCountry+", "+highestRate*100 + " %";
            Issifu.answers.put(3,ans);
        }
        catch(FileNotFoundException e){
            System.out.println("The specified file could not be found, Answer for c will be null");
            Issifu.setWait(false);
        }
        catch(Exception e){
            System.out.println(e.toString());
            System.out.println("exception");
        }
    }
}
/**
 * OverallDeathRate class computes the overall death rate based on the dataset given
 * It implements The runnable interface to enable multi-threading
 * Hence, the computation is done in the run() method
 */
class OverallDeathRate implements Runnable{
    @Override
    public void run() {
        Double totalInfection=0.0;
        //getting a set of country names from the mytable hash table
        Set<String> keys=Issifu.mytable.keySet();
        //finding total infection by looping through hash table and summing up infections
        for(String key : keys){
            totalInfection+=Double.parseDouble(Issifu.mytable.get(key).toString());
        }
        //computes death rate by dividing total death by total infection and inserting result into the answer hash table
        String ans="(d) "+(Issifu.getTotalDeath()/totalInfection)*100 + " %";
        Issifu.answers.put(4,ans);
    }
}
/**
 * HighestDeathRate class finds the death rate per country and finds the country with the highest rate
 * It implements The runnable interface to enable multi-threading
 * Hence, the computation is done in the run() method
 */
class HighestDeathRate implements Runnable{
    @Override
    public void run() {
        //hash table to hold countries and the number of deaths recorded
        Hashtable<String,Integer> deaths = new Hashtable<String,Integer>();
        try{
            String country="",highestCountry="None";
            Double rate=0.0,highestRate=0.0;
            Integer numOfDeaths=0;
            BufferedReader br = new BufferedReader(new FileReader(Issifu.getCovid()));
            String st;
            //reads the firstLine (titles)
            br.readLine();
            while ((st = br.readLine()) != null) {
                country=st.split(",")[1]; //obtains country name
                numOfDeaths=Integer.parseInt(st.split(",")[3]); //obtains number deaths
                //puts data into hashtable
                Integer oldValue = deaths.put(country,numOfDeaths);
                //if country already exist in hashtable, it updates by adding old value to new value
                if(!(oldValue==null)){
                    Integer newValue=oldValue+numOfDeaths;
                    oldValue=deaths.put(country,newValue);
                }
            }
            br.close();
            //obtaining a set of country names from deaths hashtable
            Set<String> keys=deaths.keySet();
            for(String key : keys){
                //computing death rate by dividing nunber of deaths by number of infections
                rate=Double.parseDouble(deaths.get(key).toString())/Double.parseDouble(Issifu.mytable.get(key).toString());
                //does comparisons to find the highest death rate and the country it corresponds to
                if(rate>highestRate){
                    highestCountry=key;
                    highestRate=rate;
                }
            }
            //creating answer string and inserting into the answers hash table
            String ans = "(e) "+highestCountry + ", "+highestRate*100 + " %";
            Issifu.answers.put(5,ans);
        }
        catch(FileNotFoundException e){System.out.println("The specified file could not be found");}
        catch(Exception e){System.out.println(e.getMessage());}
    }
}
/**
 * RiseCountries class determines the countries that have a rising trend for the most recent week
 * It implements The runnable interface to enable multi-threading
 * Hence, the computation is done in the run() method
 */
class RiseCountries implements Runnable{
    @Override
    public void run() {
        //Creating hash table to store countries with positive trends and their slopes
        Hashtable<String, Double> rise = new Hashtable<String, Double>();
        //Obtaining a set of country names from the weakly hash table
        Set<String> keys=Issifu.weekly.keySet();

        Double slope=0.0,steepestIncrease=0.0;
        String steepestICountry="None";
        //loops through the weekly hash table and computes the slope of each country for the most recent week
        for(String key : keys) {
            //calls the bestFitGradient method from the Issifu class. this method returns the gradient of the line of best fit
            slope=Issifu.bestFitGradient(Issifu.weekly.get(key));
            //adds country to the rise hash table if the country has a positive slope
            if( slope>0.0){rise.put(key,slope);}
            //does comparison to find the steepest slope and its country
            if(slope>steepestIncrease){
                steepestIncrease=slope;
                steepestICountry=key;
            }
        }
        //Creating answer strings and putting them into the answers hash table
        String ans = ""+rise.keySet();
        ans = ans.substring(1,ans.length()-1);//strips off "[" aand "]" from ans
        ans = "(f) " + ans;
        Issifu.answers.put(6,ans);
        ans = "(g) "+ steepestICountry;
        Issifu.answers.put(7,ans);
    }
}
/**
 * DecreaseCountries class determines the countries that have a negative trend for the most recent week
 * It implements The runnable interface to enable multi-threading
 * Hence, the computation is done in the run() method
 */
class DecreaseCountries implements Runnable{
    @Override
    public void run() {
        //Creating hash table to store countries with negative trends and their slopes
        Hashtable<String, Double> decrease = new Hashtable<String, Double>();
        //Obtaining a set of country names from the weakly hash table
        Set<String> keys=Issifu.weekly.keySet();

        Double slope=0.0,steepestDecrease=0.0;
        String steepestDCountry="None";
        //loops through the weekly hash table and computes the slope of each country for the most recent week
        for(String key : keys) {
            //calls the bestFitGradient method from the Issifu class. this method returns the gradient of the line of best fit
            slope=Issifu.bestFitGradient(Issifu.weekly.get(key));
            //adds country to the decrease hash table if the country has a negative slope
            if( slope<0.0){decrease.put(key,slope);}
            //does comparison to find the steepest slope and its country
            if(slope<steepestDecrease){
                steepestDecrease=slope;
                steepestDCountry=key;
            }
        }
        //Creating answer strings and putting them into the answers hash table
        String ans = "" + decrease.keySet();
        ans = ans.substring(1,ans.length()-1);//strips off "[" aand "]" from ans
        ans = "(h) " + ans;
        Issifu.answers.put(8,ans);
        ans = "(i) "+ steepestDCountry;
        Issifu.answers.put(9,ans);

        String earliestPeakDate="",earliestPeakCountry="None";
        String [] dataArray={},dailyArr={};
        //Obtains a set of countries with negative trend
        Set<String> decreaseKeys=decrease.keySet();
        //looping through to find which country reached its peak earliest
        for(String key: decreaseKeys){
            //an array to store data for the current country being processes
            dataArray=Issifu.entire.get(key);
            String peakDate="";
            int peakValue=0;
            for(String daily: dataArray){
                dailyArr=daily.split(",");
                //does comparison to find the max infections per day(peak) recorded and obtains the peak and its date
                if(Integer.parseInt(dailyArr[2])>peakValue){
                    peakValue=Integer.parseInt(dailyArr[2]);
                    peakDate=dailyArr[0];
                }
            }
            //does comparison to find which country has the earliest peak
            //it calls the "earlier" method which is a method that compares dates
            if(earliestPeakDate.equalsIgnoreCase("")|| earlier(peakDate,earliestPeakDate)){
                earliestPeakDate=peakDate;
                earliestPeakCountry=key;
            }
        }
        //Creating answer strings and putting them into the answers hash table
        ans = "(j) "+ earliestPeakCountry + ", "+ earliestPeakDate;
        Issifu.answers.put(10,ans);
    }
    /** Returns true if date1 is earlier than date2 and false otherwise */
    public static boolean earlier(String date1,String date2){
        //splitting dates into arrays
        String [] dateOne=date1.split("/");
        String [] dateTwo=date2.split("/");
        try {
            //checks if inputs are empty strings
            if(date2.equalsIgnoreCase("")){ return true; }
            else if(date1.equalsIgnoreCase("")) { return false; }
            //comparing years
            if(Integer.parseInt(dateOne[2])>Integer.parseInt(dateTwo[2])){ return false; }
            //comparing months
            else if(Integer.parseInt(dateOne[0])>Integer.parseInt(dateTwo[0])){ return false; }
            //comparing days
            else if(Integer.parseInt(dateOne[1])>Integer.parseInt(dateTwo[1])){ return false; }
            else{return true;}
        }
        catch (Exception e){ return false; }
    }
}
/**
 * PrintAnswers class retrieces data from the answers hash table and print them to a textfile
 * It equally implements the runnable interface for multithreading
 */
class PrintAnswers implements Runnable{
    public void run(){
        try {
            //Creating a FileWriter and BufferedWriter objects
            FileWriter writer = new FileWriter(Issifu.getOutputFileName());
            BufferedWriter br = new BufferedWriter(writer);
            String finalAnswer="";
            //looping through answers hash table and appends anwers to form a long string
            for(Integer i=1; i<11;i++){
                //waits until the data is present so that answers are printed in order (because of multithreading)
                while(!(Issifu.answers.containsKey(i))){if(!(Issifu.getWait()) && i==3){break;}}
                finalAnswer+=Issifu.answers.get(i)+"\n";
            }
            //now printing answer into textfile
            br.write(finalAnswer);
            br.close();
            System.out.println("Done!");
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (Exception e){System.out.println("An error occured");}
    }
}