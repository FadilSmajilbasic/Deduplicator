package samt.smajilbasic.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * La classe GlobalPath descrive un percorso inserito dall'utente per la
 * scansione o per essere escluso dalla scansione.
 */
public class GlobalPath {
	/**
	 * L'attributo path conteine il percorso assoluto del file o cartella. Utilizza
	 * l'annotazione @Id per indicare che è una chiave primaria della tabella.
	 */
	private String path = "";

	/**
	 * L'attributo file indica se il percorso {@link GlobalPath#path} sia un file
	 * oppure una directory.
	 */
	private Boolean file = false;

	/**
	 * L'attributo ignoreFile indica se l'oggetto è da ignorare o se deve essere
	 * preso in considerazione durante la scansione.
	 */
	private Boolean ignoreFile = false;

	/**
	 * L'attributo date indica la data d'aggiunta in formato timestamp.
	 */
	private Long date = 0L;

	private Action action;

	/**
	 * Costruttore vuoto.
	 */
	public GlobalPath() {
	}

	/**
	 * Costruttore che accetta il percorso dell'oggetto e se è da ignorare oppure
	 * no.
	 * 
	 * @param path       il percorso del oggetto.
	 * @param ignoreFile se è da ignorare o no.
	 */
	public GlobalPath(String path, boolean ignoreFile, Long date) {
		this.setPath(path);
		this.setignoreFile(ignoreFile);
		this.setDate(date);
	}

	/**
	 * Metodo getter per la variabile path..
	 * 
	 * @return il percorso del oggetto.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Metodo setter per la variabile path. Se il percorso non è valido viene tirata
	 * una RuntimeException.
	 * 
	 * @param path il percorso da impostare.
	 */
	private void setPath(String path) {
		this.path = path;
	}

	/**
	 * Metodo getter per la variabile file.
	 * 
	 * @return true se il percorso impostato è un file, false se è una cartella.
	 */
	public boolean isFile() {
		return file;
	}

	/**
	 * Metodo setter per la variabile file.
	 * 
	 * @param file true se il percorso impostato è un file, false altrimenti.
	 */
	private void setFile(boolean file) {
		this.file = file;
	}

	/**
	 * Metodo getter per la variabile ignoreFile.
	 * 
	 * @return true se l'oggetto è impostato per essere ignorato, false altrimenti.
	 */
	public boolean isignoreFile() {
		return ignoreFile;
	}

	/**
	 * Metodo setter per la variabile ignoreFile
	 * 
	 * @param ignoreFile true se il l'oggetto deve essere ignorato, false se deve
	 *                   essere preso in considerazione durante la scansione.
	 */
	private void setignoreFile(boolean ignoreFile) {
		this.ignoreFile = ignoreFile;
	}

	/**
	 * Metodo getter per la variabile date.
	 * 
	 * @return la data in formato timestamp.
	 */
	public Long getDate() {
		return date;
	}

	public String getDateFormatted() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		return dateFormat.format(cal.getTime());
	}

	/**
	 * Metodo setter per la variabile date.
	 * 
	 * @param date la data da impostare in formato timestamp.
	 */
	private void setDate(Long date) {
		this.date = date;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}