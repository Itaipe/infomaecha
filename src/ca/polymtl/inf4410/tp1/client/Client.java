package ca.polymtl.inf4410.tp1.client;

import ca.polymtl.inf4410.tp1.shared.FileTP1;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	public static void main(String[] args) throws IOException {
                         
            String distantHostname = null;
            //if (args.length > 0) {
            //    distantHostname = args[0];
            //} else {
            //    System.out.println("Vous devez specifier les arguments attendus (Cf Readme).");
            //}
          
            if (args.length < 1) {
            	System.out.println("Vous devez specifier le nom de la méthode et les arguments eventuels");
            } 
            String method = args[0];
            Client client = new Client(distantHostname);
            switch (method) {
                case "create":
                    if (args.length >= 2) {
                        client.createRMIDistant(args[1]); // Appel create avec le nom en parametre
                    } else {
                        System.out.println("Vous devez spécifier le nom du fichier créé.");
                    }
                    break;
                case "list" :
                    client.listRMIDistant();
                    break;
                case "syncLocalDir" :
                    client.syncLocalDirRMIDistant();
                    break;
                case "get" :
                    if (args.length >= 2) {
                        client.getRMIDistant(args[1]);
                    } else {
                        System.out.println("Vous devez spécifier le nom du fichier que vous voulez récupérer.");
                    }
                    break;
                case "lock" :
                    if (args.length >= 2) {
                        client.lockRMIDistant(args[1]);
                    } else {
                        System.out.println("Vous devez spécifier le nom du fichier que vous voulez \"locker\".");
                    }
                    break;
                case "push" :
                    if (args.length >= 2) {
                        client.pushRMIDistant(args[1]);
                    } else {
                        System.out.println("Vous devez spécifier le nom du fichier que vous voulez push.");
                    }
                    break;
                default :
                    System.out.println("La methode " + method + " n'est pas reconnue.");
            }	
            
            //Client client = new Client(distantHostname, method);
            //client.run();
	}

	private ServerInterface localServerStub = null;
	//private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
                localServerStub = loadServerStub("127.0.0.1");
		/*if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}*/
	}

	/*private void run() {
            
		if (distantServerStub != null) {
			appelRMIDistant(convertpow(x));
                        switch (methode) {
                            case "create":
                                createRMIDistant(nom);
                        }
		}
	}*/

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

        /*
	private void appelRMIDistant(String parametre) {
		try {
			long start = System.nanoTime();
			int result = distantServerStub.execute(4, 7, parametre);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI distant: "
					+ (end - start) + " ns");
			System.out.println("Résultat appel RMI distant: " + result);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}*/
	
        private void createRMIDistant(String nom) {
            try {
                long start = System.nanoTime();
                localServerStub.create(nom);
                long end = System.nanoTime();
                
                System.out.println("Temps écoulé appel RMI distant: "
                        + (end - start) + " ns");
            } catch (RemoteException e) {
               System.out.println("Erreur: " + e.getMessage());
            }
        }
        
        private void listRMIDistant() {
            try {
                long start = System.nanoTime();
                HashMap<String, FileTP1> liste = localServerStub.list();
                long end = System.nanoTime();
                for (FileTP1 fichier_courant : liste.values()) {
                    System.out.println("Fichier : " + fichier_courant.nom);
                    if (fichier_courant.client_id > 0)
                        System.out.println("Ce fichier est verouillé par le client " + fichier_courant.client_id);
                }
                System.out.println("Temps écoulé appel RMI distant: " + (end - start) + " ns");
            } catch (RemoteException e) {
               System.out.println("Erreur: " + e.getMessage());
            }
	}
        
        private int generateclientidRMIDistant() throws IOException {
            int client_id = 0;
            try {
                File fichier = new File("client_id.txt");
                if (fichier.exists()) {
                    FileInputStream fi = new FileInputStream(fichier);                    
                    InputStreamReader ipsr = new InputStreamReader(fi);
                    BufferedReader br = new BufferedReader(ipsr);
                    client_id = Integer.parseInt(br.readLine());                    
                } else {
                    client_id = localServerStub.generateclientid();
                    FileWriter fw = new FileWriter(fichier);
                    fw.write(String.valueOf(client_id));
                    fw.close();
                }
            } catch (RemoteException e) {
               System.out.println("Erreur: " + e.getMessage());
            }
            return client_id;
	}
        
        private void syncLocalDirRMIDistant() throws IOException {
            try {
                long start = System.nanoTime();
                HashMap<String, FileTP1> liste = localServerStub.syncLocalDir();
                long end = System.nanoTime();
                //Parcours de la liste des fichiers du serveur
                for (FileTP1 fichier_courant : liste.values()) {
                    System.out.println("Fichier : " + fichier_courant.nom);
                    File fichier = new File(fichier_courant.nom);
                    FileWriter fw = new FileWriter(fichier);
                    fw.write(Arrays.toString(fichier_courant.contenu));
                    fw.close();
                }
                
                System.out.println("Temps écoulé appel RMI distant: " + (end - start) + " ns");
            } catch (RemoteException e) {
               System.out.println("Erreur: " + e.getMessage());
            }
	}
        
        private void getRMIDistant(String nom) throws IOException {
            
            long start = System.nanoTime();
            File fichier_actuel = new File(nom);
            FileTP1 fichier_nouveau = new FileTP1(nom);
            if (fichier_actuel.exists()) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    DigestInputStream checksum = new DigestInputStream(new FileInputStream(fichier_actuel), md);
                    fichier_nouveau = localServerStub.get(nom, checksum);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                fichier_nouveau = localServerStub.get(nom, null); // ???
            }
            long end = System.nanoTime();
            
            if (fichier_nouveau == null) {
                System.out.println("Le fichier demandé est déjà à jour (ou n'existe pas sur le serveur).");
            } else {
                FileWriter fw = new FileWriter(fichier_actuel);
                fw.write(Arrays.toString(fichier_nouveau.contenu));
            }
            System.out.println("Temps écoulé appel RMI distant: "
                    + (end - start) + " ns");
        }
        
        private void lockRMIDistant(String nom) throws IOException {
            
            File fichier_actuel = new File(nom);
            FileTP1 fichier_nouveau = new FileTP1(nom);
            int client_id = generateclientidRMIDistant();
            //System.out.println(String.valueOf(client_id));
            if (fichier_actuel.exists()) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    DigestInputStream checksum = new DigestInputStream(new FileInputStream(fichier_actuel), md);
                    fichier_nouveau = localServerStub.lock(nom, checksum, client_id);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                fichier_nouveau = localServerStub.lock(nom, null, client_id); // ???
            }
            
            if (fichier_nouveau == null) {
                System.out.println("Le fichier est déjà verrouillé par un client (ou n'existe pas sur le serveur).");
            } else {
                FileWriter fw = new FileWriter(fichier_actuel);
                fw.write(Arrays.toString(fichier_nouveau.contenu));
            }
        }
        
        
        private void pushRMIDistant(String nom) throws IOException {
            
            File fichier = new File(nom);
            
            if (fichier.exists()) {
                byte[] contenu = new byte[10000000];
                FileInputStream fi = new FileInputStream(fichier); 
                fi.read(contenu);
                int erreur = localServerStub.push(nom, contenu, generateclientidRMIDistant());
                if (erreur == 1) {
                    System.out.println("Vous devez lock le fichier avant de le push");
                } else {
                    System.out.println("Push réussi !");
                }
            } else {
                System.out.println("Le nom du fichier spécifier n'existe pas en local.");
            }
        }

}
