import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleChatClient {
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    String name;

    public static void main(String[] args) {
        SimpleChatClient client = new SimpleChatClient();
        client.go();
    }

    public void go(){
        JFrame frame = new JFrame("Simple Chat Box");
        JPanel panel = new JPanel();
        incoming = new JTextArea(15,30);
        incoming.setLineWrap(true);
        incoming.setEditable(false);
        incoming.setWrapStyleWord(true);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        panel.add(qScroller);
        panel.add(outgoing);
        panel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER,panel);
        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.setSize(400,500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new NameBox().start();
    }

    public void setUpNetworking(){
        try {
            sock = new Socket("192.168.1.12",2002);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Network Established");
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                writer.println(name+": "+outgoing.getText());
                writer.flush();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }


    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {

                while ((message = reader.readLine()) != null){
                    System.out.println("read "+message);
                    incoming.append(message+"\n");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public class NameBox implements ActionListener {
        JButton enter;
        JFrame frame;
        JTextField nameBar;
        @Override
        public void actionPerformed(ActionEvent e) {
            name = nameBar.getText();
            frame.dispose();
        }

        public void start(){
            frame = new JFrame("Name");
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Please Enter Your Name");
            nameBar = new JTextField(20);
            enter = new JButton("Enter");
            enter.addActionListener(this);
            panel.add(label);
            panel.add(nameBar);
            panel.add(enter);
            frame.getContentPane().add(panel);
            frame.setSize(300,200);
            frame.setVisible(true);
        }
    }
}
