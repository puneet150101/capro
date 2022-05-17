import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class processor {
    public static void main(String[] args) {
        VisualFrame visualFrame = new VisualFrame();
    }
}
class VisualFrame extends JFrame{
    VisualFrame(){
        Arch arch = new Arch();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(arch);
        pack();
        setLocationRelativeTo(null);
    }
}
class Arch extends JPanel{
    Color[] colors = new Color[14];
    Color[] sigs = new Color[25];
    int t,ins,hexCode = 0,total;
    boolean rec;
    JButton init = new JButton("Initiate");
    JButton next = new JButton("next");
    JButton updateRam = new JButton("<html>Update<br/>RAM</html>");
    JButton load = new JButton("Load");
    JTextArea code = new JTextArea("mvia 0x05\n" +
            "mvic 0x02\n" +
            "call 0x0B\n" +
            "inra\n" +
            "dcrc\n" +
            "jnz 0x06\n" +
            "hlt\n" +
            "addac\n" +
            "ret");
    JTable ramTab = new JTable();
    DefaultTableModel rammod;
    JScrollPane codeScroll;
    JScrollPane tableScroll;
    ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
    ArrayList<ArrayList<Integer>> conSigs = new ArrayList<>();
    String[] assem = new String[1];
    Arch(){
        reset();
        backend.setup();
        backend.initialize();
        updateRam();
        setVisible(true);
        setLayout(null);
        ins=0;

        setPreferredSize(new Dimension(1000,730));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + "," + y);
            }
        });
        init.setBounds(420,520,100,30);
        next.setBounds(420,560,100,30);
        updateRam.setBounds(420,600,100,45);
        load.setBounds(420,480,100,30);
        code.setBounds(80,475,350-80,675-475);
        code.setLineWrap(true);
        add(code);
        codeScroll = new JScrollPane(code);
        codeScroll.setBounds(80,475,350-80,675-475);
        codeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(codeScroll);
        ramTab.setCellSelectionEnabled(true);
        ramTab.setBounds(95,275,267-95,437-275);
        tableScroll = new JScrollPane(ramTab);
        tableScroll.setBounds(95,275,267-95,437-275);
        tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(tableScroll);
        init.setEnabled(false);
        next.setEnabled(false);
        add(next);
        add(init);
        add(updateRam);
        add(load);
        load.addActionListener(ae->{
            backend.initialize();
            backend.executeAllCode(code.getText());
            assem = code.getText().split("\n");
            total = assem.length;
            rec=true;
            for(String e:assem){
                if(e.trim().equals("hlt")){
                    rec = false;
                }
            }
            if(!rec){
                updateRam();
                init.setEnabled(true);
            }
            reset();repaint();
        });
        init.addActionListener(ae->{
            reset();
            repaint();
            t=0;
            hexCode = backend.RAM[backend.pc];
            updateColors();
            System.out.println(ins);
        });
        next.addActionListener(ae->{
            reset();
            //System.out.println(t);
            //System.out.println(backend.time);
            backend.executeCode(hexCode,t);
            for(Integer ar: backend.paths)
                colors[ar] = Color.green;
            for(Integer ar: backend.sig)
                sigs[ar] = Color.green;
            updateRam();
            repaint();
            t++;
            if(t==backend.time) {
                next.setEnabled(false);

                if(hexCode!=2) {
                    ins++;
                    init.setEnabled(true);
                }
            }
        });
        updateRam.addActionListener(ae->{
            int rowCount = rammod.getRowCount();
            int columnCount = rammod.getColumnCount();
            for(int j=0;j<rowCount;j++){
                backend.RAM[j]=backend.hexToDec(rammod.getValueAt(j,1).toString());
//                //System.out.println(backend.RAM[j]);
            }
            updateRam();
        });

    }
    public void reset(){
        for (int i = 0; i < 14; i++) {
            colors[i] = Color.gray;
        }
        for (int i=0;i<25;i++)
            sigs[i] = Color.gray;
    }
    public void updateRam(){
        String[][] ram = new String[256][2];
        for(int i=0;i<256;i++){
            ram[i][0]="0x"+backend.decToHex(i).toUpperCase();
            ram[i][1]="0x"+backend.decToHex(backend.RAM[i]).toUpperCase();
        }
        rammod = new DefaultTableModel(ram,new String[]{"Address","Data"});
        ramTab.setModel(rammod);
    }
    public void updateColors(){
        next.setEnabled(true);
        init.setEnabled(false);
        t=0;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        {
            g2.setColor(Color.black);
            g2.drawRect(80,70,775,385);//main
            g2.drawRect(140,95,225-140,135-95);//pc
            g2.drawRect(140,170,225-140,210-170);//mar
            g2.drawRect(95,240,265-95,435-240);//ram
            g2.drawRect(760,300,835-760,435-300);//rom
            g2.drawRect(665,320,65,355-320);//ir
            g2.drawRect(300,370,65,40) ;//in
            g2.drawRect(410,370,80,40);//out
            g2.drawRect(540,370,65,40);//b
            g2.drawRect(725,140,835-725,205-140);//alu
            g2.drawRect(580,95,645-580,140-95);//a
            g2.drawRect(480,95,80,140-95);//tmp
            g2.drawRect(400,95,535-470,140-95);//c
            g2.drawRect(300,95,535-450,140-95);//stack-pointer
            g2.drawRect(585,480,855-585,585-480);//curr_ins_block
        }//blocks

        {
            g2.setFont(new Font("Courier",Font.BOLD,15));
            g2.setColor(sigs[0]);
            g2.drawString("E",140,90);//pc
            g2.setColor(sigs[1]);
            g2.drawString("L",177,90);
            g2.setColor(sigs[2]);
            g2.drawString("I",210,90);

            g2.setColor(sigs[3]);
            g2.drawString("E",123,185);//mar
            g2.setColor(sigs[4]);
            g2.drawString("L",123,205);

            g2.setColor(sigs[5]);
            g2.drawString("E",140,235);//ram
            g2.setColor(sigs[6]);
            g2.drawString("L",95,235);

            g2.setColor(sigs[7]);
            g2.drawString("E",300,430);//in
            g2.setColor(sigs[8]);
            g2.drawString("L",355,430);

            g2.setColor(sigs[9]);
            g2.drawString("E",420,430);//out
            g2.setColor(sigs[10]);
            g2.drawString("L",475,430);

            g2.setColor(sigs[11]);
            g2.drawString("E",540,430);
            g2.setColor(sigs[12]);
            g2.drawString("L",595,430);//b

            g2.setColor(sigs[13]);
            g2.drawString("L",690,370);//ir

            g2.setColor(sigs[14]);
            g2.drawString("E",400,90);
            g2.setColor(sigs[15]);
            g2.drawString("L",455,90);//c

            g2.setColor(sigs[16]);
            g2.drawString("E",490,90);
            g2.setColor(sigs[17]);
            g2.drawString("L",545,90);//tmp

            g2.setColor(sigs[18]);
            g2.drawString("E",580,90);
            g2.setColor(sigs[19]);
            g2.drawString("L",605,90);
            g2.setColor(sigs[20]);
            g2.drawString("I",635,90);//a

            g2.setColor(sigs[21]);
            g2.drawString("E",300,90);//stack pointer
            g2.setColor(sigs[22]);
            g2.drawString("L",325,90);
            g2.setColor(sigs[23]);
            g2.drawString("I",353,90);
            g2.setColor(sigs[24]);
            g2.drawString("D",375,90);
        }//signals

        {
            g2.setColor(colors[0]);
            g2.fillRect(180,135,10,170-135);//pc-mar
            g2.setColor(colors[1]);
            g2.fillRect(180,210,10,240-210);//mar-ram
            g2.setColor(colors[2]);
            g2.fillRect(265,330,665-265,10);//ram-ir
            g2.setColor(colors[3]);
            g2.fillRect(730,330,760-730,10);//ir-rom
            g2.setColor(colors[4]);
            g2.fillRect(327,340,10,370-340);//ram-in
            g2.setColor(colors[5]);
            g2.fillRect(447,340,10,370-340);//ram-out
            g2.setColor(colors[6]);
            g2.fillRect(567,340,10,370-340);//ram-b
            g2.setColor(colors[7]);
            g2.fillRect(605,140,10,330-140);//ram-a
            g2.setColor(colors[8]);
            g2.fillRect(630,165,10,330-165);//ram-alu1
            g2.fillRect(630,165,725-630,10);//ram-alu2
            g2.setColor(colors[9]);
            g2.fillRect(770,110,10,140-110);//alu-a1
            g2.fillRect(645,110,770-645,10);//alu-a2
            g2.setColor(colors[10]);
            g2.fillRect(520,140,10,330-140);//ram-tmp
            g2.setColor(colors[11]);
            g2.fillRect(428,140,10,330-140);//ram-c
            g2.setColor(colors[12]);
            g2.fillRect(338,140,10,330-140);//ram-sp
            g2.setColor(colors[13]);
            g2.fillRect(190,150,338-190,10);//pc-sp
        } //paths

        {
            g2.setFont(new Font("Courier",Font.BOLD,14));
            g2.setColor(Color.darkGray);
            g2.drawString("PC:0x"+backend.decToHex(backend.pc),150,120); //pc
            g2.drawString("MAR:0x"+backend.decToHex(backend.mar),148,198); //mar
            g2.drawString("RAM:0x"+backend.decToHex(backend.RAM[backend.mar]),125,265);
            g2.drawString("IN:0x00",307,398); //in
            g2.drawString("OUT:0x00",415,398); //out
            g2.drawString("B:0x"+backend.decToHex(backend.B),548,398); //b
            g2.drawString("IR:0x"+backend.decToHex(backend.IR),672,345); //ir
            g2.drawString("ALU",755,160); //alu
            g2.drawString(backend.alu,750,185); //aluexp
            g2.drawString("A:0x"+backend.decToHex(backend.A),588,123); //a
            g2.drawString("TMP:0x"+backend.decToHex(backend.TMP),488,123); //tmp
            g2.drawString("C:0x"+backend.decToHex(backend.C),408,123); //c
            g2.drawString("SP:0x"+backend.decToHex(backend.stackPointer),310,123); //sp
            g2.drawString("ROM",777,355);

        } //register values

        {
            g2.drawRect(660,400,735-660,435-400);
            g2.drawLine(697,400,697,435);
            g2.setFont(new Font("Courier",Font.BOLD,20));
            if(backend.z == 1) g2.setColor(Color.green);
            else g2.setColor(Color.gray);
            g2.drawString("Z",670,425);
            if(backend.c == 1) g2.setColor(Color.green);
            else g2.setColor(Color.gray);
            g2.drawString("C",710,425);
        }//flags

        {
            if(rec){
                g2.setColor(Color.red);
               g2.drawString("No halt found!",600,505);
               g2.drawString("Halt needed!",600,535);
            }
            else if (total>ins) {
//                g2.setFont();
                //System.out.println(assem[ins]);
                g2.setColor(Color.darkGray);
                g2.drawString("Current instruction:",600,505);
                g2.drawString(backend.getInstruction(hexCode)+" clock cycle = "+t+"",600,535);
            }
        }//curr_ins+hlt check

    }
}
class backend{

