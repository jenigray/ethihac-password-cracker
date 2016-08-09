import org.soulwing.crypt4j.Crypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jennica on 31/07/2016.
 */
public class Cracker {

    private Hashtable<String, String> dictionary;
    private ArrayList<String> users;
    private ArrayList<String> results;

    private long startTime;
    private long endTime;
    private long elapsedTime;
    private double runningTime;

    public Cracker() {
        this.dictionary = new Hashtable<>();
        this.users = new ArrayList<>();
        this.results = new ArrayList<>();

        this.startTime = System.nanoTime();
        this.endTime = 0;
        this.elapsedTime = 0;
        this.runningTime = 0.0;
    }

    public boolean build(ArrayList<String> filePaths) {
        this.buildUserList(filePaths.get(0));
        return this.buildCrack(filePaths.get(1), filePaths.get(2));
    }

    /**
     * Reading the Linux Password File containing the User Accounts
     *
     * @param filePath
     */
    public boolean buildUserList(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));

            String account;

            while ( (account = br.readLine()) != null ) {
                String[] temp = account.split(":");

                Float userId = Float.parseFloat(temp[2]);
//                if ( userId > 1000 && userId < 60000 ) {
                if ( userId > 1000 ) {
                    // Adding the username
                    this.users.add(temp[0]);
                }
            }

            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if ( br != null )
                    br.close();
                return true;
            } catch ( Exception ex ) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Reading the Linux Shadow Password File containing the Salt & Hashed Passwords
     *
     * @param shadowPath
     * @param dictionaryPath
     */
    public boolean buildCrack(String shadowPath, String dictionaryPath) {
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

                    // check if the salt & hashed password is a SHA-512
                    if ( passwordHash.substring(0, 1).equals("$") ) {
                        passwordArray = passwordHash.split("\\$");
                        tempType = Integer.parseInt(passwordArray[1]);

                        if ( passwordArray.length < 3 ) {
                            this.buildDictionaryTable(dictionaryPath, "", 0);
                        } else {
                            salt = passwordArray[2];
                            this.buildDictionaryTable(dictionaryPath, salt, tempType);
                        }

                        this.endTime = System.nanoTime();
                        this.elapsedTime = this.endTime - this.startTime;

                        double convert = TimeUnit.MILLISECONDS
                                .convert(this.elapsedTime, TimeUnit.NANOSECONDS) / 1000.0;
                        double resultTime = convert - this.runningTime;
                        this.runningTime = convert;

                        if ( this.dictionary.containsKey(passwordArray[3]) ) {
                            String result = "User: " + temp[0] + " -- Password: " + this.dictionary.get(passwordArray[3])
                                    + "\nCrack Time: " + resultTime + " sec"
                                    + "\n**************************************************";

                            System.out.println(result);
                            this.results.add(result);
                        } else {
                            String result = "User: " + temp[0] + " -- Password: Not Found"
                                    + "\nCrack Time: " + resultTime + " sec"
                                    + "\n**************************************************";

                            System.out.println(result);
                            this.results.add(result);
                        }

                        this.writeToFile("result/result.txt");
                    }
                }
            }

            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if ( br != null )
                    br.close();
                return true;
            } catch ( Exception ex ) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Creating a dictonary that contains the different possible user account password
     *
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

    private void writeToFile(String fileName) {
        try {
            File file = new File(fileName);
            Path path = file.toPath();
            Files.write(path, this.results, Charset.forName("UTF-8"));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getResults() {
        return this.results;
    }
}
