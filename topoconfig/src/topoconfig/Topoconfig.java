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
    ArrayList <Router> routerList = new ArrayList<Router>();
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
                        Router router = new Router (lineSplitted[0], num_connections);
                        for (int i = 0; i < num_connections; i++) {
                            int indexi = searchNetworkIndex (lineSplitted[i+2]);
                            router.netNamesList.add(lineSplitted[i+2]);
                            for (int j = 0; j < num_connections; j++) {
                                if (j != i) { //Don't compare same networks
                                    int indexj = searchNetworkIndex (lineSplitted[j+2]);
                                    routerMatrix[indexi][indexj] = lineSplitted[0];
                                }
                            }
                        }
                        routerList.add(router);
                    }   
                }
            }
        }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int searchNetworkIndex (String netName){
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
    
    public void configureRouters () {
        
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
    
    public String searchConnection (String roter, String destiny) {
        boolean found = false;
        String addr = "null";
        int index = 0;
        for (int i = 0; i < routerList.size(); i++) {
            if (routerList.get(i).name.equals(roter)){
                index = i;
            }
        }
        routerList.get(index).marked = true;
        for (int i = 0; i < routerList.get(index).netNamesList.size(); i++) {
            if (routerList.get(index).netNamesList.get(i).equals(destiny)) return routerList.get(index).netNamesList.get(i);
        }
        
        for (int i = 0; i < routerList.size(); i++) {
            if (!routerList.get(i).marked) {
                for (int j = routerList.get(index).netNamesList.size()-1; j > -1; j--) {
                    //System.out.println ("will try " + );
                    for (int k = routerList.get(i).netNamesList.size()-1; k > -1; k--)
                    {
                        if (routerList.get(i).netNamesList.get(k).equals(routerList.get(index).netNamesList.get(j))){
                            if (!searchConnection(routerList.get(i).name, destiny).equals("null")) addr = routerList.get(index).netNamesList.get(j) + " " + routerList.get(i).name;
                        }
                    }
                }
            }
        }
        return addr;
    }
    
    public void cleanMarks () {
        for (int i = 0; i < routerList.size(); i++) {
            routerList.get(i).marked = false;
        }
    }
    
    public String toString () {
        String s = "";
        s+= "#NETWORK\n";
        for (int i = 0; i < netList.size(); i++) {
            s += netList.get(i).name + ", " + netList.get(i).addressToString() +"/" + netList.get(i).mask + ", ";
            s += addressToString(sumAddress(netList.get(i).address, 1)) + "-" + addressToString(sumAddress(netList.get(i).address, netList.get(i).nodes));
            s += "\n";
        }
        s+= "#ROUTER\n";
        for (int i = 0; i < routerList.size(); i++) {
            s += routerList.get(i).name + ", " + routerList.get(i).connections;
            for (int j = 0; j < routerList.get(i).connections; j++) {
                int index = searchNetworkIndex(routerList.get(i).netNamesList.get(j));
                s+= ", " + addressToString(sumAddress(netList.get(index).address, netList.get(index).connections+1)) + "/" + netList.get(index).mask;
                routerList.get(i).netPortList.add(sumAddress(netList.get(index).address, netList.get(index).connections+1));
                netList.get(index).connections++;
            }
            s+= "\n";
        }
        s += "#ROUTERTABLE\n";
        for (int i = 0; i<routerList.size(); i++) {
            for (int j = 0; j < netList.size(); j++) {
                s+= routerList.get(i).name + ", ";
                s+= netList.get(j).addressToString() + "/" + netList.get(j).mask + ", ";
                boolean connectionExists = false;
                int index = 0;
                for (int k = 0; k < routerList.get(i).connections; k++) {
                    if (routerList.get(i).netNamesList.get(k).equals(netList.get(j).name)) {
                        connectionExists = true;
                        index = k;
                    }
                }
                if (connectionExists) s+= "0.0.0.0, " + index;
                else {
                    int ConnectionIndex = 0;
                    String netName = searchConnection (routerList.get(i).name, netList.get(j).name);
                    if (netName.equals(null)) {
                        s += "not connected";
                    }else{
                        String[] netNameAux = netName.split(" ");
                        int routerIndex = 0;
                        int sourceIndex = 0;
                        for (int k = 0; k < routerList.size(); k++) {
                            if (routerList.get(k).name.equals(netNameAux[1])) routerIndex = k;
                        }
                        for (int k = 0; k < routerList.get(routerIndex).netNamesList.size(); k++) {
                            if (routerList.get(routerIndex).netNamesList.get(k).equals(netNameAux[0])) {
                                ConnectionIndex = k;
                            }
                        }
                        for (int k = 0; k < routerList.get(i).netNamesList.size(); k++) {
                            if (routerList.get(i).netNamesList.get(k).equals(netNameAux[0])) {
                                sourceIndex = k;
                            }
                        }
                        s+= addressToString(routerList.get(routerIndex).netPortList.get(ConnectionIndex));
                        s+= ", " + sourceIndex;
                        cleanMarks();
                    }
                }
                s+= "\n";
            }
        }
        
        /*
        for (int i = 0; i < routerMatrix.length; i++) {
            for (int j = 0; j < routerMatrix.length; j++) {
                s+= routerMatrix [i][j] + "\t";
            }
            s+= "\n";
        }*/
        return s;
    }
}
