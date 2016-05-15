import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Magdalena Polak on 09.05.2016.
 */
public class Proces {

    ArrayList proces;
    int PFrame;
    int PPF = 0;
    int FRAME_SIZE;
    ArrayList<Page> Frame = new ArrayList<Page>();
    public Proces(ArrayList proces, int PFrame)
    {
        this.PFrame = PFrame;
        this.proces = proces;
        this.PPF = PPF;

    }
    public Proces(Proces p)
    {
        this.proces = p.proces;
        this.PFrame = p.PFrame;
    }
    public void setFrame(int frame) {
        FRAME_SIZE = frame;
    }

    public void setPPF(int PPF) {
        this.PPF = PPF;
    }

    public void setProces(ArrayList proces) {
        this.proces = proces;
    }

    public int PF = 0;
    public int LRU(ArrayList<Page> PageReferences ) {
        int PF = 0;
         ArrayList<Page> Pages2 = new ArrayList<>();
        for (Page p : PageReferences) {
            Pages2.add(new Page(p));
        }

        Page n;

            n = Pages2.get(0);
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
      //  System.out.println("bang" +Frame+"pf: " + PF);
        setPPF(PPF+PF);
        return  PF;
    }
}
