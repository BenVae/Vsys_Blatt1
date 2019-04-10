package aufgabe2;

import rm.requestResponse.Component;
import rm.requestResponse.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class PrimeClient extends Thread {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 1234;
    private static final String REQUEST_MODE = "b";
    private static final long INITIAL_VALUE = (long) 1e17;
    private static final long COUNT = 20;
    private static final String CLIENT_NAME = PrimeClient.class.getName();

    private Component communication;
    String hostname, requestMode;
    int port;
    long initialValue, count;

    public PrimeClient(String hostname, int port, String requestMode, long initialValue, long count) {
        this.hostname = hostname;
        this.port = port;
        this.requestMode = requestMode;
        this.initialValue = initialValue;
        this.count = count;
    }

    public void run() {
        try {
            communication = new Component();
            for (long i = initialValue; i < initialValue + count; i++) {
                switch (requestMode) {
                    case "p":
                        polling(i);
                        break;
                    case "c":
                        concurrent(i);
                        break;
                    default:
                        synchronised(i);
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void synchronised(long value) throws IOException, ClassNotFoundException {
        System.out.print(value + ": ");

        communication.send(new Message(hostname, port, value), false);
        Boolean isPrime = (Boolean) communication.receive(port, true, true).getContent();

        System.out.println((isPrime ? "prime" : "not prime"));
    }

    private void concurrent(long value) throws IOException, ClassNotFoundException {
        System.out.print(value + ": ");

        communication.send(new Message(hostname, port, value), false);

        Thread thread = new AusgabeThread();
        thread.start();

        Boolean isPrime = (Boolean) communication.receive(port, true, true).getContent();

        thread.stop();

        System.out.println((isPrime ? " prime" : " not prime"));
    }

    private void polling(long value) throws IOException, ClassNotFoundException {
        System.out.print(value + ": ");

        while (true) {
            communication.send(new Message(hostname, port, value), true);

            System.out.print(".");

            Message response = communication.receive(port, false, true);
            if (response != null) {
                Boolean isPrime = (Boolean) response.getContent();
                System.out.println((isPrime ? " prime" : " not prime"));
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String args[]) throws IOException {
        String hostname = HOSTNAME;
        int port = PORT;
        String requestMode = REQUEST_MODE;
        long initialValue = INITIAL_VALUE;
        long count = COUNT;

        boolean doExit = false;

        String input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to " + CLIENT_NAME + "\n");

        while (!doExit) {
            System.out.print("Server hostname [" + hostname + "] > ");
            input = reader.readLine();
            if (!input.equals("")) hostname = input;

            System.out.print("Server port [" + port + "] > ");
            input = reader.readLine();
            if (!input.equals("")) port = Integer.parseInt(input);

            System.out.println("Request Modes: (b)locking, (p)olling, (c)oncurrent");
            System.out.print("Request mode [" + requestMode + "] > ");
            input = reader.readLine();
            if (!input.equals("")) requestMode = input;

            System.out.print("Prime search initial value [" + initialValue + "] > ");
            input = reader.readLine();
            if (!input.equals("")) initialValue = Integer.parseInt(input);

            System.out.print("Prime search count [" + count + "] > ");
            input = reader.readLine();
            if (!input.equals("")) count = Integer.parseInt(input);

            new PrimeClient(hostname, port, requestMode, initialValue, count).run();

            System.out.println("Exit [n]> ");
            input = reader.readLine();
            if (input.equals("y") || input.equals("j")) doExit = true;
        }
    }
}

	
