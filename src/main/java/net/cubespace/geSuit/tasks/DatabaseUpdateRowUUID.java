package net.cubespace.geSuit.tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.database.ConnectionHandler;
import net.cubespace.geSuit.managers.DatabaseManager;
import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author JR
 */
public class DatabaseUpdateRowUUID implements Runnable
{

    int rowID;
    String playerName;

    public DatabaseUpdateRowUUID(int id, String pname)
    {
        rowID = id;
        playerName = pname;
    }

    @Override
    public void run()
    {
        if (rowID == -1) {
            ProxyServer.getInstance().getLogger().warning("Incorrect row " + rowID + " for player " + playerName);
            return;
        }
        String uuid = Utilities.getUUID(playerName);
        if (uuid == null || uuid.isEmpty()) {
            ProxyServer.getInstance().getLogger().warning("Could not fetch UUID for player " + playerName);
        }
        else {
            ConnectionHandler connectionHandler = null;
            try {
                connectionHandler = DatabaseManager.connectionPool.getConnection();
                PreparedStatement updateUUID = connectionHandler.getPreparedStatement("updateRowUUID");
                updateUUID.setString(1, uuid);
                updateUUID.setInt(2, rowID);
                updateUUID.executeUpdate();
            }
            catch (SQLException ex) {
                ProxyServer.getInstance().getLogger().warning("Error while updating db for player " + playerName + " with UUID " + uuid);
                Logger.getLogger(DatabaseUpdateRowUUID.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                if (connectionHandler != null) {
                    connectionHandler.release();
                }
            }

        }
    }

}
