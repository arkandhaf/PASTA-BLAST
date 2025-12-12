package com.tugasbesar.core;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * IntroVideoPlayer - Handles playing the intro video once at startup.
 */
public final class IntroVideoPlayer {

    private static final AtomicBoolean FX_INITIALIZED = new AtomicBoolean(false);
    private static final AtomicBoolean PLAYBACK_FINISHED = new AtomicBoolean(false);

    private static final Path VIDEO_PATH = Paths.get(System.getProperty("user.dir"), "src", "resources", "assets",
            "video", "intro.mp4");

    private IntroVideoPlayer() {
    }

    public static void playIntro(Runnable onFinish) {
        if (!Files.exists(VIDEO_PATH)) {
            onFinish.run();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Dimension size = new Dimension(960, 540);
            frame.setSize(size);

            JFXPanel jfxPanel = new JFXPanel();
            jfxPanel.setPreferredSize(size);
            jfxPanel.setFocusable(false);
            frame.add(jfxPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            SwingUtilities.invokeLater(frame::requestFocusInWindow);

            final MediaPlayer[] playerHolder = new MediaPlayer[1];

            KeyAdapter skipListener = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        stopPlayback(frame, playerHolder[0], onFinish);
                    }
                }
            };
            frame.addKeyListener(skipListener);

            runOnFxThread(() -> createScene(frame, jfxPanel, playerHolder, onFinish));
        });
    }

    private static void createScene(JFrame frame, JFXPanel jfxPanel, MediaPlayer[] playerHolder, Runnable onFinish) {
        if (PLAYBACK_FINISHED.get()) {
            return;
        }
        try {
            Media media = new Media(VIDEO_PATH.toUri().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);

            playerHolder[0] = mediaPlayer;

            mediaPlayer.setOnEndOfMedia(() -> stopPlayback(frame, mediaPlayer, onFinish));
            mediaPlayer.setOnError(() -> stopPlayback(frame, mediaPlayer, onFinish));

            Group root = new Group();
            root.getChildren().add(mediaView);

            Scene scene = new Scene(root);
            mediaView.fitWidthProperty().bind(scene.widthProperty());
            mediaView.fitHeightProperty().bind(scene.heightProperty());
            jfxPanel.setScene(scene);

            mediaPlayer.play();
        } catch (MediaException ex) {
            SwingUtilities.invokeLater(() -> stopPlayback(frame, null, onFinish));
        }
    }

    private static void stopPlayback(JFrame frame, MediaPlayer mediaPlayer, Runnable onFinish) {
        if (!PLAYBACK_FINISHED.compareAndSet(false, true)) {
            return;
        }

        if (mediaPlayer != null) {
            runOnFxThread(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            });
        }

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            frame.dispose();
            onFinish.run();
        });
    }

    private static void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        try {
            Platform.runLater(runnable);
            FX_INITIALIZED.set(true);
        } catch (IllegalStateException ex) {
            if (FX_INITIALIZED.compareAndSet(false, true)) {
                Platform.startup(() -> runnable.run());
            } else {
                throw ex;
            }
        }
    }
}
