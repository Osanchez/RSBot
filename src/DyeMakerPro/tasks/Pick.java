package DyeMakerPro.tasks;

import DyeMakerPro.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class Pick extends Task {

    public Pick(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return (ctx.inventory.select().count() < 28);
    }

    @Override
    public void execute() {
        int currentFloor = ctx.players.local().tile().floor();

        if(!onionFarmArea.contains(ctx.players.local()) && currentFloor != 0) {
            System.out.println("Climbing down stairs to farm");
            climbDownStairs();
        } else if(!onionFarmArea.contains(ctx.players.local()) && currentFloor == 0) {
            System.out.println("Walking to farm");
            walkToFarm();
        } else if(onionFarmArea.contains(ctx.players.local())) {
            boolean closedGate = onionFarmArea.contains(ctx.objects.select().id(gateClosedObjectID).nearest().poll());
            if(closedGate) {
                System.out.println("Opening closed gate");
                openGate();
            } else {
                System.out.println("Picking onions");
                pickOnions();
            }
        }
    }

    //Used to keep track of current task in paint
    @Override
    public String toString() {
        return "Picking Onions";
    }

    //climb stairs to the bank
    private void climbDownStairs() {
        GameObject stairs = ctx.objects.select().id(bankStairs).nearest().poll();

        if(stairs.inViewport()) { //if stairs are in the area and viewable
            System.out.println("Stairs are in the area and are in the viewport. attempting to climb stairs");
            final int currentFloor = ctx.players.local().tile().floor();
            stairs.interact("Climb-down");
            Condition.wait(new Callable<Boolean>() { //sleep until the location floor changes
                @Override
                public Boolean call() {
                    int newFloor = ctx.players.local().tile().floor();
                    return newFloor != currentFloor;
                }
            }, 1000, 5);
        } else { //if stairs are in the area and not viewable
            System.out.println("stairs not in viewport. Turning Camera.");
            ctx.camera.turnTo(stairs);
        }
    }

    //open gate to farm if onions are not reachable
    private void openGate(){
        GameObject closedGate = ctx.objects.select().id(gateClosedObjectID).within(onionFarmArea).poll();
        if(closedGate.inViewport()) {
            closedGate.interact("Open");
        } else {
            System.out.println("Turning camera to closed gate");
            ctx.camera.turnTo(closedGate);
        }
    }

    //walk to farm
    private void walkToFarm() {
        //walking to stairs
        System.out.println("Walking to farm");
        ctx.movement.newTilePath(pathToBankStairs).reverse().traverse();
    }

    //Pick onions from farm
    private void pickOnions() {
        GameObject onionToPick = ctx.objects.select().id(onionObjectID).nearest().poll();
        if(onionToPick.inViewport()) {
            onionToPick.interact("Pick");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    final int currentAnimation = ctx.players.local().animation();
                    return currentAnimation != pickingAnimation;
                }
            }, 600, 10);
        } else {
            System.out.println("Turning camera to onion");
            ctx.camera.turnTo(onionToPick);
        }
    }
}
