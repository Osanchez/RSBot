package DyeMaker;

import DyeMaker.ui.DyeMakerGUI;
import DyeMaker.ui.DyeMakerPaint;
import DyeMaker.util.ProfitTracker;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

@Script.Manifest(
        name= "DyeMaker",
        description="Collects onions and creates all colored dyes",
        properties="author=Alexisblack; topic=1350080; client=4;"
)

public class DyeMaker extends PollingScript<ClientContext>  {



    private DyeMakerPaint dyeMakerPaint;
    private ProfitTracker profitTracker;
    private TaskManager taskManager;


    @Override
    public void start() {
        System.out.println("DyeMaker starting.");

        //initialize new GUI
        this.taskManager = new DyeMakerGUI().fetchManager(ctx);

        //initialize the dyeMakerPaint
        this.dyeMakerPaint = new DyeMakerPaint(ctx);

        //initialize profit tracker
        this.profitTracker = new ProfitTracker();
    }

    @Override
    public void stop() {
        System.out.println("DyeMaker stopping.");
    }

    @Override
    public void poll() {
        //execute respective tasks
        taskManager.execute();

        //----- update paint -----

        //update current task
        dyeMakerPaint.updateTaskName(taskManager.getCurrentTask());

        //update values for dyeMakerPaint
        profitTracker.addEarnedItem(taskManager.options.getDyeId(), taskManager.createTask.dyesCreated);
        dyeMakerPaint.updateProfit(profitTracker.getTotalEarnedCoins(), profitTracker.getTotalEarnedPerHour());
        dyeMakerPaint.updateDyesCreated(taskManager.createTask.dyesCreated);
    }
}