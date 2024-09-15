#import "com_codename1_ext_filechooser_FileChooserNativeImpl.h"
#import "CodenameOne_GLViewController.h"
#import <MobileCoreServices/MobileCoreServices.h>
#import "com_codename1_ext_filechooser_FileChooser.h"

extern int isIPad();
extern int CN1lastTouchX;
extern int CN1lastTouchY;
static BOOL multiSelect = NO;

static UIPopoverController* popoverController;
static int popoverSupported()
{
    return ( NSClassFromString(@"UIPopoverController") != nil) &&  (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad);
}

@implementation com_codename1_ext_filechooser_FileChooserNativeImpl

-(BOOL)isSupported{
    return YES;
}

-(BOOL)showNativeChooser: (NSString*)accept param1:(BOOL)multi {
    multiSelect = multi;
    id me = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        POOL_BEGIN();

        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Choose File"
                                                                       message:nil
                                                                preferredStyle:UIAlertControllerStyleActionSheet];

        UIAlertAction *documentAction = [UIAlertAction actionWithTitle:@"Documents" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [me openDocumentPickerWithAccept:accept];
        }];
        [alert addAction:documentAction];

        if ([accept containsString:@"image"] || [accept containsString:@"jpg"] || [accept containsString:@"png"] || [accept containsString:@"tif"] || [accept containsString:@"gif"]) {
            UIAlertAction *imageAction = [UIAlertAction actionWithTitle:@"Images" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                [me openGallery:0];
            }];
            [alert addAction:imageAction];
        }

        if ([accept containsString:@"video"] || [accept containsString:@"avi"] || [accept containsString:@"mpg"] || [accept containsString:@"mp4"] || [accept containsString:@"mpeg"] || [accept containsString:@"mov"]) {
            UIAlertAction *videoAction = [UIAlertAction actionWithTitle:@"Videos" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
                [me openGallery:1];
            }];
            [alert addAction:videoAction];
        }

        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
            com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG JAVA_NULL);
        }];
        [alert addAction:cancelAction];

        if (isIPad()) {
            alert.popoverPresentationController.sourceView = [[CodenameOne_GLViewController instance] view];
            alert.popoverPresentationController.sourceRect = CGRectMake(CN1lastTouchX, CN1lastTouchY, 1, 1);
        }

        [[CodenameOne_GLViewController instance] presentViewController:alert animated:YES completion:nil];

        POOL_END();
    });
    return YES;
}



- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentsAtURLs:(NSArray<NSURL *> *)urls {
    NSMutableString *urlString=[[NSMutableString alloc] init];
    BOOL first = YES;
    for (NSURL* url in urls) {
        if (first) {
            first = NO;
        } else {
            [urlString appendString:@"\n"];
        }
        [urlString appendString:[url path]];
    }
    if (controller.documentPickerMode == UIDocumentPickerModeImport) {
        [controller dismissViewControllerAnimated:YES completion:nil];
        com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG urlString));
    }
}

- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentAtURL:(NSURL*)url {

    if (controller.documentPickerMode == UIDocumentPickerModeImport) {
        [controller dismissViewControllerAnimated:YES completion:nil];
        com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG [url path]));
    }
}

- (void)documentPickerWasCancelled:(UIDocumentPickerViewController *)controller {
    [controller dismissViewControllerAnimated:YES completion:nil];
    com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG JAVA_NULL);
}

-(NSString *) preferredUTIForExtension:(NSString *)ext
{
    CFStringRef str = (__bridge CFStringRef)ext;
    CFStringRef theUTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, str, NULL);
    if (theUTI != NULL) {
        NSString *uti = (__bridge_transfer NSString *)theUTI;
        return uti;
    } else {
        return nil;
    }
}


-(NSArray *) preferredUTIsForExtensions:(NSString *)exts {
    NSArray *arr = [exts componentsSeparatedByString:@","];
    NSMutableArray *out = [NSMutableArray arrayWithCapacity:[arr count]];
    for (NSString *ext in arr) {
        NSString *uti = [self preferredUTIForExtension:ext];
        if (uti != nil) {
            [out addObject:uti];
        } else {
            NSLog(@"Warning: Could not find UTI for extension %@", ext);
        }
    }
    return out;
}


-(void) openGallery:(int)type {
    id me = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        POOL_BEGIN();
        UIImagePickerControllerSourceType sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        if(![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
            if(![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeSavedPhotosAlbum]) {
                return;
            }
            sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
        }
        popoverController = nil;

#ifndef CN1_USE_ARC
        UIImagePickerController* pickerController = [[[UIImagePickerController alloc] init] autorelease];
#else
        UIImagePickerController* pickerController = [[UIImagePickerController alloc] init];
#endif

        pickerController.delegate = me;
        pickerController.sourceType = sourceType;
        if (type==0){
            pickerController.mediaTypes = [[NSArray alloc] initWithObjects:(NSString*)kUTTypeImage, nil];
        } else if (type==1){
            pickerController.mediaTypes = [[NSArray alloc] initWithObjects:(NSString*)kUTTypeMovie, nil];
        } else if (type==2){
            pickerController.mediaTypes = [[NSArray alloc] initWithObjects:(NSString*)kUTTypeMovie, (NSString*)kUTTypeImage,  nil];
        }

        if(popoverSupported()) {
#ifndef CN1_USE_ARC
            popoverController = [[[NSClassFromString(@"UIPopoverController") alloc]
                                  initWithContentViewController:pickerController] autorelease];
#else
            popoverController = [[NSClassFromString(@"UIPopoverController") alloc]
                                 initWithContentViewController:pickerController];
#endif
            popoverController.delegate = me;
            [popoverController presentPopoverFromRect:CGRectMake(0,32,320,480)
                                               inView:[[CodenameOne_GLViewController instance] view]
                             permittedArrowDirections:UIPopoverArrowDirectionAny
                                             animated:YES];
#ifndef CN1_USE_ARC
            [popoverController retain];
#endif
        } else {
            [[CodenameOne_GLViewController instance] presentModalViewController:pickerController animated:YES];
        }
        POOL_END();
    });
}

