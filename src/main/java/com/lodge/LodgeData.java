package com.lodge;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class LodgeData {

    private static final Path DATA_FILE = Path.of(
            System.getProperty("user.home"),
            ".lodge-billing-system",
            "data.bin"
    );

    private static StoreState state = loadState();

    private LodgeData() {
        // Utility class; do not instantiate.
    }

    private static class StoreState implements Serializable {

        private static final long serialVersionUID = 1L;

        private final List<UserAccount> users = new ArrayList<>();
        private final List<RoomRecord> rooms = new ArrayList<>();
        private final List<BookingRecord> bookings = new ArrayList<>();
        private final List<TaskRecord> tasks = new ArrayList<>();
    }

    private static StoreState loadState() {
        if (Files.exists(DATA_FILE)) {
            try (ObjectInputStream in = new ObjectInputStream(
                    Files.newInputStream(DATA_FILE))) {

                Object object = in.readObject();

                if (object instanceof StoreState saved) {
                    return saved;
                }

                System.err.println("Saved lodge data has an unexpected format.");

            } catch (IOException | ClassNotFoundException ex) {
                System.err.println(
                        "Could not load lodge data: " + ex.getMessage()
                );
            }
        }

        return new StoreState();
    }

    public static synchronized void save() {
        Path temporary = DATA_FILE.resolveSibling("data.tmp");

        try {
            Files.createDirectories(DATA_FILE.getParent());

            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(temporary))) {
                out.writeObject(state);
            }

            try {
                Files.move(
                        temporary,
                        DATA_FILE,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(
                        temporary,
                        DATA_FILE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException ex) {
            System.err.println(
                    "Could not save lodge data: " + ex.getMessage()
            );

            try {
                Files.deleteIfExists(temporary);
            } catch (IOException cleanupError) {
                System.err.println(
                        "Could not clean up temporary file: "
                                + cleanupError.getMessage()
                );
            }
        }
    }

    public static List<UserAccount> users() {
        return state.users;
    }

    public static List<RoomRecord> rooms() {
        return state.rooms;
    }

    public static List<BookingRecord> bookings() {
        return state.bookings;
    }

    public static List<TaskRecord> tasks() {
        return state.tasks;
    }
}