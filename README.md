# topoconfig

Ferramenta para configuração automática de topologias de redes.

Antes de iniciá-lo, é necessário criar um .txt que representa a topologia desejada seguindo o seguinte padrão:


#NETWORK
<net_name>, <num_nodes>
#ROUTER
<router_name>, <num_ports>, <net_name0>, <net_name1>, …, <net_nameN>

Para executar a ferramento, é necessário possuir o $.jar$ e o $.txt$ e utilizar o seguinte comando:

    java -jar topoconfig.jar <topologia.txt> <endereço/prefix>


Assim, o programa executará e irá imprimir uma saída com toda a configuração feita da topologia da rede no seguinte formato.


#NETWORK
<net_name>, <net_address/prefix>, <IP_range>
#ROUTER
<router_name>, <num_ports>, <IP0/prefix>, <IP1/prefix>, …, <IPN/prefix>
#ROUTERTABLE
<router_name>, <net_dest/prefix>, <nexthop>, <port>

