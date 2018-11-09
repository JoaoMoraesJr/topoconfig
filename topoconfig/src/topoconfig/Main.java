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
        //topoconfig.readFile ("topologia.txt");
        //topoconfig.setAddress("200.10.20.0/24");
        topoconfig.readFile ("topologia2.txt");
        topoconfig.setAddress("192.168.0.0/23");
        if (topoconfig.verifyAdresses()) {
            topoconfig.calculateMasks();
            topoconfig.calculateAddresses();
            System.out.println (topoconfig.toString());
        }else{
            System.out.println ("Não há endereços suficientes para alocar");
        }
    }
    
}



