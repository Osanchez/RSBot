package DyeMaker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

public class DyeMakerGUI extends JFrame {

    JComboBox<String> dyeChoiceList = new JComboBox<>();
    JComboBox<String> ingredientChoiceList = new JComboBox<>();
    JRadioButton choice1;
    JRadioButton choice2;
    JButton btn;


    public DyeMakerGUI() {
        init();
        this.setVisible(true);
    }

    private void init() {
        //components

        //frame
        this.setTitle("DyeMaker v1.1");
        this.setSize(350, 230);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //main panel
        JPanel main = new JPanel();
        main.setBounds(0, 0, 345, 200);
        main.setOpaque(false);

        //create and set layout
        BoxLayout boxLayout = new BoxLayout(main, BoxLayout.Y_AXIS);
        main.setLayout(boxLayout);
        main.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedSoftBevelBorder(), "DyeMaker"));

        //container for everything
        Container contain = getContentPane();
        contain.add(main);

        //radio buttons for actions
        {
            JPanel p = new JPanel(new GridLayout(0,2));
            p.setBorder(BorderFactory.createTitledBorder("Select Action"));
            ButtonGroup buttonGroup = new ButtonGroup();
            choice1 = new JRadioButton("Collect Ingredients", false);
            choice2 = new JRadioButton("Create Dyes", false);
            {
                buttonGroup.add(choice1);
                buttonGroup.add(choice2);
            }
            p.add(choice1);
            p.add(choice2);

            //action listeners

            //choice 1
            choice1.addActionListener(e -> {
                if(choice1.isSelected()) {
                    ingredientChoiceList.setEnabled(true);
                    dyeChoiceList.setEnabled(false);
                    btn.setEnabled(true);
                }
            });

            //choice 2
            choice2.addActionListener(e -> {
                if(choice2.isSelected()) {
                    ingredientChoiceList.setEnabled(false);
                    dyeChoiceList.setEnabled(true);
                    btn.setEnabled(true);
                }
            });
            main.add(p);
        }

        {
            //ingredient choices
            JPanel p = new JPanel(new GridLayout(0,1));
            p.setBorder(BorderFactory.createTitledBorder("Select Ingredient"));
            String[] ingredientChoice = {"Onions"};
            ingredientChoiceList.setModel(new DefaultComboBoxModel<>(ingredientChoice));
            ingredientChoiceList.setSelectedIndex(0);
            p.add(ingredientChoiceList);

            //dye choices
            JPanel p2 = new JPanel(new GridLayout(0,1));
            p2.setBorder(BorderFactory.createTitledBorder("Select Dye"));
            String[] dyeChoice = {"Red Dye", "Blue Dye", "Yellow Dye"};
            dyeChoiceList.setModel(new DefaultComboBoxModel<>(dyeChoice));
            dyeChoiceList.setSelectedIndex(0);
            p2.add(dyeChoiceList);


            //add panels to main
            JPanel combination = new JPanel(new GridLayout(0, 2));
            combination.add(p);
            combination.add(p2);

            main.add(combination);

        }

        //start button
        {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btn = new JButton("Start");
            btn.addActionListener(e -> {
                System.out.println("Action: " + getActionChoice());
                System.out.println("Ingredient: " + getItemChoice());
                System.out.println();
            });
            btn.setEnabled(false);
            p.add(btn);
            main.add(p);
        }

    }

    public String getActionChoice() {
        if (choice1.isSelected()) {
            return "Collect";
        } else {
            return "Create";
        }
    }

    public String getItemChoice() {
        if(ingredientChoiceList.isEnabled()) {
          return ingredientChoiceList.getSelectedItem().toString();
        }
        if(dyeChoiceList.isEnabled()) {
            return dyeChoiceList.getSelectedItem().toString();
        }
        return "";
    }

    public enum Dye {
        RED("Red Dye", 1763, 1951, 1234, 3),
        BLUE("Blue Dye", 1767, 1793, 5678, 2),
        YELLOW("Yellow Dye", 1765, 1957, 3366, 2);

        private final String name;
        private final int amount;
        private final int dyeId;
        private final int ingredientItemId;
        private final int ingredientObjectId;

        Dye(String name, int dyeId, int ingredientItemId, int ingredientObjectId, int amt) {
            this.name = name;
            this.dyeId = dyeId;
            this.ingredientItemId = ingredientItemId;
            this.ingredientObjectId = ingredientObjectId;
            this.amount = amt;
        }

        //returns array of values for selected enum
        public static Dye forName(String name) {
            return Arrays.stream(Dye.values()).filter(f -> f.name.equals(name)).findFirst().orElse(null);
        }

        //getters
        public String getName() {
            return name;
        }

    }

    public static void main(String[] args) {
        DyeMakerGUI testGUI = new DyeMakerGUI();
    }

}