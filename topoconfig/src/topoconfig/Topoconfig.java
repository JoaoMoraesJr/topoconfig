/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topoconfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.pow;
import java.util.ArrayList;

/**
 *
 * @author 15201850
 */
public class Topoconfig {
    
    ArrayList <Network> netList = new ArrayList<Network>();
    String[][] routerMatrix;
    int [] address;
    int mask;
    
    public void setAddress (String adr) {
        String splittedAddress[] = adr.split("/");
        mask = Integer.parseInt(splittedAddress[1]);
        String splittedAddress2[] = splittedAddress[0].split("\\.");
        address = new int[4];
        for (int i = 0; i < 4; i++) {
            address[i] = Integer.parseInt(splittedAddress2[i]);
        }
    }
    
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
    }
    
    public static int searchNetworkIndex (ArrayList<Network> netList, String netName){
        for (int i = 0; i < netList.size(); i++) {
            if (netName.equals(netList.get(i).name))
                return netList.get(i).index;
        }
        return -1;
    }
    
    public boolean verifyAdresses () {
        int sumAdrNets = 0;
        for (int i = 0; i<netList.size();i++) {
            sumAdrNets += netList.get(i).nodes;
        }
        if (sumAdrNets > pow (2, (32-mask))) return false;
        return true;
    }
    
    public void calculateMasks () {
        for (int i = 0; i < netList.size(); i++) {
            int nAddress = netList.get(i).nodes + 2; //rede e broadcast
            int exp = 0;
            while (pow(2, exp) < nAddress) {
                exp++;
            }
            netList.get(i).mask = 32 - exp;
        }
        
    }
    
    public int[] sumAddress (int[] address, int sumToAddress) {
        int[] numAddress = address.clone();
        while (sumToAddress > 0) {
            numAddress[3]++;
            sumToAddress--;
            for (int i = 3; i > -1; i--) {
                if (numAddress[i] > 255) {
                    numAddress[i] = 0;
                    numAddress[i-1]++;
                }
            }
        }
        if (numAddress[3] > 255) System.out.println ("ERRO! Endereço ultrapassou limite: " + numAddress[0] +"."+ numAddress[1] +"."+ numAddress[2] +"."+ numAddress[3]);
        return numAddress;
    }
    
    public void calculateAddresses (){
        calculateMasks();
        int[] numAddress = address;
        int actualMask = mask;
        int addressesCalculated = 0;
        while (addressesCalculated < netList.size()) {
            int indexSmallestMask = 0;
            for (int i = 0; i < netList.size(); i++) { //Search for first not calculated address.
                if (netList.get(i).address == null) {
                    indexSmallestMask = i;
                    break;
                }
            }
            for (int i = 0; i < netList.size(); i++) {
                if (netList.get(i).address == null && (netList.get(i).mask < netList.get(indexSmallestMask).mask)) {
                    indexSmallestMask = i;
                }
            }
            while (netList.get(indexSmallestMask).mask > actualMask){
                actualMask++;
            }
            netList.get(indexSmallestMask).setAddress(numAddress);
            int sumToAddress = (int) pow (2, 32 - (actualMask));
            numAddress = sumAddress (numAddress, sumToAddress);
            addressesCalculated++;
        }
    }
    
    public String addressToString (int[] address) {
        String s = "";
        if (address == null) return "null";
        for (int i = 0; i < 3; i ++) {
            s+= address[i] + ".";
        }
        s+= address[3];
        return s;
    }
    
    public String toString () {
        String s = "";
        s+= "#NETWORK\n";
        for (int i = 0; i < netList.size(); i++) {
            s += netList.get(i).name + ", " + netList.get(i).addressToString() +"/" + netList.get(i).mask + ", ";
            s += addressToString(sumAddress(netList.get(i).address, 1)) + "-" + addressToString(sumAddress(netList.get(i).address, netList.get(i).nodes));
            s += "\n";
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
