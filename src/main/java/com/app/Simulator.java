package com.app;
import com.app.blockchain.Blockchain;
import com.app.blockchain.CenterAuthority;
import com.app.mongodb.RepositoryFNs;
import com.app.user.Clinic;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Scanner;

public class Simulator {

    //the existing clinics in the system
    Hashtable<String,Clinic> existingClinics;

    //the blockchain object
    Blockchain blockchain = new Blockchain(this);

    //the Certificate/Authority Center
    CenterAuthority centerAuthority = new CenterAuthority(this);


    public Simulator(){
        existingClinics = new Hashtable<>();
        clinicsInit();    //get the clinics registered in the system
        //clinics();          //initialize/add new clinics to the system
    }

    public void run() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        //draw some ASCII ART at the beginning of the application :)
        ASCIIStart();

        //take an input from the user, [1 -> continue as a User] [2 -> continue as the COA] [ -1 to Exit the application]
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            System.out.println("Press:  [ 1 -> to continue as Clinic] [ 2 -> to continue as the Center Authority ] OR  [ -1 -> to EXIT]");
            int x = sc.nextInt();
            if(x == -1)
                break;

            else if(x == 2){

                System.out.println("Enter the CENTER AUTHORITY password to continue");
                String CenterAuthorityPassword = sc.next();

                if(CenterAuthorityPassword.equals(centerAuthority.getPassword())) {
                    while (true) {
                        System.out.println(" ###########  WELCOME TO THE AUTHORITY CENTER ########## \n Press: [2 -> to continue ] OR [-1 -> to SIGN OUT]");
                        int choice = sc.nextInt();
                        if (choice == -1)
                            break;
                        else if (choice == 2) {
                            AuthorityFunctions();
                            int cNum = sc.nextInt();
                            centerAuthority.execute(cNum);
                        } else
                            System.out.println("TRY AGAIN");
                    }
                }
                else
                    System.out.println("Invalid password, enter the right password to continue");

            }
            else if (x == 1) {
                Clinic clinicAuth = authorizeClinic();

                if(clinicAuth != null){

                    while(true){
                        System.out.println(" ###########  AUTHORIZED CLINIC ##########  \n Press: [2 -> to continue ] OR [-1 -> to SIGN OUT]");
                        int choice = sc.nextInt();
                        if(choice == -1)
                            break;
                        else if(choice == 2){
                            ClinicFunctions();
                            int cNum = sc.nextInt();
                            clinicAuth.execute(cNum);
                        }
                        else
                            System.out.println("TRY AGAIN");
                    }
                }
            }
        }

        //another ASCII Art at the very end of the application execution :)
        ASCIIEnd();
    }

    /**
     * authorize the clinic
     * @return true if clinic is legitimate, false otherwise
     */
    public Clinic authorizeClinic(){
        System.out.println("Enter your Clinic ID");
        Scanner I_id = new Scanner(System.in);
        String id = I_id.nextLine();

        System.out.println("Enter Your Password");
        Scanner I_password = new Scanner(System.in);
        String password = I_password.nextLine();

        boolean exist = RepositoryFNs.authorizeClinic(id, password);
        if(exist)
            return existingClinics.get(password);
        else return null;
    }

    public static void ClinicFunctions(){
        System.out.println("1 --> View the blockchain");
        System.out.println("2 --> View a patient tx");
        System.out.println("3 --> Insert a Transaction");
    }

    public void AuthorityFunctions(){
        System.out.println("1 --> View the blockchain");
        System.out.println("2 --> View a patient tx");
    }

    public void clinics(){
        existingClinics.put("123",new Clinic("123",this));
        existingClinics.put("a123",new Clinic("a123",this));
        existingClinics.put("b123",new Clinic("b123",this));
        existingClinics.put("c123",new Clinic("c123",this));
        existingClinics.put("d123",new Clinic("d123",this));
    }

    public Blockchain getBlockchain() {return blockchain;}

    public CenterAuthority getCenterAuthority() {return centerAuthority;}

    public Hashtable<String, Clinic> getExistingClinics() {
        return existingClinics;
    }

    private void clinicsInit(){
        FindIterable<Document> clinicInfo=  RepositoryFNs.getClinicInfo();
        // (2)
        try (MongoCursor<Document> cursor = clinicInfo.iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                existingClinics.put((String) document.get("password"), new Clinic(document.get("password").toString(), document.get("_id").toString(),
                        this));
            }
        }
    }

    private void ASCIIStart(){
        String art = """
                 _______   ___  ___  ________          ________  ___       ________  ________  ___  __    ________  ___  ___  ________  ___  ________     \s
                |\\  ___ \\ |\\  \\|\\  \\|\\   __  \\        |\\   __  \\|\\  \\     |\\   __  \\|\\   ____\\|\\  \\|\\  \\ |\\   ____\\|\\  \\|\\  \\|\\   __  \\|\\  \\|\\   ___  \\   \s
                \\ \\   __/|\\ \\  \\\\\\  \\ \\  \\|\\  \\       \\ \\  \\|\\ /\\ \\  \\    \\ \\  \\|\\  \\ \\  \\___|\\ \\  \\/  /|\\ \\  \\___|\\ \\  \\\\\\  \\ \\  \\|\\  \\ \\  \\ \\  \\\\ \\  \\  \s
                 \\ \\  \\_|/_\\ \\   __  \\ \\   _  _\\       \\ \\   __  \\ \\  \\    \\ \\  \\\\\\  \\ \\  \\    \\ \\   ___  \\ \\  \\    \\ \\   __  \\ \\   __  \\ \\  \\ \\  \\\\ \\  \\ \s
                  \\ \\  \\_|\\ \\ \\  \\ \\  \\ \\  \\\\  \\|       \\ \\  \\|\\  \\ \\  \\____\\ \\  \\\\\\  \\ \\  \\____\\ \\  \\\\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \\  \\ \\  \\ \\  \\ \\  \\\\ \\  \\\s
                   \\ \\_______\\ \\__\\ \\__\\ \\__\\\\ _\\        \\ \\_______\\ \\_______\\ \\_______\\ \\_______\\ \\__\\\\ \\__\\ \\_______\\ \\__\\ \\__\\ \\__\\ \\__\\ \\__\\ \\__\\\\ \\__\\
                    \\|_______|\\|__|\\|__|\\|__|\\|__|        \\|_______|\\|_______|\\|_______|\\|_______|\\|__| \\|__|\\|_______|\\|__|\\|__|\\|__|\\|__|\\|__|\\|__| \\|__|
                                                                                                                                                          \s
                                                                                                                                                          \s
                                                                                                                                                          \s
                """;

        String art1 = """
                      |________|___________________|_
                      |        | | | | | | | | | | | |________________
                      |________|___________________|_|                ,
                      |        |                   |                  ,
                                
                                
                """;

        for(int i = 0; i < art.length(); i++){
            System.out.print(art.charAt(i));
        }


        for(int i = 0; i < art1.length(); i++){
            System.out.print(art1.charAt(i));
        }
    }

    public void ASCIIEnd(){
        String art = """
                                      __
                                    .'_|\\     _.--._
                                   |_| ` \\---/     ``.
                                   | ` .'   .--._____`\\
                                    \\ /    /#"__ _<  \\/
                                     |    |#'/  \\| \\
                                     |__  |# | '@|'@
                                    ,'-.`.|"  \\_/|_/
                                    |   ` \\\\  __<.`)__
                    .'~~`.           `.__,|\\\\ \\ `-_` /
                  ,' .--. `.            ) '  \\ \\(  `'
                 / ,'    `. |         _/  ,,  \\ \\_--
                (_/       | |        / \\\\/  ``\\\\_/   `-._____
                         .' .        |  \\\\    _________<____))
                       .' .'        /|   \\\\--'  _____----.`-'
                      / .'         / (    >----'  .-<_==='
                     / /          |   \\          /
                    | .           |____\\        /
                    | |             |   \\      /
                    | .             |    \\   _/`
                     \\ `.         .'|    `\\_(`\\
                      `. `~----~~' .'\\    \\    )
                        `-._____.-'  |    )   /
                                     |   /|  /
                                     |  / (-<_______
                                    _/--`-------____\\
                                    `---._  ==='
                                         `--'
                """;


        for(int i = 0; i < art.length(); i++){
            System.out.print(art.charAt(i));
        }
    }

}
