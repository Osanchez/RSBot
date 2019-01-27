package DyeMakerPro.tasks;

import DyeMakerPro.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

public class BankDye extends Task {

    public BankDye(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(onionID).count() < 2 | ctx.inventory.select().id(goldID).count(true) < 5;
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
            depositDyes();
            withdrawalOnions();
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
            }
        }
    }

    //Used to keep track of current task in paint
    @Override
    public String toString() {
        return "Banking Dyes";
    }

    @SuppressWarnings("Duplicated")
    private void openDoor() {
        final int[] closedDoorBounds = {-36, 12, -232, 4, 12, 136}; //was having some issues opening the door so had to set model bounds
        GameObject closedDoor = ctx.objects.select().id(closedDoorID).each(Interactive.doSetBounds(closedDoorBounds)).nearest().poll();

        if(closedDoor.inViewport()) {
            System.out.println("Opening door.");
            closedDoor.click();
        } else {
            System.out.println("Turning camera to door.");
            ctx.camera.turnTo(closedDoor);
        }
    }

    private void walkToBank() {
        //walking to Bank
        ctx.movement.newTilePath(pathToBankFromAggie).traverse();
    }

    private void depositDyes() {
        ctx.bank.deposit(yellowdyeID, 28);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return ctx.inventory.select().id(yellowdyeID).count() == 0;
            }
        }, 100, 50);
    }
    private void withdrawalOnions() {
        ctx.bank.withdraw(onionID, 26);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return ctx.inventory.select().id(onionObjectID).count() > 0;
            }
        }, 100, 20);
        ctx.bank.close();
    }

    private boolean hasGold() {
        return ctx.inventory.select().id(goldID).count(true) > 5;
    }
}
