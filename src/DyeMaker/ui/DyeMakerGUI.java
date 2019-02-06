package DyeMaker.ui;

import DyeMaker.TaskManager;
import DyeMaker.Dyes;
import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.awt.*;

public class DyeMakerGUI extends JFrame {

    JComboBox<String> dyeChoiceList = new JComboBox<>();
    JComboBox<String> ingredientChoiceList = new JComboBox<>();
    JRadioButton choice1;
    JRadioButton choice2;
    JButton btn;

    //initialized options
    Dyes userOptions = null;


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
            JPanel p = new JPanel(new GridLayout(0, 2));
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
                if (choice1.isSelected()) {
                    ingredientChoiceList.setEnabled(true);
                    dyeChoiceList.setEnabled(false);
                    btn.setEnabled(true);
                }
            });

            //choice 2
            choice2.addActionListener(e -> {
                if (choice2.isSelected()) {
                    ingredientChoiceList.setEnabled(false);
                    dyeChoiceList.setEnabled(true);
                    btn.setEnabled(true);
                }
            });
            main.add(p);
        }

        {
            //ingredient choices
            JPanel p = new JPanel(new GridLayout(0, 1));
            p.setBorder(BorderFactory.createTitledBorder("Select Ingredient"));
            String[] ingredientChoice = {"Onions"};
            ingredientChoiceList.setModel(new DefaultComboBoxModel<>(ingredientChoice));
            ingredientChoiceList.setSelectedIndex(0);
            p.add(ingredientChoiceList);

            //dye choices
            JPanel p2 = new JPanel(new GridLayout(0, 1));
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
        //TODO: assign variables in gui
        {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btn = new JButton("Start");
            btn.addActionListener(e -> this.setVisible(false));
            btn.setEnabled(false);
            p.add(btn);
            main.add(p);
        }

    }

    private String getActionChoice() {
        if (choice1.isSelected()) {
            return "Collect";
        } else {
            return "Create";
        }
    }

    private void getOptions() {
        if (getActionChoice().equals("Collect")) {
            userOptions = Dyes.ONIONS;
        } else if(getActionChoice().equals("Create")) {
            if(dyeChoiceList.getSelectedItem().equals("Red Dye")) {
                userOptions = Dyes.RED;
            }else if(dyeChoiceList.getSelectedItem().equals("Blue Dye")) {
                userOptions = Dyes.BLUE;
            } else if(dyeChoiceList.getSelectedItem().equals("Yellow Dye")) {
                userOptions = Dyes.YELLOW;
            }
        }
    }

    public TaskManager fetchManager(ClientContext ctx) {
        while(this.isVisible()) {
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        //collects options
        getOptions();

        if(userOptions == null){
            System.out.println("Options were not selected");
            ctx.controller.stop();
        }

        return new TaskManager(ctx, userOptions, getActionChoice());
    }

}