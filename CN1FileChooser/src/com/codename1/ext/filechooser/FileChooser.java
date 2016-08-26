/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.ext.filechooser;

import com.codename1.system.NativeLookup;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionListener;
import java.util.List;

/**
 *
 * @author shannah
 */
public class FileChooser {
    
    public static boolean isAvailable() {
        try {
            return ((FileChooserNative)NativeLookup.create(FileChooserNative.class)).isSupported();
        } catch (Exception ex) {
            return false;
        }
    }
    
    
    public static void showOpenDialog(String accept, ActionListener onComplete) {
        if (isAvailable()) {
            Display d = Display.getInstance();
            String key = "javascript.openGallery.accept";
            d.setProperty(key, accept);
            d.openGallery(onComplete, -9999);
        } else {
            onComplete.actionPerformed(null);
        }
    }
    
    
    
    
    
    
    
}
