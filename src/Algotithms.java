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


    public Algotithms(int FRAME_SIZE, int PagesNr, int interval, int Processes) {
        this.FRAME_SIZE = FRAME_SIZE;
        this.PagesNr = PagesNr;
        this.Processes = Processes;
        this.interval = interval;
        ProcessesTab = new Proces[Processes];
        for (int a = 0; a < PagesNr; a++) {
            int k = (int) (Math.random() * Processes);
            int r = (int) (Math.random() * interval);
            if ((k == 3 || k == 5) && k % 3 != 0) {
                k = 8;
            }
            PageReferences.add(new Page(r, 0, k));

        }

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

    public int EQUAL() {

        Proces[] ProcessesTabCopy = new Proces[Processes];
        int PF_SUM = 0;
        int frame_size = FRAME_SIZE / (ProcessesTab.length);
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
            int p = LRU(ProcessesTabCopy[k].proces, frame_size);
            PF_SUM += p;

        }

        return PF_SUM;
    }

    public int PROPORTIONAL() {
        int frame_size = FRAME_SIZE / ProcessesTab.length;
        Proces[] ProcessesTabCopy = new Proces[Processes];
        int PF_SUM = 0;
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);
            // ProcessesTabCopy[k].setFrame(frame_size);

        }
        for (int j = 0; j < ProcessesTabCopy.length; j++) {

            frame_size = ProcessesTabCopy[j].proces.size() * FRAME_SIZE / PagesNr;

            if (frame_size == 0) {
                frame_size = 3;
            }
            int p = LRU(ProcessesTabCopy[j].proces, frame_size);
            PF_SUM += p;
        }
        return PF_SUM;
    }

    public int ALG_3() {
        int PFMax =(int)0.6*PagesNr;
        int frame_size = FRAME_SIZE / ProcessesTab.length;
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
            int minI = 0;
            int maxI = 0;
            for (int i = 0; i < ProcessesTabCopy.length; i++) {
                Proces t = ProcessesTabCopy[i];
                if (t != null && t.proces.size() != 0) {
                    if (size == 1) {
                        ProcessesTabCopy[i].setFrame(ProcessesTabCopy[i].FRAME_SIZE + freeFrames);
                        freeFrames = 0;
                    }
                    int pf = t.PPF;
                    int pfsingle = t.LRU(t.proces);
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
                        ProcessesTabCopy[maxI].setFrame(ProcessesTabCopy[maxI].FRAME_SIZE + ProcessesTabCopy[i].FRAME_SIZE);
                    } else {
                        freeFrames += ProcessesTabCopy[i].FRAME_SIZE;
                    }
                    ProcessesTabCopy[i] = null;
                    size--;
                }

            }
            if (ProcessesTabCopy[minI] != null && ProcessesTabCopy[maxI] != null && ProcessesTabCopy[minI].PFrame != 1
                    &&max>PFMax ) {
                if (ProcessesTabCopy[minI].FRAME_SIZE > 3) {
                    ProcessesTabCopy[minI].setFrame(ProcessesTabCopy[minI].FRAME_SIZE - 1);
                    ProcessesTabCopy[maxI].setFrame(ProcessesTabCopy[maxI].FRAME_SIZE + 1 + freeFrames);
                    freeFrames = 0;
                }
            }
        }

        return PFGlobal;
    }
    public int ALG_4(int zone) {
        int PFGlobal = 0;
        int freeFrames = FRAME_SIZE;
        int allDone = -1;
        Proces[] ProcessesTabCopy = new Proces[Processes];
        for (int k = 0; k < ProcessesTab.length; k++) {
            ProcessesTabCopy[k] = new Proces(ProcessesTab[k]);

            int frame_size = numberOfDuplications(ProcessesTabCopy[k].proces, zone);

            ProcessesTabCopy[k].setFrame(frame_size);


        }
       do {
            for (int k = allDone + 1; k < ProcessesTab.length; k++) {
               if (freeFrames > ProcessesTabCopy[k].FRAME_SIZE) {
                    allDone++;
                   int w = ProcessesTabCopy[k].FRAME_SIZE;
                   freeFrames-=w;
                    if(ProcessesTabCopy[k].FRAME_SIZE!= 0){
                        int h = LRU(ProcessesTabCopy[k].proces, ProcessesTabCopy[k].FRAME_SIZE);

                        PFGlobal +=h ;
                    }

                }


            }
           freeFrames = FRAME_SIZE;
        }
            while (allDone != Processes-1);

        return PFGlobal;
    }
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