- (void)popoverControllerDidDismissPopover:(id)popoverController {
    com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG nil);
}

-(void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    //[self dismissModalViewControllerAnimated:YES];
    //com_codename1_impl_ios_IOSImplementation_capturePictureResult___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG nil);
    com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG nil);
    [picker dismissModalViewControllerAnimated:YES];
}

- (void)imagePickerController:(UIImagePickerController*)picker didFinishPickingMediaWithInfo:(NSDictionary*)info {
    POOL_BEGIN();
    NSString* mediaType = [info objectForKey:UIImagePickerControllerMediaType];
    if ([mediaType isEqualToString:@"public.image"]) {
        // get the image
        UIImage* originalImage = [info objectForKey:UIImagePickerControllerOriginalImage];
#ifndef CN1_USE_ARC
        [originalImage retain];
#endif
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            POOL_BEGIN();
            UIImage* image = originalImage;
            BOOL releaseImage = YES;
#ifndef LOW_MEM_CAMERA
            if (image.imageOrientation != UIImageOrientationUp) {
                UIGraphicsBeginImageContextWithOptions(image.size, NO, image.scale);
                [image drawInRect:(CGRect){0, 0, image.size}];
                releaseImage = NO;
#ifndef CN1_USE_ARC
                [originalImage release];
#endif
                image = UIGraphicsGetImageFromCurrentImageContext();
                UIGraphicsEndImageContext();
            }
#endif

            NSData* data = UIImageJPEGRepresentation(image, 90 / 100.0f);

            NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
            NSString *documentsDirectory = [paths objectAtIndex:0];
            NSString *path = [documentsDirectory stringByAppendingPathComponent:@"temp_image.jpg"];
            [data writeToFile:path atomically:YES];
            if(releaseImage) {
#ifndef CN1_USE_ARC
                [originalImage release];
#endif
            }
            path = [NSString stringWithFormat: @"file://%@", path];
            com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG path));
            //com_codename1_impl_ios_IOSImplementation_capturePictureResult___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG path));
            POOL_END();
        });

    } else {
        // was movie type
        NSURL *mediaURL = [info objectForKey:UIImagePickerControllerMediaURL];
        NSString *sourcePath = [mediaURL path];

        // Extract the original file name and extension
        NSString *fileName = [sourcePath lastPathComponent];
        NSString *extension = [sourcePath pathExtension];

        // Create a destination path in the documents directory using the original file name
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSString *destinationPath = [documentsDirectory stringByAppendingPathComponent:fileName];

        NSError *error = nil;
        NSFileManager *fileManager = [NSFileManager defaultManager];

        // Remove existing file at destinationPath if any
        if ([fileManager fileExistsAtPath:destinationPath]) {
            [fileManager removeItemAtPath:destinationPath error:nil];
        }

        // Copy the video file to the documents directory
        BOOL success = [fileManager copyItemAtPath:sourcePath toPath:destinationPath error:&error];
        if (!success) {
            NSLog(@"Error copying movie file: %@", [error localizedDescription]);
            com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG JAVA_NULL);
        } else {
            NSString *path = [NSString stringWithFormat: @"file://%@", destinationPath];
            com_codename1_ext_filechooser_FileChooser_fireNativeOnComplete___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG path));
        }
    }


    if(popoverSupported() && popoverController != nil) {
        [popoverController dismissPopoverAnimated:YES];
        popoverController.delegate = nil;
        popoverController = nil;
    } else {
#ifdef LOW_MEM_CAMERA
        [picker dismissModalViewControllerAnimated:NO];
#else
        [picker dismissModalViewControllerAnimated:YES];
#endif
    }

    //picker.delegate = nil;
    //picker = nil;
    POOL_END();
}


-(void)openDocumentPickerWithAccept:(NSString *)accept {
    id me = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        POOL_BEGIN();

        UIDocumentPickerViewController *documentPicker =
        [[UIDocumentPickerViewController alloc] initWithDocumentTypes:[self preferredUTIsForExtensions:accept]
                                                               inMode:UIDocumentPickerModeImport];
        documentPicker.delegate = me;
        if (@available(iOS 11, *)) {
            [documentPicker setAllowsMultipleSelection:multiSelect];
        }

        documentPicker.modalPresentationStyle = UIModalPresentationFormSheet;
        if (isIPad()) {
            documentPicker.popoverPresentationController.sourceView = [[CodenameOne_GLViewController instance] view];
            documentPicker.popoverPresentationController.sourceRect = CGRectMake(CN1lastTouchX, CN1lastTouchY, 1, 1);
        }

        [[CodenameOne_GLViewController instance] presentViewController:documentPicker animated:YES completion:nil];

        POOL_END();
    });
}


@end
