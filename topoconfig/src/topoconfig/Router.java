/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topoconfig;

import java.util.ArrayList;

/**
 *
 * @author Pichau
 */
public class Router {
    String name;
    int connections;
    boolean marked = false;
    ArrayList<String> netNamesList = new ArrayList<String>();
    ArrayList<Address> netPortList = new ArrayList<Address>();
    
    public Router (String name, int connections) {
        this.name = name;
        this.connections = connections;
    }
    
    public void addNetworkConnection (Network net) {
        netNamesList.add(net.name);
    }
}
