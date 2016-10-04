/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.polymtl.inf4410.tp1.exception;

import java.io.IOException;
/**
 *
 * @author chjeab
 */

public class FileException extends Exception {
    
  public FileException(){
    System.out.println("Le fichier créé existe déjà...");
  } 
    
}
