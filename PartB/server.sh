echo "killing rmiregistry in case if it already exists"
/usr/bin/pkill rmiregistry

echo "sleeping: to make sure rmiregistry has been killed"
sleep 2

echo "restarting rmiregistry on port 51100, please change this if you would like to change the port"
rmiregistry 51100 &

echo "compiling source code"
javac IBankServer.java
javac BankServerImpl.java
rmic BankServerImpl

echo "starting rmi server on port 51100, please change this if you would like to change the port"
java -Djava.security.policy=mySecurityPolicyfile BankServerImpl 51100
