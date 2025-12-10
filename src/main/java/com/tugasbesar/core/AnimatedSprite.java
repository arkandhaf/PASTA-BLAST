package com.tugasbesar.core;

import java.awt.image.BufferedImage;

/**
 * AnimatedSprite - Handles sprite animation with frame management
 * Useful for animating character movement, cooking actions, etc.
 */
public class AnimatedSprite {
    
    private BufferedImage[] frames;
    private int currentFrameIndex;
    private int frameCounter;
    private int animationSpeed; // How many update cycles per frame
    private boolean isLooping;
    private boolean isPlaying;
    
    /**
     * Constructor for AnimatedSprite
     * @param frames Array of BufferedImage frames
     * @param animationSpeed Number of cycles between frame changes (higher = slower)
     */
    public AnimatedSprite(BufferedImage[] frames, int animationSpeed) {
        this.frames = frames;
        this.animationSpeed = animationSpeed;
        this.currentFrameIndex = 0;
        this.frameCounter = 0;
        this.isLooping = true;
        this.isPlaying = true;
    }
    
    /**
     * Update the animation frame
     */
    public void update() {
        if (!isPlaying || frames == null || frames.length == 0) {
            return;
        }
        
        frameCounter++;
        if (frameCounter >= animationSpeed) {
            frameCounter = 0;
            currentFrameIndex++;
            
            if (currentFrameIndex >= frames.length) {
                if (isLooping) {
                    currentFrameIndex = 0;
                } else {
                    currentFrameIndex = frames.length - 1;
                    isPlaying = false;
                }
            }
        }
    }
    
    /**
     * Get the current frame image
     */
    public BufferedImage getCurrentFrame() {
        if (frames == null || frames.length == 0) {
            return null;
        }
        return frames[currentFrameIndex];
    }
    
    /**
     * Reset animation to first frame
     */
    public void reset() {
        currentFrameIndex = 0;
        frameCounter = 0;
        isPlaying = true;
    }
    
    /**
     * Stop animation and return to first frame
     */
    public void stop() {
        isPlaying = false;
        reset();
    }
    
    /**
     * Play animation
     */
    public void play() {
        isPlaying = true;
    }
    
    /**
     * Pause animation at current frame
     */
    public void pause() {
        isPlaying = false;
    }
    
    /**
     * Check if animation is finished (only valid for non-looping animations)
     */
    public boolean isFinished() {
        return !isPlaying && !isLooping;
    }
    
    // Getters and Setters
    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }
    
    public void setCurrentFrameIndex(int index) {
        if (index >= 0 && index < frames.length) {
            currentFrameIndex = index;
            frameCounter = 0;
        }
    }
    
    public void setAnimationSpeed(int speed) {
        this.animationSpeed = speed;
    }
    
    public void setLooping(boolean looping) {
        this.isLooping = looping;
    }
    
    public boolean isLooping() {
        return isLooping;
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public int getFrameCount() {
        return frames != null ? frames.length : 0;
    }
}
