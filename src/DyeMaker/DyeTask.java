package DyeMaker;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

public abstract class DyeTask extends ClientAccessor implements Task {

    //user options
    protected Dyes options;

    //counter
    protected int dyesCreated = 0;

    //magic numbers
    protected final static int goldRequired = 5;
    protected final static int createWidget = 193;
    protected final static int createComponent = 2;

    //animation ids
    protected final static int pickingAnimation = 827;

    //bounds
    protected final static int[] closedDoorBounds = {116, 132, -152, -100, 48, 88}; //was having some issues opening the door so had to set model bounds

    //object ids
    protected final static int gateClosedObjectID = 12987;
    protected final static int depositBoxID = 6948; //interact "Deposit"
    protected final static int closedDoorID = 1535;

    //item ids
    protected final static int goldID = 995;

    //nps ids
    protected final static int aggieWitchNPC = 4284;
    protected final static int banker = 395;

    //bank stairs
    protected final static int[] bankStairs = {16671, 16672, 16673}; //interact "Climb-up"

    //paths
    protected static final Tile[] pathToBankStairs = new Tile[]{
            new Tile(3184, 3268, 0),
            new Tile(3184, 3264, 0),
            new Tile(3187, 3260, 0),
            new Tile(3191, 3255, 0),
            new Tile(3193, 3251, 0),
            new Tile(3194, 3246, 0),
            new Tile(3195, 3241, 0),
            new Tile(3196, 3236, 0),
            new Tile(3197, 3232, 0),
            new Tile(3198, 3228, 0),
            new Tile(3198, 3223, 0),
            new Tile(3198, 3218, 0),
            new Tile(3202, 3217, 0),
            new Tile(3203, 3214, 0),
            new Tile(3206, 3213, 0),
            new Tile(3208, 3212, 0),
            new Tile(3208, 3209, 0),
            new Tile(3206, 3209, 0)
    };

    protected static final Tile[] pathToBankFromAggie = new Tile[]{
            new Tile(3089, 3258, 0),
            new Tile(3098, 3253, 0),
            new Tile(3092, 3243, 0)
    };

    //areas
    protected static final Area onionFarmArea = new Area (
            new Tile(3183, 3275, 0),
            new Tile(3192, 3264, 0)
    );

    protected static final Area aggieWitchHouseArea = new Area (
            new Tile(3095, 3264, 0),
            new Tile(3083, 3256, 0)
    );

    public DyeTask(ClientContext ctx, Dyes options) {
        super(ctx);
        this.options = options;
    }

    protected void updateDyesCreated() {
        dyesCreated++;
    }

}