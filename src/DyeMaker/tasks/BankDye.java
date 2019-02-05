package DyeMaker.tasks;

import DyeMaker.DyeTask;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;

public class BankDye extends DyeTask {

    public BankDye(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return (ctx.inventory.select().id(ingredientItemID).count() < amountRequired | ctx.inventory.select().id(goldID).count(true) < goldRequired |
                ctx.inventory.select().id(dyeID).count() >= 26);
    }

    @Override
    public void execute() {
        Npc draynorBanker = ctx.npcs.select().id(banker).nearest().poll();
        double distanceToBanker = ctx.players.local().tile().distanceTo(draynorBanker);
        boolean doorClosed = aggieWitchHouseArea.contains(ctx.objects.select().id(closedDoorID).nearest().poll());
        boolean playerAtAggies = aggieWitchHouseArea.contains(ctx.players.local());

        if(!hasGold()) {
            System.out.println("There is no gold in inventory. Stopping script.");
            ctx.controller.stop(); //stop script if user has no gold
        }

        if(ctx.bank.opened()) {
            if(!hasIngredients()) {
                System.out.println("Out of ingredients. Stopping script.");
                ctx.controller.stop(); //stop script if user has no gold
            }
            depositDyes();
            withdrawalIngredient();
        } else {
            if(ctx.bank.inViewport()) {
                ctx.bank.open();
            } else if (distanceToBanker > 5) {
                if(doorClosed && playerAtAggies) {
                    openDoor();
                }
                walkToBank();
            } else {
                ctx.camera.turnTo(draynorBanker);
                ctx.camera.pitch(Random.nextInt(60, 100));
            }
        }
    }

    //Used to keep track of current task in DyeMakerPaint
    @Override
    public String toString() {
        return "Banking Dyes";
    }

    @SuppressWarnings("Duplicates")
    private void openDoor() {
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
            ctx.camera.pitch(Random.nextInt(60, 100));
        }
    }

    private void walkToBank() {
        //walking to Bank
        ctx.movement.newTilePath(pathToBankFromAggie).traverse();
    }

    private void depositDyes() {
        ctx.bank.deposit(dyeID, Bank.Amount.ALL);
        Condition.wait(() -> ctx.inventory.select().id(dyeID).count() == 0, 100, 50);
    }
    private void withdrawalIngredient() {
        ctx.bank.withdraw(ingredientItemID, 28);
        Condition.wait(() -> ctx.inventory.select().id(ingredientItemID).count() > 0, 100, 20);
        ctx.bank.close();
    }

    private boolean hasGold() {
        return ctx.inventory.select().id(goldID).count(true) >= goldRequired;
    }

    private boolean hasIngredients() {
        return (ctx.bank.select().id(ingredientItemID).count() >= amountRequired |
                ctx.bank.select().id(ingredientItemID).count(true) >= amountRequired) |
                (ctx.inventory.select().id(ingredientItemID).count() >= amountRequired |
                ctx.inventory.select().id(ingredientItemID).count(true) >= amountRequired);
    }
}
