import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class graphics {
    public static void main(String[] args) {
        Draw draw = new Draw();
    }
}
class Draw extends JFrame {
    PanelGui panelGui = new PanelGui();
    public Draw(){
        setVisible(true);
        setDefaultCloseOperation(3);
        add(panelGui);
        pack();
//        setLayout(null);

        setLocationRelativeTo(null);

    }
}
class PanelGui extends Panel{

    Color color = Color.blue;
    String text = new String("Initially");
    JButton b1 = new JButton("Test");

    PanelGui(){
        setLayout(null);
        setPreferredSize(new Dimension(1000,700));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + "," + y);
            }
        });
        b1.setBounds(20,20,70,40);
        add(b1);
        setVisible(true);
        b1.addActionListener(ae->{
            color=Color.red;
            text = new String("String changed");
            repaint();
            System.out.println("work");
        });

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(color);
        g2.fillRect(200,200,500,50);
        g2.setFont(new Font("Courier",Font.BOLD,20));
        g2.drawString(text,400,400);
    }
}
