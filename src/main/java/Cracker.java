import org.soulwing.crypt4j.Crypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Jennica on 31/07/2016.
 */
public class Cracker {

    private Hashtable<String, String> dictionary;
    private ArrayList<String> users;
    private ArrayList<String> results;

    public Cracker() {
        this.dictionary = new Hashtable<>();
        this.users = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    /**
     * Reading the Linux Password File containing the User Accounts
     *
     * @param filePath
     */
    public void buildUserList(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));

            String account;

            while ( (account = br.readLine()) != null ) {
                String[] temp = account.split(":");

                // debugging
                System.out.println("temp[2] = " + temp[2]);

                Float userId = Float.parseFloat(temp[2]);
                if ( userId > 1000 && userId < 60000 ) {
                    // Adding the username
                    this.users.add(temp[0]);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( br != null )
                    br.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Reading the Linux Shadow Password File containing the Salt & Hashed Passwords
     * @param shadowPath
     * @param dictionaryPath
     */
    public void buildCrack(String shadowPath, String dictionaryPath) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(shadowPath));
            String account;
            int tempType;
            String salt;
            String passwordHash;
            String[] passwordArray;
            while ( (account = br.readLine()) != null ) {
                String[] temp = account.split(":");

                // temp[0] is the username
                if ( this.users.contains(temp[0]) ) {
                    // temp[1] is the salt & hashed password
                    passwordHash = temp[1];
                    passwordArray = passwordHash.split("\\$");
                    tempType = Integer.parseInt(passwordArray[1]);

                    if ( passwordArray.length < 3 ) {
                        this.buildDictionaryTable(dictionaryPath, "", 0);
                    } else {
                        salt = passwordArray[2];
                        buildDictionaryTable(dictionaryPath, salt, tempType);
                    }

                    if ( this.dictionary.containsKey(passwordArray[3]) ) {
                        String result = "User: " + temp[0] + " - Password: " + this.dictionary.get(passwordArray[3]);
                        System.out.println(result);
                        this.results.add(result);
                    } else {
                        String result = ("User: " + temp[0] + " - Password: Not Found");
                        System.out.println(result);
                        this.results.add(result);
                    }

                    this.writeFile("/result.txt");
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( br != null )
                    br.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creating a dictonary that contains the different possible user account password
     * @param filepath
     * @param salt
     * @param type
     */
    public void buildDictionaryTable(String filepath, String salt, int type) {
        BufferedReader br = null;

        try {
            String saltType;
            if ( type == 0 )
                saltType = "$" + type + "$";
            else
                saltType = "$" + type + "$" + salt;

            br = new BufferedReader(new FileReader(filepath));
            String word;
            String hash;
            String[] hashArray;
            while ( (word = br.readLine()) != null ) {
                hash = Crypt.crypt(word.toCharArray(), saltType);
                hashArray = hash.split("\\$");

                this.dictionary.put(hashArray[3], word);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( br != null )
                    br.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }

    private void writeFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.write(path, this.results, Charset.forName("UTF-8"));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
