import java.io.InputStreamReader;
import java.io.BufferedReader;


public class CheckOpenPrograms {
    public static void main(String[] args) {
        System.out.println(isProcess());
    }

    public static boolean isProcess() {
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
            }
            input.close();
            System.out.println("---> Good: " + num_good + ", bad: " + num_bad);
            return (num_good <= 2 && num_bad >= 2);
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }
}
