# Filechooser for [Codename One](https://www.codenameone.com)

Supported Platforms:

* Javascript
* iOS
* Android
* Simulator (JavaSE)

## Installation

For instructions on installing cn1libs, see https://www.codenameone.com/blog/automatically-install-update-distribute-cn1libs-extensions.html[this tutorial].

### Alternate Maven Installation

If your project uses Maven, the above installation instructions will still work, but you can alternately simply add the Maven dependency to your common/pom.xml file:


```xml
<dependency>
  <groupId>com.codenameone</groupId>
  <artifactId>filechooser-lib</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

**IMPORTANT:** If deploying to iOS, you'll need to make sure that your App ID includes iCloud support, and you must associate with at least one iCloud container.  [See iOS setup instructions here](https://github.com/shannah/cn1-filechooser/wiki/iOS-Setup)


## Code Sample:

~~~~
if (FileChooser.isAvailable()) {
    FileChooser.showOpenDialog(".xls, .csv, text/plain", e2-> {
        String file = (String)e2.getSource();
        if (file == null) {
            hi.add("No file was selected");
            hi.revalidate();
        } else {
           String extension = null;
           if (file.lastIndexOf(".") > 0) {
               extension = file.substring(file.lastIndexOf(".")+1);
           }
           if ("txt".equals(extension)) {
               FileSystemStorage fs = FileSystemStorage.getInstance();
               try {
                   InputStream fis = fs.openInputStream(file);
                   hi.addComponent(new SpanLabel(Util.readToString(fis)));
               } catch (Exception ex) {
                   Log.e(ex);
               }
           } else {
               hi.add("Selected file "+file);
           }
        }
        hi.revalidate();
    });
}
~~~~

## Troubleshooting:

See [Troubleshooting wiki page](https://github.com/shannah/cn1-filechooser/wiki/Troubleshooting) for common errors related to this library and how to fix them.

## Other Useful Links

* [New File Dialogs in Codename One (Blog Post)](https://www.codenameone.com/blog/native-file-open-dialogs.html)

## Credits

* Created by [Steve Hannah](https://sjhannah.com)