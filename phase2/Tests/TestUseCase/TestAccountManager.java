package TestUseCase;

import Controllers.AccountController;
import UseCases.AccountManager;
import Util.UserType;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAccountManager {

    //TODO Test Cases for the AccountManager Use Case Class

    @Test
    public void test_createUser() {
        AccountController accountController = new AccountController();
        AccountManager accountManager = new AccountManager();
        accountController.createUser("Jason", "Jason_baba", "Jason123", "Jason123",UserType.ATTENDEE);
        assertEquals(false, accountManager.getUser("jason_baba") == null);
        assertEquals(true, accountManager.getUser("jason_2") == null);
    }

    @Test
    public void test_changeUserType() {
        AccountManager accountManager = new AccountManager();
        AccountController accountController = new AccountController();
        accountController.createUser("Jason", "Jason_baba", "Jason123","Jason123",  UserType.ATTENDEE);
        accountManager.changeUserType("Jason_baba", UserType.SPEAKER);
        assertFalse(accountManager.getUser("jason_baba").getUserType() == UserType.ATTENDEE);
        assertEquals(true, accountManager.getUser("jason_baba").getUserType() == UserType.SPEAKER);
    }

    @Test
    public void test_getUserNames() {
        AccountManager accountManager = new AccountManager();
        AccountController accountController = new AccountController();
        accountController.createUser("Jafar", "Jafar_baba", "password",
                "password", UserType.VIP);
        accountController.createUser("Sepehr", "Sepehr_baba", "password2",
                "password2", UserType.ORGANIZER);
        assertTrue(accountManager.getUsernames().contains("Jafar_baba"));
        assertTrue(accountManager.getUsernames().contains("Sepehr_baba"));
    }

    @Test
    public void test_userExists() {
        AccountManager accountManager = new AccountManager();
        accountManager.createUser("Jason", "Jason_baba", "Jason123", UserType.ATTENDEE);
        assertTrue(accountManager.userExists("Jason_baba"));
        assertFalse(accountManager.userExists("khoobi"));
    }
}
