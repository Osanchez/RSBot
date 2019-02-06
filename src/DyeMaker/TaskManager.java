package DyeMaker;

import DyeMaker.tasks.*;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final ClientContext ctx;
    private final String action;
    public final Dyes options;
    private String currentTask;

    private List<DyeTask> dyeTaskList = new ArrayList<DyeTask>();
    public DyeTask pickTask;
    public DyeTask createTask;
    public DyeTask bankOnionsTask;
    public DyeTask bankDyeTask;

    public TaskManager(ClientContext ctx, Dyes options, String action) {
        this.ctx = ctx;
        this.options = options;
        this.action = action;
        init();
    }

    public void init() {
        if (action.equals("Collect")) { //collect ingredient
            this.pickTask = new Pick(ctx, this.options);
            this.bankOnionsTask = new BankOnions(ctx, this.options);

            dyeTaskList.add(pickTask);
            dyeTaskList.add(bankOnionsTask);
        } else {                                   //create dyes
            this.createTask = new Create(ctx, this.options);
            this.bankDyeTask = new BankDye(ctx, this.options);

            //add tasks to list
            dyeTaskList.add(createTask);
            dyeTaskList.add(bankDyeTask);
        }
    }

    public void execute() {
        if (ctx.players.local().animation() == -1) {
            for (DyeTask dyeTask : dyeTaskList) {
                if (dyeTask.activate()) {
                    this.currentTask = dyeTask.toString();
                    Condition.sleep(Random.nextInt(100, 1200)); //random sleep between tasks
                    dyeTask.execute();
                    break;
                }
            }
        }
    }

    public String getCurrentTask() {
        return this.currentTask;
    }

}
