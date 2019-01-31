package DyeMaker;

import org.powerbot.script.ClientContext;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;

import java.awt.*;

public class Paint implements PaintListener, MessageListener {

    private ClientContext ctx;
    private int numOnionsPicked = 0;
    private int numDyesCreated = 0;
    private String taskName = "";

    private static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public Paint(ClientContext ctx) {
        this.ctx = ctx;
        this.ctx.dispatcher.add(this);
        System.out.println("Paint Created.");
    }

    @Override
    public void messaged(MessageEvent e) {
        if (e.text().contains("You pick an onion.")) {
            ++numOnionsPicked;
        }
    }


    @Override
    public void repaint(Graphics graphics) {
        long milliseconds = ctx.controller.script().getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000*60) % 60);
        long hours = (milliseconds / (1000*60*60)) % 24;

        Graphics2D g = (Graphics2D)graphics;
        g.setFont(TAHOMA);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(200, 0, 170, 120);
        g.setColor(new Color(52, 52, 52));
        g.drawRect(200, 0, 170, 120);
        g.setColor(new Color(255, 255, 255));
        g.drawString("DyeMaker", 210, 20);
        g.drawString("Run time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds), 210, 50);
        g.drawString("DyeTask: " + taskName,210, 70);
        g.drawString("Collected/Created: " + numOnionsPicked + "/" + numDyesCreated, 210, 90);
        g.drawString("Profit:", 210, 110);
    }


    //setters
    public void updateDyesCreated(Integer dyesCreated) {
        this.numDyesCreated = dyesCreated;
    }

    public void updateTaskName(String name) {
        this.taskName = name;
    }

}
