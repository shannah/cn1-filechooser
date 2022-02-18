/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.ext.filechooser;

import com.codename1.io.Util;
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
        ".aac","audio/aac",
        ".abw","application/x-abiword",
        ".arc","application/x-freearc",
        ".avi","video/x-msvideo",
        ".azw","application/vnd.amazon.ebook",
        ".bin","application/octet-stream",
        ".bmp","image/bmp",
        ".bz","application/x-bzip",
        ".bz2","application/x-bzip2",
        ".csh","application/x-csh",
        ".css","text/css",
        ".csv","text/csv",
        ".doc","application/msword",
        ".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        ".eot","application/vnd.ms-fontobject",
        ".epub","application/epub+zip",
        ".gz","application/gzip",
        ".gif","image/gif",
        ".htm","text/html",
        ".html","text/html",
        ".ico","image/vnd.microsoft.icon",
        ".ics","text/calendar",
        ".jar","application/java-archive",
        ".jpg","image/jpeg",
        ".jpeg","image/jpeg",
        ".js","text/javascript",
        ".json","application/json",
        ".jsonld","application/ld+json",
        ".mid","audio/midi,audio/x-midi",
        ".midi","audio/midi,audio/x-midi",
        ".mjs","text/javascript",
        ".mp3","audio/mpeg",
        ".mpeg","video/mpeg",
        ".mpkg","application/vnd.apple.installer+xml",
        ".odp","application/vnd.oasis.opendocument.presentation",
        ".ods","application/vnd.oasis.opendocument.spreadsheet",
        ".odt","application/vnd.oasis.opendocument.text",
        ".oga","audio/ogg",
        ".ogv","video/ogg",
        ".ogx","application/ogg",
        ".opus","audio/opus",
        ".otf","font/otf",
        ".png","image/png",
        ".pdf","application/pdf",
        ".php","application/x-httpd-php",
        ".ppt","application/vnd.ms-powerpoint",
        ".pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation",
        ".rar","application/vnd.rar",
        ".rtf","application/rtf",
        ".sh","application/x-sh",
        ".svg","image/svg+xml",
        ".swf","application/x-shockwave-flash",
        ".tar","application/x-tar",
        ".tif","image/tiff",
        ".tiff","image/tiff",
        ".ts","video/mp2t",
        ".ttf","font/ttf",
        ".txt","text/plain",
        ".vsd","application/vnd.visio",
        ".wav","audio/wav",
        ".weba","audio/webm",
        ".webm","video/webm",
        ".webp","image/webp",
        ".woff","font/woff",
        ".woff2","font/woff2",
        ".xhtml","application/xhtml+xml",
        ".xls","application/vnd.ms-excel",
        ".xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        ".xml","application/xml",
        ".xul","application/vnd.mozilla.xul+xml",
        ".zip","application/zip",
        ".3gp","video/3gpp,audio/3gpp",
        ".3g2","video/3gpp2,audio/3gpp2",
        ".7z","application/x-7z-compressed"
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
    
    private static final String KEY_OPEN_FILES_IN_PLACE="openGallery.openFilesInPlace";
    
    /**
     * Sets a flag that causes the simulator to open files in place (i.e. without copying the file).
     */
    public static void setOpenFilesInPlace(boolean inPlace) {
        Display.getInstance().setProperty(KEY_OPEN_FILES_IN_PLACE, inPlace ? "true":"false");
    }
    
    /**
     * If true, then the simulator will open files in place rather than copying files upon opening.
     */
    public static boolean isOpenFilesInPlace() {
        return "true".equals(Display.getInstance().getProperty(KEY_OPEN_FILES_IN_PLACE, "false"));
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
        showOpenDialog(false, accept, onComplete);
    }
    
    private static boolean lastRequestIsMulti=false;
    public static void showOpenDialog(boolean multi, String accept, ActionListener onComplete) {
        lastRequestIsMulti = multi;
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
                nativePeer().showNativeChooser(extensions, multi);
            } else {
                d.openGallery(onComplete, multi?-9998:-9999);
            }
        } else {
            onComplete.actionPerformed(null);
        }
    }
    
    
    
    static void fireNativeOnComplete(String path) {
        if (callback != null) {
            Display.getInstance().callSerially(()->{
                if (callback != null) {
                    if (lastRequestIsMulti) {
                        callback.actionPerformed(new ActionEvent(Util.split(path, "\n")));
                    } else {
                        callback.actionPerformed(new ActionEvent(path));
                    }
                    callback = null;
                }
            });
        }
    }
    
    
    
}
