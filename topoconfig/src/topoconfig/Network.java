/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topoconfig;

/**
 *
 * @author 15201850
 */
public class Network {
    String name;
    int nodes;
    int index; //Position in connection matrix
    
    public Network (String name, int nodes, int index) {
        this.name = name;
        this.nodes = nodes;
        this.index = index;
    }
}