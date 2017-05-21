import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
			if(isHour() && vreme && isProcess() && Blokada.readServer()){
				// Prikaze obvestilo o 15 minutnem odstevanju - v novm thread-u
				(new Thread(new Poziv())).start();
				// Zaspi za 15 minut
				try {
				    Thread.sleep(5000);	// CHANGED FROM 15-1
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
		return true;
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

    	//What happens when the frame closes
    	poziv.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	
    	//Create components and put them in the frame.
    	JLabel label = new JLabel("IMAS 15 MINUT DO BLOKADE", SwingConstants.CENTER);
    	poziv.add(label);
    	//4. Size the frame.
    	poziv.setSize(new Dimension(400, 300));

    	//5. Show it.
    	poziv.setVisible(true);
    	poziv.setLocationRelativeTo(null);
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
    		if(!readServer()){
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

    	//What happens when the frame closes
    	blokada.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	
    	//Create components and put them in the frame.
    	JLabel label = new JLabel("BLOKADA", SwingConstants.CENTER);
    	blokada.add(label);
    	//4. Size the frame.
    	blokada.setSize(new Dimension(1600, 900));

    	//5. Show it.
    	blokada.setVisible(true);
    	blokada.setLocationRelativeTo(null);
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
    	return stream.equals("true");
    }
    public static String getIn() throws Exception {
        URL str = new URL("https://kabl-mama-dh2071-matevzfa.c9users.io/existbadtabs");
        URLConnection con = str.openConnection();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine=in.readLine();

        in.close();
        return inputLine;
    }
}
