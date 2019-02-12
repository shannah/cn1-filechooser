# Filechooser for Codename One

Supported Platforms:

* Javascript
* iOS
* Android
* Simulator (JavaSE)

## Installation

1. Add [CN1FileChooser.cn1lib](bin/CN1FileChooser.cn1lib) to your project's lib directory and refresh your cn1libs.
2. If deploying to iOS, you'll need to make sure that your App ID includes iCloud support, and you must associate with at least one iCloud container.  [See iOS setup instructions here](https://github.com/shannah/cn1-filechooser/wiki/iOS-Setup)


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