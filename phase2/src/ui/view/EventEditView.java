package ui.view;

import Presenters.EventEditPresenter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilCalendarModel;
import ui.state.EventBundle;
import ui.state.EventEditBundle;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A view for editing or creating events. WIP
 */
public class EventEditView extends JPanel implements View {
    private JTextField titleField = new JTextField();
    private JList<String> speakerField;
    private JTextField timeField = new JTextField();

    // stuff for making JDatePicker work.
    private UtilCalendarModel calModel = new UtilCalendarModel();
    private Properties properties = new Properties();

    {
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
    }

    private AbstractFormatter formatter = new AbstractFormatter() {
        private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws ParseException {
            return f.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return f.format(cal.getTime());
            }

            return "";
        }
    };


    private JDatePanelImpl datePanel = new JDatePanelImpl(calModel, properties);
    private JDatePickerImpl dateField = new JDatePickerImpl(datePanel, formatter);

    private JTextField durationField = new JTextField();
    private JTextField capacityField = new JTextField();

    private JComboBox<String> roomField = new JComboBox<>();
    private JTextArea descField = new JTextArea();
    private JCheckBox vipCheckBox = new JCheckBox();

    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");

    // for converting calendar date-time to time
    private SimpleDateFormat df = new SimpleDateFormat("h:mm a");

    private EventEditPresenter presenter;

    /**
     * Constructor for pre-populated forms (for editing events)
     *
     * @param presenter the corresponding presenter for this view.
     */
    public EventEditView(EventEditPresenter presenter) {
        this.presenter = presenter;
        EventEditBundle bundle = presenter.getBundle();

        GridBagConstraints cst = new GridBagConstraints();
        setLayout(new GridBagLayout());

        speakerField = new JList<>();
        DefaultListModel listModel = new DefaultListModel<>();
        bundle.getSpeakerOptions().stream().forEach(listModel::addElement);
        speakerField.setModel(listModel);
        speakerField.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        speakerField.setLayoutOrientation(JList.VERTICAL_WRAP);
        int[] selectedIndeces = new int[bundle.getSpeaker().size()];
        for (int i = 0; i < selectedIndeces.length; i++) {
            selectedIndeces[i] = listModel.indexOf(bundle.getSpeaker().get(i));
        }
        speakerField.setSelectedIndices(selectedIndeces);

        titleField.setText(bundle.getTitle());
        titleField.setPreferredSize(new Dimension(120, 20));
        descField.setText(bundle.getDescription());
        durationField.setText(bundle.getDuration());
        capacityField.setText(Integer.toString(bundle.getCapacity()));
        vipCheckBox.setSelected(bundle.isVipOnly());

        bundle.getRoomOptions().stream().forEach(roomField::addItem);
        if (!bundle.getRoom().isEmpty())
            roomField.setSelectedItem(bundle.getRoom());

        cst.gridx = 0;
        cst.gridy = 0;
        cst.gridwidth = 1;
        cst.gridheight = 1;
        cst.insets = new Insets(7, 7, 7, 7);
        cst.fill = GridBagConstraints.BOTH;

        add(new JLabel("Title:"), cst);
        cst.gridx = 1;
        add(titleField, cst);
        if (!titleField.getText().equals("")) {
            titleField.setEnabled(false);
        }
        cst.gridy++;
        cst.gridx = 0;
        add(new JLabel("Speaker:"), cst);
        cst.gridy++;
        cst.gridheight = 3;
        cst.gridwidth = 2;
        JScrollPane scroll = new JScrollPane(speakerField);
        scroll.setMinimumSize(new Dimension(400, 50));
        add(scroll, cst);
        cst.gridheight = 1;
        cst.gridwidth = 1;
        cst.gridy += 3;
        cst.gridx = 0;
        add(new JLabel("Time:"), cst);
        cst.gridx = 1;
        add(timeField, cst);
        cst.gridy++;

        cst.gridx = 0;
        add(new JLabel("Date:"), cst);
        cst.gridx = 1;
        add(dateField, cst);
        cst.gridy++;

        cst.gridx = 0;
        add(new JLabel("Duration:"), cst);
        cst.gridx = 1;
        add(durationField, cst);
        cst.gridy++;

        cst.gridx = 0;
        add(new JLabel("Room:"), cst);
        cst.gridx = 1;
        add(roomField, cst);
        cst.gridy++;

        cst.gridx = 0;
        add(new JLabel("Capacity:"), cst);
        cst.gridx = 1;
        add(capacityField, cst);
        cst.gridy++;

        cst.gridx = 0;
        add(new JLabel("VIP Only:"), cst);
        cst.gridx = 1;
        add(vipCheckBox, cst);
        cst.gridy++;

        cst.gridx = 0;
        cst.gridwidth = 2;
        add(new JLabel("Description"), cst);
        cst.gridy++;
        cst.gridheight = 2;
        descField.setMinimumSize(new Dimension(20, 50));
        add(descField, cst);
        cst.gridy++;
        cst.gridy++;
        cst.gridheight = 1;
        cst.gridwidth = 1;
        add(saveButton, cst);
        cst.gridx = 1;
        add(cancelButton, cst);

        saveButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> cancel());

        timeField.setText(df.format(bundle.getTime().getTime()));
        calModel.setValue(bundle.getTime());

        repaint();
        revalidate();
    }

    /**
     * Triggered when user presses save button
     */
    private void save() {
        EventBundle bundle = null;
        try {
            bundle = getEventBundle();
        } catch (ParseException e) {
            showIncorrectInputDialog("Proper time format: hour:minute am/pm\nProper duration format: hour:minute");
            return;
        }
        presenter.save(bundle, this);
    }

    /**
     * Triggered when user presses cancel button
     */
    private void cancel() {
        if (showConfirmDialog("Are you sure? Your changes will be unsaved.")) {
            presenter.cancel();
        }
    }

    /**
     * Gets the values from the fields as EventBundle
     *
     * @return EventBundle of info from fields.
     * @throws ParseException iff user entered invalid input, i.e., invalid time or duration or capacity
     */
    public EventBundle getEventBundle() throws ParseException {
        String title = titleField.getText();
        String description = descField.getText();
        String room = (String) roomField.getSelectedItem();
        List<String> speaker = speakerField.getSelectedValuesList();
        String duration = durationField.getText();
        boolean vipOnly = vipCheckBox.isSelected();

        // accepting 0:00, 1:10, 1323:00 but not 10:87
        Pattern durationPattern = Pattern.compile("^\\d+:[0-5]\\d$");
        if (!durationPattern.matcher(duration).find()) {
            throw new ParseException("Invalid duration entered", 0);
        }

        String cap = capacityField.getText();
        int capacity = 0;
        try {
            capacity = Integer.parseInt(cap);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid capacity entered", 0);
        }


        String timeString = timeField.getText();
        Date timeNoDate = df.parse(timeString); // throws if improper

        Calendar calTime = Calendar.getInstance();
        calTime.setTime(timeNoDate);
        Calendar date = (Calendar) dateField.getModel().getValue();
        date.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));


        return new EventBundle(title, description, speaker, room, date, duration, capacity, vipOnly);
    }

    @Override
    public String getViewName() {
        return "Edit/Create Event";
    }
}
