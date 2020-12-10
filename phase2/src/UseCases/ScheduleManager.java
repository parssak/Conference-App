package UseCases;

import Entities.Event;
import Entities.Room;
import Entities.Schedule;
import Gateways.JsonDatabase;
import Util.ScheduleTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Haoming & Parssa
 */
public class ScheduleManager implements Serializable {
    Schedule theSchedule; // should never be given out; its mutable
    private JsonDatabase<Event> eventJsonDatabase;
    HashMap<String, Room> rooms; // should never be given out; its mutable

    public ScheduleManager() {
        eventJsonDatabase = new JsonDatabase<>("Event", Event.class);
//        theSchedule = new Schedule();
//        events = new HashMap<>();
//        rooms = new HashMap<>();
    }

    /**
     * Adds a new event to the schedule at a certain time in a room
     *
     * @param roomName  Room that the event is going inside
     * @param eventName Event that is being added
     * @param time      The time that the event is taking place
     * @return true if the event was successfully added
     */
    public boolean addNewEvent(String roomName, String eventName, String time) {
        if (!eventExists(eventName)) {
            return false;
        }
        if (getEvent(eventName).getEventCapacity() > rooms.get(roomName).getRoomCapacity()) {
            return false;
        }
        return theSchedule.addToSchedule(roomName, eventName, time);
    }

    /**
     * Gets a schedule of all event's an attendee is enrolled in
     *
     * @param username Username of the attendee
     * @return list containing event names of event the attendee is in
     */
    public ArrayList<String> getAttendeeEvents(String username) {
        //TODO
//        return scheduleGateway.getUserEvents(username);
        return new ArrayList<>();
    }
    
    /**
     * Gets a schedule of all event's of a Speaker
     *
     * @param username Username of the speaker
     * @return Schedule containing only event's a Speaker is speaking at
     */
    protected Schedule getSpeakerEvents(String username) {
        // TODO
        //  return a schedule of events where username is speaker, if there are none, return empty.
        HashMap<ScheduleTime, HashMap<String, String>> schedule = theSchedule.getSchedule();
        Schedule speakerSchedule = new Schedule();

        for (Map.Entry<ScheduleTime, HashMap<String, String>> timeEntry : schedule.entrySet()) {
            ScheduleTime time = timeEntry.getKey();
            for (Map.Entry<String, String> roomEntry : timeEntry.getValue().entrySet()) {
                String room = roomEntry.getKey();
                String event = roomEntry.getValue();
                if (getEvent(event).getSpeakers().contains(username)) {
                    speakerSchedule.addToSchedule(room, event, time);
                }
            }
        }
        if (!speakerSchedule.getSchedule().isEmpty()) {
            return speakerSchedule;
        }
        return null;
    }

    /**
     * Checks if an event exists
     *
     * @param eventName Name of event that is being checked
     * @return true if event exists
     */
    public boolean eventExists(String eventName) {
        return eventJsonDatabase.getIds().contains(eventName);
    }

    /**
     * Checks if event has already happened
     *
     * @param eventName Event that is being checked
     * @return true if event has already occurred
     */
    public boolean eventHasHappened(String eventName) {
        if (eventExists(eventName)) {
            return true;
        } else return false;
    }

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
     * Creates a new Room
     *
     * @param roomName Name of Room that is to be created
     * @return true if no other room has that name and new Room is created
     */
    public boolean createRoom(String roomName, int roomCapacity) {
        if (getRoom(roomName) != null) return false;
        Room room = new Room(roomName);
        if (! room.setRoomCapacity(roomCapacity)) {
            return false;
        }
        rooms.put(roomName, room);
        return true;
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
     * Gets Room from it's name
     *
     * @param roomName Name of Room that is to be taken
     * @return the room if it exists and null otherwise
     */
    public Room getRoom(String roomName) {
        return (rooms.containsKey(roomName)) ? rooms.get(roomName) : null;
    }
    /**
     * Gets list of attendees (as strings) of an event
     *
     * @param eventName Name of Event that we want attendees from
     * @return the list of attendees of the event (is also empty if the event does not exist)
     */
    public ArrayList<String> getEventAttendees(String eventName) {
        ArrayList<String> attendees = new ArrayList<>();

        if (eventExists(eventName)) {
            for (String e : getEvent(eventName).getAttendees()) {
                attendees.add(e);
            }
        }

        return attendees;
    }

    /**
     * Assigns speaker to a room at a certain time
     *
     * @param speaker Name of Speaker to be added
     * @param room Name of room
     * @param time Time of the event
     * @return true if an event exists at such a time and room and speaker is added
     */
    public boolean assignSpeaker(String speaker, String room, String time) {

        ScheduleTime timeKey = ScheduleTime.toScheduleTime(time);
        HashMap<ScheduleTime, HashMap<String, String>> schedule = theSchedule.getSchedule();
        if (schedule.containsKey(timeKey)) {
            String eventName = schedule.get(timeKey).get(room);
            return getEvent(eventName).setSpeaker(speaker);
        }
        return false;
    }

    /**
     * Creates a new Event
     *
     * @param eventName Name of Event that is to be created
     * @param eventCapacity Capacity of Event that is to be created
     * @return true if no other event has that name and capacity is positiv and new Event is created
     */
    public boolean createEvent(String eventName, int eventCapacity) {
        if (!eventExists(eventName)) return false;
        Event event = new Event(eventName);
        if (!event.setEventCapacity(eventCapacity)) {
            return false;
        }
        eventJsonDatabase.write(event, eventName);
        return true;
    }
}
