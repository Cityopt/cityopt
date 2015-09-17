package eu.cityopt.crypting;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncrypter {
    public static void main(String[] args) {
        
        hash("admin");
        hash("expert");
        hash("standard");
        hash("quest");
        hash("omni");        
        

}
    
public static void hash(String args) {
        
        if (args.length() < 1)
                    return;        
                                
        // hashed password
        BCryptPasswordEncoder passwordEnconder = new BCryptPasswordEncoder(12);
        String hashedPassword = passwordEnconder.encode(args);
        
        
        System.out.println(args + "-->" + hashedPassword);
        

}

}
