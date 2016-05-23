/**
 * Created by Magdalena Polak on 09.05.2016.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class Algotithms {

    public int FRAME_SIZE;
    public int PagesNr;
    public int Processes;
    public int interval;
    public ArrayList<Page> Proceses;
    public Proces[] ProcessesTab;

    ArrayList<Page> PageReferences = new ArrayList<Page>();
    ArrayList<Page> Frame = new ArrayList<Page>();
    public int PF = 0;

    /**
     * constructor for algorithms
     * @param FRAME_SIZE
     * @param PagesNr
     * @param interval
     * @param Processes
     */
    public Algotithms(int FRAME_SIZE, int PagesNr, int interval, int Processes) {
        this.FRAME_SIZE = FRAME_SIZE;
        this.PagesNr = PagesNr;
        this.Processes = Processes;
        this.interval = interval;
        ProcessesTab = new Proces[Processes];

        //randomizing
        for (int a = 0; a < PagesNr; a++) {
            int k = (int) (Math.random() * Processes);
            int r = (int) (Math.random() * interval);
            if ((k == 3 || k == 5) && k % 3 != 0) {
                k = 8;
            }
            //creating page references
            PageReferences.add(new Page(r, 0, k));

        }
    //creating table of processes
        for (int w = 0; w < Processes; w++) {
            ProcessesTab[w] = new Proces(new ArrayList(), 0);
            for (int s = 0; s < PageReferences.size(); s++) {
                if ((PageReferences.get(s)).proces == w) {
                    Proces a = ProcessesTab[w];
                    a.proces.add(PageReferences.get(s));
                }
            }
        }


    }

    /**
     * Equal algorithm
     * @return global page faults
     */
    public int EQUAL() {
        //copying references
        Proces[] ProcessesTabCopy = new Proces[Processes];
        int PF_SUM = 0;
        //frame size as a whole frames divided by processes length
        int frame_size = FRAME_SIZE / (ProcessesTab.length);
        //adds all page faults
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
            int p = LRU(ProcessesTabCopy[k].proces, frame_size);
            PF_SUM += p;

        }

        return PF_SUM;
    }

    /**
     * Proportional algorithm
     * @return global page faults
     */
    public int PROPORTIONAL() {
        //frame size as a whole frames divided by processes length
        int frame_size = FRAME_SIZE / ProcessesTab.length;
        Proces[] ProcessesTabCopy = new Proces[Processes];
        int PF_SUM = 0;
        //copying page references
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
            // ProcessesTabCopy[k].setFrame(frame_size);

        }
        for (int j = 0; j < ProcessesTabCopy.length; j++) {

            //frame size: depends on proces size
            frame_size = ProcessesTabCopy[j].proces.size() * FRAME_SIZE / PagesNr;
        //minimal frame size
            if (frame_size == 0) {
                frame_size = 3;
            }
            int p = LRU(ProcessesTabCopy[j].proces, frame_size);
            PF_SUM += p;
        }
        return PF_SUM;
    }

    /**
     * steering page faults
     * @return global page faults
     */
    public int ALG_3() {
        //max page faults for starting working algorithm
        int PFMax =(int)0.6*PagesNr;
        //starting page size
        int frame_size = FRAME_SIZE / ProcessesTab.length;
        //copying page rreferences
        Proces[] ProcessesTabCopy = new Proces[Processes];
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
            ProcessesTabCopy[k].setFrame(frame_size);
            //    System.out.println(ProcessesTabCopy[k].proces);
        }
        int freeFrames = 0;
        boolean allDone = false;
        int size = Processes;
        int PFGlobal = 0;
        while (size != 0) {
            int min = interval;
            int max = 0;
            //index of proces which generates minimal page faults
            int minI = 0;
            //index of proces which generates maximal page faults
            int maxI = 0;
            for (int i = 0; i < ProcessesTabCopy.length; i++) {
                Proces t = ProcessesTabCopy[i];
                if (t != null && t.proces.size() != 0) {
                    if (size == 1) {
                        ProcessesTabCopy[i].setFrame(ProcessesTabCopy[i].FRAME_SIZE + freeFrames);
                        freeFrames = 0;
                    }
                    int pf = t.PPF;
                    //single page fault
                    int pfsingle = t.LRU(t.proces);
                    //changing indexes of min and max
                    if (pf > max) {
                        max = pf;
                        maxI = i;
                    }
                    if (pf < min) {
                        min = pf;
                        minI = i;
                    }
                    t.proces.remove(0);
                    PFGlobal += pfsingle;
                } else if (t != null) {

                    if (ProcessesTabCopy[maxI] != null && maxI != i) {
                        //if process is done
                        ProcessesTabCopy[maxI].setFrame(ProcessesTabCopy[maxI].FRAME_SIZE + ProcessesTabCopy[i].FRAME_SIZE);
                    } else {
                        freeFrames += ProcessesTabCopy[i].FRAME_SIZE;
                    }
                    ProcessesTabCopy[i] = null;
                    size--;
                }

            }
            //if page faults reach edge
            if (ProcessesTabCopy[minI] != null && ProcessesTabCopy[maxI] != null && ProcessesTabCopy[minI].PFrame != 1
                    &&max>PFMax ) {
                if (ProcessesTabCopy[minI].FRAME_SIZE > 3) {
                    //substracting frame from proces which generates too few page faults
                    ProcessesTabCopy[minI].setFrame(ProcessesTabCopy[minI].FRAME_SIZE - 1);
                    //adding frame for proces which generates too many page faults
                    ProcessesTabCopy[maxI].setFrame(ProcessesTabCopy[maxI].FRAME_SIZE + 1 + freeFrames);
                    freeFrames = 0;
                }
            }
        }

        return PFGlobal;
    }

    /**
     * zone model algotithm
     * @param zone
     * @return global page faults
     */
    public int ALG_4(int zone) {
        int PFGlobal = 0;
        int freeFrames = FRAME_SIZE;
        int allDone = -1;
        //copying page references
        Proces[] ProcessesTabCopy = new Proces[Processes];
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);

            //frame size as a number of duplications in zone
            int frame_size = numberOfDuplications(ProcessesTabCopy[k].proces, zone);
            //setting frame size to proces
            ProcessesTabCopy[k].setFrame(frame_size);


        }
       do {
            for (int k = allDone + 1; k < ProcessesTab.length; k++) {
                //if there are free frames
               if (freeFrames > ProcessesTabCopy[k].FRAME_SIZE) {
                    allDone++;
                   int w = ProcessesTabCopy[k].FRAME_SIZE;
                   //substracting allocated frames
                   freeFrames-=w;
                    if(ProcessesTabCopy[k].FRAME_SIZE!= 0){
                        int h = LRU(ProcessesTabCopy[k].proces, ProcessesTabCopy[k].FRAME_SIZE);

                        PFGlobal +=h ;
                    }

                }


            }
           freeFrames = FRAME_SIZE;
        }
       //waiting processes
            while (allDone != Processes-1);

        return PFGlobal;
    }

    /**
     *
     * @param PagesRef references
     * @param FRAME_SIZE
     * @return page faults
     */
    public int LRU(ArrayList<Page> PagesRef, int FRAME_SIZE) {
        PF = 0;
        ArrayList<Page> Pages2 = new ArrayList<>();
        for (Page p : PagesRef) {
            Pages2.add(new Page(p));
        }
        if (Pages2.size() == 0) {
            return 0;
        }
        Page n;
        for (int i = 0; i < Pages2.size(); i++) {
            n = Pages2.get(i);
            mainloop:
            if (Frame.size() < FRAME_SIZE) {
                for (Page p : Frame) {
                    if (p.nr == n.nr) {
                        p.setRef(p.ref + 1);
                        break mainloop;
                    }
                }
                PF++;
                Frame.add(n);
            } else {
                for (Page p : Frame) {
                    if (p.nr == n.nr) {
                        p.setRef(p.ref + 1);
                        break mainloop;
                    }
                }

                Collections.sort(Frame, Page.refComparator);

                Frame.remove(0);
                Frame.add(n);
                PF++;

            }
        }
        Frame.clear();
        return PF;
    }

    /**
     *
     * @param a arraylist of references
     * @param zone
     * @return number of duplications
     */
   public int numberOfDuplications(ArrayList<Page> a, int zone)   {
       HashSet h = new HashSet();
       if(zone>a.size())
       {
           zone = a.size();
       }
       for(int i =0; i<zone; i++)
       {
           h.add(a.get(i).nr);
       }
       return h.size();
   }

}



/*

 public int EQUAL() {
        int undone = -1;
        Proces[] ProcessesTabCopy = new Proces[Processes];
        int PF_SUM = 0;
        int frame_size;
        do {
            frame_size = FRAME_SIZE / (ProcessesTab.length - undone);
            undone++;
        }
        while(frame_size<=1);

        for (int k = 0; k < ProcessesTab.length-undone; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
            int p = LRU(ProcessesTabCopy[k].proces, frame_size);
            PF_SUM += p;

        }
        if(undone!=0) {
            frame_size = FRAME_SIZE / undone;
            for (int k = 0; k < ProcessesTab.length - undone; k++) {
                ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
                int p = LRU(ProcessesTabCopy[k].proces, frame_size);
                PF_SUM += p;

            }

        }
        return PF_SUM;
    }
 */
/*
pr odw
        ramki
strony

bardzo mało i bardzo duzo
1000 500 2000 10 odwołan
parametr( ramka strona albo strona ramka) tyle przebiegów ile drzew, ile wartosci ile jest w lisciu, punkty pomiarowe
strona : scolar
1000 odwołan, 250 stron globalnie, 10 ramek
*/