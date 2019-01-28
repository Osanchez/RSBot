package DyeMakerPro.tasks;

import DyeMakerPro.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.*;

//TODO: Woad leaves are stackable so the bot needs to recognize that there are enough ingredients to create dyes
//TODO: More work on opening the door to Aggie's


public class Create extends Task {

    public Create(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(ingredientItemID).count() >= amountRequired | ctx.inventory.select().id(ingredientItemID).count(true) >= amountRequired
                && ctx.inventory.select().id(goldID).count(true) >= goldRequired;
    }

    @Override
    public void execute() {
        if(!aggieWitchHouseArea.contains(ctx.players.local())) {
            System.out.println("Walking to Aggie's");
            walkToAggie();
        } else if(aggieWitchHouseArea.contains(ctx.players.local())) {
            boolean doorClosed = aggieWitchHouseArea.contains(ctx.objects.select().id(closedDoorID).nearest().poll());
            if(doorClosed) {
                System.out.println("Opening Door");
                openDoor();
            } else {
                System.out.println("Creating Dyes");
                createDyes();
            }
        }
    }

    //Used to keep track of current task in paint
    @Override
    public String toString() {
        return "Creating Dyes";
    }

    private void walkToAggie() {
        ctx.movement.newTilePath(pathToBankFromAggie).reverse().traverse();
    }

    @SuppressWarnings("Duplicated")
    private void openDoor() {
        final int[] closedDoorBounds = {116, 132, -232, 0, 4, 132}; //was having some issues opening the door so had to set model bounds
        GameObject closedDoor = ctx.objects.select().id(closedDoorID).nearest().poll();
        closedDoor.bounds(closedDoorBounds);

        if(closedDoor.inViewport()) {
            System.out.println("Opening door.");
            if(ctx.inventory.selectedItemIndex() == -1) { //ensures that no item is selected when interacting with door
                closedDoor.interact("Open", "Door");
            } else { //if item is selected unselect the item
                closedDoor.interact("Use", "Door");
            }
        } else {
            System.out.println("Turning camera to door.");
            ctx.camera.turnTo(closedDoor);
        }
    }

    private void createDyes() {
        Npc aggieWitch = ctx.npcs.select().id(aggieWitchNPC).nearest().poll();
        Item randomOnion = ctx.inventory.select().id(ingredientItemID).shuffle().poll(); //selects a random onion from inventory

        if (ctx.game.tab() == Game.Tab.INVENTORY) { //checks to see if inventory tab is open
            if (ctx.inventory.selectedItemIndex() != -1) {
                //use onion on aggie and wait for dialogue window
                System.out.println("Using Ingredient");
                if(aggieWitch.inViewport()) {
                    aggieWitch.interact("Use");
                    Condition.wait(ctx.chat::canContinue, 100, 30);
                    ctx.chat.clickContinue(true);
                } else {
                    System.out.println("Turning camera to Aggie.");
                    ctx.camera.turnTo(aggieWitch);
                    ctx.movement.step(aggieWitch);
                }
            } else {
                randomOnion.interact("Use");
                Condition.wait(() -> ctx.inventory.selectedItemIndex() != -1, 100, 30);
            }
        } else { //open inventory tab
            ctx.game.tab(Game.Tab.INVENTORY);
        }
    }
}
