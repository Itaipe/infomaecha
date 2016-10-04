package ca.polymtl.inf4410.tp1.server;

import ca.polymtl.inf4410.tp1.shared.FileTP1;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;
import java.io.ByteArrayInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements ServerInterface {

        public HashMap<String, FileTP1> liste = new HashMap<String, FileTP1> ();
        public int nombre_client = 0;
        
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	/*@Override*/
	public int execute(int a, int b, String parametre) throws RemoteException {
		return a + b;
	}

    @Override
    public void create(String nom) throws RemoteException {
        if (!this.liste.containsKey(nom)) {
            liste.put(nom, new FileTP1(nom));
        } /*else {
            throw new FileException();
        }*/
    }

    @Override
    public HashMap<String, FileTP1> list() throws RemoteException {
        return liste;
    }

    @Override
    public int generateclientid() throws RemoteException {
        nombre_client ++;
        return nombre_client;
    }

    @Override
    public HashMap<String, FileTP1> syncLocalDir() throws RemoteException {
        return liste;
    }

    @Override
    public FileTP1 get(String nom, DigestInputStream checksum_client) throws RemoteException {
    //public FileTP1 get(String nom, byte[] checksum_client) throws RemoteException {
        FileTP1 fichier_nouveau = liste.get(nom);
        if (fichier_nouveau == null) {
            return null;
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                DigestInputStream checksum_server = new DigestInputStream(new ByteArrayInputStream(fichier_nouveau.contenu), md);
                //byte[] checksum_server = md.digest();
                if (checksum_server.equals(checksum_client)) {
                    System.out.println("Les checksum sont egaux");
                    return null;
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fichier_nouveau;
    }

    @Override
    public FileTP1 lock(String nom, DigestInputStream checksum_client, int client_id) throws RemoteException {
        FileTP1 fichier_lock = liste.get(nom);
        if (fichier_lock == null || fichier_lock.client_id != 0) {
            return null;
        } else {
            try {
                fichier_lock.client_id = client_id;
                MessageDigest md = MessageDigest.getInstance("MD5");
                DigestInputStream checksum_server = new DigestInputStream(new ByteArrayInputStream(fichier_lock.contenu), md);
                //byte[] checksum_server = md.digest();
                if (checksum_server.equals(checksum_client)) {
                    return null;
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fichier_lock;
    }

    @Override
    public int push(String nom, byte[] contenu, int client_id) throws RemoteException {
        FileTP1 fichier_a_push = liste.get(nom);
        
        //TO DO LEVER UNE EXCEPTION SI LE FICHIER A PUSH N EXISTE PAS
        
        // On renvoie une erreur si le fichier a push n'a pas prealablement ete lock
        if (fichier_a_push.client_id != client_id) {
            return 1; 
        }
        
        fichier_a_push.contenu = contenu;
        fichier_a_push.client_id = 0;        
        return 0;
    }
    
}
