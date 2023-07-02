package app.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {
    
    @Id
    private String username;
    
    @Lob
    private byte[] classifier;
    
    public User() {
        
    }
    
    public User(String username, byte[] classifier) {
        this.username = username;
        this.classifier = classifier;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public byte[] getClassifier() {
        return classifier;
    }
    
    public void setClassifier(byte[] classifier) {
        this.classifier = classifier;
    }
}
