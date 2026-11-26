import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.awt.Font;
public class GUI {
    private static Playlist pl = new Playlist("the playlist", new ArrayList<>());
    private static Playlist backup = pl.backup();
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // header
            JLabel headerTitle = new JLabel("NetEase Playlist Manager");
            headerTitle.setFont(new Font("Arial", Font.BOLD, 30));
            JLabel playlistInfo = new JLabel("Your Playlist : " + pl.getName() + " | " + pl.getSize() + " songs");
            playlistInfo.setFont(new Font("Arial", Font.BOLD, 24));
            JPanel header = new JPanel(new BorderLayout());
            header.add(headerTitle, BorderLayout.NORTH);
            header.add(playlistInfo, BorderLayout.SOUTH);

            // content
            JLabel message = new JLabel("Please select operation");
            JPanel content = new JPanel();
            content.add(message);


            // buttons
            JButton backupButton = new JButton("Backup");
            backupButton.addActionListener(e -> {
                backup = pl.backup();
                message.setText(backup.getName() + " updated!");
            });
            JButton compareButton = new JButton("Compare");
            compareButton.addActionListener(e -> {
                message.setText(pl.compareWith(backup));
            });
            JButton refreshButton = new JButton("Refresh");
            JPanel buttons = new JPanel();
            buttons.add(backupButton);
            buttons.add(compareButton);
            buttons.add(refreshButton);
            // frame
            JFrame frame = new JFrame("Netease Playlist Manager");
            frame.add(header, BorderLayout.NORTH);
            frame.add(content, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setSize(800, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });

        
    }
    
}

