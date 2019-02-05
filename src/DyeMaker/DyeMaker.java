package DyeMaker;

import DyeMaker.tasks.*;

import DyeMaker.ui.DyeMakerGUI;
import DyeMaker.ui.DyeMakerPaint;
import DyeMaker.util.ProfitTracker;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


@Script.Manifest(
        name= "DyeMaker",
        description="Collects onions and creates all colored dyes",
        properties="author=Alexisblack; topic=1350080; client=4;"
)

public class DyeMaker extends PollingScript<ClientContext>  {

    private List<DyeTask> dyeTaskList = new ArrayList<DyeTask>();
    private DyeTask pickDyeTask;
    private DyeTask createDyeTask;
    private DyeTask bankOnionsDyeTask;
    private DyeTask bankDyeDyeTask;

    private String dyeChoice;
    private DyeMakerPaint dyeMakerPaint;
    private ProfitTracker profitTracker;


    @Override
    public void start() {
        System.out.println("DyeMaker starting.");

        //initialize new GUI
        DyeMakerGUI gui = new DyeMakerGUI();

        String userOptions[] = {"Collect Ingredients", "Create Dye"};
        String dyeOptions[] = {"Red Dye", "Yellow Dye", "Blue Dye"};
        String taskChoice = "" + JOptionPane.showInputDialog(null, "Collect onions or create dyes?", "DyeMaker", JOptionPane.PLAIN_MESSAGE, null, userOptions, userOptions[0]);
        if (taskChoice.equals("Create Dye")) {
            dyeChoice = "" + JOptionPane.showInputDialog(null, "What color dye?", "DyeMaker", JOptionPane.PLAIN_MESSAGE, null, dyeOptions, dyeOptions[0]);
        }

        //initialize the dyeMakerPaint
        this.dyeMakerPaint = new DyeMakerPaint(ctx);

        //initialize profit tracker
        this.profitTracker = new ProfitTracker();

        if (taskChoice.equals("Collect Ingredients")) { //collect ingredient
            this.pickDyeTask = new Pick(ctx);
            this.bankOnionsDyeTask = new BankOnions(ctx);

            dyeTaskList.add(pickDyeTask);
            dyeTaskList.add(bankOnionsDyeTask);
        } else {                                   //create dyes
            this.createDyeTask = new Create(ctx);
            this.bankDyeDyeTask = new BankDye(ctx);

            //set user option variables - don't believe I need to make the changes twice
            this.createDyeTask.setUserOptions(dyeChoice);
            this.bankDyeDyeTask.setUserOptions(dyeChoice);

            //add tasks to list
            dyeTaskList.add(createDyeTask);
            dyeTaskList.add(bankDyeDyeTask);
        }
    }

    @Override
    public void stop() {
        System.out.println("DyeMaker stopping.");
    }

    @Override
    public void poll() {
        if (ctx.players.local().animation() == -1) {
            for (DyeTask dyeTask : dyeTaskList) {
                if (dyeTask.activate()) {
                    dyeMakerPaint.updateTaskName(dyeTask.toString());
                    Condition.sleep(Random.nextInt(100,1200)); //random sleep between tasks
                    dyeTask.execute();
                    break;
                }
            }
        }

        //update values for dyeMakerPaint TODO: profit tracking for collecting ingredients
        profitTracker.addEarnedItem(createDyeTask.dyeID, createDyeTask.dyesCreated);
        dyeMakerPaint.updateProfit(profitTracker.getTotalEarnedCoins(), profitTracker.getTotalEarnedPerHour());
        dyeMakerPaint.updateDyesCreated(createDyeTask.dyesCreated);
    }
}