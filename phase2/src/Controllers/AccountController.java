package Controllers;

import UseCases.AccountManager;
import Util.UserType;

/**
 * @author parssa
 */
public class AccountController {

    private AccountManager accountManager;

    public AccountController() {
        accountManager = new AccountManager();
    }

    /**
     * Authenticate a user if their password matches with the password associated with their username.
     *
     * @param username the username of the user(String).
     * @param password the password the user has entered(String).
     * @return the UserType if the password entered matches with the password associated with the username.
     * ELse, return null.
     */
    public UserType authenticateUser(String username, String password) {
        if (accountManager.userExists(username)) {
            if (accountManager.getUser(username).getPassword().equals(password))
                return accountManager.getUserType(username);
        }
        return null;
    }

    /**
     * Create a user with a name, username, password, and user type (Attendee, Organizer, Speaker) if the username
     * hasn't been taken (Checked using the !userExists method.)
     *
     * @param name     the name of the user(String) that is to be created.
     * @param username the username of the user(String) that is to be created.
     * @param password the password of the user(String) that is to be created.
     * @param type     the type of user(User.UserType) that is to be created.
     * @return true if the user is created (when the username hasn't been taken).
     * Else, return false.
     */
    public boolean createUser(String name, String username, String password, String retype, UserType type) {
        System.out.println("--ENTERED");
        if (!accountManager.userExists(username)) {
            if (password.contains(retype)) {
                accountManager.createUser(name, username, password, type);
                System.out.println("1");
                return true;
            }else  {
                System.out.println("2");
            }
        } else {
            System.out.println("3");
        }
        return false;
    }

    /**
     * Changes the type of the user.
     *
     * @param username the username of the user(String).
     * @param type     the type that we want the user's type to change to(User.UserType).
     * @return true and change the user type if the username is valid (There exists a user with that username).
     * Else, return false.
     */
    public boolean changeUserType(String username, UserType type) {
        if (accountManager.userExists(username)) {
            accountManager.changeUserType(username,type);
            return true;
        }
        return false;
    }
}
