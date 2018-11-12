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
    ArrayList <String> routerTable = new ArrayList<String>();
    String[][] routerMatrix;
    Address address;
    int mask;
    
    
    //Set address of topology configuration
    public void setAddress (String adr) {
        String splittedAddress[] = adr.split("/");
        mask = Integer.parseInt(splittedAddress[1]);
        String splittedAddress2[] = splittedAddress[0].split("\\.");
        int[] addressAux = new int[4];
        for (int i = 0; i < 4; i++) {
            addressAux[i] = Integer.parseInt(splittedAddress2[i]);
        }
        address = new Address (addressAux);
    }
    
    //Read the input file and add new networks and routers to its lists
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
    
    //Search index in list of a Network by its name
    private int searchNetworkIndex (String netName){
        for (int i = 0; i < netList.size(); i++) {
            if (netName.equals(netList.get(i).name))
                return netList.get(i).index;
        }
        return -1;
    }
    
    //Verify if addresses can be created in the given size
    public boolean verifyAdresses () {
        int sumAdrNets = 0;
        for (int i = 0; i<netList.size();i++) {
            sumAdrNets += netList.get(i).nodes;
        }
        if (sumAdrNets > pow (2, (32-mask))) return false;
        return true;
    }
    
    //Calculate all network masks
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
    
    //Calculate all network adresses
    public void calculateAddresses (){
        calculateMasks();
        Address numAddress = address;
        int actualMask = mask;
        int addressesCalculated = 0;
        while (addressesCalculated < netList.size()) {
            int indexSmallestMask = 0;
            for (int i = 0; i < netList.size(); i++) { //Search for first not calculated address.
                if (netList.get(i).isSetted == false) {
                    indexSmallestMask = i;
                    break;
                }
            }
            for (int i = 0; i < netList.size(); i++) {
                if (netList.get(i).isSetted == false && (netList.get(i).mask < netList.get(indexSmallestMask).mask)) {
                    indexSmallestMask = i;
                }
            }
            while (netList.get(indexSmallestMask).mask > actualMask){
                actualMask++;
            }
            netList.get(indexSmallestMask).setAddress(numAddress);
            netList.get(indexSmallestMask).isSetted = true;
            int sumToAddress = (int) pow (2, 32 - (actualMask));
            numAddress = numAddress.sumAddress(sumToAddress);
            addressesCalculated++;
        }
    }
    
    //Configure all routers
    public void configureRouters () {
        for (int i = 0; i < routerList.size(); i++) {
            for (int j = 0; j < routerList.get(i).connections; j++) {
                int index = searchNetworkIndex(routerList.get(i).netNamesList.get(j));
                routerList.get(i).netPortList.add(netList.get(index).address.sumAddress(netList.get(index).connections+1));
                netList.get(index).connections++;
            }
        }
    }
    
    //Search for connection to send from roter to destiny package
    private String searchConnection (String roter, String destiny) {
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
    
    public void configureRouterTable() {
        String s = "";
        for (int i = 0; i<routerList.size(); i++) {
            for (int j = 0; j < netList.size(); j++) {
                s+= routerList.get(i).name + ", ";
                s+= netList.get(j).address.toString() + "/" + netList.get(j).mask + ", ";
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
                        s+= routerList.get(routerIndex).netPortList.get(ConnectionIndex).toString();
                        s+= ", " + sourceIndex;
                        cleanMarks();
                    }
                }
                routerTable.add(s);
                s= "";
            }
        }
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
            s += netList.get(i).name + ", " + netList.get(i).address.toString() +"/" + netList.get(i).mask + ", ";
            s += netList.get(i).address.sumAddress(1).toString() + "-" + netList.get(i).address.sumAddress(netList.get(i).nodes).toString();
            s += "\n";
        }
        s+= "#ROUTER\n";
        for (int i = 0; i < routerList.size(); i++) {
            s += routerList.get(i).name + ", " + routerList.get(i).connections;
            for (int j = 0; j < routerList.get(i).connections; j++) {
                int index = searchNetworkIndex(routerList.get(i).netNamesList.get(j));
                s+= ", " + routerList.get(i).netPortList.get(j).toString() + "/" + netList.get(index).mask;
            }
            s+= "\n";
        }
        s += "#ROUTERTABLE\n";
        for (int i = 0; i < routerTable.size(); i++) {
            s+= routerTable.get(i);
            s+= "\n";
        }
        return s;
    }
}