    static HashMap<String,Integer> instructionSet;
    static int [] RAM;
    static ArrayList<Integer> paths;
    static ArrayList<Integer> sig;
    static int z,c,time;
    static int A,B,C,IR,TMP;
    static int stackPointer;
    static String alu;
    static int pc,mar,clock;
    public static void setup(){
        instructionSet = new HashMap<>();
        putinstructions();
        initialize();
    }
    public static void initialize() {
        z = 0;c = 0;
        RAM = new int[256];
        alu=new String();
        //point to last filled location
        paths = new ArrayList<>();
        sig = new ArrayList<>();
        stackPointer = 256;
        A=0;B=0;C=0;TMP=0;
        pc = 0;mar = pc;
    }

    static void executeCode(int hexCode,int clock) {
//        String INSTRUC = e;
//        INSTRUC = INSTRUC.toLowerCase();
//        String[] parts = INSTRUC.trim().split("[\\s+|\\,]");
//        if(!instructionSet.containsKey(parts[0])) {
//            //System.out.println("instruction not present");
//        }else {
//            int hexCode = instructionSet.get(parts[0]);
            if(hexCode==0) {

            }else
                callmethod(hexCode,clock);


    }
    static void executeAllCode(String code) {
        String INSTRUC[] = code.split("\r?\n|\r");
        int itr = 0;
        for(int i = 0;i<INSTRUC.length;i++) {
            String[] parts = INSTRUC[i].trim().split("[\\s+|\\,]");
            int hex = instructionSet.get(parts[0]);
            if(hex==8||hex==2||hex==1||hex==15||hex==16||hex==23||hex==24||hex==17||hex==18||hex==19||hex==20||hex==21||hex==22||hex==0) {
                RAM[itr++] = hex;
            }else {
                RAM[itr++] = hex;
                System.out.println(parts[0]);
                    RAM[itr++] = hexToDec(parts[1]);
            }
        }

    }
    static int hexToDec(String hex) {
        //remove first two Character 0x
        return Integer.parseInt(hex.substring(2),16);
    }
    static String getInstruction(int hexcode){
        for(String e:instructionSet.keySet()){
            if(instructionSet.get(e)==hexcode)return e.toUpperCase();
        }
            return " ";
    }
    static String decToHex(int dec) {
        return Integer.toHexString(dec);
    }
    static void putinstructions() {
        instructionSet.put("hlt", 2);
        instructionSet.put("movab", 1);
        instructionSet.put("addab", 15);
        instructionSet.put("addac", 16);
        instructionSet.put("subab", 23);
        instructionSet.put("subac", 24);
        instructionSet.put("jmp", 4);
        instructionSet.put("jz", 6);
        instructionSet.put("jnz", 11);
        instructionSet.put("mvia", 12);
        instructionSet.put("mvib", 13);
        instructionSet.put("mvic", 14);
        instructionSet.put("call",7);
        instructionSet.put("ret", 8);
        instructionSet.put("inra", 17);
        instructionSet.put("inrb", 18);
        instructionSet.put("inrc", 19);
        instructionSet.put("dcra", 20);
        instructionSet.put("dcrb", 21);
        instructionSet.put("dcrc", 22);
        instructionSet.put("cmp", 25);
        instructionSet.put("lda", 26);
        instructionSet.put("sta", 27);
    }
    private static void callmethod(int hexCode,int clock) {
        if(hexCode==1) {//mov
            time = 3;
            movAB(clock);
        }
        else if(hexCode==15) {//add
            time = 3;
            addab(clock);
        }
        else if(hexCode==16) {//add
            time = 3;
            addac(clock);
        }
        else if(hexCode==23) {//sub
            time = 3;
            subab(clock);
        }
        else if(hexCode==24) {//sub
            time = 3;
            subac(clock);
        }
        else if(hexCode==4) {//jmp
            time = 5;
            jmp(clock);
        }
        else if(hexCode==12) {//mvi
            time =4;
            mvia(clock);
        }
        else if(hexCode==13) {//mvi
            time =4;
            mvib(clock);
        }
        else if(hexCode==14) {//mvi
            time =4;
            mvic( clock);
        }
        else if(hexCode==6) {//jz
            time = 5;
            jz(clock);
        }
        else if(hexCode==7) {
            time = 7;
            call(clock);
        }
        else if(hexCode==8) {
            time = 4;
            ret( clock);
        }
        else if(hexCode==17) {
            time = 3;
            inra(clock);
        }
      else if(hexCode==18) {
            time = 3;
            inrb(clock);
        }
        else if(hexCode==19) {
            time = 3;
            inrc(clock);
        }

        else if(hexCode==20) {
            time = 3;
            dcra(clock);
        }  else if(hexCode==21) {
            time = 3;
            dcrb(clock);
        }
        else if(hexCode==22) {
            time = 3;
            dcrc(clock);
        }

        else if(hexCode==11) {
            time = 5;
            jnz(clock);
        }
        else if(hexCode==2){
            time = 3;
            hlt(clock);
        }
        else if(hexCode==25){
            time = 4;
            cmp(clock);
        }
        else if(hexCode==26){
            time = 6;
            lda(clock);
        }
        else if(hexCode==27){
            time = 6;
            sta(clock);
        }
    }

