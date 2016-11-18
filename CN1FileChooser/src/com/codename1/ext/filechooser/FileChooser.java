/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.ext.filechooser;

import com.codename1.system.NativeLookup;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author shannah
 */
public class FileChooser {
    
    private static FileChooserNative nativePeer;
    
    private static ActionListener callback;
    
    private static final String[] DEFAULT_MIMETYPES  = {
        ".jpg", "image/jpeg",
        ".bmp", "image/bmp",
        ".bmp", "image/x-windows-bmp",
        ".png", "image/png",
        ".gif", "image/gif",
        ".jpeg", "image/jpeg",
        ".pdf", "application/pdf",
        ".html", "text/html",
        ".txt", "text/plain",
        ".xml", "application/xml"
        
    };
    
    private static List<String> MIMETYPES;
    
    public static String[] getMimeTypes() {
        return MIMETYPES().toArray(new String[MIMETYPES().size()]);
    }
    
    private static List<String> MIMETYPES() {
        if (MIMETYPES == null) {
            MIMETYPES = new ArrayList<String>();
            MIMETYPES.addAll(Arrays.asList(DEFAULT_MIMETYPES));
        }
        return MIMETYPES;
    }
    
    public static void addMimeType(String extension, String mime) {
        if (extension.indexOf('.') != 0) {
            extension = "." + extension;
        }
        MIMETYPES().add(extension);
        MIMETYPES().add(mime);
    }
    
    private static FileChooserNative nativePeer() {
        if (nativePeer == null) {
            nativePeer = (FileChooserNative)NativeLookup.create(FileChooserNative.class);
        }
        return nativePeer;
    }
    
    public static boolean isAvailable() {
        try {
            if ("win".equals(Display.getInstance().getPlatformName())) {
                // We don't have native interfaces yet in UWP so this is a quick
                // hack to indicate that we do indeed support windows.
                return true;
            }
            //System.out.println("Checking if is available");
            return nativePeer().isSupported();
        } catch (Exception ex) {
            return false;
        }
    }
    
    
    private static String guessMime(String extension) {
        StringBuilder sb = new StringBuilder();
        int len = MIMETYPES().size();
        for (int i=0; i<len; i+=2) {
            if (extension.toLowerCase().equals(MIMETYPES().get(i))) {
                sb.append(MIMETYPES().get(i+1)).append(",");
                
            }
        }
        if (sb.length() > 0) {
            return sb.toString().substring(0, sb.length()-1);
        }
        return "";
    }
    
    private static String guessExtension(String mime, boolean includeSeparator) {
        StringBuilder sb = new StringBuilder();
        int len = MIMETYPES().size();
        for (int i=0; i<len; i+=2) {
            if (mime.toLowerCase().equals(MIMETYPES().get(i+1))) {
                String ext = MIMETYPES().get(i);
                if (!includeSeparator) {
                    ext = ext.substring(1);
                }
                sb.append(ext).append(",");
                
            }
        }
        if (sb.length() > 0) {
            return sb.toString().substring(0, sb.length()-1);
        }
        return "";
    }
   
    
    public static void showOpenDialog(String accept, ActionListener onComplete) {
        
        if (isAvailable()) {
            System.out.println("Is available");
            Display d = Display.getInstance();
            // Set javascript accept
            String key = "javascript.openGallery.accept";
            d.setProperty(key, accept);
            
            // Set Android accept
            key = "android.openGallery.accept";
            StringBuilder sb = new StringBuilder();
            List<String> parts = StringUtil.tokenize(accept, ',');
            String mimetypes = "*/*";
            for(String part : parts) {
                part = part.trim();
                if (part.length() == 0) {
                    continue;
                }
                if (part.indexOf('/') > 0) {
                    sb.append(part).append(",");
                } else if (part.indexOf('.') == 0) {
                    String mimeType = guessMime(part);
                    if (mimeType.length() != 0) {
                        sb.append(mimeType).append(",");
                    }
                }
                
                
            }
            if (sb.length() > 0) {
                mimetypes = sb.toString().substring(0, sb.length()-1);
            }
            d.setProperty(key, mimetypes);
            
            
            key = "windows.openGallery.accept";
            sb = new StringBuilder();
            for (String part : parts) {
                part = part.trim();
                if (part.length() == 0) {
                    continue;
                }
                if (part.indexOf('/') > 0) {
                    String ext = guessExtension(part, true);
                    if (ext.length() != 0) {
                        sb.append(ext).append(",");
                    }
                } else if (part.charAt(0) == '.') {
                    sb.append(part).append(",");
                }
            }
            String exts = sb.length() > 0 ? sb.toString().substring(0, sb.length()-1) : "*";
            d.setProperty(key, exts);
            
            // Set the javase accept
            key = "javase.openGallery.accept";
            sb = new StringBuilder();
            for (String part : parts) {
                part = part.trim();
                if (part.length() == 0) {
                    continue;
                }
                if (part.indexOf('/') > 0) {
                    String ext = guessExtension(part, false);
                    if (ext.length() != 0) {
                        sb.append(ext).append(",");
                    } else if (part.equals("*/*")) {
                        sb.append("*").append(",");
                    }
                } else if (part.charAt(0) == '.') {
                    sb.append(part.substring(1)).append(",");
                }
            }
            String extensions = sb.length() > 0 ? sb.toString().substring(0, sb.length()-1) : "";
            d.setProperty(key, extensions);
            
            
            
            String platform = d.getPlatformName();
            if ("ios".equals(platform) && !d.isSimulator()) {
                callback = onComplete;
                nativePeer().showNativeChooser(extensions);
            } else {
                d.openGallery(onComplete, -9999);
            }
        } else {
            onComplete.actionPerformed(null);
        }
    }
    
    
    
    static void fireNativeOnComplete(String path) {
        if (callback != null) {
            Display.getInstance().callSerially(()->{
                if (callback != null) {
                    callback.actionPerformed(new ActionEvent(path));
                    callback = null;
                }
            });
        }
    }
    
    
    
}
