import jenkins.model.*
import hudson.slaves.*

def vmName = "test-cp"
def vmUser = "azureuser"
def vmPrivateKey = "/home/azureuser/.ssh"
def vmIpAddress = "10.188.0.56"
def jenkinsUrl = "https://jenkins.abu-dev.keurig.com/"

// Create a new SSH launcher
def launcher = new SSHLauncher(vmIpAddress, 22, vmUser, vmPrivateKey, "", "")

// Create a new DumbSlave with the SSH launcher
def node = new DumbSlave(vmName, "", launcher)

// Set the node's label and other properties
node.labelString = "view"
node.numExecutors = 1
node.remoteFS = "/home/azureuser/jenkins"
node.mode = Node.Mode.EXCLUSIVE
node.setRetentionStrategy(new RetentionStrategy.Always())

// Add the node to the Jenkins instance
Jenkins.instance.addNode(node)

// Wait for the node to come online
println "Waiting for node to come online..."
while (!node.toComputer().isOnline()) {
  Thread.sleep(1000)
}
println "Node is online!"

// Create a new JNLP launcher and launch the agent
def jnlpLauncher = new JNLPLauncher()
def channel = launcher.launch()
def slave = new SlaveComputer(node)
slave.setChannel(channel)
jnlpLauncher.launch(slave, TaskListener.NULL)

// Wait for the agent to come online
println "Waiting for agent to come online..."
while (!node.toComputer().isOnline()) {
  Thread.sleep(1000)
}
println "Agent is online!"
