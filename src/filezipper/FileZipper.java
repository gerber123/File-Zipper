
package filezipper;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.*;


public class FileZipper extends JFrame
{
    public static final int BUFFOR = 1024;   
    public FileZipper()
    {
        this.setTitle("Zipper");
        this.setBounds(275,300,250,250);
        
        Action akcjaDodawania = new Akcja("Dodaj","Dodaj nowy wpis do archiwum","ctrl D",new ImageIcon("ikona1.png"));
        Action akcjaUsuwania = new Akcja("Usun","Usuń wpis z listy ","ctrl U",new ImageIcon("ikona2.png"));
        Action akcjaZipowania = new Akcja("Zip","Zippuj kurde ten program","ctrl Z");
        

        
        this.setJMenuBar(PasekMenu);
        JMenu menuPlik = PasekMenu.add(new JMenu("Plik"));
        JMenuItem menuOtworz = menuPlik.add(akcjaDodawania);
        JMenuItem menuUsun = menuPlik.add(akcjaUsuwania);
        JMenuItem menuZip = menuPlik.add(akcjaZipowania);
        
        
        bdodaj = new JButton(akcjaDodawania);
        busun = new JButton(akcjaUsuwania);
        bzip = new JButton(akcjaZipowania);
            JScrollPane scrollek = new JScrollPane(lista);
        
        lista.setBorder(BorderFactory.createEtchedBorder());
        GroupLayout layout = new GroupLayout(this.getContentPane());
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
        layout.createSequentialGroup().addComponent(scrollek,100,150,Short.MAX_VALUE)
                .addContainerGap(0,Short.MAX_VALUE)
                .addGroup(
                layout.createParallelGroup().addComponent(bdodaj).addComponent(busun).addComponent(bzip)
                )
        );
        layout.setVerticalGroup(
        layout.createParallelGroup().addComponent(scrollek,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(bdodaj).addComponent(busun).addGap(5,40,Short.MAX_VALUE).addComponent(bzip))
        );
        this.getContentPane().setLayout(layout);
        
        this.setDefaultCloseOperation(3);
        this.pack();
    }

    private JButton bdodaj;
    private JButton busun;
    private JButton bzip;

    private JMenuBar PasekMenu = new JMenuBar();
    private JFileChooser wybieracz = new JFileChooser();
    private DefaultListModel modelListy = new DefaultListModel()
    {
          @Override
          public void addElement(Object obj) 
          {
              lista.add(obj);
             super.addElement(((File)obj).getName());
    
          }
          @Override
           public Object get(int index) 
           {
                return lista.get(index);
            }
          @Override
          public Object remove(int index)
          {
              lista.remove(index);
              return super.remove(index);
          }
          ArrayList lista = new ArrayList();
    };
         private JList lista = new JList(modelListy);
    public static void main(String[] args) 
    {
       new FileZipper().setVisible(true);
    }
    
    private class Akcja extends AbstractAction
    {

        public Akcja(String nazwa,String opis,String klawiaturowySkrot)
        {
            this.putValue(Action.NAME,nazwa);
            this.putValue(Action.SHORT_DESCRIPTION,opis);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(klawiaturowySkrot));
        }
        
        public Akcja(String nazwa,String opis,String klawiaturowySkrot,Icon ikona)
        {
            this(nazwa,opis,klawiaturowySkrot);
            this.putValue(Action.SMALL_ICON, ikona);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if(e.getActionCommand().equals("Dodaj"))
            {
                dodajWpisyDoArchiwum();
            }
            else if(e.getActionCommand().equals("Usun"))
            {
                usuwanieWpisowZListy();
            }
            else if(e.getActionCommand().equals("Zip"))
            {
                stworzArchiwumzIP();
            }
        }
        private void dodajWpisyDoArchiwum()
        {
            wybieracz.setCurrentDirectory(new File(System.getProperty("user.dir")));
            wybieracz.setMultiSelectionEnabled(true);
            wybieracz.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            
            int tmp = wybieracz.showDialog(rootPane, "Dodaj do Archiwum");
            if(tmp==JFileChooser.APPROVE_OPTION)
            {
                File[] sciezki = wybieracz.getSelectedFiles();
                for(int i =0 ; i<sciezki.length;i++)
                {
                    if(!(czyWpisSiePowtarza(sciezki[i].getPath())))
                    modelListy.addElement(sciezki[i]);
                }
            }
        }
        private boolean czyWpisSiePowtarza(String testowanyWpis)
        {
           for(int i=0;i<modelListy.getSize();i++)
           {
               if(((File)modelListy.get(i)).getPath().equals(testowanyWpis))
                   return true;
           }
            
            return false;
        }
        private void usuwanieWpisowZListy()
        {
            int[] tmp = lista.getSelectedIndices();
            for(int i=0;i<tmp.length;i++)
            {
                
                modelListy.remove(tmp[i]-i);
            }
        }
        private void stworzArchiwumzIP()
        {
            wybieracz.setCurrentDirectory(new File(System.getProperty("user.dir")));
            wybieracz.setSelectedFile(new File(System.getProperty("user.dir")+File.separator+"mojaNazwa.zip"));
            
            int tmp = wybieracz.showDialog(rootPane, "Kompresuj");
         
                if(tmp==JFileChooser.APPROVE_OPTION)
                {
                    
                    byte[] tmpData = new byte[BUFFOR];
                    try
                    {

                        ZipOutputStream zOuts = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(wybieracz.getSelectedFile())));
                        for(int i=0;i<modelListy.size();i++)
                        {
                            if(!((File)modelListy.get(i)).isDirectory())
                                zipuj(zOuts,(File)modelListy.get(i),tmpData,((File)modelListy.get(i)).getPath());
                            else
                            {
                                wypiszSciezki((File)modelListy.get(i));
                                for(int j =0;j<listaSciezek.size();j++)
                                {
                                   
                                    zipuj(zOuts, (File)listaSciezek.get(j), tmpData,((File)modelListy.get(i)).getPath());
                               listaSciezek.removeAll(listaSciezek);
                                }
                            }
   
                        }
                        zOuts.close();

                    }
                    catch(IOException e)
                    {
                        System.out.println( e.getMessage());;
                    }
    
        }}
        private void zipuj(ZipOutputStream zOuts,File sciezkaPliku,byte[] tmpData,String sciezkaBazowa) throws IOException
        {
                                 BufferedInputStream inS = new BufferedInputStream(new FileInputStream(sciezkaPliku) ,BUFFOR);

                        zOuts.putNextEntry(new ZipEntry(sciezkaPliku.getPath().substring(sciezkaBazowa.lastIndexOf(File.separator)+1)));

                        int counter = 0;
                        while((counter=inS.read(tmpData, 0, BUFFOR))!=-1)
                            zOuts.write(tmpData, 0, counter);


                        zOuts.closeEntry();
        }
        private void wypiszSciezki(File nazwaSciezki)
        {
            String[] nazwaPlikowIKatalogow= nazwaSciezki.list();
            
            for(int i=0; i<nazwaPlikowIKatalogow.length;i++)
            {
                File p = new File(nazwaSciezki.getPath(),nazwaPlikowIKatalogow[i]);
                        
                        if(p.isFile())
                           listaSciezek.add(p);
                            
                            
                        if(p.isDirectory())
                            wypiszSciezki(new File(p.getPath()));
            }
        }
        ArrayList listaSciezek = new ArrayList();
    }
}
