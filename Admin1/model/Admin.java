public class Admin {

    private String nom;
    private float moy;
    public Etudiant(String nom) {
        this.nom = nom;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setMoyenne(float moye) {
        this.moy=moye;
    }
    public float getMoy() {
        return moy;
    }
    public void afficher() {
        System.out.println("bonjour "+nom+ " voter moyenne est : " + getMoy());
    }
}
