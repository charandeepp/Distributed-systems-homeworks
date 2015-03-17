echo "compiling source code"
javac SingleBankClient.java

echo "starting rmi single threaded client, please change this if you would like to change the server port"
java -Djava.security.policy=mySecurityPolicyfile SingleBankClient kh1262-08.cselabs.umn.edu 51100
