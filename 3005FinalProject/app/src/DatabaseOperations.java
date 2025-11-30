import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;


public class DatabaseOperations {

    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "password123";

    // Add a user
    public void createMember(String fname, String lname, String email, String phone, Timestamp birthday, String gender) {
        String SQL = "INSERT INTO Members (first_name, last_name, email, phone, birth_date, gender) VALUES(?,?,?,?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setTimestamp(5, birthday);
            pstmt.setString(6, gender);
            pstmt.executeUpdate();
            System.out.println("User added successfully!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Update Member based on email
    public void modifyMember(String fname, String lname, String email, String phone, Timestamp birthday, String gender) {
        if (!memberExists(email)) {
            System.out.println("Oops! This member isn't in the system!");
            return;
        }
        String SQL = "UPDATE members SET first_name=?, last_name=?, phone=?, birth_date=?, gender=? WHERE email=?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, phone);
            pstmt.setTimestamp(4, birthday);
            pstmt.setString(5, gender);
            pstmt.setString(6, email);
            pstmt.executeUpdate();
            System.out.println("Member Updated!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean memberExists(String email) {
        String SQL = "select email from members where email=?";
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                exists = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return exists;
    }

    //add a fitness goal
    public void addGoal(String name, int goal, String email) {
        String SQL = "insert into fitgoals(goal_name, goal_number, member_email) values (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, goal);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            System.out.println("Goal Added!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //create a new entry for healthMetr
    public void recordMetric(int height, int weight, int heart_rate, String email) {
        String SQL = "insert into healthmetr(height, weight_, heart_rate, member_email) values (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, height);
            pstmt.setInt(2, weight);
            pstmt.setInt(3, heart_rate);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
            System.out.println("Metric added!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //log the health metrics for the user to see, sorted by oldest to newest metrics
    public void logHealth(String email) {
        if (!memberExists(email)) {
            System.out.println("Oops! This member isn't in the system!");
            return;
        }
        String SQL = "select * from healthmetr where member_email=? order by metric_timestamp asc";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String timestamp = rs.getString("metric_timestamp");
                int weight = rs.getInt("weight_");
                int height = rs.getInt("height");
                int heartRate = rs.getInt("heart_rate");
                System.out.println("Member: " + email + ", Height: " + height + ", Weight: " + weight + ", Heart Rate: " + heartRate + ", Time of Record: " + timestamp);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //register for classes (unless they're already registered or the capacity is met)
    public void registerClasses(String email) {
        if (!memberExists(email)) {
            System.out.println("Oops! This member isn't in the system!");
            return;
        }
        String SQL = "update classes set member_email = array_append(member_email, ?) where (cardinality(member_email) < capacity and not member_email @> '{" + email + "}') or member_email is null;";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
            System.out.println("Classes Registered!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean checkTimeslot(Timestamp start_time, Timestamp end_time, int id) {
        String SQL = "select start_times, end_times from Trainers where trainer_id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp[] starts = (Timestamp[]) rs.getArray("start_times").getArray();
                Timestamp[] ends = (Timestamp[]) rs.getArray("end_times").getArray();
                for (Timestamp a : starts) {
                    for (Timestamp b : ends) {
                        if (overlapTimestamps(start_time, end_time, a, b)) {
                            System.out.println("Unfortunatly, there was a time conflict with the already available times.");
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    public boolean overlapTimestamps(Timestamp start_time, Timestamp end_time, Timestamp start_time2, Timestamp end_time2) {
        return ((start_time.compareTo(start_time2) <= 0) && end_time.compareTo(end_time2) >= 0) || (start_time.compareTo(start_time2) >= 0 && start_time.before(end_time2)) || (end_time.after(start_time2) && end_time.compareTo(end_time2) <= 0);
    }

    public void setTimeslot(Timestamp start_time, Timestamp end_time, int id) {
        String SQL = "update trainers set start_times = array_append(start_times, ?), end_times = array_append(end_times, ?) where trainer_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            
            pstmt.setTimestamp(1, start_time);
            pstmt.setTimestamp(2, end_time);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Timeslots updated!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean checkAvailability(Timestamp start_time, Timestamp end_time, int id) {
        String SQL = "select start_times, end_times from Trainers where trainer_id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp[] starts = (Timestamp[]) rs.getArray("start_times").getArray();
                Timestamp[] ends = (Timestamp[]) rs.getArray("end_times").getArray();
                for (Timestamp a : starts) {
                    for (Timestamp b : ends) {
                        if ( (start_time.after(a) || start_time.equals(a)) && (end_time.before(b) || end_time.equals(b))) {
                            //System.out.println("Unfortunatly, there was a time conflict with the already available times.");
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("The trainer is not available at this time");
        return false;
    }

    public void viewClass(int id) {
        if (!trainerExists(id)) {
            System.out.println("Oops! This trainer doesn't exist!");
            return;
        }
        String SQL = "select * from Classes where trainer_id=? order by start_time asc";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp start_time = rs.getTimestamp("start_time");
                Timestamp end_time = rs.getTimestamp("end_time");
                int room_number = rs.getInt("room_number");
                int capacity = rs.getInt("capacity");
                String[] emails = (String[]) rs.getArray("member_email").getArray();
                System.out.print("Class starts at " + start_time + " and ends at " + end_time + ". It will take place in Room " + room_number + " with a capacity of " + capacity + ", and it's participants emails are: ");
                if (emails.length == 0) {
                    System.out.print("No one has registered for this class!");
                }
                for (String a : emails) {
                    System.out.print(a + ", ");
                }
                System.out.println("");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void viewSession(int id) {
        String SQL = "select * from Sessions where trainer_id=? order by start_time asc";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp start_time = rs.getTimestamp("start_time");
                Timestamp end_time = rs.getTimestamp("end_time");
                int room_number = rs.getInt("room_number");
                String emails = rs.getString("member_email");
                System.out.println("Class starts at " + start_time + " and ends at " + end_time + ". It will take place in Room " + room_number + ", and it's participant email is " + emails);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean trainerExists(int id) {
        String SQL = "select trainer_id from trainers where trainer_id=?";
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                exists = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return exists;
    }

    public boolean assignRoom(int room_number, int id, String type, Timestamp start_time, Timestamp end_time) {
        String SQL = "select room_number, start_time, end_time from " + type + " where room_number = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, room_number);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int rm = rs.getInt("room_number");
                Timestamp st = rs.getTimestamp("start_time");
                Timestamp et = rs.getTimestamp("start_time");
                if (rm == room_number && overlapTimestamps(start_time, end_time, st, et)) {
                    System.out.println("This room is already booked");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        if (type.equalsIgnoreCase("classes")) {
            SQL = "update rooms set class_id = array_append(class_id, ?) where room_number = ?";
        } else {
            SQL = "update rooms set class_id = array_append(session_id, ?) where room_number = ?";
        }
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            
            pstmt.setInt(1, id);
            pstmt.setInt(2, room_number);
            pstmt.executeUpdate();
            System.out.println("Room Booked!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    public void createClass(Timestamp start_time, Timestamp end_time, int capacity, int room_number, int trainer_id) {
        String SQL = "select class_id from classes order by class_id desc limit 1";
        int class_id = -1;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                class_id = rs.getInt("class_id") + 1;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        if (checkAvailability(start_time, end_time, trainer_id) && assignRoom(room_number, class_id, "classes", start_time, end_time)) {
            SQL = "insert into classes(start_time, end_time, capacity, room_number, trainer_id, member_email) values(?, ?, ?, ?, ?, '{}')";
            try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            
            pstmt.setTimestamp(1, start_time);
            pstmt.setTimestamp(2, end_time);
            pstmt.setInt(3, capacity);
            pstmt.setInt(4, room_number);
            pstmt.setInt(5, trainer_id);
            pstmt.executeUpdate();
            System.out.println("Class Created!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        }

    }
    public static void main(String[] args) {
      DatabaseOperations dbOps = new DatabaseOperations();
      Scanner scanner = new Scanner(System.in);
      
      System.out.println("Are you acting as user, trainer, or an admin? (user/trainer/admin)");
      String nextline = scanner.nextLine();
      if (nextline.equalsIgnoreCase("user")) {

        //success case and failure case, if run twice
        System.out.println("Would you like to add a user? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.createMember("John", "Doe", "john@example.com", "555-555-5555", Timestamp.valueOf("2004-09-14 10:32:00"), "Male");
        }
        
        //success case if the user exists, failure otherwise
        System.out.println("Would you like to update your personal details? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.modifyMember("A", "Human", "john@example.com", "555-555-5555", Timestamp.valueOf("2004-09-14 10:32:00"), "Male");
        }

        //fail case if the user doesn't exist, success otherwise?
        System.out.println("Would you like to update/add to your fitness goals? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.addGoal("Weight loss", 130, "john@example.com");
        }

        //similar to above
        System.out.println("Would you like to store new health metrics? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.recordMetric(180, 140, 80, "john@example.com");
        }

        //if the member doesn't exist...
        System.out.println("Would you like to log your health metrics? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.logHealth("john@example.com");
        }

        //etc etc
        System.out.println("Would you like to register for any available classes? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.registerClasses("john@example.com");
        }
      }

      else if (nextline.equalsIgnoreCase("trainer")) {

        //run this twice, should cover both cases
        System.out.println("Would you like to add to your available timeslots? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            if (dbOps.checkTimeslot(Timestamp.valueOf("2023-9-16 3:15:00"), Timestamp.valueOf("2023-9-16 4:45:00"), 1)) {
                dbOps.setTimeslot(Timestamp.valueOf("2023-9-16 3:15:00"), Timestamp.valueOf("2023-9-16 4:45:00"), 1);
            }
        }

        //I guess this wouldn't work if the trainer didn't exist? sure why not
        System.out.println("Would you like to see what classes and sessions you're registered for (success case)? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.viewSession(1);
            dbOps.viewClass(1);
        }

        System.out.println("Would you like to see what classes and sessions you're registered for (failure case)? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.viewSession(14);
            dbOps.viewClass(14);
        }
      }

      else if (nextline.equalsIgnoreCase("admin")) {

        //run twice, the room will be booked.
        System.out.println("Would you like to create a new class? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            dbOps.createClass(Timestamp.valueOf("2023-9-16 3:15:00"), Timestamp.valueOf("2023-9-16 4:45:00"), 4, 1, 1);
        }
      }
      scanner.close();
  }

  //run both the dll and dml at the start, to make sure there's a clean slate
  //run the user functions thrice, once to get the failure cases in which the submitted email doesn't exist, again to get the success cases, and finally we get the user creation failure case.
  //run the trainer functions twice, y/y/y, y/n/n
  //run the admin functions twice
  //tested, it works :)
}
