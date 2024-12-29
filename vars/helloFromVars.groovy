import com.purnima.jain.EchoChamber

def call(String helloMessage) {
    echo "Inside helloFromVars.groovy....."
    echo "${helloMessage}" // Hello from vars directory....

    EchoChamber echoChamber = new EchoChamber();
    echoChamber.echoMessage("Hello from Echo Chamber...")
}