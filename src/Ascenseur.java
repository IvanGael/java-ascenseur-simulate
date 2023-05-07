import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Ascenseur extends JFrame implements ActionListener {

    private JButton[] buttons;
    private int currentFloor;
    private int destinationFloor;
    private boolean goingUp;
    private JProgressBar elevatorBar;

    private JLabel elevatorStatusLabel; // ajout d'un JLabel pour afficher le statut de l'ascenseur

    private int userWeight = 70; // poids d'un utilisateur par défaut
    private Person person = null;

    private int currentWeight; // ajout de la variable currentWeight
    private final int MAX_WEIGHT = 500; // ajout de la constante MAX_WEIGHT

    public Ascenseur() {
        currentFloor = 0;
        destinationFloor = 0;
        goingUp = true;
        currentWeight = 0; // initialisation de la variable currentWeight à 0
        person = new Person(userWeight);

        // Définition des couleurs
        Color navy = new Color(0, 0, 128);
        Color orange = new Color(255, 140, 0);

        JPanel buttonPanel = new JPanel(new GridLayout(10, 1));
        buttons = new JButton[10];
        for (int i = 9; i >= 0; i--) { // ordre inverse
            buttons[i] = new JButton("Etage " + (i+1));
            buttons[i].addActionListener(this);
            buttons[i].setBackground(navy);
            buttons[i].setForeground(Color.white);
            buttonPanel.add(buttons[i]);
        }

        add(buttonPanel, BorderLayout.WEST);

        JPanel elevatorPanel = new JPanel(new BorderLayout());
        JLabel elevatorLabel = new JLabel("Ascenseur");
        elevatorLabel.setHorizontalAlignment(JLabel.CENTER);
        elevatorPanel.add(elevatorLabel, BorderLayout.NORTH);

        elevatorBar = new JProgressBar(JProgressBar.VERTICAL, 0, 9);
        elevatorBar.setStringPainted(true);
        //elevatorBar.setBackground(Color.GREEN);
        elevatorPanel.add(elevatorBar, BorderLayout.CENTER);

        elevatorStatusLabel = new JLabel("L'ascenseur est vide."); // initialisation du JLabel pour afficher le statut de l'ascenseur
        elevatorStatusLabel.setHorizontalAlignment(JLabel.CENTER);
        elevatorPanel.add(elevatorStatusLabel, BorderLayout.SOUTH);
        elevatorPanel.setBackground(Color.GREEN);

        add(elevatorPanel, BorderLayout.CENTER);

        setSize(800, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String floorString = button.getText().substring(6);
        int floor = Integer.parseInt(floorString) - 1;
        if (floor == currentFloor) {
            return;
        }

        // ajout du calcul du poids de l'utilisateur
        currentWeight += userWeight;
        if (currentWeight > MAX_WEIGHT) {
            currentWeight -= userWeight; // annule l'ajout du poids de l'utilisateur
            JOptionPane.showMessageDialog(null, "Le poids maximum a été atteint. L'ascenseur ne peut pas bouger.");
            return;
        }

        if (floor > currentFloor) {
            goingUp = true;
        } else {
            goingUp = false;
        }
        destinationFloor = floor;
        buttons[floor].setEnabled(false);


        //appel de la méthode enterElevator
        if(person != null)
            person.enterElevator(this);

        // mise à jour de l'affichage de la barre de progression et du statut de l'ascenseur
        elevatorBar.setValue(currentFloor);
        elevatorStatusLabel.setText("L'ascenseur est en mouvement vers l'étage " + (currentFloor + 1) + ".");
    }

    public void runElevator() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Update the progress bar and status label
            elevatorBar.setValue(currentFloor);
            elevatorStatusLabel.setText("L'ascenseur est en mouvement vers l'étage " + (currentFloor + 1) + ".");

            if (currentFloor == destinationFloor) {
                // Let the person exit the elevator
                if(person != null)
                    person.exitElevator(this);

                // Reset the current weight
                currentWeight -= userWeight;

                // Update the status label
                elevatorStatusLabel.setText("L'ascenseur est à l'étage " + (currentFloor + 1) + ".");

                buttons[currentFloor].setEnabled(true);
                //person.left(); // appel de la méthode personLeft() lorsque l'on arrive à l'étage de destination
                continue;
            }

            if (goingUp) {
                currentFloor++;
            } else {
                currentFloor--;
            }

            elevatorBar.setValue(currentFloor);
            elevatorBar.setString("Etage " + (currentFloor+1));
        }
    }

    public static void main(String[] args) {
        Ascenseur ascenseur = new Ascenseur();
        ascenseur.runElevator();
    }

    class Person {
        private int weight;

        public Person(int weight) {
            this.weight = weight;
        }

        public void enterElevator(Ascenseur elevator) {
            JOptionPane.showMessageDialog(null, "Une personne est entrée dans l'ascenseur");
            elevator.currentWeight -= weight;
        }

        public void exitElevator(Ascenseur elevator) {
            elevator.currentWeight -= weight; // retirer le poids de la personne actuelle
            elevator.elevatorBar.setValue(elevator.currentFloor); // met à jour la position de la barre de progression
            elevator.elevatorStatusLabel.setText("La personne est sortie à l'étage " + (elevator.currentFloor + 1) + "."); // met à jour le statut de l'ascenseur
            elevator.buttons[elevator.currentFloor].setEnabled(true); // réactive le bouton de l'étage courant
            elevator.person = null; // vide la variable person
        }

        public void left(){
            //System.out.println("Personne sortie de l'ascenseur à l'étage " + (currentFloor+1));
            currentWeight -= this.weight; // retirer le poids
        }
    }

}
