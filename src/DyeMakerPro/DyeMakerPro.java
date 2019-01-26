package DyeMakerPro;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import DyeMakerPro.tasks.*;

@Script.Manifest(
        name="DyeMakerPro",
        description="Collects onions and creates yellow dye",
        properties="author=Alexisblack; topic=9999; client=4;"
)

public class DyeMakerPro extends PollingScript<ClientContext> implements PaintListener, MessageListener {

    private int numOnionsPicked=0;
    private int numDyesCreated=0;

    public String taskName = "";

    private List<Task> taskList = new ArrayList<Task>();
    private Task pickTask;
    private Task createTask;
    private Task bankTask;

    @Override
    public void start() {
        System.out.println("DyeMakerPro starting.");
        String userOptions[] = {"Collect Onions", "Create Dye"};
        String userChoice = "" + JOptionPane.showInputDialog(null, "Collect onions or Create Yellow Dye?", "DyeMakerPro", JOptionPane.PLAIN_MESSAGE, null, userOptions, userOptions[0]);

        //banking tasks
        this.bankTask = new Bank(ctx);

        //creating dye tasks
        taskList.add(bankTask);

        if (userChoice.equals("Collect Onions")) {
            this.pickTask = new Pick(ctx);
            taskList.add(pickTask);
        } else {
            this.createTask = new Create(ctx);
            taskList.add(createTask);
        }
    }

    @Override
    public void stop() {
        System.out.println("DyeMakerPro stopping.");
    }

    private static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);


    @Override
    public void repaint(Graphics graphics) {
        long milliseconds = getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000*60) % 60);
        long hours = (milliseconds / (1000*60*60)) % 24;

        Graphics2D g = (Graphics2D)graphics;
        g.setFont(TAHOMA);
        g.setColor(new Color(255, 107, 107, 180));
        g.fillRect(200, 0, 170, 120);
        g.setColor(new Color(255, 45, 45));
        g.drawRect(200, 0, 170, 120);
        g.setColor(new Color(255, 255, 255));
        g.drawString("DyeMakerPro", 210, 20);
        g.drawString("Run time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds), 210, 40);
        g.drawString("Task: " + taskName,210, 60);
        g.drawString("Collected/Created: " + numOnionsPicked + "/" + numDyesCreated, 210, 80);
    }

    @Override
    public void poll() {
        if (ctx.players.local().animation() == -1) {
            for (Task task : taskList) {
                if (task.activate()) {
                    Condition.sleep(Random.nextInt(100,1500));
                    taskName = task.toString();
                    task.execute();
                    break;
                }
            }
        }
    }
    @Override
    public void messaged(MessageEvent e) {
        if (e.text().contains("You pick an onion.")) {
            ++numOnionsPicked;
        }
        else if (e.text().contains("filler text.")) { //TODO: get message for dyes created
            ++numDyesCreated;
        }
    }
}