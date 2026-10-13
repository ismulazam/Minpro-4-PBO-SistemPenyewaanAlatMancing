package Model;

/**
 *
 * @author mismu
 */
public class Penyewa {
    private String idPenyewa;
    private String namaPenyewa;
    private String noTelp;

    public Penyewa(String idPenyewa, String namaPenyewa, String noTelp) {
        this.idPenyewa = idPenyewa;
        this.namaPenyewa = namaPenyewa;
        this.noTelp = noTelp;
    }

    public String getNamaPenyewa() { return namaPenyewa; }
}
