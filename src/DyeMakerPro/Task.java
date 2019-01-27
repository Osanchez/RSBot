package DyeMakerPro;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

public abstract class Task extends ClientAccessor {

    //animation ids
    protected final static int pickingAnimation = 827;

    //object ids
    protected final static int gateClosedObjectID = 12987;
    protected final static int depositBoxID = 6948; //interact "Deposit"
    protected final static int closedDoorID = 1535;
    protected final static int onionObjectID = 3366;


    //item ids
    protected final static int onionID = 1957;
    protected final static int goldID = 995;
    protected final static int yellowdyeID = 1765;


    //nps ids
    protected final static int aggieWitchNPC = 4284;
    protected final static int banker = 395;

    //bank stairs
    protected final static int[] bankStairs = {16671, 16672, 16673}; //interact "Climb-up"


    //paths
    protected static final Tile[] pathToBankStairs = new Tile[]{
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

    protected static final Tile[] pathToBankFromAggie = new Tile[]{
            new Tile(3089, 3258, 0),
            new Tile(3098, 3253, 0),
            new Tile(3092, 3243, 0)
    };

    //areas
    //Onion Farm
    protected static final Area onionFarmArea = new Area (
            new Tile(3183, 3275, 0),
            new Tile(3192, 3264, 0)
    );

    //Aggies House
    protected static final Area aggieWitchHouseArea = new Area (
            new Tile(3095, 3264, 0),
            new Tile(3083, 3256, 0)
    );

    public Task(ClientContext ctx) {
        super(ctx);
    }

    public abstract boolean activate();
    public abstract void execute();

}