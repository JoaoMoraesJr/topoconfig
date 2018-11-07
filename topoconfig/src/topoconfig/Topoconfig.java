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
public class Topoconfig {
    
    ArrayList <Network> netList = new ArrayList<Network>();
    String[][] routerMatrix;
    
    public void readFile(String txtFile) {
        int index = 0;
        try {
        BufferedReader file = new BufferedReader(new FileReader(txtFile)); 
        String  line;
        while((line = file.readLine())!=null){
            if (line.contains("#NETWORK")) {    //CREATE NETWORKS
                while (!(line = file.readLine()).contains("#ROUTER")) {
                    if (!line.equals(" ")) {
                        String lineSplitted[] = line.split(", ");
                        netList.add (new Network (lineSplitted[0], Integer.parseInt(lineSplitted[1]), index));
                        index++;
                    }
                }
                if (line.contains("#ROUTER")) {    //CREATE CONNECTION MATRIX
                    routerMatrix = new String[netList.size()][netList.size()];
                    while ((line = file.readLine())!=null) {
                        String lineSplitted[] = line.split(", ");
                        int num_connections = Integer.parseInt(lineSplitted[1]);
                        for (int i = 0; i < num_connections; i++) {
                            int indexi = searchNetworkIndex (netList, lineSplitted[i+2]);
                            for (int j = 0; j < num_connections; j++) {
                                if (j != i) { //Don't compare same networks
                                    int indexj = searchNetworkIndex (netList, lineSplitted[j+2]);
                                    routerMatrix[indexi][indexj] = lineSplitted[0];
                                }
                            }
                        }
                    }   
                }
            }
        }
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println (toString());
    }
    
    public static int searchNetworkIndex (ArrayList<Network> netList, String netName){
        for (int i = 0; i < netList.size(); i++) {
            if (netName.equals(netList.get(i).name))
                return netList.get(i).index;
        }
        return -1;
    }
    
    public String toString () {
        String s = "";
        for (int i = 0; i < netList.size(); i++) {
            s += netList.get(i).name + " " + netList.get(i).nodes + "\n";
        }
        s += "\n";
        for (int i = 0; i < routerMatrix.length; i++) {
            for (int j = 0; j < routerMatrix.length; j++) {
                s+= routerMatrix [i][j] + "\t";
            }
            s+= "\n";
        }
        return s;
    }
}
