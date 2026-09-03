package com.sunrisedental.service.observer;

import com.sunrisedental.dto.AppointmentDetailDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject / Publisher in the Observer Design Pattern.
 * Manages registered observers and dispatches event notifications.
 */
public class NotificationPublisher {

    private final List<NotificationObserver> observers = new ArrayList<>();

    public NotificationPublisher() {
        // Register default notification channels
        registerObserver(new SMSNotificationService());
        registerObserver(new EmailNotificationService());
    }

    public void registerObserver(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyAppointmentScheduled(AppointmentDetailDTO appointment) {
        for (NotificationObserver observer : observers) {
            try {
                observer.onAppointmentScheduled(appointment);
            } catch (Exception e) {
                // Log and continue without crashing publisher
                e.printStackTrace();
            }
        }
    }

    public void notifyAppointmentStatusChanged(AppointmentDetailDTO appointment, String oldStatus, String newStatus) {
        for (NotificationObserver observer : observers) {
            try {
                observer.onAppointmentStatusChanged(appointment, oldStatus, newStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
