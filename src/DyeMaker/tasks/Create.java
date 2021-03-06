package DyeMaker.tasks;

import DyeMaker.DyeTask;
import DyeMaker.Dyes;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.*;

public class Create extends DyeTask {

    public Dyes options;

    public Create(ClientContext ctx, Dyes options) {
        super(ctx, options);
        this.options = options;
    }

    @Override
    public boolean activate() {
        return (ctx.inventory.select().id(options.getingredientItemId()).count() >= options.getAmountRequired() |
                ctx.inventory.select().id(options.getingredientItemId()).count(true) >= options.getAmountRequired() &&
                ctx.inventory.select().id(options.getDyeId()).count() < 26) &&
                ctx.inventory.select().id(goldID).count(true) >= goldRequired;
    }

    @Override
    public void execute() {
        boolean doorClosed = aggieWitchHouseArea.contains(ctx.objects.select().id(closedDoorID).nearest().poll());

        if(!aggieWitchHouseArea.contains(ctx.players.local())) {
            System.out.println("Walking to Aggie's");
            walkToAggie();
        }

        if(doorClosed) {
            System.out.println("Opening Door");
            openDoor();
        }

        if(aggieWitchHouseArea.contains(ctx.players.local()) && !doorClosed) {
            System.out.println("Creating Dyes");
            createDyes();
        }

    }

    //Used to keep track of current task in DyeMakerPaint
    @Override
    public String toString() {
        return "Creating Dyes";
    }

    private void walkToAggie() {
        ctx.movement.newTilePath(pathToBankFromAggie).reverse().traverse();
    }

    @SuppressWarnings("Duplicates")
    private void openDoor() {
        GameObject closedDoor = ctx.objects.select().id(closedDoorID).nearest().poll();
        closedDoor.bounds(closedDoorBounds);

        if(!closedDoor.inViewport()) {
            System.out.println("Turning camera to door.");
            ctx.camera.turnTo(closedDoor);
            ctx.camera.pitch(Random.nextInt(60, 100));
        }

        if(ctx.inventory.selectedItemIndex() != -1) {
            closedDoor.interact("Use", "Door");
        }

        if(ctx.inventory.selectedItemIndex() == -1) {
            System.out.println("Opening door.");
            closedDoor.interact("Open", "Door");
            Condition.wait(() -> !aggieWitchHouseArea.contains(ctx.objects.select().id(closedDoorID).nearest().poll()), 250, 20);
        }


    }

    //TODO: optimize
    private void createDyes() {
        Npc aggieWitch = ctx.npcs.select().id(aggieWitchNPC).nearest().poll();
        Item randomOnion = ctx.inventory.select().id(options.getingredientItemId()).shuffle().poll(); //selects a random onion from inventory

        if (ctx.game.tab() == Game.Tab.INVENTORY) { //checks to see if inventory tab is open
            if (ctx.inventory.selectedItemIndex() != -1) {
                //use onion on aggie and wait for dialogue window
                System.out.println("Using Ingredient");
                if(aggieWitch.inViewport()) {
                    aggieWitch.interact("Use", "Aggie");
                    Condition.wait(ctx.chat::canContinue, 250, 10);
                    ctx.chat.clickContinue(true);
                    Condition.wait(() -> ctx.widgets.widget(createWidget).component(createComponent).visible(), 250,20);
                    if(ctx.widgets.widget(createWidget).component(createComponent).visible()) {
                        System.out.println("Dye Created.");
                        updateDyesCreated();
                    }
                } else {
                    System.out.println("Turning camera to Aggie.");
                    ctx.camera.turnTo(aggieWitch);
                    ctx.camera.pitch(Random.nextInt(60, 100));
                }
            } else {
                randomOnion.interact("Use");
                Condition.wait(() -> ctx.inventory.selectedItemIndex() != -1, 250, 25);
            }
        } else { //open inventory tabs
            ctx.game.tab(Game.Tab.INVENTORY);
        }
    }
}
