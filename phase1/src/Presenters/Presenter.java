package Presenters;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Abstract presenter class. WIP
 *
 * @author Alex & Parssa
 */
public class Presenter {
    private Scanner s = new Scanner(System.in);
    //    private List<String> commands;
//    private List<String> descriptions;
    private Map<String, String> commands;

    /**
     * Constructor for presenter.
     *
     * @param commands list of accepted commands map, with command descriptions as values
     */
    public Presenter(Map<String, String> commands) {
        this.commands = commands;
    }

    /**
     * Closes the presenter. Pls run this when you don't need this presenter anymore.
     */
    public void close() {
        s.close();

        // clears screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Gets input from user as a string.
     *
     * @return user input as string
     */
    public String getInput() {
        return s.nextLine();
    }


    /**
     * Prints the list of acceptable CLI commands and their descriptions. Printed when help command is entered.
     */
    public void printCommandList() {

        for (Map.Entry<String, String> command : commands.entrySet()
        ) {
            System.out.printf("%s : %s", command.getKey(), command.getValue());
        }
    }

    /**
     * Getter for commands
     *
     * @return list of commands
     */
    public List<String> getCommands() {
        return new ArrayList<>(commands.keySet());
    }

    /**
     * Prints a given list out for the user
     *
     * @param list the list to print out
     */
    public void printList(List<String> list) {
        list.forEach(System.out::println);
    }

    /**
     * Prints lines on screen.
     * You can pass any sequence of strings, or an array of strings. e.g.:
     *
     * @param lines a sequence of strings OR an array of strings.
     */
    public void printLines(String... lines) {
        if (lines == null) return;

        for (String line : lines) {
            System.out.println(line);
        }
    }
}