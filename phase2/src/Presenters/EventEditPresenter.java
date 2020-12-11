package Presenters;

import Controllers.AccountController;
import Controllers.EventController;
import Util.UserType;
import ui.state.EventBundle;
import ui.state.EventEditBundle;
import ui.view.EventEditView;
import ui.view.View;

import java.util.List;

/**
 * Corresponding presenter for EventEditView. Allows organizers to create new events or modify existing
 * ones.
 */
public class EventEditPresenter implements Presenter {
    private EventEditBundle bundle;
    private String eventName;
    private MainPresenter mainPresenter;
    private boolean newEventMode = false;


    /**
     * Constructor when an existing event is being edited.
     *
     * @param eventName     the name of the event (assumes event exists)
     * @param mainPresenter main presenter
     */
    public EventEditPresenter(String eventName, MainPresenter mainPresenter) {
        this.eventName = eventName;
        this.mainPresenter = mainPresenter;

        AccountController accountController = new AccountController();
        EventController eventController = new EventController();
        List<String> speakerOptions = accountController.getUsernamesOfType(UserType.SPEAKER);
        List<String> roomOptions = eventController.getRoomNames();


        if (eventName == null) {
            this.bundle = new EventEditBundle(speakerOptions, roomOptions);
        } else {
            EventBundle bundle = null;
            //TODO ASSEMBLE EVENTBUNDLE (above) BUNDLE FROM CONTROLLERS (ASSUME THIS EVENT EXISTS)

            this.bundle = new EventEditBundle(bundle, speakerOptions, roomOptions);
        }
    }

    /**
     * Constructor when a new event is being created.
     *
     * @param mainPresenter main presenter.
     */
    public EventEditPresenter(MainPresenter mainPresenter) {
        this(null, mainPresenter);
        this.newEventMode = true;
    }

    public EventEditBundle getBundle() {
        return bundle;
    }

    /**
     * Method for saving changes made to event.
     *
     * @param bundle event bundle being saved to event
     * @param view   the view calling this method (used for showing popups)
     */
    public void save(EventBundle bundle, View view) {
        //TODO DO THIS METHOD
        //IF THERE ARE ANY ISSUES WITH INPUT, USE view.showErrorDialog() to tell user.
    }

    /**
     * If user wants to cancel doing any changes. Returns them to previous presenter.
     */
    public void cancel() {
        mainPresenter.back();
    }

    @Override
    public View makeView() {
        return new EventEditView(this);
    }

    @Override
    public MainPresenter getMainPresenter() {
        return mainPresenter;
    }
}