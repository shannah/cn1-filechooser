#import <Foundation/Foundation.h>

@interface com_codename1_ext_filechooser_FileChooserNativeImpl : NSObject<UIDocumentPickerDelegate> {
}

-(BOOL)isSupported;
-(BOOL)showNativeChooser: (NSString*)accept param1:(BOOL)multi;
- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentsAtURLs:(NSArray<NSURL *>*)urls;
- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentAtURL:(NSURL *)url;
- (void)documentPickerWasCancelled:(UIDocumentPickerViewController *)controller;
@end
