package DyeMakerPro.tasks;

import DyeMakerPro.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

public class BankOnions extends Task {


    public BankOnions(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return (ctx.inventory.select().count() == 28);
    }

    @Override
    public void execute() {
        boolean closedGate = onionFarmArea.contains(ctx.objects.select().id(gateClosedObjectID).nearest().poll());
        boolean playerInFarm = onionFarmArea.contains(ctx.players.local());

        //if player is in farm area and the gate is closed, open the gate
        if(playerInFarm && closedGate) {
            System.out.println("Opening gate");
            openGate();
        } else {
            //if player is outside of farm area walk to bank
            GameObject stairs = ctx.objects.select().id(bankStairs).nearest().poll();
            int currentFloor = ctx.players.local().tile().floor();
            double distanceToStairs = ctx.players.local().tile().distanceTo(stairs);

            if(distanceToStairs > 3 && currentFloor != 2){
                System.out.println("Walking to bank");
                walkToBankStairs();
            }
            else if(distanceToStairs < 3 && currentFloor != 2) {
                System.out.println("Climbing stairs to bank");
                climbUpStairs();
            } else {
                //bank
                System.out.println("Banking Onions");
                depositOnions();
            }
        }
    }

    //Used to keep track of current task in paint
    @Override
    public String toString() {
        return "Banking Onions";
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

    //walk to bank
    private void walkToBankStairs() {
        //walking to stairs
        ctx.movement.newTilePath(pathToBankStairs).traverse();
    }

    //climb stairs to the bank
    private void climbUpStairs() {
        GameObject stairs = ctx.objects.select().id(bankStairs).nearest().poll();

        if(stairs.inViewport()) { //if stairs are in the area and viewable
            System.out.println("Stairs are in the area and are in the viewport. attempting to climb stairs");
            final int currentFloor = ctx.players.local().tile().floor();
            stairs.interact("Climb-up");
            //sleep until the location floor changes
            Condition.wait(() -> {
                int newFloor = ctx.players.local().tile().floor();
                return newFloor != currentFloor;
            }, 1000, 5);
        } else { //if stairs are in the area and not viewable
            System.out.println("stairs not in viewport. Turning Camera.");
            ctx.camera.turnTo(stairs);
        }
    }

    //deposit onions into bank account
    private void depositOnions() {
        if(ctx.depositBox.opened()) {
            if(ctx.depositBox.depositInventory()) {
                final int inventoryCount = ctx.inventory.select().count();
                Condition.wait(() -> ctx.inventory.select().count() != inventoryCount, 500, 20);
            }
            ctx.depositBox.close();
            Condition.wait(() -> !ctx.depositBox.opened(), 500, 20);
        } else {
            GameObject depositBox = ctx.objects.select().id(depositBoxID).nearest().poll();
            if(depositBox.inViewport()) {
                depositBox.interact("Deposit");
                Condition.wait(() -> ctx.depositBox.opened(), 250, 20);
            } else {
                ctx.camera.turnTo(depositBox);
                ctx.movement.step(depositBox);
            }
        }
    }
}
