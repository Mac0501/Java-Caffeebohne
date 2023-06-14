package com.example.baum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomData {
    private final ObservableList<Room> roomList;
    private final DatabaseManager databaseManager;

    public RoomData(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        roomList = FXCollections.observableArrayList();
    }

    public ObservableList<Room> getRoomList() {
        return roomList;
    }

    public void fetchRoomsFromDatabase() {
        String selectQuery = "SELECT * FROM room";
        try {
            Statement statement = databaseManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Room room = new Room(id, name);
                roomList.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Room getRoomById(int roomId) {
        for (Room room : roomList) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }

    public Room getRoomByName(String roomName) {
        for (Room room : roomList) {
            if (room.getName().equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    public void addRoom(String name) {
        String insertQuery = "INSERT INTO room (name) VALUES (?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.executeUpdate();

            int lastInsertedId = getLastInsertedId();
            Room newRoom = new Room(lastInsertedId, name);
            roomList.add(newRoom);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeRoom(Room room) {
        if (room != null) {
            try {
                String deleteQuery = "DELETE FROM room WHERE id = ?";
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(deleteQuery);
                statement.setInt(1, room.getId());
                statement.executeUpdate();
                roomList.remove(room);
            } catch (SQLException e) {
                // Display an error alert
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error: Delete Room");
                alert.setHeaderText("Failed to delete the room.");
                alert.setContentText("The room has associated courses.");

                alert.showAndWait();
            }
        }
    }

    private int getLastInsertedId() throws SQLException {
        Statement statement = databaseManager.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    public ObservableList<Room> searchRoomsByName(String searchTerm) {
        ObservableList<Room> searchResults = FXCollections.observableArrayList();
        String searchQuery = "SELECT * FROM room WHERE LOWER(name) LIKE ?";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(searchQuery);
            statement.setString(1, "%" + searchTerm.toLowerCase() + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Room room = new Room(id, name);
                searchResults.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

}
