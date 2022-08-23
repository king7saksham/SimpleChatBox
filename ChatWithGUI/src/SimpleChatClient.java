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
    JFrame theFrame;
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    String name;
    String ip;

    public static void main(String[] args) {
        SimpleChatClient client = new SimpleChatClient();
        client.go();
    }

    public void go(){
        theFrame = new JFrame("Simple Chat Box");
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
        theFrame.getContentPane().add(BorderLayout.CENTER,panel);
        nameAndIp();

        theFrame.getRootPane().setDefaultButton(sendButton);
        theFrame.setSize(400,350);
        theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        theFrame.setVisible(true);
    }

    public void setUpNetworking(){
        try {
            sock = new Socket(ip,2002);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Network Established");

            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();
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

    public void nameAndIp() {
        Object[] option = {"Enter"};

        JTextField nameField = new JTextField(20);
        JTextField ipField = new JTextField(20);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Please Enter your Name: "));
        panel.add(nameField);
        panel.add(new JLabel("Please Enter the Server IP Address: "));
        panel.add(ipField);

        panel.setLayout(new GridLayout(2, 2));

        JOptionPane.showOptionDialog(theFrame,
                panel,
                "User Name and Server IP",
                JOptionPane.YES_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                option,
                option[0]);
        name = nameField.getText();
        ip = ipField.getText();

        setUpNetworking();
    }
}
