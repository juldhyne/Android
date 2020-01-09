package be.heh.juliendhyne.projetandroid.DB;

public class User {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private int level;

    public User() {}

    public User(String firstname, String lastname, String email, String password, int level) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: "
        + Integer.toString(getId()) + "\n"
        + "Prénom: " + getFirstname() + "\n"
        + "Nom: " + getLastname() + "\n"
        + "Email: " + getEmail() + "\n"
        + "Mot de passe: " + getPassword() + "\n"
        + "Niveau: " + getLevel());

        return sb.toString();
    }


}
