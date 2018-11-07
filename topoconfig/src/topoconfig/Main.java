/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topoconfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author 15201850
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Topoconfig topoconfig = new Topoconfig ();
        //topoconfig.readFile(args[1]);
        topoconfig.readFile ("topologia.txt");
    }
    
}



