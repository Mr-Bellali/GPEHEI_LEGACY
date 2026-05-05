public class Ouvrage {
    private String titre;
    private String auteur;
    private String code;
    public Ouvrage(String titre, String auteur, String code) {
        this.titre = titre;
        this.auteur = auteur;
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public String toString() {
        return titre + " -- " + auteur + " -- " + code;
    }

    //•    public void ajouteOuvrage(String titre, String auteur) ; // Ajoute un ouvrage à la bibliothèque.
    public void ajouteOuvrage(String titre, String auteur){
        this.titre = titre;
        this.auteur = auteur;

    }
    //•    public void afficheTous() ; // Affiche tous les ouvrage de la bibliothèque.

    public void afficheTous(){
        System.out.println(titre + " -- " + auteur + " -- " + code);
    }
    //•    public void afficheEmpruntes() ; // Affiche tous les ouvrages empruntés.
    //•    public Ouvrage get(String code) ; // Retourne l’ouvrage dont le code est passé en paramètre.
    //•    public boolean estEmprunte(Ouvrage o) ;
    // Retourne « true » si l’ouvrage o est emprunté
    // sinon elle retourne « false ».
    //•    public void emprunte(String code) ; // Ajoute à la fin de liste des ouvrages empruntés,
    // l’ouvrage dont le code est passé en paramètre.


}
