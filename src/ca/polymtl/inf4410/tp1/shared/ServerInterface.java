package ca.polymtl.inf4410.tp1.shared;

import ca.polymtl.inf4410.tp1.exception.FileException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.DigestInputStream;
import java.util.HashMap;

public interface ServerInterface extends Remote {
	int execute(int a, int b, String parametre) throws RemoteException;
        
        void create(String nom) throws RemoteException;
        
        HashMap<String, FileTP1> list() throws RemoteException;
        
        int generateclientid() throws RemoteException;
        
        HashMap<String, FileTP1> syncLocalDir() throws RemoteException;
        
        FileTP1 get(String nom, DigestInputStream checksum_client) throws RemoteException;
        
        FileTP1 lock(String nom, DigestInputStream checksum_client, int client_id) throws RemoteException;
        
        int push(String nom, byte[] contenu, int client_id) throws RemoteException;
}