    static void hlt(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
//	            RAM[0]=1;
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
//	            RAM[1]=5;
        }
    }
    static void movAB(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
//	            RAM[0]=1;
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
//	            RAM[1]=5;
        }
        else{
            A =B;
            sig = new ArrayList<>(Arrays.asList(11,19));
            paths = new ArrayList<>(Arrays.asList(6,2,7));
        }
    }
    static void mvia(int clock){
        //System.out.println("in"+"  "+clock);
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else{
            pc++;
                A = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(5,19));
                paths = new ArrayList<>(Arrays.asList(2,7));
        }

    }
    static void mvib(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else{
            pc++;
                B = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(5,12));
                paths = new ArrayList<>(Arrays.asList(2,6));
        }

    }
    static void mvic(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else{
            pc++;
                C = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(5,15));
                paths = new ArrayList<>(Arrays.asList(2,11));
        }

    }
    static void call(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==3){
            pc++;
            TMP = RAM[mar];
            stackPointer--;
            sig = new ArrayList<>(Arrays.asList(2,5,17,24));
            paths = new ArrayList<>(Arrays.asList(2,10));
        }else if(clock==4){
            mar = stackPointer;
            sig = new ArrayList<>(Arrays.asList(21,3,4));
            paths = new ArrayList<>(Arrays.asList(13,1,0,12));
        }else if(clock==5){
            RAM[mar] = pc;
            sig = new ArrayList<>(Arrays.asList(0,6));
            paths = new ArrayList<>(Arrays.asList(0,13,12,2));
        }
        else {
            pc = TMP;
            sig = new ArrayList<>(Arrays.asList(16,1));
            paths = new ArrayList<>(Arrays.asList(2,10,12,13,0));
        }
    }
    static void lda(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==3){
            pc++;
            TMP = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,17));
            paths = new ArrayList<>(Arrays.asList(2,10));
        }else if(clock==4){
            mar = TMP;
            sig = new ArrayList<>(Arrays.asList(16,3,4));
            paths = new ArrayList<>(Arrays.asList(10,2,12,13,0,1));
        }else if(clock==5){
            A = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(19,5));
            paths = new ArrayList<>(Arrays.asList(7,2));
        }
    }
    static void sta(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==3){
            pc++;
            TMP = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,17));
            paths = new ArrayList<>(Arrays.asList(2,10));
        }else if(clock==4){
            mar = TMP;
            sig = new ArrayList<>(Arrays.asList(16,3,4));
            paths = new ArrayList<>(Arrays.asList(10,2,12,13,0,1));
        }else if(clock==5){
            RAM[mar] = A;
            sig = new ArrayList<>(Arrays.asList(18,6));
            paths = new ArrayList<>(Arrays.asList(7,2));
        }
    }
    static void ret(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = stackPointer;
            sig = new ArrayList<>(Arrays.asList(21,3,4));
            paths = new ArrayList<>(Arrays.asList(12,13,0,1));
        }else{
            stackPointer++;
            pc = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(23,5,1));
            paths = new ArrayList<>(Arrays.asList(2,12,13,0));
        }

    }
    static void jmp(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==3){
            sig = new ArrayList<>(Arrays.asList(2,5,17));
            paths = new ArrayList<>(Arrays.asList(2,10));
        }else if(clock==4){
            sig = new ArrayList<>(Arrays.asList(16,1));
            paths = new ArrayList<>(Arrays.asList(13,10,2,0,12));
        }
    }
    static void cmp(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==3){
            pc++;
            if(RAM[mar]>A) {
                c = 1;z = 0;
            }else if(RAM[mar]==A) {
                z = 1;
                c = 0;
            }else {
                c = 0;z = 0;
            }
            sig = new ArrayList<>(Arrays.asList(2,5,18));
            paths = new ArrayList<>(Arrays.asList(2,9,8));
        }
    }
    static void addab(int clock) {
            if(clock==0){
                mar =pc;
                sig = new ArrayList<>(Arrays.asList(0,3,4));
                paths = new ArrayList<>(Arrays.asList(0,1));
            }else if(clock==1){
                pc++;
                IR = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(2,5,13));
                paths = new ArrayList<>(Arrays.asList(2));
            }
            else if(clock==2){
                A = A+B;
                sig = new ArrayList<>(Arrays.asList(18,19,11));
                paths = new ArrayList<>(Arrays.asList(9,6,2,8));
            }
    }
    static void addac(int clock) {
            if(clock==0){
                mar =pc;
                sig = new ArrayList<>(Arrays.asList(0,3,4));
                paths = new ArrayList<>(Arrays.asList(0,1));
            }else if(clock==1){
                pc++;
                IR = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(2,5,13));
                paths = new ArrayList<>(Arrays.asList(2));
            }
            else if(clock==2){
                A = A+C;
                sig = new ArrayList<>(Arrays.asList(18,19,14));
                paths = new ArrayList<>(Arrays.asList(9,11,2,8));
            }
    }
    static void subab(int clock) {
            if(clock==0){
                mar =pc;
                sig = new ArrayList<>(Arrays.asList(0,3,4));
                paths = new ArrayList<>(Arrays.asList(0,1));
            }else if(clock==1){
                pc++;
                IR = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(2,5,13));
                paths = new ArrayList<>(Arrays.asList(2));
            }
            else if(clock==2){
                A = A-B;
                sig = new ArrayList<>(Arrays.asList(18,19,11));
                paths = new ArrayList<>(Arrays.asList(9,6,2,8));
            }

    }
    static void subac(int clock) {
            if(clock==0){
                mar =pc;
                sig = new ArrayList<>(Arrays.asList(0,3,4));
                paths = new ArrayList<>(Arrays.asList(0,1));
            }else if(clock==1){
                pc++;
                IR = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(2,5,13));
                paths = new ArrayList<>(Arrays.asList(2));
            }
            else if(clock==2){
                A = A-C;
                sig = new ArrayList<>(Arrays.asList(18,19,14));
                paths = new ArrayList<>(Arrays.asList(9,11,2,8));
            }

    }

    static void jz(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            if(z==0) {
                return;
            }else {
                mar =pc;
                sig = new ArrayList<>(Arrays.asList(0,3,4));
                paths = new ArrayList<>(Arrays.asList(0,1));
            }
        }else if(clock==3){
            pc++;
            TMP = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,17));
            paths = new ArrayList<>(Arrays.asList(2,10));
        }else if(clock==4){
            pc = TMP;
            sig = new ArrayList<>(Arrays.asList(16,1));
            paths = new ArrayList<>(Arrays.asList(13,10,2,0,12));
        }
    }
    static void jnz(int clock) {
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }
        else if(clock==2){
            if(z!=0) {
                pc++;
                time=3;
                return;
            }else {
                mar =pc;
                sig = new ArrayList<>(Arrays.asList(0,3,4));
                paths = new ArrayList<>(Arrays.asList(0,1));
            }
        }else if(clock==3){
            pc++;
            TMP = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,17));
            paths = new ArrayList<>(Arrays.asList(2,10));
        }else if(clock==4){
            pc = TMP;
            sig = new ArrayList<>(Arrays.asList(16,1));
            paths = new ArrayList<>(Arrays.asList(13,10,2,0,12));
        }
    }
    static void inra(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }else{
                A++;
                //System.out.println(A);
//                sig = new ArrayList<>(Arrays.asList(18,19));
//                paths = new ArrayList<>(Arrays.asList(9));

        }
    }
    static void inrb(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }else{

                B++;
//                sig = new ArrayList<>(Arrays.asList(18,19));
//                paths = new ArrayList<>(Arrays.asList(9));

        }
    }
    static void inrc(int clock){
        if(clock==0){
            mar =pc;
            sig = new ArrayList<>(Arrays.asList(0,3,4));
            paths = new ArrayList<>(Arrays.asList(0,1));
        }else if(clock==1){
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2,5,13));
            paths = new ArrayList<>(Arrays.asList(2));
        }else{

                C++;
//                sig = new ArrayList<>(Arrays.asList(18,19));
//                paths = new ArrayList<>(Arrays.asList(9));
            }

    }
    static void dcra( int clock) {
        if (clock == 0) {
            mar = pc;
            sig = new ArrayList<>(Arrays.asList(0, 3, 4));
            paths = new ArrayList<>(Arrays.asList(0, 1));
        } else if (clock == 1) {
            pc++;
            IR = RAM[mar];
            sig = new ArrayList<>(Arrays.asList(2, 5, 13));
            paths = new ArrayList<>(Arrays.asList(2));
        } else {
            A--;
            if (A == 0) z = 1;
//                sig = new ArrayList<>(Arrays.asList(18,19));
//                paths = new ArrayList<>(Arrays.asList(9));

        }
    }
        static void dcrb(int clock){
            if (clock == 0) {
                mar = pc;
                sig = new ArrayList<>(Arrays.asList(0, 3, 4));
                paths = new ArrayList<>(Arrays.asList(0, 1));
            } else if (clock == 1) {
                pc++;
                IR = RAM[mar];
                sig = new ArrayList<>(Arrays.asList(2, 5, 13));
                paths = new ArrayList<>(Arrays.asList(2));
            } else {
                B--;
                if (B == 0) z = 1;
//                sig = new ArrayList<>(Arrays.asList(18,19));
//                paths = new ArrayList<>(Arrays.asList(9));

            }
        }
            static void dcrc (int clock){
                if (clock == 0) {
                    mar = pc;
                    sig = new ArrayList<>(Arrays.asList(0, 3, 4));
                    paths = new ArrayList<>(Arrays.asList(0, 1));
                } else if (clock == 1) {
                    pc++;
                    IR = RAM[mar];
                    sig = new ArrayList<>(Arrays.asList(2, 5, 13));
                    paths = new ArrayList<>(Arrays.asList(2));
                } else {

                    C--;
                    if (C == 0) z = 1;
//                sig = new ArrayList<>(Arrays.asList(18,19));
//                paths = new ArrayList<>(Arrays.asList(9));

                }
            }
        }

