package UseCases;

import Entities.Event;
import Entities.ScheduleEntry;
import Gateways.IGateway;
import Gateways.JsonDatabase;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Haoming & Parssa
 */
public class ScheduleManager implements Serializable {
    private IGateway<Event> eventJsonDatabase;
    private IGateway<ScheduleEntry> scheduleEntryJsonDatabase;


    public ScheduleManager() {

        eventJsonDatabase = new JsonDatabase<>("Event", Event.class);
        scheduleEntryJsonDatabase = new JsonDatabase<>("Schedule Entry", ScheduleEntry.class);
    }

    /**
     * Adds a new event to the schedule at a certain time in a room
     *
     * @param roomName  Room that the event is going inside
     * @param eventName Event that is being added
     * @param time      The time that the event is taking place
     * @return true if the event was successfully added
     */
    private boolean addNewEvent(String roomName, String eventName, Calendar time, int duration) {
        ScheduleEntry scheduleEntry = new ScheduleEntry(eventName, roomName, time, duration);
        scheduleEntryJsonDatabase.write(scheduleEntry, eventName);
        return true;
    }

    /**
     * Gets a schedule of all event's an attendee is enrolled in
     *
     * @param username Username of the attendee
     * @return list containing event names of event the attendee is in
     */
    public List<ScheduleEntry> getAttendeeEvents(String username) {
        return eventJsonDatabase.filterStream(e -> e.getAttendees()
                .contains(username))
                .map(e -> scheduleEntryJsonDatabase.read(e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of ScheduleEntries of all event's of a Speaker
     *
     * @param username Username of the speaker
     * @return List of ScheduleEntries containing only event's a Speaker is speaking at
     */
    public List<ScheduleEntry> getSpeakerEvents(String username) {
        return eventJsonDatabase.filterStream(e -> e.getSpeakers()
                .contains(username))
                .map(e -> scheduleEntryJsonDatabase.read(e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if an event exists
     *
     * @param eventName Name of event that is being checked
     * @return true if event exists
     */
    public boolean eventExists(String eventName) {
        System.out.println(eventJsonDatabase.getIds());
        return eventJsonDatabase.getIds().contains(eventName);
    }

    public void setDescription(String description, String eventName) {
        getEvent(eventName).setDescription(description);
    }

    /**
     * Checks if event has already happened
     *
     * @param eventName Event that is being checked
     * @return true if event has already occurred
     */
    public boolean eventHasHappened(String eventName) {
        //if (eventExists(eventName)) {
        //    return true;
        return false;
        }  //TODO need to account for if event has actually happened

    /**
     * Checks if an event is full
     *
     * @param eventName Event that is being checked
     * @return true if the event is full
     */
    public boolean eventFull(String eventName) {
        return eventJsonDatabase.read(eventName).isEventFull();
    }

    /**
     * Gets Event from it's name
     *
     * @param eventName Name of Event that is to be taken
     * @return the event if it exists and null otherwise
     */
    public Event getEvent(String eventName) {
        return eventJsonDatabase.read(eventName);
    }

    /**
     * Gets set of attendees (as strings) of an event
     *
     * @param eventName Name of Event that we want attendees from
     * @return the list of attendees of the event (is also empty if the event does not exist)
     */
    public Set<String> getEventAttendees(String eventName) {
        return getEvent(eventName).getAttendees();
    }

    public boolean inEvent(String eventName, String username) {
        return getEventAttendees(eventName).contains(username);
    }

    /**
     * Assigns speaker to an event
     *
     * @param speaker Name of Speaker to be added
     * @param event   Name of event
     * @return true if an event exists and speaker is added
     */
    public boolean assignSpeaker(String speaker, String event) {
        return getEvent(event).setSpeaker(speaker);
    }

    /**
     * Creates a new Event
     *
     * @param eventName     Name of Event that is to be created
     * @param eventCapacity Capacity of Event that is to be created
     * @return true if no other event has that name and capacity is positive and new Event is created
     */
    public boolean createEvent(String eventName, int eventCapacity, String roomName, Calendar time, int duration) {
        Event event = new Event(eventName);
        event.setEventCapacity(eventCapacity);
        eventJsonDatabase.write(event, eventName);
        return addNewEvent(roomName, eventName, time, duration);
    }

    /**
     * Setter for the capacity of events
     *
     * @param capacity: capacity of event
     * @return true if capacity is positive and is successfully set
     */
    public boolean setEventCapacity(String event, int capacity) {
        return getEvent(event).setEventCapacity(capacity);
    }


    /**
     * Method for checking if user can sign up for an event.
     *
     * @param eventName the name of event.
     * @return true iff
     * 1) event exists
     * 2) event has not occurred
     * 3) the event is not full
     */
    public boolean canSignUpForEvent(String eventName) {
        return !(!eventExists(eventName) || eventHasHappened(eventName) || !eventFull(eventName));
    }

    /**
     * Removes Attendee from an event
     *
     * @param username the user being checked.
     * @param event    name of event
     * @return true if succesfully removed from event
     */
    public boolean removeFromEvent(String username, String event) {
        return eventJsonDatabase.read(event).removeAttendeeFromEvent(username);
    }

    /**
     * Signs Attendee up for event
     *
     * @param username username of user trying to sign up for event
     * @param event    name of event
     * @return true if succesfully signed up for event
     */
    public boolean signUpForEvent(String username, String event) {
        Event myEvent = eventJsonDatabase.read(event);
        boolean t = myEvent.addAttendeeToEvent(username);
        System.out.println("aw");
        if (t) {
            System.out.println("yay");
            eventJsonDatabase.write(myEvent, event);
            return true;
        }
        return false;
    }

    /**
     * Returns a list of all events (vip events hidden unles vipFilter is true)
     *
     * @param vipFilter true if vip events should be shown
     * @return list of event titles.
     */
    public List<String> getEventNames(boolean vipFilter) {
        return vipFilter
                ? eventJsonDatabase.getIds()
                : eventJsonDatabase
                .filterStream(e -> !(e.isVIPOnly()))
                .map(Event::getName)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of names of VIP events
     * @return list of vip event names.
     */
    public List<String> getVIPEventNames() {
        return eventJsonDatabase.filterStream(Event::isVIPOnly).map(Event::getName).collect(Collectors.toList());
    }

    /**
     * Gets the event that is occurring in that room
     *
     * @param roomID id of the room
     * @return a list of events occurring in that room
     */
    // todo
    public List<ScheduleEntry> getRoomEvents(String roomID) {
        return null;
    }
}
