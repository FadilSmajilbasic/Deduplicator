# Deduplicator GUI
Un programma per gestire i file duplicati

# Istruzioni per l'installazione e avvio

## Avviare il servizio deduplicator REST

##### Requisiti:
- Servizio MYSQL in esecuzione
- Avere un utente MYSQL con accesso sufficiente per creare e modificare la struttura di tabelle e database
- versione nodejs >12.16 
- versione npm >6.13.4 

##### Modifica del file di configurazione:

Dopo aver clonato la repo navigare sotto `5_Applicativo/deduplicator/src/main/resources` e cambiare i seguenti campi in base alle impostazioni del server MYSQL in base al nome utente e password dell'utente MYSQL.

|Proprietà| Descrizione|
|-|-|
|``spring.datasource.username``|Nome utente di mysql
|`spring.datasource.datasource.password`|La password dell'utente di mysql
|`spring.datasource.url` | L'URL del servizio e la port|
|`server.port` | La porta del servizio | 


##### Compilazione:

Per compilare l'applicazione navigare su `5_Applicativo/deduplicator` ed eseguire il Gradle wrapper aprendo il terminale e scrivendo il seguente comando: <br>`./gradlew build`. 

Il Gradle wrappe creerà un eseguibile jar nella cartella `5_Applicativo/deduplicator/build/libs/`.

##### Esecuzione:
Per avviare il servizio deduplicator si può semplicemente eseguire il file jar compilato con java usando il seguente comando: <br>
`java -jar build/libs/deduplicator-0.0.8.jar` . 

Se si desidera eseguire il programma come un servizio seguire [questa](https://www.baeldung.com/spring-boot-app-as-a-service#on-linux) guida che ha una buona spiegazione su come avviare l'applicazione compilata come un servizio.

## Avviare il server GUI del deduplicator

##### Requisiti:
- versione maven >3.6.0 
#### Compilazione

Navigare sotto `5_Applicativo/deduplicatorVaadinGUI/`. 
Eseguire il seguente comando per compilare il progetto e creare un jar eseguibile: `mvn clean package`.

Maven costruirà il file eseguibile jar nella cartella `5_Applicativo/deduplicatorVaadinGUI/target`.

##### Eseguire come programma:

Si può eseguire il programma così com'è semplicemente navigando nella cartella `5_Applicativo/deduplicatorVaadinGUI/target` ed eseguendolo nel terminale in questo modo con il seguente comando `./deduplicatorgui-2.0.jar`.

È anche possibile creare un collegamento simbolico per poterlo eseguire da qualsiasi luogo. È possibile farlo eseguendo il seguente comando: 
`sudo ln -s /complete/path/to/to/jar/deduplicatorgui-2.0.jar /sbin/deduplicatorGUI`.

Ora si può semplicemente scrivere `deduplicatorGUI` nel terminale e l'applicazione si avvierà.

##### Esecuzione come servizio:

È necessario fare un collegamento simbolico per eseguire il programma come servizio. Potete farlo eseguendo il seguente comando: 
`sudo ln -s /complete/path/to/to/jar/deduplicatorgui-2.0.jar /etc/init.d/deduplicatorGUI`.

Per avviare il servizio basta eseguire il seguente comando: `sudo service deduplicatorGUI start`. 
