echo "compiling source code"
javac MultiBankClient.java

echo "starting rmi multi threaded client, please change this if you would like to change the server port"
java -Djava.security.policy=mySecurityPolicyfile MultiBankClient kh1262-08.cselabs.umn.edu 51100 100 100
