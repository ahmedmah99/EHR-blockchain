package com.app;

import com.app.blockchain.Block;
import com.app.blockchain.Blockchain;
import com.app.blockchain.CenterAuthority;
import com.app.mongodb.RepositoryFNs;
import com.app.user.Clinic;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.sun.tools.jconsole.JConsoleContext;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Console;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Scanner;

public class Simulator {

    Hashtable<String,Clinic> existingClinics;
    Blockchain blockchain = new Blockchain(this);
    CenterAuthority centerAuthority = new CenterAuthority(this);

    public Simulator(){
        existingClinics = new Hashtable<>();
        clinicsInit();

        //clinics();
    }

    public void run() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        ASCIIStart();
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            System.out.println("Enter [ 1 to continue as Clinic] [2 to continue as the Center Authority] OR  -1 to Exit");
            int x = sc.nextInt();
            if(x == -1)
                break;

            else if(x == 2){

                while(true){
                    System.out.println(" WELCOME TO THE AUTHORITY CENTER \n Enter '2' to continue, -1 to sign out");
                    int choice = sc.nextInt();
                    if(choice == -1)
                        break;
                    else if(choice == 2){
                        AuthorityFunctions();
                        int cNum = sc.nextInt();
                        centerAuthority.execute(cNum);
                    }
                    else
                        System.out.println("Wrong number, TRY AGAIN");
                }

            }
            else if (x == 1) {
                Clinic clinicAuth = authorizeClinic();

                if(clinicAuth != null){

                    while(true){
                        System.out.println(" ------ AUTHORIZED CLINIC ------ \n Enter '2' to continue, -1 to sign out");
                        int choice = sc.nextInt();
                        if(choice == -1)
                            break;
                        else if(choice == 2){
                            ClinicFunctions();
                            int cNum = sc.nextInt();
                            clinicAuth.execute(cNum);
                        }
                        else
                            System.out.println("Wrong number, TRY AGAIN");
                    }
                }
            }
        }
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
                                                                                  _.._                                                  
                                                                             .--'` .-,)
                                                                           .'     /
                                                            ,             /      /
                                                           /\\            ;      ;
                                                           | `.__..__    |      |
                                                           |         `''-\\      ;
                                                            \\             `      \\
                                                             '.                   `.
                                                               '--.,__   __..-'-.   '.\s
                                                                      ```        `.   '.
                                                                                   `.   `\\
                                                                   _.._              \\    `\\
                                                                _.'    '-._ .__       |     `\\                                             
                                                              .'/        .-'   `\\     |       \\
                                                            .'  :           .-.  |    /        \\
                                                  _        /     \\         /_  | /_..-`"-.     ;
                                                 / '.     |  .    )  .-')_/` \\.'`         \\    |
                                                ;    \\    /_.'  .'_.' .-. .-./       .--._/    ;
                                                |   _ '-'`      ` /  /o )(o (       (   __    /
                                                ;  ( '           ///     _) |'.      `'`  `'-;
                                                 \\  `   _       ////  ,__   /  `,            _)
                                                  '. ' ( `--.__.\\  '.  `"` /              .-'
                                                    '.  '      .-)  /-.__.'`-.  (     .  /
                                                      \\  ' __.' /  /          \\  '---'  |
                                                       `-.'-=\\.'  /     _._\\   \\        /
                                                         '===/   /`'._.'   _\\_  \\-.__.-'
                                                            `|   /`-...--'''      |
                                                            \\__/`-._       __.-'`
                                                                    `""\"""`
                """;

        for(int i = 0; i < art.length(); i++){
            System.out.print(art.charAt(i));
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
