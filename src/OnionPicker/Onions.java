package OnionPicker;

import java.util.concurrent.Callable;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;


@Script.Manifest(
        name="PickOnions",
        description = "collect onions for yellow dye",
        properties = "author=Alexisblack; topic=9999; client=4")

public class Onions extends PollingScript<ClientContext> {
    private final static int onionObjectID = 3366;
    private final static int gateClosedObjectID = 12987;
    private final static int pickingAnimation = 827;
    private final static int depositBoxID = 6948; //interact "Deposit"

    //bank stairs
    private final static int[] bankStairs = {16671, 16672, 16673}; //interact "Climb-up"


    private static final Tile[] pathToBankStairs = new Tile[]{
            new Tile(3185, 3268, 0),
            new Tile(3184, 3259, 0),
            new Tile(3188, 3251, 0),
            new Tile(3194, 3241, 0),
            new Tile(3199, 3228, 0),
            new Tile(3199, 3221, 0),
            new Tile(3203, 3214, 0),
            new Tile(3205, 3214, 0),
            new Tile(3205, 3209, 0)
    };


    private static final Area onionFarmArea = new Area (
            new Tile(3183, 3275, 0),
            new Tile(3192, 3264, 0)
    );


    @Override
    public void start() {
        System.out.println("Started");
    }

    @Override
    public  void stop() {
        System.out.println("Stopped");
    }

    @Override
    public void poll() {
        GameObject stairs = ctx.objects.select().id(bankStairs).nearest().poll();
        GameObject closedGate = ctx.objects.select().id(gateClosedObjectID).nearest().poll();

        int inventorySpace = ctx.inventory.select().count();
        int currentFloor = ctx.players.local().tile().floor();
        double distanceToStairs = ctx.players.local().tile().distanceTo(stairs);


        if(inventorySpace < 28) {
            if(!onionFarmArea.contains(ctx.players.local()) && currentFloor != 0) {
                System.out.println("Climbing down stairs to farm");
                climbStairs(false);

            } else if(!onionFarmArea.contains(ctx.players.local()) && currentFloor == 0) {
                System.out.println("Walking to farm");
                walkToFarm();

            } else if(onionFarmArea.contains(ctx.players.local())) {
                if(onionFarmArea.contains(closedGate)) {
                    System.out.println("Opening closed gate");
                    openGate();
                } else {
                    System.out.println("Picking onions");
                    pickOnions();
                }
            }
        }

        else if(inventorySpace == 28) {
            if(onionFarmArea.contains(ctx.players.local()) && onionFarmArea.contains(closedGate)) {
                System.out.println("Opening closed gate");
                openGate();
            }
            else if(distanceToStairs > 3 && currentFloor != 2){
                System.out.println("Walking to bank stairs");
                walkToBankStairs();
            }
            else if(distanceToStairs < 3 && currentFloor != 2) {
                System.out.println("Climbing up stairs to bank");
                climbStairs(true);
            }
            else if(currentFloor == 2) {
                System.out.println("Depositing onions");
                depositOnions();
            }
        }
        else {
            ctx.controller.stop();
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

    //deposit onions into bank account
    private void depositOnions() {
        if(ctx.depositBox.opened()) {
            if(ctx.depositBox.depositInventory()) {
                final int inventoryCount = ctx.inventory.select().count();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return ctx.inventory.select().count() != inventoryCount;
                    }
                }, 500, 20);
            }
            ctx.depositBox.close();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return !ctx.depositBox.opened();
                }
            }, 500, 20);
        } else {
            GameObject depositBox = ctx.objects.select().id(depositBoxID).nearest().poll();
            if(depositBox.inViewport()) {
                depositBox.interact("Deposit");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return ctx.depositBox.opened();
                    }
                }, 250, 20);
            } else {
                ctx.camera.turnTo(depositBox);
            }
        }
    }

    //walk to bank
    private void walkToBankStairs() {
        //walking to stairs
        System.out.println("Walking to bank stairs");
        ctx.movement.newTilePath(pathToBankStairs).traverse();
    }

    private void climbStairs(boolean banking) {
        GameObject stairs = ctx.objects.select().id(bankStairs).nearest().poll();

        if(stairs.inViewport()) { //if stairs are in the area and viewable
            System.out.println("Stairs are in the area and are in the viewport. attempting to climb stairs");
            final int currentFloor = ctx.players.local().tile().floor();
            if(banking) {
                stairs.interact("Climb-up");
            } else {
                stairs.interact("Climb-down");
            }
            Condition.wait(new Callable<Boolean>() {
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

    //walk to farm
    private void walkToFarm() {
        //walking to stairs
        System.out.println("Walking to farm");
        ctx.movement.newTilePath(pathToBankStairs).reverse().traverse();
    }

}
