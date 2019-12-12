package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * La classe AuthenticationDetails descrive un duplicato.
 * Usa l'annotazione @Entity per segnalare a Spring che si tratta di una classe che dovrà essere creata nel database.
 * I duplicati non vengono salvati nel database ma viene comunque usata l'annotazione @Entity per permettere l'utilizzo di questa classe nel {@link samt.smajilbasic.deduplicator.repository.DuplicateRepository}.
 */
@Entity
public class Duplicate {

    /**
     * L'attributo hash contiene il hash del contenuto del duplicato.
     * Utilizza l'annotazione @Id per indicare che è una chiave primaria della tabella.
     */
    @Id
    private String hash;
    /**
     * L'attributo size specifica la grandeza del duplicato in Byte.
     */
    private Integer size;
    /**
     * L'attributo count specifica il numero di file che ci sono nel duplicato.
     */
    private Integer count;
    

    /**
     * Metodo costruttore vuoto.
     */
    public Duplicate() {
        super();
    }
    /**
     * Metodo costruttore che accettà tutti i parametri.
     * @param size la grandeza del duplicato.
     * @param hash il hash del contenuto del duplicato.
     * @param count il numero di file che ci sono nel duplicato.
     */
    public Duplicate(Integer size,String hash,Integer count) {
        this.size = size;
        this.hash = hash;
        this.count = count;
    }

    /**
     * Metodo getter per l'attributo size.
     * @return la grandeza del duplicato in Byte.
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Metodo getter per l'attributo hash.
     * @return il hash del contenuto del duplicato.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Metodo getter per l'attributo count.
     * @return il numero di file che ci sono nel duplicato.
     */
    public Integer getCount() {
        return count;
    }
}