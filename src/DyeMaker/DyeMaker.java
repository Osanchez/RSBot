package DyeMaker;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import DyeMaker.tasks.*;

@Script.Manifest(
        name= "DyeMaker",
        description="Collects onions and creates yellow dye",
        properties="author=Alexisblack; topic=9999; client=4;"
)

public class DyeMaker extends PollingScript<ClientContext> implements PaintListener, MessageListener {

    private int numOnionsPicked=0;
    private int numDyesCreated=0;

    public String taskName = "";

    private List<Task> taskList = new ArrayList<Task>();
    private Task pickTask;
    private Task createTask;
    private Task bankOnionsTask;
    private Task bankDyeTask;

    private String dyeChoice;

    @Override
    public void start() {
        System.out.println("DyeMaker starting.");
        String userOptions[] = {"Collect Ingredients", "Create Dye"};
        String dyeOptions[] = {"Red Dye", "Yellow Dye", "Blue Dye"};
        String taskChoice = "" + JOptionPane.showInputDialog(null, "Collect onions or create dyes?", "DyeMaker", JOptionPane.PLAIN_MESSAGE, null, userOptions, userOptions[0]);
        if (taskChoice.equals("Create Dye")) {
            dyeChoice = "" + JOptionPane.showInputDialog(null, "What color dye?", "DyeMaker", JOptionPane.PLAIN_MESSAGE, null, dyeOptions, dyeOptions[0]);
        }



        if (taskChoice.equals("Collect Ingredients")) { //collect ingredient
            this.pickTask = new Pick(ctx);
            this.bankOnionsTask = new BankOnions(ctx);

            taskList.add(pickTask);
            taskList.add(bankOnionsTask);
        } else {                                   //create dyes
            this.createTask = new Create(ctx);
            this.bankDyeTask = new BankDye(ctx);

            //set user option variables - don't believe I need to make the changes twice
            this.createTask.setUserOptions(dyeChoice);
            this.bankDyeTask.setUserOptions(dyeChoice);

            //add tasks to list
            taskList.add(createTask);
            taskList.add(bankDyeTask);
        }
    }

    @Override
    public void stop() {
        System.out.println("DyeMaker stopping.");
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
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(200, 0, 170, 120);
        g.setColor(new Color(52, 52, 52));
        g.drawRect(200, 0, 170, 120);
        g.setColor(new Color(255, 255, 255));
        g.drawString("DyeMaker", 210, 20);
        g.drawString("Run time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds), 210, 40);
        g.drawString("Task: " + taskName,210, 60);
        g.drawString("Collected/Created: " + numOnionsPicked + "/" + numDyesCreated, 210, 80);
    }

    @Override
    public void poll() {
        if (ctx.players.local().animation() == -1) {
            for (Task task : taskList) {
                if (task.activate()) {
                    Condition.sleep(Random.nextInt(100,1200)); //random sleep between tasks
                    taskName = task.toString();
                    task.execute();
                    break;
                }
            }
        }
        if(taskName.equals("Creating Dyes")) {
            updateDyesCreated();
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        if (e.text().contains("You pick an onion.")) {
            ++numOnionsPicked;
        }
    }

    private void updateDyesCreated() {
        numDyesCreated = createTask.dyesCreated;
    }
}