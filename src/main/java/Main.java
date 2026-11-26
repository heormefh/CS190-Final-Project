import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.awt.Font;
public class Main {
    private static Playlist pl = new Playlist(1, "emty playlist", new ArrayList<>());
    private static Playlist backup = null;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            // content
            JLabel message = new JLabel("Click Refresh after entering playlist ID");
            JPanel content = new JPanel();
            JTextField idInput = new JTextField("Input Playlist ID Here", 15);
            content.add(idInput);
            content.add(message);
            NeteaseAuth auth = new NeteaseAuth();
            try {
                pl = auth.getPlaylist(Long.parseLong(idInput.getText()));

                System.out.println("Playlist: " + pl.getName());
                System.out.println("Songs: " + pl.getSize());
                backup = pl.backup();
            } catch (Exception e) {
                if (!idInput.getText().equals("Input Playlist ID Here")) {
                    message.setText("Authorization Failed");
                    e.printStackTrace();
                }
            }
            // header
            JLabel headerTitle = new JLabel("NetEase Playlist Manager");
            headerTitle.setFont(new Font("Arial", Font.BOLD, 30));
            JLabel playlistInfo = new JLabel("Your Playlist : " + pl.getName() + " | " + pl.getSize() + " songs");
            playlistInfo.setFont(new Font("Arial", Font.BOLD, 24));
            JPanel header = new JPanel(new BorderLayout());
            header.add(headerTitle, BorderLayout.NORTH);
            header.add(playlistInfo, BorderLayout.SOUTH);

            // buttons
            JButton backupButton = new JButton("Backup");
            backupButton.addActionListener(e -> {
                backup = pl.backup();
                message.setText(backup.getName() + " updated!");
            });
            JButton compareButton = new JButton("Compare");
            compareButton.addActionListener(e -> {
                if (backup == null || pl == null) {
                    message.setText("Playlist or Backup not initialized!");
                    return;
                }
                message.setText(pl.compareWith(backup));
            });
            
            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> {
                try {
                    message.setText("Playlist Refreshing...");   
                    long playlistID = Long.parseLong(idInput.getText());

                    pl = auth.getPlaylist(playlistID);
                    playlistInfo.setText("Your Playlist: " + pl.getName() + " | " + pl.getSize() + " songs");
                    message.setText("Playlist refreshed successfully!");
                } catch (Exception ex) {
                    message.setText("Playlist refresh failed");
                    ex.printStackTrace();
                }
            });

            JButton overwriteButton = new JButton("Overwrite");
            overwriteButton.addActionListener(e -> {
                if (pl == null || backup == null) {
                    message.setText("Playlist or backup is missing!");
                    return;
                }
                if (backup.getSongs().isEmpty()) {
                    message.setText("Backup is empty!");
                    return;
                }
                try {
                    message.setText("Overwriting your playlist...");
                    String deleteResponse = auth.sendPlaylistOperation("del", pl.getId(), pl.getSongs());
                    String addResponse = auth.sendPlaylistOperation("add", pl.getId(), backup.getSongs());
                    pl = auth.getPlaylist(pl.getId());
                    playlistInfo.setText("Your Playlist: " + pl.getName() + " | " + pl.getSize() + " songs");
                    
                    System.out.println("Add response: " + addResponse);

		            message.setText("Overwrite Success!");
                } catch (Exception ex) {
                    message.setText("Overwrite failed!");
                    ex.printStackTrace();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(backupButton);
            buttons.add(compareButton);
            buttons.add(refreshButton);
            buttons.add(overwriteButton);
            // frame
            JFrame frame = new JFrame("Netease Playlist Manager");
            frame.add(header, BorderLayout.NORTH);
            frame.add(content, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setSize(800, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });   
    };
}

