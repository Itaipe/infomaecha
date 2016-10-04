/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.polymtl.inf4410.tp1.shared;

import java.io.Serializable;

/**
 *
 * @author chjeab
 */
public class FileTP1 implements Serializable {
    
    public String nom;
    public byte[] contenu = new byte[10000000]; //Taille max d un fichier : 10Mo
    public int checksum;
    
    /* Client possédant le verrou */
    public int client_id = 0;
    
    public FileTP1(String nom) {
        this.nom = nom;
        this.checksum = 0;
    }
    
}
