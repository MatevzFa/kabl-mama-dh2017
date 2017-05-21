import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.net.*;
import java.io.*;

public class MakeMe {
	// SPLOSNE SPREMENLJIVKE
	static boolean token = true;
	static int activeHourStart = 0;
	static int activeHourEnd = 40;
	static boolean vreme = false;
	
	static Thread pozivThread;
	
	public static void main(String[] args){
		// Main tread tece v ozadju
		
		//System.out.println(Blokada.readServer());
		vreme = isWeather();
		
		// Caka na primerne pogoje za sprozitev blokade
		while(true){
			// Ce so vsi trije pogoji izpolnjeni sprozi blokado
			if(isHour() && vreme && isProcess()){
				// Prikaze obvestilo o 15 minutnem odstevanju - v novm thread-u
				(new Thread(new Poziv())).start();
				// Zaspi za 15 minut
				try {
				    Thread.sleep(15000);	// CHANGED FROM 15-1
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				// Pricne blokado - v novm thread-u
				Thread blokada = (new Thread(new Blokada()));
				blokada.start();
				//caka dokler blokada ne prekine dela;
				synchronized(blokada){
		            try{
		                //System.out.println("Waiting for b to complete...");
		                blokada.wait();
		            }catch(InterruptedException e){
		                e.printStackTrace();
		            }
		        }
			break;
			}
		}
	}
	
	/*
	* POMOZNE FUNKCIJE
	*/
	
	// Funkcija vrne true, ce je cas znotraj dolocenega obmocja
	public static boolean isHour(){
		//CAS
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		//int minute = rightNow.get(Calendar.MINUTE);
		if(hour > activeHourStart && hour < activeHourEnd ){
			return true;
		}
		return false;
	}
	
	// Funkcija vrne true, ce je vreme primerno
	public static boolean isWeather(){
		return true;
	}
	
	// Funkcija vrne true, ce je delo nepomembno
	public static boolean isProcess(){
		int isP = checkExe();
		// nic ni odprto
		if (isP == 0)return false;
		if (isP > 1) return true;
		
		String stream=null;
    	try{
    		stream = getIn();
    	}
    	catch(Exception e){
    		Thread.currentThread().interrupt();
    	}
    	System.out.println((stream.equals("true") ? "Bad tabs exist" : "Bad tabs don't exist"));
    	return stream.equals("true");
	}
	
	// Funkcija vrne stream za tabe-chrom extension
	public static String getIn() throws Exception {
        URL str = new URL("https://kabl-mama-dh2071-matevzfa.c9users.io/existbadtabs");
        URLConnection con = str.openConnection();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine=in.readLine();

        in.close();
        return inputLine;
    }
	
	public static int checkExe(){
		try {
            String line;

            Process p = null;
            try {
                System.out.println(System.getProperty("os.name"));
                if (System.getProperty("os.name").equals("Linux")) {
                    p = Runtime.getRuntime().exec("ps -e -Ao '%a'");
                } else {
                    p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
                }
            } catch (Exception e) {
                ;
            }


            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String[] good_programs = new String[] {"atom", "eclipse", "word", "bash"};
            String[] bad_programs = new String[] {"vlc"};

            int num_good = 0;
            int num_bad = 0;
            boolean chrome = false;
            while ((line = input.readLine()) != null) {
                //System.out.println(line);
                for (int i = 0; i < good_programs.length; i++) {
                    if (line.contains(good_programs[i])) {
                        //System.out.println("---> Good one!");
                        good_programs[i] = "*** Ime procesa, ki zagotovo ne obstaja.";
                        num_good++;
                    }
                }
                for (int i = 0; i < bad_programs.length; i++) {
                    if (line.contains(bad_programs[i])) {
                        //System.out.println("---> Bad one!");
                        bad_programs[i] = "*** Ime procesa, ki zagotovo ne obstaja.";
                        num_bad++;
                    }
                }
                if (line.contains("chrome")) {
                    chrome = true;
                }
            }
            input.close();
            System.out.println("---> Good: " + num_good + ", bad: " + num_bad);

            /**
                return | numgood <= 1 AND numbad >= 2    chrome
                    0  |               0                   0
                    1  |               0                   1 
                    2  |               1                   0
                    3  |               1                   1
             */
            boolean goodBad = (num_good <= 1 && num_bad >= 2);
            int retval = 0;
            if (goodBad && chrome) {
                retval = 3;
            } else if (goodBad && !chrome) {
                retval = 2;
            } else if (!goodBad && chrome) {
                retval = 1;
            }
            return retval;
            
        } catch (Exception err) {
            err.printStackTrace();
            return -1;
        }
    }
}

/*
 * DRUGI RAZREDI
*/
class Poziv implements Runnable{
		
    public void run() {
    	createFramePoziv();
    }
    
    // Funkcija ustvari pop up allert poziv
    public static void createFramePoziv(){
    	//Create the frame.
    	JFrame poziv = new JFrame("Poziv");
    	poziv.toFront();
    	//What happens when the frame closes
    	poziv.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	
    	//Create components and put them in the frame.
    	JLabel label = new JLabel("You have 15 minutes to stop waisting your time.", SwingConstants.CENTER);
    	label.setFont(new Font("Serif", Font.BOLD,55));
    	poziv.add(label);
    	//4. Size the frame.
    	poziv.setSize(new Dimension(1200, 500));
    	//5. Show it.
    	poziv.setVisible(true);
    	poziv.setLocationRelativeTo(null);
    	poziv.setAlwaysOnTop(true);
    	
    }
}

class Blokada implements Runnable{
	static JFrame blokada;
    public void run() {
    	// Ustvari Frame Blokada (POP UP)
    	createFrameBlokada();
    	boolean hasToken = false;
    	//Dokler iz streznika ne dobi sporocila o aktivnem token zetonu
    	while(!hasToken){
    		//Sleep for one second
    		try {
			    Thread.sleep(1000);	
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
    		//bere s streznika + ce dobi pozitiven token signal to javi ocetu-thredu
    		if(readServer()){
    			synchronized(this){
                    notify();
                }
        		blokada.setVisible(false);
    			break;
    		}
    	}
    	
    }
    
    // Funkcija ustvari pop up allert poziv
    public static void createFrameBlokada(){
    	//Create the frame.
    	blokada = new JFrame("Blokada");
    	blokada.setResizable(false);
    	blokada.setUndecorated(true);
    	//What happens when the frame closes
    	blokada.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	
    	//Create components and put them in the frame.
    	JLabel label = new JLabel("BLOCKED", SwingConstants.CENTER);
    	label.setFont(new Font("Serif", Font.BOLD,95));
    	blokada.add(label);
    	//4. Size the frame.
    	blokada.setExtendedState(JFrame.MAXIMIZED_BOTH);

    	//5. Show it.
    	
    	blokada.setVisible(true);
    	blokada.setLocationRelativeTo(null);
    	blokada.setAlwaysOnTop (true);

    }
    
    
    // Bere s streznika
    public static boolean readServer(){
    	String stream=null;
    	try{
    		stream = getIn();
    		System.out.println(stream);
    	}
    	catch(Exception e){
    		Thread.currentThread().interrupt();
    	}
    	return stream.equals("continue");
    }
    public static String getIn() throws Exception {
        URL str = new URL("https://kabl-mama-dh2071-matevzfa.c9users.io/cancontinue");
        URLConnection con = str.openConnection();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine=in.readLine();

        in.close();
        return inputLine;
    }
}
