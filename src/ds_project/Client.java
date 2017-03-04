package ds_project;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import ds_project.node.Messages.*;

public class Client {

    static private String remotePath = null; // Akka path of the bootstrapping peer
    static private String operation = null;
    static private Integer key = null;
    static private String value = null;

    public static void main(String[] args) {
        if ((args.length != 4) && (args.length != 5) && (args.length != 6)) {
            System.out.println("Operation not recognized");
            System.exit(0);
        }

        if ((args.length == 4 && args[3].equals("leave"))
                || (args.length == 5 && args[3].equals("read"))
                || (args.length == 6 && args[3].equals("write"))) {

            Config config = ConfigFactory.load(args[0]);

            String ip = args[1];
            String port = args[2];

            remotePath = "akka://DHTsystem@" + ip + ":" + port + "/user/node";

            final ActorSystem clientSystem = ActorSystem.create("clientSystem", config);

            if (args.length == 4 && args[3].equals("leave")) {
                System.out.println("\nOperation requested: LEAVE\n");
                operation = "leave";
            } else if (args.length == 5 && args[3].equals("read")) {
                System.out.println("\nOperation requested: GET - Key requested: " + args[4] + "\n");
                operation = "read";
                key = Integer.valueOf(args[4]);
            } else if (args.length == 6 && args[3].equals("write")) {
                System.out.println("\nOperation requested: WRITE - Key requested:" + args[4] + " New value: " + args[5] + "\n");
                operation = "write";
                key = Integer.valueOf(args[4]);
                value = args[5];
            } else {
                System.out.println("Wrong parameters for operation: " + args[3]);
            }

            final ActorRef client = clientSystem.actorOf(
                    Props.create(ClientNode.class), // actor class
                    "client" // actor name
            );
        }
    }

    public static class ClientNode extends UntypedActor {

        public void preStart() throws InterruptedException {
            if (remotePath != null && operation != null) {
                ActorSelection coordinator = context().actorSelection(remotePath);

                System.out.println("--- Sending " + operation
                        + " request message to " + remotePath + " ---");

                if (operation.equals("read")) {
                    coordinator.tell(new GetKey(key), getSelf());
                } else if (operation.equals("write")) {
                    coordinator.tell(new Update(key, value), getSelf());
                } else if (operation.equals("leave")) {
                    coordinator.tell(new Leave(), getSelf());
                } else {
                    getContext().stop(getSelf());
                }
            } else {
                getContext().stop(getSelf());
            }
        }

        public void onReceive(Object message) {
            //When receiving a DataItem as response (richiesta Get)
            if (message instanceof DataItem) {
                ImmutableItem item = ((DataItem) message).item;
                if (item != null) {
                    System.out.println(item.getKey() + " , " + item.getValue() + " , " + item.getVersion());
                } else {
                    System.out.println("Item with key " + key + " not present!");
                }
                getContext().stop(getSelf());
            } else if (message instanceof Terminated) {
                System.out.println(getSender() + " has successfully left the network");
                getContext().stop(getSelf());
            } else if (message instanceof String) {
                System.out.println(message);
                getContext().stop(getSelf());
            } else {
                unhandled(message);        // this actor does not handle any incoming messages
                getContext().stop(getSelf());
            }
        }

        @Override
        public void postStop() {
            System.out.print("I have stopped!");
            System.exit(0);
        }
    }
}
