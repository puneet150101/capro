import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class first {
    public static void main(String[] args) {
        Gui gui = new Gui();
    }
}
class Gui extends JFrame
{
    JLabel l = new JLabel("Testing the label");
    ArrayList<HashMap<Integer,String>> a = new ArrayList<>();
    String[][] data ={{"1", "Test"},{"1", "Test"},{"1", "Test"},{"1", "Test"},{"1", "Test"},{"1", "Test"},
            {"1", "Test"},{"1", "Test"},{"1", "Test"},{"1", "Test"}};
    String[] column = {"address","data"};
    JTable table = new JTable(data,column);
    JTextArea text =  new JTextArea();
    JScrollPane scroll;
    public Gui(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + "," + y);
            }
        });
        table.setCellSelectionEnabled(true);
//        ListSelectionModel select = table.getSelectionModel();
//        select.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        for(int i=0;i<a.size();i++)
        setLayout(null);
        setVisible(true);
        setSize(500,500);
        setDefaultCloseOperation(3);
        table.setBounds(20,20,100,100);
//        add(list);
        text.setLineWrap(true);
        scroll = new JScrollPane(table);
        scroll.setBounds(20,20,100,100);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
    }
}