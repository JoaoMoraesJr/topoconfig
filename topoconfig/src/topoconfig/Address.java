/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topoconfig;

/**
 *
 * @author Pichau
 */
public class Address {
    int [] address = new int [4];
    int mask;
    
    public Address (int[] address) {
        this.address = address.clone();
    }
    
    public Address (int [] address, int mask) {
        this.address = address.clone();
        this.mask = mask;
    }

    public Address() {
    }
    
    public void setAddress (int [] address) {
        this.address = address.clone();
    }
    
    public void setMask (int mask) {
        this.mask = mask;
    }
    
    public Address sumAddress (int sumToAddress) { //Sum this address to an int and return it without overwriting.
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
        Address addressToReturn = new Address (numAddress, mask);
        return addressToReturn;
    }
    
    public String toString () {
        String s = "";
        if (address == null) return "null";
        for (int i = 0; i < 3; i ++) {
            s+= address[i] + ".";
        }
        s+= address[3];
        return s;
    }
    
}
